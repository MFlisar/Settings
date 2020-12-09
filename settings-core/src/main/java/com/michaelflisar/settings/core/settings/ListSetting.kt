package com.michaelflisar.settings.core.settings

import com.michaelflisar.settings.core.classes.SettingsDisplaySetup
import com.michaelflisar.settings.core.classes.SettingsMetaData
import com.michaelflisar.settings.core.enums.SupportType
import com.michaelflisar.settings.core.interfaces.ISettingsData
import com.michaelflisar.settings.core.interfaces.ISettingsIcon
import com.michaelflisar.settings.core.interfaces.ISettingsItem
import com.michaelflisar.settings.core.interfaces.ISettingsListItem
import com.michaelflisar.settings.core.items.SettingsItemList
import com.michaelflisar.settings.core.items.setups.ListSetup
import com.michaelflisar.settings.core.settings.base.BaseSetting
import com.michaelflisar.text.Text
import kotlinx.parcelize.Parcelize

@Parcelize
open class ListSetting(
        override val id: Long,
        override val label: Text,
        override val info: Text?,
        override val help: Text?,
        override val icon: ISettingsIcon?,
        override var setup: ListSetup,
        override val supportType: SupportType = SupportType.All,
        override val editable: Boolean = true
) : BaseSetting<ISettingsListItem, ListSetting, ListSetup>() {

    override fun createSettingsItem(parent: ISettingsItem<*, *, *>?, index: Int, itemData: SettingsMetaData, settingsData: ISettingsData, setup: SettingsDisplaySetup): ISettingsItem<ISettingsListItem, *, *> {
        return SettingsItemList(parent, index, this, itemData, settingsData, setup, this.setup.mode)
    }
}