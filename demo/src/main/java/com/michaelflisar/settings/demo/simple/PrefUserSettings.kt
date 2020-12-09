package com.michaelflisar.settings.demo.simple

import android.os.Parcelable
import com.michaelflisar.settings.core.interfaces.ISettingsData
import com.michaelflisar.settings.storage.datastorepreferences.IPrefSubIDProvider
import com.michaelflisar.settings.utils.SettingsData
import kotlinx.parcelize.Parcelize

@Parcelize
class PrefUserSettings(
        override val id: Long,
        val userName: String
) : IPrefSubIDProvider, ISettingsData.Element, Parcelable {
    override val global = SettingsData.Global
}