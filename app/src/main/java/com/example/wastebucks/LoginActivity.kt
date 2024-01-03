package com.example.wastebucks

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {


    private var auth: FirebaseAuth = Firebase.auth

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if (currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        supportActionBar?.hide()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        // get input from login here
        val email = findViewById<EditText>(R.id.logemail)
        val password = findViewById<EditText>(R.id.log_pwd)


        val loginButton = findViewById<TextView>(R.id.login_btn)


        findViewById<TextView>(R.id.login).setOnClickListener{
            startActivity(Intent( this, RegisterActivity::class.java))
        }
        loginButton.setOnClickListener{
            if(email.text.toString().isEmpty())
            {
                email.error = "Please Enter an email"
                email.requestFocus()
                return@setOnClickListener
            }

            if(!email.text.toString().contains("@")) {
                email.error = "Please enter a valid email"
                email.requestFocus()
                return@setOnClickListener
            }


            if(password.text.toString().isEmpty())
            {
                password.error = "Please Enter a password"
                password.requestFocus()
                return@setOnClickListener
            }

            if(password.text.toString().length < 8) {
                password.error = "Password should be at least 8 characters long"
                password.requestFocus()
                return@setOnClickListener
            }

            if(! password.text.toString().matches("[a-zA-Z0-9]+".toRegex()))
            {
                password.error = "Password can only contain alphanumeric character"
                password.requestFocus()
                return@setOnClickListener
            }


            auth.signInWithEmailAndPassword(email.text.toString(), password.text.toString())
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithEmail:success")
                        val user = auth.currentUser
                        Log.d(TAG, user?.email ?: "")

                        startActivity(Intent(this, MainActivity::class.java))
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithEmail:failure", task.exception)
                        Toast.makeText(
                            baseContext,
                            "Authentication failed. Please enter valid credentials",
                            Toast.LENGTH_SHORT,
                        ).show()

                    }
                }



        }
    }
}