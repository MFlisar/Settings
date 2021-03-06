package com.michaelflisar.settings.core.internal

import com.michaelflisar.settings.core.classes.SettingsDependency
import com.michaelflisar.settings.core.classes.SettingsDisplaySetup
import com.michaelflisar.settings.core.classes.SettingsMetaData
import com.michaelflisar.settings.core.enums.SupportType
import com.michaelflisar.settings.core.interfaces.ISetting
import com.michaelflisar.settings.core.interfaces.ISettingsData
import com.michaelflisar.settings.core.interfaces.ISettingsItem
import com.michaelflisar.settings.core.settings.SettingsGroup
import com.michaelflisar.text.asText

internal object SettingsItemsUtil {

    fun getTopLevelGroups(settingsData: ISettingsData, settings: List<ISetting<*>>, addGroupForItemsWithoutGroups: Boolean, dependencies: List<SettingsDependency<*>>, setup: SettingsDisplaySetup): List<SettingsGroup> {

        val items = getItems(settingsData, settings, dependencies, setup, null)

        val groups = ArrayList<SettingsGroup>()
        groups.addAll(
                items
                        .filter { it.item is SettingsGroup }
                        .map { it.item as SettingsGroup }
        )

        if (addGroupForItemsWithoutGroups) {
            val itemsWithoutGroups = items.filter { it.item !is SettingsGroup }
            if (itemsWithoutGroups.isNotEmpty()) {
                val noGroupGroup = SettingsGroup(
                        -1L,
                        setup.noGroupTitle.asText(),
                        null,
                        null,
                        null
                )
                noGroupGroup.add(*itemsWithoutGroups.map { it.item }.toTypedArray())
                groups.add(noGroupGroup)
            }
        }

        return groups
    }

    fun getItems(settingsData: ISettingsData, settings: List<ISetting<*>>, dependencies: List<SettingsDependency<*>>, setup: SettingsDisplaySetup, parent: ISettingsItem<*, *, *>?): List<ISettingsItem<*, *, *>> {
        val items = ArrayList<ISettingsItem<*, *, *>>()

        var index = 0
        for (setting in settings) {
            // SettingsItemHeader(null, index++, setting, setup)
            val item = setting.createSettingsItem(parent, index++, getMetaDataForItem(setting, dependencies), settingsData, setup)
            if (setting is SettingsGroup) {
                item.addSubItems(getItems(settingsData, setting.getItems(), dependencies, setup, item))
            }
            items.add(item)
        }

        // 1) filter out invalid settings based on setup
        filterItems(items) {
            isSettingsTypeValid(settingsData, it)
        }

        // 2) filter out empty groups
        if (!setup.showEmptyGroups) {
            filterItems(items) {
                it.item !is SettingsGroup || it.subItems.size > 0
            }
        }

        return items
    }

    // ------------------------
    // private helper functions
    // ------------------------

    private fun getMetaDataForItem(setting: ISetting<*>, dependencies: List<SettingsDependency<*>>): SettingsMetaData {
        return SettingsMetaData(
                dependencies.filter { it.childThatDependsOnParent == setting }
        )
    }

    private fun filterItems(items: MutableList<ISettingsItem<*, *, *>>, predicate: ((ISettingsItem<*, *, *>) -> Boolean)) {
        val iter = items.iterator()
        while (iter.hasNext()) {
            val item = iter.next()
            if (item.item is SettingsGroup) {
                filterItems(item.subBaseItems, predicate)
            }
            if (!predicate(item)) {
                iter.remove()
            }
        }
    }

    private fun isSettingsTypeValid(settingsData: ISettingsData, setting: ISettingsItem<*, *, *>): Boolean {
        return isSettingsTypeValid(settingsData, setting.item)
    }

    private fun isSettingsTypeValid(settingsData: ISettingsData, setting: ISetting<*>): Boolean {
        if (!setting.valid) {
            return false
        }
        return if (settingsData.isGlobal) {
            setting.supportType == SupportType.All || setting.supportType == SupportType.GlobalOnly
        } else {
            setting.supportType == SupportType.All || setting.supportType == SupportType.CustomOnly
        }
    }
}