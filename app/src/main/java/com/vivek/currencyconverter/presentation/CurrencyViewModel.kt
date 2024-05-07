package com.vivek.currencyconverter.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vivek.currencyconverter.data.local.database.CurrencyExchange
import com.vivek.currencyconverter.data.repository.ExchangeRepository
import com.vivek.currencyconverter.utils.network.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class CurrencyViewModel @Inject constructor(
    private val repo: ExchangeRepository
) : ViewModel() {

    //observe this as live data
    private val _currencyRates = MutableStateFlow<Resource<List<CurrencyExchange>>>(Resource.Loading())
    val currencyRates = _currencyRates.asStateFlow()


    private val _currencyRatesUpdates = MutableStateFlow(currencyRates.value.data ?: emptyList())
    val currencyRatesUpdates = _currencyRatesUpdates.asStateFlow()

    val currenciesList = mutableListOf<String>()


    init {
        getCurrencyRates()
    }

    // get currency rates
    fun getCurrencyRates() {
        viewModelScope.launch {
            _currencyRates.emit(Resource.Loading())
            repo.getCurrencyRates().catch {
                Timber.d("getCurrencyRates: Error: ${it.message}")
                _currencyRates.emit(Resource.Error(it))
            }.collect {
                Timber.d("getCurrencyRates: Success")
                Timber.d("item at first index: ${it.data?.getOrNull(0)}")
                _currencyRates.emit(it)

                currenciesList.clear()
                it.data?.map {
                    currenciesList.add("${it.code} - ${it.name}")
                }
                currenciesList.sort()
            }
        }
    }


    fun updateCurrencyValue(code: String, value: Double) {
        viewModelScope.launch {
            val currencyExchange = _currencyRatesUpdates.value.find { it.code == code }
            currencyExchange?.let {
                val updatedCurrencyExchange = it.copy(value = value)
                //based on this update all currency rate with new selected base currency
                val updatedList = _currencyRatesUpdates.value.map {
                    val rate = it.rate / updatedCurrencyExchange.rate
                    it.copy(value = rate * updatedCurrencyExchange.value)
                }
                _currencyRatesUpdates.emit(updatedList)
            }
        }
    }

}