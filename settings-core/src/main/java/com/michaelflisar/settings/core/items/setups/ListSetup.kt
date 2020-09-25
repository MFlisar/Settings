package com.michaelflisar.settings.core.items.setups

import android.os.Parcelable
import com.michaelflisar.settings.core.interfaces.ISettingsListItem
import kotlinx.android.parcel.Parcelize

@Parcelize
class ListSetup(
        val items: ArrayList<ISettingsListItem>,
        val mode: Mode = Mode.Popup,
        val iconPosition: IconPosition,
        val style: Style = Style.Default
) : Parcelable {
    fun getItemByID(id: Long) = items.find { it.id == id }!!
    fun getItemByIndex(index: Int) = items[index]
    fun getIndex(item: ISettingsListItem) = items.indexOf(item)

    enum class IconPosition {
        Left, Right
    }

    enum class Mode {
        Dialog,
        Popup
    }

    enum class Style {
        Default,
        IconOnly,
        TextOnly;

        val showText: Boolean
                get() = this == Default || this == TextOnly

        val showIcon: Boolean
            get() = this == Default || this == IconOnly
    }
}