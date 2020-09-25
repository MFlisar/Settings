package com.michaelflisar.settings.core.internal.pager

import androidx.fragment.app.FragmentManager
import com.michaelflisar.settings.core.classes.SettingsDisplaySetup
import com.michaelflisar.settings.core.classes.SettingsCustomObject
import com.michaelflisar.settings.core.classes.SettingsDependency
import com.michaelflisar.settings.core.internal.SettingsPayload
import com.michaelflisar.settings.core.classes.SettingsState
import com.michaelflisar.settings.core.internal.fragments.SettingsFragment
import com.michaelflisar.settings.core.settings.SettingsGroup

internal class SettingsFragmentAdapter(
        fm: FragmentManager,
        val customItem: SettingsCustomObject,
        val groups: List<SettingsGroup>,
        val dependencies: List<SettingsDependency>,
        val setup: SettingsDisplaySetup,
        val state: SettingsState
) : AdvancedFragmentStatePagerAdapter<SettingsFragment>(fm) {

    override fun createItem(position: Int): SettingsFragment {
//        Log.d("SettingsFragment", "createItem: $position")
        return SettingsFragment.create(customItem, groups[position], dependencies, setup, state)
    }

    override fun getCount(): Int = groups.size
    override fun getPageTitle(position: Int): CharSequence? =
            groups[position].getDisplayLabel(setup, true)

    fun updateFilter(text: String) {
        state.filter = text
        allCreatedFragments.forEach {
            it.updateFilter(text)
        }
    }

    fun onDependencyChanged(dependency: SettingsDependency, payload: SettingsPayload) {
        allCreatedFragments.forEach {
            it.onDependencyChanged(dependency, payload)
        }
    }
}