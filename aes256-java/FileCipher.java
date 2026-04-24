import java.io.File;
import java.nio.file.Path;

/**
 * File-oriented crypto facade.
 *
 * <p>Method bodies remain intentionally minimal in BOLT-2.1 so this file can lock
 * signatures for BOLT-2.2 implementation.
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
     */
    public Path encrypt(Path input) {
        throw notYetImplemented("encrypt(Path)");
    }

    /**
     * Encrypts a file object and writes to default output (`<input>.enc`).
     *
     * @param input source file
     * @return output path
     */
    public Path encrypt(File input) {
        throw notYetImplemented("encrypt(File)");
    }

    /**
     * Encrypts an input path to an explicit output path.
     *
     * @param input source path
     * @param output destination path
     * @return output path
     */
    public Path encrypt(Path input, Path output) {
        throw notYetImplemented("encrypt(Path,Path)");
    }

    /**
     * Decrypts an encrypted path to the resolved default output.
     *
     * @param input encrypted input path
     * @return output path
     */
    public Path decrypt(Path input) {
        throw notYetImplemented("decrypt(Path)");
    }

    @Override
    public String describe() {
        return "FileCipher";
    }

    private static UnsupportedOperationException notYetImplemented(String methodName) {
        return new UnsupportedOperationException(methodName + " is scheduled for BOLT-2.2");
    }
}
