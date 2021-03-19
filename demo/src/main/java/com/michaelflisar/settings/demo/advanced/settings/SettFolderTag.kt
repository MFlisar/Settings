package com.michaelflisar.settings.demo.advanced.settings

import com.michaelflisar.settings.utils.interfaces.ICustomSetting
import com.michaelflisar.settings.core.classes.SettingsIcon
import com.michaelflisar.settings.core.enums.ChangeType
import com.michaelflisar.settings.core.enums.SupportType
import com.michaelflisar.settings.core.interfaces.ISettingsData
import com.michaelflisar.settings.core.items.setups.TextSetup
import com.michaelflisar.settings.core.settings.StringSetting
import com.michaelflisar.settings.demo.R
import com.michaelflisar.settings.demo.advanced.data.db.FolderWithFolders
import com.michaelflisar.settings.demo.advanced.data.global.GlobalPreference
import com.michaelflisar.settings.utils.SettingsData
import com.michaelflisar.settings.utils.interfaces.IGlobalSetting
import com.michaelflisar.text.asText
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.coroutines.runBlocking
import kotlinx.parcelize.Parcelize

@Parcelize
object SettFolderTag : StringSetting(
        6L, // TODO
        "Folder tag".asText(),
        null,
        null,
        SettingsIcon(R.drawable.ic_label_outline_white_24dp),
        TextSetup(true)
), IGlobalSetting<String, GlobalPreference>, ICustomSetting<String, FolderWithFolders> {

    override val onValueChanged = { settingsData: ISettingsData, change: ChangeType ->
        // callback... unused in the demo but may be useful in a real world example
        // e.g. to forward an event to an observable that is used in different UI parts of the app
    }

    // ------------
    // Global
    // ------------

    override val globalItem: GlobalPreference = GlobalPreference
    override val globalReadFunc: (GlobalPreference.() -> String) = { folderTag.value }
    override val globalWriteFunc: (GlobalPreference.(String) -> Boolean) = {
        runBlocking {
            folderTag.update(it)
        }
        true
    }

    override val onAfterGlobalWrite = null

    // ------------
    // Custom
    // ------------

    override fun getCustomItem(settingsData: SettingsData.Custom): FolderWithFolders = settingsData.loadItem(FolderWithFolders::class.java)

    override val customReadFunc: (FolderWithFolders.() -> String) = { folder.customTag }
    override val customWriteFunc: (FolderWithFolders.(String) -> Boolean) = {
        folder.customTag = it
        true
    }

    override val customReadIsEnabledFunc: (FolderWithFolders.() -> Boolean) = { folder.hasCustomTag }
    override val customWriteIsEnabledFunc: (FolderWithFolders.(Boolean) -> Boolean) = {
        folder.hasCustomTag = it
        true
    }

    override val onAfterCustomWrite: ((item: FolderWithFolders, change: ChangeType) -> Unit) = { item, change ->
        item.folder.persist()
    }
}