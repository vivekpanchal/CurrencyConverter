package com.vivek.currencyconverter.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.vivek.currencyconverter.data.local.database.CurrencyExchange
import com.vivek.currencyconverter.databinding.ItemRatesBinding
import com.vivek.currencyconverter.utils.roundOffDecimal

class ExchangeAdapter : ListAdapter<CurrencyExchange, ExchangeAdapter.ViewHolder>(diffCallback) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item: CurrencyExchange = getItem(position)
        item.let {
            holder.bind(it)
        }
    }


    class ViewHolder(private val binding: ItemRatesBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            item: CurrencyExchange
        ) {
            binding.apply {
                binding.tvCode.text = item.code
                binding.tvRate.text = (item.value * item.rate).roundOffDecimal().toString()
            }
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemRatesBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }


    companion object {
        val diffCallback =
            object : DiffUtil.ItemCallback<CurrencyExchange>() {
                override fun areItemsTheSame(
                    oldItem: CurrencyExchange,
                    newItem: CurrencyExchange
                ): Boolean {
                    return oldItem == newItem
                }

                override fun areContentsTheSame(
                    oldItem: CurrencyExchange,
                    newItem: CurrencyExchange
                ): Boolean {
                    return oldItem.code == newItem.code
                }
            }
    }
}