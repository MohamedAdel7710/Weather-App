package com.example.skyreference.home.view

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentTransaction
import com.example.skyreference.R
import com.example.skyreference.SearchActivity
import com.example.skyreference.alerts.AlertFragment
import com.example.skyreference.favourites.FavFragment
import com.example.skyreference.settings.SettingsFragment
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.material.navigation.NavigationView

class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    companion object {
        private const val REQLOCATIONCODE: Int = 7
        const val USERLOCATION_LONG = "userlocationlong"
        const val USERLOCATION_LAT = "userlocationlat"
        const val SHARED_DATA = "sharedData"
        const val GPSREQCODE = 100
    }
    lateinit var drawerLayout: DrawerLayout
    lateinit var locationRequest: LocationRequest
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val homeFragment = HomeFragment()
        supportFragmentManager.beginTransaction().replace(R.id.home_fragmentcontainer, homeFragment)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .commit()
        //Navigation Drawer
        drawerLayout = findViewById(R.id.drawer_layout)
        val navigationView: NavigationView = findViewById(R.id.nav_view)


        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.tool_bar)
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        actionBar?.title = ""
        val drawerToggle: ActionBarDrawerToggle = object : ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.open,
            R.string.close
        ) {

        }
        drawerToggle.isDrawerIndicatorEnabled = true
        drawerLayout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()

        navigationView.setNavigationItemSelectedListener(this)
        //Fetch User Location
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
//        checkGPS()
        takeLocationPermissionAccess()


    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.home_p -> {
                val homeFragment = HomeFragment()
                supportFragmentManager.beginTransaction()
                    .replace(R.id.home_fragmentcontainer, homeFragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit()
            }
            R.id.fav_p -> {
                val favFragment = FavFragment()
                supportFragmentManager.beginTransaction()
                    .replace(R.id.home_fragmentcontainer, favFragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit()
            }
            R.id.alert_p -> {
                val alertFragment = AlertFragment()
                supportFragmentManager.beginTransaction()
                    .replace(R.id.home_fragmentcontainer, alertFragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit()
            }
            R.id.setting_p -> {
                val settingFragment = SettingsFragment()
                supportFragmentManager.beginTransaction()
                    .replace(R.id.home_fragmentcontainer, settingFragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit()
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onBackPressed() {

        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
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
                Manifest.permission.ACCESS_COARSE_LOCATION,),REQLOCATIONCODE)
        }
        checkGPS()
    }

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
    private fun printLocation()
    {
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager?
        val locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                val latitude = location.latitude
                val longitude = location.longitude
                Log.i("call", "Latitude: $latitude ; longitude: $longitude")
                Toast.makeText(applicationContext,"Latitude: $latitude ; longitude: $longitude",Toast.LENGTH_SHORT).show()
                //save user location
                val sharedPreferences = getSharedPreferences(SHARED_DATA, Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                    editor.apply {
                        putString(USERLOCATION_LAT,latitude.toString())
                        putString(USERLOCATION_LONG,longitude.toString())
                    }.apply()

            }
            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
            override fun onProviderEnabled(provider: String) {}
            override fun onProviderDisabled(provider: String) {}
        }
        locationManager!!.requestLocationUpdates(
            LocationManager.NETWORK_PROVIDER,
            0L,
            0f,
            locationListener
        )
    }

    private fun userResponseToPermissionReq():Boolean{
        var permission : Boolean = false


         return permission
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            REQLOCATIONCODE->{
                    if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    {
                        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                        {
                            checkGPS()
                        }
                    } else {
                        startActivity(Intent(this,SearchActivity::class.java))
                        finish()
                    }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == GPSREQCODE)
        {
            when(resultCode){
                Activity.RESULT_OK->{
                    printLocation()
                }
                Activity.RESULT_CANCELED->
                {
                    if(!getStoredUserLocation())
                    {
                        startActivity(Intent(this,SearchActivity::class.java))
                        finish()
                    }
                    val sharedPreferences = getSharedPreferences(SHARED_DATA,Context.MODE_PRIVATE)
                    val lat = sharedPreferences.getString(USERLOCATION_LAT,null)
                    val long = sharedPreferences.getString(USERLOCATION_LONG,null)
                    Log.i("call", "Latitude: $lat ; longitude: $long")
                    Toast.makeText(applicationContext,"Latitude: $lat ; longitude: $long",Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    private fun getStoredUserLocation():Boolean{
        val sharedPreferences = getSharedPreferences(SHARED_DATA,Context.MODE_PRIVATE)
        val lat = sharedPreferences.getString(USERLOCATION_LAT,null)
        val long = sharedPreferences.getString(USERLOCATION_LONG,null)
        if(lat !=null && long != null)
        {
            return true
        }
        return false
    }


}