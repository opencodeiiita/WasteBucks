package com.example.wastebucks

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class ContactUs : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_us)

        supportActionBar?.hide()
        supportActionBar?.setDisplayShowTitleEnabled(false)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        auth = FirebaseAuth.getInstance()
        progressDialog = ProgressDialog(this)
        progressDialog.setCanceledOnTouchOutside(false)

        val back = findViewById<ImageButton>(R.id.back)
        back.setOnClickListener {
            val intent = Intent(this@ContactUs, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        val name = findViewById<EditText>(R.id.name)
        val email = findViewById<EditText>(R.id.email)
        val msg = findViewById<EditText>(R.id.msg)
        val submit = findViewById<Button>(R.id.submit)

        submit.setOnClickListener {
            if(name.text.toString().isEmpty()) {
                name.error = "Please enter your name"
                name.requestFocus()
                return@setOnClickListener
            }
            else if(name.text.toString().contains("[0-9]".toRegex())) {
                name.error = "Name cannot contain digits"
                name.requestFocus()
                return@setOnClickListener
            }
            else if(name.text.toString().contains("[!@#$%^&*(),.?\":{}|<>]".toRegex())) {
                name.error = "Name cannot contain symbols"
                name.requestFocus()
                return@setOnClickListener
            }
            else if(email.text.toString().isEmpty()) {
                email.error = "Please enter your email"
                email.requestFocus()
                return@setOnClickListener
            }
            else if(!email.text.toString().contains("@")) {
                email.error = "Please enter a valid email"
                email.requestFocus()
                return@setOnClickListener
            }
            else if(msg.text.toString().isEmpty()) {
                msg.error = "Please enter your password"
                msg.requestFocus()
                return@setOnClickListener
            }
            else{
                progressDialog.setMessage("Sending Message...")
                val currentUser = auth.currentUser
                val uid = currentUser?.uid

                val name = findViewById<EditText>(R.id.name).text.toString()
                val email = findViewById<EditText>(R.id.email).text.toString()
                val msg = findViewById<EditText>(R.id.msg).text.toString()
                val hashMap: HashMap<String, String> = HashMap()
                hashMap["uid"] = uid.toString()
                hashMap["email"] = email
                hashMap["name"] = name
                hashMap["message"] = msg

                val ref = FirebaseDatabase.getInstance().getReference("Help")
                ref.child(uid.toString()).setValue(hashMap)
                    .addOnSuccessListener {
                        progressDialog.dismiss()
                        Toast.makeText(this, "Message Sent", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    .addOnFailureListener {
                        progressDialog.dismiss()
                        Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
                    }
            }
        }

    }
}