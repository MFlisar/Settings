package com.michaelflisar.settings.core.interfaces

import android.os.Parcelable
import com.michaelflisar.settings.core.classes.SettingsDisplaySetup

interface ISettingsFilter : Parcelable {
    fun transformFilter(filter: String?) : String
    fun isValid(filter: String, item: ISetting<*>): Boolean
    fun highlight(label: Boolean, text: String, filter: String, setup: SettingsDisplaySetup): CharSequence
}