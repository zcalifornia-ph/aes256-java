# Version 0.1.2 Documentation

## Title
PHASE 02 / MILESTONE 2.2: OOP Wrapper Behavior Implementation

## Quick Diagnostic Read

`v0.1.2` moves PHASE-02 from signatures-only scaffolding to runnable library behavior.

This version completes the core wrapper implementation layer:

- `CryptoOperation` now includes a passphrase consume-and-clear lifecycle.
- `TextCipher` now performs Base64 text envelope encryption/decryption using PHASE-01 APIs.
- `FileCipher` now performs stream file encryption/decryption with naming and overwrite policies.

## One-Sentence Objective

Implement and validate PHASE-02 OOP behavior wrappers so text/file encryption flows can be exercised through `TextCipher` and `FileCipher`, not only through `AesGcmEngine`.

## Scope and Versioning

- Previous documented version: `v0.1.1`
- Current documented version: `v0.1.2`
- Release type: pre-alpha patch milestone for PHASE-02 behavior implementation

## File-Level Change Summary

1. `aes256-java/CryptoOperation.java`

- Added `consumePassphrase()` to return one operation copy and clear internal stored passphrase state.
- Updated `getPassphrase()` to guard against use after clear.
- Kept encapsulation and inheritance seams from MILESTONE-2.1 intact.

2. `aes256-java/TextCipher.java`

- Implemented:
  - `encrypt(String plaintext)`
  - `encrypt(char[] plaintext)`
  - `decrypt(String base64Envelope)`
- Added Base64 envelope encoding/decoding around PHASE-01 engine byte-array APIs.
- Added null/input validation and explicit memory clearing for operation buffers.

3. `aes256-java/FileCipher.java`

- Implemented:
  - `encrypt(Path input)`
  - `encrypt(File input)`
  - `encrypt(Path input, Path output)`
  - `decrypt(Path input)`
- Added file policy logic:
  - encrypt defaults to `<input>.enc`
  - decrypt strips `.enc` when present, else appends `.dec`
  - output overwrite is rejected via `FileAlreadyExistsException` with `refusing to overwrite: <path>` message prefix.
- Added input/output path validation and stream delegation to `AesGcmEngine`.

4. `README.md`

- Bumped visible version/status to `v0.1.2`.
- Updated feature and roadmap milestones for completed MILESTONE-2.2.
- Updated library-mode usage notes to describe implemented PHASE-02 wrappers and passphrase reset behavior between operations.

5. `CHANGELOG.md`

- Added `v0.1.2` entry with implementation scope, validation notes, and build-artifact deletion candidates.

## Validation Notes

Validation executed for this version scope:

- Compile gate:
  - `javac *.java` in `aes256-java/` passed.
- Library-mode acceptance harness:
  - `TEST-01` small text round-trip passed.
  - `TEST-02` large text round-trip (1 KiB, 1 MiB) passed.
  - `TEST-03` 128 MiB file stream round-trip passed.
  - `TEST-04` tamper detection path passed (`AEADBadTagException`).
  - `TEST-05` wrong-passphrase rejection path passed (`AEADBadTagException`).
  - `TEST-06` overwrite refusal path passed with required message prefix.
  - Summary: `ACCEPTANCE-SUMMARY TEST-01/02/03/04/05/06 PASS`.
- Updated concept-map/implementation lint:
  - `PASS TEST-12 concept-map-implementation-lint`.

## Risk and Follow-Up Notes

- `CryptoOperation` passphrase is consumed per operation; callers must set passphrase again before subsequent operations on the same object.
- CLI-friendly error mapping is still pending in PHASE-03 (`Main` not yet implemented).
- SelfTest integration for these wrappers remains planned for `MILESTONE-2.3`.

## Deletion Assessment

Build artifacts detected and recorded as deletion candidates in changelog:

- `aes256-java/*.class` outputs.

Per project policy, no files were deleted in this release cycle.

## Next-Step Recommendations

1. Execute `build aes256-java/ PHASE 02 MILESTONE 2.3` to add `SelfTest` and run both entry points.
2. Wire PHASE-03 CLI menus to `TextCipher` and `FileCipher` with friendly user-facing error mapping.
3. Keep OOP concept-map consistency synchronized as README/Main artifacts land in later milestones.


