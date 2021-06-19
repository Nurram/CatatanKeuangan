package com.nurram.project.pencatatkeuangan.view.activity.dark

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.nurram.project.pencatatkeuangan.databinding.ActivityDarkOptionsBinding
import com.nurram.project.pencatatkeuangan.utils.PrefUtil

class DarkOptionsActivity : AppCompatActivity() {
    companion object {
        fun getIntent(context: Context) = Intent(context, DarkOptionsActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityDarkOptionsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.darkToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

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
        }
    }
}