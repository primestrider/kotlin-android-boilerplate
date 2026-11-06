package com.primestrider.kotlinstarter.feature.example.gestures

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
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

    @Test
    fun clamp_pan_locks_at_scale_one() {
        assertEquals(Offset.Zero, clampPan(Offset(50f, 50f), 1f, Size(100f, 100f)))
    }

    @Test
    fun clamp_pan_stops_at_edge() {
        assertEquals(Offset(50f, -50f), clampPan(Offset(500f, -500f), 2f, Size(100f, 100f)))
        assertEquals(Offset(10f, -20f), clampPan(Offset(10f, -20f), 2f, Size(100f, 100f)))
    }

    @Test
    fun toggle_zoom_alternates() {
        assertEquals(DOUBLE_TAP_ZOOM, toggleZoom(MIN_ZOOM))
        assertEquals(MIN_ZOOM, toggleZoom(DOUBLE_TAP_ZOOM))
        assertEquals(MIN_ZOOM, toggleZoom(MAX_ZOOM))
        assertEquals(2.5f, DOUBLE_TAP_ZOOM)
    }
}
