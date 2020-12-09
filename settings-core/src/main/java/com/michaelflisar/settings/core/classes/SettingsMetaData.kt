package com.michaelflisar.settings.core.classes

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class SettingsMetaData(
        val dependencies: List<SettingsDependency<*>>
) : Parcelable