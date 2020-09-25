package com.michaelflisar.settings.core.items.base

import android.view.View
import androidx.viewbinding.ViewBinding
import com.michaelflisar.settings.core.settings.base.BaseSetting
import com.michaelflisar.settings.core.classes.SettingsDisplaySetup
import com.michaelflisar.settings.core.classes.DialogContext
import com.michaelflisar.settings.core.classes.SettingsCustomObject
import com.michaelflisar.settings.core.databinding.SettingsItemBaseBinding
import com.michaelflisar.settings.core.enableRecursively
import com.michaelflisar.settings.core.enums.CustomLayoutStyle
import com.michaelflisar.settings.core.interfaces.ISettingsItem
import com.mikepenz.fastadapter.ClickListener
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.binding.BindingViewHolder

abstract class BaseSettingsItem<ValueType, SubViewBinding : ViewBinding, Setting : BaseSetting<ValueType, *, *>>(
        setup: SettingsDisplaySetup
) : BaseBaseSettingsItem<ValueType, SubViewBinding, Setting>(setup) {

    override val supportsBottomBinding: Boolean = true

    override fun onBindViewsFinished(binding: SettingsItemBaseBinding, subBindingTop: SubViewBinding, subBindingBottom: SubViewBinding?, payloads: List<Any>) {
        when (settingsCustomItem) {
            SettingsCustomObject.None -> {
                // nothing to do
            }
            is SettingsCustomObject.Element -> {
                val isCustomSettingEnabled = item.readSettingEnabled(settingsCustomItem as SettingsCustomObject.Element)
                binding.sivIcon.setIconEnabled(isCustomSettingEnabled, false)

                binding.tvTitle.isEnabled = isCustomSettingEnabled
                binding.tvSubTitle.isEnabled = isCustomSettingEnabled
                subBindingTop.root.enableRecursively(isCustomSettingEnabled)

                val globalValueInBottomViewIsEditable = setup.customLayout == CustomLayoutStyle.LargeEditable
                binding.tvBottomTitle.isEnabled = !isCustomSettingEnabled
                subBindingBottom?.root?.enableRecursively(!isCustomSettingEnabled && globalValueInBottomViewIsEditable)
            }
        }

        applyStyle(binding, false, getLevel() == 0)
        applyTinting(binding, setup.style.item)
    }

    final override fun bindSubViews(subBindingTop: SubViewBinding, subBindingBottom: SubViewBinding?, payloads: List<Any>) {
        val topValue = item.readSetting(settingsCustomItem)
        val bottomValue = item.readSetting(SettingsCustomObject.None)
        bindSubViews(subBindingTop, subBindingBottom, payloads, topValue, bottomValue)
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

    abstract fun bindSubViews(bindingTop: SubViewBinding, bindingBottom: SubViewBinding?, payloads: List<Any>, topValue: ValueType, bottomValue: ValueType)

    fun getItemToChange(): SettingsCustomObject? {
        val isGlobal = settingsCustomItem is SettingsCustomObject.None
        val isCustomAndEnabled = !isGlobal && item.readSettingEnabled(settingsCustomItem as SettingsCustomObject.Element)
        val isChangeAllowed = isGlobal ||
                setup.customLayout == CustomLayoutStyle.LargeEditable ||
                isCustomAndEnabled

        if (isChangeAllowed) {
            val changeGlobalValue = isGlobal || !isCustomAndEnabled
            return if (changeGlobalValue) SettingsCustomObject.None else settingsCustomItem
        }

        return null
    }
}