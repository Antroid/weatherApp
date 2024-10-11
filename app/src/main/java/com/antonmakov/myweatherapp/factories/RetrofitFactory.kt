package com.antonmakov.myweatherapp.factories

import com.antonmakov.myweatherapp.retrofit.AppIdInterceptor
import com.antonmakov.myweatherapp.retrofit.ForecastInterface
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object RetrofitFactory {
    const val BASE_URL = "https://api.openweathermap.org"

//    https://api.openweathermap.org/data/2.5/weather?lat=%f&lon=%f&appid=$API_KEY"

    fun makeForecastService(): ForecastInterface {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(getClient())
            .addConverterFactory(MoshiConverterFactory.create())
            .build().create(ForecastInterface::class.java)
    }


    private fun getLoggingInterceptor(): Interceptor{
        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        return interceptor
    }

    private fun getClient(): OkHttpClient = OkHttpClient.Builder()
            .addInterceptor(AppIdInterceptor())
            .addInterceptor(getLoggingInterceptor())
            .build()



}