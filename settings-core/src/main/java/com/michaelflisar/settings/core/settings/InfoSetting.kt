package com.michaelflisar.settings.core.settings

import com.michaelflisar.settings.core.settings.base.BaseSetting
import com.michaelflisar.settings.core.classes.SettingsDisplaySetup
import com.michaelflisar.settings.core.classes.SettingsMetaData
import com.michaelflisar.settings.core.enums.SupportType
import com.michaelflisar.settings.core.interfaces.ISettingsData
import com.michaelflisar.settings.core.interfaces.ISettingsIcon
import com.michaelflisar.settings.core.interfaces.ISettingsItem
import com.michaelflisar.settings.core.items.SettingsItemInfo
import com.michaelflisar.settings.core.items.setups.InfoSetup
import com.michaelflisar.text.Text
import com.michaelflisar.text.asText
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
open class InfoSetting(
        override val id: Long,
        override val label: Text,
        override val info: Text?,
        override val help: Text?,
        override val icon: ISettingsIcon?,
        override val setup: InfoSetup = InfoSetup(),
        override val supportType: SupportType = SupportType.All,
        override val editable: Boolean = true
) : BaseSetting<Unit, InfoSetting, InfoSetup>() {

    @IgnoredOnParcel
    override val clickable: Boolean = help != null

    @IgnoredOnParcel
    override val canHoldData = false // must be false for Unit Types!

    // -----------------
    // function implementations
    // -----------------

    override fun createSettingsItem(parent: ISettingsItem<*, *, *>?, index: Int, itemData: SettingsMetaData, settingsData: ISettingsData, setup: SettingsDisplaySetup): ISettingsItem<Unit, *, *> {
        return SettingsItemInfo(parent, index, this, itemData, settingsData, setup)
    }
}