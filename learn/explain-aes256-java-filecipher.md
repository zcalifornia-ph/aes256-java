# FileCipher.java Explained (aes256-java)

## 1. Title
`aes256-java/FileCipher.java` - File-oriented AES-GCM facade over stream crypto primitives.

## 2. Quick Diagnostic Read
Prerequisites:
- Basic Java classes, methods, exceptions, and `try-with-resources`.
- Basic file APIs (`Path`, `Files`, `InputStream`, `OutputStream`).
- High-level idea of encryption/decryption APIs.

High-value things this file teaches:
- How to wrap low-level stream crypto in safer file-level operations.
- How filename policies are enforced (`.enc` and `.dec`) without overwriting files.
- How passphrase lifecycle is constrained to one operation and then wiped.

## 3. One-Sentence Objective
Understand how `FileCipher` validates file paths, chooses output names, delegates encryption/decryption to `AesGcmEngine`, and enforces safe file-write behavior.

## 4. Why This File Matters
`FileCipher` is the bridge between user-facing file actions and cryptographic stream operations. In the CLI flow, `Main` creates `FileCipher` for menu options 3 and 4 (`aes256-java/Main.java:338`, `aes256-java/Main.java:370`) and uses it to encrypt/decrypt files (`aes256-java/Main.java:345`, `aes256-java/Main.java:377`). If this class is wrong, users can lose data (wrong output naming), hit insecure behaviors (passphrase mishandling), or get fragile I/O outcomes (accidental overwrite).

## 5. Plan A / Plan B
Plan A (code-first, 45-60 min):
1. Read the whole file once.
2. Track method call flow for `encrypt(Path)` and `decrypt(Path)`.
3. Re-read helper methods (`require*`, `resolveDefault*`) and map all exceptions.

Plan B (concept-first, 30-45 min):
1. Start with the System View and Data Flow sections in this guide.
2. Read only public methods (`encrypt`, `decrypt`, `describe`) first.
3. Read private helpers second to understand guardrails.

## 6. System View / Mental Model
```text
Caller (CLI/Main)
  -> FileCipher.encrypt(inputPath)
      -> validate input exists and is regular file
      -> choose output name (input + ".enc")
      -> validate output does not exist and parent dir exists
      -> consume passphrase (one-time)
      -> open input/output streams
      -> AesGcmEngine.encrypt(in, out, passphrase)
      -> wipe operation passphrase
      -> return output path

Caller (CLI/Main)
  -> FileCipher.decrypt(inputPath)
      -> validate input
      -> choose output name:
           if name ends with ".enc" and has basename -> strip suffix
           else -> append ".dec"
      -> same write/crypto/wipe pipeline via private decrypt(input, output)
```

## 7. What This File Is and Where It Fits
- File type: Java source file (concrete class).
- Role: OOP facade focused on file-path and file-stream concerns.
- Inheritance: `FileCipher extends CryptoOperation` (`aes256-java/FileCipher.java:19`).
- Key dependencies:
  - `CryptoOperation` for `consumePassphrase()` and `getEngine()` (`aes256-java/CryptoOperation.java:30`, `aes256-java/CryptoOperation.java:86`).
  - `AesGcmEngine` stream methods (`aes256-java/AesGcmEngine.java:194`, `aes256-java/AesGcmEngine.java:265`).
  - Java NIO/file APIs for existence checks and stream creation.
- Primary consumers:
  - CLI code in `Main` encrypt/decrypt file actions.

## 8. Just-Enough Primer
- `Path` is an abstract filesystem path object (`File` is older API; both are supported here).
- `try ( ... ) { ... }` auto-closes streams even when exceptions happen.
- `StandardOpenOption.CREATE_NEW` means "create only if missing" and fails if already present.
- `throws IOException, GeneralSecurityException` means callers must handle both I/O failures and crypto failures.

## 9. Whole-File Outline Mapped to Code Regions
- Imports and class documentation: lines 1-18
- Class declaration and constructor: lines 19-29
- Public encrypt overloads:
  - `encrypt(Path)`: lines 39-43
  - `encrypt(File)`: lines 53-58
  - `encrypt(Path, Path)`: lines 69-85
- Public decrypt entrypoint: lines 95-99
- Override descriptor: lines 106-109
- Private decrypt worker: lines 111-126
- Input/output validation helpers:
  - `requireReadableInput`: lines 128-139
  - `requireWritableOutput`: lines 141-153
- Default output naming helpers:
  - `resolveDefaultEncryptedOutput`: lines 155-161
  - `resolveDefaultDecryptedOutput`: lines 163-177

## 10. Walkthrough by Section
### A) Constructor and inheritance seam (lines 19-29)
- Observed: constructor forwards `engine` and `passphrase` to superclass with `super(engine, passphrase)`.
- Why it exists: centralize passphrase and engine lifecycle in `CryptoOperation` instead of duplicating logic.
- Side effect: constructor can throw from superclass validation when inputs are invalid.

### B) Encrypt overload set (lines 39-85)
- `encrypt(Path)` is convenience mode: validate input, auto-pick `<filename>.enc`, then delegate to explicit-output overload.
- `encrypt(File)` is compatibility overload for callers with legacy `File`; it null-checks and converts to `Path`.
- `encrypt(Path, Path)` is the core write path:
  - validates input/output,
  - obtains one-time passphrase copy via `consumePassphrase()`,
  - opens streams with `CREATE_NEW` + `WRITE`,
  - delegates to `getEngine().encrypt(in, out, passphrase)`,
  - wipes operation passphrase in `finally`.
- Contract preserved: output must not already exist.
- What breaks if changed carelessly: replacing `CREATE_NEW` with overwrite behavior would violate current safety policy.

### C) Decrypt entry + worker split (lines 95-99, 111-126)
- Public `decrypt(Path)` mirrors encrypt convenience mode: validate input, compute default output, call private worker.
- Private worker handles shared write mechanics and passphrase wiping.
- Observed asymmetry: private decrypt assumes `input` is already validated by caller and does not re-check readability.
- Inference: split avoids duplicating stream/cleanup logic while keeping the public API small.

### D) `describe()` override (lines 106-109)
- Returns constant `"FileCipher"`.
- This fulfills polymorphic contract in `CryptoOperation#describe()` and supports banners/docs surfaces.

### E) Readability and writability guards (lines 128-153)
- `requireReadableInput` enforces:
  - non-null,
  - path exists,
  - is regular file.
- `requireWritableOutput` enforces:
  - non-null,
  - destination must not exist,
  - parent directory must exist (if parent is present).
- Side effects: none on filesystem; only checks and throws.
- Risk point: these checks happen before opening stream; race conditions are still possible in concurrent filesystem scenarios.

### F) Default filename policies (lines 155-177)
- Encrypt default: sibling path with `.enc` appended (`report.txt` -> `report.txt.enc`).
- Decrypt default:
  - if ends with `.enc` and has non-empty basename, strip it (`report.txt.enc` -> `report.txt`),
  - else append `.dec` (`blob.bin` -> `blob.bin.dec`, `.enc` -> `.enc.dec`).
- Contract: input must have filename component (`getFileName() != null`) or it throws.

## 11. Data Flow / Control Flow / Dependency Map
Data flow (encrypt):
1. `Path input` enters API.
2. Validation ensures readable regular file.
3. Output path resolved/validated.
4. Passphrase copied + stored copy cleared by superclass logic.
5. File bytes stream from input -> `AesGcmEngine.encrypt` -> output stream.
6. Operation passphrase array wiped.

Control dependencies:
- `FileCipher` controls file policy and stream lifecycle.
- `AesGcmEngine` controls cryptographic transform details.
- `CryptoOperation` controls passphrase state discipline.

Who depends on this file:
- CLI file operations in `Main`.

## 12. Minimal Usage Example or Execution Trace
Hypothetical trace:
```java
AesGcmEngine engine = new AesGcmEngine();
FileCipher cipher = new FileCipher(engine, "secret123".toCharArray());
Path encrypted = cipher.encrypt(Path.of("notes.txt"));
// encrypted -> notes.txt.enc (if destination does not already exist)
```

Then for decrypt:
```java
FileCipher decryptCipher = new FileCipher(new AesGcmEngine(), "secret123".toCharArray());
Path plain = decryptCipher.decrypt(Path.of("notes.txt.enc"));
// plain -> notes.txt
```

## 13. Common Pitfalls / Misconceptions
- Reusing one `FileCipher` instance for many operations without calling `setPassphrase(...)` again. `consumePassphrase()` clears stored passphrase after one operation.
- Expecting overwrite behavior. Existing output path throws `FileAlreadyExistsException`.
- Assuming decrypt always strips suffix. It only strips when filename ends with `.enc` and has additional basename.
- Passing directory paths as input; class requires regular files.

## 14. Safe-Change Guide
Safer changes:
- Improve error message wording in validation helpers.
- Add additional overloads that still delegate into existing validated core methods.
- Add docs/comments clarifying naming rules.

Higher-risk changes:
- Changing output open options (`CREATE_NEW` policy).
- Altering passphrase handling sequence (consume -> use -> wipe).
- Modifying default naming behavior without updating CLI/user docs and tests.

## 15. Invariants / Contracts / Side Effects
Invariants:
- Input for crypto operations must be a readable regular file.
- Output path must not already exist.
- Passphrase copy used for a crypto operation is wiped in `finally`.
- `describe()` stays stable as `"FileCipher"` for polymorphic consumers.

Side effects:
- Creates new output file and writes encrypted/decrypted bytes.
- Can throw checked exceptions for I/O and crypto failures.

## 16. Self-Check or Practice Drill
Exercise (20-40 min):
1. Add tests for `resolveDefaultDecryptedOutput` edge cases:
   - `a.enc` -> `a`
   - `.enc` -> `.enc.dec`
   - `archive` -> `archive.dec`
2. Add test proving overwrite refusal when output already exists.

Self-check rubric:
- You can explain why each expected output name is produced.
- You can show that overwrite policy is enforced by exception, not by silent replacement.
- Existing behavior for normal `.enc` inputs remains unchanged.

## 17. Artifact Map
Target file read:
- `aes256-java/FileCipher.java` (primary explanation target)

Supporting files read (bounded context):
- `aes256-java/CryptoOperation.java` (superclass contracts for passphrase/engine lifecycle)
- `aes256-java/AesGcmEngine.java` (stream encrypt/decrypt APIs used by this class)
- `aes256-java/Main.java` (runtime caller surface and user-facing flow)
- `aes256-java/README.md` (project-level behavior contract and file naming expectations)

Style references read:

## 18. Copy-Paste Prompt Examples
```text
explain aes256-java/FileCipher.java
explain aes256-java/CryptoOperation.java
explain aes256-java/AesGcmEngine.java "stream decrypt"
explain aes256-java/Main.java "Encrypt file / Decrypt file menu path"
explain aes256-java
```

## 19. 24-72 Hour Next Steps
1. Write a focused component test suite for `FileCipher` path-validation and naming behavior.
2. Add explicit API docs stating one-time passphrase semantics inherited from `CryptoOperation`.
3. Trace one end-to-end file operation from `Main` -> `FileCipher` -> `AesGcmEngine` and document exact exception mapping.
4. If changing file naming policy, update README and CLI messaging together to avoid user confusion.

