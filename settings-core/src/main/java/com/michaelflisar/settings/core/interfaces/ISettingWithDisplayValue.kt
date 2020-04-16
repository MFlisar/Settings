package com.michaelflisar.settings.core.interfaces

import com.michaelflisar.settings.core.classes.SettingsText

interface ISettingWithDisplayValue<T>: ISetting<T>  {

    fun getDisplayValue(): SettingsText
}