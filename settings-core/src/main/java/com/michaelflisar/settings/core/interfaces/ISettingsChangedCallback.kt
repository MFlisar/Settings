package com.michaelflisar.settings.core.interfaces

import com.michaelflisar.settings.core.classes.SettingsCustomObject

interface ISettingsChangedCallback {
    fun onSettingChanged(setting: ISetting<*>, customItem: SettingsCustomObject, oldValue: Any?, newValue: Any?)
    fun onCustomEnabledChanged(setting: ISetting<*>, customItem: SettingsCustomObject.Element, oldValue: Boolean, newValue: Boolean)
}