package com.primestrider.kotlinstarter.feature.example

import androidx.compose.material3.Surface
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.primestrider.kotlinstarter.core.designsystem.theme.AppTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [36])
class HomeScreenTest {
    @get:Rule
    val rule = createComposeRule()

    private fun show(onOpen: (Any) -> Unit) =
        rule.setContent {
            AppTheme(darkTheme = false) {
                Surface { HomeScreen(sections = exampleSections, onOpen = onOpen) }
            }
        }

    @Test
    fun rendersSectionTitleAndSettingsEntry() {
        show {}
        rule.onNodeWithText("Features").assertIsDisplayed()
        rule.onNodeWithText("Settings").assertIsDisplayed()
    }

    @Test
    fun clickingSettingsOpensSettingsRoute() {
        var opened: Any? = null
        show { opened = it }
        rule.onNodeWithText("Settings").performClick()
        assertEquals(SettingsRoute, opened)
    }

    @Test
    @Config(sdk = [36], fontScale = 2.0f)
    fun settingsEntryReachableAtLargeFontScale() {
        show {}
        rule.onNodeWithTag(HOME_LIST_TAG).performScrollToNode(hasText("Settings"))
        rule.onNodeWithText("Settings").assertIsDisplayed()
    }
}
