package com.example.billapp.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.billapp.MainActivity
import com.example.billapp.R
import com.example.billapp.firebase.FirestoreClass
import kotlinx.coroutines.DelicateCoroutinesApi


@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_splash)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        val tvAppName: TextView = findViewById(R.id.tv_app_name)
        val typeface: Typeface =
            Typeface.createFromAsset(assets, "Montserrat-Bold.ttf")
        tvAppName.typeface = typeface

        Handler(Looper.getMainLooper()).postDelayed({
            Log.d("SplashActivity", "Starting IntroActivity")

            var currentUserID = FirestoreClass().getCurrentUserID()

            if(currentUserID.isNotEmpty()){
                val mainIntent = Intent(this@SplashActivity, MainActivity::class.java)
                startActivity(mainIntent)
            }else{
                val introIntent = Intent(this@SplashActivity, IntroActivity::class.java)
                startActivity(introIntent)
            }

            finish()
        }, 2500)
    }
}