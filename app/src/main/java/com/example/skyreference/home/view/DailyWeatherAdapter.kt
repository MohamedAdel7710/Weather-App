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
import com.example.skyreference.model.Daily
import java.text.SimpleDateFormat
import java.util.*

class DailyWeatherAdapter(view:HomeFragmentInterface) : RecyclerView.Adapter<DailyWeatherAdapter.DailyViewHolder>(){

    var dailyWeather : List<Daily> = emptyList()
    private val imgURL = "https://openweathermap.org/img/wn/"
    inner class DailyViewHolder(viewItem: View) : RecyclerView.ViewHolder(viewItem){
        var img : ImageView = itemView.findViewById(R.id.daily_weather_icon)
        var dayTxt : TextView = itemView.findViewById(R.id.day_txt)
        var tempTxtMax : TextView = itemView.findViewById(R.id.daily_temp_max)
        var tempTxtMin : TextView = itemView.findViewById(R.id.daily_temp_min)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyViewHolder {
        return DailyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.daily_weather_cell,parent,false))
    }

    override fun onBindViewHolder(holder: DailyViewHolder, position: Int) {
        holder.dayTxt.text = getdate(dailyWeather[position])
        holder.tempTxtMax.text = "${dailyWeather[position].temp.max.toInt()}°"
        holder.tempTxtMin.text = "${dailyWeather[position].temp.min.toInt()}°"
        val icon = dailyWeather[position].weatherList[0].weatherIcon
        Glide.with(holder.img.context)
            .setDefaultRequestOptions(RequestOptions().error(R.mipmap.ic_launcher_round))
            .load(imgURL+icon+".png")
            .into(holder.img)
    }

    override fun getItemCount(): Int {
        return dailyWeather.size
    }

    private fun getdate(daily: Daily):String
    {

        val calendar = Calendar.getInstance()
        val timeZone = TimeZone.getDefault()
        calendar.add(Calendar.MILLISECOND, timeZone.getOffset(calendar.timeInMillis))
        val simpleDateFormat = SimpleDateFormat("EEEE", Locale.getDefault())
        val currenTimeZone = (daily.time.toLong())?.times(1000)?.let { it1 -> Date(it1) }
        return simpleDateFormat.format(currenTimeZone)
    }
}