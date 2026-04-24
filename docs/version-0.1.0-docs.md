# Version 0.1.0 Documentation

## Title
Pre-Alpha Milestone: Core AES-GCM Engine Surface Expanded to Stream APIs

## Quick Diagnostic Read

`v0.1.0` marks the first pre-alpha milestone where the core crypto engine exposes:

- PBKDF2 key derivation,
- byte-array encrypt/decrypt,
- stream encrypt/decrypt overloads.

This version also includes a docs reconciliation pass (`docs.task`) to align root docs with current code and remove internal-path leakage from public changelog text.

## One-Sentence Objective

Promote the project to `v0.1.0` after landing the initial end-to-end core cryptographic API surface with passing stream acceptance evidence.

## Scope and Versioning

- Previous documented version: `v0.0.4`
- Current documented version: `v0.1.0`
- Release type: pre-alpha milestone bump + documentation reconciliation

## File-Level Change Summary

1. `aes256-java/AesGcmEngine.java`

- Stream API overloads are present for encryption/decryption on `InputStream`/`OutputStream`.
- Stream handling now uses bounded-memory record framing:
  - header: `salt(16) || streamIv(12)`
  - records: `length(4) || ciphertext || tag(16)` repeated
  - record plaintext size: up to 64 KiB

2. `README.md`

- Bumped visible version markers and status to `v0.1.0` pre-alpha.
- Updated feature list and usage examples to include stream API surfaces.
- Updated roadmap to show `v0.1.0` milestone achieved and Unit 02/OOP work as the next major build target.

3. `CHANGELOG.md`

- Added `v0.1.0` entry describing stream API milestone and validation status.
- Updated changelog status line to `v0.1.0`.
- Sanitized an older historical line to avoid enumerating ignored/internal workflow paths.

## Validation Notes

Known verification state at this milestone:

- `TEST-03` (stream round-trip, 128 MiB synthetic payload): `PASS TEST-03 round-trip-128MiB elapsedSeconds=2.462 throughputMiBps=52.00`
- `TEST-07` (stream 1 GiB bounded-memory target at `-Xmx512m`): `PASS TEST-07 round-trip-1024MiB elapsedSeconds=4.561 throughputMiBps=224.53`

## docs.task Reconciliation Outcomes

- `README.md` and `CHANGELOG.md` were updated to match current code reality.
- `CODE_OF_CONDUCT.md`, `CONTRIBUTING.md`, and `SECURITY.md` were reviewed and left unchanged because no policy/process changes were required for this version bump.
- Local learning artifacts and the private requirements baseline were not present in that workspace; reconciliation was performed against available root/docs artifacts and current source reality.

## Deletion Assessment

No tracked build artifacts were introduced by this documentation/version bump context.
No deletion candidates were identified.

## Next-Step Recommendations

1. Proceed to Unit 02 Bolt work (`BOLT-2.1`) for OOP hierarchy design and implementation scaffolding.
2. Add/commit a reproducible selftest harness to make stream-path evidence easy to rerun from `Main`.
3. Continue toward CLI wiring and user-facing error mapping for subsequent milestones.
