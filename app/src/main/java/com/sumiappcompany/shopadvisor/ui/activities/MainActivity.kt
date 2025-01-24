package com.sumiappcompany.shopadvisor.ui.activities

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.sumiappcompany.shopadvisor.R
import com.sumiappcompany.shopadvisor.utils.Constants

class MainActivity : AppCompatActivity() {

    lateinit var hellotext:TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

       initialize()

        //get the shared preference
        val sharedPreferences=getSharedPreferences(
            Constants.ShopAdvisor_Preferences,
            Context.MODE_PRIVATE)
        val username=sharedPreferences.getString(Constants.LOGGED_IN_USERNAME,getString(R.string.welcome))!!
        hellotext.text= "Hello $username"




    }


    private fun initialize(){
        hellotext=findViewById(R.id.TvMAin)
    }
}