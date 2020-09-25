package com.michaelflisar.settings.core

import com.michaelflisar.settings.core.interfaces.ISettingsModule
import com.michaelflisar.settings.core.items.*
import com.michaelflisar.settings.core.settings.*

object SettingsCoreModule : ISettingsModule {

    override val supportedTypes = listOf(
            IntSetting::class.java,
            BooleanSetting::class.java,
            StringSetting::class.java,
            ListSetting::class.java,
            MultiListSetting::class.java
    )

    override fun init() {
        // we must register this modules dialog handler
        SettingsManager.addDialogEventHandler(SettingsItemText.DIALOG_HANDLER)
        SettingsManager.addDialogEventHandler(SettingsItemInfo.DIALOG_HANDLER)
        SettingsManager.addDialogEventHandler(SettingsItemInt.DIALOG_HANDLER)
        SettingsManager.addDialogEventHandler(SettingsItemList.DIALOG_HANDLER)
        SettingsManager.addDialogEventHandler(SettingsItemMultiList.DIALOG_HANDLER)
    }
}