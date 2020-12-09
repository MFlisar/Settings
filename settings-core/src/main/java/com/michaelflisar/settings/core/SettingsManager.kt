package com.michaelflisar.settings.core

import android.content.Context
import android.os.Parcelable
import com.michaelflisar.dialogs.DialogSetup
import com.michaelflisar.settings.core.classes.DialogContext
import com.michaelflisar.settings.core.enums.ChangeType
import com.michaelflisar.settings.core.interfaces.*
import com.michaelflisar.settings.core.internal.ConcurrentSaveMutableList

object SettingsManager {

    // easy and quick solution to avoid the need of a custom interface to listen for setting changes inside an activity
    // - necessary because we can't hand on a callback (which is not parcelable) to the fragments inside the view pager mode
    // - ConcurrentSaveMutableList is used because this allows the callback to remove itself or add new callbacks even from within the callback itself
    private val callbacks: ConcurrentSaveMutableList<ISettingsChangedCallback> = ConcurrentSaveMutableList()
    private val dialogCallbacks: ConcurrentSaveMutableList<ISettingsDialogEventHandler<*, *>> = ConcurrentSaveMutableList()

    internal var settingsProvider: ((id: Long) -> ISetting<*>)? = null

    //var DEBUGGER: ((tag: String, log: String) -> Unit)? = null
    lateinit var context: Context
        private set

    private var storageManagerInstance: ISettingsStorageManager? = null
    internal val storageManager: ISettingsStorageManager
        get() {
            return storageManagerInstance ?: throw RuntimeException("You did not call SettingsManager.init(...) yet!")
        }

    fun init(context: Context, storageManager: ISettingsStorageManager): SettingsManager {

        // 1) save app context
        this.context = context.applicationContext

        // 2) save storage manager and settings
        this.storageManagerInstance = storageManager

        // 3) init all modules (this should add their dialog callbacks!)
        storageManager.init()

        // 4) handle dialog events and forward them to the dialog callbacks
        DialogSetup.addResultHandler { event, fragment ->
            pushDialogEvent(event, fragment.parentDialogContext)
        }

        return this
    }

    /*
     * provide an [ISettingsProvider] to avoid the need to parcel and unparcel settings
     * this can improve performance if a lot of settings are used
     */
    fun setSettingsProvider(provider: (id: Long) -> ISetting<*>) {
        settingsProvider = provider
    }

    fun reset() {
        storageManagerInstance = null
        callbacks.clear()
        dialogCallbacks.clear()
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

    fun pushDialogEvent(event: Any, dialogContext: DialogContext) {
        dialogCallbacks.forEach {
            // forward to callback if type fits the handler
            if (it.getDialogType(event) == it.dialogType) {
                it.onDialogEvent(event, dialogContext)
            }
        }
    }

    // -----------------
    // notifier functions
    // -----------------

    fun notifyOnSettingsChangedCallbacks(changeType: ChangeType, setting: ISetting<*>, settingsData: ISettingsData, oldValue: Any?, newValue: Any?) {
        callbacks.forEach {
            it.onSettingChanged(changeType, setting, settingsData, oldValue, newValue)
        }
    }
}