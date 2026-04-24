# Version 0.3.3 Documentation

## Title
Submission Packaging, Checksum Publication, and Fresh-Extract Validation

## Quick Diagnostic Read

`v0.3.3` is the packaging and delivery release for the PE04 submission flow.

This version:

- adds the grader-facing submission README and rubric self-check inside `aes256-java/`,
- produces the flat submission archive `Adeva_California_Rizal_PE04.zip`,
- publishes the archive checksum,
- proves the bundle from a fresh extraction with compile, menu-launch, and selftest evidence.

## One-Sentence Objective

Ship a flat-directory submission package that graders can extract, compile, run, and verify without repository-only tooling or hidden internal files.

## Scope and Versioning

- Previous documented version: `v0.3.2`
- Current documented version: `v0.3.3`
- Release type: pre-alpha patch milestone for PHASE-04 / MILESTONE-4.2 submission packaging and grading evidence

## File-Level Change Summary

1. `aes256-java/README.md`

- Added a submission-specific README that stands alone inside the archive.
- Documented the compile/run commands expected after extraction.
- Included the interactive menu summary, OOP concept map, library example, and educational warning.

2. `aes256-java/rubric-self-check.md`

- Added a row-by-row rubric mapping sourced from the local course rubric baseline.
- Recorded the packaging and validation evidence that backs the self-assessment.

3. `aes256-java/Adeva_California_Rizal_PE04.zip`

- Built the flat-directory PE04 archive from an explicit allowlist.
- Ensured the archive root contains only:
  - `AesGcmEngine.java`
  - `CryptoOperation.java`
  - `FileCipher.java`
  - `Main.java`
  - `README.md`
  - `SelfTest.java`
  - `TextCipher.java`

4. `aes256-java/sha256.txt`

- Published the SHA-256 checksum for the archive:
  - `8b47782dc7d2dd9596196b33c80c14c6b7d90a24d8c77a9be757a0c9918842ae`

5. `README.md`

- Bumped the visible repo version and status from `v0.3.2` to `v0.3.3`.
- Added release-surface references to the packaged submission outputs under `aes256-java/`.
- Updated the roadmap to mark the submission packaging milestone complete.

6. `CHANGELOG.md`

- Added the `v0.3.3` release entry with archive contents, validation evidence, and deletion candidates.

7. `docs/version-0.3.3-docs.md`

- Added this release companion note.

8. Governance docs reviewed

- `CODE_OF_CONDUCT.md`, `CONTRIBUTING.md`, and `SECURITY.md` were reviewed and left unchanged because this version does not change contributor process, conduct expectations, or security-reporting rules.

## Packaging Notes

- The zip was built from an allowlisted staging directory rather than by zipping the live `aes256-java/` tree.
- That choice prevented local `.class` artifacts and ignored internal material from entering the archive.
- The archive is intentionally flat so graders can run:
  - `javac *.java`
  - `java Main`
  - `java Main --selftest`
  immediately after extraction.

## Validation Notes

Validation executed for this version scope:

- Archive filename:
  - `Adeva_California_Rizal_PE04.zip`
- Archive content audit:
  - passed with only the 6 Java source files plus submission README at archive root
- Fresh-extract compile:
  - `javac *.java` -> exit code `0`
- Fresh-extract run:
  - `java Main` -> branded header/menu shown, quit path clean
- Fresh-extract selftest:
  - `java Main --selftest` -> `SELFTEST SUMMARY passed=6 failed=0`
- Fresh-extract menu selftest route:
  - option `5` printed `selftest exit code=0`
  - return prompt `Press Enter to return to menu...` appeared as expected

## Traceability and Scope Notes

- This release closes PHASE-04 / MILESTONE-4.2 in the internal planning requirements baseline.
- No runtime crypto parameters or public Java API contracts changed in this version.
- Ignored internal planning artifacts were updated for traceability but intentionally remain outside the tracked commit per repo rules.

## Deletion Assessment

Build artifacts listed as deletion candidates in `CHANGELOG.md`:

- `aes256-java/*.class`
- `aes256-java/*$*.class`

Per project policy, no files were deleted in this release cycle.

## Next-Step Recommendations

1. Reconcile any future repo-root documentation changes against the submission-local `aes256-java/README.md` so the grader and repo narratives stay aligned.
2. If the project evolves beyond the PE submission, use the next milestone for public-release cleanup and API stabilization rather than changing the submission archive layout casually.


