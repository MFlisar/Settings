package com.michaelflisar.settings.core.classes

import android.os.Parcelable
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.michaelflisar.settings.core.Settings
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SettingsState(
        var filter: String = "",
        val expandedIds: ArrayList<Long> = ArrayList(),
        // List
        val currentListPosition: Int? = null,
        // Pager
        val page: Int? = null
) : Parcelable {

    constructor(setting: Settings, definition: Settings.Definition.ListDefinitions) : this(
            setting.filter,
            definition.getExpandedIds(),
            definition.rv.computeVerticalScrollOffset()
    )

    constructor(setting: Settings, definition: Settings.Definition.PagerDefinitons) : this(
            setting.filter,
            page = definition.viewPager.currentItem
    )

    constructor(setting: Settings, definition: Settings.Definition.PagerDefinitons2) : this(
            setting.filter,
            page = definition.viewPager.currentItem
    )

    fun restore(settings: Settings, rv: RecyclerView) {
        settings.filter(filter)
        currentListPosition?.let {
            rv.scrollTo(0, it)
        }
    }

    fun restore(settings: Settings, vp: ViewPager) {
        settings.filter(filter)
        page?.let {
            vp.currentItem = it
        }
    }

    fun restore(settings: Settings, vp: ViewPager2) {
        settings.filter(filter)
        page?.let {
            vp.currentItem = it
        }
    }
}