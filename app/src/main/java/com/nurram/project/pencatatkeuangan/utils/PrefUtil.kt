package com.nurram.project.pencatatkeuangan.utils

import android.content.Context
import android.content.SharedPreferences

class PrefUtil(val context: Context) {
    private var PRIVATE_MODE = 0
    private val PREF_NAME = "financial-records"

    private var sharedPref: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, PRIVATE_MODE)

    fun saveToPref(key: String, value: Int) {
        sharedPref.edit().putInt(key, value).apply()
    }

    fun getFromPref(key: String) = sharedPref.getInt(key, 2)
}