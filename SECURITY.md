# Security Policy

## Adaptation and Transparency Notice

This is an independent security policy for this repository.
It adapts publicly available principles from University of the Philippines (UP) documents, including the UP Quality Policy and the UP Statement of the Philosophy of Education and Graduate Attributes (approved November 28, 2019), to embody their spirit of quality, responsibility, and continuous improvement in an open-source setting.
This adaptation does not imply UP affiliation, adoption, sponsorship, or endorsement unless explicitly stated by repository maintainers.

## Scope and Intended Use

`aes256-java` is an educational AES-256 encryption project. Treat it as reference and learning material, not as a vetted production cryptography library. See the Security Notes in [README.md](README.md) for the recommended production alternatives.

Security reports for this repository should focus on:

- incorrect cryptographic behavior (for example, predictable IVs, weak key derivation, nonce reuse, misuse of modes).
- memory or resource issues reachable from public entry points (the CLI and the library API).
- documentation that could mislead learners or integrators into unsafe usage.

## Security Principles

We treat security as part of quality.
Our approach emphasizes:

- responsible stewardship of user and contributor trust
- ethical, professional, and evidence-based issue handling
- inclusive and respectful collaboration during incident response
- continuous improvement through post-incident learning

## Supported Versions

Starting with `v1.0.0`, the latest stable `1.x` version is supported for security fixes. Older `0.x` pre-release versions are no longer supported.

| Version | Supported |
| --- | --- |
| latest `1.x` | :white_check_mark: |
| `0.x` pre-release versions | :x: |

## Reporting a Vulnerability

Use coordinated disclosure.
Do not open public issues for suspected vulnerabilities.

Send reports privately to any of the maintainers listed below:

- Jayrad P. Adeva - jpadeva@up.edu.ph
- Zildjian E. California - zecalifornia@up.edu.ph
- Rey Marvin C. Rizal - rcrizal@up.edu.ph

Please include:

- affected component, version, or file path
- reproduction steps or proof of concept
- impact assessment and severity estimate
- suggested mitigation or patch (optional)

If encrypted reporting is supported in the future, key-sharing instructions will be documented in this section.

## Response Targets

- acknowledgment within 72 hours
- initial triage within 7 calendar days
- best-effort remediation plan within 30 calendar days

Complex issues may require more time; we will provide status updates during triage and remediation.

## Safe Handling and Disclosure

- We will coordinate disclosure timing with the reporter after a fix or mitigation is available.
- We will credit reporters unless anonymity is requested.
- We ask reporters to avoid privacy violations, service disruption, or data destruction during testing.
