package com.vivek.currencyconverter.utils

import org.junit.Test


class CoreUnitTests {

    @Test
    fun roundOffDecimalTest() {
        val value = 1.234567
        val result = value.roundOffDecimal()
        assert(result == 1.23)
    }

    @Test
    fun shouldRefreshTest() {
        val lastRefreshTime = 1000L
        val currentTime = System.currentTimeMillis()
        val result = shouldRefresh(currentTime, lastRefreshTime, 30)
        assert(result)
    }
}