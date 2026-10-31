package com.primestrider.kotlinstarter.feature.example.sdk.payment

import androidx.lifecycle.SavedStateHandle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import kotlin.random.Random

@OptIn(ExperimentalCoroutinesApi::class)
class PaymentCodeViewModelTest {
    private val scheduler = TestCoroutineScheduler()

    @Before
    fun setUp() = Dispatchers.setMain(StandardTestDispatcher(scheduler))

    @After
    fun tearDown() = Dispatchers.resetMain()

    private fun advance(millis: Long) {
        scheduler.advanceTimeBy(millis)
        scheduler.runCurrent()
    }

    @Test
    fun counts_down_to_one_then_refreshes_with_a_new_code() {
        val vm = PaymentCodeViewModel(SavedStateHandle(), Random(7))
        scheduler.runCurrent()
        val first = vm.state.value
        assertEquals(60, first.secondsLeft)

        advance(59_000)
        assertEquals(1, vm.state.value.secondsLeft)
        assertEquals(first.code, vm.state.value.code)

        advance(1_000)
        assertEquals(60, vm.state.value.secondsLeft)
        assertNotEquals(first.code, vm.state.value.code)
    }

    @Test
    fun amount_comes_from_the_navigation_arguments() {
        assertEquals(50_000L, PaymentCodeViewModel(SavedStateHandle(mapOf("amount" to 50_000L)), Random(1)).amount)
        assertNull(PaymentCodeViewModel(SavedStateHandle(), Random(1)).amount)
    }
}
