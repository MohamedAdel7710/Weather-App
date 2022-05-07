package com.example.skyreference.home.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.MenuItem
import android.widget.Toolbar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentTransaction
import com.example.skyreference.R
import com.example.skyreference.alerts.AlertFragment
import com.example.skyreference.favourites.FavFragment
import com.example.skyreference.settings.SettingsFragment
import com.google.android.material.navigation.NavigationView

class HomeActivity : AppCompatActivity(),NavigationView.OnNavigationItemSelectedListener {


    lateinit var drawerLayout : DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val homeFragment = HomeFragment()
        supportFragmentManager.beginTransaction().replace(R.id.home_fragmentcontainer,homeFragment)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .commit()
        //Navigation Drawer
            drawerLayout = findViewById(R.id.drawer_layout)
            val navigationView: NavigationView = findViewById(R.id.nav_view)



            val toolbar : androidx.appcompat.widget.Toolbar = findViewById(R.id.tool_bar)

            setSupportActionBar(toolbar)
            val actionBar = supportActionBar
            actionBar?.title = ""
            val drawerToggle : ActionBarDrawerToggle = object : ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.open,
                R.string.close
            ){

            }
            drawerToggle.isDrawerIndicatorEnabled = true
            drawerLayout.addDrawerListener(drawerToggle)
            drawerToggle.syncState()

            navigationView.setNavigationItemSelectedListener(this)


    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.home_p->{
                val homeFragment = HomeFragment()
                supportFragmentManager.beginTransaction().replace(R.id.home_fragmentcontainer,homeFragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit()
            }
            R.id.fav_p->{
                val favFragment = FavFragment()
                supportFragmentManager.beginTransaction().replace(R.id.home_fragmentcontainer,favFragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit()
            }
            R.id.alert_p->{
                val alertFragment = AlertFragment()
                supportFragmentManager.beginTransaction().replace(R.id.home_fragmentcontainer,alertFragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit()
            }
            R.id.setting_p->{
                val settingFragment = SettingsFragment()
                supportFragmentManager.beginTransaction().replace(R.id.home_fragmentcontainer,settingFragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit()
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onBackPressed() {

        if(drawerLayout.isDrawerOpen(GravityCompat.START))
        {
            drawerLayout.closeDrawer(GravityCompat.START)
        }
        else{
            super.onBackPressed()
        }
    }
}