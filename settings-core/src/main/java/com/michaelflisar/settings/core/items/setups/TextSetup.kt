package com.michaelflisar.settings.core.items.setups

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class TextSetup (
        val allowEmptyText: Boolean = true
): Parcelable