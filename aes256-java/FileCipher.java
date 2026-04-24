import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.GeneralSecurityException;
import java.util.Arrays;

/**
 * File-oriented crypto facade.
 *
 * <p>This class maps file operations to Unit-01 stream encrypt/decrypt APIs while applying
 * filename policies from the requirements contract.
 */
public final class FileCipher extends CryptoOperation {

    /**
     * Creates a file cipher wrapper.
     *
     * @param engine cryptographic engine dependency
     * @param passphrase mutable passphrase characters
     */
    public FileCipher(AesGcmEngine engine, char[] passphrase) {
        super(engine, passphrase);
    }

    /**
     * Encrypts a file path and writes to default output (`<input>.enc`).
     *
     * @param input source path
     * @return output path
     * @throws IOException if file I/O fails
     * @throws GeneralSecurityException if encryption fails
     */
    public Path encrypt(Path input) throws IOException, GeneralSecurityException {
        Path normalizedInput = requireReadableInput(input);
        Path output = resolveDefaultEncryptedOutput(normalizedInput);
        return encrypt(normalizedInput, output);
    }

    /**
     * Encrypts a file object and writes to default output (`<input>.enc`).
     *
     * @param input source file
     * @return output path
     * @throws IOException if file I/O fails
     * @throws GeneralSecurityException if encryption fails
     */
    public Path encrypt(File input) throws IOException, GeneralSecurityException {
        if (input == null) {
            throw new IllegalArgumentException("input must not be null");
        }
        return encrypt(input.toPath());
    }

    /**
     * Encrypts an input path to an explicit output path.
     *
     * @param input source path
     * @param output destination path
     * @return output path
     * @throws IOException if file I/O fails
     * @throws GeneralSecurityException if encryption fails
     */
    public Path encrypt(Path input, Path output) throws IOException, GeneralSecurityException {
        Path normalizedInput = requireReadableInput(input);
        Path normalizedOutput = requireWritableOutput(output);

        char[] operationPassphrase = consumePassphrase();
        try (InputStream in = Files.newInputStream(normalizedInput);
                OutputStream out =
                        Files.newOutputStream(
                                normalizedOutput,
                                StandardOpenOption.CREATE_NEW,
                                StandardOpenOption.WRITE)) {
            getEngine().encrypt(in, out, operationPassphrase);
            return normalizedOutput;
        } finally {
            Arrays.fill(operationPassphrase, '\0');
        }
    }

    /**
     * Decrypts an encrypted path to the resolved default output.
     *
     * @param input encrypted input path
     * @return output path
     * @throws IOException if file I/O fails
     * @throws GeneralSecurityException if decryption fails or authentication fails
     */
    public Path decrypt(Path input) throws IOException, GeneralSecurityException {
        Path normalizedInput = requireReadableInput(input);
        Path output = resolveDefaultDecryptedOutput(normalizedInput);
        return decrypt(normalizedInput, output);
    }

    @Override
    public String describe() {
        return "FileCipher";
    }

    private Path decrypt(Path input, Path output) throws IOException, GeneralSecurityException {
        Path normalizedOutput = requireWritableOutput(output);

        char[] operationPassphrase = consumePassphrase();
        try (InputStream in = Files.newInputStream(input);
                OutputStream out =
                        Files.newOutputStream(
                                normalizedOutput,
                                StandardOpenOption.CREATE_NEW,
                                StandardOpenOption.WRITE)) {
            getEngine().decrypt(in, out, operationPassphrase);
            return normalizedOutput;
        } finally {
            Arrays.fill(operationPassphrase, '\0');
        }
    }

    private static Path requireReadableInput(Path input) throws IOException {
        if (input == null) {
            throw new IllegalArgumentException("input must not be null");
        }
        if (!Files.exists(input)) {
            throw new FileNotFoundException("input file not found: " + input);
        }
        if (!Files.isRegularFile(input)) {
            throw new IOException("input is not a regular file: " + input);
        }
        return input;
    }

    private static Path requireWritableOutput(Path output) throws IOException {
        if (output == null) {
            throw new IllegalArgumentException("output must not be null");
        }
        if (Files.exists(output)) {
            throw new FileAlreadyExistsException("refusing to overwrite: " + output);
        }
        Path parent = output.toAbsolutePath().getParent();
        if (parent != null && !Files.exists(parent)) {
            throw new FileNotFoundException("parent directory does not exist: " + parent);
        }
        return output;
    }

    private static Path resolveDefaultEncryptedOutput(Path input) {
        Path fileName = input.getFileName();
        if (fileName == null) {
            throw new IllegalArgumentException("input path must include a filename: " + input);
        }
        return input.resolveSibling(fileName + ".enc");
    }

    private static Path resolveDefaultDecryptedOutput(Path input) {
        Path fileName = input.getFileName();
        if (fileName == null) {
            throw new IllegalArgumentException("input path must include a filename: " + input);
        }

        String name = fileName.toString();
        String outputName;
        if (name.endsWith(".enc") && name.length() > ".enc".length()) {
            outputName = name.substring(0, name.length() - ".enc".length());
        } else {
            outputName = name + ".dec";
        }
        return input.resolveSibling(outputName);
    }
}
