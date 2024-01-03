package com.example.wastebucks

import android.content.Intent
import android.health.connect.datatypes.units.Length
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import android.widget.TextView
import android.widget.EditText
import com.google.firebase.auth.FirebaseAuth
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.ktx.Firebase

class RegisterActivity : AppCompatActivity() {

    private var auth :FirebaseAuth = Firebase.auth
    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if (currentUser != null) {

            Toast.makeText(this, currentUser.email.toString(), Toast.LENGTH_LONG).show()
            startActivity(Intent(this, MainActivity::class.java))
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        supportActionBar?.hide()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        //sampleopencode23@gmail.com
        //Opencode123

        // get input from register form
        val name = findViewById<EditText>(R.id.name)
        val email = findViewById<EditText>(R.id.email)
        val password = findViewById<EditText>(R.id.pwd)
        val confirmPassword = findViewById<EditText>(R.id.cpwd)

        // get button from register form
        val registerButton = findViewById<TextView>(R.id.nxtbtn)

        findViewById<TextView>(R.id.login).setOnClickListener{
            startActivity(Intent(this, LoginActivity::class.java))
        }

        registerButton.setOnClickListener {
            // No fields can be left blank
            //Both password should match and should be 8 digit longs and combinations of alphanumeric
            //Gmail entered should be valid by the user and should contains @ check condition
            //Name cannot contains symbols and digits.
            //Implement Register Page to store data in Firebase. Firebase email id and password has been provided in RegisterActivity.kt file just use it and store all the new register data in that database and if the user is already registered, redirect them to the LoginActivity.

            if(name.text.toString().isEmpty()) {
                name.error = "Please enter your name"
                name.requestFocus()
                return@setOnClickListener
            }
            if(name.text.toString().contains("[0-9]".toRegex())) {
                name.error = "Name cannot contain digits"
                name.requestFocus()
                return@setOnClickListener
            }
            if(name.text.toString().contains("[!@#$%^&*(),.?\":{}|<>]".toRegex())) {
                name.error = "Name cannot contain symbols"
                name.requestFocus()
                return@setOnClickListener
            }
            if(email.text.toString().isEmpty()) {
                email.error = "Please enter your email"
                email.requestFocus()
                return@setOnClickListener
            }
            if(!email.text.toString().contains("@")) {
                email.error = "Please enter a valid email"
                email.requestFocus()
                return@setOnClickListener
            }
            if(password.text.toString().isEmpty()) {
                password.error = "Please enter your password"
                password.requestFocus()
                return@setOnClickListener
            }
            if(password.text.toString().length < 8) {
                password.error = "Password should be at least 8 characters long"
                password.requestFocus()
                return@setOnClickListener
            }
            if(confirmPassword.text.toString().isEmpty()) {
                confirmPassword.error = "Please confirm your password"
                confirmPassword.requestFocus()
                return@setOnClickListener
            }
            if(password.text.toString() != confirmPassword.text.toString()) {
                confirmPassword.error = "Passwords do not match"
                confirmPassword.requestFocus()
                return@setOnClickListener
            }

            val profileUpdates = userProfileChangeRequest {
                displayName = name.text.toString()
            }


            auth.createUserWithEmailAndPassword(email.text.toString(), password.text.toString())
                .addOnCompleteListener(this) { task ->
                    if(task.isSuccessful) {
                        Log.d("RegisterActivity", "createUserWithEmail:success")
                        val user = auth.currentUser
                        user!!.updateProfile(profileUpdates)
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Log.w("RegisterActivity", "createUserWithEmail:failure", task.exception)
                        if (task.exception is com.google.firebase.auth.FirebaseAuthUserCollisionException) {
                            val intent = Intent(this, LoginActivity::class.java)
                            startActivity(intent)
                        }
                        else {
                            email.error = "Please enter a valid email"
                            email.requestFocus()
                        }
                    }
                }
        }
    }
}