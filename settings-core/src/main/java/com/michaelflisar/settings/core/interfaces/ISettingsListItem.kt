package com.michaelflisar.settings.core.interfaces

import android.os.Parcelable

interface ISettingsListItem : Parcelable {
    val id: Long
    val icon: ISettingsIcon?
    fun getDisplayValue(): String
}