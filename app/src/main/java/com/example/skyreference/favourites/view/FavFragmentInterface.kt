package com.example.skyreference.favourites.view

import com.example.skyreference.model.WeatherData

interface FavFragmentInterface {
    fun showLocationWeather(lat:Double,lon:Double)
    fun deleteLocation(weatherData: WeatherData)
}