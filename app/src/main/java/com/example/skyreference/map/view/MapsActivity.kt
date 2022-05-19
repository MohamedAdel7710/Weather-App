package com.example.skyreference.map.view

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.lifecycle.ViewModelProvider
import com.example.skyreference.R

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.skyreference.databinding.ActivityMapsBinding
import com.example.skyreference.db.WeatherLocalClient
import com.example.skyreference.favourites.view.FavActivity
import com.example.skyreference.home.view.HomeActivity
import com.example.skyreference.home.viewmodel.HomeViewModel
import com.example.skyreference.home.viewmodel.HomeViewModelFactory
import com.example.skyreference.map.viewmodel.MapViewModel
import com.example.skyreference.map.viewmodel.MapViewModelFactory
import com.example.skyreference.model.Constant
import com.example.skyreference.model.Repository
import com.example.skyreference.networklayer.WeatherClient
import com.example.skyreference.splash.MainActivity
import com.google.android.gms.location.FusedLocationProviderClient

class MapsActivity : AppCompatActivity(), OnMapReadyCallback{

    //GoogleMap.OnMarkerClickListener

    private lateinit var map: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var mapViewModelFactory: MapViewModelFactory
    private lateinit var mapViewModel: MapViewModel
    private lateinit var doneBtn : Button
    private lateinit var lat:String
    private lateinit var long:String
    private lateinit var homeViewModelFactory: HomeViewModelFactory
    private lateinit var homeviewModel: HomeViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        //fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mapViewModelFactory = MapViewModelFactory(
            Repository.getInstance(
                WeatherClient.getInstance(),
                WeatherLocalClient(context = applicationContext),
                context = applicationContext
            )
        )
        mapViewModel = ViewModelProvider(this, mapViewModelFactory).get(MapViewModel::class.java)

        homeViewModelFactory = HomeViewModelFactory(
            Repository.getInstance(
                WeatherClient.getInstance(),
                WeatherLocalClient(applicationContext),
                applicationContext
            )
        )
        homeviewModel = ViewModelProvider(this, homeViewModelFactory).get(HomeViewModel::class.java)
        val sharedPreferences = getSharedPreferences(Constant.SHARED_DATA, Context.MODE_PRIVATE)
        val lang = sharedPreferences?.getString(Constant.LANG, null) ?: "en"
        val unit = sharedPreferences?.getString(Constant.UNIT, null) ?: "metric"
        val id = sharedPreferences?.getString(Constant.ID, null)
            ?: "566c25ae9df35962cf23b1a974ae435c"

        doneBtn = findViewById(R.id.add_location)
        doneBtn.setOnClickListener {
//            sharedPreferences.edit().apply(){
//                putString(Constant.USER_LOCATION_LAT,lat)
//                putString(Constant.USER_LOCATION_LONG,long)
//            }.apply()
            homeviewModel.getWeatherForRemote(lat.toDouble(), long.toDouble(), "en", id, "metric")
            homeviewModel.weatherDataLive.observe(this, {
                mapViewModel.insertNewLocation(it)
                val intent = Intent(this,HomeActivity::class.java)
                intent.putExtra("fromMap",true)
                intent.putExtra("lat",lat)
                intent.putExtra("lon",long)
                startActivity(intent)
            })

        }


    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
        map.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        map.moveCamera(CameraUpdateFactory.newLatLng(sydney))
        map.setOnMapClickListener {
            val markerOptions = MarkerOptions()
            markerOptions.position(it)
            lat = it.latitude.toString()
            long = it.longitude.toString()
            Log.i("Call", "onMapReady: lat: ${it.latitude},long: ${it.longitude}")
            map.clear()
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(it,10f))
            map.addMarker(markerOptions)
        }

    }

//    override fun onMarkerClick(p0: Marker): Boolean {
//        Log.i("call", "onMarkerClick: ${p0.title}")
//        return true
//    }

}