package com.michaelflisar.settings.core.items.base

import android.view.View
import androidx.viewbinding.ViewBinding
import com.michaelflisar.settings.core.settings.base.BaseSetting
import com.michaelflisar.settings.core.classes.SettingsDisplaySetup
import com.michaelflisar.settings.core.classes.DialogContext
import com.michaelflisar.settings.core.interfaces.ISettingsDialogEventHandler
import com.mikepenz.fastadapter.FastAdapter

abstract class BaseSettingsItemDialog<ValueType : Any, SubViewBinding : ViewBinding, Setup, Setting : BaseSetting<ValueType, *, Setup>>(
        setup: SettingsDisplaySetup
) : BaseSettingsItem<ValueType, SubViewBinding, Setting>(setup) {

    final override val noStartIconMode = NoStartIconMode.Invisible
    override val endIconType = EndIcon.More

    abstract val dialogHandler: ISettingsDialogEventHandler<ValueType, Setting>

    final override fun onClickEvent(view: View, dialogContext: DialogContext, fastAdapter: FastAdapter<*>, pos: Int): Boolean {
        val itemToChange = getItemToChange()
        itemToChange?.let {
            dialogHandler.showDialog(view, dialogContext, item, itemToChange)
            return true
        }
        setup.showCantChangeSettingInfo(this, view)
        return false
    }

}