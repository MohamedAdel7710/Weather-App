package com.example.skyreference.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.skyreference.model.Weather

@Database(entities = [Weather::class], version = 1)
abstract class WeatherDB : RoomDatabase() {

    abstract fun getWeatherDAO(): WeatherDAO

    companion object {
        private var instance : WeatherDB?= null
        fun getInstance(context: Context): WeatherDB {
            return instance?: Room.databaseBuilder(context,WeatherDB::class.java,"FavouriteLocations").build()
        }
    }
}
