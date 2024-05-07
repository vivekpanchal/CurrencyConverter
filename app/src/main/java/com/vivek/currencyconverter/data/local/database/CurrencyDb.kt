package com.vivek.currencyconverter.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [CurrencyExchange::class],
    version =1,
    exportSchema = false
)
abstract class CurrencyDb: RoomDatabase() {
    abstract fun currencyDao(): CurrencyDao
}