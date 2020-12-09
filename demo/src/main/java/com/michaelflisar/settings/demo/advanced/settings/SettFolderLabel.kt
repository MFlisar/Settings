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
import com.michaelflisar.settings.utils.SettingsData
import com.michaelflisar.text.asText
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
object SettFolderLabel : StringSetting(
        3L, // TODO
        "Folder label".asText(),
        null,
        null,
        SettingsIcon(R.drawable.ic_baseline_text_fields_24),
        TextSetup(false)
), ICustomSetting<String, FolderWithFolders> {

    @IgnoredOnParcel
    override val supportType: SupportType = SupportType.CustomOnly // must fit the implemented interface

    override val onValueChanged = { settingsData: ISettingsData, change: ChangeType ->
        // callback... unused in the demo but may be useful in a real world example
        // e.g. to forward an event to an observable that is used in different UI parts of the app
    }

    // ------------
    // Global
    // ------------

    // not supported!!!

    // ------------
    // Custom
    // ------------

    override fun getCustomItem(settingsData: SettingsData.Custom): FolderWithFolders = settingsData.loadItem(FolderWithFolders::class.java)

    override val customReadFunc: (FolderWithFolders.() -> String) = { folder.label }
    override val customWriteFunc: (FolderWithFolders.(String) -> Boolean) = {
        folder.label = it
        true
    }

    override val customReadIsEnabledFunc: (FolderWithFolders.() -> Boolean) = { true }
    override val customWriteIsEnabledFunc: (FolderWithFolders.(Boolean) -> Boolean) = { true }

    override val onAfterCustomWrite: ((item: FolderWithFolders, change: ChangeType) -> Unit) = { item, change ->
        item.folder.persist()
    }
}