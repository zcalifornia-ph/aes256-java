<!-- Adapted from Best-README-Template. Reference-style links live at the bottom of this file. -->
<a id="readme-top"></a>



<!-- PROJECT SHIELDS -->
<div align="center">

[![Contributors][contributors-shield]][contributors-url]
[![Forks][forks-shield]][forks-url]
[![Stargazers][stars-shield]][stars-url]
[![Issues][issues-shield]][issues-url]
[![License][license-shield]][license-url]
[![LinkedIn][linkedin-shield]][linkedin-url]

</div>

<!-- ABOUT THE PROJECT -->
##
[![aes256-java Screen Shot][product-screenshot]](https://github.com/zcalifornia-ph/aes256-java)
##



<div align="center">
<h3 align="center">aes256-java</h3>

  <p align="center">
    <strong>Lightweight, zero-dependency AES-256 encryption for Java. Built with clean OOP design; encapsulation, inheritance, overloading, and overriding.</strong>
    <br />
    Version: v0.0.4
    <br />
    Status: early development (Unit 01 / Bolt 1.2 complete: byte-array AES-GCM encrypt/decrypt landed).
    <br />
    <a href="https://github.com/zcalifornia-ph/aes256-java"><strong>Explore the docs »</strong></a>
    <br />
    <br />
    <a href="https://github.com/zcalifornia-ph/aes256-java">View Demo</a>
    &middot;
    <a href="https://github.com/zcalifornia-ph/aes256-java/issues/new?labels=bug&template=bug-report---.md">Report Bug</a>
    &middot;
    <a href="https://github.com/zcalifornia-ph/aes256-java/issues/new?labels=enhancement&template=feature-request---.md">Request Feature</a>
  </p>
</div>



<!-- TABLE OF CONTENTS -->
## Table of Contents

1. [About The Project](#about-the-project)
   - [Features](#features)
   - [What aes256-java Is Not](#what-aes256-java-is-not)
   - [Built With](#built-with)
2. [Getting Started](#getting-started)
   - [Prerequisites](#prerequisites)
   - [Quick Start](#quick-start)
3. [Usage](#usage)
   - [CLI Mode](#cli-mode)
   - [Library Mode](#library-mode)
4. [Security Notes](#security-notes)
5. [Roadmap](#roadmap)
6. [Contributing](#contributing)
7. [License](#license)
8. [Third-Party Notices](THIRD-PARTY-NOTICES.md)
9. [Contact](#contact)
10. [Acknowledgments](#acknowledgments)

<p align="right">(<a href="#readme-top">back to top</a>)</p>



## About The Project

aes256-java is a lightweight, zero-dependency AES-256 encryption toolkit for Java. It ships as both a command-line tool for encrypting plaintext and files, and as an embeddable library you can drop into your own Java projects. The codebase is built with clean object-oriented design, demonstrating encapsulation, inheritance, method overloading, and method overriding.

### Features

- PBKDF2-HMAC-SHA256 key-derivation baseline (`AesGcmEngine`, Bolt 1.1).
- Byte-array AES-256/GCM encrypt/decrypt API (`AesGcmEngine`, Bolt 1.2).
- AES-256 encryption and decryption for plaintext input.
- AES-256 encryption and decryption for files.
- Dual-mode usage: standalone CLI or embeddable library.
- Zero external dependencies; pure Java on top of `javax.crypto`.
- Clean OOP architecture suitable for learning, extending, or integrating.

### What aes256-java Is Not

- Not a vetted production cryptography library. See [Security Notes](#security-notes).
- Not a key-management or secrets-storage system.
- Not a replacement for authenticated encryption libraries such as Tink or Bouncy Castle in regulated or high-assurance contexts.

### Built With

* [![Java][Java]][Java-url]

<p align="right">(<a href="#readme-top">back to top</a>)</p>



<!-- GETTING STARTED -->
## Getting Started

Status: early development (v0.0.4). Interfaces and command shapes may change before the first stable release.

### Prerequisites

- A Java Development Kit (JDK 17 or later recommended; any modern JDK that provides `javax.crypto` should work).
- Ability to run `java` and `javac` from your shell.

Verify your toolchain:

```sh
java -version
javac -version
```

### Quick Start

1. Clone the repo.
   ```sh
   git clone https://github.com/zcalifornia-ph/aes256-java.git
   cd aes256-java
   ```
2. Review the project layout and planned entry points once sources land under the project root.
3. Compile the current crypto-engine baseline:
   ```sh
   cd aes256-java
   javac AesGcmEngine.java
   ```
4. Full interactive CLI and streaming file encrypt/decrypt flows land in upcoming Bolts. Track updates in [CHANGELOG.md](CHANGELOG.md).

<p align="right">(<a href="#readme-top">back to top</a>)</p>



<!-- USAGE EXAMPLES -->
## Usage

The project exposes two intended usage modes. Concrete command and API signatures are being introduced incrementally as Bolts land.

### CLI Mode

Planned shape:

```text
aes256-java encrypt --text  "hello"        --password "..."
aes256-java encrypt --file  path/to/input  --password "..." --out path/to/output.enc
aes256-java decrypt --file  path/to/input  --password "..." --out path/to/output
```

### Library Mode

Current baseline (`v0.0.4`):

```java
// Current implemented primitives in aes256-java/AesGcmEngine.java:
// - PBKDF2WithHmacSHA256 key derivation (210000 iterations, 16-byte salt, 256-bit key)
// - AES/GCM/NoPadding byte-array encrypt/decrypt with envelope:
//   salt(16) || iv(12) || ciphertext || tag(16)
AesGcmEngine engine = new AesGcmEngine();
char[] passphrase = "secret".toCharArray();
byte[] envelope = engine.encrypt(plaintext, passphrase);
byte[] recovered = engine.decrypt(envelope, "secret".toCharArray());
```

<p align="right">(<a href="#readme-top">back to top</a>)</p>



## Security Notes

This is an educational implementation built as a learning exercise in applied cryptography and object-oriented design. For production systems, prefer:

- Java's built-in `javax.crypto` primitives paired with a vetted key derivation function such as PBKDF2 or Argon2.
- Authenticated encryption modes (for example AES-GCM) from a reviewed cryptographic library.
- Proper key storage (OS keychain, KMS, HSM) rather than passwords embedded in source or configuration.

Do not use this library to protect information with real regulatory, legal, financial, or safety consequences without independent security review.

Report suspected vulnerabilities through the process in [SECURITY.md](SECURITY.md). Do not open public issues for security reports.

<p align="right">(<a href="#readme-top">back to top</a>)</p>



<!-- ROADMAP -->
## Roadmap

- [ ] v0.0.x - Project scaffolding, governance docs, and core OOP skeleton (BOLT-1.1 and BOLT-1.2 complete).
- [ ] v0.1.x - Plaintext encryption and decryption implementation with unit tests.
- [ ] v0.2.x - File encryption and decryption implementation with integration tests.
- [ ] v0.3.x - CLI entry point, argument parsing, and usability polish.
- [ ] v0.4.x - Library packaging guidance, sample projects, and API stabilization.
- [ ] v1.0.0 - Public stable release with documented API and acceptance tests.

See the [open issues](https://github.com/zcalifornia-ph/aes256-java/issues) for proposed features and known gaps.

<p align="right">(<a href="#readme-top">back to top</a>)</p>



<!-- CONTRIBUTING -->
## Contributing

Contributions are welcome, especially around OOP clarity, test coverage, and documentation for learners.
See [CONTRIBUTING.md](CONTRIBUTING.md), [CODE_OF_CONDUCT.md](CODE_OF_CONDUCT.md), and [SECURITY.md](SECURITY.md) for process, behavior, and vulnerability reporting.

1. Fork the project.
2. Create your feature branch (`git checkout -b feat/your-feature`).
3. Commit your changes (`git commit -m 'feat: add some feature'`).
4. Push to your branch (`git push origin feat/your-feature`).
5. Open a pull request.

<p align="right">(<a href="#readme-top">back to top</a>)</p>

### Top contributors:

<a href="https://github.com/zcalifornia-ph/aes256-java/graphs/contributors">
  <img src="https://contrib.rocks/image?repo=zcalifornia-ph/aes256-java" alt="contrib.rocks image" />
</a>



<!-- LICENSE -->
## License

This project is licensed under the Apache License 2.0.
See [LICENSE.txt](LICENSE.txt) for the full license text and
[THIRD-PARTY-NOTICES.md](THIRD-PARTY-NOTICES.md) for third-party and adaptation
notices.

<p align="right">(<a href="#readme-top">back to top</a>)</p>



<!-- CONTACT -->
## Contact

Maintainers:

- Jayrad P. Adeva - [@Shinranation](https://github.com/Shinranation) - jpadeva@up.edu.ph
- Zildjian E. California - [@zcalifornia-ph](https://github.com/zcalifornia-ph) - zecalifornia@up.edu.ph
- Rey Marvin C. Rizal - [@marverickdev](https://github.com/marverickdev) - rcrizal@up.edu.ph

Project Link: [https://github.com/zcalifornia-ph/aes256-java](https://github.com/zcalifornia-ph/aes256-java)

<p align="right">(<a href="#readme-top">back to top</a>)</p>



<!-- ACKNOWLEDGMENTS -->
## Acknowledgments

- Built by Adeva, California, and Rizal.
- The Java Cryptography Architecture (JCA) and `javax.crypto` maintainers, whose primitives make a zero-dependency AES implementation practical.
- Open-source educators whose OOP and applied-crypto material informed the design of this project.

<p align="right">(<a href="#readme-top">back to top</a>)</p>



<!-- MARKDOWN LINKS & IMAGES -->
[contributors-shield]: https://img.shields.io/github/contributors/zcalifornia-ph/aes256-java.svg?style=for-the-badge
[contributors-url]: https://github.com/zcalifornia-ph/aes256-java/graphs/contributors
[forks-shield]: https://img.shields.io/github/forks/zcalifornia-ph/aes256-java.svg?style=for-the-badge
[forks-url]: https://github.com/zcalifornia-ph/aes256-java/network/members
[stars-shield]: https://img.shields.io/github/stars/zcalifornia-ph/aes256-java.svg?style=for-the-badge
[stars-url]: https://github.com/zcalifornia-ph/aes256-java/stargazers
[issues-shield]: https://img.shields.io/github/issues/zcalifornia-ph/aes256-java.svg?style=for-the-badge
[issues-url]: https://github.com/zcalifornia-ph/aes256-java/issues
[license-shield]: https://img.shields.io/badge/License-Apache%202.0-blue.svg?style=for-the-badge
[license-url]: https://github.com/zcalifornia-ph/aes256-java/blob/main/LICENSE.txt
[linkedin-shield]: https://img.shields.io/badge/-LinkedIn-black.svg?style=for-the-badge&logo=linkedin&colorB=555
[linkedin-url]: https://linkedin.com/in/zcalifornia
[product-screenshot]: repo/images/project_screen.png
[Java]: https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white
[Java-url]: https://www.java.com/
