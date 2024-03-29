package com.nurram.project.pencatatkeuangan.view.activity.dark

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.nurram.project.pencatatkeuangan.R
import com.nurram.project.pencatatkeuangan.databinding.ActivityDarkOptionsBinding
import com.nurram.project.pencatatkeuangan.utils.PrefUtil

class DarkOptionsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityDarkOptionsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.darkToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.dark_mode)

        val sharedPref = PrefUtil(this)
        binding.apply {
            when (sharedPref.getIntFromPref("dark")) {
                0 -> darkOn.isChecked = true
                1 -> darkOff.isChecked = true
                2 -> darkSystem.isChecked = true
            }

            darkOn.setOnClickListener {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                sharedPref.saveToPref("dark", 0)
            }
            darkOff.setOnClickListener {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                sharedPref.saveToPref("dark", 1)
            }
            darkSystem.setOnClickListener {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                sharedPref.saveToPref("dark", 2)
            }

            MobileAds.initialize(this@DarkOptionsActivity) { }
            val adRequest = AdRequest.Builder().build()
            adView.loadAd(adRequest)
        }
    }
}