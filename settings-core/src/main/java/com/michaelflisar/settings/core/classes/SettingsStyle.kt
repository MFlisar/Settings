package com.michaelflisar.settings.core.classes

import android.os.Parcelable
import com.michaelflisar.settings.core.R
import kotlinx.android.parcel.Parcelize

@Parcelize
class SettingsStyle(
        val topLevelStyle: Style = Style.CardsRounded,
        val subLevelStyle: Style = Style.CardsRounded,
        val group: GroupStyle = GroupStyle(),
        val item: ItemStyle = ItemStyle(),
        val cornerRadiusInDp: Int = 12,
        val elevationInDp: Int = 8
) : Parcelable {


    enum class Style {
        Flat,
        CardsRounded,
        CardsRect
    }

    sealed class SubGroupColorMode : Parcelable {
        @Parcelize
        class Full(val lighteningFactorPerLevel: Float = 0.1f) : SubGroupColorMode()

        @Parcelize
        object Border : SubGroupColorMode()
    }

    interface BaseStyle : Parcelable {
        val color: SettingsColor
        val textColor: SettingsColor
        val tintIconWithTextColor: Boolean
    }

    /*
    * not yet fully implemented (only works for global settings)
     */
    @Parcelize
    class ItemStyle(
            override val color: SettingsColor = SettingsColor.ColorRes(android.R.attr.windowBackground),
            override val textColor: SettingsColor = SettingsColor.ColorRes(android.R.attr.textColorSecondary),
            override val tintIconWithTextColor: Boolean = true
    ) : BaseStyle

    @Parcelize
    class GroupStyle(
            override val color: SettingsColor = SettingsColor.ColorRes(R.attr.colorPrimary),
            override val textColor: SettingsColor = SettingsColor.ColorRes(R.attr.colorOnPrimary),
            override val tintIconWithTextColor: Boolean = true,
            val subGroupStyle: SubGroupColorMode = SubGroupColorMode.Border
    ) : BaseStyle
}