package com.michaelflisar.settings.core.interfaces

import com.michaelflisar.settings.core.enums.ChangeType

//typealias SettingsChangedCallback = (changeType: ChangeType, setting: ISetting<*>, settingsDataHolder: SettingsDataHolder, oldValue: Any?, newValue: Any?) -> Unit
interface ISettingsChangedCallback {
    fun onSettingChanged(changeType: ChangeType, setting: ISetting<*>, settingsData: ISettingsData, oldValue: Any?, newValue: Any?)
}