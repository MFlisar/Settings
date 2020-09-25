package com.michaelflisar.settings.core.classes

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class SettingsMetaData(
        val dependencies: List<SettingsDependency>
) : Parcelable