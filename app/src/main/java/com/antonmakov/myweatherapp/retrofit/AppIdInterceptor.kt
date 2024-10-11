package com.antonmakov.myweatherapp.retrofit

import com.antonmakov.myweatherapp.Consts
import okhttp3.Interceptor
import okhttp3.Response

class AppIdInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val currentUrl = chain.request().url()
        val newUrl = currentUrl.newBuilder().addQueryParameter("appid", Consts.API_KEY).build()
        val currentRequest = chain.request().newBuilder()
        val newRequest = currentRequest.url(newUrl).build()
        return chain.proceed(newRequest)
    }
}