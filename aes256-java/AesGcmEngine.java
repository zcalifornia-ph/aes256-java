import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Arrays;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Core cryptographic primitives for AES-256/GCM operations.
 *
 * <p>BOLT-1.1 introduces deterministic PBKDF2 key derivation using a supplied salt.
 * BOLT-1.2 introduces byte-array encrypt/decrypt operations using AES/GCM envelopes.
 */
public final class AesGcmEngine {

    static final int PBKDF2_ITERATIONS = 210_000;
    static final int SALT_LENGTH_BYTES = 16;
    static final int DERIVED_KEY_BITS = 256;
    static final int GCM_IV_LENGTH_BYTES = 12;
    static final int GCM_TAG_LENGTH_BITS = 128;
    static final int GCM_TAG_LENGTH_BYTES = GCM_TAG_LENGTH_BITS / 8;

    private static final String KDF_ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final String KEY_ALGORITHM = "AES";
    private static final String CIPHER_TRANSFORMATION = "AES/GCM/NoPadding";
    private static final int MIN_ENVELOPE_LENGTH_BYTES =
            SALT_LENGTH_BYTES + GCM_IV_LENGTH_BYTES + GCM_TAG_LENGTH_BYTES;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    /**
     * Derives a 256-bit AES key using PBKDF2-HMAC-SHA256.
     *
     * <p>Security contract:
     *
     * <ul>
     *   <li>Salt must be exactly 16 bytes.</li>
     *   <li>Iterations are fixed at {@value #PBKDF2_ITERATIONS}.</li>
     *   <li>The provided passphrase is zeroed before returning.</li>
     * </ul>
     *
     * @param passphrase user passphrase as mutable characters
     * @param salt per-operation random salt (16 bytes)
     * @return derived AES key
     * @throws GeneralSecurityException if the key cannot be derived by the JDK provider
     * @throws IllegalArgumentException if inputs are null/invalid
     */
    SecretKey deriveKey(char[] passphrase, byte[] salt) throws GeneralSecurityException {
        if (passphrase == null || passphrase.length == 0) {
            throw new IllegalArgumentException("passphrase must not be null or empty");
        }
        if (salt == null) {
            Arrays.fill(passphrase, '\0');
            throw new IllegalArgumentException("salt must not be null");
        }
        if (salt.length != SALT_LENGTH_BYTES) {
            Arrays.fill(passphrase, '\0');
            throw new IllegalArgumentException("salt must be exactly 16 bytes");
        }

        PBEKeySpec keySpec = new PBEKeySpec(passphrase, salt, PBKDF2_ITERATIONS, DERIVED_KEY_BITS);
        byte[] keyBytes = null;
        try {
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(KDF_ALGORITHM);
            keyBytes = keyFactory.generateSecret(keySpec).getEncoded();
            return new SecretKeySpec(keyBytes, KEY_ALGORITHM);
        } finally {
            keySpec.clearPassword();
            Arrays.fill(passphrase, '\0');
            if (keyBytes != null) {
                Arrays.fill(keyBytes, (byte) 0);
            }
        }
    }

    /**
     * Encrypts plaintext with AES-256/GCM and returns a binary envelope.
     *
     * <p>Envelope layout: {@code salt(16) || iv(12) || ciphertext || tag(16)}.
     *
     * <p>Example usage:
     *
     * <pre>{@code
     * AesGcmEngine engine = new AesGcmEngine();
     * byte[] envelope = engine.encrypt("hello".getBytes(), "secret".toCharArray());
     * }</pre>
     *
     * @param plaintext bytes to encrypt
     * @param passphrase passphrase used for key derivation
     * @return encrypted envelope with prepended salt and IV
     * @throws GeneralSecurityException if encryption fails in the configured JDK provider
     * @throws IllegalArgumentException if plaintext or passphrase input is invalid
     */
    public byte[] encrypt(byte[] plaintext, char[] passphrase) throws GeneralSecurityException {
        if (plaintext == null) {
            wipePassphrase(passphrase);
            throw new IllegalArgumentException("plaintext must not be null");
        }

        byte[] salt = randomBytes(SALT_LENGTH_BYTES);
        byte[] iv = randomBytes(GCM_IV_LENGTH_BYTES);
        byte[] ciphertextAndTag = null;
        try {
            SecretKey key = deriveKey(passphrase, salt);
            Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv));
            ciphertextAndTag = cipher.doFinal(plaintext);

            ByteBuffer envelope =
                    ByteBuffer.allocate(SALT_LENGTH_BYTES + GCM_IV_LENGTH_BYTES + ciphertextAndTag.length);
            envelope.put(salt);
            envelope.put(iv);
            envelope.put(ciphertextAndTag);
            return envelope.array();
        } finally {
            Arrays.fill(salt, (byte) 0);
            Arrays.fill(iv, (byte) 0);
            if (ciphertextAndTag != null) {
                Arrays.fill(ciphertextAndTag, (byte) 0);
            }
        }
    }

    /**
     * Decrypts a binary envelope produced by {@link #encrypt(byte[], char[])}.
     *
     * <p>Envelope layout: {@code salt(16) || iv(12) || ciphertext || tag(16)}.
     *
     * <p>Example usage:
     *
     * <pre>{@code
     * AesGcmEngine engine = new AesGcmEngine();
     * byte[] plaintext = engine.decrypt(envelope, "secret".toCharArray());
     * }</pre>
     *
     * @param envelope encrypted envelope bytes
     * @param passphrase passphrase used for key derivation
     * @return decrypted plaintext bytes
     * @throws GeneralSecurityException if authentication fails or decryption cannot complete
     * @throws IllegalArgumentException if envelope or passphrase input is invalid
     */
    public byte[] decrypt(byte[] envelope, char[] passphrase) throws GeneralSecurityException {
        if (envelope == null) {
            wipePassphrase(passphrase);
            throw new IllegalArgumentException("envelope must not be null");
        }
        if (envelope.length < MIN_ENVELOPE_LENGTH_BYTES) {
            wipePassphrase(passphrase);
            throw new IllegalArgumentException("envelope is too short");
        }

        byte[] salt = Arrays.copyOfRange(envelope, 0, SALT_LENGTH_BYTES);
        byte[] iv =
                Arrays.copyOfRange(
                        envelope, SALT_LENGTH_BYTES, SALT_LENGTH_BYTES + GCM_IV_LENGTH_BYTES);
        byte[] ciphertextAndTag =
                Arrays.copyOfRange(envelope, SALT_LENGTH_BYTES + GCM_IV_LENGTH_BYTES, envelope.length);
        try {
            SecretKey key = deriveKey(passphrase, salt);
            Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv));
            return cipher.doFinal(ciphertextAndTag);
        } finally {
            Arrays.fill(salt, (byte) 0);
            Arrays.fill(iv, (byte) 0);
            Arrays.fill(ciphertextAndTag, (byte) 0);
        }
    }

    private static byte[] randomBytes(int length) {
        byte[] bytes = new byte[length];
        SECURE_RANDOM.nextBytes(bytes);
        return bytes;
    }

    private static void wipePassphrase(char[] passphrase) {
        if (passphrase != null) {
            Arrays.fill(passphrase, '\0');
        }
    }
}
