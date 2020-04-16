package com.michaelflisar.settings.sharedpreferences

import android.content.Context
import android.content.SharedPreferences

object SharedPrefSettingsManager {

    lateinit var sharedPreferences: SharedPreferences
        private set

    fun init(context: Context, sharedPreferencesName: String) {
        this.sharedPreferences = context.applicationContext.getSharedPreferences(sharedPreferencesName, Context.MODE_PRIVATE)
    }

}