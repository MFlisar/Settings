package com.michaelflisar.settings.demo.advanced.settings

import com.michaelflisar.settings.utils.interfaces.ICustomSetting
import com.michaelflisar.settings.utils.interfaces.IGlobalSetting
import com.michaelflisar.settings.color.ColorSetting
import com.michaelflisar.settings.color.ColorSetup
import com.michaelflisar.settings.core.classes.SettingsIcon
import com.michaelflisar.settings.core.enums.ChangeType
import com.michaelflisar.settings.core.interfaces.ISettingsData
import com.michaelflisar.settings.demo.R
import com.michaelflisar.settings.demo.advanced.data.db.FolderWithFolders
import com.michaelflisar.settings.demo.advanced.data.global.GlobalPreference
import com.michaelflisar.settings.utils.SettingsData
import com.michaelflisar.text.asText
import kotlinx.parcelize.Parcelize

@Parcelize
object SettFolderColor : ColorSetting(
        2L, // TODO
        "Folder color".asText(),
        null,
        null,
        SettingsIcon(R.drawable.ic_color_lens_black_24dp),
        ColorSetup(true)
), IGlobalSetting<Int, GlobalPreference>, ICustomSetting<Int, FolderWithFolders> {

    override val onValueChanged = { settingsData: ISettingsData, change: ChangeType ->
        // callback... unused in the demo but may be useful in a real world example
        // e.g. to forward an event to an observable that is used in different UI parts of the app
    }

    // ------------
    // Global
    // ------------

    override val globalItem: GlobalPreference = GlobalPreference
    override val globalReadFunc: (GlobalPreference.() -> Int) = { folderColor }
    override val globalWriteFunc: (GlobalPreference.(Int) -> Boolean) = {
        folderColor = it
        true
    }

    override val onAfterGlobalWrite = null

    // ------------
    // Custom
    // ------------

    override fun getCustomItem(settingsData: SettingsData.Custom): FolderWithFolders = settingsData.loadItem(FolderWithFolders::class.java)

    override val customReadFunc: (FolderWithFolders.() -> Int) = { folder.customColor }
    override val customWriteFunc: (FolderWithFolders.(Int) -> Boolean) = {
        folder.customColor = it
        true
    }

    override val customReadIsEnabledFunc: (FolderWithFolders.() -> Boolean) = { folder.hasCustomColor }
    override val customWriteIsEnabledFunc: (FolderWithFolders.(Boolean) -> Boolean) = {
        folder.hasCustomColor = it
        true
    }

    override val onAfterCustomWrite: ((item: FolderWithFolders, change: ChangeType) -> Unit) = { item, change ->
        item.folder.persist()
    }
}