# Weekly Duty Roster

This repository contains a **Kotlin Compose Multiplatform** application for the Dunkwa FPU Weekly Duty Roster system. It runs on desktop, Android, iOS, and web platforms with shared business logic across all platforms.

## Features

- Login / Signup workflow with role-based access:
  - `Station Officer` with full edit and roster generation access
  - `Secretary` with view-only roster preview access
- Personnel registration module with add/delete actions
- Duty point management with editable PW-allowed duty points
- Weekly roster preview with a generated assignment grid
- Preview roster screen for verification before printing

## Supported Platforms

- **Desktop** (Windows, macOS, Linux) - via Compose Desktop
- **Android** - native Android app
- **iOS** - native iOS app (via Kotlin/Native)
- **Web** - browser-based version (via Kotlin/JS)

## Getting Started

### Requirements

- Java 17 or later
- Gradle
- Android SDK (for Android builds)
- Xcode (for iOS builds)

### Run Desktop Version

```bash
gradle run
```

### Build and Install Android Version

```bash
gradle assembleDebug
# Then install the APK on your device
```

### Build iOS Version

```bash
gradle iosSimulatorArm64
```

### Build Web Version

```bash
gradle wasmJsRun
```

### Build Native Distributions

```bash
gradle package
```

## Project Structure

```
src/
├── commonMain/          # Shared code for all platforms
│   ├── Models.kt        # Data models
│   ├── AppLogic.kt      # Business logic
│   └── App.kt           # Compose UI
├── desktopMain/         # Desktop entry point
├── androidMain/         # Android entry point + resources
├── iosMain/             # iOS entry point
└── wasmJsMain/          # Web entry point
```

## Notes

This Compose Multiplatform version provides a modern, responsive UI that works seamlessly across desktop, mobile, and web platforms. The shared code ensures consistent functionality and reduces maintenance overhead.

