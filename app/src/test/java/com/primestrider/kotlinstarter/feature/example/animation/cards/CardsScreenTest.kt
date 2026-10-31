package com.primestrider.kotlinstarter.feature.example.animation.cards

import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.StateRestorationTester
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeLeft
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.primestrider.kotlinstarter.core.designsystem.theme.AppTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [36])
class CardsScreenTest {
    @get:Rule
    val rule = createComposeRule()

    private fun show() = rule.setContent { AppTheme(false) { CardsScreen(onBack = {}) } }

    private fun archive(
        title: String,
        letSnackbarTimeOut: Boolean = true,
    ) {
        rule.onNodeWithContentDescription("Archive $title").performScrollTo().performClick()
        // A timed-out snackbar would otherwise sit over the next button.
        if (letSnackbarTimeOut) rule.mainClock.advanceTimeBy(11_000)
        rule.waitForIdle()
    }

    /** The title also appears in the carousel, so the swipe list is addressed by its tag. */
    private fun listCard(title: String) = rule.onNode(hasText(title) and hasAnyAncestor(hasTestTag("swipeList")))

    @Test
    fun archiving_a_card_removes_it_and_undo_brings_it_back() {
        show()
        listCard("Mountain Trip").performScrollTo().assertExists()
        archive("Mountain Trip", letSnackbarTimeOut = false)
        listCard("Mountain Trip").assertDoesNotExist()
        rule.onNodeWithText("Undo").performClick()
        listCard("Mountain Trip").performScrollTo().assertExists()
    }

    @Test
    fun swiping_a_card_archives_it() {
        show()
        listCard("Ocean Days").performScrollTo().performTouchInput { swipeLeft() }
        rule.waitForIdle()
        listCard("Ocean Days").assertDoesNotExist()
        listCard("City Lights").assertExists()
    }

    @Test
    fun archiving_every_card_shows_the_empty_state_and_reset_restores() {
        show()
        listOf("Mountain Trip", "City Lights", "Ocean Days", "Forest Walk", "Desert Road").forEach { archive(it) }
        rule.onNodeWithText("All cards archived").performScrollTo().assertExists()
        rule.onNodeWithText("Reset").performScrollTo().performClick()
        rule.onNodeWithText("All cards archived").assertDoesNotExist()
        listCard("Desert Road").performScrollTo().assertExists()
    }

    @Test
    fun expand_toggles_the_extra_text() {
        show()
        rule.onNodeWithText("Expanded details", substring = true).assertDoesNotExist()
        rule.onNodeWithText("Trip details").performScrollTo().performClick()
        rule.onNodeWithText("Expanded details", substring = true).assertExists()
        rule.onNodeWithText("Trip details").performClick()
        rule.waitForIdle()
        rule.onNodeWithText("Expanded details", substring = true).assertDoesNotExist()
    }

    @Test
    fun flip_button_shows_the_back_face() {
        show()
        rule.onNodeWithText("Flip card back").assertDoesNotExist()
        rule.onNodeWithText("Flip").performScrollTo().performClick()
        rule.waitForIdle()
        rule.onNodeWithText("Flip card back").performScrollTo().assertExists()
        rule.onNodeWithText("Flip card front").assertDoesNotExist()
    }

    @Test
    fun expanded_flipped_and_archived_state_survives_recreation() {
        val tester = StateRestorationTester(rule)
        tester.setContent { AppTheme(false) { CardsScreen(onBack = {}) } }
        rule.onNodeWithText("Trip details").performScrollTo().performClick()
        rule.onNodeWithText("Flip").performScrollTo().performClick()
        archive("City Lights")
        tester.emulateSavedInstanceStateRestore()
        rule.onNodeWithText("Expanded details", substring = true).performScrollTo().assertExists()
        rule.onNodeWithText("Flip card back").performScrollTo().assertExists()
        listCard("City Lights").assertDoesNotExist()
        listCard("Mountain Trip").performScrollTo().assertExists()
    }
}
