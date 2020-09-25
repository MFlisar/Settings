package com.michaelflisar.settings.core.items

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.michaelflisar.dialogs.events.DialogNumberEvent
import com.michaelflisar.dialogs.setups.DialogNumber
import com.michaelflisar.dialogs.setups.DialogNumberPicker
import com.michaelflisar.settings.core.settings.base.BaseSetting
import com.michaelflisar.settings.core.R
import com.michaelflisar.settings.core.classes.SettingsDisplaySetup
import com.michaelflisar.settings.core.classes.*
import com.michaelflisar.settings.core.databinding.SettingsItemTextBinding
import com.michaelflisar.settings.core.interfaces.ISettingsItem
import com.michaelflisar.settings.core.items.base.BaseSettingsItemDialog
import com.michaelflisar.settings.core.items.setups.IntSetup
import com.michaelflisar.settings.core.show

internal class SettingsItemInt(
        override var parentSetting: ISettingsItem<*, *, *>?,
        override var index: Int,
        override var item: BaseSetting<Int, *, IntSetup>,
        override var itemData: SettingsMetaData,
        override var settingsCustomItem: SettingsCustomObject,
        setup: SettingsDisplaySetup
) : BaseSettingsItemDialog<Int, SettingsItemTextBinding, IntSetup, BaseSetting<Int, *, IntSetup>>(setup) {

    override val type = R.id.settings_item_int
    override val dialogHandler = DIALOG_HANDLER

    override fun bindSubViews(bindingTop: SettingsItemTextBinding, bindingBottom: SettingsItemTextBinding?, payloads: List<Any>, topValue: Int, bottomValue: Int) {
        bindingTop.tvDisplayValue.text = topValue.toString()
        bindingBottom!!.tvDisplayValue.text = bottomValue.toString()
    }

    override fun createSubBinding(inflater: LayoutInflater, parent: ViewGroup?, topBinding: Boolean): SettingsItemTextBinding {
        val binding = SettingsItemTextBinding.inflate(inflater, parent, false)
        // set tag for event hook
        setViewTag(binding.tvDisplayValue, topBinding)
        return binding
    }

    companion object {

        val DIALOG_HANDLER = object : BaseSettingsDialogEventHandler<Int, DialogNumberEvent, BaseSetting<Int, *, IntSetup>>() {

            override val dialogType: Int = R.id.settings_dialog_type_item_int

            override fun showDialog(view: View, dialogContext: DialogContext, item: BaseSetting<Int, *, IntSetup>, customItem: SettingsCustomObject) {
                val value = item.readSetting(customItem)
                val extra = createDialogBundle(item, customItem)
                val setup = item.setup
                val dlg = when (setup) {
                    is IntSetup.Input -> {
                        DialogNumber(
                                item.id.toInt(),
                                item.label,
                                text = item.label,
                                initialValue = value,
                                extra = extra,
                                min = item.setup.min,
                                max = item.setup.max,
                                errorMessage = setup.errorMessage
                        )
                                .create()
                    }
                    is IntSetup.Picker -> {
                        DialogNumberPicker(
                                item.id.toInt(),
                                item.label,
                                text = item.label,
                                initialValue = value,
                                extra = extra,
                                min = setup.min,
                                max = setup.max,
                                step = setup.step
                        )
                                .create()
                    }
                }
                dlg.show(dialogContext)
            }

            override fun onDialogEvent(event: DialogNumberEvent, setting: BaseSetting<Int, *, IntSetup>, customItem: SettingsCustomObject) {
                if (event.posClicked()) {

                    // 1) get new value
                    val newValue = event.data?.value

                    // 2) save new value -> this will automatically notify callbacks if setting has changed
                    if (newValue != null) {
                        setting.writeSetting(customItem, newValue)
                    }
                }
            }
        }
    }
}