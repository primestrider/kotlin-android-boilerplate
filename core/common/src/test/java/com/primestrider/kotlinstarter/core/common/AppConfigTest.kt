package com.primestrider.kotlinstarter.core.common

import org.junit.Assert.assertEquals
import org.junit.Test

class AppConfigTest {
    @Test
    fun app_scheme_matches_build_value() {
        assertEquals("kotlinstarter", AppConfig.appScheme)
    }
}
