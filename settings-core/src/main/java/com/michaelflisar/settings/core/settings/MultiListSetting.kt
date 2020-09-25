package com.michaelflisar.settings.core.settings

import android.os.Parcelable
import com.michaelflisar.settings.core.settings.base.BaseSetting
import com.michaelflisar.settings.core.classes.SettingsDisplaySetup
import com.michaelflisar.settings.core.classes.SettingsCustomObject
import com.michaelflisar.settings.core.classes.SettingsIcon
import com.michaelflisar.settings.core.classes.SettingsMetaData
import com.michaelflisar.settings.core.interfaces.ISettingsIcon
import com.michaelflisar.settings.core.interfaces.ISettingsItem
import com.michaelflisar.settings.core.interfaces.ISettingsListItem
import com.michaelflisar.settings.core.items.SettingsItemMultiList
import com.michaelflisar.settings.core.items.setups.MultiListSetup
import com.michaelflisar.text.Text
import com.michaelflisar.text.asText
import kotlinx.android.parcel.Parcelize

@Parcelize
class MultiListSetting(
        override val id: Long,
        override val label: Text,
        override val info: Text?,
        override val help: Text?,
        override val icon: ISettingsIcon?,
        override val defaultValue: MultiListData,
        override val defaultIsCustomEnabled: Boolean = false,
        override var setup: MultiListSetup
) : BaseSetting<MultiListSetting.MultiListData, MultiListSetting, MultiListSetup>() {

    // -----------------
    // default constructors with String or Resource Ints for convenience
    // -----------------

    constructor(id: Long, label: String, info: String?, help: String?, icon: Int?, defaultValue: MultiListData, setup: MultiListSetup, defaultIsCustomEnabled: Boolean = false) : this(id, label.asText(), info?.asText(), help?.asText(), icon?.let { SettingsIcon(it) }, defaultValue, defaultIsCustomEnabled, setup)
    constructor(id: Long, label: Int, info: Int?, help: Int?, icon: Int?, defaultValue: MultiListData, setup: MultiListSetup, defaultIsCustomEnabled: Boolean = false) : this(id, label.asText(), info?.asText(), help?.asText(), icon?.let { SettingsIcon(it) }, defaultValue, defaultIsCustomEnabled, setup)

    // -----------------
    // function implementations
    // -----------------

    override fun createSettingsItem(parent: ISettingsItem<*, *, *>?, index: Int, itemData: SettingsMetaData, customItem: SettingsCustomObject, setup: SettingsDisplaySetup): ISettingsItem<MultiListData, *, *> {
        return SettingsItemMultiList(parent, index, this, itemData, customItem, setup)
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