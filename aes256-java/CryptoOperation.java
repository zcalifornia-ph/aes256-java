import java.util.Arrays;

/**
 * Abstract base type for the Unit-02 OOP abstraction layer.
 *
 * <p>This class is intentionally small in BOLT-2.1. It establishes encapsulation and
 * inheritance seams; behavior-rich cipher bodies are scheduled for BOLT-2.2.
 */
public abstract class CryptoOperation {

    private char[] passphrase;
    protected AesGcmEngine engine;

    /**
     * Creates a crypto operation with a concrete engine and passphrase state.
     *
     * @param engine cryptographic engine dependency
     * @param passphrase mutable passphrase characters
     */
    protected CryptoOperation(AesGcmEngine engine, char[] passphrase) {
        setEngine(engine);
        setPassphrase(passphrase);
    }

    /**
     * Gets the configured engine.
     *
     * @return the engine instance used by this operation
     */
    public final AesGcmEngine getEngine() {
        return engine;
    }

    /**
     * Replaces the engine dependency.
     *
     * @param engine cryptographic engine dependency
     */
    public final void setEngine(AesGcmEngine engine) {
        if (engine == null) {
            throw new IllegalArgumentException("engine must not be null");
        }
        this.engine = engine;
    }

    /**
     * Returns a defensive copy of the passphrase.
     *
     * @return copied passphrase characters
     */
    public final char[] getPassphrase() {
        return Arrays.copyOf(passphrase, passphrase.length);
    }

    /**
     * Replaces the stored passphrase value using defensive copy semantics.
     *
     * @param passphrase mutable passphrase characters
     */
    public final void setPassphrase(char[] passphrase) {
        if (passphrase == null || passphrase.length == 0) {
            throw new IllegalArgumentException("passphrase must not be null or empty");
        }
        clearStoredPassphrase();
        this.passphrase = Arrays.copyOf(passphrase, passphrase.length);
    }

    /**
     * Clears the stored passphrase in-place.
     */
    public final void clearStoredPassphrase() {
        if (passphrase != null) {
            Arrays.fill(passphrase, '\0');
        }
    }

    /**
     * Returns a small presentation banner generated from the subclass description.
     *
     * @return banner text
     */
    public final String banner() {
        return "[CryptoOperation] " + describe();
    }

    /**
     * Provides a subclass description used by the CLI and docs map.
     *
     * @return operation description text
     */
    public abstract String describe();
}
