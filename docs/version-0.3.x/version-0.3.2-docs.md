# Version 0.3.2 Documentation

## Title
CLI Presentation Polish and Console Output Handling

## Quick Diagnostic Read

`v0.3.2` is a focused CLI-experience release.

This version:

- gives the interactive shell a branded header and clearer action framing,
- keeps action results visible until the user explicitly returns to the menu,
- improves output rendering setup so the CLI can print its banner and text more consistently across console and non-console runs.

## One-Sentence Objective

Ship a cleaner, easier-to-demo interactive CLI without changing the underlying AES-GCM encryption and decryption behavior.

## Scope and Versioning

- Previous documented version: `v0.3.1`
- Current documented version: `v0.3.2`
- Release type: pre-alpha patch milestone for CLI presentation and usability polish

## File-Level Change Summary

1. `aes256-java/Main.java`

- Added branded CLI constants for the header, subtitle, divider, and fallback clear behavior.
- Added `configureConsoleEncoding()` plus charset detection so `System.out` and `System.err` prefer the console charset and otherwise fall back to UTF-8.
- Added screen helpers:
  - `printCliHeader()`
  - `beginActionScreen(String title)`
  - `clearConsole()`
  - `tryNativeClear()`
  - `promptEnterToContinue(Scanner scanner)`
- Updated menu behavior so the interactive flow:
  - clears before repainting the menu when possible,
  - opens each action on a dedicated screen,
  - pauses after each action result before returning to the menu,
  - prints a clearer exit message on quit.
- Expanded the About screen body to include project authorship credits.

2. `README.md`

- Bumped visible version and status references from `v0.3.1` to `v0.3.2`.
- Added a feature bullet for the CLI presentation polish.
- Updated the CLI usage section to describe the new bannered screen behavior and return-to-menu prompt.
- Replaced the older plain-text menu transcript with a transcript that matches the current interactive shape.
- Updated the roadmap:
  - marked `v0.3.2` complete,
  - moved packaging guidance and sample-project work to `v0.3.3`.

3. `CHANGELOG.md`

- Bumped the public status line to `v0.3.2`.
- Added a dedicated `v0.3.2` entry with release notes, validation notes, and deletion candidates.

4. `docs/version-0.3.2-docs.md`

- Added this release companion note.

5. Governance docs reviewed

- `CODE_OF_CONDUCT.md`, `CONTRIBUTING.md`, and `SECURITY.md` were reviewed and left unchanged because this release does not change contributor expectations, conduct rules, or security-reporting process.

## Behavior Notes

- The encryption/decryption primitives and file/text command wiring are unchanged in this release.
- The main user-visible differences are in presentation and interaction flow.
- Console clearing is best-effort:
  - native clear is attempted first,
  - ANSI clear is attempted when a console exists but native clear is unavailable,
  - spacer-line fallback is used when no console is attached.
- The smoke-test route still runs through `SelfTest.runDefault(System.out)`; only the menu wording and surrounding screen flow changed.

## Validation Notes

Validation executed for this version scope:

- Compile gate:
  - `javac *.java` in `aes256-java/` passed.
- Help path:
  - `java Main --help` passed and printed the branded header plus usage text.
- Selftest path:
  - `java Main --selftest` passed with summary `SELFTEST SUMMARY passed=6 failed=0`.
- Scripted menu checks:
  - option `5` then Enter then `0` exercised the smoke-test route and return-to-menu prompt,
  - option `6` then Enter then `0` exercised the About screen and clean exit path.

## Traceability and Scope Notes

- This version is a public-surface refinement release centered on `Main.java`.
- No cryptographic algorithm parameters, envelope formats, or wrapper contracts were changed.
- No hidden internal files were added to the tracked change set.

## Deletion Assessment

Build artifacts listed as deletion candidates in `CHANGELOG.md`:

- `aes256-java/*.class`
- `aes256-java/*$*.class`

Per project policy, no files were deleted in this release cycle.

## Next-Step Recommendations

1. Execute the next packaging-focused milestone (`v0.3.3`) for archive guidance, sample-project usage, and fresh-extract validation.
2. Keep future CLI transcript examples synchronized with the actual `Main.java` menu and about/help screens.

