package com.michaelflisar.settings.demo.simple.classes

import com.michaelflisar.settings.core.classes.SettingsIcon
import com.michaelflisar.settings.core.interfaces.ISettingsIcon
import com.michaelflisar.settings.core.interfaces.ISettingsListItem
import com.michaelflisar.settings.demo.R
import kotlinx.parcelize.Parcelize

@Parcelize
enum class BookmarkEnum(
        override val id: Long,
        override val icon: ISettingsIcon,
        val label: String
) : ISettingsListItem {

    Bookmark1(1L,  SettingsIcon(R.drawable.ic_baseline_bookmarks_24), "BM1"),
    Bookmark2(2L, SettingsIcon(R.drawable.ic_baseline_bookmarks_24), "BM2"),
    Bookmark3(3L, SettingsIcon(R.drawable.ic_baseline_bookmarks_24), "BM3"),
    Bookmark4(4L, SettingsIcon(R.drawable.ic_baseline_bookmarks_24), "BM4"),
    Bookmark5(5L, SettingsIcon(R.drawable.ic_baseline_bookmarks_24), "BM5"),
    Bookmark6(6L, SettingsIcon(R.drawable.ic_baseline_bookmarks_24), "BM6"),
    ;

    override fun getDisplayValue() = label

    companion object {
        val LIST: ArrayList<ISettingsListItem> = ArrayList(values().toList())
    }
}