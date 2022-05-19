package com.example.skyreference.settings

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import com.example.skyreference.R
import com.example.skyreference.model.Constant
import com.example.skyreference.splash.MainActivity


class SettingsFragment : Fragment() {

    private lateinit var unit : String
    private lateinit var lang : String
    private var alert : Boolean = false
    //UI
    lateinit var lang_radioGroup : RadioGroup
    lateinit var windSpeed_radioGroup:RadioGroup
    lateinit var tempUnit_radipGroup:RadioGroup
    lateinit var notify_radipGroup:RadioGroup
    lateinit var updateBtn:Button
    lateinit var lang_radioBtn:RadioButton
    lateinit var windSpeed_radioBtn:RadioButton
    lateinit var tempUnit_radioBtn:RadioButton
    lateinit var notify_radioBtn:RadioButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI(view)
        getUserSetting(view)

    }
    private fun getUserSetting(view: View)
    {
        val sharedPreferences = activity?.getSharedPreferences(Constant.SHARED_DATA, Context.MODE_PRIVATE)
        lang = sharedPreferences?.getString(Constant.LANG,null)?: "en"
        unit = sharedPreferences?.getString(Constant.UNIT,null)?: "metric"
        alert = sharedPreferences?.getBoolean(Constant.ALERT,false)?: false
        if(lang == "en") {
            val btnNum = lang_radioGroup.getChildAt(0).id
            //lang_radioBtn = view.findViewById(btnNum)
            lang_radioGroup.check(btnNum)
        }
        else{
            val btnNum = lang_radioGroup.getChildAt(1).id
            //lang_radioBtn = view.findViewById(btnNum)
            lang_radioGroup.check(btnNum)
        }
        //wind speed & temp
        if(unit == "metric") {
            val windbtnNum = windSpeed_radioGroup.getChildAt(0).id
            val tempbtnNum = tempUnit_radipGroup.getChildAt(0).id
            windSpeed_radioGroup.check(windbtnNum)
            tempUnit_radipGroup.check(tempbtnNum)
        }
        else if(unit == "imperial") {
            val windbtnNum = windSpeed_radioGroup.getChildAt(1).id
            val tempbtnNum = tempUnit_radipGroup.getChildAt(2).id
            windSpeed_radioGroup.check(windbtnNum)
            tempUnit_radipGroup.check(tempbtnNum)
        }
        else{
            val windbtnNum = windSpeed_radioGroup.getChildAt(0).id
            val tempbtnNum = tempUnit_radipGroup.getChildAt(1).id
            windSpeed_radioGroup.check(windbtnNum)
            tempUnit_radipGroup.check(tempbtnNum)
        }


        //notifications
        if(!alert) {
            val notify_num = notify_radipGroup.getChildAt(1).id
            notify_radipGroup.check(notify_num)
        }
        else{
            val notify_num = notify_radipGroup.getChildAt(0).id
            notify_radipGroup.check(notify_num)
        }

    }
    private fun initUI(view: View)
    {
        lang_radioGroup = view.findViewById(R.id.lang_group)
        windSpeed_radioGroup= view.findViewById(R.id.wind_group)
        tempUnit_radipGroup= view.findViewById(R.id.temp_group)
        notify_radipGroup= view.findViewById(R.id.notify_group)
        updateBtn = view.findViewById(R.id.update_btn)

        val sharedPreferences = activity?.getSharedPreferences(Constant.SHARED_DATA, Context.MODE_PRIVATE)
        updateBtn.setOnClickListener {
            //Language
            val langNum = lang_radioGroup.checkedRadioButtonId
            lang_radioBtn = view.findViewById(langNum)
            updateLang(lang_radioBtn.text.toString())
        }

    }

    //update button
    private fun updateLang(lang:String){
        val sharedPreferences = activity?.getSharedPreferences(Constant.SHARED_DATA, Context.MODE_PRIVATE)

        sharedPreferences?.edit()?.apply() {
            if (lang == "Arabic") {
                putString(Constant.LANG, "ar")
            } else {
                putString(Constant.LANG, "en")
            }
        }?.apply()

        var lang_shared = sharedPreferences?.getString(Constant.LANG,null)
        Log.i("call", "initUI: $lang_shared")
    }
    private fun updateWeatherUnit(unit:String){}
    private fun updateAlertSetting(isEnable:Boolean){}
}