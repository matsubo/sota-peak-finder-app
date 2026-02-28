# SOTA Peak Finder Flutter App - Implementation Plan

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** Wrap the existing SOTA Peak Finder PWA in a Flutter WebView app and prepare it for Google Play Store release.

**Architecture:** Full-screen `flutter_inappwebview` loading `https://matsubo.github.io/sota-peak-finder/` with native GPS permission handling, external link interception, and Android back button support.

**Tech Stack:** Flutter, Dart, flutter_inappwebview 6.x, permission_handler 11.x, flutter_launcher_icons, flutter_native_splash

**Design Doc:** `docs/plans/2026-02-19-flutter-app-design.md`

---

## Prerequisites

Flutter SDK must be installed. If not:

```bash
brew install --cask flutter
flutter doctor
```

Accept Android licenses if needed:
```bash
flutter doctor --android-licenses
```

---

### Task 1: Create Flutter Project

**Files:**
- Create: Flutter project scaffold in `/Users/matsu/ghq/github.com/matsubo/sota-peak-finder-app/`

**Step 1: Create Flutter project in a temp directory and move files**

The project directory already exists with `docs/` and `.claude/`, so we create in a temp dir and move.

```bash
cd /tmp
flutter create --org com.teraren --project-name sotapeakfinder --platforms android,ios sota_peak_finder_temp
```

**Step 2: Copy Flutter files into the existing project directory**

```bash
cp -r /tmp/sota_peak_finder_temp/* /Users/matsu/ghq/github.com/matsubo/sota-peak-finder-app/
cp /tmp/sota_peak_finder_temp/.gitignore /Users/matsu/ghq/github.com/matsubo/sota-peak-finder-app/
cp /tmp/sota_peak_finder_temp/.metadata /Users/matsu/ghq/github.com/matsubo/sota-peak-finder-app/
cp -r /tmp/sota_peak_finder_temp/.dart_tool /Users/matsu/ghq/github.com/matsubo/sota-peak-finder-app/ 2>/dev/null || true
rm -rf /tmp/sota_peak_finder_temp
```

**Step 3: Verify project structure**

```bash
cd /Users/matsu/ghq/github.com/matsubo/sota-peak-finder-app
flutter pub get
flutter analyze
```

Expected: No errors.

**Step 4: Commit**

```bash
git add -A
git commit -m "chore: scaffold Flutter project with Android and iOS platforms"
```

---

### Task 2: Add Dependencies

**Files:**
- Modify: `pubspec.yaml`

**Step 1: Update pubspec.yaml dependencies**

Replace the `dependencies` and `dev_dependencies` sections in `pubspec.yaml`:

```yaml
dependencies:
  flutter:
    sdk: flutter
  flutter_inappwebview: ^6.1.5
  permission_handler: ^11.3.1
  url_launcher: ^6.3.1

dev_dependencies:
  flutter_test:
    sdk: flutter
  flutter_lints: ^5.0.0
  flutter_launcher_icons: ^0.14.3
  flutter_native_splash: ^2.4.4
```

**Step 2: Install dependencies**

```bash
cd /Users/matsu/ghq/github.com/matsubo/sota-peak-finder-app
flutter pub get
```

Expected: All packages resolve successfully.

**Step 3: Commit**

```bash
git add pubspec.yaml pubspec.lock
git commit -m "chore: add flutter_inappwebview, permission_handler, url_launcher dependencies"
```

---

### Task 3: Configure Android Manifest

**Files:**
- Modify: `android/app/src/main/AndroidManifest.xml`

**Step 1: Add required permissions and configuration**

The AndroidManifest.xml needs these additions:

1. Add permissions before `<application>` tag:

```xml
<uses-permission android:name="android.permission.INTERNET"/>
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"/>
```

2. Inside the `<application>` tag, ensure `usesCleartextTraffic` is false (HTTPS only):

```xml
<application
    android:usesCleartextTraffic="false"
    ...>
```

**Step 2: Set app label**

In AndroidManifest.xml, set:

```xml
android:label="SOTA Peak Finder"
```

**Step 3: Verify build compiles**

```bash
cd /Users/matsu/ghq/github.com/matsubo/sota-peak-finder-app
flutter build apk --debug
```

Expected: BUILD SUCCESSFUL

**Step 4: Commit**

```bash
git add android/app/src/main/AndroidManifest.xml
git commit -m "feat: configure Android permissions for internet and GPS"
```

---

### Task 4: Implement Main Entry Point

**Files:**
- Modify: `lib/main.dart`

**Step 1: Write main.dart**

Replace the contents of `lib/main.dart` with:

```dart
import 'package:flutter/material.dart';
import 'package:flutter_inappwebview/flutter_inappwebview.dart';
import 'package:sotapeakfinder/app.dart';

Future<void> main() async {
  WidgetsFlutterBinding.ensureInitialized();

  if (await InAppWebViewController.isWebViewAvailable()) {
    runApp(const SotaPeakFinderApp());
  } else {
    runApp(
      const MaterialApp(
        home: Scaffold(
          body: Center(
            child: Text('WebView is not available on this device'),
          ),
        ),
      ),
    );
  }
}
```

**Step 2: Verify no compile errors**

```bash
cd /Users/matsu/ghq/github.com/matsubo/sota-peak-finder-app
flutter analyze lib/main.dart
```

Expected: No issues found (will show error for missing app.dart - that's OK, created in next task).

**Step 3: Commit**

```bash
git add lib/main.dart
git commit -m "feat: implement main entry point with WebView availability check"
```

---

### Task 5: Implement App Widget

**Files:**
- Create: `lib/app.dart`

**Step 1: Write app.dart**

```dart
import 'package:flutter/material.dart';
import 'package:sotapeakfinder/screens/webview_screen.dart';

class SotaPeakFinderApp extends StatelessWidget {
  const SotaPeakFinderApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'SOTA Peak Finder',
      debugShowCheckedModeBanner: false,
      theme: ThemeData(
        colorScheme: ColorScheme.fromSeed(
          seedColor: const Color(0xFF0C1420),
          brightness: Brightness.dark,
        ),
        useMaterial3: true,
      ),
      home: const WebViewScreen(),
    );
  }
}
```

**Step 2: Commit**

```bash
git add lib/app.dart
git commit -m "feat: implement MaterialApp with dark theme"
```

---

### Task 6: Implement WebView Screen

**Files:**
- Create: `lib/screens/webview_screen.dart`

This is the core of the app. The WebView screen loads the PWA and handles:
- GPS permission delegation
- External link interception
- Android back button navigation
- Loading indicator

**Step 1: Create screens directory and write webview_screen.dart**

```dart
import 'package:flutter/material.dart';
import 'package:flutter_inappwebview/flutter_inappwebview.dart';
import 'package:url_launcher/url_launcher.dart';

class WebViewScreen extends StatefulWidget {
  const WebViewScreen({super.key});

  @override
  State<WebViewScreen> createState() => _WebViewScreenState();
}

class _WebViewScreenState extends State<WebViewScreen> {
  InAppWebViewController? _webViewController;
  bool _isLoading = true;
  static const String _baseUrl = 'https://matsubo.github.io/sota-peak-finder/';
  static const String _allowedHost = 'matsubo.github.io';

  @override
  Widget build(BuildContext context) {
    return PopScope(
      canPop: false,
      onPopInvokedWithResult: (didPop, result) async {
        if (didPop) return;
        if (_webViewController != null &&
            await _webViewController!.canGoBack()) {
          await _webViewController!.goBack();
        }
      },
      child: Scaffold(
        body: SafeArea(
          child: Stack(
            children: [
              InAppWebView(
                initialUrlRequest: URLRequest(url: WebUri(_baseUrl)),
                initialSettings: InAppWebViewSettings(
                  javaScriptEnabled: true,
                  domStorageEnabled: true,
                  databaseEnabled: true,
                  supportZoom: false,
                  useWideViewPort: true,
                  loadWithOverviewMode: true,
                  mixedContentMode: MixedContentMode.MIXED_CONTENT_NEVER_ALLOW,
                  allowFileAccessFromFileURLs: false,
                  allowUniversalAccessFromFileURLs: false,
                ),
                onWebViewCreated: (controller) {
                  _webViewController = controller;
                },
                onLoadStop: (controller, url) {
                  setState(() {
                    _isLoading = false;
                  });
                },
                onGeolocationPermissionsShowPrompt:
                    (controller, origin) async {
                  return GeolocationPermissionShowPromptResponse(
                    origin: origin,
                    allow: true,
                    retain: true,
                  );
                },
                shouldOverrideUrlLoading: (controller, navigationAction) async {
                  final uri = navigationAction.request.url;
                  if (uri != null && uri.host != _allowedHost) {
                    await launchUrl(
                      uri,
                      mode: LaunchMode.externalApplication,
                    );
                    return NavigationActionPolicy.CANCEL;
                  }
                  return NavigationActionPolicy.ALLOW;
                },
                onReceivedError: (controller, request, error) {
                  if (request.isForMainFrame == true) {
                    controller.loadData(
                      data: '''
                        <html>
                        <body style="display:flex;justify-content:center;align-items:center;height:100vh;margin:0;background:#0C1420;color:#FFB928;font-family:sans-serif;text-align:center;padding:20px;">
                          <div>
                            <h2>No Internet Connection</h2>
                            <p>Please check your connection and try again.</p>
                            <button onclick="location.reload()" style="padding:12px 24px;background:#FFB928;color:#0C1420;border:none;border-radius:8px;font-size:16px;cursor:pointer;">Retry</button>
                          </div>
                        </body>
                        </html>
                      ''',
                    );
                  }
                },
              ),
              if (_isLoading)
                Container(
                  color: const Color(0xFF0C1420),
                  child: const Center(
                    child: CircularProgressIndicator(
                      color: Color(0xFFFFB928),
                    ),
                  ),
                ),
            ],
          ),
        ),
      ),
    );
  }
}
```

**Step 2: Verify build**

```bash
cd /Users/matsu/ghq/github.com/matsubo/sota-peak-finder-app
flutter analyze
```

Expected: No issues found.

**Step 3: Commit**

```bash
git add lib/screens/webview_screen.dart
git commit -m "feat: implement WebView screen with GPS, external links, back navigation"
```

---

### Task 7: Write Widget Test

**Files:**
- Modify: `test/widget_test.dart`

**Step 1: Write the widget test**

Replace `test/widget_test.dart` with:

```dart
import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:sotapeakfinder/app.dart';

void main() {
  testWidgets('App renders without crashing', (WidgetTester tester) async {
    await tester.pumpWidget(const SotaPeakFinderApp());
    expect(find.byType(MaterialApp), findsOneWidget);
  });
}
```

**Step 2: Run test**

```bash
cd /Users/matsu/ghq/github.com/matsubo/sota-peak-finder-app
flutter test
```

Expected: All tests passed.

**Step 3: Commit**

```bash
git add test/widget_test.dart
git commit -m "test: add basic app widget test"
```

---

### Task 8: Set Up App Icon

**Files:**
- Create: `assets/icons/app-icon.png` (copied from existing PWA)
- Modify: `pubspec.yaml` (add flutter_launcher_icons config)

**Step 1: Copy icon from existing PWA**

```bash
mkdir -p /Users/matsu/ghq/github.com/matsubo/sota-peak-finder-app/assets/icons
cp /Users/matsu/ghq/github.com/matsubo/sota-peak-finder/public/icon-512.png /Users/matsu/ghq/github.com/matsubo/sota-peak-finder-app/assets/icons/app-icon.png
```

**Step 2: Add flutter_launcher_icons config to pubspec.yaml**

Append to the end of `pubspec.yaml`:

```yaml
flutter_launcher_icons:
  android: true
  ios: true
  image_path: "assets/icons/app-icon.png"
  adaptive_icon_background: "#0C1420"
  adaptive_icon_foreground: "assets/icons/app-icon.png"
```

**Step 3: Generate icons**

```bash
cd /Users/matsu/ghq/github.com/matsubo/sota-peak-finder-app
dart run flutter_launcher_icons
```

Expected: Icons generated for Android (mipmap folders populated).

**Step 4: Commit**

```bash
git add assets/ pubspec.yaml android/app/src/main/res/
git commit -m "feat: generate app icons from existing PWA icon"
```

---

### Task 9: Set Up Splash Screen

**Files:**
- Modify: `pubspec.yaml` (add flutter_native_splash config)

**Step 1: Add flutter_native_splash config to pubspec.yaml**

Append to the end of `pubspec.yaml`:

```yaml
flutter_native_splash:
  color: "#0C1420"
  image: "assets/icons/app-icon.png"
  android: true
  ios: true
  android_12:
    color: "#0C1420"
    image: "assets/icons/app-icon.png"
```

**Step 2: Generate splash screen**

```bash
cd /Users/matsu/ghq/github.com/matsubo/sota-peak-finder-app
dart run flutter_native_splash:create
```

Expected: Splash screen assets generated.

**Step 3: Commit**

```bash
git add pubspec.yaml android/
git commit -m "feat: add native splash screen with dark theme"
```

---

### Task 10: Configure Build Settings for Release

**Files:**
- Modify: `android/app/build.gradle.kts` (or `build.gradle`)

**Step 1: Verify Android applicationId and min/target SDK**

Check `android/app/build.gradle.kts` and ensure:

```kotlin
android {
    namespace = "com.teraren.sotapeakfinder"
    compileSdk = flutter.compileSdkVersion

    defaultConfig {
        applicationId = "com.teraren.sotapeakfinder"
        minSdk = 21
        targetSdk = flutter.targetSdkVersion
        versionCode = flutter.versionCode
        versionName = flutter.versionName
    }
}
```

If `minSdk` is higher than 21, change it to 21.

**Step 2: Add .gitignore entries for signing**

Append to `.gitignore`:

```
# Signing
android/key.properties
*.jks
*.keystore
```

**Step 3: Verify debug build**

```bash
cd /Users/matsu/ghq/github.com/matsubo/sota-peak-finder-app
flutter build apk --debug
```

Expected: BUILD SUCCESSFUL

**Step 4: Commit**

```bash
git add android/app/build.gradle.kts .gitignore
git commit -m "chore: configure Android build settings and gitignore signing files"
```

---

### Task 11: Test on Emulator or Device

**Step 1: List available devices**

```bash
flutter devices
```

**Step 2: Run the app**

```bash
cd /Users/matsu/ghq/github.com/matsubo/sota-peak-finder-app
flutter run
```

**Step 3: Manual testing checklist**

Verify each of these on the running app:

- [ ] Splash screen appears on launch with dark background and icon
- [ ] PWA loads in WebView after splash
- [ ] GPS permission dialog appears when tapping "Nearby" in the PWA
- [ ] Location-based summit search works after granting permission
- [ ] External links (e.g., sotl.as links) open in system browser
- [ ] Android back button navigates back within WebView
- [ ] Android back button at root does not crash the app
- [ ] Offline error page shows when internet is disconnected
- [ ] App icon appears correctly in launcher

**Step 4: Commit any fixes**

```bash
git add -A
git commit -m "fix: address issues found during manual testing"
```

---

### Task 12: Build Release AAB

**Step 1: Generate signing keystore (manual, interactive)**

This step must be done manually by the developer:

```bash
keytool -genkey -v -keystore ~/sota-peak-finder-release.jks -keyalg RSA -keysize 2048 -validity 10000 -alias sota-peak-finder
```

**Step 2: Create key.properties**

Create `android/key.properties`:

```properties
storePassword=<your-password>
keyPassword=<your-password>
keyAlias=sota-peak-finder
storeFile=/Users/matsu/sota-peak-finder-release.jks
```

**Step 3: Configure signing in build.gradle.kts**

Add signing config to `android/app/build.gradle.kts`:

```kotlin
import java.util.Properties
import java.io.FileInputStream

val keystoreProperties = Properties()
val keystorePropertiesFile = rootProject.file("key.properties")
if (keystorePropertiesFile.exists()) {
    keystoreProperties.load(FileInputStream(keystorePropertiesFile))
}

android {
    // ... existing config ...

    signingConfigs {
        create("release") {
            keyAlias = keystoreProperties["keyAlias"] as String
            keyPassword = keystoreProperties["keyPassword"] as String
            storeFile = file(keystoreProperties["storeFile"] as String)
            storePassword = keystoreProperties["storePassword"] as String
        }
    }

    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
        }
    }
}
```

**Step 4: Build release AAB**

```bash
cd /Users/matsu/ghq/github.com/matsubo/sota-peak-finder-app
flutter build appbundle --release
```

Expected: `build/app/outputs/bundle/release/app-release.aab` created.

**Step 5: Commit signing config (not the keystore or passwords)**

```bash
git add android/app/build.gradle.kts
git commit -m "chore: configure release signing for Google Play Store"
```

---

### Task 13: Final Verification and Tag

**Step 1: Run all tests**

```bash
cd /Users/matsu/ghq/github.com/matsubo/sota-peak-finder-app
flutter test
flutter analyze
```

Expected: All tests pass, no analysis issues.

**Step 2: Verify AAB file exists**

```bash
ls -la build/app/outputs/bundle/release/app-release.aab
```

**Step 3: Tag release**

```bash
git tag v1.0.0
```

**Step 4: Summary**

The `.aab` file at `build/app/outputs/bundle/release/app-release.aab` is ready to upload to Google Play Console.
