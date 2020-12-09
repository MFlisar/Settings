package com.michaelflisar.settings.core.items.setups

import android.graphics.Color
import android.os.Parcelable
import com.michaelflisar.settings.core.classes.SettingsColor
import kotlinx.parcelize.Parcelize

@Parcelize
class InfoSetup(
        val backgroundTint: SettingsColor = DEFAULT_BACKGROUND_TINT,
        val foregroundTint: SettingsColor = DEFAULT_FOREGROUND_TINT
) : Parcelable {

    companion object {
        var DEFAULT_BACKGROUND_TINT = SettingsColor.Color(Color.rgb(255, 165, 0)) /* orange */
        var DEFAULT_FOREGROUND_TINT  = SettingsColor.Color(Color.WHITE)
    }
}