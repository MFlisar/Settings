package com.michaelflisar.settings.demo

import android.app.Application
import android.content.Context
import com.michaelflisar.settings.core.SettingsManager
import com.michaelflisar.settings.storage.datastorepreferences.DataStorePrefSettings

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        // -------------
        // Init settings
        // -------------

        // 1) our StorageManager is the DataStorePrefsSetting - so we create one here...
        val storageManager = DataStorePrefSettings(this, "demo_data_store")

        // 2) ... and then we initialise the SettingsManager with the StorageManager it should use
        SettingsManager.init(this, storageManager)

        // -------------
        // Init some initial custom settings
        // -------------

        SettingsDefs.onAppStart()
    }
}