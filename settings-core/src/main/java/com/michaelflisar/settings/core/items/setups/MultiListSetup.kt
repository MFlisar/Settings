package com.michaelflisar.settings.core.items.setups

import android.os.Parcelable
import com.michaelflisar.settings.core.interfaces.ISettingsListItem
import kotlinx.parcelize.Parcelize

@Parcelize
class MultiListSetup(
        val items: ArrayList<ISettingsListItem>,
        val displayType: DisplayType = DEFAULT_DISPLAY_TYPE
) : Parcelable {

    companion object {
        var DEFAULT_DISPLAY_TYPE: DisplayType = DisplayType.Count
    }

    fun getItemById(id: Long) = items.find { it.id == id }!!
    fun getItemByIndex(index: Int) = items[index]
    fun getIndex(item: ISettingsListItem) = items.indexOf(item)

    sealed class DisplayType : Parcelable {
        @Parcelize
        object Count: DisplayType()
        @Parcelize
        class CommaSeparated(val limit: Int = -1): DisplayType()
    }
}