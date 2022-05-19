package com.example.skyreference.db

import androidx.room.TypeConverter
import com.example.skyreference.model.CurrentWeather
import com.example.skyreference.model.Daily
import com.example.skyreference.model.Hourly
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Convert {
        @TypeConverter
        fun fromcurrentWeatherToString(current: CurrentWeather): String {

            val gson = Gson()
            return gson.toJson(current)
        }

        @TypeConverter
        fun fromStringTocurrentWeather(currentString: String): CurrentWeather {

            return Gson().fromJson(currentString, CurrentWeather::class.java)
        }


        @TypeConverter
        fun fromhourlyWeatherToString(hourly: List<Hourly>): String {

            val gson = Gson()
            return gson.toJson(hourly)
        }

        @TypeConverter
        fun fromStringTohourlyWeather(hourlyString: String): List<Hourly> {

            val gson = Gson()
            val type = object : TypeToken<List<Hourly>>() {}.type
            return gson.fromJson(hourlyString, type)
        }


        @TypeConverter
        fun fromdailyWeatherToString(daily: List<Daily>): String {

            val gson = Gson()
            return gson.toJson(daily)
        }

        @TypeConverter
        fun fromStringTodailyWeather(dailyString: String): List<Daily> {

            val gson = Gson()
            val type = object : TypeToken<List<Daily>>() {}.type
            return gson.fromJson(dailyString, type)
        }


}