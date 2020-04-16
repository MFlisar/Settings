package com.michaelflisar.settings.core.interfaces

import android.os.Parcelable
import com.michaelflisar.settings.core.SettingsDisplaySetup
import com.michaelflisar.settings.core.classes.SettingsText
import com.michaelflisar.settings.core.items.BaseSettingsItem

interface IBaseSetting : Parcelable {

    val id: Long
    val label: SettingsText
    val info: SettingsText?
    val icon: ISettingsIcon?

    fun createSettingsItem(parent: BaseSettingsItem<*>?, index: Int, setting: IBaseSetting, setup: SettingsDisplaySetup): BaseSettingsItem<*>
}