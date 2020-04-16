package com.michaelflisar.settings.core.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.michaelflisar.settings.core.SettingsDisplaySetup
import com.michaelflisar.settings.core.classes.SettingsDefinitions
import com.michaelflisar.settings.core.classes.SettingsGroup
import com.michaelflisar.settings.core.databinding.SettingsFragmentSettingsBinding
import com.michaelflisar.settings.core.interfaces.IBaseSetting

class SettingsFragment : Fragment() {

    companion object {

        fun create(globalSettings: Boolean, group: SettingsGroup, setup: SettingsDisplaySetup): SettingsFragment {
            val f = SettingsFragment()
            val args = Bundle().apply {
                putBoolean("global", globalSettings)
                putParcelableArrayList("items", group.items)
                putParcelable("setup", setup)
            }
            f.arguments = args
            return f
        }
    }

    private var binding: SettingsFragmentSettingsBinding? = null

    private var globalSettings: Boolean = true
    private lateinit var items: ArrayList<IBaseSetting>
    private lateinit var setup: SettingsDisplaySetup
    private lateinit var settingsDefinitions: SettingsDefinitions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments!!.let {
            globalSettings = it.getBoolean("global")
            items = it.getParcelableArrayList("items")!!
            setup = it.getParcelable("setup")!!
        }

        settingsDefinitions = SettingsDefinitions(items, globalSettings, setup)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = SettingsFragmentSettingsBinding.inflate(inflater)

        // bind settings to recyclerview
        settingsDefinitions.bindToRecyclerView(binding!!.rv)

        return binding!!.root
    }

    fun updateFilter(text: String?) {
        settingsDefinitions.filter(text)
    }
}