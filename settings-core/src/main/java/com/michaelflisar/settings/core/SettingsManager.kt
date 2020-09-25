package com.michaelflisar.settings.core

import android.content.Context
import com.michaelflisar.dialogs.DialogSetup
import com.michaelflisar.settings.core.classes.SettingsCustomObject
import com.michaelflisar.settings.core.interfaces.ISetting
import com.michaelflisar.settings.core.interfaces.ISettingsChangedCallback
import com.michaelflisar.settings.core.interfaces.ISettingsDialogEventHandler
import com.michaelflisar.settings.core.interfaces.ISettingsStorageManager
import com.michaelflisar.settings.core.internal.ConcurrentSaveMutableList

object SettingsManager {

    // easy and quick solution to avoid the need of a custom interface to listen for setting changes inside an activity
    // - necessary because we can't hand on a callback (which is not parcelable) to the fragments inside the view pager mode
    // - ConcurrentSaveMutableList is used because this allows the callback to remove itself or add new callbacks even from within the callback itself
    private val callbacks: ConcurrentSaveMutableList<ISettingsChangedCallback> = ConcurrentSaveMutableList()
    private val dialogCallbacks: ConcurrentSaveMutableList<ISettingsDialogEventHandler<*, *>> = ConcurrentSaveMutableList()

    lateinit var context: Context
        private set

    internal lateinit var storageManager: ISettingsStorageManager

    fun init(context: Context, storageManager: ISettingsStorageManager): SettingsManager {

        // 1) save app context
        this.context = context.applicationContext

        // 2) save storage manager
        this.storageManager = storageManager

        // 3) init all modules (this should add their dialog callbacks!)
        storageManager.init()

        // 4) handle dialog events and forward them to the dialog callbacks
        DialogSetup.resultHandler = { event ->
            pushDialogEvent(event)
        }

        return this
    }

    fun registerCallback(callback: ISettingsChangedCallback) {
        callbacks.add(callback)
    }

    fun unregisterCallback(callback: ISettingsChangedCallback) {
        callbacks.remove(callback)
    }

    fun addDialogEventHandler(callback: ISettingsDialogEventHandler<*, *>): SettingsManager {
        dialogCallbacks.add(callback)
        return this
    }

    fun pushDialogEvent(event: Any) {
        dialogCallbacks.forEach {
            // forward to callback if type fits the handler
            if (it.getDialogType(event) == it.dialogType) {
                it.onDialogEvent(event)
            }
        }
    }

    // -----------------
    // internal notifier functions
    // -----------------

    internal fun notifyOnSettingsChangedCallbacks(setting: ISetting<*>, customItem: SettingsCustomObject, oldValue: Any?, newValue: Any?) {
        callbacks.forEach {
            it.onSettingChanged(setting, customItem, oldValue, newValue)
        }
    }

    internal fun notifyOnCustomEnabledCallbacks(setting: ISetting<*>, customItem: SettingsCustomObject.Element, oldValue: Boolean, newValue: Boolean) {
        callbacks.forEach {
            it.onCustomEnabledChanged(setting, customItem, oldValue, newValue)
        }
    }
}