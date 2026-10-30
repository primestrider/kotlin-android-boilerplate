package com.primestrider.kotlinstarter.core.designsystem.component

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import com.primestrider.kotlinstarter.R
import com.primestrider.kotlinstarter.core.designsystem.theme.Radii
import com.primestrider.kotlinstarter.core.designsystem.theme.Spacing

private const val PULSE_MS = 800

@Composable
private fun pulseAlpha(): Float {
    val alpha by rememberInfiniteTransition(label = "skeleton").animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(PULSE_MS), RepeatMode.Reverse),
        label = "skeleton-alpha",
    )
    return alpha
}

/** Pulsing placeholder block; size it with [modifier]. */
@Composable
fun Skeleton(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(Radii.sm),
) {
    SkeletonBar(modifier, shape, pulseAlpha())
}

@Composable
private fun SkeletonBar(
    modifier: Modifier,
    shape: Shape,
    alpha: Float,
) {
    Box(modifier.alpha(alpha).background(MaterialTheme.colorScheme.surfaceContainerHigh, shape))
}

/** Placeholder paragraph of [lines] bars, the last one shorter; announced as "Loading". */
@Composable
fun SkeletonText(
    lines: Int = 3,
    modifier: Modifier = Modifier,
    lastLineFraction: Float = 0.6f,
) {
    val loading = stringResource(R.string.component_loading)
    // One shared pulse for all bars instead of an infinite transition per bar.
    val alpha = pulseAlpha()
    Column(
        modifier.fillMaxWidth().clearAndSetSemantics { contentDescription = loading },
        verticalArrangement = Arrangement.spacedBy(Spacing.s2),
    ) {
        repeat(lines) { index ->
            val fraction = if (index == lines - 1) lastLineFraction else 1f
            SkeletonBar(Modifier.fillMaxWidth(fraction).height(Spacing.s4), RoundedCornerShape(Radii.sm), alpha)
        }
    }
}
