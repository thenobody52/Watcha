# Project: Watcha - Memory & Context

## Project Overview
Watcha is a modern, feature-rich, open-source Android media player built with **Jetpack Compose**, **Material 3**, **Room**, and **Media3 (ExoPlayer)**.

## Architecture & Tech Stack
*   **Architecture:** Clean Architecture + MVVM + Repository Pattern.
*   **Key Libraries:** Jetpack Compose, Room (KSP), Media3, DataStore.
*   **UI:** Glassmorphism design language.

## Development Log & Context (What, Why, When)

| Date / Phase | Action / Event | Why / Context |
| :--- | :--- | :--- |
| **July 2026** | Initial Analysis | Mapped project structure to understand MVVM/Clean Architecture patterns. |
| **July 2026** | Build Attempt 1 | Attempted `./gradlew assembleDebug`. Failed due to corrupted `gradle-wrapper.jar` and lack of execution permissions on Termux. |
| **July 2026** | Build Attempt 2 | Installed `openjdk-17`/`gradle` via `pkg` to bypass wrapper. Build stalled during task graph calculation because the project requires a configured Android SDK (which is not readily installable in Termux). |
| **July 2026** | Strategy Shift | User decided to pivot to an Ubuntu environment (via `proot-distro`) within Termux to provide a standard Linux build environment with better support for Android SDK tools. |

## Current Status & Next Steps

**Status:** Awaiting Ubuntu container initialization and configuration.

**Immediate Next Steps (Upon Ubuntu Setup):**
1.  **Configure Android SDK:** Install Android SDK command-line tools *inside* the new Ubuntu environment.
2.  **Environment Variables:** Set `ANDROID_HOME` pointing to the SDK directory.
3.  **Local Configuration:** Create `local.properties` in the project root with the correct `sdk.dir` reference.
4.  **Final Build:** Execute the build command again within the Ubuntu shell.
