plugins {
    id("kotlinstarter.android.library")
    id("kotlinstarter.android.compose")
    id("kotlinstarter.hilt")
}

android {
    namespace = "com.primestrider.kotlinstarter.core.designsystem"
}

dependencies {
    api(projects.core.model)
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)
    testImplementation(projects.core.testing)
}
