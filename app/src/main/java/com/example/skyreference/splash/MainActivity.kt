package com.example.skyreference.splash

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.skyreference.map.view.MapsActivity
import com.example.skyreference.R
import com.example.skyreference.favourites.view.FavActivity
import com.example.skyreference.home.view.HomeActivity
import com.example.skyreference.model.Constant
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import java.util.*

class MainActivity : AppCompatActivity() {

    private val REQ_LOCATION_CODE: Int = 7
    private val GPS_REQ_CODE = 100
    lateinit var locationRequest: LocationRequest
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setDefaultSetting()
        splashScreen()


        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
    }
    private fun setDefaultSetting()
    {
        val sharedPreferences = getSharedPreferences(Constant.SHARED_DATA, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.apply {
            putString(Constant.LANG,"en")
            putString(Constant.UNIT,"metric")
            putString(Constant.ID,"566c25ae9df35962cf23b1a974ae435c")
            putBoolean(Constant.ALERT,false)
//            putString(Constant.USER_LOCATION_LAT,"0.0")
//            putString(Constant.USER_LOCATION_LONG,"0.0")
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
        val locationRequest = LocationRequest
            .create()
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setInterval(10 * 1000)
            .setFastestInterval(2000)

        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    super.onLocationResult(locationResult)
                    Log.i("call", "Latitude: ${locationResult.lastLocation.latitude} ; longitude: ${locationResult.lastLocation.longitude}")
                    Toast.makeText(applicationContext,"Latitude: ${locationResult.lastLocation.latitude} ; longitude: ${locationResult.lastLocation.longitude}",Toast.LENGTH_SHORT).show()
                    val sharedPreferences = getSharedPreferences(Constant.SHARED_DATA, Context.MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.apply {
                        putString(HomeActivity.USER_LOCATION_LAT,locationResult.lastLocation.latitude.toString())
                        putString(HomeActivity.USER_LOCATION_LONG,locationResult.lastLocation.longitude.toString())
                    }.apply()
                    val intent = Intent(this@MainActivity,HomeActivity::class.java)
                    intent.putExtra("toFav",false)
                    startActivity(intent)
                    fusedLocationProviderClient.removeLocationUpdates(this)
                }
            }, Looper.getMainLooper()
        )


//        fusedLocationProviderClient.re.addOnSuccessListener(this
//        ) {
//            if(it != null)
//            {
//                Log.i("call", "Latitude: ${it.latitude} ; longitude: ${it.longitude}")
//                Toast.makeText(applicationContext,"Latitude: ${it.latitude} ; longitude: ${it.longitude}",Toast.LENGTH_SHORT).show()
//                val sharedPreferences = getSharedPreferences(SHARED_DATA, Context.MODE_PRIVATE)
//                val editor = sharedPreferences.edit()
//                editor.apply {
//                    putString(HomeActivity.USER_LOCATION_LAT,it.latitude.toString())
//                    putString(HomeActivity.USER_LOCATION_LONG,it.longitude.toString())
//                }.apply()
//                startActivity(Intent(this,HomeActivity::class.java))
//            }
//        }

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
        val sharedPreferences = getSharedPreferences(Constant.SHARED_DATA,Context.MODE_PRIVATE)
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
                    val intent = Intent(this,HomeActivity::class.java)
                    intent.putExtra("toFav",true)
                    startActivity(intent)
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
//                    startActivity(Intent(this,HomeActivity::class.java))
                }
                Activity.RESULT_CANCELED->
                {
                    if(!getStoredUserLocation())
                    {
                        val intent = Intent(this, HomeActivity::class.java)
                        intent.putExtra("toFav",true)
                        startActivity(intent)
                        finish()
                    }
                    else {
                        val sharedPreferences =
                            getSharedPreferences(Constant.SHARED_DATA, Context.MODE_PRIVATE)
                        val lat = sharedPreferences.getString(Constant.USER_LOCATION_LAT, null)
                        val long = sharedPreferences.getString(Constant.USER_LOCATION_LONG, null)
                        val lang = sharedPreferences.getString(Constant.LANG, null)
                        val unit = sharedPreferences.getString(Constant.UNIT, null)
                        val intent = Intent(this, HomeActivity::class.java)
                        intent.putExtra("toFav", false)
                        startActivity(intent)
                        Log.i("call", "Latitude: $lat ; longitude: $long; lang: $lang ; unit:$unit")
//                        Toast.makeText(
//                            applicationContext,
//                            "Latitude: $lat ; longitude: $long",
//                            Toast.LENGTH_SHORT
//                        ).show()
                    }
                }
            }
        }
    }


}