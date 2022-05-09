package com.example.skyreference.networklayer


import com.example.skyreference.model.WeatherData
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherServiceInterface {

    @GET("data/2.5/onecall?")
    suspend fun getCurrentWeather(@Query("lat") lat:Double,
                                  @Query("lon") lon:Double,
                                  //@Query("exclude") exclude:String,
                                  @Query("lan")lang:String,
                                  @Query("appid")id:String,
                                  @Query("units")unit:String)
                                  : Response<WeatherData>
}