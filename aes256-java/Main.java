import java.io.Console;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Scanner;
import javax.crypto.AEADBadTagException;

/*
 * OOP CONCEPT MAP (source: oop-notes/java-oop.txt)
 * - Encapsulation: CryptoOperation#passphrase with controlled access via
 *   getPassphrase(), setPassphrase(char[]), and consumePassphrase().
 * - Inheritance: TextCipher extends CryptoOperation; FileCipher extends CryptoOperation.
 * - Method Overloading: TextCipher#encrypt(String) / TextCipher#encrypt(char[]) and
 *   FileCipher#encrypt(Path) / FileCipher#encrypt(File) / FileCipher#encrypt(Path, Path).
 * - Method Overriding: TextCipher#describe() and FileCipher#describe() override
 *   CryptoOperation#describe().
 */
/**
 * Interactive CLI entrypoint for aes256-java.
 *
 * <p>BOLT-3.2 wires menu actions to {@link TextCipher} and {@link FileCipher}
 * with friendly error mapping and passphrase prompting behavior.
 */
public final class Main {

    private static final String HELP_TEXT =
            String.join(
                    System.lineSeparator(),
                    "Usage: java Main [--help] [--selftest | --selftest-large]",
                    "",
                    "Options:",
                    "  --help           Show this help text and exit.",
                    "  --selftest       Run the default in-program test suite and exit.",
                    "  --selftest-large Run selftest with 128 MiB stream round-trip and exit.",
                    "",
                    "No option starts the interactive menu.");

    private static final String EDUCATIONAL_WARNING =
            "EDUCATIONAL WARNING: This project is for learning and coursework only,"
                    + " not production cryptography deployment.";
    private static final String PASS_FALLBACK_WARNING =
            "warning: console is not attached; passphrase input will be visible.";

    private Main() {}

    /**
     * Runs the command-line entrypoint.
     *
     * @param args command-line args
     */
    public static void main(String[] args) {
        if (hasArg(args, "--help")) {
            printHelp();
            return;
        }

        if (hasArg(args, "--selftest") || hasArg(args, "--selftest-large")) {
            boolean includeLarge = hasArg(args, "--selftest-large");
            int code = SelfTest.run(System.out, includeLarge);
            System.exit(code);
            return;
        }

        if (args != null && args.length > 0) {
            System.out.println("unknown option(s). Run with --help for usage.");
            printHelp();
            return;
        }

        runMenu();
    }

    private static void runMenu() {
        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                printMenu();

                if (!scanner.hasNextLine()) {
                    System.out.println();
                    System.out.println("input stream closed; exiting");
                    return;
                }

                String choice = scanner.nextLine().trim();
                try {
                    switch (choice) {
                        case "1":
                            encryptText(scanner);
                            break;
                        case "2":
                            decryptText(scanner);
                            break;
                        case "3":
                            encryptFile(scanner);
                            break;
                        case "4":
                            decryptFile(scanner);
                            break;
                        case "5":
                            int code = SelfTest.runDefault(System.out);
                            System.out.println("selftest exit code=" + code);
                            break;
                        case "6":
                            printAbout();
                            break;
                        case "0":
                            System.out.println("bye");
                            return;
                        default:
                            System.out.println("invalid menu choice");
                            break;
                    }
                } catch (InputClosedException ex) {
                    System.out.println();
                    System.out.println("input stream closed; exiting");
                    return;
                }
            }
        }
    }

    private static void printMenu() {
        System.out.println();
        System.out.println("=== aes256-java ===");
        System.out.println("1) Encrypt text");
        System.out.println("2) Decrypt text");
        System.out.println("3) Encrypt file");
        System.out.println("4) Decrypt file");
        System.out.println("5) SelfTest");
        System.out.println("6) About");
        System.out.println("0) Quit");
        System.out.print("Select option: ");
    }

    private static void printHelp() {
        System.out.println(HELP_TEXT);
        System.out.println(EDUCATIONAL_WARNING);
    }

    private static void printAbout() {
        System.out.println("aes256-java CLI");
        System.out.println("Unit-03 menu actions are wired to TextCipher and FileCipher.");
        System.out.println("Friendly error mapping is enabled for common user failures.");
        System.out.println(EDUCATIONAL_WARNING);
    }

    private static void encryptText(Scanner scanner) {
        String plaintext = promptLine(scanner, "Plaintext: ");
        char[] passphrase = null;
        TextCipher cipher = null;
        try {
            passphrase = readPassphrase(scanner, "Passphrase: ");
            cipher = new TextCipher(new AesGcmEngine(), passphrase);
            String ciphertext = cipher.encrypt(plaintext);
            System.out.println("ciphertext (Base64):");
            System.out.println(ciphertext);
        } catch (IllegalArgumentException ex) {
            System.out.println("encrypt text failed: " + ex.getMessage());
        } catch (GeneralSecurityException ex) {
            System.out.println("encrypt text failed due to a cryptographic error.");
        } finally {
            if (cipher != null) {
                cipher.clearStoredPassphrase();
            }
            wipe(passphrase);
        }
    }

    private static void decryptText(Scanner scanner) {
        String base64Envelope = promptLine(scanner, "Ciphertext (Base64): ");
        char[] passphrase = null;
        TextCipher cipher = null;
        try {
            passphrase = readPassphrase(scanner, "Passphrase: ");
            cipher = new TextCipher(new AesGcmEngine(), passphrase);
            String plaintext = cipher.decrypt(base64Envelope);
            System.out.println("plaintext:");
            System.out.println(plaintext);
        } catch (AEADBadTagException ex) {
            System.out.println(
                    "decrypt text failed: wrong passphrase or corrupted ciphertext.");
        } catch (IllegalArgumentException ex) {
            System.out.println("decrypt text failed: " + ex.getMessage());
        } catch (GeneralSecurityException ex) {
            System.out.println("decrypt text failed due to a cryptographic error.");
        } finally {
            if (cipher != null) {
                cipher.clearStoredPassphrase();
            }
            wipe(passphrase);
        }
    }

    private static void encryptFile(Scanner scanner) {
        String rawInputPath = promptRequiredLine(scanner, "Input file path: ", "input file path");
        char[] passphrase = null;
        FileCipher cipher = null;
        try {
            Path inputPath = parsePath(rawInputPath);
            passphrase = readPassphrase(scanner, "Passphrase: ");
            cipher = new FileCipher(new AesGcmEngine(), passphrase);
            Path outputPath = cipher.encrypt(inputPath);
            System.out.println("encrypt file: success");
            System.out.println("output: " + outputPath);
        } catch (InvalidPathException ex) {
            System.out.println("encrypt file failed: invalid path");
        } catch (FileAlreadyExistsException ex) {
            String message = ex.getMessage() == null ? "refusing to overwrite output file" : ex.getMessage();
            System.out.println(message);
        } catch (FileNotFoundException ex) {
            System.out.println("encrypt file failed: " + ex.getMessage());
        } catch (IllegalArgumentException ex) {
            System.out.println("encrypt file failed: " + ex.getMessage());
        } catch (IOException ex) {
            System.out.println("encrypt file failed: " + ex.getMessage());
        } catch (GeneralSecurityException ex) {
            System.out.println("encrypt file failed due to a cryptographic error.");
        } finally {
            if (cipher != null) {
                cipher.clearStoredPassphrase();
            }
            wipe(passphrase);
        }
    }

    private static void decryptFile(Scanner scanner) {
        String rawInputPath = promptRequiredLine(scanner, "Input file path: ", "input file path");
        char[] passphrase = null;
        FileCipher cipher = null;
        try {
            Path inputPath = parsePath(rawInputPath);
            passphrase = readPassphrase(scanner, "Passphrase: ");
            cipher = new FileCipher(new AesGcmEngine(), passphrase);
            Path outputPath = cipher.decrypt(inputPath);
            System.out.println("decrypt file: success");
            System.out.println("output: " + outputPath);
        } catch (InvalidPathException ex) {
            System.out.println("decrypt file failed: invalid path");
        } catch (AEADBadTagException ex) {
            System.out.println(
                    "decrypt file failed: wrong passphrase or corrupted encrypted file.");
        } catch (FileAlreadyExistsException ex) {
            String message = ex.getMessage() == null ? "refusing to overwrite output file" : ex.getMessage();
            System.out.println(message);
        } catch (FileNotFoundException ex) {
            System.out.println("decrypt file failed: " + ex.getMessage());
        } catch (IllegalArgumentException ex) {
            System.out.println("decrypt file failed: " + ex.getMessage());
        } catch (IOException ex) {
            System.out.println("decrypt file failed: " + ex.getMessage());
        } catch (GeneralSecurityException ex) {
            System.out.println("decrypt file failed due to a cryptographic error.");
        } finally {
            if (cipher != null) {
                cipher.clearStoredPassphrase();
            }
            wipe(passphrase);
        }
    }

    private static String promptLine(Scanner scanner, String prompt) {
        System.out.print(prompt);
        if (!scanner.hasNextLine()) {
            throw new InputClosedException();
        }
        return scanner.nextLine();
    }

    private static String promptRequiredLine(Scanner scanner, String prompt, String fieldLabel) {
        String value = promptLine(scanner, prompt).trim();
        if (value.isEmpty()) {
            throw new IllegalArgumentException(fieldLabel + " must not be empty");
        }
        return value;
    }

    private static char[] readPassphrase(Scanner scanner, String prompt) {
        Console console = System.console();
        if (console != null) {
            char[] passphrase = console.readPassword("%s", prompt);
            if (passphrase == null) {
                throw new InputClosedException();
            }
            if (passphrase.length == 0) {
                throw new IllegalArgumentException("passphrase must not be empty");
            }
            return passphrase;
        }

        System.out.println(PASS_FALLBACK_WARNING);
        String line = promptLine(scanner, prompt);
        if (line.isEmpty()) {
            throw new IllegalArgumentException("passphrase must not be empty");
        }
        return line.toCharArray();
    }

    private static Path parsePath(String rawPath) {
        return Paths.get(rawPath).normalize();
    }

    private static void wipe(char[] value) {
        if (value != null) {
            Arrays.fill(value, '\0');
        }
    }

    private static boolean hasArg(String[] args, String flag) {
        if (args == null) {
            return false;
        }
        for (String arg : args) {
            if (flag.equals(arg)) {
                return true;
            }
        }
        return false;
    }

    private static final class InputClosedException extends RuntimeException {}
}
