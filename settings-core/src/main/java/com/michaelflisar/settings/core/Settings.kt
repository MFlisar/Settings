package com.michaelflisar.settings.core

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.michaelflisar.settings.core.classes.*
import com.michaelflisar.settings.core.enums.ChangeType
import com.michaelflisar.settings.core.interfaces.ISetting
import com.michaelflisar.settings.core.interfaces.ISettingsChangedCallback
import com.michaelflisar.settings.core.interfaces.ISettingsData
import com.michaelflisar.settings.core.internal.SettingsItemsUtil
import com.michaelflisar.settings.core.internal.SettingsPayload
import com.michaelflisar.settings.core.internal.pager.SettingsFragmentAdapter
import com.michaelflisar.settings.core.internal.pager.SettingsFragmentAdapter2
import com.michaelflisar.settings.core.internal.recyclerview.SettingsAdapter
import com.michaelflisar.settings.core.internal.recyclerview.SettingsBottomSpaceDecorator
import com.michaelflisar.settings.core.settings.SettingsGroup

class Settings(
        private val definitions: SettingsDefinition,
        private val settingsData: ISettingsData,
        val setup: SettingsDisplaySetup
) : ISettingsChangedCallback {

    init {
        SettingsUtils.checkUniqueIds(definitions.settings, true)
    }

    private enum class Bound {
        None,
        List,
        Pager,
        Pager2
    }

    private lateinit var definition: Definition

    // State
    private var bound: Bound = Bound.None
    var filter: String = ""
        private set

    // ------------
    // bind views
    // ------------

    fun bind(viewContext: ViewContext, state: SettingsState, tabLayout: TabLayout, viewPager: ViewPager) {
        filter = state.filter
        val def = Definition.PagerDefinitons(viewContext, tabLayout, viewPager)
        def.init(this, settingsData, SettingsItemsUtil.getTopLevelGroups(settingsData, definitions.settings, true, definitions.dependencies, setup), definitions.dependencies, setup, state)
        definition = def
        bound = Bound.Pager
        SettingsManager.registerCallback(this)
    }

    fun bind(viewContext: ViewContext, state: SettingsState, tabLayout: TabLayout, viewPager: ViewPager2) {
        filter = state.filter
        val def = Definition.PagerDefinitons2(viewContext, tabLayout, viewPager)
        def.init(this, settingsData, SettingsItemsUtil.getTopLevelGroups(settingsData, definitions.settings, true, definitions.dependencies, setup), definitions.dependencies, setup, state)
        definition = def
        bound = Bound.Pager2
        SettingsManager.registerCallback(this)
    }

    fun bind(viewContext: ViewContext, state: SettingsState, recyclerView: RecyclerView, emptyView: View?) {
        filter = state.filter
        val def = Definition.ListDefinitions(recyclerView, emptyView)
        def.init(this, viewContext, settingsData, definitions.settings, definitions.dependencies, setup, state)
        definition = def
        bound = Bound.List
        SettingsManager.registerCallback(this)
    }

    fun unbind() {
        if (bound != Bound.None) {
            SettingsManager.unregisterCallback(this)
            definition.destroy()
        }
        bound = Bound.None
    }

    fun getViewState(): SettingsState? {
        return when (bound) {
            Bound.None -> null
            Bound.List -> SettingsState(this, definition as Definition.ListDefinitions)
            Bound.Pager -> SettingsState(this, definition as Definition.PagerDefinitons)
            Bound.Pager2 -> SettingsState(this, definition as Definition.PagerDefinitons2)
        }
    }

    // ------------
    // filtering
    // ------------

    fun filter(text: String): Boolean {
        val f = setup.filter.transformFilter(text)
        if (f != filter) {
            filter = f
            definition.updateFilter(filter)
            return true
        }
        return false
    }

    internal fun onDependencyChanged(dependency: SettingsDependency<*>, payload: SettingsPayload) {
        definition.onDependencyChanged(dependency, payload)
    }

    // ------------
    // dialog callbacks
    // ------------

    override fun onSettingChanged(changeType: ChangeType, setting: ISetting<*>, settingsData: ISettingsData, oldValue: Any?, newValue: Any?) {
        // UI needs to be updated
        definition.onSettingChanged(changeType, setting, settingsData, oldValue, newValue)
        // check dependencies
        checkDependency(setting)
    }

    private fun checkDependency(setting: ISetting<*>) {
        definitions.dependencies
                .filter { it.checkParent(setting) }
                .forEach {
                    // notify item changed for each child
                    definition.onDependencyChanged(it, SettingsPayload.DependencyChanged)
                }
    }

    // ------------
    // sub classes
    // ------------

    sealed class ViewContext {
        abstract val fragmentManager: FragmentManager
        abstract val lifeCycle: Lifecycle

        class Activity(val activity: AppCompatActivity) : ViewContext() {
            override val fragmentManager = activity.supportFragmentManager
            override val lifeCycle = activity.lifecycle
        }

        class Fragment(val fragment: androidx.fragment.app.Fragment) : ViewContext() {
            override val fragmentManager = fragment.childFragmentManager
            override val lifeCycle = fragment.lifecycle
        }

        fun getDialogContext(): DialogContext {
            return when (this) {
                is Activity -> DialogContext.Activity(this.activity)
                is Fragment -> DialogContext.Fragment(this.fragment)
            }
        }
    }

    sealed class Definition {

        abstract fun updateFilter(text: String)
        abstract fun getExpandedIds(): ArrayList<Long>
        internal abstract fun onDependencyChanged(dependency: SettingsDependency<*>, payload: SettingsPayload)

        abstract fun onSettingChanged(changeType: ChangeType, setting: ISetting<*>, settingsData: ISettingsData, oldValue: Any?, newValue: Any?)

        open fun destroy() {

        }

        class PagerDefinitons(
                val viewContext: ViewContext,
                val tabLayout: TabLayout,
                val viewPager: ViewPager
        ) : Definition() {

            private lateinit var adapter: SettingsFragmentAdapter

            fun init(settings: Settings, settingsData: ISettingsData, groups: List<SettingsGroup>, dependencies: List<SettingsDependency<*>>, setup: SettingsDisplaySetup, state: SettingsState) {
                // 1) create adapter
                adapter = SettingsFragmentAdapter(viewContext.fragmentManager, settingsData, groups, dependencies, setup, state)
                // 2) set adapter
                viewPager.adapter = adapter
                viewPager.offscreenPageLimit = groups.size
                // 3) bind tabs to viewpager
                tabLayout.setupWithViewPager(viewPager)
                // 4) bind icons to viewpager
                for (i in 0 until tabLayout.tabCount) {
                    groups[i].icon?.display(tabLayout.getTabAt(i)!!)
                }
                // 5) listen to page changed
//                viewPager.addOnPageChangeListener(SimplePageChangedListener {
//                    val f = adapter.getFragmentAt(viewPager.currentItem)
//                    // we may need to update an outdated filter (fragment may have been saved and did not get this change!)
//                    f?.updateFilter(settings.filter)
//                })
            }

            override fun updateFilter(text: String) {
                adapter.updateFilter(text)
            }

            override fun getExpandedIds(): ArrayList<Long> {
                val f = adapter.getFragmentAt(viewPager.currentItem)
                return f?.getViewState()?.expandedIds ?: ArrayList()
            }

            override fun onDependencyChanged(dependency: SettingsDependency<*>, payload: SettingsPayload) {
                adapter.onDependencyChanged(dependency, payload)
            }

            override fun onSettingChanged(changeType: ChangeType, setting: ISetting<*>, settingsData: ISettingsData, oldValue: Any?, newValue: Any?) {
                val f = adapter.getFragmentAt(viewPager.currentItem)
                f?.let {
                    f.onSettingChanged(changeType, setting, settingsData, oldValue, newValue)
                }
            }
        }

        class PagerDefinitons2(
                val viewContext: ViewContext,
                val tabLayout: TabLayout,
                val viewPager: ViewPager2
        ) : Definition() {

            private lateinit var adapter: SettingsFragmentAdapter2

            fun init(settings: Settings, settingsData: ISettingsData, groups: List<SettingsGroup>, dependencies: List<SettingsDependency<*>>, setup: SettingsDisplaySetup, state: SettingsState) {
                // 1) create adapter
                adapter = SettingsFragmentAdapter2(viewContext.fragmentManager, viewContext.lifeCycle, settingsData, groups, dependencies, setup, state)
                // 2) set adapter
                viewPager.adapter = adapter
                viewPager.offscreenPageLimit = groups.size
                // 3) bind tabs to viewpager
                TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                    tab.text = adapter.getPageTitle(position)
                }.attach()
                // 4) bind icons to viewpager
                for (i in 0 until tabLayout.tabCount) {
                    groups[i].icon?.display(tabLayout.getTabAt(i)!!)
                }
                // 5) listen to page changed
//                viewPager.addOnPageChangeListener(SimplePageChangedListener {
//                    val f = adapter.getFragmentAt(viewPager.currentItem)
//                    // we may need to update an outdated filter (fragment may have been saved and did not get this change!)
//                    f?.updateFilter(settings.filter)
//                })
            }

            override fun updateFilter(text: String) {
                adapter.updateFilter(text)
            }

            override fun getExpandedIds(): ArrayList<Long> {
                val f = adapter.getFragmentAt(viewPager.currentItem)
                return f?.getViewState()?.expandedIds ?: ArrayList()
            }

            override fun onDependencyChanged(dependency: SettingsDependency<*>, payload: SettingsPayload) {
                adapter.onDependencyChanged(dependency, payload)
            }

            override fun onSettingChanged(changeType: ChangeType, setting: ISetting<*>, settingsData: ISettingsData, oldValue: Any?, newValue: Any?) {
                val f = adapter.getFragmentAt(viewPager.currentItem)
                f?.let {
                    f.onSettingChanged(changeType, setting, settingsData, oldValue, newValue)
                }
            }
        }

        class ListDefinitions(
                val rv: RecyclerView,
                val emptyView: View?
        ) : Definition() {

            private lateinit var adapter: SettingsAdapter
            private var decorators: ArrayList<RecyclerView.ItemDecoration> = ArrayList()

            fun init(settings: Settings, viewContext: ViewContext, settingsData: ISettingsData, items: List<ISetting<*>>, dependencies: List<SettingsDependency<*>>, setup: SettingsDisplaySetup, state: SettingsState) {
                // 1) create adapter
                adapter = SettingsAdapter(viewContext.getDialogContext(), settingsData, items, dependencies, setup, state)
                // 2) set adapter + init rv
                rv.layoutManager = LinearLayoutManager(rv.context, RecyclerView.VERTICAL, false)
                adapter.bind(rv, emptyView)
                // 3) add decorator
                val decorator =setup.createSettingsViewsDecorator()
                rv.addItemDecoration(decorator)
                decorators.add(decorator)
                if (setup.additionalListBottomSpace != 0) {
                    val decorator2 = SettingsBottomSpaceDecorator(setup.additionalListBottomSpace)
                    rv.addItemDecoration(decorator2)
                    decorators.add(decorator2)
                }
            }

            override fun destroy() {
                decorators.forEach { rv.removeItemDecoration(it) }
            }

            override fun updateFilter(text: String) {
                adapter.updateFilter(text)
            }

            override fun getExpandedIds(): ArrayList<Long> {
                return adapter.getExpandedIds()
            }

            override fun onDependencyChanged(dependency: SettingsDependency<*>, payload: SettingsPayload) {
                adapter.onDependencyChanged(dependency, payload)
            }

            override fun onSettingChanged(changeType: ChangeType, setting: ISetting<*>, settingsData: ISettingsData, oldValue: Any?, newValue: Any?) {
                val payload = when (changeType) {
                    ChangeType.GlobalValue -> SettingsPayload.ValueChanged
                    ChangeType.CustomValue -> SettingsPayload.ValueChanged
                    ChangeType.CustomIsEnabled -> SettingsPayload.IsCustomEnabledChanged
                }
                adapter.notifyItemChanged(setting, payload)
            }
        }
    }
}