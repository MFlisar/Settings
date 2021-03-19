package com.michaelflisar.settings.demo

import android.app.Application
import com.michaelflisar.settings.demo.advanced.DBManager
import com.michaelflisar.settings.storage.datastorepreferences.DataStorePrefSettings

class App : Application() {

    companion object {
        // DataStore forces you to use a singleton!
        val DEMO_PREFS_MANAGER by lazy { DataStorePrefSettings("demo_data_store") }
    }

    override fun onCreate() {
        super.onCreate()

        // necessary for advanced example
        DBManager.init(this)

        // -------------
        // Init settings
        // -------------

        // this is done in the MainActivity dependent on which demo is selected... normally this could be done here

        // -------------
        // Init some initial custom settings
        // -------------


    }
}