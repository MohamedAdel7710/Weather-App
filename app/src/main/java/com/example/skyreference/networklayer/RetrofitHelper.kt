package com.example.skyreference.networklayer

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitHelper{
    private const val baseURL = "https://api.openweathermap.org/"
    fun getInstance(): Retrofit {
        return Retrofit.Builder().baseUrl(baseURL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}