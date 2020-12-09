package com.michaelflisar.settings.core.enums

import android.os.Parcelable
import com.michaelflisar.settings.core.interfaces.ISettingsItem
import com.michaelflisar.settings.core.items.SettingsItemGroup
import com.michaelflisar.settings.core.items.SettingsItemInfo
import kotlinx.parcelize.Parcelize


sealed class CountDisplayType : Parcelable {
    @Parcelize
    object None : CountDisplayType()

    @Parcelize
    class DirectChildren(val withGroups: Boolean, val withInfos: Boolean) : CountDisplayType()

    @Parcelize
    class AllChildren(val withGroups: Boolean, val withInfos: Boolean) : CountDisplayType()

    internal fun isValid(item: ISettingsItem<*, *, *>): Boolean {
        return when (this) {
            is None -> false
            is DirectChildren -> isValid(item, withGroups, withInfos)
            is AllChildren -> isValid(item, withGroups, withInfos)
        }
    }

    private fun isValid(item: ISettingsItem<*, *, *>, withGroups: Boolean, withInfos: Boolean): Boolean {
        return when (item) {
            is SettingsItemGroup -> withGroups
            is SettingsItemInfo -> withInfos
            else -> true
        }
    }
}