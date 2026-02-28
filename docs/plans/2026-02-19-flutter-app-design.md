# SOTA Peak Finder - Flutter Android App Design

## Overview

Wrap the existing SOTA Peak Finder PWA (https://matsubo.github.io/sota-peak-finder/) in a Flutter WebView app for Android release on Google Play Store. Future versions will add native push notifications and offline map downloads.

## Approach

**WebView + Native Features** using `flutter_inappwebview` package.

- MVP (v1.0): Full-screen WebView displaying the existing PWA
- v2: Push notifications (proximity alerts for nearby summits)
- v3: Offline map tile downloads

## Architecture

```
Flutter App Shell
├── flutter_inappwebview (full-screen)
│   └── Existing PWA (React 19 + SQLite WASM)
│       URL: https://matsubo.github.io/sota-peak-finder/
└── Native layer (future)
    ├── Background GPS [v2]
    ├── Push notifications [v2]
    └── Offline map downloads [v3]
```

## App Identity

- **Package name:** `com.teraren.sotapeakfinder`
- **App name:** SOTA Peak Finder
- **Min SDK:** 21 (Android 5.0)
- **Target SDK:** 34 (Android 14)

## Project Structure

```
sota-peak-finder-app/
├── android/
├── ios/                       # Future
├── lib/
│   ├── main.dart
│   ├── app.dart
│   └── screens/
│       └── webview_screen.dart
├── assets/
│   └── icons/
├── pubspec.yaml
├── analysis_options.yaml
└── README.md
```

## Dependencies

- `flutter_inappwebview: ^6.x` - WebView engine with Service Worker support
- `permission_handler: ^11.x` - Location permission handling
- `flutter_launcher_icons` (dev) - App icon generation
- `flutter_native_splash` (dev) - Splash screen generation

## MVP Implementation Details

### WebView Configuration
- Service Workers enabled (preserves PWA offline functionality)
- JavaScript enabled
- GPS permission delegation to native
- Zoom disabled (PWA handles its own responsive layout)

### Location Permission
- Handle `onGeolocationPermissionsShowPrompt` for native permission dialog
- Request `ACCESS_FINE_LOCATION` via Android manifest

### Navigation
- External links (outside `matsubo.github.io`) open in system browser
- Android back button navigates WebView history
- No native navigation bar (PWA has its own)

### Splash Screen
- Native splash screen on app launch
- Dismiss when WebView finishes loading

### App Icon
- Reuse existing PWA icon (`../sota-peak-finder/public/icon-512.png`)
- Generate Adaptive Icons via `flutter_launcher_icons`

## Build & Release

### Signing
- Generate release keystore manually
- Store signing config in `android/key.properties` (gitignored)

### Build Command
```bash
flutter build appbundle
```

### Play Store
- Category: Tools or Travel & Local
- Languages: Japanese, English
- Screenshots and description to be created separately

## Future Roadmap

### v2: Push Notifications
- Firebase Cloud Messaging integration
- Proximity-based alerts when near SOTA summits
- Activation zone entry notifications

### v3: Offline Maps
- Download map tiles for specified areas
- Native tile storage and serving
- Integration with WebView map component via JavaScript bridge
