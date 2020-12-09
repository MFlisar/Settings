package com.michaelflisar.settings.core.items

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.michaelflisar.dialogs.events.DialogInputEvent
import com.michaelflisar.dialogs.setups.DialogInput
import com.michaelflisar.settings.core.R
import com.michaelflisar.settings.core.classes.*
import com.michaelflisar.settings.core.databinding.SettingsItemTextBinding
import com.michaelflisar.settings.core.interfaces.ISettingsData
import com.michaelflisar.settings.core.interfaces.ISettingsItem
import com.michaelflisar.settings.core.items.base.BaseSettingsItemDialog
import com.michaelflisar.settings.core.items.setups.InfoSetup
import com.michaelflisar.settings.core.items.setups.TextSetup
import com.michaelflisar.settings.core.settings.base.BaseSetting
import com.michaelflisar.settings.core.show
import com.michaelflisar.text.Text

class SettingsItemText(
        override var parentSetting: ISettingsItem<*, *, *>?,
        override var index: Int,
        override var item: BaseSetting<String, *, TextSetup>,
        override var itemData: SettingsMetaData,
        override var settingsData: ISettingsData,
        setup: SettingsDisplaySetup
) : BaseSettingsItemDialog<String, SettingsItemTextBinding, TextSetup, BaseSetting<String, *, TextSetup>>(setup) {

    override val type = R.id.settings_item_text
    override val dialogHandler = DIALOG_HANDLER

    override fun bindSubView(binding: SettingsItemTextBinding, payloads: List<Any>, value: String, topBinding: Boolean) {
        binding.tvDisplayValue.text = value
    }

    override fun createSubBinding(inflater: LayoutInflater, parent: ViewGroup?, topBinding: Boolean): SettingsItemTextBinding {
        val binding = SettingsItemTextBinding.inflate(inflater, parent, false)
        // set tag for event hook
        setViewTag(binding.tvDisplayValue, topBinding)
        return binding
    }

    companion object {

        val DIALOG_HANDLER = object : BaseSettingsDialogEventHandler<String, DialogInputEvent, BaseSetting<String, *, TextSetup>>() {

            override val dialogType: Int = R.id.settings_dialog_type_item_text

            override fun showDialog(view: View, dialogContext: DialogContext, settingsItem: ISettingsItem<String, *, BaseSetting<String, *, TextSetup>>, settingsData: ISettingsData) {
                val item = settingsItem.item
                val value = item.read(settingsData)
                val extra = createDialogBundle(item, settingsData)
                DialogInput(
                        item.id.toInt(),
                        item.label,
                        DialogInput.InputField(
                                label = item.info,
                                initialText = Text.String(value),
                                allowEmptyText = item.setup.allowEmptyText
                        ),
                        selectText = item.setup.selectText,
                        extra = extra
                )
                        .create()
                        .show(dialogContext)
            }

            override fun onDialogEvent(dialogContext: DialogContext, event: DialogInputEvent, setting: BaseSetting<String, *, TextSetup>, settingsData: ISettingsData) {
                if (event.posClicked()) {

                    // 1) get new value
                    val newValue = event.data?.input ?: ""

                    // 2) save new value -> this will automatically notify callbacks if setting has changed
                    setting.write(settingsData, newValue)
                }
            }
        }
    }
}