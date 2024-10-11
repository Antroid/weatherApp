package com.antonmakov.myweatherapp

import android.app.Application
import com.antonmakov.myweatherapp.managers.SharedPreferencesManager

class WeatherApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        SharedPreferencesManager.init(this)
    }
}