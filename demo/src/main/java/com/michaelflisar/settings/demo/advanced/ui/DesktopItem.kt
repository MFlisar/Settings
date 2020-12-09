package com.michaelflisar.settings.demo.advanced.ui

import android.graphics.Color
import android.view.View
import androidx.core.graphics.ColorUtils
import com.michaelflisar.settings.core.SettingsUtils
import com.michaelflisar.settings.demo.R
import com.michaelflisar.settings.demo.advanced.data.db.entities.DBDesktopItem
import com.michaelflisar.settings.demo.advanced.data.db.entities.DBFolderItem
import com.michaelflisar.settings.demo.databinding.ItemDesktopBinding
import com.michaelflisar.settings.demo.databinding.ItemFolderBinding
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.items.AbstractItem

class DesktopItem(
        val desktopItem: DBDesktopItem
) : AbstractItem<DesktopItem.ViewHolder>() {

    override val type = 2
    override val layoutRes = R.layout.item_desktop

    override fun getViewHolder(v: View) = ViewHolder(v)

    class ViewHolder(view: View) : FastAdapter.ViewHolder<DesktopItem>(view) {
        var binding: ItemDesktopBinding = ItemDesktopBinding.bind(view)

        override fun bindView(item: DesktopItem, payloads: List<Any>) {
            val desktopColor = item.desktopItem.calcColor()
            binding.flImageContainer.setBackgroundColor(desktopColor)
            val foregroundColor = if (ColorUtils.calculateLuminance(desktopColor) <= 0.2) Color.WHITE else Color.BLACK
            binding.iv.setColorFilter(foregroundColor)
            binding.tvLabel.text = item.desktopItem.label
        }

        override fun unbindView(item: DesktopItem) {
            binding.tvLabel.text = null
        }
    }
}