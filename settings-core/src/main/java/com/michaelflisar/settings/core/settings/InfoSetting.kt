package com.michaelflisar.settings.core.settings

import com.michaelflisar.settings.core.settings.base.BaseSetting
import com.michaelflisar.settings.core.classes.SettingsDisplaySetup
import com.michaelflisar.settings.core.classes.SettingsCustomObject
import com.michaelflisar.settings.core.classes.SettingsIcon
import com.michaelflisar.settings.core.classes.SettingsMetaData
import com.michaelflisar.settings.core.interfaces.ISettingsIcon
import com.michaelflisar.settings.core.interfaces.ISettingsItem
import com.michaelflisar.settings.core.items.SettingsItemInfo
import com.michaelflisar.settings.core.items.setups.InfoSetup
import com.michaelflisar.text.Text
import com.michaelflisar.text.asText
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize

@Parcelize
class InfoSetting(
        override val id: Long,
        override val label: Text,
        override val info: Text?,
        override val icon: ISettingsIcon?,
        override var setup: InfoSetup = InfoSetup()
) : BaseSetting<Unit, InfoSetting, InfoSetup>() {

    @IgnoredOnParcel
    override val defaultValue: Unit = Unit
    @IgnoredOnParcel
    override val defaultIsCustomEnabled: Boolean = false
    @IgnoredOnParcel
    override val clickable: Boolean = false

    @IgnoredOnParcel
    override val help: Text? = null

    // -----------------
    // default constructors with String or Resource Ints for convenience
    // -----------------

    constructor(id: Long, label: String, info: String?, icon: Int?) : this(id, label.asText(), info?.asText(), icon?.let { SettingsIcon(it) })
    constructor(id: Long, label: Int, info: Int?, icon: Int?) : this(id, label.asText(), info?.asText(), icon?.let { SettingsIcon(it) })

    // -----------------
    // function implementations
    // -----------------

    override fun createSettingsItem(parent: ISettingsItem<*, *, *>?, index: Int, itemData: SettingsMetaData, customItem: SettingsCustomObject, setup: SettingsDisplaySetup): ISettingsItem<Unit, *, *> {
        return SettingsItemInfo(parent, index, this, itemData, customItem, setup)
    }
}