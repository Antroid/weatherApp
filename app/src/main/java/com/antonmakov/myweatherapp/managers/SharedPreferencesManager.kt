package com.antonmakov.myweatherapp.managers

import android.content.Context
import android.content.SharedPreferences

class SharedPreferencesManager {

    companion object {
        private const val PREF_NAME = "MySharedPrefs"
        private lateinit var sharedPreferences: SharedPreferences

        fun init(context: Context) {
            sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        }

        fun saveString(key: String, value: String?) {
            sharedPreferences.edit().putString(key, value).apply()
        }

        fun getString(key: String, defaultValue: String?): String? {
            return sharedPreferences.getString(key, defaultValue) ?: defaultValue
        }

        fun saveBoolean(key: String, value: Boolean) {
            sharedPreferences.edit().putBoolean(key, value).apply()
        }

        fun getBoolean(key: String, defaultValue: Boolean): Boolean {
            return sharedPreferences.getBoolean(key, defaultValue)
        }

    }

}