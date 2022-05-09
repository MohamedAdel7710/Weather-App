package com.example.skyreference.networklayer

import com.example.skyreference.model.WeatherData
import retrofit2.Response

interface RemoteClientInterface {
    suspend fun fetchCurrentWeather(lat:Double,
                                    lon:Double,
                                    lang:String,
                                    id:String,
                                    unit:String): Response<WeatherData>
}