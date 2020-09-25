package com.michaelflisar.settings.core.interfaces

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Parcelable
import android.widget.ImageView
import com.google.android.material.tabs.TabLayout

interface ISettingsIcon : Parcelable {
    fun display(imageView: ImageView)
    fun display(tab: TabLayout.Tab)
    fun getDrawable(context: Context): Drawable
}