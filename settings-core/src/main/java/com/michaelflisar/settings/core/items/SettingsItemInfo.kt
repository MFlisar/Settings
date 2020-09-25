package com.michaelflisar.settings.core.items

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.michaelflisar.dialogs.events.DialogInputEvent
import com.michaelflisar.settings.core.settings.base.BaseSetting
import com.michaelflisar.settings.core.R
import com.michaelflisar.settings.core.classes.SettingsDisplaySetup
import com.michaelflisar.settings.core.classes.*
import com.michaelflisar.settings.core.databinding.SettingsItemBaseBinding
import com.michaelflisar.settings.core.databinding.SettingsItemEmptyBinding
import com.michaelflisar.settings.core.interfaces.ISettingsItem
import com.michaelflisar.settings.core.items.base.BaseSettingsItemDialog
import com.michaelflisar.settings.core.items.setups.InfoSetup
import com.michaelflisar.settings.core.tint

internal class SettingsItemInfo(
        override var parentSetting: ISettingsItem<*, *, *>?,
        override var index: Int,
        override var item: BaseSetting<Unit, *, InfoSetup>,
        override var itemData: SettingsMetaData,
        override var settingsCustomItem: SettingsCustomObject,
        setup: SettingsDisplaySetup
) : BaseSettingsItemDialog<Unit, SettingsItemEmptyBinding, InfoSetup, BaseSetting<Unit, *, InfoSetup>>(setup) {

    override val type = R.id.settings_item_info
    override val dialogHandler = DIALOG_HANDLER
    override val endIconType = EndIcon.None
    override val supportsBottomBinding = false

    override fun bindSubViews(bindingTop: SettingsItemEmptyBinding, bindingBottom: SettingsItemEmptyBinding?, payloads: List<Any>, topValue: Unit, bottomValue: Unit) {
    }

    override fun createSubBinding(inflater: LayoutInflater, parent: ViewGroup?, topBinding: Boolean): SettingsItemEmptyBinding {
        val binding = SettingsItemEmptyBinding.inflate(inflater, parent, false)
        return binding
    }

    override fun onBindViewsFinished(binding: SettingsItemBaseBinding, subBindingTop: SettingsItemEmptyBinding, subBindingBottom: SettingsItemEmptyBinding?, payloads: List<Any>) {

        // super.onBindViewsFinished(binding, subBindingTop, subBindingBottom, payloads)
        applyStyle(binding, false, getLevel() == 0)
        // this item has it's own tinting logic!!!
        // applyTinting(binding, setup.style.item)

        // text should stretch to the right/end as we do not a have a view for the not existing data!
        move23GuidelineForItemsWithoutData(binding)

        // apply setting
        item.setup.backgroundTint.let {
//            binding.vBackground.setBackgroundColor(it)
//            binding.vBackground.visibility = View.VISIBLE
            // TODO: those break top "shadow" of cardview...
            binding.cardView.setCardBackgroundColor(it)
//            binding.cardView.backgroundTintList = ColorStateList.valueOf(it)
        }
        item.setup.foregroundTint.let {
            binding.tvTitle.setTextColor(it)
            binding.tvSubTitle.setTextColor(it)
            binding.tvBottomTitle.setTextColor(it)
            binding.tvBottomTitle.setTextColor(it)
            binding.ivIcon.tint(it)
            binding.ivHelp.tint(it)
        }
    }

    companion object {

        val DIALOG_HANDLER = object : BaseSettingsDialogEventHandler<Unit, DialogInputEvent, BaseSetting<Unit, *, InfoSetup>>() {

            override val dialogType: Int = R.id.settings_dialog_type_item_info

            override fun showDialog(view: View, dialogContext: DialogContext, item: BaseSetting<Unit, *, InfoSetup>, customItem: SettingsCustomObject) {
                // TODO... Info Details anzeigen???
            }

            override fun onDialogEvent(event: DialogInputEvent, setting: BaseSetting<Unit, *, InfoSetup>, customItem: SettingsCustomObject) {
                // nothing to do...
            }
        }
    }
}