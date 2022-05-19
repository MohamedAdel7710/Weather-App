package com.example.skyreference.favourites.view

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.skyreference.R
import com.example.skyreference.db.WeatherLocalClient
import com.example.skyreference.home.view.DailyWeatherAdapter
import com.example.skyreference.home.view.HomeActivity
import com.example.skyreference.home.view.HomeFragment
import com.example.skyreference.map.view.MapsActivity
import com.example.skyreference.map.viewmodel.MapViewModel
import com.example.skyreference.map.viewmodel.MapViewModelFactory
import com.example.skyreference.model.Repository
import com.example.skyreference.model.WeatherData
import com.example.skyreference.networklayer.NetworkValidation
import com.example.skyreference.networklayer.WeatherClient
import com.google.android.material.floatingactionbutton.FloatingActionButton

class FavFragment : Fragment() ,FavFragmentInterface {

    private lateinit var mapViewModelFactory: MapViewModelFactory
    private lateinit var mapViewModel: MapViewModel
    private lateinit var loctionsRecycleView : RecyclerView
    private lateinit var favLocationsAdapter: FavLocationsAdapter
    private lateinit var addLocBtn : FloatingActionButton
    private lateinit var deleteBtn : Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_fav, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapViewModelFactory = MapViewModelFactory(
            Repository.getInstance(
                WeatherClient.getInstance(),
                WeatherLocalClient(view.context),
                view.context
            )
        )
        addLocBtn = view.findViewById(R.id.floating_btn)
        addLocBtn.setOnClickListener {
            if(NetworkValidation.isNetworkAvailable(loctionsRecycleView.context)) {
                startActivity(Intent(activity?.applicationContext, MapsActivity::class.java))
            }
            else{
                Toast.makeText(
                    activity?.applicationContext,
                    "Please Check your internet Connection",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        mapViewModel = ViewModelProvider(this, mapViewModelFactory).get(MapViewModel::class.java)
        loctionsRecycleView = view.findViewById(R.id.fav_recycle_view)
        loctionsRecycleView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        favLocationsAdapter = FavLocationsAdapter(this)
        loctionsRecycleView.adapter = favLocationsAdapter
        getFavLocations()
    }
    private fun getFavLocations(){
        mapViewModel.locations.observe(requireActivity(),{
            favLocationsAdapter.weatherData = it
            favLocationsAdapter.notifyDataSetChanged()
        })
    }

    override fun showLocationWeather(lat: Double, lon: Double) {
//        val intent = Intent(activity?.applicationContext, HomeActivity::class.java)
//        intent.putExtra("fromMap",true)
//        intent.putExtra("lat",lat)
//        intent.putExtra("lon",lon)
//
//        val homeFragment = HomeFragment()
//        parentFragmentManager.beginTransaction()
//            .replace(R.id.home_fragmentcontainer, homeFragment)
//            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
//            .commit()

        val bundle = Bundle()
        bundle.putBoolean("fromFav",true)
        bundle.putString("lat",lat.toString())
        bundle.putString("lon",lon.toString())

        val homeFragment = HomeFragment()
        homeFragment.arguments = bundle
        parentFragmentManager.beginTransaction()
        .replace(R.id.home_fragmentcontainer, homeFragment)
        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        .commit()

    }

    override fun deleteLocation(weatherData: WeatherData) {
        mapViewModel.deleteFavLocation(weatherData)
        favLocationsAdapter.notifyDataSetChanged()
    }


}