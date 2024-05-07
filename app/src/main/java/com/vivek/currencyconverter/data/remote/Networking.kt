package com.vivek.currencyconverter.data.remote

import android.content.SharedPreferences
import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.File


object Networking {

    private const val HEADER_ACCESS_TOKEN = "Jwt"
    private const val NETWORK_CALL_TIMEOUT = 90

    inline fun <reified T> create(
        baseUrl: String,
        cacheDir: File,
        cacheSize: Long,
        prefs: SharedPreferences,
        moshi: Moshi
    ): T {
        val okHttpClient= OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .cache(okhttp3.Cache(cacheDir, cacheSize))
            .addInterceptor { chain ->
                val original = chain.request()
                val requestBuilder = original.newBuilder()
                    .header("Accept", "application/json")
                val request = requestBuilder.build()
                chain.proceed(request)
            }
            .build()

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(okHttpClient)
            .build()
            .create(T::class.java)
    }


}