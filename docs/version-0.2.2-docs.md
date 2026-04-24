# Version 0.2.2 Documentation

## Title
Unit 03 / Bolt 3.2: Interactive Encrypt/Decrypt Wiring and Friendly Error Mapping

## Quick Diagnostic Read

`v0.2.2` completes Unit-03 Bolt-3.2 by replacing menu placeholders with working crypto actions in `Main`.

This version adds:

- menu option wiring for text and file encrypt/decrypt paths,
- friendly user-facing error mapping for passphrase/corruption and file-path failures,
- passphrase prompting behavior that uses masked console input when available and explicit fallback warnings otherwise.

## One-Sentence Objective

Ship a functional interactive CLI that can encrypt/decrypt text and files with safe, clear UX behavior and no user-path stack traces.

## Scope and Versioning

- Previous documented version: `v0.2.1`
- Current documented version: `v0.2.2`
- Release type: pre-alpha patch milestone for Unit-03 Bolt-3.2 implementation

## File-Level Change Summary

1. `aes256-java/Main.java`

- Wired menu options `1`..`4` to concrete handlers:
  - `encryptText(Scanner)` / `decryptText(Scanner)`
  - `encryptFile(Scanner)` / `decryptFile(Scanner)`
- Added passphrase read policy:
  - masked input through `System.console().readPassword(...)` when console is attached,
  - fallback warning plus scanner input when console is unavailable.
- Added friendly error mapping with no user-path stack traces:
  - `AEADBadTagException` -> wrong-passphrase/corruption guidance,
  - `FileNotFoundException` -> missing-file guidance,
  - `FileAlreadyExistsException` -> overwrite refusal messaging.
- Preserved existing menu/help/about/selftest routing behavior from `v0.2.1`.

2. `README.md`

- Bumped visible version/status references from `v0.2.1` to `v0.2.2`.
- Updated feature and usage sections to reflect fully wired menu options `1`..`4`.
- Added a concrete CLI transcript for non-console fallback behavior.
- Marked roadmap item `v0.2.2` as completed.

3. `CHANGELOG.md`

- Bumped status line to `v0.2.2`.
- Added a `v0.2.2` entry with implementation, validation notes, and deletion candidates.

4. `docs/version-0.2.2-docs.md`

- Added this detailed companion document for the release.

5. Governance docs reviewed

- `CODE_OF_CONDUCT.md`, `CONTRIBUTING.md`, and `SECURITY.md` were not modified because this version did not require governance-policy changes.

## Validation Notes

Validation executed for this version scope:

- Compile gate:
  - `javac *.java` in `aes256-java/` passed.
- Selftest gate:
  - `java Main --selftest` exit code `0`, summary `SELFTEST SUMMARY passed=6 failed=0`.
- Scripted menu acceptance checks passed for:
  - text encrypt/decrypt success path,
  - friendly wrong-passphrase and tamper mapping (`TEST-04`, `TEST-05`),
  - overwrite refusal (`TEST-06`),
  - fallback warning path for non-console runs (`TEST-08`),
  - file encrypt/decrypt success and friendly wrong-passphrase mapping.

## Traceability and Artifact Scope Notes

- Unit-03 design/traceability artifacts were also updated under `aes256-java/ai-dlc-docs/` during build-task execution.
- Those paths are intentionally ignored by repository `.gitignore`; commit scope here includes only tracked public docs and source files.

## Deletion Assessment

Build artifacts noted as deletion candidates in `CHANGELOG.md`:

- `aes256-java/*.class`
- `aes256-java/*$*.class`

Per task rules, no files were deleted in this workflow.

## Next-Step Recommendations

1. Execute Unit-04 / Bolt-4.1 to complete Javadoc and OOP concept map synchronization artifacts.
2. Keep CLI message text stable while adding final packaging checks in Unit-04.
3. Maintain versioned `docs/version-*.md` companions for each bolt-completion release.
