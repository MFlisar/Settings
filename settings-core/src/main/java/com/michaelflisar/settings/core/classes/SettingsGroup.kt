package com.michaelflisar.settings.core.classes

import com.michaelflisar.settings.core.SettingsDisplaySetup
import com.michaelflisar.settings.core.interfaces.IBaseSetting
import com.michaelflisar.settings.core.interfaces.ISettingsGroup
import com.michaelflisar.settings.core.interfaces.ISettingsIcon
import com.michaelflisar.settings.core.items.BaseSettingsItem
import com.michaelflisar.settings.core.items.SettingsItemHeader
import kotlinx.android.parcel.Parcelize

@Parcelize
class SettingsGroup(
        override val id: Long,
        override val label: SettingsText,
        override val info: SettingsText?,
        override val icon: ISettingsIcon?,
        override val items: ArrayList<IBaseSetting> = ArrayList()
) : ISettingsGroup {

    override fun createSettingsItem(parent: BaseSettingsItem<*>?, index: Int, setting: IBaseSetting, setup: SettingsDisplaySetup): BaseSettingsItem<*> {
        return SettingsItemHeader(parent, index, setting, setup)
    }

}