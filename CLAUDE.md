# SOTA Peak Finder - Android App

## Project Overview

Android TWA (Trusted Web Activity) app that wraps the existing SOTA Peak Finder PWA
(https://matsubo.github.io/sota-peak-finder/) for Google Play Store distribution.

The PWA is an amateur radio tool for finding nearby SOTA (Summits On The Air) summits
worldwide using GPS, with 179,000+ summits, weather forecasts, and activation history.

## Tech Stack

- **Android TWA** using `com.google.androidbrowserhelper:androidbrowserhelper:2.5.0`
- **Gradle 8.12** with Kotlin DSL (`build.gradle.kts`)
- **No application code** — the app is a thin wrapper around the PWA
- **Web source repo:** `../sota-peak-finder/` (React 19 + TypeScript + Vite + SQLite WASM)

## App Identity

- **Package name:** `com.teraren.sotapeakfinder`
- **App name:** SOTA Peak Finder
- **Min SDK:** 21 (Android 5.0)
- **Target SDK:** 34
- **Version:** 1.0.0 (versionCode: 1)

## Project Structure

```
app/
├── build.gradle.kts           # App build config with signing
├── proguard-rules.pro         # ProGuard rules for TWA
└── src/main/
    ├── AndroidManifest.xml    # TWA launcher activity config
    └── res/
        ├── drawable/splash.png
        ├── mipmap-*/ic_launcher.png
        ├── values/{colors,strings,styles}.xml
        └── xml/filepaths.xml
build.gradle.kts               # Root build config
settings.gradle.kts            # Gradle settings
twa-manifest.json              # TWA configuration reference
```

## Build Commands

Requires `JAVA_HOME` and `ANDROID_HOME` environment variables:

```bash
export JAVA_HOME="/opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home"
export ANDROID_HOME="/opt/homebrew/share/android-commandlinetools"

# Debug build
./gradlew assembleDebug
# Output: app/build/outputs/apk/debug/app-debug.apk

# Release build (requires key.properties + keystore)
./gradlew bundleRelease
# Output: app/build/outputs/bundle/release/app-release.aab
```

## Signing

- Keystore: `sota-peak-finder.keystore` (gitignored)
- Config: `key.properties` (gitignored)
- Password: `keystore-password.txt` (gitignored)
- **CRITICAL:** Back up keystore and password. Loss prevents app updates.

## Digital Asset Links

For address bar removal in TWA, `assetlinks.json` must be deployed at:
`https://matsubo.github.io/.well-known/assetlinks.json`

The file exists in `../sota-peak-finder/public/.well-known/assetlinks.json`.

## Future Roadmap

- v2: Push notifications (Firebase Cloud Messaging, proximity alerts)
- v3: Offline map tile downloads
- iOS version (future)
