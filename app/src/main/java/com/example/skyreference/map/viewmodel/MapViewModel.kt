package com.example.skyreference.map.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skyreference.model.RepositoryInterface
import com.example.skyreference.model.WeatherData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MapViewModel (private val repo : RepositoryInterface): ViewModel() {

    private var weatherData = MutableLiveData<WeatherData>()
    var weatherDataLive : LiveData<WeatherData> = weatherData

    var locations : LiveData<List<WeatherData>> = repo.favLocations

    fun insertNewLocation(weatherData: WeatherData){
        viewModelScope.launch(Dispatchers.IO) {
            repo.insertFavLocation(weatherData)
        }
    }
    fun getLocation(lat:Double,lon:Double)
    {
        viewModelScope.launch(Dispatchers.IO) {
            weatherData.postValue(repo.getWeatherLocation(lat,lon))
            weatherDataLive = weatherData
        }
    }
    fun deleteFavLocation(weatherData: WeatherData)
    {
        viewModelScope.launch(Dispatchers.IO) {
            repo.deleteLocation(weatherData)
        }
    }




}