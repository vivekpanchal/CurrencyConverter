package com.vivek.currencyconverter.data.local.database

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.vivek.currencyconverter.utils.Constants.TABLE_NAME
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(
    tableName = TABLE_NAME
)
data class CurrencyExchange(
    @PrimaryKey val code: String,
    val name: String, // name of the currency
    val rate: Double, // rate of the currency
    val base: String, //base currency
    val value: Double // value of the currency
) : Parcelable


