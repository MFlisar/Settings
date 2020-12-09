package com.michaelflisar.settings.core.items

import android.graphics.Typeface
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.github.zawadz88.materialpopupmenu.popupMenu
import com.michaelflisar.dialogs.enums.IconSize
import com.michaelflisar.dialogs.events.DialogListEvent
import com.michaelflisar.dialogs.setups.DialogList
import com.michaelflisar.settings.core.R
import com.michaelflisar.settings.core.SettingsUtils
import com.michaelflisar.settings.core.classes.*
import com.michaelflisar.settings.core.databinding.SettingsItemTextBinding
import com.michaelflisar.settings.core.interfaces.ISettingsData
import com.michaelflisar.settings.core.interfaces.ISettingsIcon
import com.michaelflisar.settings.core.interfaces.ISettingsItem
import com.michaelflisar.settings.core.interfaces.ISettingsListItem
import com.michaelflisar.settings.core.items.base.BaseSettingsItemDialog
import com.michaelflisar.settings.core.items.setups.ListSetup
import com.michaelflisar.settings.core.settings.base.BaseSetting
import com.michaelflisar.settings.core.show

class SettingsItemList(
        override var parentSetting: ISettingsItem<*, *, *>?,
        override var index: Int,
        override var item: BaseSetting<ISettingsListItem, *, ListSetup>,
        override var itemData: SettingsMetaData,
        override var settingsData: ISettingsData,
        setup: SettingsDisplaySetup,
        mode: ListSetup.Mode
) : BaseSettingsItemDialog<ISettingsListItem, SettingsItemTextBinding, ListSetup, BaseSetting<ISettingsListItem, *, ListSetup>>(setup) {

    override val type = R.id.settings_item_list
    override val dialogHandler = DIALOG_HANDLER

    override val endIconType: EndIcon = if (mode == ListSetup.Mode.Popup) EndIcon.Popup else EndIcon.None

    override fun bindSubView(binding: SettingsItemTextBinding, payloads: List<Any>, value: ISettingsListItem, topBinding: Boolean) {
        binding.tvDisplayValue.text = if (item.setup.style.showText) value.getDisplayValue() else ""
        setCompoundDrawableLeft(binding.tvDisplayValue, if (item.setup.style.showIcon) value.icon else null)
    }

    override fun createSubBinding(inflater: LayoutInflater, parent: ViewGroup?, topBinding: Boolean): SettingsItemTextBinding {
        val binding = SettingsItemTextBinding.inflate(inflater, parent, false)
        // set tag for event hook
        setViewTag(binding.tvDisplayValue, topBinding)
        return binding
    }

    private fun setCompoundDrawableLeft(tv: TextView?, icon: ISettingsIcon?) {
        if (tv == null) {
            return
        }
        if (icon == null) {
            tv.setCompoundDrawables(null, null, null, null)
        } else {
            val size = (tv.lineHeight * 2f).toInt()
            val d = icon.getDrawable(tv.context).apply {
                setBounds(0, 0, size, size)
            }
            tv.setCompoundDrawables(if (item.setup.iconPosition == ListSetup.IconPosition.Left) d else null, null, if (item.setup.iconPosition == ListSetup.IconPosition.Right) d else null, null)
        }
    }

    companion object {

        val DIALOG_HANDLER = object : BaseSettingsDialogEventHandler<ISettingsListItem, DialogListEvent, BaseSetting<ISettingsListItem, *, ListSetup>>() {

            override val dialogType: Int = R.id.settings_dialog_type_item_list

            override fun showDialog(view: View, dialogContext: DialogContext, settingsItem: ISettingsItem<ISettingsListItem, *, BaseSetting<ISettingsListItem, *, ListSetup>>, settingsData: ISettingsData) {

                val item = settingsItem.item
                val value = item.read(settingsData)
                val extra = createDialogBundle(item, settingsData)

                val selectedIndex = item.setup.getIndex(value)

                when (item.setup.mode) {
                    ListSetup.Mode.Dialog -> {
                        val items = item.setup.items
                                .map { DialogList.Item.Custom(ListItem(it)) }
                        DialogList(
                                item.id.toInt(),
                                item.label,
                                text = item.info,
                                items = items,
                                selectionMode = DialogList.SelectionMode.Single,
                                initialSingleSelection = selectedIndex,
                                checkMark = R.drawable.checkmark,
                                //iconColorTint = SettingsUtils.attrColor(dialogContext.context, R.attr.colorOnBackground),
                                iconSize = IconSize.Medium,
                                noImageVisibility = View.GONE,
                                extra = extra
                        )
                                .create()
                                .show(dialogContext)
                    }
                    ListSetup.Mode.Popup -> {
                        // Popups won't survive screen rotation just as it is for normal Spinners!
                        // so we do not need to use the dialog event but forward the event directly from the popup callback
                        val popup = popupMenu {
                            dropdownGravity = Gravity.END or Gravity.TOP
                            style = if (settingsItem.setup.useDarkTheme) R.style.Widget_MPM_Menu_Dark else R.style.Widget_MPM_Menu
                            section {
                                title = if (item.setup.showTitleInPopup) item.label.get(dialogContext.context) else null
                                for (entry in item.setup.items) {
                                    item {
                                        label = entry.getDisplayValue()
                                        iconDrawable = entry.icon?.getDrawable(dialogContext.context)
                                        callback = {
                                            val selectedItem = ListItem(entry)
                                            handleEvent(item, settingsData, selectedItem)
                                        }
                                        viewBoundCallback = {
                                            if (entry.id == value.id) {
                                                (it.findViewById<TextView>(R.id.mpm_popup_menu_item_label))?.setTypeface(null, Typeface.BOLD)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        popup.show(dialogContext.context, view)
                    }
                }
            }

            override fun onDialogEvent(dialogContext: DialogContext, event: DialogListEvent, setting: BaseSetting<ISettingsListItem, *, ListSetup>, settingsData: ISettingsData) {

                val selectedItem: ListItem? = event.data?.getItem()
                selectedItem?.let {
                    handleEvent(setting, settingsData, it)
                }
            }

            private fun handleEvent(setting: BaseSetting<ISettingsListItem, *, ListSetup>, settingsData: ISettingsData, selectedItem: ListItem) {
                // 1) get new value
                val newValue = selectedItem.item

                // 2) save new value -> this will automatically notify callbacks if setting has changed
                setting.write(settingsData, newValue)
            }
        }
    }

}