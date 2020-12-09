package com.michaelflisar.settings.core.items.setups

import android.os.Parcelable
import com.michaelflisar.settings.core.interfaces.ISettingsListItem
import kotlinx.parcelize.Parcelize

@Parcelize
class ListSetup(
        val items: ArrayList<ISettingsListItem>,
        val mode: Mode = DEFAULT_MODE,
        val iconPosition: IconPosition = DEFAULT_ICON_POSITION,
        val style: Style = DEFAULT_STYLE,
        val showTitleInPopup: Boolean = DEFAULT_SHOW_TITLE_IN_POPUP
) : Parcelable {

    companion object {
        var DEFAULT_MODE: Mode = Mode.Popup
        var DEFAULT_ICON_POSITION: IconPosition = IconPosition.Right
        var DEFAULT_STYLE: Style = Style.Default
        var DEFAULT_SHOW_TITLE_IN_POPUP: Boolean = true
    }

    fun getItemById(id: Long) = items.find { it.id == id }
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