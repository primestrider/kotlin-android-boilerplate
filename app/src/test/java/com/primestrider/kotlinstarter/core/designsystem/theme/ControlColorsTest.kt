package com.primestrider.kotlinstarter.core.designsystem.theme

import androidx.compose.ui.graphics.compositeOver
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class ControlColorsTest {
    @Test
    fun focus_tint_follows_primary() {
        val green = LightColors.copy(primary = Palette.Success.c500)
        assertNotEquals(LightColors.controlFocusFill, green.controlFocusFill)
        assertEquals(
            Palette.Primary.c500
                .copy(alpha = 0.12f)
                .compositeOver(Palette.white),
            LightColors.controlFocusFill,
        )
    }

    @Test
    fun error_tint_follows_destructive() {
        assertEquals(
            Palette.Error.c500
                .copy(alpha = 0.10f)
                .compositeOver(DarkColors.background),
            DarkColors.controlErrorFill,
        )
    }

    @Test
    fun control_fill_differs_from_background_and_card() {
        listOf(LightColors, DarkColors).forEach {
            assertNotEquals(it.background, it.controlFill)
            assertNotEquals(it.card, it.controlFill)
        }
    }
}
