package com.michaelflisar.settings.core.classes

import android.widget.ImageView
import com.michaelflisar.dialogs.interfaces.IParcelableTextImageProvider
import com.michaelflisar.settings.core.interfaces.ISettingsListItem
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize

@Parcelize
class ListItem(
        val item: ISettingsListItem
) : IParcelableTextImageProvider {
    @IgnoredOnParcel
    override val subTitle: String? = null

    @IgnoredOnParcel
    override val title: String = item.getDisplayValue()
    override fun hasImage(): Boolean = item.icon != null
    override fun loadImage(iv: ImageView) {
        item.icon?.display(iv)
    }
}