package com.michaelflisar.settings.core.items.setups

import android.graphics.Color
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class InfoSetup(
        val backgroundTint: Int = Color.rgb(255, 165, 0) /* orange */,
        val foregroundTint: Int = Color.WHITE
) : Parcelable