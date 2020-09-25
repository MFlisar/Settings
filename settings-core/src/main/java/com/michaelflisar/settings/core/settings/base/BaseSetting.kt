package com.michaelflisar.settings.core.settings.base

import com.michaelflisar.settings.core.SettingsManager
import com.michaelflisar.settings.core.classes.NoValidStorageException
import com.michaelflisar.settings.core.classes.SettingsCustomObject
import com.michaelflisar.settings.core.classes.SettingsDisplaySetup
import com.michaelflisar.settings.core.enums.SupportType
import com.michaelflisar.settings.core.interfaces.ISetting
import com.michaelflisar.settings.core.settings.SettingsGroup

abstract class BaseSetting<ValueType, S : BaseSetting<ValueType, S, Setup>, Setup> : ISetting<ValueType> {

    abstract var setup: Setup

    override var editable: Boolean = true
    override val clickable: Boolean
        get() = editable

    override var numbering: List<Int> = emptyList()

    override var supportType: SupportType = SupportType.All

    override fun isShowNumbers(setup: SettingsDisplaySetup): Boolean = setup.showNumbersForItems

    @Suppress("UNCHECKED_CAST")
    fun addToGroup(group: SettingsGroup): S {
        group.add(this)
        return this as S
    }

//    @Suppress("UNCHECKED_CAST")
//    fun withSetup(setup: Setup): S {
//        this.setup = setup
//        return this as S
//    }

    // ---------------------------
    // public read/write functions
    // ---------------------------

    override fun readRealValue(element: SettingsCustomObject): ValueType {
        return when (element) {
            SettingsCustomObject.None -> readGlobal()
            is SettingsCustomObject.Element -> if (readSettingEnabled(element)) readCustom(element.item) else readGlobal()
        }
    }

    override fun readSetting(element: SettingsCustomObject): ValueType {
        return when (element) {
            SettingsCustomObject.None -> readGlobal()
            is SettingsCustomObject.Element -> readCustom(element.item)
        }
    }

    override fun writeSetting(element: SettingsCustomObject, value: ValueType) {
        when (element) {
            SettingsCustomObject.None -> {
                val oldValue = readGlobal()
                if (oldValue != value) {
                    writeGlobal(value)
                    SettingsManager.notifyOnSettingsChangedCallbacks(this, element, oldValue, value)
                }
            }
            is SettingsCustomObject.Element -> {
                val oldValue = readCustom(element.item)
                if (oldValue != value) {
                    writeCustom(element.item, value)
                    SettingsManager.notifyOnSettingsChangedCallbacks(this, element, oldValue, value)
                }
            }
        }
    }

    override fun readSettingEnabled(element: SettingsCustomObject.Element): Boolean {
        return readIsCustomEnabled(element.item)
    }

    override fun writeSettingEnabled(element: SettingsCustomObject.Element, isEnabled: Boolean) {
        val oldValue = readIsCustomEnabled(element.item)
        if (oldValue != isEnabled) {
            writeIsCustomEnabled(element.item, isEnabled)
            SettingsManager.notifyOnCustomEnabledCallbacks(this, element, oldValue, isEnabled)
        }
    }

    // -----------------
    // forward read/write functions to SettingsManager which will use the correct SettingsStorage
    // -----------------

    fun readGlobal(): ValueType {
        return if (defaultValue == Unit) Unit as ValueType else SettingsManager.storageManager.takeIf { it.supports(this) }?.readGlobal(this) as ValueType
                ?: throw NoValidStorageException
    }

    fun readCustom(customItem: Any): ValueType {
        return if (defaultValue == Unit) Unit as ValueType else SettingsManager.storageManager.takeIf { it.supports(this) }?.readCustom(this, customItem) as ValueType
                ?: throw NoValidStorageException
    }

    fun readIsCustomEnabled(customItem: Any): Boolean {
        return if (defaultValue == Unit) false else SettingsManager.storageManager.takeIf { it.supports(this) }?.readCustomEnabled(this, customItem)
                ?: throw NoValidStorageException
    }

    fun writeGlobal(value: ValueType): Boolean {
        return if (defaultValue == Unit) true else SettingsManager.storageManager.takeIf { it.supports(this) }?.writeGlobal(this, value)
                ?: throw NoValidStorageException
    }

    fun writeCustom(customItem: Any, value: ValueType): Boolean {
        return if (defaultValue == Unit) true else SettingsManager.storageManager.takeIf { it.supports(this) }?.writeCustom(this, value, customItem)
                ?: throw NoValidStorageException
    }

    fun writeIsCustomEnabled(customItem: Any, value: Boolean): Boolean {
        return if (defaultValue == Unit) true else SettingsManager.storageManager.takeIf { it.supports(this) }?.writeCustomEnabled(this, value, customItem)
                ?: throw NoValidStorageException
    }
}