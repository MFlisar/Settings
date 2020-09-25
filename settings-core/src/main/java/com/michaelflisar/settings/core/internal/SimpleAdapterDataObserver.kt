package com.michaelflisar.settings.core.internal

import androidx.recyclerview.widget.RecyclerView

internal class SimpleAdapterDataObserver(
        val callback: () -> Unit
) : RecyclerView.AdapterDataObserver() {

    override fun onChanged() {
        callback()
    }

    override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
        callback()
    }

    override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
        callback()
    }

    override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
        callback()
    }

    override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
        callback()
    }
}