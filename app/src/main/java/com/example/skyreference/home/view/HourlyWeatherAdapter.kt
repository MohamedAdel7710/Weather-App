package com.example.skyreference.home.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.skyreference.R
import com.example.skyreference.model.Hourly
import java.text.SimpleDateFormat
import java.util.*

class HourlyWeatherAdapter(view:HomeFragmentInterface) : RecyclerView.Adapter<HourlyWeatherAdapter.HourlyViewHolder>() {

    var weatherData:List<Hourly> = emptyList()
    private val imgURL = "https://openweathermap.org/img/wn/"
    inner class HourlyViewHolder(val itemView: View) : RecyclerView.ViewHolder(itemView){
        var img : ImageView  = itemView.findViewById(R.id.hourly_weather_icon)
        var hourTxt :TextView= itemView.findViewById(R.id.hour_txt)
        var tempTxt :TextView= itemView.findViewById(R.id.hourly_temp_txt)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourlyViewHolder {
        return HourlyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.hourly_weather_cell,parent,false))
    }

    override fun onBindViewHolder(holder: HourlyViewHolder, position: Int) {
        holder.hourTxt.text = getdate(weatherData[position])
        holder.tempTxt.text = "${weatherData[position].temp.toInt()}°"
        val icon = weatherData[position].weatherList[0].weatherIcon
        Glide.with(holder.img.context)
            .setDefaultRequestOptions(RequestOptions().error(R.mipmap.ic_launcher_round))
            .load(imgURL+icon+".png")
            .into(holder.img)
    }

    override fun getItemCount(): Int {
        return weatherData.size
    }
    private fun getdate(hourly:Hourly):String
    {
        val calendar = Calendar.getInstance()
        val tz = TimeZone.getDefault()
        calendar.add(Calendar.MILLISECOND, tz.getOffset(calendar.timeInMillis))
        val sdf = SimpleDateFormat("HH:mm a", Locale.getDefault())
        val currenTimeZone = (hourly.dt.toLong()).times(1000).let { it1 -> Date(it1) }
       val time =  sdf.format(currenTimeZone).split(":")
        val interval = time[1].split(" ")
        return time[0]+" "+interval[1]
    }
}