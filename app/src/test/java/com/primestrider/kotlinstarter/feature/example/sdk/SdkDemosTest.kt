package com.primestrider.kotlinstarter.feature.example.sdk

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.primestrider.kotlinstarter.core.designsystem.theme.AppTheme
import com.primestrider.kotlinstarter.feature.example.SdkDemoRoute
import com.primestrider.kotlinstarter.feature.example.exampleSections
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [36])
class SdkDemosTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun ids_are_unique() {
        assertEquals(sdkDemos.size, sdkDemos.map { it.id }.toSet().size)
    }

    @Test
    fun ids_are_kebab_case() {
        val kebab = Regex("[a-z0-9]+(-[a-z0-9]+)*")
        sdkDemos.forEach { assertTrue("${it.id} is not kebab-case", kebab.matches(it.id)) }
    }

    @Test
    fun registry_holds_demos_in_reference_order() {
        assertEquals(
            listOf(
                "clipboard",
                "device",
                "share",
                "sqlite",
                "files",
                "app-info",
                "live-photo",
                "blob",
                "status-bar",
            ),
            sdkDemos.map {
                it.id
            },
        )
    }

    @Test
    fun home_section_sits_between_features_and_styling() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val titles = exampleSections.map { context.getString(it.title) }
        assertEquals(listOf("Features", "Device APIs", "Styling"), titles)
        val sdk = exampleSections[1]
        assertEquals(sdkDemos.map { SdkDemoRoute(it.id) }, sdk.entries.map { it.route })
    }

    @Test
    fun unknown_id_shows_the_unknown_state_with_back() {
        var backs = 0
        rule.setContent { AppTheme(false) { SdkDemoScreen(id = "nope", onBack = { backs++ }) } }
        rule.onNodeWithText("This demo does not exist").assertIsDisplayed()
        rule.onNodeWithContentDescription("Back").performClick()
        assertEquals(1, backs)
    }
}
