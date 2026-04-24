import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Base64;

/**
 * Text-oriented crypto facade.
 *
 * <p>This class maps UTF-8 text payloads to the Unit-01 byte-array envelope via
 * {@link AesGcmEngine}.
 */
public final class TextCipher extends CryptoOperation {

    /**
     * Creates a text cipher wrapper.
     *
     * @param engine cryptographic engine dependency
     * @param passphrase mutable passphrase characters
     */
    public TextCipher(AesGcmEngine engine, char[] passphrase) {
        super(engine, passphrase);
    }

    /**
     * Encrypts a text payload.
     *
     * @param plaintext text payload
     * @return Base64 envelope text
     * @throws GeneralSecurityException if encryption fails or provider operations fail
     */
    public String encrypt(String plaintext) throws GeneralSecurityException {
        if (plaintext == null) {
            throw new IllegalArgumentException("plaintext must not be null");
        }

        byte[] plaintextBytes = plaintext.getBytes(StandardCharsets.UTF_8);
        return encryptBytes(plaintextBytes);
    }

    /**
     * Encrypts a character payload.
     *
     * @param plaintext character payload
     * @return Base64 envelope text
     * @throws GeneralSecurityException if encryption fails or provider operations fail
     */
    public String encrypt(char[] plaintext) throws GeneralSecurityException {
        if (plaintext == null) {
            throw new IllegalArgumentException("plaintext must not be null");
        }

        char[] plaintextCopy = Arrays.copyOf(plaintext, plaintext.length);
        byte[] plaintextBytes = null;
        try {
            plaintextBytes = toUtf8Bytes(plaintextCopy);
            return encryptBytes(plaintextBytes);
        } finally {
            Arrays.fill(plaintextCopy, '\0');
            if (plaintextBytes != null) {
                Arrays.fill(plaintextBytes, (byte) 0);
            }
        }
    }

    /**
     * Decrypts a Base64 envelope back to text.
     *
     * @param base64Envelope encoded envelope string
     * @return decrypted text
     * @throws GeneralSecurityException if authentication or decryption fails
     */
    public String decrypt(String base64Envelope) throws GeneralSecurityException {
        if (base64Envelope == null) {
            throw new IllegalArgumentException("base64Envelope must not be null");
        }

        byte[] envelopeBytes = decodeEnvelope(base64Envelope);
        char[] operationPassphrase = consumePassphrase();
        byte[] plaintextBytes = null;
        try {
            plaintextBytes = getEngine().decrypt(envelopeBytes, operationPassphrase);
            return new String(plaintextBytes, StandardCharsets.UTF_8);
        } finally {
            Arrays.fill(operationPassphrase, '\0');
            Arrays.fill(envelopeBytes, (byte) 0);
            if (plaintextBytes != null) {
                Arrays.fill(plaintextBytes, (byte) 0);
            }
        }
    }

    @Override
    public String describe() {
        return "TextCipher";
    }

    private String encryptBytes(byte[] plaintextBytes) throws GeneralSecurityException {
        char[] operationPassphrase = consumePassphrase();
        byte[] envelopeBytes = null;
        try {
            envelopeBytes = getEngine().encrypt(plaintextBytes, operationPassphrase);
            return Base64.getEncoder().encodeToString(envelopeBytes);
        } finally {
            Arrays.fill(operationPassphrase, '\0');
            Arrays.fill(plaintextBytes, (byte) 0);
            if (envelopeBytes != null) {
                Arrays.fill(envelopeBytes, (byte) 0);
            }
        }
    }

    private static byte[] decodeEnvelope(String base64Envelope) {
        try {
            return Base64.getDecoder().decode(base64Envelope);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("ciphertext must be valid Base64", ex);
        }
    }

    private static byte[] toUtf8Bytes(char[] plaintext) {
        return new String(plaintext).getBytes(StandardCharsets.UTF_8);
    }
}
