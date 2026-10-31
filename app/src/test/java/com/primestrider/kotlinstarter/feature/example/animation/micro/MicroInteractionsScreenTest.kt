package com.primestrider.kotlinstarter.feature.example.animation.micro

import androidx.compose.ui.test.junit4.StateRestorationTester
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.primestrider.kotlinstarter.core.designsystem.theme.AppTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [36])
class MicroInteractionsScreenTest {
    @get:Rule
    val rule = createComposeRule()

    private fun click(description: String) {
        rule.onNodeWithContentDescription(description).performScrollTo().performClick()
    }

    @Test
    fun like_toggles_its_description() {
        rule.setContent { AppTheme(false) { MicroInteractionsScreen(onBack = {}) } }
        click("Like")
        rule.onNodeWithContentDescription("Liked").performScrollTo().assertExists()
        click("Liked")
        rule.onNodeWithContentDescription("Like").performScrollTo().assertExists()
    }

    @Test
    fun counter_is_exact_after_rapid_taps() {
        rule.setContent { AppTheme(false) { MicroInteractionsScreen(onBack = {}) } }
        repeat(5) { click("Increase") }
        repeat(2) { click("Decrease") }
        rule.onNodeWithText("3").performScrollTo().assertExists()
    }

    @Test
    fun content_change_cycles_pending_paid_shipped() {
        rule.setContent { AppTheme(false) { MicroInteractionsScreen(onBack = {}) } }
        rule.onNodeWithText("Pending").performScrollTo().assertExists()
        click("Next status")
        rule.onNodeWithText("Paid").performScrollTo().assertExists()
        click("Next status")
        rule.onNodeWithText("Shipped").performScrollTo().assertExists()
        click("Next status")
        rule.onNodeWithText("Pending").performScrollTo().assertExists()
    }

    @Test
    fun state_survives_recreation() {
        val tester = StateRestorationTester(rule)
        tester.setContent { AppTheme(false) { MicroInteractionsScreen(onBack = {}) } }
        click("Like")
        repeat(4) { click("Increase") }
        click("Next status")
        tester.emulateSavedInstanceStateRestore()
        rule.onNodeWithContentDescription("Liked").performScrollTo().assertExists()
        rule.onNodeWithText("4").performScrollTo().assertExists()
        rule.onNodeWithText("Paid").performScrollTo().assertExists()
    }
}
