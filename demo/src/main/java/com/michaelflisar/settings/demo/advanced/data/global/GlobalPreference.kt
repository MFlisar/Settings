package com.michaelflisar.settings.demo.advanced.data.global

import android.graphics.Color
import com.michaelflisar.materialpreferences.core.SettingsModel
import com.michaelflisar.materialpreferences.datastore.DataStoreStorage

object GlobalPreference : SettingsModel(DataStoreStorage("global")) {
    val folderColor by intPref(Color.BLACK)
    val folderTag by stringPref("DEFAULT")
    val desktopBackgroundColor by intPref(Color.WHITE)
}