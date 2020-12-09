package com.michaelflisar.settings.core.interfaces

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.michaelflisar.settings.core.classes.SettingsDisplaySetup
import com.michaelflisar.settings.core.classes.DialogContext
import com.michaelflisar.settings.core.classes.SettingsMetaData
import com.michaelflisar.settings.core.decorator.ICardGroupItem
import com.michaelflisar.settings.core.settings.base.BaseSettingsGroup
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.IExpandable
import com.mikepenz.fastadapter.IItem

interface ISettingsItem<ValueType, VH : RecyclerView.ViewHolder, Setting: ISetting<ValueType>> : IItem<VH>, IExpandable<VH>, ICardGroupItem {

    var index: Int
    val parentSetting: ISettingsItem<*, *, *>?
    val setup: SettingsDisplaySetup
    val subBaseItems: MutableList<ISettingsItem<*, *, *>>
    val item: Setting
    val itemData: SettingsMetaData

    override fun getLevel(): Int
    fun validForFilter(text: String, checkParents: Boolean = true, checkSubItems: Boolean = true): Boolean
    fun filterSubItems(text: String)
    fun getAllSubItems(filtered: Boolean): List<ISettingsItem<*, *, *>>

    fun onClickEvent(view: View, dialogContext: DialogContext, fastAdapter: FastAdapter<*>, pos: Int): Boolean

    fun addSubItems(items: List<ISettingsItem<*, *, *>>)

    // ICardGroupItem
    override fun getCardGroupParentItem() : ICardGroupItem? = parentSetting
    override fun getSubCardGroupItems(): List<ICardGroupItem> = subBaseItems
    override fun isCardGroupHeader(): Boolean = item is BaseSettingsGroup<*>
}