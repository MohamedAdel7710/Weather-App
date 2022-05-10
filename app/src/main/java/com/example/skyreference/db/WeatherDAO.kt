package com.example.skyreference.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.skyreference.model.WeatherData

@Dao
interface WeatherDAO {

    @get:Query("SELECT * FROM FavouriteLocations")
    val allFavouriteLocation: LiveData<List<WeatherData>>

//    @Query("SELECT * FROM FavouriteLocations WHERE (lat = :lati AND long = :long)")
//    fun getWeatherOfLocation(lati:Double,long:Double):LiveData<List<WeatherData>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun inserLocation(location:WeatherData)

    @Delete
    fun deleteLocation(location: WeatherData)
}