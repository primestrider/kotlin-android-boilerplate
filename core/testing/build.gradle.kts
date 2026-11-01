plugins {
    id("kotlinstarter.android.library")
    id("kotlinstarter.android.compose")
}

android {
    namespace = "com.primestrider.kotlinstarter.core.testing"
}

dependencies {
    api(libs.androidx.datastore.preferences)
    api(libs.kotlinx.coroutines.test)
}
