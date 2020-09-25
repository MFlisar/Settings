package com.michaelflisar.settings.core.items

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.michaelflisar.settings.core.settings.base.BaseSetting
import com.michaelflisar.settings.core.R
import com.michaelflisar.settings.core.classes.SettingsDisplaySetup
import com.michaelflisar.settings.core.classes.DialogContext
import com.michaelflisar.settings.core.classes.SettingsCustomObject
import com.michaelflisar.settings.core.classes.SettingsMetaData
import com.michaelflisar.settings.core.databinding.SettingsItemBaseBinding
import com.michaelflisar.settings.core.databinding.SettingsItemBoolBinding
import com.michaelflisar.settings.core.interfaces.ISettingsItem
import com.michaelflisar.settings.core.items.base.BaseSettingsItem
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.binding.BindingViewHolder
import com.mikepenz.fastadapter.listeners.CustomEventHook

internal class SettingsItemBool(
        override var parentSetting: ISettingsItem<*, *, *>?,
        override var index: Int,
        override var item: BaseSetting<Boolean, *, Unit>,
        override var itemData: SettingsMetaData,
        override var settingsCustomItem: SettingsCustomObject,
        setup: SettingsDisplaySetup
) : BaseSettingsItem<Boolean, SettingsItemBoolBinding, BaseSetting<Boolean, *, Unit>>(setup) {

    override val type = R.id.settings_item_bool
    override val noStartIconMode = NoStartIconMode.Invisible
    override val endIconType = EndIcon.None

    override fun bindSubViews(bindingTop: SettingsItemBoolBinding, bindingBottom: SettingsItemBoolBinding?, payloads: List<Any>, topValue: Boolean, bottomValue: Boolean) {
        bindingTop.cbChecked.isChecked = topValue
        bindingBottom!!.cbChecked.isChecked = bottomValue
    }

    override fun createSubBinding(inflater: LayoutInflater, parent: ViewGroup?, topBinding: Boolean): SettingsItemBoolBinding {
        val binding = SettingsItemBoolBinding.inflate(inflater, parent, false)
        // set tag for event hook
        setViewTag(binding.cbChecked, topBinding)
        return binding
    }

    override fun onClickEvent(view: View, dialogContext: DialogContext, fastAdapter: FastAdapter<*>, pos: Int): Boolean {
        val itemToChange = getItemToChange()
        itemToChange?.let {
            val value = item.readSetting(itemToChange)
            item.writeSetting(itemToChange, !value)
            return true
        }
        setup.showCantChangeSettingInfo(this, view)
        return false
    }

    companion object {

        val EVENT_HOOK = object : CustomEventHook<SettingsItemBool>() {

            override fun onBindMany(viewHolder: RecyclerView.ViewHolder): List<View>? {
                val baseBinding = (viewHolder as? BindingViewHolder<*>)?.binding as? SettingsItemBaseBinding
                val list: MutableList<View> = ArrayList()
                baseBinding?.let {
                    val subBindingTop = it.root.getTag(R.id.tag_settings_subview_top) as ViewBinding?
                    val subBindingBottom = it.root.getTag(R.id.tag_settings_subview_bottom) as ViewBinding?
                    (subBindingTop as? SettingsItemBoolBinding)?.cbChecked?.let { list.add(it) }
                    (subBindingBottom as? SettingsItemBoolBinding)?.cbChecked?.let { list.add(it) }
                }
                return list.takeIf { it.size > 0 }
            }

            override fun attachEvent(view: View, viewHolder: RecyclerView.ViewHolder) {
                (view as CheckBox).setOnCheckedChangeListener { _, checked ->

                    val item = FastAdapter.getHolderAdapterItem<BaseSettingsItem<Boolean, SettingsItemBoolBinding, BaseSetting<Boolean, *, *>>>(viewHolder)

                    item?.let {

                        val customItem = item.settingsCustomItem
                        val isTopView = hasTopViewTag(view)
                        val data = item.item

                        if (isTopView) {
                            // may save custom or global value
                            data.writeSetting(customItem, checked)
                        } else {
                            // always saves global value
                            data.writeSetting(SettingsCustomObject.None, checked)
                        }
                    }
                }
            }
        }
    }
}