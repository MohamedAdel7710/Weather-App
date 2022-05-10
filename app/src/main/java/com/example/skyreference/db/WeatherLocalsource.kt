package com.example.skyreference.db

import androidx.lifecycle.LiveData
import com.example.skyreference.model.WeatherData

interface WeatherLocalsource {
    fun getAllFavourites():LiveData<List<WeatherData>>
    fun insertFavLocation(location:WeatherData)
    fun deleteThisLocation(location:WeatherData)
}