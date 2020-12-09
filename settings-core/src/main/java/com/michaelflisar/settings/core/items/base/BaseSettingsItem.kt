package com.michaelflisar.settings.core.items.base

import android.view.View
import androidx.viewbinding.ViewBinding
import com.michaelflisar.settings.core.classes.DialogContext
import com.michaelflisar.settings.core.classes.SettingsDisplaySetup
import com.michaelflisar.settings.core.databinding.SettingsItemBaseBinding
import com.michaelflisar.settings.core.enableRecursively
import com.michaelflisar.settings.core.enums.CustomLayoutStyle
import com.michaelflisar.settings.core.interfaces.ISettingsData
import com.michaelflisar.settings.core.interfaces.ISettingsItem
import com.michaelflisar.settings.core.settings.base.BaseSetting
import com.mikepenz.fastadapter.ClickListener
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.binding.BindingViewHolder

abstract class BaseSettingsItem<ValueType, SubViewBinding : ViewBinding, Setting : BaseSetting<ValueType, *, *>>(
        setup: SettingsDisplaySetup
) : BaseBaseSettingsItem<ValueType, SubViewBinding, Setting>(setup) {

    override val supportsBottomBinding: Boolean = true

    override fun onBindViewsFinished(binding: SettingsItemBaseBinding, subBindingTop: SubViewBinding, subBindingBottom: SubViewBinding?, payloads: List<Any>) {
        if (settingsData.isGlobal) {
            // nothing to do
        } else {
            val isCustomSettingEnabled = item.readIsEnabled(settingsData as ISettingsData.Element)
            binding.sivIcon.setIconEnabled(isCustomSettingEnabled, false)

            binding.tvTitle.isEnabled = isCustomSettingEnabled
            binding.tvSubTitle.isEnabled = isCustomSettingEnabled
            subBindingTop.root.enableRecursively(isCustomSettingEnabled)

            val globalValueInBottomViewIsEditable = setup.customLayout == CustomLayoutStyle.LargeEditable
            binding.tvBottomTitle.isEnabled = !isCustomSettingEnabled
            subBindingBottom?.root?.enableRecursively(!isCustomSettingEnabled && globalValueInBottomViewIsEditable)
        }

        applyStyle(binding, false, getLevel() == 0)
        applyTinting(binding, setup.style.item)
    }

    final override fun bindSubViewTop(subBindingTop: SubViewBinding, payloads: List<Any>) {
        val value = item.read(settingsData)
        bindSubView(subBindingTop, payloads, value, true)
    }

    final override fun bindSubViewBottom(subBindingBottom: SubViewBinding, payloads: List<Any>) {
        val value = item.read(settingsData.global)
        bindSubView(subBindingBottom, payloads, value, false)
    }

    override fun onClickEvent(view: View, dialogContext: DialogContext, fastAdapter: FastAdapter<*>, pos: Int): Boolean {
        return false
    }

    override var onItemClickListener: ClickListener<ISettingsItem<ValueType, BindingViewHolder<SettingsItemBaseBinding>, Setting>>?
        get() = null
        set(_) {}

    override var onPreItemClickListener: ClickListener<ISettingsItem<ValueType, BindingViewHolder<SettingsItemBaseBinding>, Setting>>?
        get() = null
        set(_) {}

    abstract fun bindSubView(binding: SubViewBinding, payloads: List<Any>, value: ValueType, topBinding: Boolean)

    fun getItemToChange(): ISettingsData? {
        val isGlobal = settingsData.isGlobal
        val isCustomAndEnabled = !isGlobal && item.readIsEnabled(settingsData as ISettingsData.Element)
        val isChangeAllowed = isGlobal ||
                (item.clickable && !item.canHoldData) ||
                setup.customLayout == CustomLayoutStyle.LargeEditable ||
                isCustomAndEnabled

        if (isChangeAllowed) {
            val changeGlobalValue = isGlobal || !isCustomAndEnabled
            return if (changeGlobalValue) settingsData.global else settingsData
        }

        return null
    }
}