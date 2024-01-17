package com.example.wastebucks

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class RateUs : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rate_us)
        supportActionBar?.hide()
        supportActionBar?.setDisplayShowTitleEnabled(false)

        auth = FirebaseAuth.getInstance()
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please Wait")
        progressDialog.setCanceledOnTouchOutside(false)

        val ratingbar = findViewById<RatingBar>(R.id.ratingbar)
        val txt = findViewById<TextView>(R.id.textView1)
        val submit = findViewById<TextView>(R.id.textView2)

        val currentUser = auth.currentUser
        val uid = currentUser?.uid
        val ratingsRef = FirebaseDatabase.getInstance().getReference("Ratings").child(uid.toString())


        ratingsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val userRating = snapshot.child("rating").value.toString().toFloat()
                    ratingbar.rating = userRating
                    txt.text = userRating.toString()

                    when (userRating.toInt()) {
                        1 -> txt.text = "Very Bad"
                        2 -> txt.text = "Bad"
                        3 -> txt.text = "Good"
                        4 -> txt.text = "Great"
                        5 -> txt.text = "Awesome"
                        else -> txt.text = " "
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@RateUs, "Failed to retrieve rating", Toast.LENGTH_SHORT).show()
            }
        })

        ratingbar.setOnRatingBarChangeListener { _, rating, _ ->
            txt.text = rating.toString()
            when (rating.toInt()) {
                1 -> txt.text = "Very Bad"
                2 -> txt.text = "Bad"
                3 -> txt.text = "Good"
                4 -> txt.text = "Great"
                5 -> txt.text = "Awesome"
                else -> txt.text = " "
            }
        }

        submit.setOnClickListener {
            val message = ratingbar.rating.toString()
            progressDialog.setMessage("Please Wait...")
            val currentUser = auth.currentUser
            val uid = currentUser?.uid
            val hashMap: HashMap<String, String> = HashMap()
            hashMap["uid"] = uid.toString()
            hashMap["rating"] = message
            val ref = FirebaseDatabase.getInstance().getReference("Ratings")
            ref.child(uid.toString()).setValue(hashMap)
                .addOnSuccessListener {
                    progressDialog.dismiss()
                    Toast.makeText(this, "Thanks for rating", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@RateUs, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                .addOnFailureListener {
                    progressDialog.dismiss()
                    Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
                }
        }

        val back = findViewById<ImageButton>(R.id.back)
        back.setOnClickListener {
            val intent = Intent(this@RateUs, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        val later = findViewById<TextView>(R.id.textView3)
        later.setOnClickListener {
            val intent = Intent(this@RateUs, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

    }
}