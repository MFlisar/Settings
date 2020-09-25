package com.michaelflisar.settings.core.classes

import android.R
import android.content.Context
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatDrawableManager
import androidx.core.content.ContextCompat
import com.google.android.material.tabs.TabLayout
import com.michaelflisar.settings.core.SettingsManager
import com.michaelflisar.settings.core.interfaces.ISettingsIcon
import kotlinx.android.parcel.Parcelize


@Parcelize
class SettingsIcon(
        val resIcon: Int
) : ISettingsIcon {
    override fun display(imageView: ImageView) {
        imageView.setImageResource(resIcon)
    }

    override fun display(tab: TabLayout.Tab) {
        tab.setIcon(resIcon)
    }

    override fun getDrawable(context: Context): Drawable {
        return ContextCompat.getDrawable(context, resIcon)!!
    }
}