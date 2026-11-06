package com.primestrider.kotlinstarter.feature.example.gestures

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
