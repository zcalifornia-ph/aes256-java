# Changelog

Status: pre-alpha (v0.2.0). Interfaces, commands, and packaging may change before the first stable release.

## v0.2.0

### Added or Changed
- Reconciled root documentation via `docs.task` against current codebase behavior and public docs constraints.
- Bumped public versioning references from `v0.1.3` to `v0.2.0` in `README.md` and release docs.
- Added `docs/version-0.2.0-docs.md` with the milestone summary, validation evidence, and follow-up scope.
- Preserved contributor policy docs (`CODE_OF_CONDUCT.md`, `CONTRIBUTING.md`, `SECURITY.md`) unchanged because no governance-policy updates were required.
- Sanitized deletion notes to avoid exposing ignored internal artifact paths.

### Validation Notes
- `javac *.java` from `aes256-java/` passed.
- `java Main --selftest` passed with summary `SELFTEST SUMMARY passed=6 failed=0`.
- Menu-path selftest invocation (`5`, then `0`) passed and reported `selftest exit code=0`.

### For Deletion
- Local Java compile artifacts generated during validation runs should be cleaned before packaging.

## v0.1.3

### Added or Changed
- Added Unit 02 / Bolt 2.3 in-program assertion artifacts:
  - `aes256-java/SelfTest.java`
  - `aes256-java/Main.java`
- Implemented `SelfTest` pass/fail scenario runner with process-friendly exit-code policy (`0` all pass, non-zero otherwise).
- Implemented dual selftest invocation paths required by `TEST-10`:
  - `java Main --selftest`
  - menu option `5` from interactive `Main`.
- Updated `README.md` version/status/features/usage/roadmap sections to reflect `v0.1.3`.
- Added `docs/version-0.1.3-docs.md` with detailed implementation and validation evidence.

### Validation Notes
- `javac *.java` from `aes256-java/` passed.
- `java Main --selftest` passed with summary `SELFTEST SUMMARY passed=6 failed=0`.
- Menu-path selftest invocation (`5`, then `0`) passed and reported `selftest exit code=0`.
- Optional large selftest path passed:
  - `java Main --selftest-large` with `PASS TEST-03 round-trip 128MiB`.

### For Deletion
- Local Java compile artifacts generated during validation runs should be cleaned before packaging.

## v0.1.2

### Added or Changed
- Implemented Unit 02 / Bolt 2.2 behavior bodies in:
  - `aes256-java/CryptoOperation.java`
  - `aes256-java/TextCipher.java`
  - `aes256-java/FileCipher.java`
- Added secure consume-and-clear passphrase lifecycle in `CryptoOperation` for per-operation hygiene.
- Implemented `TextCipher` Base64 wrappers for text encrypt/decrypt over the Unit-01 byte-array engine APIs.
- Implemented `FileCipher` stream wrappers for file encrypt/decrypt with default naming policy (`.enc` / strip `.enc` else `.dec`) and overwrite refusal checks.
- Updated `README.md` version/status/features/usage/roadmap sections to reflect `v0.1.2`.
- Added `docs/version-0.1.2-docs.md` with detailed implementation and validation evidence.

### Validation Notes
- `javac *.java` from `aes256-java/` passed.
- Library-mode acceptance harness passed:
  - `ACCEPTANCE-SUMMARY TEST-01/02/03/04/05/06 PASS`
- Updated Unit-02 concept-map implementation lint passed:
  - `PASS TEST-12 concept-map-implementation-lint`

### For Deletion
- Local Java compile artifacts generated during validation runs should be cleaned before packaging.

## v0.1.1

### Added or Changed
- Added Unit 02 / Bolt 2.1 OOP abstraction skeleton classes:
  - `aes256-java/CryptoOperation.java`
  - `aes256-java/TextCipher.java`
  - `aes256-java/FileCipher.java`
- Updated `README.md` version/status/features/roadmap sections to reflect `v0.1.1` and the completed hierarchy scaffold milestone.
- Added `docs/version-0.1.1-docs.md` with implementation rationale, validation evidence, and follow-up scope for Bolt 2.2.

### Validation Notes
- `javac *.java` from `aes256-java/` passed.
- `TEST-12 concept-map-lint` passed, confirming source anchors for encapsulation, inheritance, method overloading, and method overriding.

### For Deletion
- Local Java compile artifacts generated during validation runs should be cleaned before packaging.

## v0.1.0

### Added or Changed
- Extended `aes256-java/AesGcmEngine.java` with Unit 01 / Bolt 1.3 stream APIs:
  - `void encrypt(InputStream in, OutputStream out, char[] passphrase)`
  - `void decrypt(InputStream in, OutputStream out, char[] passphrase)`
- Implemented bounded-memory chunked stream framing for stream paths:
  - header: `salt(16) || streamIv(12)`
  - records: `length(4) || ciphertext || tag(16)` repeated per chunk
  - plaintext chunk size: 64 KiB per record
- Updated `README.md` version/status/roadmap sections to reflect the `v0.1.0` pre-alpha milestone and current API surface.
- Added `docs/version-0.1.0-docs.md` with release notes and validation evidence.
- Reconciled historical wording in `CHANGELOG.md` to avoid exposing ignored/internal workflow paths in public-facing documentation.

### Validation Notes
- `TEST-03` (stream round-trip 128 MiB) passed.
- `TEST-07` (stream bounded-memory 1 GiB target under `-Xmx512m`) passed.

### For Deletion
- None from this task context (no tracked build artifacts were produced for this update).

## v0.0.4

### Added or Changed
- Extended `aes256-java/AesGcmEngine.java` for Unit 01 / Bolt 1.2 with byte-array AES-GCM APIs:
  - `byte[] encrypt(byte[] plaintext, char[] passphrase)`
  - `byte[] decrypt(byte[] envelope, char[] passphrase)`
- Implemented binary envelope policy `salt(16) || iv(12) || ciphertext || tag(16)` using `AES/GCM/NoPadding`, random salt/IV generation per encryption call, and authenticated decryption via GCM tag validation.
- Added input validation for null/short envelope cases and preserved passphrase-wiping behavior on validation failure paths.
- Updated `README.md` version markers from `v0.0.3` to `v0.0.4` and refreshed feature/usage/roadmap text to reflect BOLT-1.2 completion.
- Added `docs/version-0.0.4-docs.md` with detailed version notes, test evidence summary, and next-step guidance.

### For Deletion
- None from this task context (no tracked build artifacts were produced for this update).

## v0.0.3

### Added or Changed
- Added `aes256-java/AesGcmEngine.java` implementing the Unit 01 / Bolt 1.1 PBKDF2 key-derivation baseline:
  - `PBKDF2WithHmacSHA256`
  - `210000` iterations
  - `16`-byte salt validation
  - `256`-bit AES key derivation
  - passphrase and temporary key-material wiping in method-finalization path
- Updated `README.md` version marker from `v0.0.2` to `v0.0.3`.
- Updated `README.md` status/quick-start/library sections to reflect the current implemented baseline (`AesGcmEngine`) and near-term scope boundaries.
- Added `docs/version-0.0.3-docs.md` with detailed release notes and validation evidence context.

### For Deletion
- None from this task context (no tracked build artifacts were produced for this update).

## v0.0.2

### Added or Changed
- Finalized repository licensing as Apache License 2.0 by adding root `LICENSE.txt`.
- Updated `THIRD-PARTY-NOTICES.md` to replace proprietary wording with Apache 2.0-aligned repository and third-party notice language.
- Updated `README.md` License section to point to `LICENSE.txt` and `THIRD-PARTY-NOTICES.md`.
- Replaced the collapsible README table of contents with an always-visible table of contents for faster navigation.
- Updated the README license badge to a static Apache 2.0 badge so the visible badge label is not blocked by delayed remote license detection.
- Bumped `README.md` version markers from `v0.0.1` to `v0.0.2`.

### For Deletion
- None from this task context (documentation and licensing updates only; no build artifacts generated by this update).

## v0.0.1

### Added or Changed
- Initialized the `aes256-java` repository with project-level governance and documentation.
- Added root `README.md` describing the project scope, dual CLI/library usage model, prerequisites, roadmap, and contributor entry points.
- Added `CODE_OF_CONDUCT.md`, `CONTRIBUTING.md`, and `SECURITY.md` adapted from the repository templates with project-specific contacts.
- Added this `CHANGELOG.md` to track changes from the initial version onward.
- Updated `.gitignore` to exclude internal workflow/tooling artifacts and to add conservative Java build/IDE/OS ignore patterns appropriate for a zero-dependency Java CLI and library project.

### For Deletion
- None from this task context (documentation and configuration updates only; no new build artifacts generated by this update).
