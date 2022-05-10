package com.example.skyreference.home.view

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.skyreference.R
import com.example.skyreference.home.viewmodel.HomeViewModel
import com.example.skyreference.home.viewmodel.HomeViewModelFactory
import com.example.skyreference.model.Repository
import com.example.skyreference.networklayer.WeatherClient
import com.example.skyreference.splash.MainActivity
import java.text.SimpleDateFormat
import java.util.*


class HomeFragment : Fragment(), HomeFragmentInterface {

    private lateinit var homeViewModelFactory: HomeViewModelFactory
    private lateinit var viewModel: HomeViewModel

    //UI_Section
    private lateinit var tempTxt: TextView
    private lateinit var locationTxt: TextView
    private lateinit var currentIconImg: ImageView
    private lateinit var timeTxt: TextView
    private lateinit var weaterDes: TextView
    lateinit var hourlyRecyclerView: RecyclerView
    lateinit var hourlyWeatherAdapter: HourlyWeatherAdapter
    lateinit var dailyRecyclerView: RecyclerView
    lateinit var dailyWeatherAdapter: DailyWeatherAdapter
    lateinit var cloudTxt: TextView
    lateinit var pressureTxt: TextView
    lateinit var humidityTxt: TextView
    lateinit var windSpeedTxt: TextView
    private val imgURL = "https://openweathermap.org/img/wn/"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI(view)

        homeViewModelFactory = HomeViewModelFactory(
            Repository.getInstance(
                WeatherClient.getInstance(),
                view.context
            )
        )
        viewModel = ViewModelProvider(this, homeViewModelFactory).get(HomeViewModel::class.java)
        val sharedPreferences =
            activity?.getSharedPreferences(MainActivity.SHARED_DATA, Context.MODE_PRIVATE)
        val lat =
            sharedPreferences?.getString(HomeActivity.USER_LOCATION_LAT, null)?.toDouble() ?: 0.0
        val long =
            sharedPreferences?.getString(HomeActivity.USER_LOCATION_LONG, null)?.toDouble() ?: 0.0
        val lang = sharedPreferences?.getString(MainActivity.LANG, null) ?: "en"
        val unit = sharedPreferences?.getString(MainActivity.UNIT, null) ?: "metric"
        val id = sharedPreferences?.getString(MainActivity.ID, null)
            ?: "566c25ae9df35962cf23b1a974ae435c"
        viewModel.getWeatherForRemote(lat, long, lang, id, unit)
        getLocationDetails(lat, long)

        hourlyRecyclerView = view.findViewById(R.id.hourly_weather_recycleVew)
        hourlyRecyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        hourlyWeatherAdapter = HourlyWeatherAdapter(this)
        hourlyRecyclerView.adapter = hourlyWeatherAdapter

        dailyRecyclerView = view.findViewById(R.id.daily_weather_recycleVew)
        dailyRecyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        dailyWeatherAdapter = DailyWeatherAdapter(this)
        dailyRecyclerView.adapter = dailyWeatherAdapter


        getweatherData()
    }

    fun getweatherData() {
        viewModel.weatherDataLive.observe(requireActivity(), {
            //Current weather
            val icon = it.currentWeather.weatherList[0].weatherIcon
            tempTxt.text = "${it.currentWeather.temp.toInt()}°"
            activity?.let { myView ->
                Glide.with(myView)
                    .setDefaultRequestOptions(RequestOptions().error(R.mipmap.ic_launcher_round))
                    .load(imgURL + icon + ".png")
                    .into(currentIconImg)
            }
            getTime(it.currentWeather.time)
            weaterDes.text = it.currentWeather.weatherList[0].weatherDes
            //TODO: make values precent.................
            cloudTxt.text = it.currentWeather.clouds.toString()
            pressureTxt.text = it.currentWeather.pressure.toString()
            humidityTxt.text = it.currentWeather.humidity.toString()
            windSpeedTxt.text= it.currentWeather.windSpeed.toInt().toString()
            //Hourly weather data
            hourlyWeatherAdapter.weatherData = it.hourlyWeather
            hourlyWeatherAdapter.notifyDataSetChanged()
            //Daily weather data
            dailyWeatherAdapter.dailyWeather = it.dailyWeather
            dailyWeatherAdapter.notifyDataSetChanged()

        })
    }

    private fun getLocationDetails(myLat: Double, myLong: Double) {
        val geocoder = Geocoder(activity?.applicationContext, Locale.getDefault())
        val addresses: List<Address> = geocoder.getFromLocation(myLat, myLong, 1)
        val cityName: String = addresses[0].getAddressLine(0)
        val list = cityName.split(",")
        val cityNameList = list[list.size - 2].split(" ")
        locationTxt.text = "${cityNameList[1]},${list[list.size - 1]}"

    }

    private fun getTime(time: Int) {
        val calendar = Calendar.getInstance()
        val timeZone = TimeZone.getDefault()
        calendar.add(Calendar.MILLISECOND, timeZone.getOffset(calendar.timeInMillis))
        val simpleDateFormat = SimpleDateFormat("EEE, HH:mm a", Locale.getDefault())
        val currenTimeZone = (time.toLong())?.times(1000)?.let { it1 -> Date(it1) }
        timeTxt.text = simpleDateFormat.format(currenTimeZone)
    }

    fun initUI(view: View) {
        tempTxt = view.findViewById(R.id.current_temp_txt) as TextView
        locationTxt = view.findViewById(R.id.location_txt) as TextView
        currentIconImg = view.findViewById(R.id.current_icon) as ImageView
        timeTxt = view.findViewById(R.id.time_txt) as TextView
        weaterDes = view.findViewById(R.id.current_weather_desc) as TextView
        cloudTxt = view.findViewById(R.id.cloud_value_txt)
        pressureTxt = view.findViewById(R.id.pressure_value)
        humidityTxt = view.findViewById(R.id.humidity_value)
        windSpeedTxt = view.findViewById(R.id.wind_speed_value)


    }


}