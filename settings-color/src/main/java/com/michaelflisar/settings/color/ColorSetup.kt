package com.michaelflisar.settings.color

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class ColorSetup(
        val supportAlpha: Boolean = DEFAULT_SUPPORT_ALPHA
) : Parcelable {

    companion object {
        val DEFAULT_SUPPORT_ALPHA: Boolean = true
    }
}