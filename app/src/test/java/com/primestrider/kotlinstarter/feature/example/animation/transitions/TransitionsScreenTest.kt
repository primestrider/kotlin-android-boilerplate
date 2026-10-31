package com.primestrider.kotlinstarter.feature.example.animation.transitions

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isSelectable
import androidx.compose.ui.test.junit4.StateRestorationTester
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.primestrider.kotlinstarter.core.designsystem.theme.AppTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [36])
class TransitionsScreenTest {
    @get:Rule
    val rule = createAndroidComposeRule<ComponentActivity>()

    private var backs = 0

    private fun show() = rule.setContent { AppTheme(false) { TransitionsScreen(onBack = { backs++ }) } }

    private fun systemBack() {
        rule.runOnUiThread { rule.activity.onBackPressedDispatcher.onBackPressed() }
        rule.waitForIdle()
    }

    private fun open(title: String) {
        rule.onNodeWithText(title).performScrollTo().performClick()
        rule.waitForIdle()
    }

    private fun detail(title: String) = rule.onNodeWithText("$title details", substring = true)

    private fun patternChip(label: String) = rule.onNode(hasText(label) and isSelectable())

    @Test
    fun tapping_an_item_opens_its_detail_and_back_button_returns() {
        show()
        detail("Mountain Trip").assertDoesNotExist()
        open("Mountain Trip")
        detail("Mountain Trip").assertExists()
        rule.onNodeWithText("Back").performScrollTo().performClick()
        rule.waitForIdle()
        detail("Mountain Trip").assertDoesNotExist()
        rule.onNodeWithText("City Lights").assertExists()
    }

    @Test
    fun system_back_pops_the_demo_detail_before_leaving_the_page() {
        show()
        open("Mountain Trip")
        systemBack()
        detail("Mountain Trip").assertDoesNotExist()
        rule.onNodeWithText("Mountain Trip").assertExists()
        assertEquals(0, backs)
        systemBack()
        assertEquals(1, backs)
    }

    @Test
    fun system_back_during_the_open_transition_pops_only_the_detail() {
        show()
        rule.onNodeWithText("Ocean Days").performScrollTo()
        rule.mainClock.autoAdvance = false
        rule.onNodeWithText("Ocean Days").performClick()
        rule.mainClock.advanceTimeByFrame()
        rule.runOnUiThread { rule.activity.onBackPressedDispatcher.onBackPressed() }
        rule.mainClock.autoAdvance = true
        rule.waitForIdle()
        detail("Ocean Days").assertDoesNotExist()
        assertEquals(0, backs)
    }

    @Test
    fun double_tapping_an_item_opens_exactly_one_detail() {
        show()
        rule.onNodeWithText("Mountain Trip").performScrollTo()
        rule.mainClock.autoAdvance = false
        rule.onNodeWithText("Mountain Trip").performClick()
        rule.onNodeWithText("Mountain Trip").performClick()
        rule.mainClock.autoAdvance = true
        rule.waitForIdle()
        detail("Mountain Trip").assertExists()
        systemBack()
        detail("Mountain Trip").assertDoesNotExist()
        assertEquals(0, backs)
    }

    @Test
    fun every_pattern_opens_and_closes_the_detail() {
        show()
        listOf("Container transform", "Shared axis X", "Shared axis Y", "Shared axis Z", "Fade through", "Fade").forEach {
            patternChip(it).performScrollTo().performClick()
            open("City Lights")
            detail("City Lights").assertExists()
            systemBack()
            detail("City Lights").assertDoesNotExist()
        }
        assertEquals(0, backs)
    }

    @Test
    fun selected_pattern_and_open_detail_survive_state_restoration() {
        val tester = StateRestorationTester(rule)
        tester.setContent { AppTheme(false) { TransitionsScreen(onBack = { backs++ }) } }
        patternChip("Container transform").assertIsSelected()
        patternChip("Shared axis Z").performScrollTo().performClick()
        open("Mountain Trip")
        tester.emulateSavedInstanceStateRestore()
        patternChip("Shared axis Z").assertIsSelected()
        patternChip("Container transform").assertIsNotSelected()
        detail("Mountain Trip").assertExists()
        systemBack()
        detail("Mountain Trip").assertDoesNotExist()
        assertEquals(0, backs)
    }

    @Test
    fun predictive_back_preview_closes_with_system_back_before_the_page_leaves() {
        show()
        rule.onNodeWithText("Open preview").performScrollTo().performClick()
        rule.onNodeWithText("Swipe back from the edge", substring = true).assertExists()
        systemBack()
        rule.onNodeWithText("Swipe back from the edge", substring = true).assertDoesNotExist()
        assertEquals(0, backs)
    }

    @Test
    @Config(sdk = [33])
    fun predictive_back_example_shows_the_fallback_below_android_14() {
        show()
        rule.onNodeWithText("Predictive back needs Android 14.").performScrollTo().assertExists()
        rule.onNodeWithText("Open preview").performScrollTo().performClick()
        rule.onNodeWithText("Swipe back from the edge", substring = true).assertExists()
        systemBack()
        rule.onNodeWithText("Swipe back from the edge", substring = true).assertDoesNotExist()
        assertEquals(0, backs)
    }
}
