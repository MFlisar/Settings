package com.michaelflisar.settings.core.settings

import com.michaelflisar.settings.core.settings.base.BaseSetting
import com.michaelflisar.settings.core.classes.SettingsDisplaySetup
import com.michaelflisar.settings.core.classes.SettingsCustomObject
import com.michaelflisar.settings.core.classes.SettingsIcon
import com.michaelflisar.settings.core.classes.SettingsMetaData
import com.michaelflisar.settings.core.interfaces.ISettingsIcon
import com.michaelflisar.settings.core.interfaces.ISettingsItem
import com.michaelflisar.settings.core.interfaces.ISettingsListItem
import com.michaelflisar.settings.core.items.SettingsItemList
import com.michaelflisar.settings.core.items.setups.ListSetup
import com.michaelflisar.text.Text
import com.michaelflisar.text.asText
import kotlinx.android.parcel.Parcelize

@Parcelize
class ListSetting(
        override val id: Long,
        override val label: Text,
        override val info: Text?,
        override val help: Text?,
        override val icon: ISettingsIcon?,
        override val defaultValue: ISettingsListItem,
        override val defaultIsCustomEnabled: Boolean = false,
        override var setup: ListSetup
) : BaseSetting<ISettingsListItem, ListSetting, ListSetup>() {

    // -----------------
    // default constructors with String or Resource Ints for convenience
    // -----------------

    constructor(id: Long, label: String, info: String?, help: String?, icon: Int?, defaultValue: ISettingsListItem, setup: ListSetup, defaultIsCustomEnabled: Boolean = false) : this(id, label.asText(), info?.asText(), help?.asText(), icon?.let { SettingsIcon(it) }, defaultValue, defaultIsCustomEnabled, setup)
    constructor(id: Long, label: Int, info: Int?, help: Int?, icon: Int?, defaultValue: ISettingsListItem, setup: ListSetup, defaultIsCustomEnabled: Boolean = false) : this(id, label.asText(), info?.asText(), help?.asText(), icon?.let { SettingsIcon(it) }, defaultValue, defaultIsCustomEnabled, setup)

    // -----------------
    // function implementations
    // -----------------

    override fun createSettingsItem(parent: ISettingsItem<*, *, *>?, index: Int, itemData: SettingsMetaData, customItem: SettingsCustomObject, setup: SettingsDisplaySetup): ISettingsItem<ISettingsListItem, *, *> {
        return SettingsItemList(parent, index, this, itemData, customItem, setup, this.setup.mode)
    }
}