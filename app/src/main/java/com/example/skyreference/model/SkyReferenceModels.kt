package com.example.skyreference.model

import androidx.room.Entity
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.jetbrains.annotations.NotNull

@Entity(tableName = "FavouriteLocations")
data class WeatherData(@NotNull @SerializedName("lat") var lat : Double,
                       @NotNull@SerializedName("lon")var long : Double,
                       @SerializedName("timezone")var timeZone : String,
                       @SerializedName("current")var currentWeather: CurrentWeather,
                       @SerializedName("hourly")var hourlyWeather:List<Hourly>,
                       @SerializedName("daily")var dailyWeather:List<Daily>)
//Current Model..........................................................................
data class CurrentWeather(@SerializedName("dt")var time:Int,//
                          @SerializedName("sunrise")var sunRiseTime:Int,
                          @SerializedName("sunset")var sunSetTime:Int,
                          @SerializedName("temp")var temp:Double,//
                          @SerializedName("feels_like")var feelsLike:Double,
                          @SerializedName("pressure")var pressure:Int,//
                          @SerializedName("humidity")var humidity: Int,//
                          @SerializedName("uvi")var uvi: Double,
                          @SerializedName("clouds")var clouds: Int,//
                          @SerializedName("visibility")var visibility: Int,//MaxValue:10000
                          @SerializedName("wind_speed")var windSpeed: Double,//
                          @SerializedName("weather")var weatherList:List<Weather>//
                          )
data class Weather(@SerializedName("id")var conditionID: Int,
                   @SerializedName("main")var weatherParams: String,
                   @SerializedName("description")var weatherDes: String,
                   @SerializedName("icon")var weatherIcon: String)
//Daily Weather Model......................................................................
data class Daily ( @SerializedName("dt") var time: Int,//
                   @SerializedName("sunrise") var sunRiseTime: Int ,
                   @SerializedName("sunset") var sunSetTime: Int ,
                   @SerializedName("temp") var temp: Temp ,
                   @SerializedName("pressure") var pressure: Int ,//
                   @SerializedName("humidity") var humidity: Int ,//
                   @SerializedName("wind_speed") var windSpeed: Double,//
                   @SerializedName("weather") var weatherList: List<Weather> ,//
                   @SerializedName("clouds") var clouds: Int )

data class Temp(@SerializedName("day") var day: Double ,
                @SerializedName("min") var min: Double ,
                @SerializedName("max") var max: Double ,
                @SerializedName("night") var night: Double ,
                @SerializedName("eve") var eve: Double,
                @SerializedName("morn") var morn: Double)
//Hourly Weather Model......................................................................
data class Hourly(@SerializedName("dt") var dt: Int,//
                  @SerializedName("temp") var temp: Double,//
                  @SerializedName("feels_like") var feelsLike: Double ,
                  @SerializedName("pressure") var pressure: Int ,//
                  @SerializedName("humidity") var humidity: Int,//
                  @SerializedName("dew_point") var dewPoint: Double ,
                  @SerializedName("uvi") var uvi: Double,
                  @SerializedName("clouds") var clouds: Int ,//
                  @SerializedName("visibility") var visibility: Int ,
                  @SerializedName("wind_speed") var windSpeed: Double,//
                  @SerializedName("wind_deg") var windDeg: Int,
                  @SerializedName("weather") var weatherList: List<Weather> ,//
                  @SerializedName("pop") var pop: Double)