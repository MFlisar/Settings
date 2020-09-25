package com.michaelflisar.settings.core.classes

import android.content.Context
import android.os.Parcelable
import android.widget.TextView
import com.michaelflisar.settings.core.SettingsManager
import com.michaelflisar.settings.core.SettingsUtils
import kotlinx.android.parcel.Parcelize

sealed class SettingsColor : Parcelable {

    abstract fun getColor(context: Context): Int

    @Parcelize
    class Color(val color: Int): SettingsColor() {
        override fun getColor(context: Context): Int = color
    }

    @Parcelize
    class ColorRes(val color: Int): SettingsColor() {
        override fun getColor(context: Context): Int = SettingsUtils.attrColor(context, color)
    }
}