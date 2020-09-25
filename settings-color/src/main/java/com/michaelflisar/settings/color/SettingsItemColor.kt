package com.michaelflisar.settings.color

import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.michaelflisar.dialogs.events.DialogColorEvent
import com.michaelflisar.dialogs.setups.DialogColor
import com.michaelflisar.settings.color.databinding.SettingsItemColorBinding
import com.michaelflisar.settings.core.settings.base.BaseSetting
import com.michaelflisar.settings.core.classes.SettingsDisplaySetup
import com.michaelflisar.settings.core.classes.*
import com.michaelflisar.settings.core.interfaces.ISettingsItem
import com.michaelflisar.settings.core.items.base.BaseSettingsItemDialog
import com.michaelflisar.settings.core.show

class SettingsItemColor(
        override var parentSetting: ISettingsItem<*, *, *>?,
        override var index: Int,
        override var item: BaseSetting<Int, *, ColorSetup>,
        override var itemData: SettingsMetaData,
        override var settingsCustomItem: SettingsCustomObject,
        setup: SettingsDisplaySetup
) : BaseSettingsItemDialog<Int, SettingsItemColorBinding, ColorSetup, BaseSetting<Int, *, ColorSetup>>(setup) {

    override val type: Int = R.id.settings_item_color
    override val dialogHandler = DIALOG_HANDLER

    override fun bindSubViews(bindingTop: SettingsItemColorBinding, bindingBottom: SettingsItemColorBinding?, payloads: List<Any>, topValue: Int, bottomValue: Int) {
        (bindingTop.vDisplayValue.background as GradientDrawable).setColor(topValue)
        (bindingBottom!!.vDisplayValue.background as GradientDrawable).setColor(bottomValue)
    }

    override fun createSubBinding(inflater: LayoutInflater, parent: ViewGroup?, topBinding: Boolean): SettingsItemColorBinding {
        val binding = SettingsItemColorBinding.inflate(inflater, parent, false)
        // set tag for event hook
        setViewTag(binding.vDisplayValue, topBinding)
        return binding
    }

    companion object {

        val DIALOG_HANDLER = object : BaseSettingsDialogEventHandler<Int, DialogColorEvent, BaseSetting<Int, *, ColorSetup>>() {

            override val dialogType: Int = R.id.settings_dialog_type_item_color

            override fun showDialog(view: View, dialogContext: DialogContext, item: BaseSetting<Int, *, ColorSetup>, customItem: SettingsCustomObject) {

                val value = item.readSetting(customItem)
                val extra = createDialogBundle(item, customItem)

                DialogColor(
                        item.id.toInt(),
                        item.label,
                        darkTheme = dialogContext.setup.useDarkTheme,
                        color = value,
                        showAlpha = item.setup.supportAlpha,
                        extra = extra
                )
                        .create()
                        .show(dialogContext)
            }

            override fun onDialogEvent(event: DialogColorEvent, setting: BaseSetting<Int, *, ColorSetup>, customItem: SettingsCustomObject) {
                val selectedItem = event.data?.color
                selectedItem?.let {

                    // 1) get new value
                    val newValue = selectedItem

                    // 2) save new value -> this will automatically notify callbacks if setting has changed
                    setting.writeSetting(customItem, newValue)
                }
            }
        }
    }
}