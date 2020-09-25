package com.michaelflisar.settings.core.settings

import com.michaelflisar.settings.core.settings.base.BaseSettingsGroup
import com.michaelflisar.settings.core.classes.SettingsDisplaySetup
import com.michaelflisar.settings.core.classes.SettingsCustomObject
import com.michaelflisar.settings.core.classes.SettingsIcon
import com.michaelflisar.settings.core.classes.SettingsMetaData
import com.michaelflisar.settings.core.enums.SupportType
import com.michaelflisar.settings.core.interfaces.ISetting
import com.michaelflisar.settings.core.interfaces.ISettingsIcon
import com.michaelflisar.settings.core.interfaces.ISettingsItem
import com.michaelflisar.settings.core.items.SettingsItemGroup
import com.michaelflisar.text.Text
import com.michaelflisar.text.asText
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SettingsGroup(
        override val id: Long,
        override val label: Text,
        override val info: Text?,
        override val help: Text?,
        override val icon: ISettingsIcon?,
        override val iconOpened: ISettingsIcon? = null,
        override val items: ArrayList<ISetting<*>> = ArrayList(),
        override var supportType: SupportType = SupportType.All
) : BaseSettingsGroup<SettingsGroup>() {

    override val defaultValue: Unit = Unit
    override val defaultIsCustomEnabled: Boolean = true

    override fun isShowNumbers(setup: SettingsDisplaySetup): Boolean = setup.showNumbersForGroups

    // -----------------
    // default constructors with String or Resource Ints for convenience
    // -----------------

    constructor(id: Long, label: String, info: String?, help: String?, icon: Int?, iconOpened: Int? = null) : this(id, label.asText(), info?.asText(), help?.asText(), icon?.let { SettingsIcon(it) }, iconOpened?.let { SettingsIcon(it) })
    constructor(id: Long, label: Int, info: Int?, help: Int?, icon: Int?, iconOpened: Int? = null) : this(id, label.asText(), info?.asText(), help?.asText(), icon?.let { SettingsIcon(it) }, iconOpened?.let { SettingsIcon(it) })

    // -----------------
    // function implementations
    // -----------------

    override fun createSettingsItem(parent: ISettingsItem<*, *, *>?, index: Int, itemData: SettingsMetaData, customItem: SettingsCustomObject, setup: SettingsDisplaySetup): ISettingsItem<Unit, *, *> {
        return SettingsItemGroup(parent, index, this, itemData, customItem, setup)
    }
}