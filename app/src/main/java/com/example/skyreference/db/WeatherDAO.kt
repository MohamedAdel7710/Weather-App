package com.example.skyreference.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.skyreference.model.WeatherData

@Dao
interface WeatherDAO {

    @get:Query("SELECT * FROM FavouriteLocations")
    val allFavouriteLocation: LiveData<List<WeatherData>>

    @Query("SELECT * FROM FavouriteLocations WHERE (lat = :lat AND lon = :lon)")
    fun getWeatherOfLocation(lat:Double,lon:Double):WeatherData

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertLocation(location:WeatherData)

    @Delete
    fun deleteLocation(location: WeatherData)
}