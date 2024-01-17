package com.example.wastebucks

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Profile : Fragment() {

    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_profile, container, false)


        val name: TextView = view.findViewById(R.id.textView2)
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        val uid = currentUser?.uid

        val userRef = FirebaseDatabase.getInstance().getReference("Users").child(uid.toString())
        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val userName = snapshot.child("name").value.toString()
                    name.text = userName
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Failed to retrieve user data", Toast.LENGTH_SHORT).show()
            }
        })

        return view
    }
}


