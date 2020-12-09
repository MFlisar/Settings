package com.michaelflisar.settings.core.classes

import android.os.Bundle
import com.michaelflisar.dialogs.events.BaseDialogEvent
import com.michaelflisar.settings.core.R
import com.michaelflisar.settings.core.SettingsManager
import com.michaelflisar.settings.core.interfaces.ISetting
import com.michaelflisar.settings.core.interfaces.ISettingsData
import com.michaelflisar.settings.core.interfaces.ISettingsDialogEventHandler

abstract class BaseSettingsDialogEventHandler<ValueType, EventType, Setting : ISetting<ValueType>> : ISettingsDialogEventHandler<ValueType, Setting> {

    override fun getDialogType(event: Any): Int? {
        val e = event as? BaseDialogEvent
        val extra = e?.extras
        val type = extra?.getInt(getKey(R.string.settings_dialog_event_tag_dialog_type))
        return type
    }

    @Suppress("UNCHECKED_CAST")
    override fun onDialogEvent(event: Any, dialogContext: DialogContext) {
        val e = event as BaseDialogEvent
        val extras = e.extras
        val setting = extras?.get(getKey(R.string.settings_dialog_event_tag_setting)) as ISetting<*>?

        val isCorrectType = extras?.getInt(getKey(R.string.settings_dialog_event_tag_dialog_type)) == dialogType

        if (isCorrectType && setting != null) {
            val settingsDataHolder = event.extras!!.get(getKey(R.string.settings_dialog_event_tag_custom_item)) as ISettingsData
            onDialogEvent(dialogContext, event as EventType, setting as Setting, settingsDataHolder)
        }
    }

    abstract fun onDialogEvent(dialogContext: DialogContext, event: EventType, setting: Setting, settingsData: ISettingsData)

    // -----------------
    // helper functions
    // -----------------

    fun createDialogBundle(setting: ISetting<*>, settingsData: ISettingsData?): Bundle {
        return Bundle().apply {
            putInt(getKey(R.string.settings_dialog_event_tag_dialog_type), dialogType)
            putParcelable(getKey(R.string.settings_dialog_event_tag_setting), setting)
            putParcelable(getKey(R.string.settings_dialog_event_tag_custom_item), settingsData)
        }
    }

    fun getKey(key: Int): String = SettingsManager.context.getString(key)

}