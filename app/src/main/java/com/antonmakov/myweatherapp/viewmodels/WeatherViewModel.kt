package com.antonmakov.myweatherapp.viewmodels

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Address
import android.location.Geocoder
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.antonmakov.myweatherapp.R
import com.antonmakov.myweatherapp.factories.RetrofitFactory
import com.antonmakov.myweatherapp.models.retrofit.WeatherResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL
import kotlin.coroutines.suspendCoroutine

class WeatherViewModel: ViewModel() {

private val weatherLiveData: MutableLiveData<WeatherResult> = MutableLiveData()
private var weather: WeatherResult? = null

private val weatherIconLiveData: MutableLiveData<Bitmap> = MutableLiveData()
private var weatherIcon: Bitmap? = null

private val error: MutableLiveData<String> = MutableLiveData()

private val job = Job()
private val coroutineScope = CoroutineScope(Dispatchers.Default + job)


    fun loadWeather(lat : Double?, long: Double?, context: Context) {
        if(lat == null || long == null) {
            error.postValue("Unable to find Location, please try again")
            weatherLiveData.postValue(null)
        } else {
            coroutineScope.launch {
                val service = RetrofitFactory.makeForecastService()
                CoroutineScope(Dispatchers.IO).launch {
                    val response = service.getWeatherForecast(
                        lat,
                        long,
                        context.getString(R.string.imperial_units)
                    )
                    if (response.isSuccessful) {
                        weather = response.body()
                        weatherLiveData.postValue(weather)
                        error.postValue("")
                    } else {
                        error.postValue(response.errorBody().toString())
                        weatherLiveData.postValue(null)
                    }
                }
            }
        }
    }

    fun getWeather() = weatherLiveData

    fun loadWeatherImage(weatherState: String) {
        coroutineScope.launch {
            withContext(coroutineScope.coroutineContext) {
                weatherIcon =
                    BitmapFactory.decodeStream(URL("https://openweathermap.org/img/wn/${weatherState}@2x.png").openStream())
                weatherIconLiveData.postValue(weatherIcon)
            }
        }
    }

    fun getWeatherIcon() = weatherIconLiveData

    override fun onCleared() {
        job.cancel()
    }

}