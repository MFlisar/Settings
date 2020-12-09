package com.michaelflisar.settings.core.internal.recyclerview

import android.graphics.Rect
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.fastadapter.FastAdapter

internal class SettingsBottomSpaceDecorator(
        private val bottomSpace: Int
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {

        val index = parent.getChildAdapterPosition(view)
        if (index == RecyclerView.NO_POSITION) {
            return
        }

        val childCount = (parent.adapter as FastAdapter<*>).itemCount
        val isLastIndex = index == childCount - 1

        outRect.bottom = if (isLastIndex) bottomSpace else 0
    }
}