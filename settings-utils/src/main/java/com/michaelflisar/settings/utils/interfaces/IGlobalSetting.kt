package com.michaelflisar.settings.utils.interfaces

import com.michaelflisar.settings.core.interfaces.ISetting
import com.michaelflisar.settings.utils.SettingsData

interface IGlobalSetting<ValueType, Item> : ISetting<ValueType> {

    val globalItem: Item

    val globalReadFunc: (Item.() -> ValueType)
    val globalWriteFunc: (Item.(ValueType) -> Boolean)

    val onAfterGlobalWrite: ((item: Item) -> Unit)?

    fun readGlobal(globalData: SettingsData.Global): ValueType = globalItem.globalReadFunc()
    fun writeGlobal(globalData: SettingsData.Global, value: ValueType): Boolean {
        val success = globalItem.globalWriteFunc(value)
        onAfterGlobalWrite?.invoke(globalItem)
        return success
    }
}