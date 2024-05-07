package com.vivek.currencyconverter.data.remote

import com.vivek.currencyconverter.data.remote.response.GetLatestCurrenciesResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("latest.json")
    suspend fun getLatestCurrencies(
        @Query("app_id") appId: String = "ebba4e133b304407becaea593ca82e32",
    ): GetLatestCurrenciesResponse


    @GET("currencies.json")
    suspend fun getCurrencies(
        @Query("app_id") appId: String ="ebba4e133b304407becaea593ca82e32",
    ): Map<String, String>

}
