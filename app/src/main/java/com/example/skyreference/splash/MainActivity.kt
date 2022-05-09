package com.example.skyreference.splash

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.example.skyreference.R
import com.example.skyreference.home.view.HomeActivity

class MainActivity : AppCompatActivity() {
    companion object{
        const val SHARED_DATA = "sharedData"
        const val LANG = "Lang"
        const val UNIT = "unit"
        const val ID = "id"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        splashScreen()
        setDefualtSetting()
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
            val mainIntent = Intent(this@MainActivity, HomeActivity::class.java)
            this@MainActivity.startActivity(mainIntent)
            finish()
        }, 3000)
    }

}