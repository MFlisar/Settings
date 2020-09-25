package com.michaelflisar.settings.demo.classes

import com.michaelflisar.settings.core.classes.SettingsIcon
import com.michaelflisar.settings.core.interfaces.ISettingsIcon
import com.michaelflisar.settings.core.interfaces.ISettingsListItem
import com.michaelflisar.settings.demo.R
import kotlinx.android.parcel.Parcelize

@Parcelize
enum class AppStyleEnum(
        override val id: Long,
        override val icon: ISettingsIcon,
        val label: String
) : ISettingsListItem {

    Classic(1L,  SettingsIcon(R.drawable.ic_style_black_24dp), "Classic"),
    Modern(2L, SettingsIcon(R.drawable.ic_style_black_24dp), "Modern"),
    Colorful(3L, SettingsIcon(R.drawable.ic_style_black_24dp), "Colorful");

    override fun getDisplayValue() = label

    companion object {
        val LIST: ArrayList<ISettingsListItem> = ArrayList(values().toList())
    }
}