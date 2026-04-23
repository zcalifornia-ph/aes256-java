import java.security.GeneralSecurityException;
import java.util.Arrays;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Core cryptographic primitives for AES-256/GCM operations.
 *
 * <p>BOLT-1.1 introduces deterministic PBKDF2 key derivation using a supplied salt.
 * Encrypt/decrypt operations are added in later Bolts.
 */
public final class AesGcmEngine {

    static final int PBKDF2_ITERATIONS = 210_000;
    static final int SALT_LENGTH_BYTES = 16;
    static final int DERIVED_KEY_BITS = 256;

    private static final String KDF_ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final String KEY_ALGORITHM = "AES";

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
}
