package com.example.skyreference.model

import androidx.lifecycle.LiveData
import retrofit2.Response

interface RepositoryInterface {
    suspend fun getWeather(lat:Double,
                           lon:Double,
                           lang:String,
                           id:String,
                           unit:String):Response<WeatherData>

    //local
    suspend fun insertFavLocation(weatherData: WeatherData)
    val favLocations:LiveData<List<WeatherData>>
    suspend fun getWeatherLocation(lat: Double,lon: Double):WeatherData
    suspend fun deleteLocation(weatherData: WeatherData)



}