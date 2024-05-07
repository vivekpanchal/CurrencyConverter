package com.vivek.currencyconverter.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import com.vivek.currencyconverter.R
import com.vivek.currencyconverter.data.repository.ExchangeRepository
import com.vivek.currencyconverter.databinding.ActivityMainBinding
import com.vivek.currencyconverter.utils.network.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val adapter by lazy { ExchangeAdapter() }

    private val vm: CurrencyViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.rvItems.adapter = adapter
        setupObservers()


        val arrayAdapter = ArrayAdapter(this, R.layout.dropdown_item, vm.currenciesList)
        binding.filledExposedDropdown.setAdapter(arrayAdapter)

        binding.filledExposedDropdown.setOnItemClickListener { adapterView, view, i, l ->
            val selectedCurrency = adapterView.getItemAtPosition(i).toString()
            val currencyCode = selectedCurrency.split(" - ")[0]
            // do the calculation and update
            Timber.d("selected currency code: $currencyCode")
        }

        val defaultCurrency = "USD - United States Dollar"
        binding.filledExposedDropdown.setText(defaultCurrency, false)


        binding.etAmount.doOnTextChanged { text, start, before, count ->
            Timber.d("text: $text")
            // do the calculation and update
            if (text.toString().isNotEmpty()) {
                val amount = text.toString().toDouble()
                val selectedCurrency = binding.filledExposedDropdown.text.toString().split(" - ")[0]
                Timber.d("selected currency code: $selectedCurrency")
                vm.updateCurrencyValue(selectedCurrency, amount)
            }

        }
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            vm.currencyRates.collect {
                binding.progressBar.isVisible = it is Resource.Loading

                if (it is Resource.Error) {
                    Timber.d("Error: ${it.error?.message}")
                    Toast.makeText(this@MainActivity, "Error: ${it.error?.message}", Toast.LENGTH_SHORT).show()
                }

                if (it is Resource.Success) {
                    Timber.d("list size: ${it.data?.size}")
                    adapter.submitList(it.data)
                }
            }
        }
        lifecycleScope.launch {
            vm.currencyRatesUpdates.collect{
                adapter.submitList(it)
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }


}