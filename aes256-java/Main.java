import java.util.Scanner;

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
 * <p>BOLT-3.1 provides menu scaffolding only: command dispatch, menu loop,
 * help text, SelfTest routing, and About information. Encryption/decryption
 * action wiring is intentionally deferred to BOLT-3.2.
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
                switch (choice) {
                    case "1":
                        printDeferred("Encrypt text");
                        break;
                    case "2":
                        printDeferred("Decrypt text");
                        break;
                    case "3":
                        printDeferred("Encrypt file");
                        break;
                    case "4":
                        printDeferred("Decrypt file");
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
        System.out.println("aes256-java CLI (Unit-03 scaffolding)");
        System.out.println("This menu shell is wired in BOLT-3.1.");
        System.out.println("Encrypt/decrypt actions are implemented in BOLT-3.2.");
        System.out.println(EDUCATIONAL_WARNING);
    }

    private static void printDeferred(String actionLabel) {
        System.out.println(actionLabel + " is not wired yet. Continue with BOLT-3.2.");
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
}
