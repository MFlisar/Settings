package com.michaelflisar.settings.demo.advanced

import com.michaelflisar.settings.core.interfaces.ISetting
import com.michaelflisar.settings.demo.advanced.settings.*

object SettingsDefinitions {

    val SETTINGS_FOR_DESKTOPS: List<ISetting<*>> = listOf(
        SettDesktopLabel,
        SettDesktopColor
    )
    val SETTINGS_FOR_FOLDERS: List<ISetting<*>> = listOf(
        SettFolderLabel,
        SettFolderImage,
        SettFolderColor,
        SettFolderTag
    )
}