package com.michaelflisar.settings.core.settings

import com.michaelflisar.settings.core.settings.base.BaseSetting
import com.michaelflisar.settings.core.classes.SettingsDisplaySetup
import com.michaelflisar.settings.core.classes.SettingsCustomObject
import com.michaelflisar.settings.core.classes.SettingsIcon
import com.michaelflisar.settings.core.classes.SettingsMetaData
import com.michaelflisar.settings.core.interfaces.ISettingsIcon
import com.michaelflisar.settings.core.interfaces.ISettingsItem
import com.michaelflisar.settings.core.items.SettingsItemBool
import com.michaelflisar.text.Text
import com.michaelflisar.text.asText
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize

@Parcelize
class BooleanSetting(
        override val id: Long,
        override val label: Text,
        override val info: Text?,
        override val help: Text?,
        override val icon: ISettingsIcon?,
        override val defaultValue: Boolean,
        override val defaultIsCustomEnabled: Boolean = false
) : BaseSetting<Boolean, BooleanSetting, Unit>() {

    @IgnoredOnParcel
    override var setup: Unit = Unit

    // -----------------
    // default constructors with String or Resource Ints for convenience
    // -----------------

    constructor(id: Long, label: String, info: String?, help: String?, icon: Int?, defaultValue: Boolean, defaultIsCustomEnabled: Boolean = false) : this(id, label.asText(), info?.asText(), help?.asText(), icon?.let { SettingsIcon(it) }, defaultValue, defaultIsCustomEnabled)
    constructor(id: Long, label: Int, info: Int?, help: Int?, icon: Int?, defaultValue: Boolean, defaultIsCustomEnabled: Boolean = false) : this(id, label.asText(), info?.asText(), help?.asText(), icon?.let { SettingsIcon(it) }, defaultValue, defaultIsCustomEnabled)

    // -----------------
    // function implementations
    // -----------------

    override fun createSettingsItem(parent: ISettingsItem<*, *, *>?, index: Int, itemData: SettingsMetaData, customItem: SettingsCustomObject, setup: SettingsDisplaySetup): ISettingsItem<Boolean, *, *> {
        return SettingsItemBool(parent, index, this, itemData, customItem, setup)
    }
}