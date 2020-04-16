package com.michaelflisar.settings.sharedpreferences

import com.michaelflisar.settings.core.SettingsDisplaySetup
import com.michaelflisar.settings.core.classes.SettingsText
import com.michaelflisar.settings.core.interfaces.IBaseSetting
import com.michaelflisar.settings.core.interfaces.ISetting
import com.michaelflisar.settings.core.interfaces.ISettingsIcon
import com.michaelflisar.settings.core.items.BaseSettingsItem
import com.michaelflisar.settings.core.items.SettingsItemHeader
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize

@Parcelize
class BooleanPrefSetting(
        override val parent: IBaseSetting,
        override val id: Long,
        override val label: SettingsText,
        override val info: SettingsText?,
        override val icon: ISettingsIcon?,
        val defaultValue: Boolean
) :  ISetting<Boolean> {

    override fun read(): Boolean {
        return SharedPrefSettingsManager.sharedPreferences.getBoolean(id.toString(), defaultValue)
    }

    override fun save(value: Boolean) {
        SharedPrefSettingsManager.sharedPreferences
                .edit()
                .putBoolean(id.toString(), value)
                .apply()
    }

    override fun createSettingsItem(parent: BaseSettingsItem<*>?, index: Int, setting: IBaseSetting, setup: SettingsDisplaySetup): BaseSettingsItem<*> {
        return SettingsItemHeader(parent, index, setting, setup)
    }
}