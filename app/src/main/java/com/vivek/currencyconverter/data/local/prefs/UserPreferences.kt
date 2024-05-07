package com.vivek.currencyconverter.data.local.prefs

import android.content.SharedPreferences
import android.util.Base64
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
        private const val DB_ROOM_KEY = "RoomKey"
        private const val LOGIN_TYPE = "login_type"

    }

    private fun encodeToString(aKey: ByteArray): String {
        return Base64.encodeToString(aKey, Base64.DEFAULT)
    }

    private fun decodeFromString(aSavedKey: String): ByteArray {
        return Base64.decode(aSavedKey, Base64.DEFAULT)
    }


}