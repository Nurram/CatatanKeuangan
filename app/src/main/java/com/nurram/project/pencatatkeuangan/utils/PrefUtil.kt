package com.nurram.project.pencatatkeuangan.utils

import android.content.Context
import android.content.SharedPreferences

class PrefUtil(val context: Context) {

    private var privateName = 0
    private val prefName = "financial-records"

    private var sharedPref: SharedPreferences =
        context.getSharedPreferences(prefName, privateName)

    fun saveToPref(key: String, value: Int) {
        sharedPref.edit().putInt(key, value).apply()
    }

    fun saveToPref(key: String, value: String) {
        sharedPref.edit().putString(key, value).apply()
    }

    fun getIntFromPref(key: String): Int = sharedPref.getInt(key, 2)
    fun getStringFromPref(key: String, default: String): String =
        sharedPref.getString(key, default)!!
}