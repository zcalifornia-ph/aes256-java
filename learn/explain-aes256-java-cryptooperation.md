# 1. Title
CryptoOperation.java Explained: One-Time Passphrase Lifecycle Base Class

## 2. Quick Diagnostic Read
Prerequisites:
- Java basics: classes, `abstract`, inheritance, visibility (`private`, `protected`, `public`).
- Basic security hygiene: why secrets should be copied and wiped.

High-value new idea in this file:
- A reusable base class that enforces a one-time passphrase consumption pattern for subclasses.

## 3. One-Sentence Objective
Understand how `CryptoOperation` centralizes engine wiring plus passphrase copy/wipe rules so `TextCipher` and `FileCipher` can safely inherit a consistent secret-handling contract.

## 4. Why This File Matters
`CryptoOperation` is the shared abstraction layer between app-facing cipher classes and the core crypto engine. If this class is weakened, both text and file workflows can leak secret material longer than intended or break lifecycle assumptions (`consumePassphrase()` only once unless reset), affecting `TextCipher`, `FileCipher`, and CLI behavior in `Main`.

## 5. Plan A / Plan B
Plan A (code-first, 30-45 min):
1. Read line ranges in section 9.
2. Trace one flow: constructor -> `consumePassphrase()` -> subclass operation.
3. Confirm how subclasses rely on it.

Plan B (concept-first, 25-35 min):
1. Read sections 6 and 11 first.
2. Treat this class as a small state machine: `set` -> `get` -> `consume` -> `cleared`.
3. Revisit the code with that model.

## 6. System View / Mental Model
```text
Main CLI
  -> new TextCipher(engine, passphrase) / new FileCipher(engine, passphrase)
      -> CryptoOperation constructor
          -> validates engine
          -> defensively copies passphrase into private storage

During encrypt/decrypt:
  subclass calls consumePassphrase()
    -> getPassphrase() returns copy
    -> clearStoredPassphrase() wipes stored secret and nulls reference
    -> subclass uses returned one-time char[] and wipes it in finally

Subclass identity:
  banner() -> "[CryptoOperation] " + describe()
  describe() is overridden by each subclass
```

## 7. What This File Is and Where It Fits
- File type: Java source file defining an abstract base class.
- Role: enforce shared state and security rules for passphrase lifecycle.
- Direct dependencies: `java.util.Arrays`, project class `AesGcmEngine`.
- Consumers:
- `TextCipher extends CryptoOperation` and calls `consumePassphrase()` + `getEngine()`.
- `FileCipher extends CryptoOperation` and calls `consumePassphrase()` + `getEngine()`.
- `Main` instantiates `TextCipher`/`FileCipher`, which indirectly depends on this contract.

## 8. Just-Enough Primer
- `abstract class`: cannot be instantiated directly; subclasses must implement abstract methods.
- `final` methods: subclass cannot override, which is used here to lock down security-sensitive lifecycle behavior.
- Defensive copy (`Arrays.copyOf`): prevents callers from mutating internal secret state by reference.

## 9. Whole-File Outline Mapped to Code Regions
- Imports: lines 1.
- Class declaration + fields: lines 9-12.
- Constructor (initial validation + setup): lines 20-23.
- Engine accessors and validation: lines 30-44.
- Passphrase access + setter with validation/copying: lines 51-69.
- Secret wipe and lifecycle transition methods: lines 74-90.
- Presentation helper (`banner`): lines 97-99.
- Abstract subclass contract (`describe`): lines 106.

## 10. Walkthrough by Section
### Imports (line 1)
What it does: imports `Arrays` utility for copying and filling arrays.
Why it exists: this class must both copy and wipe `char[]` securely.
Data/side effects: none by itself.
Risk if changed carelessly: replacing `Arrays.copyOf`/`Arrays.fill` with reference sharing would weaken encapsulation.

### Class + fields (lines 9-12)
What it does: defines `private char[] passphrase` and `protected AesGcmEngine engine`.
How it works: passphrase is private to prevent direct external access; engine is protected so subclasses can use base getter/setter semantics.
Invariant: stored passphrase is either a valid non-empty char array copy or `null` (cleared state).
Risk: widening passphrase visibility would bypass lifecycle controls.

### Constructor (lines 20-23)
What it does: routes initialization through `setEngine` and `setPassphrase` instead of direct assignment.
Why: guarantees constructor path reuses validation + defensive-copy logic.
Data flow: input params -> validated setters -> internal fields.
Risk: direct assignment in constructor would duplicate logic and can drift from setter rules.

### Engine getter/setter (lines 30-44)
What it does: exposes engine and blocks null assignment.
How it works: `setEngine` throws `IllegalArgumentException` for null.
Side effects: mutates engine reference when valid.
Invariant: `engine` should never be null after successful construction/set.
Risk: removing null check can push failures downstream into encrypt/decrypt call sites.

### Passphrase getter/setter (lines 51-69)
What it does:
- `getPassphrase()` returns a defensive copy.
- `setPassphrase()` validates non-null/non-empty, clears old stored passphrase, stores a new copy.
How it works: explicit cleared-state guard in getter (`IllegalStateException` if null).
Side effects: setter wipes old stored secret before replacement.
Invariant: callers never get direct reference to internal `passphrase`.
Risk: returning raw reference would allow external mutation of internal secret.

### Clearing + one-time consume (lines 74-90)
What it does:
- `clearStoredPassphrase()` zeroes current array with `'\0'` and nulls field.
- `consumePassphrase()` gets a copy, immediately clears internal storage, returns one-time operation copy.
Why: reduce secret lifetime in object memory and enforce explicit reset for subsequent operations.
Data flow: internal stored passphrase -> returned operation copy; internal state transitions to cleared.
Risk: if `consumePassphrase()` stopped clearing state, subclasses could accidentally reuse secrets indefinitely.

### Banner + subclass description contract (lines 97-106)
What it does: composes presentation string via `banner()` and requires subclass `describe()` override.
Why: polymorphic identity for CLI/docs surfaces while keeping shared prefix format.
Risk: making `banner()` non-final could lead to inconsistent output conventions.

## 11. Data Flow / Control Flow / Dependency Map
Data/control flow for passphrase lifecycle:
1. Construction or reset: `setPassphrase()` validates and stores a copy.
2. Subclass operation starts: `consumePassphrase()` retrieves copy via `getPassphrase()`.
3. Base class immediately wipes internal storage.
4. Subclass uses returned `char[]` with `AesGcmEngine` and then wipes operation array in `finally` (verified in `TextCipher` lines 84-85, 109 and `FileCipher` lines 83-84, 124).

Dependency map:
- Requires: `AesGcmEngine` and `java.util.Arrays`.
- Required by: `TextCipher`, `FileCipher`.
- Indirect runtime caller path: `Main` -> `TextCipher`/`FileCipher` -> `CryptoOperation`.

Safe modification points:
- `banner()` format string (low risk if docs/UX tolerate change).
- Error message text (low-medium risk; may affect tests or UX consistency).

Dangerous modification points:
- `getPassphrase()` defensive copy behavior.
- `setPassphrase()` pre-clear + copy semantics.
- `consumePassphrase()` immediate clear behavior.
- `clearStoredPassphrase()` wiping implementation.

## 12. Minimal Usage Example or Execution Trace
Hypothetical static trace (not executed):
```java
AesGcmEngine engine = new AesGcmEngine();
TextCipher cipher = new TextCipher(engine, "secret".toCharArray());

String c1 = cipher.encrypt("hello"); // consumePassphrase() clears stored passphrase

cipher.setPassphrase("secret".toCharArray()); // required before next operation
String p1 = cipher.decrypt(c1);
```
Expected lifecycle effect:
- First call consumes and clears stored passphrase.
- Second call requires explicit `setPassphrase(...)` first, otherwise base getter path would throw `IllegalStateException`.

## 13. Common Pitfalls / Misconceptions
- “`getPassphrase()` gives me the actual stored array.” Incorrect: it returns a copy.
- “I can call encrypt/decrypt repeatedly without resetting passphrase.” Not with current contract; passphrase is one-time consumed.
- “`clearStoredPassphrase()` is optional cleanup.” In this design, it is part of the core security lifecycle.

## 14. Safe-Change Guide
Safer changes:
- Add JavaDoc clarifications for one-time-use semantics.
- Improve banner wording if consumer output expectations are updated.

High-risk changes:
- Converting `char[]` passphrase handling to immutable `String` (raises memory-retention risk).
- Removing `final` from lifecycle methods (`getPassphrase`, `setPassphrase`, `consumePassphrase`, `clearStoredPassphrase`).
- Stopping passphrase wipe/nulling.

## 15. Invariants / Contracts / Side Effects
Observed facts:
- `engine` cannot be set to null (`setEngine`, lines 39-43).
- Stored passphrase must be non-null and non-empty when set (`setPassphrase`, lines 63-66).
- Stored passphrase is copied on set and copied again on get (lines 55, 68).
- `consumePassphrase()` clears internal storage immediately after obtaining operation copy (lines 86-89).

Inference (explicit):
- The one-time-consume pattern is intended to minimize accidental secret reuse and secret lifetime in memory.

Side effects:
- Mutates internal secret state, including irreversible clear to `null` until reset.
- Throws runtime exceptions for invalid lifecycle/use states.

## 16. Self-Check or Practice Drill
Exercise (20-30 min):
1. Add a small temporary test/program snippet that:
- creates `TextCipher` with passphrase,
- calls `encrypt` once,
- calls `encrypt` again without `setPassphrase`.
2. Observe and explain the failure path.
3. Add `setPassphrase` between operations and verify successful second call.

Self-check rubric:
- You can explain why second call fails without reset.
- You can identify exact base-class method enforcing that behavior.
- You can describe where passphrase wiping happens in both base and subclass layers.

## 17. Artifact Map
Target file read fully:
- `aes256-java/CryptoOperation.java` (primary source of truth).

Supporting context read (bounded):
- `aes256-java/TextCipher.java` (subclass behavior, `consumePassphrase()` and wipe usage).
- `aes256-java/FileCipher.java` (subclass behavior, stream path + wipe usage).
- `aes256-java/AesGcmEngine.java` (engine dependency surface used by subclasses).
- `aes256-java/Main.java` (caller path showing instantiation/usage context).
- `aes256-java/README.md` (project-level OOP role description alignment).

## 18. Copy-Paste Prompt Examples
```text
explain aes256-java/CryptoOperation.java
explain aes256-java/TextCipher.java
explain aes256-java/FileCipher.java
explain aes256-java/Main.java "encryptText flow"
explain aes256-java
```

## 19. 24-72 Hour Next Steps
1. Explain `TextCipher.java` next to see how this base contract is consumed in text workflows.
2. Explain `FileCipher.java` to compare the same contract in stream/file workflows.
3. Do one controlled refactor proposal: keep invariants but improve developer ergonomics (for example, add an explicit `isPassphraseLoaded()` query) and reason about tradeoffs before coding.

