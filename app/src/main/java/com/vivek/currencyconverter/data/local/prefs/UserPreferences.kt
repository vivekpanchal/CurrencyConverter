package com.vivek.currencyconverter.data.local.prefs

import android.content.SharedPreferences
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferences @Inject constructor(
    private val pref: SharedPreferences,
    private val editor: SharedPreferences.Editor
) {

    /**
     * all the KEY's related to Shared Preferences
     */
    companion object {
        private const val LAST_REFRESH_TIME = "last_refresh_time"
    }

    fun saveLastRefreshTime(time: Long) {
        editor.putLong(LAST_REFRESH_TIME, time)
        editor.apply()
    }

    fun getLastRefreshTime(): Long {
        return pref.getLong(LAST_REFRESH_TIME, 0L)
    }

}