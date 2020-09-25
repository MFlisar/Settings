package com.michaelflisar.settings.color

import com.michaelflisar.settings.core.SettingsManager
import com.michaelflisar.settings.core.interfaces.ISettingsModule

object SettingsColorModule : ISettingsModule {

    override val supportedTypes = listOf(
            ColorSetting::class.java
    )

    override fun init() {
        // we must register this modules dialog handler
        SettingsManager.addDialogEventHandler(SettingsItemColor.DIALOG_HANDLER)
    }
}