package com.example.wastebucks

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatDelegate

class AboutUs : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_us)

        supportActionBar?.hide()
        supportActionBar?.setDisplayShowTitleEnabled(false)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        val back = findViewById<ImageButton>(R.id.back)
        back.setOnClickListener {
            val intent = Intent(this@AboutUs, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

    }
}