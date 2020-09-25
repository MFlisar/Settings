package com.michaelflisar.settings.demo

import android.os.Parcelable
import com.michaelflisar.settings.storage.datastorepreferences.IPrefSubIDProvider
import kotlinx.android.parcel.Parcelize

@Parcelize
class PrefUserSettings(
        override val id: Long,
        val userName: String
) : IPrefSubIDProvider, Parcelable