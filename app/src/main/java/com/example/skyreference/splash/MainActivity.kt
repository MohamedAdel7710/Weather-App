package com.example.skyreference.splash

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.skyreference.R
import com.example.skyreference.SearchActivity
import com.example.skyreference.home.view.HomeActivity
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import java.util.*

class MainActivity : AppCompatActivity() {
    companion object{
        const val SHARED_DATA = "sharedData"
        const val LANG = "Lang"
        const val UNIT = "unit"
        const val ID = "id"
    }
    private val REQ_LOCATION_CODE: Int = 7
    private val GPS_REQ_CODE = 100
    lateinit var locationRequest: LocationRequest
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        splashScreen()
        setDefualtSetting()

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
    }
    private fun setDefualtSetting()
    {
        val sharedPreferences = getSharedPreferences(MainActivity.SHARED_DATA, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.apply {
            putString(LANG,"en")
            putString(UNIT,"metric")
            putString(ID,"566c25ae9df35962cf23b1a974ae435c")
        }.apply()
    }
    private fun splashScreen(){
        Handler().postDelayed({
          takeLocationPermissionAccess()
        }, 3000)
//        Thread.sleep(5_000)
    }
    private fun takeLocationPermissionAccess()
    {

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,),REQ_LOCATION_CODE)
            return
        }
        checkGPS()
    }
    //checking gps enable or not if not ask from user
    private fun checkGPS(){
        locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 5000
        locationRequest.fastestInterval = 2000

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
        builder.setAlwaysShow(true)
        val  result = LocationServices.getSettingsClient(this.applicationContext)
            .checkLocationSettings(builder.build())

        result.addOnCompleteListener {
                task ->
            try {
                val response = task.getResult(
                    ApiException::class.java
                )
                getLocation()
//                startActivity(Intent(this,HomeActivity::class.java))
            }
            catch (e : ApiException)
            {
                e.printStackTrace()
                when(e.statusCode)
                {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED ->{
                        try{
                            // ask for enable GPS
                            val resolveApiException = e as ResolvableApiException
                            resolveApiException.startResolutionForResult(this,100)
                        }
                        catch (sendIntentException: IntentSender.SendIntentException){
                        }
                    }
                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE->{
                        //when
                    }
                }
            }
        }

    }

    @SuppressLint("MissingPermission")
    private fun getLocation()
    {
        fusedLocationProviderClient.lastLocation.addOnCompleteListener { task ->
            val location = task.getResult()
            if(location != null)
            {
                Log.i("call", "Latitude: ${location.latitude} ; longitude: ${location.longitude}")
                Toast.makeText(applicationContext,"Latitude: ${location.latitude} ; longitude: ${location.longitude}",Toast.LENGTH_SHORT).show()
                val sharedPreferences = getSharedPreferences(SHARED_DATA, Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.apply {
                    putString(HomeActivity.USER_LOCATION_LAT,location.latitude.toString())
                    putString(HomeActivity.USER_LOCATION_LONG,location.longitude.toString())
                }.apply()
                startActivity(Intent(this,HomeActivity::class.java))
            }
        }

//        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager?
//        val locationListener = object : LocationListener {
//            override fun onLocationChanged(location: Location) {
//                val latitude = location.latitude
//                val longitude = location.longitude
//                Log.i("call", "Latitude: $latitude ; longitude: $longitude")
//                Toast.makeText(applicationContext,"Latitude: $latitude ; longitude: $longitude",Toast.LENGTH_SHORT).show()
//                //save user location
//                val sharedPreferences = getSharedPreferences(SHARED_DATA, Context.MODE_PRIVATE)
//                val editor = sharedPreferences.edit()
//                editor.apply {
//                    putString(HomeActivity.USER_LOCATION_LAT,latitude.toString())
//                    putString(HomeActivity.USER_LOCATION_LONG,longitude.toString())
//                }.apply()
//
//            }
//
//            override fun onFlushComplete(requestCode: Int) {
//                super.onFlushComplete(requestCode)
//            }
//            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
//            override fun onProviderEnabled(provider: String) {}
//            override fun onProviderDisabled(provider: String) {}
//        }
//        locationManager!!.requestLocationUpdates(
//            LocationManager.NETWORK_PROVIDER,
//            0L,
//            0f,
//            locationListener
//        )
    }

    private fun getStoredUserLocation():Boolean{
        val sharedPreferences = getSharedPreferences(SHARED_DATA,Context.MODE_PRIVATE)
        val lat = sharedPreferences.getString(HomeActivity.USER_LOCATION_LAT,null)
        val long = sharedPreferences.getString(HomeActivity.USER_LOCATION_LONG,null)
        if(lat !=null && long != null)
        {
            return true
        }
        return false
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            REQ_LOCATION_CODE->{
                if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                    {
                        checkGPS()
                    }
                } else {
                    startActivity(Intent(this, SearchActivity::class.java))
                    finish()
                }
            }
        }
    }
    //result of gps enable or not
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == GPS_REQ_CODE)
        {
            when(resultCode){
                Activity.RESULT_OK->{
                    getLocation()
                    startActivity(Intent(this,HomeActivity::class.java))
                }
                Activity.RESULT_CANCELED->
                {
                    if(!getStoredUserLocation())
                    {
                        startActivity(Intent(this,SearchActivity::class.java))
                        finish()
                    }
                    val sharedPreferences = getSharedPreferences(MainActivity.SHARED_DATA,Context.MODE_PRIVATE)
                    val lat = sharedPreferences.getString(HomeActivity.USER_LOCATION_LAT,null)
                    val long = sharedPreferences.getString(HomeActivity.USER_LOCATION_LONG,null)
                    val lang = sharedPreferences.getString(MainActivity.LANG,null)
                    val unit = sharedPreferences.getString(MainActivity.UNIT,null)
                    Log.i("call", "Latitude: $lat ; longitude: $long; lang: $lang ; unit:$unit")
                    Toast.makeText(applicationContext,"Latitude: $lat ; longitude: $long", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


}