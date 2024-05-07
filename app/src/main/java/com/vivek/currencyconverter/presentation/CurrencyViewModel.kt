package com.vivek.currencyconverter.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vivek.currencyconverter.data.local.database.CurrencyExchange
import com.vivek.currencyconverter.domain.ExchangeRepository
import com.vivek.currencyconverter.utils.network.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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


    private val _currencyRatesUiState = MutableStateFlow<Resource<List<CurrencyExchange>>>(Resource.Loading())
    val currencyRatesUiState: StateFlow<Resource<List<CurrencyExchange>>> = _currencyRatesUiState

    val currenciesList = mutableListOf<String>()


    init {
        getCurrencyRates()
    }

    // get currency rates
    private fun getCurrencyRates() {
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


    fun convertCurrency(amount: Double, selectedCurrency: String) {
        viewModelScope.launch {
            repo.convertCurrencies(amount, selectedCurrency, currencyRates.value.data ?: emptyList())
                .onSuccess {
                    _currencyRatesUiState.emit(Resource.Success(it))
                }
                .onFailure { e ->
                    _currencyRatesUiState.emit(Resource.Error(e))
                }
        }

    }

}