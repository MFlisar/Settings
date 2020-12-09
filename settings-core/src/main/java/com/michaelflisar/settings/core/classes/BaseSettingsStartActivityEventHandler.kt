package com.michaelflisar.settings.core.classes

import com.michaelflisar.settings.core.interfaces.ISetting
import com.michaelflisar.settings.core.interfaces.ISettingsDialogEventHandler

abstract class BaseSettingsStartActivityEventHandler<ValueType, EventType, Setting : ISetting<ValueType>> : ISettingsDialogEventHandler<ValueType, Setting> {

    override fun getDialogType(event: Any): Int? {
        // we cannot better distinct the event here because we can't attach custom data to system intents and retrieve it in the result again
        if (event is SettingsActivityResultEvent) {
            return dialogType
        }
        return null
    }

    @Suppress("UNCHECKED_CAST")
    override fun onDialogEvent(event: Any, dialogContext: DialogContext) {
        val e = event as SettingsActivityResultEvent
        onDialogEvent(e, dialogContext)
    }

    abstract fun onDialogEvent(event: SettingsActivityResultEvent, dialogContext: DialogContext)
}