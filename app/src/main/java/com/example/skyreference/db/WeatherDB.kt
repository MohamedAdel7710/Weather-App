package com.example.skyreference.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.skyreference.model.WeatherData

@TypeConverters(Convert::class)
@Database(entities = [WeatherData::class], version = 1)
abstract class WeatherDB : RoomDatabase() {

    abstract fun getWeatherDAO(): WeatherDAO

    companion object {
        private var instance : WeatherDB?= null
        fun getInstance(context: Context): WeatherDB {
            return instance?: Room.databaseBuilder(context,WeatherDB::class.java,"FavouriteLocations").build()
        }
    }
}
