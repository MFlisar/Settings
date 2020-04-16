package com.michaelflisar.settings.core.pager

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.michaelflisar.settings.core.SettingsDisplaySetup
import com.michaelflisar.settings.core.classes.SettingsGroup
import com.michaelflisar.settings.core.fragments.SettingsFragment

class SettingsFragmentAdapter(
        fm: FragmentManager,
        val globalSettings: Boolean,
        val groups: List<SettingsGroup>,
        val setup: SettingsDisplaySetup
) : AdvancedFragmentStatePagerAdapter(fm) {

    override fun createItem(position: Int): Fragment {
        return SettingsFragment.create(globalSettings, groups[position], setup)
    }

    override fun getCount(): Int = groups.size
    override fun getPageTitle(position: Int): CharSequence? = groups[position].label.text ?: ""

    fun updateFilter(text: String?) {
        allCachedFragments.forEach {
            (it as SettingsFragment).updateFilter(text)
        }
    }
}