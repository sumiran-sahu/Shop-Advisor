package com.sumiappcompany.shopadvisor.ui.activities

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowInsets
import android.view.WindowManager
import com.sumiappcompany.shopadvisor.R
import com.sumiappcompany.shopadvisor.firestore.FireStoreClass

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)


        //using this for full screen splashscreen
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

        //code for the splashscreen showing time

        Handler(Looper.getMainLooper()).postDelayed({

            val currentUserID = FireStoreClass().getCurrentUserID()
            if (currentUserID.isNotEmpty()) {
                // Launch dashboard screen.
                startActivity(Intent(this@SplashScreen, DashBoardActivity::class.java))
            } else {
                // Launch the Login Activity
                startActivity(Intent(this@SplashScreen, LogInActivity::class.java))
            }

            finish()
        }, 1500)



    }
}