package com.michaelflisar.settings.demo.advanced.data.global

import android.graphics.Color
import com.chibatching.kotpref.KotprefModel

object GlobalPreference : KotprefModel() {

    // KotPref Setup
    override val commitAllPropertiesByDefault = true

    // Preferences
    var folderColor by intPref(Color.BLACK)
    var desktopBackgroundColor by intPref(Color.WHITE)
}