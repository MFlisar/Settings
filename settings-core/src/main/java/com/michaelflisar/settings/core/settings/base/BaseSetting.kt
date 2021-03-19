package com.michaelflisar.settings.core.settings.base

import android.os.Parcelable
import com.michaelflisar.settings.core.SettingsManager
import com.michaelflisar.settings.core.classes.NoValidStorageException
import com.michaelflisar.settings.core.classes.SettingsDisplaySetup
import com.michaelflisar.settings.core.enums.ChangeType
import com.michaelflisar.settings.core.interfaces.ISetting
import com.michaelflisar.settings.core.interfaces.ISettingsData
import com.michaelflisar.settings.core.settings.SettingsGroup

abstract class BaseSetting<ValueType, S : BaseSetting<ValueType, S, Setup>, Setup> : ISetting<ValueType> {

    abstract val setup: Setup

    override val onValueChanged: ((settingsData: ISettingsData, changeType: ChangeType) -> Unit)? = null

//    override val supportType: SupportType = SupportType.All

    override val canHoldData = true // must be false for Unit Types!

    override val valid: Boolean = true

    //    override val editable: Boolean = true
    override val clickable: Boolean
        get() = editable

    override var numbering: List<Int> = emptyList()

    override fun isShowNumbers(setup: SettingsDisplaySetup): Boolean = setup.showNumbersForItems

    @Suppress("UNCHECKED_CAST")
    fun addToGroup(group: SettingsGroup): S {
        group.add(this)
        return this as S
    }

    // ---------------------------
    // public read/write functions
    // ---------------------------

    override fun readRealValue(settingsData: ISettingsData): ValueType {
        return if (settingsData.isGlobal) {
            readGlobal(settingsData as ISettingsData.Global)
        } else {
            if (readIsEnabled(settingsData as ISettingsData.Element)) readCustom(settingsData) else readGlobal(settingsData.global)
        }
    }

    override fun read(settingsData: ISettingsData): ValueType {
        return if (settingsData.isGlobal) {
            readGlobal(settingsData as ISettingsData.Global)
        } else {
            readCustom(settingsData as ISettingsData.Element)
        }
    }

    override fun write(settingsData: ISettingsData, value: ValueType) {
        if (settingsData.isGlobal) {
            val oldValue = readGlobal(settingsData as ISettingsData.Global)
            if (oldValue != value) {
                writeGlobal(settingsData, value)
                SettingsManager.notifyOnSettingsChangedCallbacks(ChangeType.GlobalValue, this, settingsData, oldValue, value, true)
            }
        } else {
            val oldValue = readCustom(settingsData as ISettingsData.Element)
            if (oldValue != value) {
                writeCustom(settingsData, value)
                SettingsManager.notifyOnSettingsChangedCallbacks(ChangeType.CustomValue, this, settingsData, oldValue, value, true)
            }
        }
    }

    override fun readIsEnabled(settingsData: ISettingsData.Element): Boolean {
        return readIsCustomEnabled(settingsData)
    }

    override fun writeIsEnabled(settingsData: ISettingsData.Element, isEnabled: Boolean) {
        val oldValue = readIsCustomEnabled(settingsData)
        if (oldValue != isEnabled) {
            writeIsCustomEnabled(settingsData, isEnabled)
            SettingsManager.notifyOnSettingsChangedCallbacks(ChangeType.CustomIsEnabled, this, settingsData, oldValue, isEnabled, true)
        }
    }

    // -----------------
    // forward read/write functions to SettingsManager which will use the correct SettingsStorage
    // -----------------

    private fun readGlobal(settingsData: ISettingsData.Global): ValueType {
        return if (!canHoldData) Unit as ValueType else SettingsManager.storageManager.takeIf { it.supports(this) }?.readGlobal(this, settingsData) as ValueType
                ?: throw NoValidStorageException
    }

    private fun readCustom(settingsData: ISettingsData.Element): ValueType {
        return if (!canHoldData) Unit as ValueType else SettingsManager.storageManager.takeIf { it.supports(this) }?.readCustom(this, settingsData) as ValueType
                ?: throw NoValidStorageException
    }

    private fun readIsCustomEnabled(settingsData: ISettingsData.Element): Boolean {
        return if (!canHoldData) false else SettingsManager.storageManager.takeIf { it.supports(this) }?.readCustomEnabled(this, settingsData)
                ?: throw NoValidStorageException
    }

    private fun writeGlobal(settingsData: ISettingsData.Global, value: ValueType): Boolean {
        return if (!canHoldData) true else SettingsManager.storageManager.takeIf { it.supports(this) }?.writeGlobal(this, value, settingsData)
                ?: throw NoValidStorageException
    }

    private fun writeCustom(settingsData: ISettingsData.Element, value: ValueType): Boolean {
        return if (!canHoldData) true else SettingsManager.storageManager.takeIf { it.supports(this) }?.writeCustom(this, value, settingsData)
                ?: throw NoValidStorageException
    }

    private fun writeIsCustomEnabled(settingsData: ISettingsData.Element, value: Boolean): Boolean {
        return if (!canHoldData) true else SettingsManager.storageManager.takeIf { it.supports(this) }?.writeCustomEnabled(this, value, settingsData)
                ?: throw NoValidStorageException
    }
}