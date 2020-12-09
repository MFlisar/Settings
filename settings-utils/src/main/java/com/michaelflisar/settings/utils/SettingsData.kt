package com.michaelflisar.settings.utils

import android.os.Parcelable
import com.michaelflisar.settings.core.interfaces.ISettingsData
import kotlinx.parcelize.Parcelize

abstract class SettingsData : ISettingsData {

    abstract val itemId: Long

    abstract class Custom : SettingsData(), ISettingsData.Element {
        abstract fun <T> loadItem(clazz: Class<T>): T
        override val global: ISettingsData.Global
            get() = Global
    }

    @Parcelize
    object Global : SettingsData(), ISettingsData.Global {
        override val itemId: Long = -1L
    }
}