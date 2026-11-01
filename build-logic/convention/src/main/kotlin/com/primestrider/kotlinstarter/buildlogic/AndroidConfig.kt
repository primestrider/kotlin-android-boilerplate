package com.primestrider.kotlinstarter.buildlogic

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

/** SDK levels and JVM target shared by every Android module. */
object AndroidConfig {
    const val COMPILE_SDK = 37
    const val MIN_SDK = 24
    const val TARGET_SDK = 37
    val JAVA_VERSION = JavaVersion.VERSION_17
}

val Project.libs: VersionCatalog
    get() = extensions.getByType<VersionCatalogsExtension>().named("libs")

fun VersionCatalog.lib(alias: String) = findLibrary(alias).get()

fun Project.configureAndroidApplication(extension: ApplicationExtension) {
    extension.apply {
        compileSdk = AndroidConfig.COMPILE_SDK
        defaultConfig {
            minSdk = AndroidConfig.MIN_SDK
            targetSdk = AndroidConfig.TARGET_SDK
        }
        compileOptions {
            sourceCompatibility = AndroidConfig.JAVA_VERSION
            targetCompatibility = AndroidConfig.JAVA_VERSION
        }
        testOptions.unitTests.robolectric()
    }
    configureKotlinAndTests()
}

fun Project.configureAndroidLibrary(extension: LibraryExtension) {
    extension.apply {
        compileSdk = AndroidConfig.COMPILE_SDK
        defaultConfig {
            minSdk = AndroidConfig.MIN_SDK
        }
        compileOptions {
            sourceCompatibility = AndroidConfig.JAVA_VERSION
            targetCompatibility = AndroidConfig.JAVA_VERSION
        }
        testOptions.unitTests.robolectric()
    }
    configureKotlinAndTests()
}

/** Robolectric needs merged resources, and its SDK 36 shadows reach into JDK internals. */
private fun com.android.build.api.dsl.UnitTestOptions.robolectric() {
    isIncludeAndroidResources = true
    all { it.jvmArgs("--add-opens=java.base/jdk.internal.access=ALL-UNNAMED") }
}

private fun Project.configureKotlinAndTests() {
    tasks.withType<KotlinCompile>().configureEach {
        compilerOptions.jvmTarget.set(JvmTarget.JVM_17)
    }
    pluginManager.apply("org.jlleitschuh.gradle.ktlint")
    dependencies {
        add("testImplementation", libs.lib("junit"))
        add("testImplementation", libs.lib("robolectric"))
        add("testImplementation", libs.lib("androidx-test-core"))
        add("testImplementation", libs.lib("androidx-test-ext-junit"))
        add("testImplementation", libs.lib("kotlinx-coroutines-test"))
        add("testImplementation", libs.lib("turbine"))
    }
}
