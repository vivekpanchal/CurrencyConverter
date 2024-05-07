package com.vivek.currencyconverter.data.repository

import com.vivek.currencyconverter.data.local.database.CurrencyDao
import com.vivek.currencyconverter.data.local.database.CurrencyExchange
import com.vivek.currencyconverter.data.local.prefs.UserPreferences
import com.vivek.currencyconverter.data.remote.ApiService
import com.vivek.currencyconverter.domain.ExchangeRepository
import com.vivek.currencyconverter.utils.network.Resource
import com.vivek.currencyconverter.utils.network.networkBoundResource
import com.vivek.currencyconverter.utils.roundOffDecimal
import com.vivek.currencyconverter.utils.shouldRefresh
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject


class ExchangeRepositoryImpl @Inject constructor(
    private val dao: CurrencyDao,
    private val api: ApiService,
    private val perf: UserPreferences,
) : ExchangeRepository {

    override fun getCurrencyRates(): Flow<Resource<List<CurrencyExchange>>> {
        return networkBoundResource(
            query = {
                dao.getCurrencyDataAsFlow()
            },
            fetch = {
                val currencies =
                    api.getCurrencies() // got key value pairs of currency code and currency name
                Timber.d("Currencies total country codes : ${currencies.keys.size}")
                val currencyRates =
                    api.getLatestCurrencies() // got key value pairs of currency code and currency rate
                Timber.d("Currency rates total country codes : ${currencyRates.rates.size}")
                // now we will combine both the data and return the list of CurrencyExchange
                val currencyExchangeList = mutableListOf<CurrencyExchange>()
                val baseRates = currencyRates.rates
                currencies.forEach { (code, name) ->
                    currencyExchangeList
                        .add(
                            CurrencyExchange(
                                code,
                                name,
                                if (baseRates[code] != null) baseRates[code]!!.roundOffDecimal() else 1.0
                            )
                        )
                }
                currencyExchangeList
            },
            saveFetchResult = {
                //save last fetched time
                perf.saveLastRefreshTime(System.currentTimeMillis())
                dao.insertAll(it)
            },
            shouldFetch = {
                val lastRefreshTime = perf.getLastRefreshTime()
                val currentTime = System.currentTimeMillis()
                //if passed 30mins then only get new updates
                shouldRefresh(currentTime, lastRefreshTime, 30)
            }
        )
    }


    override suspend fun convertCurrencies(
        amount: Double,
        selectedCurrency: String,
        currencyRates: List<CurrencyExchange>
    ): Result<List<CurrencyExchange>> {
        return if (currencyRates.isEmpty()) {
            Result.failure(IllegalArgumentException("Empty currency rates list"))
        } else {
            val baseRateFromCurrency: Double =
                getBaseRateForCurrency(currencyRates, selectedCurrency)
            computationCall {
                currencyRates.map { c ->
                    CurrencyExchange(
                        c.code,
                        c.name,
                        convertCurrency(amount, baseRateFromCurrency, c.rate).roundOffDecimal()
                    )
                }
            }
        }
    }

    private fun convertCurrency(
        amount: Double,
        baseRateFromCurrency: Double,
        baseRateToCurrency: Double
    ): Double {
        return amount * baseRateToCurrency / baseRateFromCurrency
    }

    private fun getBaseRateForCurrency(
        exchangeRates: List<CurrencyExchange>,
        selectedCurrencyCode: String
    ): Double {
        return exchangeRates.find { c -> c.code == selectedCurrencyCode }?.rate ?: 1.0
    }

    private suspend fun <T> computationCall(
        call: suspend () -> T
    ): Result<T> = runCatching {
        withContext(Dispatchers.IO) {
            call.invoke()
        }
    }


}