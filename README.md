# SOTA Peak Finder - Android App

[![Android](https://img.shields.io/badge/Platform-Android-3DDC84?logo=android&logoColor=white)](https://developer.android.com/)
[![API 21+](https://img.shields.io/badge/API-21%2B-brightgreen)](https://developer.android.com/about/versions/lollipop)
[![TWA](https://img.shields.io/badge/Type-Trusted%20Web%20Activity-4285F4?logo=googlechrome&logoColor=white)](https://developer.chrome.com/docs/android/trusted-web-activity)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Gradle](https://img.shields.io/badge/Gradle-8.12-02303A?logo=gradle&logoColor=white)](https://gradle.org/)

Android [Trusted Web Activity (TWA)](https://developer.chrome.com/docs/android/trusted-web-activity) wrapper for [SOTA Peak Finder](https://matsubo.github.io/sota-peak-finder/).

This app packages the SOTA Peak Finder web application as a native Android app using TWA, providing a full-screen, app-like experience without a browser toolbar.

## Prerequisites

- [Android Studio](https://developer.android.com/studio) (or Android SDK command-line tools)
- JDK 17+
- Android SDK 34

## Project Structure

```
├── app/
│   ├── build.gradle.kts          # App-level build config
│   ├── proguard-rules.pro        # ProGuard rules
│   └── src/main/
│       ├── AndroidManifest.xml   # TWA activity & intent filters
│       └── res/                  # Icons, splash, colors, styles
├── build.gradle.kts              # Root build config
├── settings.gradle.kts           # Project settings
├── gradle.properties             # Gradle properties
└── twa-manifest.json             # Bubblewrap TWA manifest
```

## Build

### Debug build

```bash
./gradlew assembleDebug
```

The APK will be at `app/build/outputs/apk/debug/app-debug.apk`.

### Release build

1. Generate a signing keystore (first time only):

```bash
keytool -genkey -v -keystore sota-peak-finder.keystore -alias sota-peak-finder -keyalg RSA -keysize 2048 -validity 10000
```

2. Create `key.properties` in the project root:

```properties
storePassword=<your-store-password>
keyPassword=<your-key-password>
keyAlias=sota-peak-finder
storeFile=../sota-peak-finder.keystore
```

3. Build the release APK or AAB:

```bash
# APK
./gradlew assembleRelease

# AAB (Android App Bundle, required for Google Play)
./gradlew bundleRelease
```

Outputs:
- APK: `app/build/outputs/apk/release/app-release.apk`
- AAB: `app/build/outputs/bundle/release/app-release.aab`

## Digital Asset Links

For the TWA to display without a browser toolbar, you must set up [Digital Asset Links](https://developers.google.com/digital-asset-links) verification.

1. Get your signing certificate fingerprint:

```bash
keytool -list -v -keystore sota-peak-finder.keystore -alias sota-peak-finder | grep SHA256
```

2. Host an `assetlinks.json` file at `https://matsubo.github.io/.well-known/assetlinks.json`:

```json
[{
  "relation": ["delegate_permission/common.handle_all_urls"],
  "target": {
    "namespace": "android_app",
    "package_name": "com.teraren.sotapeakfinder",
    "sha256_cert_fingerprints": ["<YOUR_SHA256_FINGERPRINT>"]
  }
}]
```

## Publish to Google Play

1. Build a signed release AAB:

```bash
./gradlew bundleRelease
```

2. Go to [Google Play Console](https://play.google.com/console/).

3. Create a new app or select an existing one.

4. Navigate to **Release** > **Production** (or Testing track).

5. Upload `app/build/outputs/bundle/release/app-release.aab`.

6. Fill in the store listing (title, description, screenshots, etc.) and submit for review.

### Version bumping

Before publishing an update, increment the version in `app/build.gradle.kts`:

```kotlin
versionCode = 2          // Increment for each upload
versionName = "1.1.0"    // User-facing version
```

## License

MIT
