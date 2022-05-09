package com.example.skyreference.networklayer

import com.example.skyreference.model.WeatherData
import retrofit2.Response

class WeatherClient private constructor():RemoteClientInterface {
    override suspend fun fetchCurrentWeather(lat:Double,
                                             lon:Double,
                                             lang:String,
                                             id:String,
                                             unit:String):Response<WeatherData> {
        val weatherService = RetrofitHelper.getInstance().create(WeatherServiceInterface::class.java)
        return weatherService.getCurrentWeather(lat,lon,lang,id,unit)
    }
    companion object{
        private var instance : WeatherClient ?= null
        fun getInstance():WeatherClient{
            return instance?: WeatherClient()
        }
    }
}