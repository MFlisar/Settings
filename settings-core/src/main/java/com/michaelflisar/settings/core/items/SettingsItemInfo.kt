package com.michaelflisar.settings.core.items

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.michaelflisar.dialogs.events.DialogInputEvent
import com.michaelflisar.settings.core.R
import com.michaelflisar.settings.core.classes.*
import com.michaelflisar.settings.core.databinding.SettingsItemBaseBinding
import com.michaelflisar.settings.core.databinding.SettingsItemEmptyBinding
import com.michaelflisar.settings.core.interfaces.ISettingsData
import com.michaelflisar.settings.core.interfaces.ISettingsItem
import com.michaelflisar.settings.core.items.base.BaseSettingsItemDialog
import com.michaelflisar.settings.core.items.setups.InfoSetup
import com.michaelflisar.settings.core.settings.base.BaseSetting
import com.michaelflisar.settings.core.tint

class SettingsItemInfo(
        override var parentSetting: ISettingsItem<*, *, *>?,
        override var index: Int,
        override var item: BaseSetting<Unit, *, InfoSetup>,
        override var itemData: SettingsMetaData,
        override var settingsData: ISettingsData,
        setup: SettingsDisplaySetup
) : BaseSettingsItemDialog<Unit, SettingsItemEmptyBinding, InfoSetup, BaseSetting<Unit, *, InfoSetup>>(setup) {

    override val type = R.id.settings_item_info
    override val dialogHandler = DIALOG_HANDLER
    override val endIconType = EndIcon.None
    override val supportsBottomBinding = false

    override fun bindSubView(binding: SettingsItemEmptyBinding, payloads: List<Any>, value: Unit, topBinding: Boolean) {
        // empty
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
        item.setup.backgroundTint.getColor(binding.root.context).let {
//            binding.vBackground.setBackgroundColor(it)
//            binding.vBackground.visibility = View.VISIBLE
            // TODO: those break top "shadow" of cardview...
            binding.cardView.setCardBackgroundColor(it)
//            binding.cardView.backgroundTintList = ColorStateList.valueOf(it)
        }
        item.setup.foregroundTint.getColor(binding.root.context).let {
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

            override fun showDialog(view: View, dialogContext: DialogContext, settingsItem: ISettingsItem<Unit, *, BaseSetting<Unit, *, InfoSetup>>, settingsData: ISettingsData) {
                // show help
                if (settingsItem.item.clickable)
                    settingsItem.setup.feedbackHandler.showHelp(settingsItem, view)
            }

            override fun onDialogEvent(dialogContext: DialogContext, event: DialogInputEvent, setting: BaseSetting<Unit, *, InfoSetup>, settingsData: ISettingsData) {
                // nothing to do...
            }
        }
    }
}