package com.primestrider.kotlinstarter.feature.example.components

import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertRangeInfoEquals
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
class DisplayScreenTest {
    @get:Rule
    val rule = createComposeRule()

    @Before
    fun setUp() {
        rule.setContent { AppTheme(darkTheme = false) { DisplayScreen(onBack = {}) } }
    }

    @Test
    fun progress_is_clamped() {
        val bar = rule.onNodeWithTag(PROGRESS_BAR_TAG).performScrollTo()
        repeat(10) { rule.onNodeWithText("−10%").performClick() }
        bar.assertRangeInfoEquals(ProgressBarRangeInfo(0f, 0f..1f))
        repeat(11) { rule.onNodeWithText("+10%").performClick() }
        bar.assertRangeInfoEquals(ProgressBarRangeInfo(1f, 0f..1f))
    }

    @Test
    fun long_headline_keeps_trailing_switch_visible() {
        rule.onNodeWithText(LONG_HEADLINE).performScrollTo().assertIsDisplayed()
        rule.onNodeWithTag(LONG_ROW_SWITCH_TAG).assertIsDisplayed()
    }

    @Test
    fun closing_banner_removes_it() {
        rule.onNodeWithText("Heads up").performScrollTo()
        rule.onNodeWithContentDescription("Dismiss Heads up").performClick()
        rule.onNodeWithText("Heads up").assertDoesNotExist()
    }

    @Test
    fun badge_caps_at_99_plus() {
        rule.onNodeWithText("99+").performScrollTo().assertIsDisplayed()
    }
}
