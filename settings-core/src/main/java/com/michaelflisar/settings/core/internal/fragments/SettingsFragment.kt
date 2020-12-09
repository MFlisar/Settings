package com.michaelflisar.settings.core.internal.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.michaelflisar.settings.core.Settings
import com.michaelflisar.settings.core.SettingsManager
import com.michaelflisar.settings.core.classes.SettingsDefinition
import com.michaelflisar.settings.core.classes.SettingsDependency
import com.michaelflisar.settings.core.classes.SettingsDisplaySetup
import com.michaelflisar.settings.core.classes.SettingsState
import com.michaelflisar.settings.core.databinding.SettingsFragmentSettingsBinding
import com.michaelflisar.settings.core.enums.ChangeType
import com.michaelflisar.settings.core.interfaces.ISetting
import com.michaelflisar.settings.core.interfaces.ISettingsData
import com.michaelflisar.settings.core.internal.SettingsPayload
import com.michaelflisar.settings.core.settings.SettingsGroup

internal class SettingsFragment : Fragment() {

    companion object {

        fun create(settingsData: ISettingsData, group: SettingsGroup, dependencies: List<SettingsDependency<*>>, setup: SettingsDisplaySetup, state: SettingsState): SettingsFragment {
            val f = SettingsFragment()
            val args = Bundle().apply {
                putParcelable("settingsData", settingsData)
                if (SettingsManager.settingsProvider == null)
                    putParcelableArrayList("items", ArrayList(group.getItems()))
                else
                    putLongArray("itemIds", group.getItems().map { it.id }.toLongArray())
                putParcelableArrayList("dependencies", ArrayList(dependencies))
                putParcelable("setup", setup)
                putParcelable("state", state)
            }
            f.arguments = args
            return f
        }
    }

    private var binding: SettingsFragmentSettingsBinding? = null

    private lateinit var settingsData: ISettingsData
    private lateinit var items: ArrayList<ISetting<*>>
    private lateinit var dependencies: ArrayList<SettingsDependency<*>>
    private lateinit var setup: SettingsDisplaySetup
    private var state: SettingsState = SettingsState()
    private lateinit var settings: Settings

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments!!.let {
            settingsData = it.getParcelable("settingsData")!!
            if (SettingsManager.settingsProvider == null)
                items = it.getParcelableArrayList("items")!!
            else
                items = ArrayList(it.getLongArray("itemIds")!!.map { SettingsManager.settingsProvider!!.invoke(it) })
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

        settings = Settings(SettingsDefinition(items, dependencies, false), settingsData, setup)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

//        Log.d("SettingsFragment", "onCreateView1: $savedInstanceState")

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

//        Log.d("SettingsFragment", "onCreateView2: $savedInstanceState")

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

    fun onDependencyChanged(dependency: SettingsDependency<*>, payload: SettingsPayload) {
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

    fun onSettingChanged(changeType: ChangeType, setting: ISetting<*>, settingsData: ISettingsData, oldValue: Any?, newValue: Any?) {
        settings.onSettingChanged(changeType, setting, settingsData, oldValue, newValue)
    }
}