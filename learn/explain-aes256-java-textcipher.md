# 1. Title
`TextCipher.java` Explained: UTF-8 Text Facade Over AES-GCM Envelopes

## 2. Quick Diagnostic Read
Prerequisites:
- Basic Java classes/methods, exceptions, and `try/finally`.
- Basic AES-GCM idea: encrypt/decrypt bytes with authentication.
- Basic Base64 encoding.

High-value ideas in this file:
- One-time passphrase consumption via inherited `consumePassphrase()`.
- Memory hygiene with `Arrays.fill(...)` on sensitive arrays.
- Two encrypt overloads (`String` and `char[]`) that converge to one internal byte-path.

## 3. One-Sentence Objective
Understand exactly how `TextCipher` converts text <-> Base64 AES-GCM envelopes while enforcing input checks and best-effort memory wiping.

## 4. Why This File Matters
`TextCipher` is the text-facing API used by CLI flows (`Main.java` text encrypt/decrypt menu handlers) and self-tests (`SelfTest.java` text round-trip/tamper/wrong-pass tests). It isolates text conversion and Base64 transport concerns so the cryptographic primitive class (`AesGcmEngine`) can stay byte-oriented.

## 5. Plan A / Plan B
Plan A (code-first, ~35 min):
1. Read the outline section (line map).
2. Trace `encrypt(String)` -> `encryptBytes(...)`.
3. Trace `decrypt(String)` end-to-end.
4. Compare against `Main.java` and `SelfTest.java` call sites.

Plan B (concept-first, ~25 min):
1. Read the system view first.
2. Read section walkthrough in this order: `encryptBytes`, `decrypt`, helpers.
3. Revisit overloads last.

## 6. System View / Mental Model
```text
Caller (Main/SelfTest)
  -> TextCipher.encrypt(String|char[])
      -> UTF-8 bytes
      -> consumePassphrase() [from CryptoOperation]
      -> AesGcmEngine.encrypt(bytes, passphrase)
      -> Base64 encode envelope bytes
      -> return String ciphertext

Caller
  -> TextCipher.decrypt(base64)
      -> Base64 decode to envelope bytes
      -> consumePassphrase()
      -> AesGcmEngine.decrypt(envelope, passphrase)
      -> UTF-8 decode plaintext bytes
      -> return String plaintext

At each sensitive stage: wipe char[] / byte[] in finally blocks.
```

## 7. What This File Is and Where It Fits
- File type: Java source class.
- Role: Text facade over byte-array crypto engine.
- Declared class: `public final class TextCipher extends CryptoOperation`.
- Depends on:
  - `CryptoOperation` for engine storage and passphrase lifecycle.
  - `AesGcmEngine` for actual AES-GCM encryption/decryption.
  - JDK `Base64`, `StandardCharsets`, `Arrays`, and `GeneralSecurityException`.
- Consumers observed in repo:
  - `Main.encryptText/decryptText` constructs `TextCipher` and calls `encrypt/decrypt`.
  - `SelfTest` uses `TextCipher` for round-trip and tamper/wrong-pass checks.

## 8. Just-Enough Primer
- Java method overloading: same method name, different params (`encrypt(String)` and `encrypt(char[])`).
- `try/finally`: `finally` runs even if exceptions occur, so it is used here for wipes.
- `StandardCharsets.UTF_8`: deterministic string-byte conversion.
- `Base64`: converts binary envelope bytes into printable text for CLI transport.

## 9. Whole-File Outline Mapped to Code Regions
- Lines 1-4: Imports.
- Lines 6-11: Class Javadoc (text facade intent).
- Lines 12-22: Class declaration + constructor.
- Lines 24-38: `encrypt(String)`.
- Lines 40-63: `encrypt(char[])`.
- Lines 65-90: `decrypt(String)`.
- Lines 92-100: `describe()` override.
- Lines 102-115: private `encryptBytes(byte[])` core encryption path.
- Lines 117-123: private `decodeEnvelope(String)` Base64 validator.
- Lines 125-127: private `toUtf8Bytes(char[])` conversion helper.

## 10. Walkthrough by Section
### Constructor (lines 20-22)
What: forwards engine + passphrase to superclass constructor.
How: `super(engine, passphrase)` lets `CryptoOperation` validate non-null engine/passphrase and copy passphrase.
Why: keeps passphrase lifecycle policy centralized in base class.

### `encrypt(String)` (lines 31-38)
What: encrypts normal Java `String` plaintext.
How:
1. Rejects null.
2. Converts plaintext to UTF-8 bytes.
3. Delegates to `encryptBytes(...)`.
Data flow: `String` -> `byte[]` -> envelope `byte[]` -> Base64 `String`.
Side effects: plaintext bytes are wiped by `encryptBytes(...)` finally block.

### `encrypt(char[])` (lines 47-63)
What: encrypts mutable character payload.
How:
1. Rejects null.
2. Defensive-copies caller array.
3. Converts copy to UTF-8 bytes.
4. Delegates to `encryptBytes(...)`.
5. Always wipes copied chars and converted bytes in `finally`.
Important note: `toUtf8Bytes` creates an intermediate `String` object (`new String(plaintext)`), which cannot be manually wiped in Java; wiping still occurs for mutable arrays.

### `decrypt(String)` (lines 72-90)
What: decrypts Base64 ciphertext envelope back to plaintext text.
How:
1. Rejects null input.
2. Decodes Base64 via `decodeEnvelope`.
3. Calls inherited `consumePassphrase()` (one-time passphrase use).
4. Calls `getEngine().decrypt(envelopeBytes, operationPassphrase)`.
5. Decodes returned bytes as UTF-8 `String`.
6. Wipes passphrase copy, envelope bytes, and plaintext bytes in `finally`.
Contracts:
- Throws `GeneralSecurityException` when auth/decryption fails.
- Throws `IllegalArgumentException` for invalid Base64.

### `describe()` (lines 97-100)
What: returns constant label `"TextCipher"`.
Why: used by polymorphic presentation surfaces (`CryptoOperation.banner()` and docs/CLI mappings).

### `encryptBytes(byte[])` (lines 102-115)
What: shared internal encryption core for both overloads.
How:
1. Consumes passphrase once (`consumePassphrase()`).
2. Calls `AesGcmEngine.encrypt(plaintextBytes, operationPassphrase)`.
3. Base64-encodes envelope bytes.
4. In `finally`, wipes passphrase chars, plaintext bytes, and envelope bytes.
Critical behavior: because passphrase is consumed, the same `TextCipher` instance cannot encrypt/decrypt again unless caller sets a new passphrase via inherited `setPassphrase(...)`.

### `decodeEnvelope(...)` + `toUtf8Bytes(...)` (lines 117-127)
- `decodeEnvelope`: wraps decoder failure with domain-specific message: `"ciphertext must be valid Base64"`.
- `toUtf8Bytes`: helper to convert `char[]` to UTF-8 `byte[]`.

## 11. Data Flow / Control Flow / Dependency Map
Primary encrypt path:
`encrypt(String|char[])` -> `encryptBytes` -> `CryptoOperation.consumePassphrase` -> `AesGcmEngine.encrypt` -> Base64 encode -> return.

Primary decrypt path:
`decrypt(String)` -> `decodeEnvelope` -> `CryptoOperation.consumePassphrase` -> `AesGcmEngine.decrypt` -> UTF-8 decode -> return.

Dependency map:
- Upstream callers: `Main` text menu handlers; `SelfTest` text test cases.
- Local dependency: `CryptoOperation` (engine/passphrase lifecycle).
- Crypto dependency: `AesGcmEngine` byte-level AES-GCM + PBKDF2 envelope implementation.

Safe modification points:
- Error messages and documentation comments.
- Presentation label from `describe()`.

Dangerous modification points:
- Any `finally` wipe block.
- Any call to `consumePassphrase()` and passphrase lifecycle assumptions.
- UTF-8/Base64 conversion boundaries.

## 12. Minimal Usage Example or Execution Trace
Hypothetical trace:
```java
TextCipher c = new TextCipher(new AesGcmEngine(), "secret".toCharArray());
String ct = c.encrypt("hello");
// c has consumed and cleared stored passphrase after encrypt.

c.setPassphrase("secret".toCharArray()); // required for second operation
String pt = c.decrypt(ct);
// pt == "hello"
```
If passphrase is wrong on decrypt, `AesGcmEngine.decrypt(...)` can raise `AEADBadTagException` (a `GeneralSecurityException` subtype), as exercised by `SelfTest`.

## 13. Common Pitfalls / Misconceptions
- Reusing one `TextCipher` instance without resetting passphrase: second operation can fail due to consumed/cleared passphrase state.
- Assuming `char[]` path avoids all immutable-string exposure: `toUtf8Bytes` still creates an intermediate `String`.
- Treating Base64 errors as crypto-auth errors: malformed Base64 fails earlier with `IllegalArgumentException`.

## 14. Safe-Change Guide
Low-risk edits:
- Refine method docs/Javadocs.
- Improve user-facing error text (keeping semantics).
- Add overloads that still delegate to `encryptBytes` and keep wipe/finally semantics.

High-risk edits:
- Removing `Arrays.fill` wipes.
- Changing passphrase-consumption order.
- Changing UTF-8 encoding choice or Base64 codec flavor without updating all callers/tests.

## 15. Invariants / Contracts / Side Effects
Observed invariants:
- Null plaintext/base64 inputs are rejected with `IllegalArgumentException`.
- Encryption output is Base64 text representing an engine envelope.
- Decryption input must be valid Base64 envelope text.
- Sensitive mutable arrays are wiped in `finally` where available.
- Passphrase is consumed per operation via inherited lifecycle.

Side effects:
- Stored passphrase state in superclass is cleared after `consumePassphrase()`.
- Input/output mutable arrays are overwritten with zero values.

## 16. Self-Check or Practice Drill
Exercise (20-40 min):
1. Add a small test that uses one `TextCipher` instance for two operations without resetting passphrase.
2. Confirm second call fails due to consumed passphrase.
3. Add `setPassphrase(...)` between calls and confirm both pass.

Self-check rubric:
- You can explain why failure occurs from `consumePassphrase()` behavior.
- You can point to exact wipe/clear points in `TextCipher` and `CryptoOperation`.
- You preserve existing behavior for invalid Base64 and wrong-passphrase detection.

## 17. Artifact Map
Target file read in full:
- `aes256-java/TextCipher.java` (primary subject).

Supporting context loaded for truthful claims:
- `aes256-java/CryptoOperation.java` (passphrase lifecycle, `consumePassphrase`, `banner`, inheritance contract).
- `aes256-java/AesGcmEngine.java` (byte-level envelope encrypt/decrypt behavior and exceptions).
- `aes256-java/Main.java` (text encrypt/decrypt call sites).
- `aes256-java/SelfTest.java` (round-trip, tamper, wrong-passphrase usage patterns).

Process/template context loaded:

## 18. Copy-Paste Prompt Examples
```text
explain aes256-java/TextCipher.java
explain aes256-java/CryptoOperation.java
explain aes256-java/AesGcmEngine.java "deriveKey method"
explain aes256-java/Main.java "encryptText and decryptText"
```

## 19. 24-72 Hour Next Steps
1. Explain `CryptoOperation.java` next to lock in passphrase lifecycle mental model.
2. Explain `AesGcmEngine.java` with focus on envelope layout (`salt || iv || ciphertext || tag`).
3. Add focused tests around passphrase reuse and invalid Base64 inputs to strengthen regression safety.

## Observed Facts vs Inferences
Observed facts:
- `TextCipher` has two public `encrypt` overloads and one public `decrypt` method.
- Both encrypt paths end at `encryptBytes(byte[])`.
- `decrypt(String)` decodes Base64 first, then decrypts via engine.
- Sensitive mutable arrays are wiped in `finally` blocks in this file.
- `consumePassphrase()` is called in both encrypt/decrypt core paths.

Inferences (explicit):
- `TextCipher` exists to keep text/Base64 concerns separate from byte-level crypto details. (Inference based on method boundaries and delegation.)
- `describe()` supports presentation/CLI documentation surfaces beyond this file. (Inference based on comments and `CryptoOperation.banner()` usage.)

## Verification Notes
- Verified all behavioral claims against `TextCipher.java` line-by-line.
- Supporting dependency claims were checked against loaded `CryptoOperation.java`, `AesGcmEngine.java`, `Main.java`, and `SelfTest.java`.
- Inferences are explicitly labeled in the section above.
- No unresolved focus request (no `INPUT_2` provided).
- No runtime-only claims made that require executing code.


