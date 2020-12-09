package com.michaelflisar.settings.utils.interfaces

import com.michaelflisar.settings.core.enums.ChangeType
import com.michaelflisar.settings.core.interfaces.ISetting
import com.michaelflisar.settings.utils.SettingsData

interface ICustomSetting<ValueType, Item> : ISetting<ValueType> {

    val customReadFunc: (Item.() -> ValueType)
    val customWriteFunc: (Item.(ValueType) -> Boolean)

    val customReadIsEnabledFunc: (Item.() -> Boolean)
    val customWriteIsEnabledFunc: (Item.(Boolean) -> Boolean)

    val onAfterCustomWrite: ((item: Item, change: ChangeType) -> Unit)?

    fun readCustom(item: Item) = item.customReadFunc()
    fun writeCustom(item: Item, value: ValueType): Boolean {
        val success = item.customWriteFunc(value)
        onAfterCustomWrite?.invoke(item, ChangeType.CustomValue)
        return success
    }

    fun readIsCustomEnabled(item: Item) = item.customReadIsEnabledFunc()
    fun writeIsCustomEnabled(item: Item, value: Boolean): Boolean {
        val success = item.customWriteIsEnabledFunc(value)
        onAfterCustomWrite?.invoke(item, ChangeType.CustomIsEnabled)
        return success
    }

    fun getCustomItem(settingsData: SettingsData.Custom): Item

    fun readCustom(settingsData: SettingsData.Custom) = readCustom(getCustomItem(settingsData))
    fun writeCustom(settingsData: SettingsData.Custom, value: ValueType) = writeCustom(getCustomItem(settingsData), value)
    fun readIsCustomEnabled(settingsData: SettingsData.Custom) = readIsCustomEnabled(getCustomItem(settingsData))
    fun writeIsCustomEnabled(settingsData: SettingsData.Custom, value: Boolean) = writeIsCustomEnabled(getCustomItem(settingsData), value)
}