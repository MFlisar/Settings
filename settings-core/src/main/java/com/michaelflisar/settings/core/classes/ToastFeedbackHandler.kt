package com.michaelflisar.settings.core.classes

import android.view.View
import android.widget.Toast
import com.michaelflisar.settings.core.R
import com.michaelflisar.settings.core.get
import com.michaelflisar.settings.core.interfaces.ISettingsFeedbackHandler
import com.michaelflisar.settings.core.interfaces.ISettingsItem
import kotlinx.parcelize.Parcelize

@Parcelize
open class ToastFeedbackHandler(
        val errorTextCantChangeDisabledSetting: Int = R.string.settings_cant_edit_an_disabled_item,
        val length: Int = Toast.LENGTH_SHORT
) : ISettingsFeedbackHandler {
    override fun showHelp(item: ISettingsItem<*, *, *>, view: View) {
        Toast.makeText(view.context, item.item.help?.get(), length).show()
    }

    override fun showCantChangeSettingInfo(item: ISettingsItem<*, *, *>, view: View) {
        Toast.makeText(view.context, errorTextCantChangeDisabledSetting, length).show()
    }
}