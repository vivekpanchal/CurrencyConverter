package com.vivek.currencyconverter.data.repository

import com.vivek.currencyconverter.data.local.database.CurrencyDao
import com.vivek.currencyconverter.data.local.database.CurrencyExchange
import com.vivek.currencyconverter.data.local.prefs.UserPreferences
import com.vivek.currencyconverter.data.remote.ApiService
import com.vivek.currencyconverter.utils.network.Resource
import com.vivek.currencyconverter.utils.network.networkBoundResource
import com.vivek.currencyconverter.utils.roundOffDecimal
import kotlinx.coroutines.flow.Flow
import timber.log.Timber
import javax.inject.Inject


class ExchangeRepositoryImpl @Inject constructor(
    private val dao: CurrencyDao,
    private val api: ApiService,
    private val perf: UserPreferences,
) : ExchangeRepository {

    override suspend fun getCurrencyRates(): Flow<Resource<List<CurrencyExchange>>> {

        return networkBoundResource(
            query = {
                dao.getCurrencyDataAsFlow()
            },
            fetch = {
                val currencies=api.getCurrencies() // got key value pairs of currency code and currency name
                Timber.d("Currencies total country codes : ${currencies.keys.size}")
                val currencyRates=api.getLatestCurrencies() // got key value pairs of currency code and currency rate
                Timber.d("Currency rates total country codes : ${currencyRates.rates.size}")
                // now we will combine both the data and return the list of CurrencyExchange
                val currencyExchangeList= mutableListOf<CurrencyExchange>()
                currencies.forEach { (code, name) ->
                    val rate=currencyRates.rates[code] ?: 0.0
                    currencyExchangeList.add(CurrencyExchange(code, name, rate.roundOffDecimal(), currencyRates.base, 1.0))
                }
                currencyExchangeList
            },
            saveFetchResult = {
                dao.insertAll(it)
            },
            shouldFetch = {
                true
            }
        )
    }


}