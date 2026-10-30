package com.primestrider.kotlinstarter.core.designsystem.component

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.remember
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.primestrider.kotlinstarter.core.designsystem.theme.AppTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [36])
class MessengerHostTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun shows_message_and_runs_action() {
        val messenger = Messenger()
        var actions = 0
        rule.setContent {
            val hostState = remember { SnackbarHostState() }
            AppTheme(false) { MessengerHost(messenger, hostState) }
        }
        messenger.show(
            UiMessage(
                text = UiText.Plain("Item deleted"),
                actionLabel = UiText.Plain("Undo"),
                onAction = { actions++ },
            ),
        )
        rule.onNodeWithText("Item deleted").assertExists()
        rule.onNodeWithText("Undo").performClick()
        rule.waitForIdle()
        assertEquals(1, actions)
    }
}
