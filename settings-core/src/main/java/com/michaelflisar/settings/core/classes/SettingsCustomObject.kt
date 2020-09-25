package com.michaelflisar.settings.core.classes

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

sealed class SettingsCustomObject : Parcelable {

    @Parcelize
    object None : SettingsCustomObject()

    @Parcelize
    class Element(val item: Parcelable) : SettingsCustomObject()
}