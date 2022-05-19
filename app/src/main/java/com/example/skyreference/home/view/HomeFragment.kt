package com.example.skyreference.home.view

import android.content.Context
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.skyreference.R
import com.example.skyreference.db.WeatherLocalClient
import com.example.skyreference.home.viewmodel.HomeViewModel
import com.example.skyreference.home.viewmodel.HomeViewModelFactory
import com.example.skyreference.map.view.MapsActivity
import com.example.skyreference.map.viewmodel.MapViewModel
import com.example.skyreference.map.viewmodel.MapViewModelFactory
import com.example.skyreference.model.Constant
import com.example.skyreference.model.Repository
import com.example.skyreference.networklayer.NetworkValidation
import com.example.skyreference.networklayer.WeatherClient
import com.example.skyreference.splash.MainActivity
import java.text.SimpleDateFormat
import java.util.*


class HomeFragment : Fragment(), HomeFragmentInterface {

    private lateinit var homeViewModelFactory: HomeViewModelFactory
    private lateinit var viewModel: HomeViewModel

    private lateinit var mapViewModelFactory: MapViewModelFactory
    private lateinit var mapViewModel: MapViewModel

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
                WeatherLocalClient(view.context),
                view.context
            )
        )
        viewModel = ViewModelProvider(this, homeViewModelFactory).get(HomeViewModel::class.java)
        if(NetworkValidation.isNetworkAvailable(tempTxt.context)) {
            val sharedPreferences =
                activity?.getSharedPreferences(Constant.SHARED_DATA, Context.MODE_PRIVATE)
            val id = sharedPreferences?.getString(Constant.ID, null)
                ?: "566c25ae9df35962cf23b1a974ae435c"

            val fromMap = activity?.intent?.getBooleanExtra("fromMap", false) ?: false
            var fromFav: Boolean = false
            val bundle = this.arguments
            if (bundle != null) {
                fromFav = bundle.getBoolean("fromFav")
            }
            if (fromMap) {
                val mapLat = activity?.intent?.getStringExtra("lat")
                val mapLon = activity?.intent?.getStringExtra("lon")
                viewModel.getWeatherForRemote(
                    mapLat?.toDouble() ?: 0.0,
                    mapLon?.toDouble() ?: 0.0,
                    "en",
                    id,
                    "metric"
                )
                getLocationDetails(mapLat?.toDouble() ?: 0.0, mapLon?.toDouble() ?: 0.0)
                activity?.intent?.putExtra("fromMap", false)
            } else if (fromFav) {
                val favLat = bundle?.getString("lat")
                val favLon = bundle?.getString("lon")
                viewModel.getWeatherForRemote(
                    favLat?.toDouble() ?: 0.0,
                    favLon?.toDouble() ?: 0.0,
                    "en",
                    id,
                    "metric"
                )
                getLocationDetails(favLat?.toDouble() ?: 0.0, favLon?.toDouble() ?: 0.0)
                bundle?.putBoolean("fromFav", false)
            } else {
                val lat =
                    sharedPreferences?.getString(Constant.USER_LOCATION_LAT, null)?.toDouble()
                        ?: 0.0
                val long =
                    sharedPreferences?.getString(Constant.USER_LOCATION_LONG, null)?.toDouble()
                        ?: 0.0
                val lang = sharedPreferences?.getString(Constant.LANG, null) ?: "en"
                val unit = sharedPreferences?.getString(Constant.UNIT, null) ?: "metric"


                viewModel.getWeatherForRemote(lat, long, lang, id, unit)
                getLocationDetails(lat, long)
            }

            getweatherData()
        }
        else{
            mapViewModelFactory = MapViewModelFactory(
                Repository.getInstance(
                    WeatherClient.getInstance(),
                    WeatherLocalClient(view.context),
                    view.context
                )
            )
            mapViewModel =
                ViewModelProvider(this, mapViewModelFactory).get(MapViewModel::class.java)

            var fromFav: Boolean = false
            val bundle = this.arguments
            if (bundle != null) {
                fromFav = bundle.getBoolean("fromFav")
            }
            if (fromFav) {
                val favLat = bundle?.getString("lat")
                val favLon = bundle?.getString("lon")
                mapViewModel.getLocation(favLat?.toDouble() ?: 0.0,favLon?.toDouble() ?: 0.0)

                getLocationDetails(favLat?.toDouble() ?: 0.0, favLon?.toDouble() ?: 0.0)
                getSelectedWeatherData()
                bundle?.putBoolean("fromFav", false)
            }
            else {

                getMainStoredWeather()
            }
        }
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
            cloudTxt.text = it.currentWeather.clouds.toString()+"%"
            pressureTxt.text = it.currentWeather.pressure.toString() +" hPa"
            humidityTxt.text = it.currentWeather.humidity.toString()+"%"
            windSpeedTxt.text= it.currentWeather.windSpeed.toInt().toString() + "mile/sec"
            //Hourly weather data
            hourlyWeatherAdapter.weatherData = it.hourlyWeather
            hourlyWeatherAdapter.notifyDataSetChanged()
            //Daily weather data
            dailyWeatherAdapter.dailyWeather = it.dailyWeather
            dailyWeatherAdapter.notifyDataSetChanged()

        })
    }

    fun getMainStoredWeather()
    {
        var flag = true
        mapViewModel.locations.observe(requireActivity(), {
            //Current weather

            if(it.size == 0){
                Toast.makeText(
                    activity?.applicationContext,
                    "No Stored Location Please Check your internet connection and GPS",
                    Toast.LENGTH_SHORT
                ).show()
                flag = false

            }
            else {
                val weatherData = it[0]
                val icon = weatherData.currentWeather.weatherList[0].weatherIcon
                getLocationDetails(weatherData.lat, weatherData.lon)
                tempTxt.text = "${weatherData.currentWeather.temp.toInt()}°"
                activity?.let { myView ->
                    Glide.with(myView)
                        .setDefaultRequestOptions(RequestOptions().error(R.mipmap.ic_launcher_round))
                        .load(imgURL + icon + ".png")
                        .into(currentIconImg)
                }
                getTime(weatherData.currentWeather.time)
                weaterDes.text = weatherData.currentWeather.weatherList[0].weatherDes
                //TODO: make values percent.................
                cloudTxt.text = weatherData.currentWeather.clouds.toString() + "%"
                pressureTxt.text = weatherData.currentWeather.pressure.toString() + " hPa"
                humidityTxt.text = weatherData.currentWeather.humidity.toString() + "%"
                windSpeedTxt.text =
                    weatherData.currentWeather.windSpeed.toInt().toString() + "mile/sec"
                //Hourly weather data
                hourlyWeatherAdapter.weatherData = weatherData.hourlyWeather
                hourlyWeatherAdapter.notifyDataSetChanged()
                //Daily weather data
                dailyWeatherAdapter.dailyWeather = weatherData.dailyWeather
                dailyWeatherAdapter.notifyDataSetChanged()
            }

            if(!flag)
            {
                startActivity(Intent(activity?.applicationContext,MapsActivity::class.java))
            }

        })

    }
    fun getSelectedWeatherData()
    {
        mapViewModel.weatherDataLive.observe(requireActivity(),{
            val weatherData = it
            val icon = weatherData.currentWeather.weatherList[0].weatherIcon
            getLocationDetails(weatherData.lat,weatherData.lon)
            tempTxt.text = "${weatherData.currentWeather.temp.toInt()}°"
            activity?.let { myView ->
                Glide.with(myView)
                    .setDefaultRequestOptions(RequestOptions().error(R.mipmap.ic_launcher_round))
                    .load(imgURL + icon + ".png")
                    .into(currentIconImg)
            }
            getTime(weatherData.currentWeather.time)
            weaterDes.text = weatherData.currentWeather.weatherList[0].weatherDes
            //TODO: make values percent.................
            cloudTxt.text = weatherData.currentWeather.clouds.toString()+"%"
            pressureTxt.text = weatherData.currentWeather.pressure.toString() +" hPa"
            humidityTxt.text = weatherData.currentWeather.humidity.toString()+"%"
            windSpeedTxt.text= weatherData.currentWeather.windSpeed.toInt().toString() + "mile/sec"
            //Hourly weather data
            hourlyWeatherAdapter.weatherData = weatherData.hourlyWeather
            hourlyWeatherAdapter.notifyDataSetChanged()
            //Daily weather data
            dailyWeatherAdapter.dailyWeather = weatherData.dailyWeather
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

    }


}