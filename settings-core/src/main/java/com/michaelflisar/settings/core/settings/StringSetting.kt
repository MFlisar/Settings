package com.michaelflisar.settings.core.settings

import com.michaelflisar.settings.core.classes.SettingsDisplaySetup
import com.michaelflisar.settings.core.classes.SettingsMetaData
import com.michaelflisar.settings.core.enums.SupportType
import com.michaelflisar.settings.core.interfaces.ISettingsData
import com.michaelflisar.settings.core.interfaces.ISettingsIcon
import com.michaelflisar.settings.core.interfaces.ISettingsItem
import com.michaelflisar.settings.core.items.SettingsItemText
import com.michaelflisar.settings.core.items.setups.TextSetup
import com.michaelflisar.settings.core.settings.base.BaseSetting
import com.michaelflisar.text.Text
import kotlinx.parcelize.Parcelize

@Parcelize
open class StringSetting(
        override val id: Long,
        override val label: Text,
        override val info: Text?,
        override val help: Text?,
        override val icon: ISettingsIcon?,
        override var setup: TextSetup = TextSetup(),
        override val supportType: SupportType = SupportType.All,
        override val editable: Boolean = true
) : BaseSetting<String, StringSetting, TextSetup>() {

    override fun createSettingsItem(parent: ISettingsItem<*, *, *>?, index: Int, itemData: SettingsMetaData, settingsData: ISettingsData, setup: SettingsDisplaySetup): ISettingsItem<String, *, *> {
        return SettingsItemText(parent, index, this, itemData, settingsData, setup)
    }
}