package com.example.wastebucks

import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.example.wastebucks.databinding.ActivityMainBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    //Google
    private lateinit var mGoogleSignInClient: GoogleSignInClient

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        supportActionBar?.setDisplayShowTitleEnabled(false)


        drawerLayout = findViewById(R.id.drawer_layout)

        navigationView = findViewById(R.id.nav_view)

        toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            findViewById(R.id.toolbar),
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )

        drawerLayout.addDrawerListener(toggle)
        toggle.drawerArrowDrawable.color = ContextCompat.getColor(this, android.R.color.white)


        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        val uid = currentUser?.uid
        val userRef = FirebaseDatabase.getInstance().getReference("Users").child(uid.toString())

        val navigationView: NavigationView = findViewById(R.id.nav_view)
        val headerView: View = navigationView.getHeaderView(0)
        val navHeaderName: TextView = headerView.findViewById(R.id.name_nav)
        val navHeaderEmail: TextView = headerView.findViewById(R.id.email_nav)

        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val userName = snapshot.child("name").value.toString()
                    val userEmail = snapshot.child("email").value.toString()
                    navHeaderName.text = userName
                    navHeaderEmail.text = userEmail
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity, "Failed to retrieve user data", Toast.LENGTH_SHORT).show()
            }
        })



        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    Toast.makeText(applicationContext, "Home clicked", Toast.LENGTH_SHORT).show()
                    drawerLayout.closeDrawer(GravityCompat.START)
                    return@setNavigationItemSelectedListener true

                    true
                }
                R.id.nav_profile -> {
                    Toast.makeText(applicationContext, "Profile clicked", Toast.LENGTH_SHORT).show()
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                R.id.nav_settings -> {
                    Toast.makeText(applicationContext, "Settings clicked", Toast.LENGTH_SHORT).show()
                    drawerLayout.closeDrawer(GravityCompat.START)
                    drawerLayout.closeDrawers()
                    true
                }
                R.id.nav_notifications -> {
                    Toast.makeText(applicationContext, "Notifications clicked", Toast.LENGTH_SHORT).show()
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                R.id.nav_help -> {
                    Toast.makeText(applicationContext, "Help clicked", Toast.LENGTH_SHORT).show()
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                R.id.nav_logout -> {
                    signOutAndStartSignInActivity()
                    Toast.makeText(applicationContext, "Logged Out", Toast.LENGTH_SHORT).show()
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                R.id.nav_contactus -> {
                    val intent = Intent(this@MainActivity, ContactUs::class.java)
                    startActivity(intent)
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                R.id.nav_rate -> {
                    val intent = Intent(this@MainActivity, RateUs::class.java)
                    startActivity(intent)
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                R.id.nav_about -> {
                    val intent = Intent(this@MainActivity, AboutUs::class.java)
                    startActivity(intent)
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                else -> false
            }
            true
        }


        binding.leaderboardButton.setOnClickListener {
            startActivity(android.content.Intent(this, LeaderboardActivity::class.java))
        }

        binding.shoppingButton.setOnClickListener {
            startActivity(android.content.Intent(this, ShoppingActivity::class.java))
        }
        replaceFragment(Home())


        //Google
        mAuth = FirebaseAuth.getInstance()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)



        binding.bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> replaceFragment(Home())
                R.id.community -> replaceFragment(Community())
                R.id.pickup -> replaceFragment(Pickup())
                R.id.profile -> replaceFragment(Profile())
                else -> {}
            }
            true
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        toggle.syncState()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        toggle.onConfigurationChanged(newConfig)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                drawerLayout.openDrawer(GravityCompat.START)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun signOutAndStartSignInActivity() {
        mAuth.signOut()

        mGoogleSignInClient.signOut().addOnCompleteListener(this) {
            // Optional: Update UI or show a message to the user
            val intent = Intent(this@MainActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

}