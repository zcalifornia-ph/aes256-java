# Contributing to aes256-java

Thanks for contributing.
This guide follows values of quality, integrity, inclusion, collaboration, service, and continuous improvement.

## Values Framework

This project aligns contributions with these working values:

- leadership through transparent, constructive collaboration
- professionalism through ethical practice and academic integrity
- expertise through strong technical and domain quality
- inquiry through evidence, testing, and open learning
- service through useful outcomes for communities and users
- diversity through inclusive and rights-respecting participation
- collaboration through interdisciplinary and cross-functional teamwork
- sustainability through maintainable and responsible engineering

## Before You Start

- Review open issues and pull requests to avoid duplicate work.
- Open an issue first for significant changes to align scope and approach.
- Keep changes focused and easy to review.
- Confirm your contribution aligns with [CODE_OF_CONDUCT.md](CODE_OF_CONDUCT.md) and [SECURITY.md](SECURITY.md).

## Contribution Standards

- Quality: include tests, validation, or clear verification steps.
- Integrity: provide accurate claims, reproducible evidence, and proper attribution.
- Inclusion: write docs and comments for broad readability and accessibility.
- Service: prioritize improvements that deliver real user and maintainer value.
- Sustainability: prefer maintainable solutions over short-lived complexity.
- Zero-dependency discipline: this project intentionally avoids third-party runtime libraries. Changes that add runtime dependencies require explicit maintainer approval.

## Development Workflow

1. Fork and create a branch from `main`.
2. Implement the change with tests and documentation updates where applicable.
3. Run project checks locally (compile with your JDK and run any included tests before opening a pull request).
4. Open a pull request containing:
   - problem statement
   - approach summary
   - validation evidence (test output, logs, screenshots)
   - risks, tradeoffs, and rollback notes when relevant

## Branch Naming

- Use kebab-case.
- Start with a Conventional Commit type.
- Keep names concise and descriptive.

Example:

```text
feat/add-file-encryption-cli
```

## Commit Conventions

Use Conventional Commits.
Examples:

```text
feat: add AES-256 file encryption
fix: correct IV handling for empty input
docs: clarify CLI usage examples
```

Common types:

- `feat`
- `fix`
- `docs`
- `refactor`
- `test`
- `chore`

## Merge Strategy

Use short-lived branches and squash before merge, unless project maintainers specify otherwise.

- Keep each pull request scoped to one logical change.
- Use a final squashed commit message that clearly describes the full change.
- Ensure branch checks pass before merge.

## Security Reporting

Do not report vulnerabilities in public issues.
Follow [SECURITY.md](SECURITY.md) for coordinated disclosure.

## Community Conduct

Participation requires compliance with [CODE_OF_CONDUCT.md](CODE_OF_CONDUCT.md).
