# PrimeLauncher Mobile

Android companion app for the PrimeLauncher ecosystem.

## Current status

Early development scaffold (version 0.1.0). The app currently includes:
- Native Android UI built with Kotlin and Jetpack Compose.
- Dark Prime-themed home screen.
- Bottom navigation for Home, Ranking, News, and Profile.
- GitHub Actions workflow that builds and uploads a debug APK.

The Ranking, News, Profile, authentication, and PC synchronization screens are placeholders until the shared Prime backend/API is connected. No live multiplayer, account, or ranking data is claimed at this stage.

## Requirements

- Android Studio with Android SDK 35
- JDK 17
- Gradle 8.9
- Android 8.0 (API 26) or newer

## Build

Open the project in Android Studio and sync Gradle, then run the `app` configuration on an Android device or emulator.

For a command-line debug build:

```bash
gradle --no-daemon assembleDebug
```

APK output:

```
app/build/outputs/apk/debug/app-debug.apk
```

Every push to `main` and every pull request targeting `main` triggers the Android build workflow. The resulting APK is available in the workflow run's Artifacts section.

## Package

`com.prime.launcher`
