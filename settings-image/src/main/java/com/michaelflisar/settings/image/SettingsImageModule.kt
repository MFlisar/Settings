package com.michaelflisar.settings.image

import com.michaelflisar.settings.core.SettingsManager
import com.michaelflisar.settings.core.interfaces.ISettingsModule

class SettingsImageModule(private val requestCode: Int = 123) : ISettingsModule {

    override val supportedTypes = listOf(
            ImageSetting::class.java
    )

    override fun init() {
        // we must register this modules dialog handler
        SettingsItemImage.REQUEST_CODE_IMAGE = requestCode
        SettingsManager.addDialogEventHandler(SettingsItemImage.DIALOG_HANDLER)
    }
}