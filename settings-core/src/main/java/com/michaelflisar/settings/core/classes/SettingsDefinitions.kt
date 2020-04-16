package com.michaelflisar.settings.core.classes

import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.michaelflisar.settings.core.SettingsDisplaySetup
import com.michaelflisar.settings.core.pager.SettingsFragmentAdapter
import com.michaelflisar.settings.core.interfaces.IBaseSetting
import com.michaelflisar.settings.core.recyclerview.SettingsAdapter

data class SettingsDefinitions(
        private val settings: List<IBaseSetting>,
        private val globalSettings: Boolean = true,
        private val setup: SettingsDisplaySetup
) {

    private lateinit var definition: Definition

    // State
    private var filter: String = ""

    // ------------
    // bind views
    // ------------

    fun bindToViewPager(fragmentManager: FragmentManager, tabLayout: TabLayout, viewPager: ViewPager) {
        val def = Definition.ViewPagerDefinitons(fragmentManager, tabLayout, viewPager)
        def.init(globalSettings, getGroupsForViewPager(), setup)
        definition = def
    }

    fun bindToRecyclerView(recyclerView: RecyclerView) {
        val def = Definition.RVDefinitions(recyclerView)
        def.init(globalSettings, settings, setup)
        definition = def
    }

    // ------------
    // filtering
    // ------------

    fun filter(text: String?) {
        filter = text ?: ""
        definition.updateFilter(filter)
    }

    // ------------
    // functions
    // ------------

    fun getGroupsForViewPager(): List<SettingsGroup> {
        // check if setting without group is found
        val countUngroupedSettings = settings.count { it !is SettingsGroup }
        if (countUngroupedSettings == 0) {
            return settings.map { it as SettingsGroup }
        } else {
            val noGroupGroup = SettingsGroup(
                    -1L,
                    SettingsText("NO GROUP"),
                    null,
                    null
            )
            return settings
                    .filter { it is SettingsGroup }
                    .map { it as SettingsGroup }
                    .plus(noGroupGroup)
        }
    }

    // ------------
    // sub classes
    // ------------

    sealed class Definition {

        abstract fun updateFilter(text: String?)

        class ViewPagerDefinitons(
                val fragmentManager: FragmentManager,
                val tabLayout: TabLayout,
                val viewPager: ViewPager
        ) : Definition() {

            private lateinit var adapter: SettingsFragmentAdapter

            fun init(globalSettings: Boolean, groups: List<SettingsGroup>, setup: SettingsDisplaySetup) {
                // 1) create adapter
                adapter = SettingsFragmentAdapter(fragmentManager, globalSettings, groups, setup)
                // 2) set adapter
                viewPager.adapter = adapter
                // 3) bind tabs to viewpager
                tabLayout.setupWithViewPager(viewPager)
            }

            override fun updateFilter(text: String?) {
                adapter.updateFilter(text)
            }
        }

        class RVDefinitions(
                val rv: RecyclerView
        ) : Definition() {

            private lateinit var adapter: SettingsAdapter

            fun init(globalSettings: Boolean, settings: List<IBaseSetting>, setup: SettingsDisplaySetup) {
                // 1) create adapter
                adapter = SettingsAdapter(rv.context, globalSettings, settings, setup)
                // 2) set adapter + init rv
                rv.layoutManager = LinearLayoutManager(rv.context, RecyclerView.VERTICAL, false)
                adapter.bind(rv)
            }

            override fun updateFilter(text: String?) {
                adapter.updateFilter(text)
            }
        }
    }

}