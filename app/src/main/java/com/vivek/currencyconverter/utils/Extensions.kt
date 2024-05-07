package com.vivek.currencyconverter.utils

/**
 *
    To round off the decimal to 2 decimal places
 */
fun Double.roundOffDecimal(): Double {
    return String.format("%.2f", this).toDouble()
}