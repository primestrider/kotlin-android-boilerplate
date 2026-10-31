package com.primestrider.kotlinstarter.feature.example.sdk.notifications

import androidx.lifecycle.SavedStateHandle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class NotificationsViewModelTest {
    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        NotificationEvents.reset()
    }

    @After
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun six_events_keep_the_newest_five_newest_first() {
        val events = mutableListOf<String>()
        var shown = emptyList<String>()
        (1..6).forEach { shown = events.pushEvent("e$it") }
        assertEquals(listOf("e6", "e5", "e4", "e3", "e2"), shown)
        assertEquals(shown, events)
    }

    @Test
    fun fewer_than_five_events_are_all_kept() {
        val events = mutableListOf<String>()
        events.pushEvent("a")
        assertEquals(listOf("b", "a"), events.pushEvent("b"))
    }

    @Test
    fun the_view_model_keeps_events_and_selections_in_the_saved_state() {
        val handle = SavedStateHandle()
        val vm = NotificationsViewModel(handle) { 0L }
        (1..6).forEach { vm.log("e$it") }
        assertEquals(5, vm.state.value.events.size)
        assertEquals(
            "e6",
            vm.state.value.events
                .first()
                .substringAfter(" · "),
        )
        vm.setDelay(30)
        vm.setTitle("Hi")
        val restored = NotificationsViewModel(handle) { 0L }
        assertEquals(vm.state.value.events, restored.state.value.events)
        assertEquals(30, restored.state.value.delaySeconds)
        assertEquals("Hi", restored.state.value.title)
    }

    @Test
    fun a_new_screen_starts_with_the_log_of_the_previous_one() {
        val first = NotificationsViewModel(SavedStateHandle()) { 0L }
        first.log("Scheduled")
        first.onTapped(id = 3, label = "Tapped: Paid")
        val second = NotificationsViewModel(SavedStateHandle()) { 0L }
        assertEquals(first.state.value.events, second.state.value.events)
        assertEquals(2, second.state.value.events.size)
    }

    @Test
    fun a_tap_is_logged_once_per_notification() {
        val vm = NotificationsViewModel(SavedStateHandle()) { 0L }
        vm.onTapped(id = 7, label = "Tapped: Paid")
        vm.onTapped(id = 7, label = "Tapped: Paid")
        assertEquals(1, vm.state.value.events.size)
    }
}
