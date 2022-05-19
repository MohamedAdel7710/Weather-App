package com.example.skyreference.favourites.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.FragmentTransaction
import com.example.skyreference.R
import com.example.skyreference.home.view.HomeFragment
import com.example.skyreference.map.view.MapsActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton

class FavActivity : AppCompatActivity() {
    private lateinit var addLocBtn :FloatingActionButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fav)
        val favFragment = FavFragment()
        supportFragmentManager.beginTransaction().replace(R.id.fav_framelayout, favFragment)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .commit()
        addLocBtn = findViewById(R.id.floating_btn)
        addLocBtn.setOnClickListener {
            startActivity(Intent(this,MapsActivity::class.java))
        }
    }
}