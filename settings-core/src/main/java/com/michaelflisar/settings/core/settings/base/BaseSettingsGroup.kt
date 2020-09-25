package com.michaelflisar.settings.core.settings.base

import com.michaelflisar.settings.core.classes.SettingsCustomObject
import com.michaelflisar.settings.core.settings.SettingsGroup
import com.michaelflisar.settings.core.interfaces.ISetting
import com.michaelflisar.settings.core.interfaces.ISettingsIcon

abstract class BaseSettingsGroup<G: BaseSettingsGroup<G>> : ISetting<Unit>  {

    override var editable: Boolean = false
    override val clickable: Boolean = true

    override var numbering: List<Int> = emptyList()

    abstract val iconOpened: ISettingsIcon?
    protected abstract val items: ArrayList<ISetting<*>>

    fun getItems(): List<ISetting<*>> = items

    fun add(vararg item: ISetting<*>) {
        items.addAll(item)
    }

    @Suppress("UNCHECKED_CAST")
    fun addToGroup(group: SettingsGroup): G {
        group.add(this)
        return this as G
    }

    override fun readRealValue(element: SettingsCustomObject) {
        // empty
    }

    override fun readSetting(element: SettingsCustomObject) {
        // empty
    }

    override fun writeSetting(element: SettingsCustomObject, value: Unit) {
        // empty
    }

    override fun readSettingEnabled(element: SettingsCustomObject.Element): Boolean {
        return false
    }

    override fun writeSettingEnabled(element: SettingsCustomObject.Element, isEnabled: Boolean) {
        // empty
    }
}