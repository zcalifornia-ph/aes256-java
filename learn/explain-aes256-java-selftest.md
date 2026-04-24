# SelfTest.java Explained (aes256-java)

## 1. Title
Self-test harness for AES-256 Java text/file encryption checks.

## 2. Quick Diagnostic Read
Prerequisites:
- Basic Java class/method syntax and exceptions.
- Basic idea of authenticated encryption (AES-GCM fails on tamper or wrong key).
- Basic file I/O with `Path`, `Files`, and streams.

High-value new ideas in this file:
- A lightweight in-program test runner (`runCase`) with explicit `PASS`/`FAIL` lines.
- Security regression checks (tamper and wrong-passphrase rejection).
- Stream round-trip integrity check using SHA-256 on generated files.

## 3. One-Sentence Objective
Understand how `SelfTest.java` executes a deterministic acceptance suite, reports process-friendly results, and returns `0/1` exit codes based on crypto behavior checks.

## 4. Why This File Matters
`SelfTest` is a no-framework acceptance harness for this project: it validates text encryption/decryption correctness, authenticated-encryption failure behavior, overwrite-safety policy, and stream integrity behavior in one place. If this file is wrong, users can get false confidence (false pass) or noisy failures (false fail) when validating the crypto stack.

## 5. Plan A / Plan B
Plan A (code-first, 60-90 min):
1. Read `run(...)` first, then each `test*` method in call order.
2. Map each test to one requirement: round-trip, tamper, wrong passphrase, overwrite, stream digest.
3. Read `runCase(...)` and `sanitize(...)` to understand failure reporting format.

Plan B (concept-first, 45-75 min):
1. Start with the system view and execution trace below.
2. Read only one test from each category: text, negative-security, file-stream.
3. Return to line map to fill in helper methods (`repeatedAscii`, `sha256`, `writePatternFile`).

## 6. System View / Mental Model
```text
main(args)
  -> parse --selftest-large flag
  -> run(System.out, includeLarge)
      -> runCase(TEST-01..06 + TEST-03 variant)
          -> each case throws or returns
          -> PASS/FAIL line printed
      -> summary line printed
      -> return code: failed==0 ? 0 : 1
  -> System.exit(code)

Core dependency edges:
SelfTest -> TextCipher + FileCipher -> AesGcmEngine
```

Think of `SelfTest` as a tiny test framework embedded inside the app.

## 7. What This File Is and Where It Fits
- File type: Java source (`final class SelfTest`) in default package.
- Role: executable self-check suite with CLI entrypoint and reusable `runDefault(...)`/`run(...)` API.
- Direct dependencies used in this file: `TextCipher`, `FileCipher`, `AesGcmEngine`, `java.nio.file.*`, `MessageDigest`, `Base64`, `AEADBadTagException`.
- Consumers: inferred to be CLI/manual runners and possibly other classes that call `runDefault(...)` (consumer file not loaded, so this is an inference).

## 8. Just-Enough Primer
- Java method references like `SelfTest::testRoundTripSmallText` pass a no-arg method to `runCase(...)` via `CheckedCase`.
- Checked exceptions bubble up from test methods and are caught centrally in `runCase(...)`.
- `AEADBadTagException` is the expected signal for auth failure in AES-GCM when ciphertext/passphrase is wrong.
- `System.exit(code)` ends the process with shell-visible status.

## 9. Whole-File Outline Mapped to Code Regions
- Imports and class constants: lines 1-27.
- Constructor suppression (`private SelfTest()`): line 28.
- Public suite entry methods:
- `runDefault(PrintStream)`: lines 36-38.
- `run(PrintStream, boolean)`: lines 47-79.
- CLI entrypoint `main(String[])`: lines 93-102.
- Core case runner: `runCase(...)`: lines 104-113.
- Test cases:
- `testRoundTripSmallText`: lines 115-125.
- `testRoundTripMediumText`: lines 127-141.
- `testTamperDetection`: lines 143-157.
- `testWrongPassphrase`: lines 159-169.
- `testOverwriteRefusal`: lines 171-187.
- `testStreamRoundTrip`: lines 189-206.
- Helpers:
- `sanitize`: lines 208-214.
- `assertEquals`: lines 216-220.
- `repeatedAscii`: lines 222-228.
- `writePatternFile`: lines 230-245.
- `sha256`: lines 247-257.
- Internal types: `CheckedCase` lines 259-261, `Counters` lines 263-266.

## 10. Walkthrough by Section
### A) Suite setup and orchestration (`runDefault`, `run`, `main`)
`runDefault(...)` is just a convenience wrapper that calls `run(..., false)`. `run(...)` enforces `out != null`, initializes counters, executes six labeled tests via `runCase(...)`, and chooses small (8 MiB) or large (128 MiB) stream test for `TEST-03` based on `includeLargeStreamingCheck`. It always prints one summary line and returns `0` only when `failed == 0`. `main(...)` parses only `--selftest-large`; all other args are ignored, then exits with returned code.

Why it exists (inference): this gives both embeddable test execution and shell/CI-friendly process exit behavior without adding JUnit.

### B) Unified case executor (`runCase`)
`runCase(...)` is the test harness core. It executes `body.run()` and treats any thrown exception as failure. Success prints `PASS <id> <label>`, failure prints `FAIL <id> <label> reason=<sanitized>`, and counters mutate accordingly.

Data flow:
- In: `CheckedCase` lambda, label metadata, mutable `Counters`.
- Out: side effects on `out.println(...)` and `counters` fields.

Danger point: widening caught exception behavior or changing output format can break downstream parsers expecting this textual contract.

### C) Text round-trip tests (`testRoundTripSmallText`, `testRoundTripMediumText`)
These methods verify encrypt/decrypt symmetry through `TextCipher` with `AesGcmEngine` and the default passphrase. Cases include empty string, 1-byte string, generated 1 KiB text, and generated 1 MiB text. Equality is enforced by `assertEquals(...)` throwing `IllegalStateException` on mismatch.

Why it exists: catches data corruption/regression in text path for tiny and larger payload sizes.

### D) Negative-security tests (`testTamperDetection`, `testWrongPassphrase`)
`testTamperDetection` decodes Base64 ciphertext, flips the last byte, re-encodes, and expects decrypt to throw `AEADBadTagException`. `testWrongPassphrase` decrypts valid ciphertext with a known wrong passphrase and expects the same exception. If decrypt unexpectedly succeeds, each method throws `IllegalStateException` to force failure.

Invariant enforced: authenticated encryption must reject modified ciphertext and wrong keys.

### E) File overwrite policy test (`testOverwriteRefusal`)
Creates temp files where output path already exists, then calls `FileCipher.encrypt(source, existing)`. It expects `FileAlreadyExistsException` and additionally validates message prefix `refusing to overwrite:`.

Why it exists: validates non-overwrite safety contract and message shape that appears tied to `FileCipher.requireWritableOutput(...)`.

### F) Stream integrity test (`testStreamRoundTrip`)
Writes deterministic binary pattern to temp file (`writePatternFile`), encrypts to default `.enc`, copies encrypted file to another temp dir, decrypts there, computes SHA-256 of source/decrypted, and fails on digest mismatch.

Why copy encrypted file before decrypt (inference): to avoid decrypting in the same directory/path context as encryption and to simulate independent decrypt location.

### G) Utility helpers and internal types
- `sanitize(...)` compacts failure reason output to class name or `Class:message`.
- `repeatedAscii(...)` generates deterministic lowercase sequence for size-controlled payloads.
- `writePatternFile(...)` writes repeated 64 KiB byte pattern until target size.
- `sha256(...)` hashes a file in streaming mode.
- `CheckedCase` functional interface allows lambdas/method refs that throw checked exceptions.
- `Counters` holds mutable pass/fail state.

## 11. Data Flow / Control Flow / Dependency Map
Control flow:
1. Caller invokes `runDefault(...)`, `run(...)`, or `main(...)`.
2. `run(...)` executes each case through `runCase(...)`.
3. Each case interacts with crypto facade classes (`TextCipher`/`FileCipher`) which call `AesGcmEngine`.
4. Any thrown exception is converted to one failure line; execution continues to next case.
5. Final summary and exit code are produced.

Dependency map (loaded evidence):
- `SelfTest` depends on `TextCipher.encrypt/decrypt` for text checks.
- `SelfTest` depends on `FileCipher.encrypt/decrypt` for file and stream checks.
- Both ciphers use `AesGcmEngine` (from constructor calls).
- `SelfTest` expects auth failures to surface as `AEADBadTagException`.
- `SelfTest` expects overwrite prevention to surface as `FileAlreadyExistsException` with prefix `refusing to overwrite:`.

Safe modification points:
- Adding a new isolated `test*` method and registering a new `runCase(...)` call.
- Adjusting test labels/IDs if all consuming docs/scripts are updated.

Dangerous modification points:
- Changing exception expectations in tamper/wrong-pass tests without aligning engine semantics.
- Changing summary or PASS/FAIL output format if external automation parses it.
- Removing message prefix check can weaken overwrite-contract assertion.

## 12. Minimal Usage Example or Execution Trace
Hypothetical execution trace (shape based on code output format):
```text
PASS TEST-01 round-trip empty/1B
PASS TEST-02 round-trip 1KiB/1MiB
PASS TEST-04 tamper rejected
PASS TEST-05 wrong passphrase rejected
PASS TEST-06 overwrite refusal
PASS TEST-03 round-trip stream smoke
SELFTEST SUMMARY passed=6 failed=0
(exit code 0)
```

Large-stream variant invocation example:
```powershell
java SelfTest --selftest-large
```

## 13. Common Pitfalls / Misconceptions
- Misconception: `--selftest` is required. In this class, tests run regardless; only `--selftest-large` changes TEST-03 size.
- Pitfall: assuming test order is numeric. `TEST-03` runs last by design.
- Pitfall: treating passphrase constants as production secrets; they are fixed test fixtures.
- Pitfall: expecting failures to stop the suite. `runCase(...)` catches and continues.

## 14. Safe-Change Guide
Low risk changes:
- Add new helper methods that do not alter existing output contract.
- Add new cases using `runCase(...)` with unique IDs.
- Tune smoke/large sizes for local runtime constraints (if expectations/docs updated).

High risk changes:
- Modify `sanitize(...)` or line format consumed by scripts.
- Change exception types expected in negative tests without reviewing `AesGcmEngine`/JCA behavior.
- Remove digest check in stream test (would reduce integrity coverage).

## 15. Invariants / Contracts / Side Effects
Observed invariants:
- `run(...)` throws `IllegalArgumentException` when `out` is null.
- Return code is binary: `0` when no failures, else `1`.
- Exactly one summary line is printed per `run(...)` invocation.
- Tampered/wrong-key decrypt must trigger `AEADBadTagException` for tests to pass.
- Overwrite attempt must trigger `FileAlreadyExistsException` with expected message prefix.

Side effects:
- Creates temporary directories/files in overwrite and stream tests.
- Writes PASS/FAIL/SUMMARY lines to provided `PrintStream`.
- `main(...)` terminates JVM via `System.exit(...)`.

## 16. Self-Check or Practice Drill
Exercise (20-40 min):
1. Add `TEST-07` that verifies decrypting non-Base64 text through `TextCipher.decrypt(...)` fails.
2. Assert failure class/message in a stable way (similar to `runCase` output style).
3. Keep existing tests untouched.

Self-check rubric:
- Pass if new test fails first, then passes after correct assertion handling.
- Pass if existing TEST-01..06 still pass.
- Pass if summary count reflects the new case accurately.

## 17. Artifact Map
Target file:
- `aes256-java/SelfTest.java`: primary source of all section-level behavior claims.

Supporting context loaded for truthful dependency/contract explanation:
- `aes256-java/TextCipher.java`: confirms text encrypt/decrypt API and Base64 handling paths used by tests.
- `aes256-java/FileCipher.java`: confirms overwrite refusal semantics and message prefix source.
- `aes256-java/AesGcmEngine.java`: confirms AES-GCM behavior surface and stream encrypt/decrypt mechanics.

Process/template context loaded for documentation consistency:

## 18. Copy-Paste Prompt Examples
```text
explain aes256-java\SelfTest.java
explain aes256-java\TextCipher.java
explain aes256-java\FileCipher.java "requireWritableOutput"
explain aes256-java\AesGcmEngine.java "stream encrypt/decrypt"
```

## 19. 24-72 Hour Next Steps
1. Run `explain` on `AesGcmEngine.java` to deepen understanding of envelope and record framing.
2. Add one new negative test to `SelfTest` and validate output/exit code behavior manually.
3. Document expected self-test runtime for smoke vs large mode to support CI usage.

