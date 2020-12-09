package com.michaelflisar.settings.core.settings

import android.os.Parcelable
import com.michaelflisar.settings.core.settings.base.BaseSetting
import com.michaelflisar.settings.core.classes.SettingsDisplaySetup
import com.michaelflisar.settings.core.classes.SettingsMetaData
import com.michaelflisar.settings.core.enums.SupportType
import com.michaelflisar.settings.core.interfaces.ISettingsData
import com.michaelflisar.settings.core.interfaces.ISettingsIcon
import com.michaelflisar.settings.core.interfaces.ISettingsItem
import com.michaelflisar.settings.core.interfaces.ISettingsListItem
import com.michaelflisar.settings.core.items.SettingsItemMultiList
import com.michaelflisar.settings.core.items.setups.MultiListSetup
import com.michaelflisar.text.Text
import kotlinx.parcelize.Parcelize

@Parcelize
open class MultiListSetting(
        override val id: Long,
        override val label: Text,
        override val info: Text?,
        override val help: Text?,
        override val icon: ISettingsIcon?,
        override var setup: MultiListSetup,
        override val supportType: SupportType = SupportType.All,
        override val editable: Boolean = true
) : BaseSetting<MultiListSetting.MultiListData, MultiListSetting, MultiListSetup>() {

    override fun createSettingsItem(parent: ISettingsItem<*, *, *>?, index: Int, itemData: SettingsMetaData, settingsData: ISettingsData, setup: SettingsDisplaySetup): ISettingsItem<MultiListData, *, *> {
        return SettingsItemMultiList(parent, index, this, itemData, settingsData, setup)
    }

    // -----------------
    // custom data class
    // -----------------

    // data class - we need an equals implementation to make sure that change events are only created if the data really changed
    @Parcelize
    data class MultiListData(val items: List<ISettingsListItem>) : Parcelable {
        fun getDisplayValue(displayType: MultiListSetup.DisplayType): String {
            return when (displayType) {
                is MultiListSetup.DisplayType.Count -> items.size.toString()
                is MultiListSetup.DisplayType.CommaSeparated -> items.map { it.getDisplayValue() }.joinToString(",", limit = displayType.limit)
            }
        }

        override fun toString(): String = getDisplayValue(MultiListSetup.DisplayType.Count)
    }
}