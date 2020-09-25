package com.michaelflisar.settings.core.internal

import androidx.viewpager.widget.ViewPager

class SimplePageChangedListener(
        val callback: (page: Int) -> Unit
) : ViewPager.OnPageChangeListener {

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
    }

    override fun onPageSelected(position: Int) {
        callback.invoke(position)
    }

    override fun onPageScrollStateChanged(state: Int) {
    }
}