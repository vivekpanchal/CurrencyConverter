package com.vivek.currencyconverter.data.remote.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


// fetching latest currency rates from the API
@JsonClass(generateAdapter = true)
data class GetLatestCurrenciesResponse(
    @Json(name = "base")
    val base: String,
    @Json(name = "rates")
    val rates: Map<String, Double>,
    @Json(name = "timestamp")
    val timestamp: Int
)