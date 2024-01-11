package com.example.wastebucks

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import com.example.wastebucks.fragments.onboarding1
import com.example.wastebucks.fragments.onboarding2
import com.example.wastebucks.fragments.onboarding3
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.wastebucks.admin.AdminScreen
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class OnboardingActivity : AppCompatActivity() {

    private var auth: FirebaseAuth = Firebase.auth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

        supportActionBar?.hide()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        val fragmentList = arrayListOf(
            onboarding1(),
            onboarding2(),
            onboarding3()
        )

        val adapter = ViewPagerAdapter(
            fragmentList,
            this.supportFragmentManager,
            lifecycle
        )

        val viewPager = findViewById<ViewPager2>(R.id.viewPager2)
        viewPager.adapter = adapter

        val indicator = findViewById<com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator>(R.id.dots_indicator)
        indicator.attachTo(viewPager)

        val next: Button = findViewById(R.id.btn_next)
        next.setOnClickListener() {
            if (viewPager.currentItem + 1 < fragmentList.size) {
                viewPager.currentItem += 1
            } else {
                startActivity(Intent(this, LoginActivity::class.java))
            }
        }
    }
}

class ViewPagerAdapter(private val fragmentList: ArrayList<Fragment>, fm: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fm, lifecycle) {
    override fun getItemCount(): Int {
        return fragmentList.size
    }
    override fun createFragment(position: Int): Fragment {
        return fragmentList[position]
    }
}