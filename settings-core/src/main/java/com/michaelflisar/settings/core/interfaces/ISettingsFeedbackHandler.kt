package com.michaelflisar.settings.core.interfaces

import android.os.Parcelable
import android.view.View

interface ISettingsFeedbackHandler : Parcelable {
    fun showHelp(item: ISettingsItem<*, *, *>, view: View)
    fun showCantChangeSettingInfo(item: ISettingsItem<*, *, *>, view: View)
}