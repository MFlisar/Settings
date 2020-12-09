package com.michaelflisar.settings.core.settings

import com.michaelflisar.settings.core.classes.SettingsDisplaySetup
import com.michaelflisar.settings.core.classes.SettingsMetaData
import com.michaelflisar.settings.core.enums.SupportType
import com.michaelflisar.settings.core.interfaces.ISetting
import com.michaelflisar.settings.core.interfaces.ISettingsData
import com.michaelflisar.settings.core.interfaces.ISettingsIcon
import com.michaelflisar.settings.core.interfaces.ISettingsItem
import com.michaelflisar.settings.core.items.SettingsItemGroup
import com.michaelflisar.settings.core.settings.base.BaseSettingsGroup
import com.michaelflisar.text.Text
import kotlinx.parcelize.Parcelize

@Parcelize
data class SettingsGroup(
        override val id: Long,
        override val label: Text,
        override val info: Text? = null,
        override val help: Text? = null,
        override val icon: ISettingsIcon? = null,
        override val iconOpened: ISettingsIcon? = null,
        override val items: ArrayList<ISetting<*>> = ArrayList(),
        override val supportType: SupportType = SupportType.All,
) : BaseSettingsGroup<SettingsGroup>() {

    override fun isShowNumbers(setup: SettingsDisplaySetup): Boolean = setup.showNumbersForGroups

    override fun createSettingsItem(parent: ISettingsItem<*, *, *>?, index: Int, itemData: SettingsMetaData, settingsData: ISettingsData, setup: SettingsDisplaySetup): ISettingsItem<Unit, *, *> {
        return SettingsItemGroup(parent, index, this, itemData, settingsData, setup)
    }
}