package com.michaelflisar.settings.color

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class ColorSetup(
        val supportAlpha: Boolean = false
) : Parcelable