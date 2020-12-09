package com.michaelflisar.settings.core.settings.base

import com.michaelflisar.settings.core.enums.ChangeType
import com.michaelflisar.settings.core.interfaces.ISetting
import com.michaelflisar.settings.core.interfaces.ISettingsData
import com.michaelflisar.settings.core.interfaces.ISettingsIcon
import com.michaelflisar.settings.core.settings.SettingsGroup

abstract class BaseSettingsGroup<G : BaseSettingsGroup<G>> : ISetting<Unit> {

    override val canHoldData = false // must be false for Unit Types!

    override val onValueChanged: ((settingsData: ISettingsData, changeType: ChangeType) -> Unit)? = null

    override val valid: Boolean = true

    override val editable: Boolean = false
    override val clickable: Boolean = true

    override var numbering: List<Int> = emptyList()

    abstract val iconOpened: ISettingsIcon?
    protected abstract val items: ArrayList<ISetting<*>>

    fun getItems(): List<ISetting<*>> = items

    fun addAll(item: List<ISetting<*>>): G {
        items.addAll(item)
        return this as G
    }

    fun add(vararg item: ISetting<*>): G {
        items.addAll(item.toList())
        return this as G
    }

    @Suppress("UNCHECKED_CAST")
    fun addToGroup(group: SettingsGroup): G {
        group.add(this)
        return this as G
    }

    override fun readRealValue(settingsData: ISettingsData) {
        // empty
    }

    override fun read(settingsData: ISettingsData) {
        // empty
    }

    override fun write(settingsData: ISettingsData, value: Unit) {
        // empty
    }

    override fun readIsEnabled(settingsData: ISettingsData.Element): Boolean {
        return false
    }

    override fun writeIsEnabled(settingsData: ISettingsData.Element, isEnabled: Boolean) {
        // empty
    }
}