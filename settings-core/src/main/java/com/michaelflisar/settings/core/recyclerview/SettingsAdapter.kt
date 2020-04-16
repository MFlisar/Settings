package com.michaelflisar.settings.core.recyclerview

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import com.michaelflisar.settings.core.SettingsDisplaySetup
import com.michaelflisar.settings.core.interfaces.IBaseSetting
import com.michaelflisar.settings.core.items.BaseSettingsItem
import com.michaelflisar.settings.core.items.SettingsItemsUtil
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.IExpandable
import com.mikepenz.fastadapter.IItem
import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.mikepenz.fastadapter.expandable.getExpandableExtension
import com.mikepenz.fastadapter.listeners.ItemFilterListener

class SettingsAdapter(
        context: Context,
        globalSettings: Boolean,
        settings: List<IBaseSetting>,
        setup: SettingsDisplaySetup
) {

    private val itemAdapter = ItemAdapter<BaseSettingsItem<*>>()
    private val fastAdapter = FastAdapter.with(itemAdapter)

    init {

        // TODO: setup definieren: expandable yes/no, onlyOneExpandable yes/no, ...

        val expandableExtension = fastAdapter.getExpandableExtension()

        val items = SettingsItemsUtil.getItems(globalSettings, settings, setup)

        // 1) set up expandable extension
        if (setup.expandable) {
            expandableExtension.isOnlyOneExpandedItem = setup.expandSingleOnly
        }

        // 2) set up filter
        itemAdapter.itemFilter.filterPredicate = { item: BaseSettingsItem<*>, constraint: CharSequence? ->
            Log.d("TEST", "Constraint: $constraint")
            item.validForFilter(constraint?.toString())
        }
        itemAdapter.itemFilter.itemFilterListener = object : ItemFilterListener<BaseSettingsItem<*>> {
            override fun itemsFiltered(constraint: CharSequence?, results: List<BaseSettingsItem<*>>?) {
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

        // 3) create items from settings and add them to the adapter
        itemAdapter.add(items)

        // 4) eventually expand all items by default
        if (!setup.expandable) {
            // expand all in this case - get items has set the correct state in each item already
            expandableExtension.withSavedInstanceState(getExpandableBundle(getExpandedIdsListAndResetExpanded(items), ""), "")
        }
    }

    fun bind(recyclerView: RecyclerView) {
        recyclerView.adapter = fastAdapter
    }

    fun updateFilter(text: String?) {
        itemAdapter.filter(text)
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

    private fun getExpandableBundle(ids: ArrayList<Long>, fastAdapterBundlePrefix: String): Bundle {
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