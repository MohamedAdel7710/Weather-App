package com.example.skyreference.home.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skyreference.model.RepositoryInterface
import com.example.skyreference.model.WeatherData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeViewModel(private val repo : RepositoryInterface): ViewModel() {

    private var weatherData = MutableLiveData<WeatherData>()
    var weatherDataLive : LiveData<WeatherData> = weatherData

    fun getWeatherForRemote(
        lat: Double,
        lon: Double,
        lang: String,
        id: String,
        unit: String
    )
    {
        viewModelScope.launch(Dispatchers.IO) {
            weatherData.postValue(repo.getWeather(lat, lon, lang, id, unit).body())
            weatherDataLive = weatherData
        }
    }


}