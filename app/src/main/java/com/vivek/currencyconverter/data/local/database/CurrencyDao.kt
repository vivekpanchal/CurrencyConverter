package com.vivek.currencyconverter.data.local.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CurrencyDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(currencyDataList: List<CurrencyExchange>)

    @Query("DELETE FROM currency_data")
    suspend fun delete()

    @Query("SELECT * FROM currency_data")
    fun getCurrencyDataAsFlow(): Flow<List<CurrencyExchange>>

    @Query("SELECT COUNT(*) FROM currency_data")
    fun count(): Int

}