package com.example.skyreference.db

import android.content.Context
import androidx.lifecycle.LiveData
import com.example.skyreference.model.WeatherData

class WeatherLocalClient(var context: Context):WeatherLocalsource {
    override fun getAllFavourites(): LiveData<List<WeatherData>> {
        val weatherDao = WeatherDB.getInstance(context).getWeatherDAO()
        return weatherDao.allFavouriteLocation
    }

    override fun insertFavLocation(location: WeatherData) {
        val weatherDao = WeatherDB.getInstance(context).getWeatherDAO()
        return weatherDao.inserLocation(location)
    }

    override fun deleteThisLocation(location: WeatherData) {
        val weatherDao = WeatherDB.getInstance(context).getWeatherDAO()
        return weatherDao.deleteLocation(location)
    }
}