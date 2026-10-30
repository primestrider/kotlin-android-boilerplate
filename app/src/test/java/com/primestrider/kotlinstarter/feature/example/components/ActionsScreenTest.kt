package com.primestrider.kotlinstarter.feature.example.components

import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.primestrider.kotlinstarter.core.designsystem.theme.AppTheme
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [36])
class ActionsScreenTest {
    @get:Rule
    val rule = createComposeRule()

    @Before
    fun setUp() {
        rule.setContent { AppTheme(darkTheme = false) { ActionsScreen(onBack = {}) } }
    }

    @Test
    fun shows_title() {
        rule.onNodeWithText("Actions").assertExists()
    }

    @Test
    fun loading_button_runs_once_on_double_tap() {
        rule.mainClock.autoAdvance = false
        val button = rule.onNodeWithTag(LOADING_BUTTON_TAG).performScrollTo()
        button.performClick()
        rule.mainClock.advanceTimeByFrame()
        button.performClick()
        rule.mainClock.advanceTimeBy(LOADING_DURATION_MS + 100)
        rule.onNodeWithText("Ran 1 time(s)").assertExists()
        button.assertIsEnabled()
    }

    @Test
    fun filter_chip_toggles() {
        val chip = rule.onNodeWithText("Favorites").performScrollTo()
        chip.performClick()
        chip.assertIsSelected()
        chip.performClick()
        chip.assertIsNotSelected()
    }

    @Test
    fun input_chip_remove() {
        rule.onNodeWithContentDescription("Remove Kotlin").performScrollTo().performClick()
        rule.onNodeWithText("Kotlin").assertDoesNotExist()
    }
}
