import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;
import javax.crypto.AEADBadTagException;

/**
 * In-program assertion runner for educational acceptance checks.
 *
 * <p>This class is JDK-only and default-package compatible. It reports explicit pass/fail
 * outcomes and returns process-friendly exit codes (0 all pass, non-zero otherwise).
 */
public final class SelfTest {

    private static final String DEFAULT_PASS = "correct horse battery staple";
    private static final String WRONG_PASS = "totally-wrong-passphrase";
    private static final int SMALL_TEXT_SIZE = 1024;
    private static final int MEDIUM_TEXT_SIZE = 1024 * 1024;
    private static final long STREAM_SMOKE_SIZE_BYTES = 8L * 1024L * 1024L;
    private static final long STREAM_LARGE_SIZE_BYTES = 128L * 1024L * 1024L;

    private SelfTest() {}

    /**
     * Runs the default selftest suite.
     *
     * @param out output stream for result lines
     * @return `0` when all tests pass, otherwise `1`
     */
    public static int runDefault(PrintStream out) {
        return run(out, false);
    }

    /**
     * Runs selftest scenarios with optional large streaming check.
     *
     * @param out output stream for result lines
     * @param includeLargeStreamingCheck true to run the 128 MiB stream round-trip case
     * @return `0` when all tests pass, otherwise `1`
     */
    public static int run(PrintStream out, boolean includeLargeStreamingCheck) {
        if (out == null) {
            throw new IllegalArgumentException("out must not be null");
        }

        Counters counters = new Counters();

        runCase(out, counters, "TEST-01", "round-trip empty/1B", SelfTest::testRoundTripSmallText);
        runCase(out, counters, "TEST-02", "round-trip 1KiB/1MiB", SelfTest::testRoundTripMediumText);
        runCase(out, counters, "TEST-04", "tamper rejected", SelfTest::testTamperDetection);
        runCase(out, counters, "TEST-05", "wrong passphrase rejected", SelfTest::testWrongPassphrase);
        runCase(out, counters, "TEST-06", "overwrite refusal", SelfTest::testOverwriteRefusal);

        if (includeLargeStreamingCheck) {
            runCase(
                    out,
                    counters,
                    "TEST-03",
                    "round-trip 128MiB",
                    () -> testStreamRoundTrip(STREAM_LARGE_SIZE_BYTES));
        } else {
            runCase(
                    out,
                    counters,
                    "TEST-03",
                    "round-trip stream smoke",
                    () -> testStreamRoundTrip(STREAM_SMOKE_SIZE_BYTES));
        }

        out.println(
                "SELFTEST SUMMARY passed=" + counters.passed + " failed=" + counters.failed);
        return counters.failed == 0 ? 0 : 1;
    }

    /**
     * Runs selftest from command-line flags.
     *
     * <p>Supported flags:
     *
     * <ul>
     *   <li>`--selftest`: run default selftest suite.</li>
     *   <li>`--selftest-large`: include 128 MiB stream round-trip check.</li>
     * </ul>
     *
     * @param args command-line args
     */
    public static void main(String[] args) {
        boolean includeLarge = false;
        for (String arg : args) {
            if ("--selftest-large".equals(arg)) {
                includeLarge = true;
            }
        }
        int code = run(System.out, includeLarge);
        System.exit(code);
    }

    private static void runCase(PrintStream out, Counters counters, String testId, String label, CheckedCase body) {
        try {
            body.run();
            counters.passed++;
            out.println("PASS " + testId + " " + label);
        } catch (Exception ex) {
            counters.failed++;
            out.println("FAIL " + testId + " " + label + " reason=" + sanitize(ex));
        }
    }

    private static void testRoundTripSmallText() throws GeneralSecurityException {
        String encryptedEmpty = new TextCipher(new AesGcmEngine(), DEFAULT_PASS.toCharArray()).encrypt("");
        String decryptedEmpty =
                new TextCipher(new AesGcmEngine(), DEFAULT_PASS.toCharArray()).decrypt(encryptedEmpty);
        assertEquals("", decryptedEmpty, "empty text mismatch");

        String encryptedOne = new TextCipher(new AesGcmEngine(), DEFAULT_PASS.toCharArray()).encrypt("A");
        String decryptedOne =
                new TextCipher(new AesGcmEngine(), DEFAULT_PASS.toCharArray()).decrypt(encryptedOne);
        assertEquals("A", decryptedOne, "1-byte text mismatch");
    }

    private static void testRoundTripMediumText() throws GeneralSecurityException {
        String payload1KiB = repeatedAscii(SMALL_TEXT_SIZE);
        String encrypted1KiB =
                new TextCipher(new AesGcmEngine(), DEFAULT_PASS.toCharArray()).encrypt(payload1KiB);
        String decrypted1KiB =
                new TextCipher(new AesGcmEngine(), DEFAULT_PASS.toCharArray()).decrypt(encrypted1KiB);
        assertEquals(payload1KiB, decrypted1KiB, "1KiB text mismatch");

        String payload1MiB = repeatedAscii(MEDIUM_TEXT_SIZE);
        String encrypted1MiB =
                new TextCipher(new AesGcmEngine(), DEFAULT_PASS.toCharArray()).encrypt(payload1MiB);
        String decrypted1MiB =
                new TextCipher(new AesGcmEngine(), DEFAULT_PASS.toCharArray()).decrypt(encrypted1MiB);
        assertEquals(payload1MiB, decrypted1MiB, "1MiB text mismatch");
    }

    private static void testTamperDetection() throws GeneralSecurityException {
        String encrypted =
                new TextCipher(new AesGcmEngine(), DEFAULT_PASS.toCharArray()).encrypt("tamper-check");
        byte[] envelope = Base64.getDecoder().decode(encrypted);
        envelope[envelope.length - 1] ^= 0x01;
        String tampered = Base64.getEncoder().encodeToString(envelope);
        Arrays.fill(envelope, (byte) 0);

        try {
            new TextCipher(new AesGcmEngine(), DEFAULT_PASS.toCharArray()).decrypt(tampered);
            throw new IllegalStateException("tamper should have been rejected");
        } catch (AEADBadTagException expected) {
            return;
        }
    }

    private static void testWrongPassphrase() throws GeneralSecurityException {
        String encrypted =
                new TextCipher(new AesGcmEngine(), DEFAULT_PASS.toCharArray())
                        .encrypt("wrong-passphrase-check");
        try {
            new TextCipher(new AesGcmEngine(), WRONG_PASS.toCharArray()).decrypt(encrypted);
            throw new IllegalStateException("wrong passphrase should have been rejected");
        } catch (AEADBadTagException expected) {
            return;
        }
    }

    private static void testOverwriteRefusal() throws IOException, GeneralSecurityException {
        Path tempDir = Files.createTempDirectory("selftest-overwrite");
        Path source = tempDir.resolve("source.txt");
        Path existing = tempDir.resolve("existing.enc");
        Files.writeString(source, "overwrite-check");
        Files.writeString(existing, "already-here");

        try {
            new FileCipher(new AesGcmEngine(), DEFAULT_PASS.toCharArray()).encrypt(source, existing);
            throw new IllegalStateException("overwrite should have been refused");
        } catch (FileAlreadyExistsException expected) {
            String message = expected.getMessage() == null ? "" : expected.getMessage();
            if (!message.startsWith("refusing to overwrite:")) {
                throw new IllegalStateException("unexpected overwrite message: " + message);
            }
        }
    }

    private static void testStreamRoundTrip(long sizeBytes) throws Exception {
        Path encryptDir = Files.createTempDirectory("selftest-stream");
        Path source = encryptDir.resolve("payload.bin");
        writePatternFile(source, sizeBytes);

        Path encrypted = new FileCipher(new AesGcmEngine(), DEFAULT_PASS.toCharArray()).encrypt(source);

        Path decryptDir = Files.createTempDirectory("selftest-stream-out");
        Path encryptedCopy = decryptDir.resolve("payload-copy.enc");
        Files.copy(encrypted, encryptedCopy);
        Path decrypted = new FileCipher(new AesGcmEngine(), DEFAULT_PASS.toCharArray()).decrypt(encryptedCopy);

        byte[] sourceDigest = sha256(source);
        byte[] decryptedDigest = sha256(decrypted);
        if (!Arrays.equals(sourceDigest, decryptedDigest)) {
            throw new IllegalStateException("stream digest mismatch");
        }
    }

    private static String sanitize(Exception ex) {
        String message = ex.getMessage();
        if (message == null || message.isBlank()) {
            return ex.getClass().getSimpleName();
        }
        return ex.getClass().getSimpleName() + ":" + message;
    }

    private static void assertEquals(String expected, String actual, String detail) {
        if (!expected.equals(actual)) {
            throw new IllegalStateException(detail);
        }
    }

    private static String repeatedAscii(int length) {
        StringBuilder builder = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            builder.append((char) ('a' + (i % 26)));
        }
        return builder.toString();
    }

    private static void writePatternFile(Path output, long sizeBytes) throws IOException {
        byte[] block = new byte[64 * 1024];
        for (int i = 0; i < block.length; i++) {
            block[i] = (byte) (i & 0xff);
        }

        try (OutputStream out = Files.newOutputStream(output)) {
            long written = 0L;
            while (written < sizeBytes) {
                int toWrite = (int) Math.min(block.length, sizeBytes - written);
                out.write(block, 0, toWrite);
                written += toWrite;
            }
            out.flush();
        }
    }

    private static byte[] sha256(Path path) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] buffer = new byte[64 * 1024];
        try (var in = Files.newInputStream(path)) {
            int read;
            while ((read = in.read(buffer)) != -1) {
                digest.update(buffer, 0, read);
            }
        }
        return digest.digest();
    }

    private interface CheckedCase {
        void run() throws Exception;
    }

    private static final class Counters {
        int passed;
        int failed;
    }
}
