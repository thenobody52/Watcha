# Watcha - Premium Android Media Player

[![Android Build](https://github.com/fraziym-soft/watcha/actions/workflows/build.yml/badge.svg)](https://github.com/fraziym-soft/watcha/actions/workflows/build.yml)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.2.10-blue.svg)](https://kotlinlang.org)
[![Jetpack Compose](https://img.shields.io/badge/Compose-Material%203-green.svg)](https://developer.android.com/jetpack/compose)

Watcha is a modern, feature-rich, and elegant open-source Android media player built with **Jetpack Compose**, **Material 3**, **Room Database**, and **AndroidX Media3 (ExoPlayer)**. 

Designed with a sleek **Glassmorphism** visual language, Watcha provides high-performance hardware/software video decoding, audio playback, background playback, gestures, and picture-in-picture capabilities.

---

## Developer Information

- **Developer:** Akik Forazi
- **Company:** FRAZIYM Soft
- **Package Name:** `com.fraziym.soft.watcha`
- **Application Storage:** `Android/data/com.fraziym.soft.watcha/`

---

## Key Features

- **Supported Video Formats:** MP4, MKV, AVI, FLV, WEBM, MOV, M4V, TS, MPEG
- **Supported Audio Formats:** MP3, AAC, WAV, FLAC
- **Playback & Control:**
  - Hardware & Software decoding toggle
  - Gesture controls for Volume, Brightness, and Double-Tap Seek (+10s / -10s)
  - Picture in Picture (PiP) & Background Audio playback
  - Speed controller (0.25x to 2.0x) & Sleep Timer
  - Resume playback & Watch History logging
- **Library & Storage:**
  - Automatic MediaStore library scanner with custom folder browser
  - Bookmarked Favorites, Watch Later lists, and Custom Playlists
  - Application private storage in `Android/data/com.fraziym.soft.watcha/`
- **Future AI Architecture Ready:** Prepared interfaces for AI video upscaling, frame interpolation, subtitle generation, and smart collections.

---

## Tech Stack & Architecture

- **Language:** 100% Kotlin
- **UI Framework:** Jetpack Compose with Material 3 & Custom Glassmorphic Modifiers
- **Architecture:** Clean Architecture + MVVM + Repository Pattern
- **Local Database:** Room Database with KSP
- **Media Engine:** AndroidX Media3 (ExoPlayer) & MediaSession
- **Asynchronous Execution:** Coroutines & Flow
- **Preferences:** Jetpack DataStore Preferences

---

## CI/CD Pipeline & GitHub Workflows

This repository includes a complete GitHub Actions CI/CD setup in `.github/workflows/`:

1. **`build.yml`**: On every push and pull request, GitHub Actions compiles the app on Ubuntu runners, executes unit tests, runs static analysis, and uploads Debug & Release APK artifacts.
2. **`release.yml`**: On pushing to `main`, automatically calculates semantic versioning (`v0.1.0`), creates a GitHub Release, tags the repository, and publishes the APK.
3. **`quality.yml`**: Validates Kotlin formatting, dependency security, and project integrity.

### Downloading APK Artifacts

1. Navigate to the **Actions** tab on GitHub.
2. Click on the latest workflow run.
3. Scroll down to **Artifacts** to download `watcha-debug-apk` or `watcha-release-apk`.

---

## License

Watcha is released under the [MIT License](LICENSE). Copyright © 2026 Akik Forazi / FRAZIYM Soft.
