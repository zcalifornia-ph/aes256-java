# Changelog

Status: stable (v1.0.0). The documented CLI entrypoints and library surface define the supported `1.x` baseline.

## v1.0.0

### Added or Changed
- Promoted the public documentation set from `v0.3.3` to `v1.0.0` and aligned the visible release/status markers in `README.md`.
- Documented the supported stable `1.x` baseline for the current CLI entrypoints, `SelfTest` runner, `AesGcmEngine`, `TextCipher`, and `FileCipher` public surfaces.
- Updated `SECURITY.md` to support the latest stable `1.x` line instead of the older `0.x` pre-release line.
- Sanitized remaining ignored/internal-path references in `README.md`, `THIRD-PARTY-NOTICES.md`, and historical versioned docs under `docs/`.
- Added `docs/version-1.0.0-docs.md` with the stable-release reconciliation notes and validation evidence.
- Reviewed `CODE_OF_CONDUCT.md`, `CONTRIBUTING.md`, and `LICENSE.txt`; no policy or license changes were required for this version.

### Validation Notes
- `javac *.java` from `aes256-java/` passed.
- `java Main --help` from `aes256-java/` passed.
- `java Main --selftest` from `aes256-java/` passed with `SELFTEST SUMMARY passed=6 failed=0`.

### For Deletion
- Generated Java class artifacts from local validation runs:
  - `aes256-java/*.class`
  - `aes256-java/*$*.class`

## v0.3.3

### Added or Changed
- Completed Unit-04 / BOLT-4.2 submission packaging for the flat-directory PE04 deliverable:
  - added `aes256-java/README.md` as the grader-facing submission guide,
  - added `aes256-java/rubric-self-check.md` with row-by-row rubric mapping sourced from the local course rubric baseline,
  - built `aes256-java/Adeva_California_Rizal_PE04.zip`,
  - published `aes256-java/sha256.txt` for the archive checksum.
- Updated root `README.md` version/status/features/usage/roadmap sections from `v0.3.2` to `v0.3.3`.
- Added `docs/version-0.3.3-docs.md` with the full packaging notes, archive contents, and fresh-extract validation evidence.
- Reviewed `CODE_OF_CONDUCT.md`, `CONTRIBUTING.md`, and `SECURITY.md`; no policy or process changes were required for this version.

### Validation Notes
- Archive filename check passed: `Adeva_California_Rizal_PE04.zip`.
- Archive listing check passed; the zip contains only:
  - `AesGcmEngine.java`
  - `CryptoOperation.java`
  - `FileCipher.java`
  - `Main.java`
  - `README.md`
  - `SelfTest.java`
  - `TextCipher.java`
- Fresh-extract validation passed:
  - `javac *.java`
  - `java Main`
  - `java Main --selftest` with `SELFTEST SUMMARY passed=6 failed=0`
  - menu option `5` route plus return-to-menu prompt
- Published SHA-256:
  - `8b47782dc7d2dd9596196b33c80c14c6b7d90a24d8c77a9be757a0c9918842ae`

### For Deletion
- Generated Java class artifacts from local validation runs:
  - `aes256-java/*.class`
  - `aes256-java/*$*.class`

## v0.3.2

### Added or Changed
- Polished `aes256-java/Main.java` interactive CLI presentation:
  - added a branded header/subtitle shell for help, menu, and action screens,
  - introduced action-specific screen transitions with return-to-menu pauses,
  - refreshed menu/about/quit wording for a cleaner interactive flow.
- Added console-output initialization in `aes256-java/Main.java` so CLI output prefers the attached console charset and falls back to UTF-8 when no console charset is available.
- Updated `README.md` version/status/features/usage/roadmap sections from `v0.3.1` to `v0.3.2`.
- Added `docs/version-0.3.2-docs.md` with the full implementation notes, behavioral context, and validation evidence for this release.
- Reviewed `CODE_OF_CONDUCT.md`, `CONTRIBUTING.md`, and `SECURITY.md`; no policy or process changes were required for this version.

### Validation Notes
- `javac *.java` from `aes256-java/` passed.
- `java Main --help` passed and printed the branded CLI header plus usage text.
- `java Main --selftest` passed with summary `SELFTEST SUMMARY passed=6 failed=0`.
- Scripted menu-path checks passed for:
  - option `5` smoke-test route plus return-to-menu flow,
  - option `6` about/credits route plus clean quit path.

### For Deletion
- Generated Java class artifacts from local validation runs:
  - `aes256-java/*.class`
  - `aes256-java/*$*.class`

## v0.3.1

### Added or Changed
- Completed Unit-04 / BOLT-4.1 public release surfaces for OOP documentation clarity:
  - synchronized the OOP concept map between `README.md` and the top-of-file `Main.java` comment block,
  - added missing public-member Javadocs for wrapper descriptor overrides in `aes256-java/TextCipher.java` and `aes256-java/FileCipher.java`.
- Updated `README.md` version/status markers from `v0.3.0` to `v0.3.1`.
- Updated `README.md` roadmap to mark `v0.3.1` complete and move packaging guidance to `v0.3.2`.
- Added `docs/version-0.3.1-docs.md` with full change notes, traceability context, and validation evidence.

### Validation Notes
- `javac *.java` from `aes256-java/` passed.
- `TEST-12` concept-map consistency check passed across README and `Main.java` source anchors.
- Public-member Javadoc coverage check passed for current source set.

### For Deletion
- Generated Java class artifacts from local validation runs:
  - `aes256-java/*.class`
  - `aes256-java/*$*.class`

## v0.3.0

### Added or Changed
- Reconciled root documentation via `docs.task` using the current project requirements baseline for consistency checks.
- Bumped public version markers from `v0.2.2` to `v0.3.0` in `README.md`.
- Sanitized root changelog wording to avoid exposing internal or ignored workflow artifact paths.
- Added `docs/version-0.3.0-docs.md` with expanded release-context and documentation reconciliation notes.

### Validation Notes
- `javac *.java` from `aes256-java/` passed.
- `java Main --selftest` passed with summary `SELFTEST SUMMARY passed=6 failed=0`.

### For Deletion
- Generated Java class artifacts from local validation runs:
  - `aes256-java/*.class`
  - `aes256-java/*$*.class`

## v0.2.2

### Added or Changed
- Completed Unit 03 / BOLT-3.2 CLI action wiring in `aes256-java/Main.java`:
  - wired menu options `1`..`4` to `TextCipher` and `FileCipher`,
  - added friendly exception mapping for wrong passphrase/corruption (`AEADBadTagException`), missing files, and overwrite refusal,
  - added passphrase prompting strategy with masked `System.console().readPassword()` when available and explicit fallback warning in non-console runs.
- Updated internal Unit-03 lifecycle artifacts and requirements state to mark BOLT-3.2 complete.
- Updated `README.md` to `v0.2.2` with wired CLI usage and sample transcript.

### Validation Notes
- `javac *.java` from `aes256-java/` passed.
- `java Main --selftest` passed with summary `SELFTEST SUMMARY passed=6 failed=0`.
- Scripted menu evidence passed for:
  - text encrypt/decrypt success path,
  - friendly wrong-passphrase and tamper error mapping (`TEST-04`, `TEST-05`),
  - overwrite refusal (`TEST-06`),
  - passphrase fallback warning path when console is unavailable (`TEST-08`),
  - file encrypt/decrypt success plus friendly wrong-passphrase/corruption mapping.

### For Deletion
- Generated Java class artifacts from local validation runs:
  - `aes256-java/*.class`
  - `aes256-java/*$*.class`

## v0.2.1

### Added or Changed
- Completed Unit 03 / BOLT-3.1 menu scaffolding in `aes256-java/Main.java`:
  - added OOP concept map header comment in `Main.java` per requirements contract,
  - added `--help` handling with educational warning output,
  - kept `--selftest` and `--selftest-large` routing with process exit-code behavior,
  - expanded interactive menu handling with explicit option placeholders (`1`..`4`), SelfTest route (`5`), About output (`6`), and graceful EOF handling.
- Updated `README.md` version/status/features/usage/roadmap sections from `v0.2.0` to `v0.2.1`.
- Added `docs/version-0.2.1-docs.md` with full implementation notes, validation evidence, and follow-up scope.
- Reviewed `CODE_OF_CONDUCT.md`, `CONTRIBUTING.md`, and `SECURITY.md`; no policy changes were required for this version.

### Validation Notes
- `javac *.java` from `aes256-java/` passed.
- `java Main --help` passed and printed usage/options text plus educational warning.
- `java Main --selftest` passed with summary `SELFTEST SUMMARY passed=6 failed=0`.
- Menu-path selftest invocation (`5`, then `0`) passed and reported `selftest exit code=0`.
- Menu-path quit invocation (`0`) exited cleanly with `bye`.

### For Deletion
- Generated Java class artifacts from local validation runs:
  - `aes256-java/*.class`
  - `aes256-java/*$*.class`

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
