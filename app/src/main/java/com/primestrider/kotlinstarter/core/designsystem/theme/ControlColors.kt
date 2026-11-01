package com.primestrider.kotlinstarter.core.designsystem.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.unit.dp

/*
 * Colours of form controls (text fields, checkbox, radio, switch, segmented, slider). They are derived
 * from the semantic roles, never from the palette, so swapping the brand colour restyles every control.
 */

/** Resting fill of a field or an unselected control. Differs from both `background` and `card`. */
val AppColors.controlFill: Color get() = secondary

/** Fill of the field being typed in: a light tint of `primary` (a deep one in dark theme). */
val AppColors.controlFocusFill: Color get() = primary.copy(alpha = 0.12f).compositeOver(background)

/** Fill of a field in error: a light tint of `destructive`. */
val AppColors.controlErrorFill: Color get() = destructive.copy(alpha = 0.10f).compositeOver(background)

/** Duration of control colour and position changes (focus tint, switch thumb, segment pill). */
const val CONTROL_ANIMATION_MS = 180

object ControlShapes {
    val field: Shape = RoundedCornerShape(Radii.xl)
    val checkbox: Shape = RoundedCornerShape(7.dp)
    val pill: Shape = RoundedCornerShape(Radii.full)
}
