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
}

dependencies {
    implementation(projects.core.common)
    implementation(projects.core.model)
    implementation(projects.core.datastore)
    implementation(projects.core.designsystem)
    implementation(projects.core.network)
    implementation(projects.core.auth)
    implementation(projects.core.ui)

    // region example:account
    implementation(projects.feature.account.api)
    implementation(projects.feature.account.impl)
    // endregion
    // region example:animation
    implementation(projects.feature.animation.api)
    implementation(projects.feature.animation.impl)
    // endregion
    // region example:catalog
    implementation(projects.feature.catalog.api)
    implementation(projects.feature.catalog.impl)
    // endregion
    // region example:devices
    implementation(projects.feature.devices.api)
    implementation(projects.feature.devices.impl)
    // endregion
    // region example:products
    implementation(projects.feature.products.api)
    implementation(projects.feature.products.impl)
    // endregion
    // region example:settings
    implementation(projects.feature.settings.api)
    implementation(projects.feature.settings.impl)
    // endregion
    // region example:todos
    implementation(projects.feature.todos.api)
    implementation(projects.feature.todos.impl)
    // endregion
    testImplementation(projects.core.testing)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.splashscreen)

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.navigation.compose)

    implementation(libs.hilt.navigation.compose)

    implementation(libs.kotlinx.serialization.json)

    implementation(libs.androidx.lifecycle.process)
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.androidx.hilt.work)
    ksp(libs.androidx.hilt.compiler)

    testImplementation(libs.okhttp.mockwebserver3)
    testImplementation(libs.androidx.work.testing)
}
