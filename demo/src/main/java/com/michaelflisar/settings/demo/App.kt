package com.michaelflisar.settings.demo

import android.app.Application
import com.chibatching.kotpref.Kotpref
import com.michaelflisar.settings.demo.advanced.DBManager
import com.michaelflisar.settings.demo.simple.SettingsDefs

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        // necessary for advanced example
        Kotpref.init(this)
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