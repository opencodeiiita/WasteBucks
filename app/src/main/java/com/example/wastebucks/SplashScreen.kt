package com.example.wastebucks

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.RequiresApi
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


@SuppressLint("CustomSplashScreen")
class SplashScreen : AppCompatActivity() {
    @RequiresApi(34)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        Executors.newSingleThreadScheduledExecutor().schedule({
            startActivity(Intent(this, MainActivity::class.java))
            overrideActivityTransition(OVERRIDE_TRANSITION_CLOSE, android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        }, 3, TimeUnit.SECONDS)

    }
}