/**
 * Text-oriented crypto facade.
 *
 * <p>Method bodies remain intentionally minimal in BOLT-2.1 so this file can lock
 * signatures for BOLT-2.2 implementation.
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
     */
    public String encrypt(String plaintext) {
        throw notYetImplemented("encrypt(String)");
    }

    /**
     * Encrypts a character payload.
     *
     * @param plaintext character payload
     * @return Base64 envelope text
     */
    public String encrypt(char[] plaintext) {
        throw notYetImplemented("encrypt(char[])");
    }

    /**
     * Decrypts a Base64 envelope back to text.
     *
     * @param base64Envelope encoded envelope string
     * @return decrypted text
     */
    public String decrypt(String base64Envelope) {
        throw notYetImplemented("decrypt(String)");
    }

    @Override
    public String describe() {
        return "TextCipher";
    }

    private static UnsupportedOperationException notYetImplemented(String methodName) {
        return new UnsupportedOperationException(methodName + " is scheduled for BOLT-2.2");
    }
}
