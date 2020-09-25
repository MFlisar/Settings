package com.michaelflisar.settings.core.items

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.michaelflisar.dialogs.events.DialogInputEvent
import com.michaelflisar.dialogs.setups.DialogInput
import com.michaelflisar.settings.core.settings.base.BaseSetting
import com.michaelflisar.settings.core.R
import com.michaelflisar.settings.core.classes.SettingsDisplaySetup
import com.michaelflisar.settings.core.classes.*
import com.michaelflisar.settings.core.databinding.SettingsItemTextBinding
import com.michaelflisar.settings.core.interfaces.ISettingsItem
import com.michaelflisar.settings.core.items.base.BaseSettingsItemDialog
import com.michaelflisar.settings.core.items.setups.TextSetup
import com.michaelflisar.settings.core.show
import com.michaelflisar.text.Text

internal class SettingsItemText(
        override var parentSetting: ISettingsItem<*, *, *>?,
        override var index: Int,
        override var item: BaseSetting<String, *, TextSetup>,
        override var itemData: SettingsMetaData,
        override var settingsCustomItem: SettingsCustomObject,
        setup: SettingsDisplaySetup
) : BaseSettingsItemDialog<String, SettingsItemTextBinding, TextSetup, BaseSetting<String, *, TextSetup>>(setup) {

    override val type = R.id.settings_item_text
    override val dialogHandler = DIALOG_HANDLER

    override fun bindSubViews(bindingTop: SettingsItemTextBinding, bindingBottom: SettingsItemTextBinding?, payloads: List<Any>, topValue: String, bottomValue: String) {
        bindingTop.tvDisplayValue.text = topValue
        bindingBottom!!.tvDisplayValue.text = bottomValue
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

            override fun showDialog(view: View, dialogContext: DialogContext, item: BaseSetting<String, *, TextSetup>, customItem: SettingsCustomObject) {
                val value = item.readSetting(customItem)
                val extra = createDialogBundle(item, customItem)
                DialogInput(
                        item.id.toInt(),
                        item.label,
                        DialogInput.InputField(
                                label = item.info,
                                initialText = Text.String(value),
                                allowEmptyText = item.setup.allowEmptyText
                        ),
                        extra = extra
                )
                        .create()
                        .show(dialogContext)
            }

            override fun onDialogEvent(event: DialogInputEvent, setting: BaseSetting<String, *, TextSetup>, customItem: SettingsCustomObject) {
                if (event.posClicked()) {

                    // 1) get new value
                    val newValue = event.data?.input ?: ""

                    // 2) save new value -> this will automatically notify callbacks if setting has changed
                    setting.writeSetting(customItem, newValue)
                }
            }
        }
    }
}