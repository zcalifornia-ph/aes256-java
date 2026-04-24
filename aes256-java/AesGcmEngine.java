import java.nio.ByteBuffer;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
 */
public final class AesGcmEngine {

    static final int PBKDF2_ITERATIONS = 210_000;
    static final int SALT_LENGTH_BYTES = 16;
    static final int DERIVED_KEY_BITS = 256;
    static final int GCM_IV_LENGTH_BYTES = 12;
    static final int GCM_TAG_LENGTH_BITS = 128;
    static final int GCM_TAG_LENGTH_BYTES = GCM_TAG_LENGTH_BITS / 8;
    static final int STREAM_BUFFER_SIZE_BYTES = 64 * 1024;
    static final int STREAM_RECORD_MAX_PLAINTEXT_BYTES = STREAM_BUFFER_SIZE_BYTES;
    static final long MAX_STREAM_RECORDS = 1L << 32;

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

    /**
     * Encrypts stream content with AES-256/GCM and writes a binary envelope.
     *
     * <p>Envelope layout:
     * {@code salt(16) || streamIv(12) || record( length(4) || ciphertext || tag(16) )* }.
     *
     * @param in plaintext stream source
     * @param out encrypted envelope destination
     * @param passphrase passphrase used for key derivation
     * @throws IOException if stream I/O fails
     * @throws GeneralSecurityException if encryption cannot complete with the active provider
     * @throws IllegalArgumentException if stream references are invalid
     */
    public void encrypt(InputStream in, OutputStream out, char[] passphrase)
            throws IOException, GeneralSecurityException {
        if (in == null) {
            wipePassphrase(passphrase);
            throw new IllegalArgumentException("input stream must not be null");
        }
        if (out == null) {
            wipePassphrase(passphrase);
            throw new IllegalArgumentException("output stream must not be null");
        }

        byte[] salt = randomBytes(SALT_LENGTH_BYTES);
        byte[] streamIv = randomBytes(GCM_IV_LENGTH_BYTES);
        byte[] transferBuffer = new byte[STREAM_RECORD_MAX_PLAINTEXT_BYTES];
        byte[] recordIv = new byte[GCM_IV_LENGTH_BYTES];
        long recordIndex = 0;
        try {
            SecretKey key = deriveKey(passphrase, salt);
            Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);

            out.write(salt);
            out.write(streamIv);

            int read;
            while ((read = in.read(transferBuffer)) != -1) {
                if (read == 0) {
                    continue;
                }
                if (recordIndex >= MAX_STREAM_RECORDS) {
                    throw new IllegalArgumentException(
                            "input stream exceeds maximum supported record count");
                }

                fillRecordIv(streamIv, recordIndex, recordIv);
                cipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(GCM_TAG_LENGTH_BITS, recordIv));

                byte[] ciphertextAndTag = cipher.doFinal(transferBuffer, 0, read);
                try {
                    writeInt(out, read);
                    out.write(ciphertextAndTag);
                } finally {
                    Arrays.fill(ciphertextAndTag, (byte) 0);
                }

                recordIndex++;
            }

            out.flush();
        } finally {
            Arrays.fill(salt, (byte) 0);
            Arrays.fill(streamIv, (byte) 0);
            Arrays.fill(transferBuffer, (byte) 0);
            Arrays.fill(recordIv, (byte) 0);
        }
    }

    /**
     * Decrypts a stream envelope produced by {@link #encrypt(InputStream, OutputStream, char[])}.
     *
     * <p>Envelope layout:
     * {@code salt(16) || streamIv(12) || record( length(4) || ciphertext || tag(16) )* }.
     *
     * <p>Each record authenticates independently before plaintext bytes are released to {@code out}.
     *
     * @param in encrypted envelope source stream
     * @param out plaintext destination stream
     * @param passphrase passphrase used for key derivation
     * @throws IOException if stream I/O fails
     * @throws GeneralSecurityException if authentication fails or decryption cannot complete
     * @throws IllegalArgumentException if stream references/header bytes are invalid
     */
    public void decrypt(InputStream in, OutputStream out, char[] passphrase)
            throws IOException, GeneralSecurityException {
        if (in == null) {
            wipePassphrase(passphrase);
            throw new IllegalArgumentException("input stream must not be null");
        }
        if (out == null) {
            wipePassphrase(passphrase);
            throw new IllegalArgumentException("output stream must not be null");
        }

        byte[] salt = new byte[SALT_LENGTH_BYTES];
        byte[] streamIv = new byte[GCM_IV_LENGTH_BYTES];
        if (!readFully(in, salt) || !readFully(in, streamIv)) {
            wipePassphrase(passphrase);
            Arrays.fill(salt, (byte) 0);
            Arrays.fill(streamIv, (byte) 0);
            throw new IllegalArgumentException("input stream does not contain a full envelope header");
        }

        byte[] recordCiphertext = new byte[STREAM_RECORD_MAX_PLAINTEXT_BYTES + GCM_TAG_LENGTH_BYTES];
        byte[] recordIv = new byte[GCM_IV_LENGTH_BYTES];
        long recordIndex = 0;
        try {
            SecretKey key = deriveKey(passphrase, salt);
            Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);

            while (true) {
                int recordPlaintextLength = readRecordLength(in);
                if (recordPlaintextLength == -1) {
                    break;
                }
                if (recordPlaintextLength <= 0
                        || recordPlaintextLength > STREAM_RECORD_MAX_PLAINTEXT_BYTES) {
                    throw new IllegalArgumentException("input stream contains invalid record length");
                }
                if (recordIndex >= MAX_STREAM_RECORDS) {
                    throw new IllegalArgumentException(
                            "input stream exceeds maximum supported record count");
                }

                int recordCiphertextLength = recordPlaintextLength + GCM_TAG_LENGTH_BYTES;
                if (!readFully(in, recordCiphertext, 0, recordCiphertextLength)) {
                    throw new IllegalArgumentException("input stream ended mid-record");
                }

                fillRecordIv(streamIv, recordIndex, recordIv);
                cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(GCM_TAG_LENGTH_BITS, recordIv));
                byte[] plaintextChunk = cipher.doFinal(recordCiphertext, 0, recordCiphertextLength);
                try {
                    if (plaintextChunk.length != recordPlaintextLength) {
                        throw new GeneralSecurityException("decrypted record length mismatch");
                    }
                    out.write(plaintextChunk);
                } finally {
                    Arrays.fill(plaintextChunk, (byte) 0);
                }

                recordIndex++;
            }
            out.flush();
        } finally {
            Arrays.fill(salt, (byte) 0);
            Arrays.fill(streamIv, (byte) 0);
            Arrays.fill(recordCiphertext, (byte) 0);
            Arrays.fill(recordIv, (byte) 0);
        }
    }

    private static byte[] randomBytes(int length) {
        byte[] bytes = new byte[length];
        SECURE_RANDOM.nextBytes(bytes);
        return bytes;
    }

    private static boolean readFully(InputStream in, byte[] target) throws IOException {
        return readFully(in, target, 0, target.length);
    }

    private static boolean readFully(InputStream in, byte[] target, int offset, int length)
            throws IOException {
        int cursor = offset;
        int end = offset + length;
        while (cursor < end) {
            int read = in.read(target, cursor, end - cursor);
            if (read == -1) {
                return false;
            }
            cursor += read;
        }
        return true;
    }

    private static void writeInt(OutputStream out, int value) throws IOException {
        out.write((value >>> 24) & 0xff);
        out.write((value >>> 16) & 0xff);
        out.write((value >>> 8) & 0xff);
        out.write(value & 0xff);
    }

    private static int readRecordLength(InputStream in) throws IOException {
        int b0 = in.read();
        if (b0 == -1) {
            return -1;
        }
        int b1 = in.read();
        int b2 = in.read();
        int b3 = in.read();
        if ((b1 | b2 | b3) < 0) {
            throw new IllegalArgumentException("input stream ended while reading record length");
        }
        return (b0 << 24) | (b1 << 16) | (b2 << 8) | b3;
    }

    private static void fillRecordIv(byte[] streamIv, long recordIndex, byte[] targetIv) {
        if (recordIndex < 0 || recordIndex >= MAX_STREAM_RECORDS) {
            throw new IllegalArgumentException("record index is outside supported range");
        }
        if (targetIv.length != GCM_IV_LENGTH_BYTES) {
            throw new IllegalArgumentException("target IV must be 12 bytes");
        }
        System.arraycopy(streamIv, 0, targetIv, 0, GCM_IV_LENGTH_BYTES);

        long carry = recordIndex;
        for (int i = GCM_IV_LENGTH_BYTES - 1; i >= 0 && carry != 0; i--) {
            long sum = (targetIv[i] & 0xffL) + (carry & 0xffL);
            targetIv[i] = (byte) sum;
            carry = (carry >>> 8) + (sum >>> 8);
        }
        if (carry != 0) {
            throw new IllegalArgumentException("record IV overflow");
        }
    }

    private static void wipePassphrase(char[] passphrase) {
        if (passphrase != null) {
            Arrays.fill(passphrase, '\0');
        }
    }
}
