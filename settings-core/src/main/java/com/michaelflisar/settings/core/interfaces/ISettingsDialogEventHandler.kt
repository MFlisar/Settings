package com.michaelflisar.settings.core.interfaces

import android.view.View
import com.michaelflisar.settings.core.classes.DialogContext

interface ISettingsDialogEventHandler<ValueType, Setting: ISetting<ValueType>> {

    /*
     * only events that fit this type will be forwarded to the [this onDialogEvent]
     */
    val dialogType: Int

    /*
     * make sure to return the dialog type from the dialog event here or null if the [event] does not fit
     */
    fun getDialogType(event: Any): Int?

    /*
     * simply show your dialog
     */
    fun showDialog(view: View, dialogContext: DialogContext, settingsItem: ISettingsItem<ValueType, *, Setting>, settingsData: ISettingsData)

    /*
     * this function is called with the correct event that fits this handler - simply cast it to what you expect it to be
     *
     * [this getDialogType] is called before this function is called ond this function will only be called with an [event] that fit's this [dialogType]
     */
    fun onDialogEvent(event: Any, dialogContext: DialogContext)

}