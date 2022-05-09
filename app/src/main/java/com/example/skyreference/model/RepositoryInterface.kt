package com.example.skyreference.model

import retrofit2.Response

interface RepositoryInterface {
    suspend fun getWeather(lat:Double,
                           lon:Double,
                           lang:String,
                           id:String,
                           unit:String):Response<WeatherData>
}