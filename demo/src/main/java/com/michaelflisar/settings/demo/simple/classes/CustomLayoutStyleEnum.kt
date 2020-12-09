package com.michaelflisar.settings.demo.simple.classes

import com.michaelflisar.settings.core.classes.SettingsIcon
import com.michaelflisar.settings.core.enums.CustomLayoutStyle
import com.michaelflisar.settings.core.interfaces.ISettingsIcon
import com.michaelflisar.settings.core.interfaces.ISettingsListItem
import com.michaelflisar.settings.demo.R
import kotlinx.parcelize.Parcelize

@Parcelize
enum class CustomLayoutStyleEnum(
        override val id: Long,
        override val icon: ISettingsIcon,
        val label: String,
        val wrapped: CustomLayoutStyle
) : ISettingsListItem {

    Compact(1L,  SettingsIcon(R.drawable.ic_baseline_aspect_ratio_24), "Compact", CustomLayoutStyle.Compact),
    LargeEditable(2L, SettingsIcon(R.drawable.ic_baseline_aspect_ratio_24), "Large Editable", CustomLayoutStyle.LargeEditable),
    LargeInfo(3L, SettingsIcon(R.drawable.ic_baseline_aspect_ratio_24), "Large Info", CustomLayoutStyle.LargeInfo)
    ;

    override fun getDisplayValue() = label

    companion object {
        val LIST: ArrayList<ISettingsListItem> = ArrayList(values().toList())

        fun getById(id: Long) = values().first { it.id == id }
    }
}