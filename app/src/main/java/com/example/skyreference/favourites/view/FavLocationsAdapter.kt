package com.example.skyreference.favourites.view

import android.location.Address
import android.location.Geocoder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.skyreference.R
import com.example.skyreference.home.view.HourlyWeatherAdapter
import com.example.skyreference.model.Hourly
import com.example.skyreference.model.WeatherData
import java.util.*

class FavLocationsAdapter (var view:FavFragmentInterface) : RecyclerView.Adapter<FavLocationsAdapter.FavLocViewHolder>(){

    var weatherData:List<WeatherData> = emptyList()
    inner class FavLocViewHolder(val itemView: View):RecyclerView.ViewHolder(itemView){
        var locTxt : TextView = itemView.findViewById(R.id.fav_name_loc)
        var layout:ConstraintLayout = itemView.findViewById(R.id.location_cell_view)
        var deleteBtn : Button = itemView.findViewById(R.id.delete_location_btn)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavLocViewHolder {
        return FavLocViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.fav_location_row,parent,false))
    }

    override fun onBindViewHolder(holder: FavLocViewHolder, position: Int) {
        getLocationDetails(holder,weatherData[position].lat,weatherData[position].lon)
        holder.layout.setOnClickListener{
            view.showLocationWeather(weatherData[position].lat,weatherData[position].lon)
        }
        holder.deleteBtn.setOnClickListener{
            view.deleteLocation(weatherData[position])
        }
    }

    override fun getItemCount(): Int {
        return weatherData.size
    }

    private fun getLocationDetails(holder: FavLocViewHolder,myLat: Double, myLong: Double) {
        val geocoder = Geocoder(holder.locTxt.context, Locale.getDefault())
        val addresses: List<Address> = geocoder.getFromLocation(myLat, myLong, 1)
        val cityName: String = addresses[0].getAddressLine(0)
        val list = cityName.split(",")
        val cityNameList = list[list.size - 2].split(" ")
        holder.locTxt.text = "${cityNameList[1]},${list[list.size - 1]}"

    }
}