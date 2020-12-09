package com.michaelflisar.settings.core.interfaces

import android.os.Parcelable
import com.michaelflisar.settings.core.SettingsManager
import com.michaelflisar.settings.core.classes.SettingsDisplaySetup
import com.michaelflisar.settings.core.classes.SettingsMetaData
import com.michaelflisar.settings.core.enums.ChangeType
import com.michaelflisar.settings.core.enums.SupportType
import com.michaelflisar.text.Text

interface ISetting<ValueType> : Parcelable {

    val id: Long
    val label: Text
    val info: Text?
    val help: Text?
    val icon: ISettingsIcon?

    val onValueChanged: ((settingsData: ISettingsData, changeType: ChangeType) -> Unit)?

    var numbering: List<Int>

    val supportType: SupportType
    val canHoldData: Boolean

    val valid: Boolean
    val editable: Boolean
    val clickable: Boolean
    fun isShowNumbers(setup: SettingsDisplaySetup): Boolean

    fun createSettingsItem(parent: ISettingsItem<*, *, *>?, index: Int, itemData: SettingsMetaData, settingsData: ISettingsData, setup: SettingsDisplaySetup): ISettingsItem<ValueType, *, *>

    fun getDisplayLabel(setup: SettingsDisplaySetup, isViewPagerTitle: Boolean = false): String {
        val prefix1 = if (setup.showID) {
            "[$id]"
        } else null
        val prefix2 = if (isShowNumbers(setup)) {
            if (isViewPagerTitle && !setup.showNumbersForViewPager) "" else getNumberingInfo().takeIf { it.isNotEmpty() }
        } else null

        return listOf(prefix1, prefix2, label.get(SettingsManager.context)).filter { it != null && it.isNotEmpty() }.joinToString(" ")
    }

    fun getNumberingInfo(): String {
        return numbering.joinToString(".") {
            "${it + 1}"
        }
    }

    fun readRealValue(settingsData: ISettingsData): ValueType
    fun read(settingsData: ISettingsData): ValueType
    fun write(settingsData: ISettingsData, value: ValueType)
    fun readIsEnabled(settingsData: ISettingsData.Element): Boolean
    fun writeIsEnabled(settingsData: ISettingsData.Element, isEnabled: Boolean)
}