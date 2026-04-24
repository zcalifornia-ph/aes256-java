# Version 0.0.4 Documentation

## Title
PHASE 01 / MILESTONE 1.2 Completion: Byte-Array AES-GCM Encrypt/Decrypt (aes256-java)

## Quick Diagnostic Read

This version upgrades the crypto baseline from key derivation only to full byte-array encryption/decryption:

- `AesGcmEngine` now supports `encrypt(...)` and `decrypt(...)` APIs.
- AES-GCM envelope framing is implemented as `salt(16) || iv(12) || ciphertext || tag(16)`.
- Root project docs now reflect MILESTONE-1.2 completion and `v0.0.4` status.

## One-Sentence Objective

Land a verified, JDK-only byte-array AES-256/GCM path that satisfies PHASE 01 MILESTONE-1.2 requirements and unblocks streaming APIs in MILESTONE-1.3.

## Why This Version Matters

Before this update:

- the repo had deterministic PBKDF2 key derivation (`MILESTONE-1.1`) but no encryption/decryption operations,
- README usage examples were still positioned as pre-encryption baseline.

After this update:

- encryption/decryption is now executable for byte-array payloads,
- tamper and wrong-passphrase paths fail through authenticated decryption semantics,
- documentation is aligned to a real crypto operation surface.

## Scope and Versioning

- Previous documented version: `v0.0.3`
- Current documented version: `v0.0.4`
- Release type: feature expansion + docs alignment

## File-Level Change Summary

1. `aes256-java/AesGcmEngine.java`

- Added AES-GCM constants and transformation policy:
  - `GCM_IV_LENGTH_BYTES = 12`
  - `GCM_TAG_LENGTH_BITS = 128`
  - `CIPHER_TRANSFORMATION = AES/GCM/NoPadding`
- Added `encrypt(byte[] plaintext, char[] passphrase)`:
  - random `salt` and random `iv` generation per call,
  - key derivation via existing PBKDF2 seam,
  - envelope assembly using `ByteBuffer`.
- Added `decrypt(byte[] envelope, char[] passphrase)`:
  - envelope parsing with length validation,
  - authenticated decryption using GCM parameters,
  - tamper/wrong-passphrase rejection via tag verification failure.
- Added helper methods for random byte generation and passphrase wiping on validation failures.

2. `README.md`

- Bumped visible version and status to `v0.0.4`.
- Updated feature list to include byte-array AES-GCM APIs.
- Updated library-mode example to show current `encrypt/decrypt` baseline usage.
- Updated roadmap phrasing to reflect MILESTONE-1.1 + MILESTONE-1.2 completion in the `v0.0.x` phase.

3. `CHANGELOG.md`

- Added `v0.0.4` entry with implementation and docs deltas.
- Updated top status marker to `v0.0.4`.

## Verification Notes

Validation performed for this version context:

- `javac AesGcmEngine.java` succeeds.
- Acceptance harness for `TEST-01`, `TEST-02`, `TEST-04`, and `TEST-05` passed with summary:
  - `ACCEPTANCE-SUMMARY TEST-01/02/04/05 PASS`

## Deletion Assessment

No tracked build artifacts were introduced by this version context.
No deletion candidates were identified for commit scope.

## Compatibility and Risk Notes

- Runtime risk: moderate; this introduces first authenticated encryption/decryption path and should keep receiving milestone-level tests as APIs expand.
- Scope boundary: stream-based encrypt/decrypt (`InputStream`/`OutputStream`) is still pending MILESTONE-1.3.
- Repo hygiene note: internal planning artifacts under `internal planning notes` remain ignored by `.gitignore` and are not included in this tracked commit.

## Next-Step Recommendations

1. Execute `build` for `PHASE-01` `MILESTONE-1.3` to add streaming file encryption/decryption.
2. Add a tracked selftest source file in a future milestone so acceptance evidence is runnable from repository code without temporary harness creation.
3. Keep README examples synchronized with each milestone completion to avoid API/docs drift.


