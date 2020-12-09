package com.michaelflisar.settings.core.items

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.michaelflisar.dialogs.events.DialogNumberEvent
import com.michaelflisar.dialogs.setups.DialogNumber
import com.michaelflisar.dialogs.setups.DialogNumberPicker
import com.michaelflisar.settings.core.R
import com.michaelflisar.settings.core.classes.*
import com.michaelflisar.settings.core.databinding.SettingsItemTextBinding
import com.michaelflisar.settings.core.interfaces.ISettingsData
import com.michaelflisar.settings.core.interfaces.ISettingsItem
import com.michaelflisar.settings.core.items.base.BaseSettingsItemDialog
import com.michaelflisar.settings.core.items.setups.InfoSetup
import com.michaelflisar.settings.core.items.setups.IntSetup
import com.michaelflisar.settings.core.settings.base.BaseSetting
import com.michaelflisar.settings.core.show

class SettingsItemInt(
        override var parentSetting: ISettingsItem<*, *, *>?,
        override var index: Int,
        override var item: BaseSetting<Int, *, IntSetup>,
        override var itemData: SettingsMetaData,
        override var settingsData: ISettingsData,
        setup: SettingsDisplaySetup
) : BaseSettingsItemDialog<Int, SettingsItemTextBinding, IntSetup, BaseSetting<Int, *, IntSetup>>(setup) {

    override val type = R.id.settings_item_int
    override val dialogHandler = DIALOG_HANDLER

    override fun bindSubView(binding: SettingsItemTextBinding, payloads: List<Any>, value: Int, topBinding: Boolean) {
        binding.tvDisplayValue.text = item.setup.formatValue(value)
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

            override fun showDialog(view: View, dialogContext: DialogContext, settingsItem: ISettingsItem<Int, *, BaseSetting<Int, *, IntSetup>>, settingsData: ISettingsData) {
                val item = settingsItem.item
                val value = item.read(settingsData)
                val extra = createDialogBundle(item, settingsData)
                val setup = item.setup
                val dlg = when (setup) {
                    is IntSetup.Input -> {
                        DialogNumber(
                                item.id.toInt(),
                                title = item.label,
                                text = if (setup.showInfoInDialog) item.info else null,
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
                                title = item.label,
                                text = if (setup.showInfoInDialog) item.info else null,
                                initialValue = value,
                                extra = extra,
                                min = setup.min,
                                max = setup.max,
                                valueFormatRes = setup.valueFormatRes,
                                step = setup.step
                        )
                                .create()
                    }
                }
                dlg.show(dialogContext)
            }

            override fun onDialogEvent(dialogContext: DialogContext, event: DialogNumberEvent, setting: BaseSetting<Int, *, IntSetup>, settingsData: ISettingsData) {
                if (event.posClicked()) {

                    // 1) get new value
                    val newValue = event.data?.value

                    // 2) save new value -> this will automatically notify callbacks if setting has changed
                    if (newValue != null) {
                        setting.write(settingsData, newValue)
                    }
                }
            }
        }
    }
}