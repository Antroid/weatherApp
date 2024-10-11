package com.antonmakov.myweatherapp.retrofit

import com.antonmakov.myweatherapp.models.retrofit.WeatherResult
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ForecastInterface {

    @GET("/data/2.5/weather")
    suspend fun getWeatherForecast(@Query("lat") lat: Double, @Query("lon") lon: Double, @Query("units") units: String): Response<WeatherResult>

}