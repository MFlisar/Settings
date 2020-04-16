package com.michaelflisar.settings.core.items

import androidx.viewbinding.ViewBinding
import com.michaelflisar.settings.core.SettingsDisplaySetup
import com.michaelflisar.settings.core.interfaces.IBaseSetting
import com.mikepenz.fastadapter.IExpandable
import com.mikepenz.fastadapter.IParentItem
import com.mikepenz.fastadapter.ISubItem
import com.mikepenz.fastadapter.MutableSubItemList
import com.mikepenz.fastadapter.binding.AbstractBindingItem
import com.mikepenz.fastadapter.binding.BindingViewHolder

abstract class BaseSettingsItem<T : ViewBinding>(

) : AbstractBindingItem<T>(), IExpandable<BindingViewHolder<T>> {

    abstract var parentSetting: BaseSettingsItem<*>?
    abstract var index: Int
    abstract val item: IBaseSetting
    abstract val setup: SettingsDisplaySetup

    override var identifier: Long
        get() = item.id
        set(value) { /* ignored */ }

    // ----------
    // functions
    // ----------

    fun init() {
        if (!setup.expandable)
            isExpanded = true
    }

    fun getLevel(): Int = parentSetting?.getLevel()?.plus(1) ?: 0

    fun getNumberingInfo(index: Int): String {
        var info = "${index + 1}"
        var item: BaseSettingsItem<*>? = this
        while (item?.parentSetting != null) {
            val parent = item.parentSetting!!
            val subIndex = parent.index
            info = "${subIndex + 1}.$info"
            item = parent
        }
        return info
    }

    fun validForFilter(text: String?, checkParents: Boolean = true, checkSubItems: Boolean = true): Boolean {

        if (text == null || text.isEmpty())
            return true

        if (item.label.text?.contains(text, true) == true)
            return true

        if (checkParents && parentSetting != null)
            return parentSetting!!.validForFilter(text, true, false)

        if (checkSubItems) {
            subItems.forEach {
                if ((it as BaseSettingsItem<*>).validForFilter(text, false, true))
                    return true
            }
        }

        return false
    }

    // ----------
    // IExpandable
    // ----------

    private val _subItems = MutableSubItemList<ISubItem<*>>(this)

    override var isExpanded: Boolean = false

    override var parent: IParentItem<*>?
        get() = parentSetting
        set(value) {
            parentSetting = value as BaseSettingsItem<*>
        }

    override var subItems: MutableList<ISubItem<*>>
        set(value) = _subItems.setNewList(value)
        get() = _subItems

    override val isAutoExpanding: Boolean
        get() = setup.expandable

    override var isSelectable: Boolean
        get() = subItems.isEmpty()
        set(_) {}
}