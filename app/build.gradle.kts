import java.util.Properties

plugins {
    id("kotlinstarter.android.application")
    id("kotlinstarter.android.compose")
    id("kotlinstarter.hilt")
    alias(libs.plugins.kotlin.serialization)
}

val localProperties: Properties =
    Properties().apply {
        rootProject
            .file("local.properties")
            .takeIf { it.exists() }
            ?.reader()
            ?.use { load(it) }
    }

// API base URL: local.properties `api.url`, else env API_URL, else DummyJSON (keep the trailing slash).
val apiUrl: String =
    localProperties
        .getProperty("api.url")
        ?.takeIf { it.isNotBlank() }
        ?: System.getenv("API_URL")?.takeIf { it.isNotBlank() }
        ?: "https://dummyjson.com/"

// Deep link scheme (`<scheme>://example/...`): local.properties `app.scheme`, else env APP_SCHEME, else "kotlinstarter".
val appScheme: String =
    localProperties
        .getProperty("app.scheme")
        ?.takeIf { it.isNotBlank() }
        ?: System.getenv("APP_SCHEME")?.takeIf { it.isNotBlank() }
        ?: "kotlinstarter"

android {
    namespace = "com.primestrider.kotlinstarter"

    defaultConfig {
        applicationId = "com.primestrider.kotlinstarter"
        versionCode = 1
        versionName = "1.0"
        manifestPlaceholders["appScheme"] = appScheme
        buildConfigField("String", "APP_SCHEME", "\"$appScheme\"")
        buildConfigField("String", "API_URL", "\"${apiUrl.replace("\\", "\\\\").replace("\"", "\\\"")}\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }
    androidResources {
        generateLocaleConfig = true
    }
    buildFeatures {
        buildConfig = true
    }
    sourceSets {
        // MigrationTestHelper reads the exported schemas as assets. Robolectric unit tests only see the variant's merged
        // assets, so they are added to debug (never release).
        getByName("debug").assets.directories.add("$projectDir/schemas")
    }
}

// Room writes each database version's schema here; commit it so migrations can be tested against it.
ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
}

dependencies {
    implementation(projects.core.common)
    implementation(projects.core.model)
    implementation(projects.core.datastore)
    implementation(projects.core.designsystem)
    testImplementation(projects.core.testing)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.splashscreen)

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.navigation.compose)

    implementation(libs.hilt.navigation.compose)

    implementation(libs.androidx.datastore.preferences)
    implementation(libs.kotlinx.serialization.json)

    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.kotlinx.serialization)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging.interceptor)
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)
    implementation(libs.androidx.paging.compose)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)
    implementation(libs.play.review.ktx)
    implementation(libs.androidx.exifinterface)
    implementation(libs.androidx.browser)
    implementation(libs.androidx.lifecycle.process)
    implementation(libs.play.services.location)
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.view)
    implementation(libs.mlkit.barcode.scanning)
    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.androidx.media3.ui)
    implementation(libs.androidx.biometric)
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.androidx.hilt.work)
    ksp(libs.androidx.hilt.compiler)
    implementation(libs.zxing.core)

    testImplementation(libs.okhttp.mockwebserver3)
    testImplementation(libs.androidx.room.testing)
    testImplementation(libs.androidx.work.testing)
}
