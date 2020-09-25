package com.michaelflisar.settings.core.internal.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.michaelflisar.settings.core.Settings
import com.michaelflisar.settings.core.classes.*
import com.michaelflisar.settings.core.databinding.SettingsFragmentSettingsBinding
import com.michaelflisar.settings.core.interfaces.ISetting
import com.michaelflisar.settings.core.interfaces.ISettingsChangedCallback
import com.michaelflisar.settings.core.internal.SettingsPayload
import com.michaelflisar.settings.core.settings.SettingsGroup

internal class SettingsFragment : Fragment(), ISettingsChangedCallback {

    companion object {

        fun create(customItem: SettingsCustomObject, group: SettingsGroup, dependencies: List<SettingsDependency>, setup: SettingsDisplaySetup, state: SettingsState): SettingsFragment {
            val f = SettingsFragment()
            val args = Bundle().apply {
                putParcelable("customItem", customItem)
                putParcelableArrayList("items", ArrayList(group.getItems()))
                putParcelableArrayList("dependencies", ArrayList(dependencies))
                putParcelable("setup", setup)
                putParcelable("state", state)
            }
            f.arguments = args
            return f
        }
    }

    private var binding: SettingsFragmentSettingsBinding? = null

    private lateinit var customItem: SettingsCustomObject
    private lateinit var items: ArrayList<ISetting<*>>
    private lateinit var dependencies: ArrayList<SettingsDependency>
    private lateinit var setup: SettingsDisplaySetup
    private var state: SettingsState = SettingsState()
    private lateinit var settings: Settings

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments!!.let {
            customItem = it.getParcelable("customItem")!!
            items = it.getParcelableArrayList("items")!!
            dependencies = it.getParcelableArrayList("dependencies")!!
            setup = it.getParcelable("setup")!!
            state = it.getParcelable("state")!!
        }

        savedInstanceState?.let {
            it.getParcelable<SettingsState>("state")?.let {
                state = it
            }
//            Log.d("SettingsFragment", "state restored: $state")
        }

        settings = Settings(SettingsDefinition(items, dependencies, false), customItem, setup)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

//        Log.d("SettingsFragment", "onCreateView: $savedInstanceState")

        binding = SettingsFragmentSettingsBinding.inflate(inflater)
        setup.noDataFoundIcon?.takeIf { it > 0 }?.let {
            binding!!.vEmpty.ivEmpty.setImageResource(it)
        }
        setup.noSearchResults.takeIf { it > 0 }?.let {
            binding!!.vEmpty.tvEmpty.setText(it)
        }
        // bind settings to recyclerview
        settings.bind(Settings.ViewContext.Fragment(this), state, binding!!.rv, binding!!.vEmpty.root)
        if (state.filter.isNotEmpty()) {
            updateFilter(state.filter)
        }
        return binding!!.root
    }

    fun updateFilter(text: String) {
        settings.filter(text)
    }

//    fun getExistingIds(): List<Long> {
//        return SettingsUtils.getAllIds(items)
//    }

    fun getViewState(): SettingsState? {
        return settings.getViewState()
    }

    fun onDependencyChanged(dependency: SettingsDependency, payload: SettingsPayload) {
        settings.onDependencyChanged(dependency, payload)
    }

    // ------------------------
    // state saving/restoring
    // ------------------------

    override fun onSaveInstanceState(outState: Bundle) {
        val state = settings.getViewState()
        outState.putParcelable("state", state)
//        Log.d("SettingsFragment", "state saved: $state")
        super.onSaveInstanceState(outState)
    }

    // ---------------
    // ISettingsChangedCallback - forwards events
    // ---------------

    override fun onSettingChanged(setting: ISetting<*>, customItem: SettingsCustomObject, oldValue: Any?, newValue: Any?) {
        settings.onSettingChanged(setting, customItem, oldValue, newValue)
    }

    override fun onCustomEnabledChanged(setting: ISetting<*>, customItem: SettingsCustomObject.Element, oldValue: Boolean, newValue: Boolean) {
        settings.onCustomEnabledChanged(setting, customItem, oldValue, newValue)
    }
}