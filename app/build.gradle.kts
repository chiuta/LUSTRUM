plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "tf.trom.lustrum"
    compileSdk = 35

    defaultConfig {
        applicationId = "tf.trom.lustrum"
        minSdk = 30
        targetSdk = 35
        versionCode = 5
        versionName = "0.5.0"
    }

    // Semnare stabilă: actualizările se instalează peste versiunea veche doar dacă cheia rămâne aceeași.
    // Cheia proprie se poate da prin variabilele LUSTRUM_KEYSTORE (cale), LUSTRUM_STORE_PASS, LUSTRUM_KEY_PASS,
    // LUSTRUM_KEY_ALIAS; altfel se folosește tools/lustrum-public.jks (cheie publică, doar pentru sideload).
    signingConfigs {
        create("lustrum") {
            storeFile = file(System.getenv("LUSTRUM_KEYSTORE") ?: "../tools/lustrum-public.jks")
            storePassword = System.getenv("LUSTRUM_STORE_PASS") ?: "lustrum-public"
            keyAlias = System.getenv("LUSTRUM_KEY_ALIAS") ?: "lustrum"
            keyPassword = System.getenv("LUSTRUM_KEY_PASS") ?: "lustrum-public"
        }
    }

    testOptions { unitTests.isReturnDefaultValues = true }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("lustrum")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }
    buildFeatures { compose = true }
}

dependencies {
    testImplementation("junit:junit:4.13.2")
    implementation(platform("androidx.compose:compose-bom:2024.12.01"))
    implementation("androidx.activity:activity-compose:1.9.3")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.7")
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.exifinterface:exifinterface:1.3.7")
}
