package com.michaelflisar.settings.demo_simple

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import com.michaelflisar.settings.core.SettingsDisplaySetup
import com.michaelflisar.settings.core.SettingsManager
import com.michaelflisar.settings.core.classes.SettingsDefinitions
import com.michaelflisar.settings.demo_simple.databinding.ActivityDemoBinding
import com.michaelflisar.settings.sharedpreferences.SharedPrefSettingsManager


class DemoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDemoBinding

    private var useList: Boolean = true
    private lateinit var settingsDefinitons: SettingsDefinitions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDemoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 0) Init Settings - should be done in an application ONCE only
        SettingsManager.init(this)
        SharedPrefSettingsManager.init(this, "demo_prefs")

        // 1) create a display setup
        val displaySetup = SettingsDisplaySetup(
                showID = true,
                showNumbers = true,
                expandable = true,
                expandSingleOnly = true,
                expandAllOnFilter = true
        )

        // 2) get the SettingsDefinitions that we want to show + optionally adjust their settings
        settingsDefinitons = SettingsDefinitions(SettingsDefs.settings, true, displaySetup)

        // 3) init UI - call on of the bind* methods
        updateUI()
        binding.tilFilter.editText?.doOnTextChanged { text, start, count, after ->
            // forward filter string to setting objects
            settingsDefinitons.filter(text?.toString())
            Log.d("DemoActivity", "Constraint: $text")
        }
    }

    private fun updateUI() {

        if (useList) {
            settingsDefinitons.bindToRecyclerView(binding.rvSettings)
        } else {
            settingsDefinitons.bindToViewPager(supportFragmentManager, binding.tabs, binding.vp)
        }

        binding.tabs.visibility = if (useList) View.GONE else View.VISIBLE
        binding.vp.visibility = if (useList) View.GONE else View.VISIBLE
        binding.rvSettings.visibility = if (useList) View.VISIBLE else View.GONE
    }

    // -----------
    // Menu
    // -----------

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        val inflater = menuInflater
        inflater.inflate(R.menu.demo_activity_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_list -> {
                useList = true
                updateUI()
                true
            }
            R.id.menu_viewpager ->  {
                useList = false
                updateUI()
                true
            }
            else -> super.onContextItemSelected(item)
        }
    }
}