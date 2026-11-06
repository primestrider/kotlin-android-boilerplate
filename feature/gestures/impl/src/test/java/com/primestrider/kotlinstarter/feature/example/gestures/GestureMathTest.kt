package com.primestrider.kotlinstarter.feature.example.gestures

import org.junit.Assert.assertEquals
import org.junit.Test

class GestureMathTest {
    @Test
    fun moved_reorders_and_ignores_bad_indices() {
        val list = listOf("a", "b", "c")
        assertEquals(listOf("b", "c", "a"), list.moved(0, 2))
        assertEquals(listOf("c", "a", "b"), list.moved(2, 0))
        assertEquals(list, list.moved(5, 0))
        assertEquals(list, list.moved(0, -1))
        assertEquals(list, list.moved(1, 1))
    }
}
