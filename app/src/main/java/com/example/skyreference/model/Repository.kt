package com.example.skyreference.model

import android.content.Context
import androidx.lifecycle.LiveData
import com.example.skyreference.db.WeatherLocalsource
import com.example.skyreference.networklayer.RemoteClientInterface
import retrofit2.Response

class Repository private constructor(
    var remoteClient:RemoteClientInterface,
    var localClient:WeatherLocalsource,
    var context: Context):RepositoryInterface{
    companion object{
        private var instance : Repository ?= null
        fun getInstance(
            remoteClient:RemoteClientInterface,
            localClient:WeatherLocalsource,
            context: Context):Repository{
            return instance?: Repository(remoteClient,localClient,context)
        }
    }
    override val favLocations:LiveData<List<WeatherData>>
        get() = localClient.getAllFavourites()

    override suspend fun getWeatherLocation(lat: Double, lon: Double):WeatherData {
       return localClient.getWeatherofThisLocation(lat,lon)
    }

    override suspend fun deleteLocation(weatherData: WeatherData) {
        localClient.deleteThisLocation(weatherData)
    }

    override suspend fun getWeather(
        lat: Double,
        lon: Double,
        lang: String,
        id: String,
        unit: String
    ):Response<WeatherData> {
        return remoteClient.fetchCurrentWeather(lat, lon, lang, id, unit)
    }

    override suspend fun insertFavLocation(weatherData: WeatherData) {
        localClient.insertFavLocation(weatherData)
    }



}