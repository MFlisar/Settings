package com.michaelflisar.settings.core.classes

import android.widget.ImageView
import com.michaelflisar.settings.core.interfaces.ISettingsIcon
import kotlinx.android.parcel.Parcelize

@Parcelize
class SettingsIcon(
        val resIcon: Int
) : ISettingsIcon {
    override fun display(imageView: ImageView) {
        imageView.setImageResource(resIcon)
    }
}