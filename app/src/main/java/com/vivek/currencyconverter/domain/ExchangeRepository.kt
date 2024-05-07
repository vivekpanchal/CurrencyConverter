package com.vivek.currencyconverter.domain

import com.vivek.currencyconverter.data.local.database.CurrencyExchange
import com.vivek.currencyconverter.utils.network.Resource
import kotlinx.coroutines.flow.Flow

interface ExchangeRepository {

    //using flow fetch data
    fun getCurrencyRates() : Flow<Resource<List<CurrencyExchange>>>

    //convert currency
    suspend fun convertCurrencies(amount: Double, selectedCurrency: String, currencyRates: List<CurrencyExchange>) : Result<List<CurrencyExchange>>

}