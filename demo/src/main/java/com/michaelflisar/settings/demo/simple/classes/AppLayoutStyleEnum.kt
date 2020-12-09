package com.michaelflisar.settings.demo.simple.classes

import com.michaelflisar.settings.core.classes.SettingsIcon
import com.michaelflisar.settings.core.interfaces.ISettingsIcon
import com.michaelflisar.settings.core.interfaces.ISettingsListItem
import com.michaelflisar.settings.demo.R
import kotlinx.parcelize.Parcelize

@Parcelize
enum class AppLayoutStyleEnum(
        override val id: Long,
        override val icon: ISettingsIcon,
        val label: String
) : ISettingsListItem {

    List(1L,  SettingsIcon(R.drawable.ic_baseline_list_24), "List"),
    ViewPager(2L, SettingsIcon(R.drawable.ic_baseline_double_arrow_24), "ViewPager");

    override fun getDisplayValue() = label

    companion object {
        val LIST: ArrayList<ISettingsListItem> = ArrayList(values().toList())
    }
}