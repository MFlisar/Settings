package com.michaelflisar.settings.demo.advanced.ui

import android.graphics.Color
import android.net.Uri
import android.view.View
import com.michaelflisar.settings.demo.R
import com.michaelflisar.settings.demo.advanced.data.db.entities.DBFolderItem
import com.michaelflisar.settings.demo.databinding.ItemFolderBinding
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.items.AbstractItem

class FolderItem(
        val folderItem: DBFolderItem
) : AbstractItem<FolderItem.ViewHolder>() {

    override val type = 1
    override val layoutRes = R.layout.item_folder

    override fun getViewHolder(v: View) = ViewHolder(v)

    class ViewHolder(view: View) : FastAdapter.ViewHolder<FolderItem>(view) {
        var binding: ItemFolderBinding = ItemFolderBinding.bind(view)

        override fun bindView(item: FolderItem, payloads: List<Any>) {
            if (item.folderItem.imageUri.isNotEmpty()) {
                val uri = Uri.parse(item.folderItem.imageUri)
                binding.iv.setImageURI(uri)
                binding.iv.colorFilter = null
                binding.flImageBackground.setBackgroundColor(item.folderItem.calcColor())
            } else {
                binding.iv.setImageResource(R.drawable.folder_closed)
                binding.iv.setColorFilter(item.folderItem.calcColor())
                binding.flImageBackground.setBackgroundColor(Color.TRANSPARENT)
            }
            binding.tvLabel.text = item.folderItem.label
        }

        override fun unbindView(item: FolderItem) {
            binding.tvLabel.text = null
        }
    }
}