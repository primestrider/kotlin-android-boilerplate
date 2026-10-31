package com.primestrider.kotlinstarter.feature.example.sdk.location

import java.util.Locale
import kotlin.math.roundToInt

/** [value] with 6 decimals and a dot in every locale: coordinates are data, not prose. */
fun formatCoordinate(value: Double): String = String.format(Locale.US, "%.6f", value)

/** Horizontal accuracy as "±13 m". */
fun formatAccuracy(meters: Float): String = "±${meters.roundToInt()} m"
