package com.example.wastebucks

import android.content.ContentValues
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
import com.google.android.gms.auth.api.identity.BeginSignInResult
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {


    private lateinit var auth: FirebaseAuth

    // for google sign in

    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var googleSignInLauncher: ActivityResultLauncher<IntentSenderRequest>
    private lateinit var oneTapClient: SignInClient
    private lateinit var signInRequest: BeginSignInRequest


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



        // initializing google auth sign in launcher and one tap client(need to do it before complete creation)
        initializeLauncherAndClient()



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
            createGoogleSignInRequest()
            initiateGoogleSignIn()

            Log.d(TAG,"5")

        }


    }

    private fun initializeLauncherAndClient() {
        oneTapClient = Identity.getSignInClient(this)
            googleSignInLauncher =
                registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result: ActivityResult ->
                    if (result.resultCode == RESULT_OK) {
                        try {
                            val credential =
                                oneTapClient.getSignInCredentialFromIntent(result.data)
                            val idToken = credential.googleIdToken
                            if (idToken != null) {
                                val firebaseCredential =
                                    GoogleAuthProvider.getCredential(idToken, null)
                                auth.signInWithCredential(firebaseCredential)
                                    .addOnCompleteListener(this
                                    ) { task ->
                                        if (task.isSuccessful) {
                                            // Sign in success, update UI with the signed-in user's information
                                            Log.d("Google Activity", "signInWithCredential:success")
                                            val user: FirebaseUser? = auth.currentUser
                                            Toast.makeText(
                                                this@LoginActivity,
                                                "Authentication Success.",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            startActivity(
                                                Intent(
                                                    this,
                                                    MainActivity::class.java
                                                )
                                            )
                                        } else {
                                            // If sign in fails, display a message to the user.
                                            Log.w(
                                                "Google Activity",
                                                "signInWithCredential:failure",
                                                task.exception
                                            )
                                        }
                                    }
                            }
                        } catch (e: ApiException) {
                            Log.e("Google Activity", "Api exception in google auth:${e.message}")
                        }
                    }
                }


    }
    private fun createGoogleSignInRequest(){
        signInRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(getString(R.string.web_client_id))
                    .setFilterByAuthorizedAccounts(false)
                    .build()
            )
            .setAutoSelectEnabled(true)
            .build()
    }
    private fun initiateGoogleSignIn() {
        oneTapClient.beginSignIn(signInRequest)
            .addOnSuccessListener{ result ->
                googleSignInLauncher.launch(
                IntentSenderRequest.Builder(
                    result.pendingIntent.intentSender
                ).build()
            )
            }
            .addOnFailureListener(this
            ) { e -> // No Google Accounts found. Just continue presenting the signed-out UI.
                e.localizedMessage?.let { Log.d("Google Not Found", it) }
            }
    }

    companion object {
        private const val TAG = "GoogleActivity"
        private const val RC_SIGN_IN = 9001
    }
}




