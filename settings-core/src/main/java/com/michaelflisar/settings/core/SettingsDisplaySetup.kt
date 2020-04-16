package com.michaelflisar.settings.core

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SettingsDisplaySetup(
        val showID: Boolean = false,
        val showNumbers: Boolean = false,
        val expandable: Boolean = true,
        val expandSingleOnly: Boolean = true,
        val expandAllOnFilter: Boolean = true
) : Parcelable