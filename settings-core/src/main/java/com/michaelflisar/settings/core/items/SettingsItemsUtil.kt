package com.michaelflisar.settings.core.items

import com.michaelflisar.settings.core.SettingsDisplaySetup
import com.michaelflisar.settings.core.classes.SettingsGroup
import com.michaelflisar.settings.core.interfaces.IBaseSetting

object SettingsItemsUtil {

    fun getItems(globalSettings: Boolean, settings: List<IBaseSetting>, setup: SettingsDisplaySetup, parent: BaseSettingsItem<*>? = null): List<BaseSettingsItem<*>> {
        val items = ArrayList<BaseSettingsItem<*>>()

        var index = 0
        for (setting in settings) {
            // SettingsItemHeader(null, index++, setting, setup)
            val item = setting.createSettingsItem(null, index++, setting, setup)
            if (setting is SettingsGroup) {
                item.subItems.addAll(getItems(globalSettings, setting.items, setup, item))
            }
            items.add(item)
        }

        return items
    }
}