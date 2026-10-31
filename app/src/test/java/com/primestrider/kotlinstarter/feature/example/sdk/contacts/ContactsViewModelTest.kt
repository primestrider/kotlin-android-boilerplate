package com.primestrider.kotlinstarter.feature.example.sdk.contacts

import androidx.lifecycle.SavedStateHandle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ContactsViewModelTest {
    private val scheduler = TestCoroutineScheduler()

    private class FakeSource : ContactsSource {
        val searches = mutableListOf<String>()

        override suspend fun count() = 42

        override suspend fun search(
            name: String,
            limit: Int,
        ): List<ContactRow> {
            searches += name
            return listOf(ContactRow(name = "Hit $name", phone = "0812"))
        }
    }

    @Before
    fun setUp() = Dispatchers.setMain(StandardTestDispatcher(scheduler))

    @After
    fun tearDown() = Dispatchers.resetMain()

    private fun advance(millis: Long) {
        scheduler.advanceTimeBy(millis)
        scheduler.runCurrent()
    }

    @Test
    fun blank_query_lists_without_a_filter_at_once() {
        val source = FakeSource()
        val vm = ContactsViewModel(source, SavedStateHandle())
        scheduler.runCurrent()
        assertEquals(listOf(""), source.searches)
        assertEquals(42, vm.state.value.count)
        assertEquals(listOf(ContactRow("Hit ", "0812")), vm.state.value.rows)
        assertTrue(vm.state.value.loaded)
    }

    @Test
    fun typing_within_250_ms_searches_once() {
        val source = FakeSource()
        val vm = ContactsViewModel(source, SavedStateHandle())
        scheduler.runCurrent()
        vm.onQueryChange("a")
        advance(100)
        vm.onQueryChange("an")
        advance(100)
        vm.onQueryChange("ann")
        advance(249)
        assertEquals(listOf(""), source.searches)
        advance(1)
        assertEquals(listOf("", "ann"), source.searches)
        assertEquals(
            "Hit ann",
            vm.state.value.rows
                .single()
                .name,
        )
    }

    @Test
    fun query_is_restored_from_the_saved_state_and_searched_at_once() {
        val source = FakeSource()
        val vm = ContactsViewModel(source, SavedStateHandle(mapOf("contacts_query" to "bo")))
        scheduler.runCurrent()
        assertEquals(listOf("bo"), source.searches)
        assertEquals("bo", vm.state.value.query)
    }
}
