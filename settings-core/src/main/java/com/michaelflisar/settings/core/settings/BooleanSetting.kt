package com.michaelflisar.settings.core.settings

import com.michaelflisar.settings.core.settings.base.BaseSetting
import com.michaelflisar.settings.core.classes.SettingsDisplaySetup
import com.michaelflisar.settings.core.classes.SettingsMetaData
import com.michaelflisar.settings.core.enums.SupportType
import com.michaelflisar.settings.core.interfaces.ISettingsIcon
import com.michaelflisar.settings.core.interfaces.ISettingsItem
import com.michaelflisar.settings.core.items.SettingsItemCheckboxBool
import com.michaelflisar.settings.core.items.SettingsItemSwitchBool
import com.michaelflisar.settings.core.enums.BooleanStyle
import com.michaelflisar.settings.core.interfaces.ISettingsData
import com.michaelflisar.settings.core.items.setups.BoolSetup
import com.michaelflisar.text.Text
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
open class BooleanSetting(
        override val id: Long,
        override val label: Text,
        override val info: Text?,
        override val help: Text?,
        override val icon: ISettingsIcon?,
        override val supportType: SupportType = SupportType.All,
        override val editable: Boolean = true
) : BaseSetting<Boolean, BooleanSetting, BoolSetup>() {

    @IgnoredOnParcel
    override val setup: BoolSetup = BoolSetup()

    override fun createSettingsItem(parent: ISettingsItem<*, *, *>?, index: Int, itemData: SettingsMetaData, settingsData: ISettingsData, setup: SettingsDisplaySetup): ISettingsItem<Boolean, *, *> {
        return when (this.setup.style) {
            BooleanStyle.Checkbox -> SettingsItemCheckboxBool(parent, index, this, itemData, settingsData, setup)
            BooleanStyle.Switch -> SettingsItemSwitchBool(parent, index, this, itemData, settingsData, setup)
        }
    }
}