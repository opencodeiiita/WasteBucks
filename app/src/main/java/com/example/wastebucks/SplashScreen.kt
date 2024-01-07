package com.example.wastebucks

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.annotation.RequiresApi
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import com.airbnb.lottie.LottieAnimationView


// @SuppressLint("CustomSplashScreen")
override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        val lottieAnimationView: LottieAnimationView = findViewById(R.id.lottieAnimationView)

        // Set Lottie animation
        lottieAnimationView.setAnimation("splash.json")
        lottieAnimationView.playAnimation()

        // Delay for 3 seconds before transitioning to the next activity
        val handler = android.os.Handler()
        handler.postDelayed({
            startActivity(Intent(this, OnboardingActivity::class.java))
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        }, 3000) // 3000 milliseconds (3 seconds)
    }
