package com.primestrider.kotlinstarter.core.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Locale

class FormattersTest {
    @Test
    fun usd_in_us_locale() = assertEquals("$9.99", formatCurrency(9.99, "USD", Locale.US))

    @Test
    fun rupiah_in_indonesian_locale() {
        val text = formatCurrency(1500.0, "IDR", Locale.forLanguageTag("in-ID"))
        assertTrue(text, text.contains("Rp"))
        assertTrue(text, text.contains("1.500"))
    }

    @Test
    fun fractional_percent_keeps_one_decimal() = assertEquals("12.6%", formatPercent(0.1258, Locale.US))

    @Test
    fun whole_percent_has_no_decimals() = assertEquals("10%", formatPercent(0.1, Locale.US))
}
