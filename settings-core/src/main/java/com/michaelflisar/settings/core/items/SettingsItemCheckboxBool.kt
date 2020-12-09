package com.michaelflisar.settings.core.items

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.michaelflisar.settings.core.R
import com.michaelflisar.settings.core.classes.DialogContext
import com.michaelflisar.settings.core.classes.SettingsDisplaySetup
import com.michaelflisar.settings.core.classes.SettingsMetaData
import com.michaelflisar.settings.core.databinding.SettingsItemBaseBinding
import com.michaelflisar.settings.core.databinding.SettingsItemCheckboxBoolBinding
import com.michaelflisar.settings.core.interfaces.ISettingsData
import com.michaelflisar.settings.core.interfaces.ISettingsItem
import com.michaelflisar.settings.core.items.base.BaseSettingsItem
import com.michaelflisar.settings.core.items.setups.BoolSetup
import com.michaelflisar.settings.core.settings.base.BaseSetting
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.binding.BindingViewHolder
import com.mikepenz.fastadapter.listeners.CustomEventHook

class SettingsItemCheckboxBool(
        override var parentSetting: ISettingsItem<*, *, *>?,
        override var index: Int,
        override var item: BaseSetting<Boolean, *, BoolSetup>,
        override var itemData: SettingsMetaData,
        override var settingsData: ISettingsData,
        setup: SettingsDisplaySetup
) : BaseSettingsItem<Boolean, SettingsItemCheckboxBoolBinding, BaseSetting<Boolean, *, BoolSetup>>(setup) {

    override val type = R.id.settings_item_checkbox_bool
    override val noStartIconMode = NoStartIconMode.Invisible
    override val endIconType = EndIcon.None

    override fun bindSubView(binding: SettingsItemCheckboxBoolBinding, payloads: List<Any>, value: Boolean, topBinding: Boolean) {
        binding.cbChecked.isChecked = value
    }

    override fun createSubBinding(inflater: LayoutInflater, parent: ViewGroup?, topBinding: Boolean): SettingsItemCheckboxBoolBinding {
        val binding = SettingsItemCheckboxBoolBinding.inflate(inflater, parent, false)
        // set tag for event hook
        setViewTag(binding.cbChecked, topBinding)
        return binding
    }

    override fun onClickEvent(view: View, dialogContext: DialogContext, fastAdapter: FastAdapter<*>, pos: Int): Boolean {
        if (!item.setup.changeStateOnClick) {
            return false
        }
        val itemToChange = getItemToChange()
        itemToChange?.let {
            val value = item.read(itemToChange)
            item.write(itemToChange, !value)
            return true
        }
        setup.showCantChangeSettingInfo(this, view)
        return false
    }

    companion object {

        val EVENT_HOOK = object : CustomEventHook<SettingsItemCheckboxBool>() {

            override fun onBindMany(viewHolder: RecyclerView.ViewHolder): List<View>? {
                val baseBinding = (viewHolder as? BindingViewHolder<*>)?.binding as? SettingsItemBaseBinding
                val list: MutableList<View> = ArrayList()
                baseBinding?.let {
                    val subBindingTop = it.root.getTag(R.id.tag_settings_subview_top) as ViewBinding?
                    val subBindingBottom = it.root.getTag(R.id.tag_settings_subview_bottom) as ViewBinding?
                    (subBindingTop as? SettingsItemCheckboxBoolBinding)?.cbChecked?.let { list.add(it) }
                    (subBindingBottom as? SettingsItemCheckboxBoolBinding)?.cbChecked?.let { list.add(it) }
                }
                return list.takeIf { it.size > 0 }
            }

            override fun attachEvent(view: View, viewHolder: RecyclerView.ViewHolder) {
                (view as CheckBox).setOnCheckedChangeListener { _, checked ->

                    val item = FastAdapter.getHolderAdapterItem<BaseSettingsItem<Boolean, SettingsItemCheckboxBoolBinding, BaseSetting<Boolean, *, *>>>(viewHolder)

                    item?.let {

                        val settingsData = item.settingsData
                        val isTopView = hasTopViewTag(view)
                        val data = item.item

                        if (isTopView) {
                            // may save custom or global value
                            data.write(settingsData, checked)
                        } else {
                            // always saves global value
                            data.write(settingsData.global, checked)
                        }
                    }
                }
            }
        }
    }
}