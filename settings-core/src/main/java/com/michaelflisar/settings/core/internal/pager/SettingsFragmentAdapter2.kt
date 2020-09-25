package com.michaelflisar.settings.core.internal.pager

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.michaelflisar.settings.core.classes.SettingsDisplaySetup
import com.michaelflisar.settings.core.classes.SettingsCustomObject
import com.michaelflisar.settings.core.classes.SettingsDependency
import com.michaelflisar.settings.core.internal.SettingsPayload
import com.michaelflisar.settings.core.classes.SettingsState
import com.michaelflisar.settings.core.internal.fragments.SettingsFragment
import com.michaelflisar.settings.core.settings.SettingsGroup

internal class SettingsFragmentAdapter2(
        val fragmentManager: FragmentManager,
        val lifecycle: Lifecycle,
        val customItem: SettingsCustomObject,
        val groups: List<SettingsGroup>,
        val dependencies: List<SettingsDependency>,
        val setup: SettingsDisplaySetup,
        val state: SettingsState
) : FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun createFragment(position: Int): SettingsFragment {
//        Log.d("SettingsFragment", "createItem: $position")
        return SettingsFragment.create(customItem, groups[position], dependencies, setup, state)
    }

    override fun getItemCount(): Int = groups.size

    fun getPageTitle(position: Int): CharSequence? =
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

    fun getFragmentAt(position: Int) = fragmentManager.findFragmentByTag("f"+ position) as SettingsFragment?

    val allCreatedFragments: List<SettingsFragment>
        get() {
            val items = ArrayList<SettingsFragment>()
            for (i in 0 until itemCount) {
                getFragmentAt(i)?.let {
                    items.add(it)
                }
            }
            return items
        }
}