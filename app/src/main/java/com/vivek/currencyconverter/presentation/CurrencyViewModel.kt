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
            }
        }
    }
}