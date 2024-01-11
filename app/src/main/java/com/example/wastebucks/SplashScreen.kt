package com.example.wastebucks

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.annotation.RequiresApi
import com.example.wastebucks.admin.AdminScreen
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


@SuppressLint("CustomSplashScreen")
class SplashScreen : AppCompatActivity() {

    private var auth: FirebaseAuth = Firebase.auth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        auth = FirebaseAuth.getInstance()

        val executor = Executors.newSingleThreadScheduledExecutor()
        executor.schedule({
            Handler(Looper.getMainLooper()).post {
                checkUser()
            }
        }, 3, TimeUnit.SECONDS)
    }

    private fun checkUser() {
        val firebaseUser = auth.currentUser
        if (firebaseUser != null) {
            val ref = FirebaseDatabase.getInstance().getReference("Users")

            ref.child(firebaseUser!!.uid).addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val userType = snapshot.child("userType").getValue(String::class.java)

                    if (userType == "user") {
                        val intent = Intent(this@SplashScreen, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    else if(userType == "admin"){
                        val intent = Intent(this@SplashScreen, AdminScreen::class.java)
                        startActivity(intent)
                        finish()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle onCancelled event
                }
            })
        }
        else{
            val intent = Intent(this@SplashScreen, OnboardingActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}