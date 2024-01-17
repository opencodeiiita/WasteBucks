package com.example.wastebucks

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import com.example.wastebucks.admin.AdminScreen
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    //G
    companion object {
        private const val RC_SIGN_IN = 9001
    }

    private lateinit var auth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        supportActionBar?.hide()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        auth = FirebaseAuth.getInstance()
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please Wait")
        progressDialog.setCanceledOnTouchOutside(false)

        val forgotPassword = findViewById<TextView>(R.id.forgot_password)
        forgotPassword.setOnClickListener {
            val dialog = AlertDialog.Builder(this)
            dialog.setTitle("Forgot Password")
            dialog.setMessage("Enter your email address.")

            val inputField = EditText(this)
            dialog.setView(inputField)

            dialog.setPositiveButton("Send") { _, _ ->
                val email = inputField.text.toString()
                auth.sendPasswordResetEmail(email)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "We have sent you instructions to reset your password!", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, "Failed to send reset email!", Toast.LENGTH_SHORT).show()
                        }
                    }
            }

            dialog.setNegativeButton("Cancel") { dialogInterface, _ ->
                dialogInterface.cancel()
            }

            dialog.create().show()
        }

        // get input from login here
        val email = findViewById<EditText>(R.id.logemail)
        val password = findViewById<EditText>(R.id.log_pwd)


        val loginButton = findViewById<TextView>(R.id.login_btn)

        //Google
        val signInButton = findViewById<LinearLayout>(R.id.linearLayout3)
        signInButton.setOnClickListener {
            signIn()
        }


        findViewById<TextView>(R.id.login).setOnClickListener{
            startActivity(Intent( this, RegisterActivity::class.java))
        }

        loginButton.setOnClickListener{
            val email = findViewById<EditText>(R.id.logemail)
            val password = findViewById<EditText>(R.id.log_pwd)

            if(email.text.toString().isEmpty())
            {
                email.error = "Please Enter an email"
                email.requestFocus()
                return@setOnClickListener
            }
            else if(!email.text.toString().contains("@")) {
                email.error = "Please enter a valid email"
                email.requestFocus()
                return@setOnClickListener
            }
            else if(password.text.toString().isEmpty())
            {
                password.error = "Please Enter a password"
                password.requestFocus()
                return@setOnClickListener
            }
            else if(password.text.toString().length < 8) {
                password.error = "Password should be at least 8 characters long"
                password.requestFocus()
                return@setOnClickListener
            }
            else if(! password.text.toString().matches("[a-zA-Z0-9]+".toRegex()))
            {
                password.error = "Password can only contain alphanumeric character"
                password.requestFocus()
                return@setOnClickListener
            }
            else{
                loginUser()
            }
        }
    }

    private fun loginUser() {
        progressDialog.setMessage("Logging In...")
        progressDialog.show()
        val email = findViewById<EditText>(R.id.logemail)
        val password = findViewById<EditText>(R.id.log_pwd)

        auth.signInWithEmailAndPassword(email.text.toString(), password.text.toString())
            .addOnSuccessListener(this) {
                checkUser()
            }
            .addOnFailureListener(this){
                progressDialog.dismiss()
                Toast.makeText(this, "Login Failed", Toast.LENGTH_SHORT).show()
            }
    }

    private fun checkUser() {
        progressDialog.setMessage("Checking User...")

        val firebaseUser = auth.currentUser
        val ref = FirebaseDatabase.getInstance().getReference("Users")

        ref.child(firebaseUser!!.uid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                progressDialog.dismiss()
                val userName = snapshot.child("name").getValue(String::class.java)
                val userType = snapshot.child("userType").getValue(String::class.java)

                if (userType == "user") {
                    Toast.makeText(this@LoginActivity, "Signed in as $userName", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                else if(userType == "admin"){
                    val txt = "Welcome Admin<br/>Signed in as $userName"
                    Toast.makeText(this@LoginActivity, Html.fromHtml(txt), Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@LoginActivity, AdminScreen::class.java)
                    startActivity(intent)
                    finish()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle onCancelled event
            }
        })
    }

    private fun signIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(this, gso)
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Toast.makeText(this, "Google sign in failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val uid = user?.uid
                    val timestamp: String = System.currentTimeMillis().toString()
                    val hashMap: HashMap<String, String> = HashMap()
                    hashMap["uid"] = uid.toString()
                    hashMap["email"] = user?.email.toString()
                    hashMap["name"] = user?.displayName.toString()
                    hashMap["profileImage"] = ""
                    hashMap["userType"] = "user"
                    hashMap["points"] = "0"
                    hashMap["timestamp"] = timestamp

                    val ref = FirebaseDatabase.getInstance().getReference("Users")
                    ref.child(uid.toString()).setValue(hashMap)
                        .addOnSuccessListener {
                            progressDialog.dismiss()
                            Toast.makeText(this, "Login Success...", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                        .addOnFailureListener {
                            progressDialog.dismiss()
                            Toast.makeText(this, "SignIn Failed", Toast.LENGTH_SHORT).show()
                        }
                }
                else {
                    Toast.makeText(this, "Authentication failed", Toast.LENGTH_SHORT).show()
                }
            }
    }
}