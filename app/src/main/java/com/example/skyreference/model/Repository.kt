package com.example.skyreference.model

import android.content.Context
import com.example.skyreference.networklayer.RemoteClientInterface
import retrofit2.Response

class Repository private constructor(
    var remoteClient:RemoteClientInterface,
    var context: Context):RepositoryInterface{
    companion object{
        private var instance : Repository ?= null
        fun getInstance(
            remoteClient:RemoteClientInterface,
            context: Context):Repository{
            return instance?: Repository(remoteClient,context)
        }
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

}