package com.michaelflisar.settings.core.settings

import com.michaelflisar.settings.core.settings.base.BaseSetting
import com.michaelflisar.settings.core.classes.SettingsDisplaySetup
import com.michaelflisar.settings.core.classes.SettingsMetaData
import com.michaelflisar.settings.core.enums.SupportType
import com.michaelflisar.settings.core.interfaces.ISettingsData
import com.michaelflisar.settings.core.interfaces.ISettingsIcon
import com.michaelflisar.settings.core.interfaces.ISettingsItem
import com.michaelflisar.settings.core.items.SettingsItemInt
import com.michaelflisar.settings.core.items.setups.IntSetup
import com.michaelflisar.text.Text
import kotlinx.parcelize.Parcelize

@Parcelize
open class IntSetting(
        override val id: Long,
        override val label: Text,
        override val info: Text?,
        override val help: Text?,
        override val icon: ISettingsIcon?,
        override var setup: IntSetup = IntSetup.Picker(),
        override val supportType: SupportType = SupportType.All,
        override val editable: Boolean = true
) : BaseSetting<Int, IntSetting, IntSetup>() {

    override fun createSettingsItem(parent: ISettingsItem<*, *, *>?, index: Int, itemData: SettingsMetaData, settingsData: ISettingsData, setup: SettingsDisplaySetup): ISettingsItem<Int, *, *> {
        return SettingsItemInt(parent, index, this, itemData, settingsData, setup)
    }
}