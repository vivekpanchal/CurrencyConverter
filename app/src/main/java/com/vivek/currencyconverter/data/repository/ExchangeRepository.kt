package com.vivek.currencyconverter.data.repository

import com.vivek.currencyconverter.data.local.database.CurrencyExchange
import com.vivek.currencyconverter.utils.network.Resource
import kotlinx.coroutines.flow.Flow

interface ExchangeRepository {

    //using flow fetch data
    suspend fun getCurrencyRates() : Flow<Resource<List<CurrencyExchange>>>

}