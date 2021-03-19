package com.michaelflisar.settings.core.internal.recyclerview

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.michaelflisar.settings.core.SettingsManager
import com.michaelflisar.settings.core.animateVisibility
import com.michaelflisar.settings.core.classes.DialogContext
import com.michaelflisar.settings.core.classes.SettingsDependency
import com.michaelflisar.settings.core.classes.SettingsDisplaySetup
import com.michaelflisar.settings.core.classes.SettingsState
import com.michaelflisar.settings.core.enums.HelpStyle
import com.michaelflisar.settings.core.interfaces.ISetting
import com.michaelflisar.settings.core.interfaces.ISettingsData
import com.michaelflisar.settings.core.interfaces.ISettingsItem
import com.michaelflisar.settings.core.internal.SettingsItemsUtil
import com.michaelflisar.settings.core.internal.SettingsPayload
import com.michaelflisar.settings.core.internal.SimpleAdapterDataObserver
import com.michaelflisar.settings.core.internal.Test
import com.michaelflisar.settings.core.items.SettingsItemCheckboxBool
import com.michaelflisar.settings.core.items.SettingsItemSwitchBool
import com.michaelflisar.settings.core.items.base.BaseBaseSettingsItem
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.IAdapter
import com.mikepenz.fastadapter.IExpandable
import com.mikepenz.fastadapter.IItem
import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.mikepenz.fastadapter.expandable.getExpandableExtension
import com.mikepenz.fastadapter.listeners.ItemFilterListener

internal class SettingsAdapter(
        val dialogContext: DialogContext,
        settingsData: ISettingsData,
        settings: List<ISetting<*>>,
        dependencies: List<SettingsDependency<*>>,
        val setup: SettingsDisplaySetup,
        state: SettingsState?
) {

    private val itemAdapter = ItemAdapter<ISettingsItem<*, *, *>>()
    private val fastAdapter = FastAdapter.with(itemAdapter)
    private val items: List<ISettingsItem<*, *, *>>
    private val expandableExtension = fastAdapter.getExpandableExtension()

    private var recyclerView: RecyclerView? = null
    private var emptyView: View? = null
    private var empty: Boolean? = null

    init {

        items = SettingsItemsUtil.getItems(settingsData, settings, dependencies, setup, null)

        // 1) set up expandable extension
        if (setup.expandable) {
            expandableExtension.isOnlyOneExpandedItem = setup.expandSingleOnly
        }

        // 2) set up filter
        if (Test.ADAPTER_TO_USE == Test.ADAPTER.FAST_ADAPTER) {
            itemAdapter.itemFilter.filterPredicate = { item: ISettingsItem<*, *, *>, constraint: CharSequence? ->
                val valid = item.validForFilter(constraint?.toString() ?: "")
                //Log.d("TEST", "Constraint: $constraint | ${item.item.label.get(SettingsManager.context)} | valid = $valid")
                valid
            }
            itemAdapter.itemFilter.itemFilterListener = object : ItemFilterListener<ISettingsItem<*, *, *>> {
                override fun itemsFiltered(constraint: CharSequence?, results: List<ISettingsItem<*, *, *>>?) {
                    if (setup.expandAllOnFilter) {
                        expandableExtension.withSavedInstanceState(getExpandableBundle(getAllExpandableIds(results), ""), "")
                    }
                }

                override fun onReset() {
                    if (setup.expandAllOnFilter) {
                        expandableExtension.withSavedInstanceState(getExpandableBundle(getAllExpandableIds(items), ""), "")
                    }
                }
            }
        }

        fastAdapter.onClickListener = { view: View?, _: IAdapter<ISettingsItem<*, *, *>>, item: ISettingsItem<*, *, *>, pos: Int ->
            if (item.item.editable && view != null) {
                item.onClickEvent(view, dialogContext, fastAdapter, pos)
                true
            } else
                false
        }

        if (setup.helpStyle.mode == HelpStyle.Mode.LongPress) {
            fastAdapter.onLongClickListener = { view: View?, _: IAdapter<ISettingsItem<*, *, *>>, item: ISettingsItem<*, *, *>, pos: Int ->
                if (view != null) {
                    BaseBaseSettingsItem.EVENT_HOOK_HELP_CLICKED.onClick(view, pos, fastAdapter, item)
                    true
                } else
                    false
            }
        }

        // add event hooks
        fastAdapter.addEventHook(BaseBaseSettingsItem.EVENT_HOOK_HELP_CLICKED)
        fastAdapter.addEventHook(BaseBaseSettingsItem.EVENT_HOOK_IS_CUSTOM_ENABLED)
        fastAdapter.addEventHook(SettingsItemCheckboxBool.EVENT_HOOK)
        fastAdapter.addEventHook(SettingsItemSwitchBool.EVENT_HOOK)

        // 3) create items from settings and add them to the adapter
        itemAdapter.add(items)
        state?.filter?.takeIf { it.isNotEmpty() }?.let {
            onFilterChanged(it)
        }

        fastAdapter.registerAdapterDataObserver(SimpleAdapterDataObserver {
            checkIsEmpty()
        })

        // 4) eventually expand all items by default
        if (!setup.expandable) {
            // expand all in this case - get items has set the correct state in each item already
            expandableExtension.withSavedInstanceState(getExpandableBundle(getExpandedIdsListAndResetExpanded(items), ""), "")
        } else {
            state?.let {
                expandableExtension.withSavedInstanceState(getExpandableBundle(state.expandedIds, ""), "")
            }
        }
    }

    fun bind(recyclerView: RecyclerView, emptyView: View?) {
        val start = Test.measureTimeStart()
        recyclerView.adapter = fastAdapter
        this.emptyView = emptyView
        this.recyclerView = recyclerView
        checkIsEmpty(true)
        Test.measureTimeStop(start, "SettingsAdapter::bind")
    }

    fun updateFilter(filter: String) {
        onFilterChanged(filter)
    }

    fun onDestroy() {
        itemAdapter.clear()
        fastAdapter.notifyAdapterDataSetChanged()
    }

    private fun checkIsEmpty(force: Boolean = false) {
        val isEmpty = fastAdapter.itemCount == 0
        if (!force && empty?.equals(isEmpty) == true) {
            return
        }
        empty = isEmpty
        emptyView?.animateVisibility(if (isEmpty) View.VISIBLE else View.GONE)
        // this breaks the layout but it's not necessary at all
        //recyclerView?.animateVisibility(if (isEmpty) View.INVISIBLE else View.VISIBLE)
        //Log.d("ADAPTER COUNT", "empty: $empty | $emptyView")
    }

    private fun onFilterChanged(filter: String) {
        if (Test.ADAPTER_TO_USE == Test.ADAPTER.FAST_ADAPTER_WITH_CUSTOM_FILTER) {
            // TODO: asynchron ausführen!
            val filtered = items.filter { item ->
                val valid = item.validForFilter(filter)
                //Log.d("TEST", "Constraint: $filter | ${item.item.label.get(SettingsManager.context)} | valid = $valid")
                valid
            }.apply {
                forEach { it.filterSubItems(filter) }
            }

            itemAdapter.set(filtered)
            if (setup.expandAllOnFilter) {
                expandableExtension.withSavedInstanceState(getExpandableBundle(getAllExpandableIds(filtered), ""), "")
            }
        } else {
            itemAdapter.filter(filter.takeIf { it.isNotEmpty() })
        }
    }

    fun getExpandedIds(items: List<IItem<*>>? = null): ArrayList<Long> {
        val itemsToUse = items ?: itemAdapter.adapterItems
        val ids = ArrayList<Long>()
        itemsToUse.forEach {
            if (it is IExpandable<*> && it.isExpanded) {
                ids.add(it.identifier)
                ids.addAll(getExpandedIds(it.subItems).toList())
            }
        }
        return ids
    }

    fun notifyItemChanged(setting: ISetting<*>, payload: SettingsPayload) {
        val pos = fastAdapter.getPosition(setting.id)
        if (pos != RecyclerView.NO_POSITION) {
            fastAdapter.notifyAdapterItemChanged(pos, payload)
        }
    }

    fun onDependencyChanged(dependency: SettingsDependency<*>, payload: SettingsPayload) {
        notifyItemChanged(dependency.childThatDependsOnParent, payload)
    }

    // --------------
    // Helper functions
    // --------------

    private fun getExpandedIdsListAndResetExpanded(items: List<IItem<*>>?): ArrayList<Long> {
        val ids = ArrayList<Long>()
        items?.forEach {
            if (it is IExpandable<*> && it.isExpanded) {
                ids.add(it.identifier)
                ids.addAll(getExpandedIdsListAndResetExpanded(it.subItems).toList())
                it.isExpanded = false
            }
        }
        return ids
    }

    private fun getExpandableBundle(ids: ArrayList<Long>, fastAdapterBundlePrefix: String): Bundle? {
        if (ids.size == 0) {
            return null
        }
        return Bundle().apply {
            putLongArray("bundle_expanded$fastAdapterBundlePrefix", ids.toLongArray())
        }
    }

    private fun getAllExpandableIds(items: List<IItem<*>>?): ArrayList<Long> {
        val ids = ArrayList<Long>()
        items?.forEach {
            if (it is IExpandable<*>) {
                ids.add(it.identifier)
                ids.addAll(getAllExpandableIds(it.subItems).toList())
            }
        }
        return ids
    }
}