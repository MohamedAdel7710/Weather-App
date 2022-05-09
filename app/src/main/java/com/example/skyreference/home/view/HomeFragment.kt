package com.example.skyreference.home.view

import android.content.Context
import android.location.Address
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.example.skyreference.R
import com.example.skyreference.home.viewmodel.HomeViewModel
import com.example.skyreference.home.viewmodel.HomeViewModelFactory
import com.example.skyreference.model.Repository
import com.example.skyreference.networklayer.WeatherClient
import com.example.skyreference.splash.MainActivity
import android.location.Geocoder
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import java.text.SimpleDateFormat
import java.util.*


class HomeFragment : Fragment() {

    private lateinit var homeViewModelFactory: HomeViewModelFactory
    private lateinit var viewModel:HomeViewModel

    //UI_Section
    private lateinit var tempTxt : TextView
    private lateinit var locationTxt : TextView
    private lateinit var currentIconImg : ImageView
    private lateinit var timeTxt : TextView

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
           ))
        viewModel = ViewModelProvider(this,homeViewModelFactory).get(HomeViewModel::class.java)
        val sharedPreferences = activity?.getSharedPreferences(MainActivity.SHARED_DATA, Context.MODE_PRIVATE)
        val lat = sharedPreferences?.getString(HomeActivity.USER_LOCATION_LAT,null)?.toDouble()?:0.0
        val long = sharedPreferences?.getString(HomeActivity.USER_LOCATION_LONG,null)?.toDouble()?:0.0
        val lang = sharedPreferences?.getString(MainActivity.LANG,null)?: "en"
        val unit = sharedPreferences?.getString(MainActivity.UNIT,null)?: "metric"
        val id = sharedPreferences?.getString(MainActivity.ID,null)?: "566c25ae9df35962cf23b1a974ae435c"
        viewModel.getWeatherForRemote(lat,long,lang,id,unit)
        getLocationDetails(lat,long)

        getweatherData()
    }
    fun getweatherData()
    {
        viewModel.weatherDataLive.observe(requireActivity(),{
//            Log.i("call", "getweatherData: ${it.currentWeather.temp}")
            tempTxt.text= "${it.currentWeather.temp.toInt()}°"
            activity?.let {myView->
                Glide.with(myView)
                .setDefaultRequestOptions(RequestOptions().error(R.mipmap.ic_launcher_round))
                .load("http://openweathermap.org/img/w/${it.currentWeather.weatherList[0].weatherIcon}.png")
                .into(currentIconImg)
            }
            getTime(it.currentWeather.time)
        })
    }
    private fun getLocationDetails(myLat:Double, myLong:Double)
    {
        val geocoder = Geocoder(activity?.applicationContext, Locale.getDefault())
        val addresses: List<Address> = geocoder.getFromLocation(myLat, myLong, 1)
        val cityName: String = addresses[0].getAddressLine(0)
        val list = cityName.split(",")
        val cityNameList = list[list.size-2].split(" ")
        locationTxt.text = "${cityNameList[1]},${list[list.size-1]}"

    }
    private fun getTime(time:Int){
        val calendar = Calendar.getInstance()
        val timeZone = TimeZone.getDefault()
        calendar.add(Calendar.MILLISECOND, timeZone.getOffset(calendar.timeInMillis))
        val simpleDateFormat = SimpleDateFormat("EEE, HH:mm a", Locale.getDefault())
        val currenTimeZone = (time.toLong())?.times(1000)?.let { it1 -> Date(it1) }
        timeTxt.text = simpleDateFormat.format(currenTimeZone)
    }

    fun initUI(view: View)
    {
        tempTxt = view.findViewById(R.id.current_temp_txt) as TextView
        locationTxt = view.findViewById(R.id.location_txt) as TextView
        currentIconImg = view.findViewById(R.id.current_icon) as ImageView
        timeTxt = view.findViewById(R.id.time_txt) as TextView
    }


}