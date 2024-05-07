package com.vivek.currencyconverter.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.squareup.moshi.Moshi
import com.vivek.currencyconverter.R
import com.vivek.currencyconverter.data.local.database.CurrencyDb
import com.vivek.currencyconverter.data.local.prefs.UserPreferences
import com.vivek.currencyconverter.data.remote.ApiService
import com.vivek.currencyconverter.data.remote.Networking
import com.vivek.currencyconverter.utils.Constants.BASE_URL
import com.vivek.currencyconverter.utils.Constants.DATABASE_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {


    @Singleton
    @Provides
    fun providesContext(@ApplicationContext context: Context) = context

    @Provides
    fun providesIoDispatcher(): CoroutineDispatcher = Dispatchers.IO


    @Provides
    @Singleton
    fun provideSharedPreferences(app: Application): SharedPreferences =
        app.getSharedPreferences(app.getString(R.string.app_name), Context.MODE_PRIVATE)

    @Provides
    @Singleton
    fun provideSharedPreferencesEditor(sharedPreferences: SharedPreferences): SharedPreferences.Editor =
        sharedPreferences.edit()

    @Provides
    @Singleton
    fun provideUserPrefs(
        sharedPreferences: SharedPreferences,
        editor: SharedPreferences.Editor,
    ): UserPreferences = UserPreferences(sharedPreferences, editor)

    @Provides
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ) = Room.databaseBuilder(context, CurrencyDb::class.java, DATABASE_NAME)
        .build()

    @Provides
    fun provideCurrencyDao(
        db: CurrencyDb
    ) = db.currencyDao()


    @Provides
    @Singleton
    fun provideMoshi(): Moshi =
        Moshi.Builder()
            .build()


    @Provides
    @Singleton
    fun provideAPiService(
        app: Application,
        preferences: SharedPreferences,
        moshi: Moshi,
    ): ApiService = Networking.create(
        BASE_URL,
        app.cacheDir,
        10 * 1024 * 1024,
        preferences,
        moshi,
    )



}