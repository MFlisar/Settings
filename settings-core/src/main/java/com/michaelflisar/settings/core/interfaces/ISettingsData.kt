package com.michaelflisar.settings.core.interfaces

import android.os.Parcelable

interface ISettingsData : Parcelable {
    val global: Global
    val isGlobal: Boolean

    interface Global : ISettingsData {
        override val global: Global
            get() = this
        override val isGlobal: Boolean
            get() = true
    }

    interface Element : ISettingsData {
        override val isGlobal: Boolean
            get() = false
    }
}