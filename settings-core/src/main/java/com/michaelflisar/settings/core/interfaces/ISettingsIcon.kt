package com.michaelflisar.settings.core.interfaces

import android.os.Parcelable
import android.widget.ImageView

interface ISettingsIcon : Parcelable {
    fun display(imageView: ImageView)
}