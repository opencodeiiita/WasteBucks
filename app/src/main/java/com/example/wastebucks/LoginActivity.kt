package com.example.wastebucks

import android.content.ContentValues.TAG
import android.content.Intent
import android.content.IntentSender
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {


    private lateinit var auth: FirebaseAuth

    // for google sign in

    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var googleSignInLauncher: ActivityResultLauncher<Intent>



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)



        supportActionBar?.hide()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)


        auth = Firebase.auth

        val currentUser = auth.currentUser
        Log.d("User123", "$currentUser")
        if (currentUser != null) {
            startActivity(Intent(this,MainActivity::class.java))
        }

        // get input from login here
        val email = findViewById<EditText>(R.id.logemail)
        val password = findViewById<EditText>(R.id.log_pwd)


        val loginButton = findViewById<TextView>(R.id.login_btn)
        val googleSignInButton = findViewById<LinearLayout>(R.id.linearLayout3)

        findViewById<TextView>(R.id.login).setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }



        // initializing google auth sign in launcher
        initializeGoogleSignInLauncher()

        // sign in with email password
        loginButton.setOnClickListener {
            if (email.text.toString().isEmpty()) {
                email.error = "Please Enter an email"
                email.requestFocus()
                return@setOnClickListener
            }

            if (!email.text.toString().contains("@")) {
                email.error = "Please enter a valid email"
                email.requestFocus()
                return@setOnClickListener
            }


            if (password.text.toString().isEmpty()) {
                password.error = "Please Enter a password"
                password.requestFocus()
                return@setOnClickListener
            }

            if (password.text.toString().length < 8) {
                password.error = "Password should be at least 8 characters long"
                password.requestFocus()
                return@setOnClickListener
            }

            if (!password.text.toString().matches("[a-zA-Z0-9]+".toRegex())) {
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

        // sign in with google one tap
        googleSignInButton.setOnClickListener {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_client_id))
                .requestEmail()
                .build()


            googleSignInClient = GoogleSignIn.getClient(this, gso)

            googleSignIn()
            Log.d(TAG,"5")

        }


    }

    private fun initializeGoogleSignInLauncher() {

        try {
            googleSignInLauncher =
                registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                    if (result.resultCode == RESULT_OK) {
                        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                        try {
                            val account = task.getResult(ApiException::class.java)!!
                            Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                            firebaseAuthWithGoogle(account.idToken!!)
                        } catch (e: ApiException) {
                            Log.w(TAG, "Google sign in failed ${e.message}", e)
                            Toast.makeText(this, "SignInWithGoogle:failed", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
        }
        catch(err: Exception)
        {
            Log.e("Big Error", err.message?:"null exception")
        }

    }
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user=  auth.currentUser

                    Log.d("User details", "$user")
                    startActivity(Intent(this, MainActivity::class.java))
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    Toast.makeText(this, "SignInWIthGoogle:Failure", Toast.LENGTH_SHORT).show()
                }
            }
    }
    private fun googleSignIn() {
        val signInIntent = googleSignInClient.signInIntent
        googleSignInLauncher.launch(signInIntent)
    }
    companion object {
        private const val TAG = "GoogleActivity"
        private const val RC_SIGN_IN = 9001
    }
}




