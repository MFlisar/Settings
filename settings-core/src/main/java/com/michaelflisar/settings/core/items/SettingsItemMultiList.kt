package com.michaelflisar.settings.core.items

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.michaelflisar.dialogs.enums.IconSize
import com.michaelflisar.dialogs.events.DialogListEvent
import com.michaelflisar.dialogs.setups.DialogList
import com.michaelflisar.settings.core.settings.base.BaseSetting
import com.michaelflisar.settings.core.R
import com.michaelflisar.settings.core.classes.SettingsDisplaySetup
import com.michaelflisar.settings.core.SettingsUtils
import com.michaelflisar.settings.core.classes.*
import com.michaelflisar.settings.core.databinding.SettingsItemTextBinding
import com.michaelflisar.settings.core.interfaces.ISettingsItem
import com.michaelflisar.settings.core.items.base.BaseSettingsItemDialog
import com.michaelflisar.settings.core.items.setups.MultiListSetup
import com.michaelflisar.settings.core.settings.MultiListSetting.MultiListData
import com.michaelflisar.settings.core.show

internal class SettingsItemMultiList(
        override var parentSetting: ISettingsItem<*, *, *>?,
        override var index: Int,
        override var item: BaseSetting<MultiListData, *, MultiListSetup>,
        override var itemData: SettingsMetaData,
        override var settingsCustomItem: SettingsCustomObject,
        setup: SettingsDisplaySetup
) : BaseSettingsItemDialog<MultiListData, SettingsItemTextBinding, MultiListSetup, BaseSetting<MultiListData, *, MultiListSetup>>(setup) {

    override val type = R.id.settings_item_multi_list
    override val dialogHandler = DIALOG_HANDLER

    override fun bindSubViews(bindingTop: SettingsItemTextBinding, bindingBottom: SettingsItemTextBinding?, payloads: List<Any>, topValue: MultiListData, bottomValue: MultiListData) {
        bindingTop.tvDisplayValue.text =  topValue.getDisplayValue(item.setup.displayType)
        bindingBottom!!.tvDisplayValue.text = bottomValue.getDisplayValue(item.setup.displayType)
    }

    override fun createSubBinding(inflater: LayoutInflater, parent: ViewGroup?, topBinding: Boolean): SettingsItemTextBinding {
        val binding = SettingsItemTextBinding.inflate(inflater, parent, false)
        // set tag for event hook
        setViewTag(binding.tvDisplayValue, topBinding)
        return binding
    }

    companion object {

        val DIALOG_HANDLER = object : BaseSettingsDialogEventHandler<MultiListData, DialogListEvent, BaseSetting<MultiListData, *, MultiListSetup>>() {

            override val dialogType: Int = R.id.settings_dialog_type_item_multi_list

            override fun showDialog(view: View, dialogContext: DialogContext, item: BaseSetting<MultiListData, *, MultiListSetup>, customItem: SettingsCustomObject) {

                val value = item.readSetting(customItem)
                val extra = createDialogBundle(item, customItem)

                val items = item.setup.items
                        .map { DialogList.Item.Custom(ListItem(it)) }

                val selectedIndex = value.items.map { item.setup.getIndex(it) }

                DialogList(
                        item.id.toInt(),
                        item.label,
                        text = item.info,
                        items = items,
                        selectionMode = DialogList.SelectionMode.Multi,
                        initialMultiSelection = selectedIndex.toIntArray(),
                        checkMark = R.drawable.checkmark,
                        iconColorTint = SettingsUtils.attrColor(dialogContext.context, R.attr.colorOnBackground),
                        iconSize = IconSize.Medium,
                        noImageVisibility = View.GONE,
                        extra = extra
                )
                        .create()
                        .show(dialogContext)
            }

            override fun onDialogEvent(event: DialogListEvent, setting: BaseSetting<MultiListData, *, MultiListSetup>, customItem: SettingsCustomObject) {

                val selectedItem: List<ListItem>? = event.data?.items as? List<ListItem>
                selectedItem?.let {

                    // 1) get new value
                    val newValue = MultiListData(it.map { it.item })

                    // 2) save new value -> this will automatically notify callbacks if setting has changed
                    setting.writeSetting(customItem, newValue)
                }
            }
        }
    }

}