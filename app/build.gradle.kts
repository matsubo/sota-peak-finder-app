import java.util.Properties
import java.io.FileInputStream

val keystoreProperties = Properties()
val keystorePropertiesFile = rootProject.file("key.properties")
if (keystorePropertiesFile.exists()) {
    keystoreProperties.load(FileInputStream(keystorePropertiesFile))
}

plugins {
    id("com.android.application")
}

android {
    namespace = "com.teraren.sotapeakfinder"
    compileSdk = 34

    signingConfigs {
        create("release") {
            if (keystorePropertiesFile.exists()) {
                keyAlias = keystoreProperties["keyAlias"] as String
                keyPassword = keystoreProperties["keyPassword"] as String
                storeFile = file(keystoreProperties["storeFile"] as String)
                storePassword = keystoreProperties["storePassword"] as String
            }
        }
    }

    defaultConfig {
        applicationId = "com.teraren.sotapeakfinder"
        minSdk = 21
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"

        // TWA metadata
        manifestPlaceholders["hostName"] = "matsubo.github.io"
        manifestPlaceholders["defaultUrl"] = "https://matsubo.github.io/sota-peak-finder/"
        manifestPlaceholders["launcherName"] = "SOTA Peak Finder"
        manifestPlaceholders["assetStatements"] = """[{"relation": ["delegate_permission/common.handle_all_urls"], "target": {"namespace": "web", "site": "https://matsubo.github.io"}}]"""
    }

    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

configurations.all {
    resolutionStrategy {
        force("org.jetbrains.kotlin:kotlin-stdlib:1.8.22")
        force("org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.8.22")
        force("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.8.22")
    }
}

dependencies {
    implementation("com.google.androidbrowserhelper:androidbrowserhelper:2.5.0")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("androidx.core:core:1.13.1")
}
