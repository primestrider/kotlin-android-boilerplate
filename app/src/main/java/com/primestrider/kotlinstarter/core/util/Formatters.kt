package com.primestrider.kotlinstarter.core.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.platform.LocalConfiguration
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale
import kotlin.math.abs
import kotlin.math.round

/** [amount] in [currencyCode], with the symbol, grouping and decimals of [locale]. */
fun formatCurrency(
    amount: Double,
    currencyCode: String = "USD",
    locale: Locale = Locale.getDefault(),
): String =
    NumberFormat
        .getCurrencyInstance(locale)
        .apply { currency = Currency.getInstance(currencyCode) }
        .format(amount)

/** [fraction] as a percentage (0.125 → "12.5%"): no decimals for whole percentages, otherwise one. */
fun formatPercent(
    fraction: Double,
    locale: Locale = Locale.getDefault(),
): String {
    val percent = fraction * 100
    val whole = abs(percent - round(percent)) < 1e-9
    return NumberFormat
        .getPercentInstance(locale)
        .apply { maximumFractionDigits = if (whole) 0 else 1 }
        .format(fraction)
}

/** The locale of the current app language; pass it to the formatters from composables. */
@Composable
@ReadOnlyComposable
fun currentLocale(): Locale = LocalConfiguration.current.locales[0]
