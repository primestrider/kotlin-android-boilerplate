import com.primestrider.kotlinstarter.buildlogic.appScheme
import com.primestrider.kotlinstarter.buildlogic.javaString

plugins {
    id("kotlinstarter.android.library")
    id("kotlinstarter.android.compose")
    id("kotlinstarter.hilt")
}

android {
    namespace = "com.primestrider.kotlinstarter.core.common"
    defaultConfig {
        buildConfigField("String", "APP_SCHEME", javaString(appScheme))
    }
    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidx.appcompat)
    testImplementation(projects.core.testing)
}
