plugins {
    id("kotlinstarter.feature.impl")
}

android {
    namespace = "com.primestrider.kotlinstarter.feature.products.impl"
}

dependencies {
    api(projects.feature.products.api)
    implementation(projects.feature.catalog.api)
    implementation(projects.core.network)
    implementation(libs.androidx.paging.compose)
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)
    testImplementation(libs.okhttp.mockwebserver3)
    testImplementation(libs.retrofit.converter.kotlinx.serialization)
}
