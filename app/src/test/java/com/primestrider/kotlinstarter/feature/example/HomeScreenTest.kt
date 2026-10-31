package com.primestrider.kotlinstarter.feature.example

import androidx.compose.material3.Surface
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isHeading
import androidx.compose.ui.test.junit4.StateRestorationTester
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTextInput
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

    private fun scrollTo(text: String) = rule.onNodeWithTag(HOME_LIST_TAG).performScrollToNode(hasText(text))

    @Test
    fun rendersSectionTitleAndFormEntry() {
        show {}
        rule.onNode(hasText("Features") and isHeading()).assertIsDisplayed()
        scrollTo("Form")
        rule.onNodeWithText("Form").assertIsDisplayed()
    }

    @Test
    fun formEntryExposesButtonRole() {
        show {}
        scrollTo("Form")
        rule
            .onNode(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Button) and hasText("Form"))
            .assertIsDisplayed()
    }

    @Test
    fun clickingSettingsOpensFormExampleRoute() {
        var opened: Any? = null
        show { opened = it }
        scrollTo("Form")
        rule.onNodeWithText("Form").performClick()
        assertEquals(FormExampleRoute, opened)
    }

    @Test
    fun searching_shows_only_matching_features() {
        show {}
        rule.onNodeWithText("Search features").performTextInput("camera")
        rule.onNodeWithTag(HOME_LIST_TAG).performScrollToNode(hasText("Camera & QR Scanner"))
        rule.onNodeWithText("Camera & QR Scanner").assertIsDisplayed()
        rule.onNodeWithText("Form").assertDoesNotExist()
    }

    @Test
    fun a_search_without_matches_offers_to_clear_it() {
        show {}
        rule.onNodeWithText("Search features").performTextInput("zzzz")
        rule.onNodeWithText("No features match \"zzzz\"").assertIsDisplayed()
        rule.onNodeWithText("Clear search").performClick()
        scrollTo("Form")
        rule.onNodeWithText("Form").assertIsDisplayed()
    }

    @Test
    fun section_chip_limits_the_list_to_that_section() {
        show {}
        rule.onNode(hasText("Styling") and SemanticsMatcher.keyIsDefined(SemanticsProperties.Selected)).performClick()
        rule.onNodeWithText("Colors").assertIsDisplayed()
        rule.onNodeWithText("Form").assertDoesNotExist()
        rule.onNode(hasText("All") and SemanticsMatcher.keyIsDefined(SemanticsProperties.Selected)).performClick()
        scrollTo("Form")
        rule.onNodeWithText("Form").assertIsDisplayed()
    }

    @Test
    fun query_and_chip_survive_recreation() {
        val restore = StateRestorationTester(rule)
        restore.setContent {
            AppTheme(darkTheme = false) { Surface { HomeScreen(sections = exampleSections, onOpen = {}) } }
        }
        rule.onNode(hasText("Features") and SemanticsMatcher.keyIsDefined(SemanticsProperties.Selected)).performClick()
        rule.onNodeWithText("Search features").performTextInput("todo")
        restore.emulateSavedInstanceStateRestore()
        rule.onNodeWithText("Todos").assertIsDisplayed()
        rule.onNodeWithText("Products").assertDoesNotExist()
        rule.onNode(hasText("Features") and SemanticsMatcher.keyIsDefined(SemanticsProperties.Selected)).assertIsSelected()
    }

    @Test
    fun a_single_section_hides_search_and_chips() {
        rule.setContent {
            AppTheme(darkTheme = false) { Surface { ComponentsHomeScreen(entries = componentEntries, onOpen = {}) } }
        }
        rule.onNodeWithText("Search features").assertDoesNotExist()
        rule.onNodeWithText("All").assertDoesNotExist()
    }

    @Test
    @Config(sdk = [36], fontScale = 2.0f)
    fun settingsEntryReachableAtLargeFontScale() {
        show {}
        rule.onNodeWithTag(HOME_LIST_TAG).performScrollToNode(hasText("Form"))
        rule.onNodeWithText("Form").assertIsDisplayed()
    }
}
