package com.primestrider.kotlinstarter.feature.example.gestures

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size

/** This list with the item at [from] moved to [to]; unchanged when either index is outside the list. */
fun <T> List<T>.moved(
    from: Int,
    to: Int,
): List<T> {
    if (from !in indices || to !in indices || from == to) return this
    return toMutableList().apply { add(to, removeAt(from)) }
}

/** The resting positions of the anchored sheet on the Drag page. */
enum class SheetAnchor { Collapsed, Half, Expanded }

/** Zoom limits of the Transform page. */
const val MIN_ZOOM = 1f
const val MAX_ZOOM = 4f

/** The zoom a double tap jumps to from the fit size. */
const val DOUBLE_TAP_ZOOM = 2.5f

/**
 * [pan] limited so content of [size] scaled by [scale] (around its centre) never leaves a gap at the edges: at most
 * `size * (scale - 1) / 2` per axis, so at scale 1 there is nothing to pan.
 */
fun clampPan(
    pan: Offset,
    scale: Float,
    size: Size,
): Offset {
    val maxX = (size.width * (scale - 1) / 2).coerceAtLeast(0f)
    val maxY = (size.height * (scale - 1) / 2).coerceAtLeast(0f)
    return Offset(pan.x.coerceIn(-maxX, maxX), pan.y.coerceIn(-maxY, maxY))
}

/** Double tap: zoom in to [DOUBLE_TAP_ZOOM] from below it, otherwise back to [MIN_ZOOM]. */
fun toggleZoom(scale: Float): Float = if (scale < DOUBLE_TAP_ZOOM - 0.01f) DOUBLE_TAP_ZOOM else MIN_ZOOM
