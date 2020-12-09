package com.michaelflisar.settings.demo.activities

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import com.evernote.android.state.State
import com.evernote.android.state.StateSaver
import com.michaelflisar.settings.core.Settings
import com.michaelflisar.settings.core.classes.SettingsDisplaySetup
import com.michaelflisar.settings.core.SettingsManager
import com.michaelflisar.settings.core.classes.SettingsFilter
import com.michaelflisar.settings.core.classes.SettingsState
import com.michaelflisar.settings.core.classes.SettingsStyle
import com.michaelflisar.settings.core.enums.ChangeType
import com.michaelflisar.settings.core.enums.CountDisplayType
import com.michaelflisar.settings.core.enums.HelpStyle
import com.michaelflisar.settings.core.decorator.IntentionMode
import com.michaelflisar.settings.core.get
import com.michaelflisar.settings.core.interfaces.ISetting
import com.michaelflisar.settings.core.interfaces.ISettingsChangedCallback
import com.michaelflisar.settings.core.interfaces.ISettingsData
import com.michaelflisar.settings.core.decorator.Style
import com.michaelflisar.settings.demo.simple.PrefUserSettings
import com.michaelflisar.settings.demo.R
import com.michaelflisar.settings.demo.simple.SettingsDefs
import com.michaelflisar.settings.demo.simple.classes.AppLayoutStyleEnum
import com.michaelflisar.settings.demo.simple.classes.CustomLayoutStyleEnum
import com.michaelflisar.settings.demo.databinding.ActivitySimpleDemo2Binding

class SimpleDemo2Activity : AppCompatActivity() {

    private lateinit var binding: ActivitySimpleDemo2Binding

    private var settings: Settings? = null

    @State
    var filter: String = ""

    @State
    var selectedPrefItem: PrefUserSettings? = null

    @State
    var settingsState: SettingsState = SettingsDefs.INITIAL_SETTINGS_STATE

    // ----------------
    // Callback - to listen to all changes of settings
    // this callback is optional of course
    // ---------------

    val onChangeCallback = object : ISettingsChangedCallback {
        override fun onSettingChanged(changeType: ChangeType, setting: ISetting<*>, settingsData: ISettingsData, oldValue: Any?, newValue: Any?) {

            if (changeType == ChangeType.CustomIsEnabled) {
                val parent = (settingsData as PrefUserSettings).userName
                Toast.makeText(this@SimpleDemo2Activity, "[$parent] Setting '${setting.label.get()}' enable changed: $oldValue => $newValue", Toast.LENGTH_SHORT).show()
                return
            }
            // we know that custom items can only be of type PrefUserSettings in this demo!
            val parent = if (settingsData.isGlobal) "Global Settings" else  (settingsData as PrefUserSettings).userName
            Toast.makeText(this@SimpleDemo2Activity, "[$parent] Setting '${setting.label.get()}' changed: $oldValue => $newValue", Toast.LENGTH_SHORT).show()

            // we also have some app settings so we handle their changes here and update our UI accordingly
            when (setting) {
                SettingsDefs.appSettingDarkTheme -> recreate()
                SettingsDefs.appSettingLayoutStyle,
                SettingsDefs.appSettingExpandSingleGroupsOnly -> {
                    updateUI()
                }
                SettingsDefs.appSettingCustomLayoutStyle -> {
                    // we do not need to update the UI if the global settings are currently selected
                    if (selectedPrefItem != null) {
                        updateUI()
                    }
                }
            }
        }
    }

    // ----------------
    // activity lifecycle
    // ---------------

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(if (SettingsDefs.appSettingDarkTheme.read(SettingsDefs.GLOBAL)) R.style.AppThemeDark else R.style.AppTheme)
        super.onCreate(savedInstanceState)
        StateSaver.restoreInstanceState(this, savedInstanceState);
        binding = ActivitySimpleDemo2Binding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        // prepare demo setting views
        initUserSpinner()

        // we want to listen to change events - ATTENTION: don't forget to unregister otherwise you will leak this activity!
        SettingsManager.registerCallback(onChangeCallback)

        // init UI
        updateUI()
        binding.tilFilter.editText?.doOnTextChanged { text, _, _, _ ->
            // forward filter string to setting objects
            filter = text?.toString() ?: ""
            settings?.filter(filter)
            Log.d("DemoActivity", "Constraint: $text")
        }
        filter.takeIf { it.isNotEmpty() }?.let {
            binding.tilFilter.editText?.setText(it)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // update settings state before it gets persisted
        settings?.getViewState()?.let {
            settingsState = it
        }
        StateSaver.saveInstanceState(this, outState)
    }

    override fun onDestroy() {
        // unregister the optional change callback to not leak the activity
        SettingsManager.unregisterCallback(onChangeCallback)
        // IMPORTANT: unbind the settings object to avoid memory leaks!
        settings?.unbind()
        super.onDestroy()
    }

    // ----------------
    // settings specific UI functions
    // ---------------

    private fun updateUI() {

        // 1) create an instance of DisplaySetup to define how the settings should look like
        val setup = SettingsDisplaySetup(
                showID = false,
                useDarkTheme = SettingsDefs.appSettingDarkTheme.read(SettingsDefs.GLOBAL),
                defaultSettingsLabel = R.string.custom_default_setting_label,
                showNumbersForGroups = true,
                showNumbersForItems = true,
                subItemsCountDisplayType = CountDisplayType.AllChildren(false, false),
                expandable = true,
                expandSingleOnly = SettingsDefs.appSettingExpandSingleGroupsOnly.read(SettingsDefs.GLOBAL),
                expandAllOnFilter = true,
                customLayout = CustomLayoutStyleEnum.getById(SettingsDefs.appSettingCustomLayoutStyle.read(SettingsDefs.GLOBAL).id).wrapped,
                style = SettingsStyle(
                        Style.CardsRounded,
                        Style.CardsRounded,
                        SettingsStyle.GroupStyle(
                                subGroupStyle = SettingsStyle.SubGroupColorMode.Border
//                        , groupColor = SettingsColor.Color(Color.GRAY)
//                        , groupTextColor = SettingsColor.Color(Color.WHITE)
                        )
                ),
                showEmptyGroups = false,
                intentionMode = IntentionMode.SubGroups,
                noDataFoundIcon = R.drawable.nodatafound,
                filter = SettingsFilter(true, true, true),
                helpStyle = HelpStyle.Icon(HelpStyle.Mode.Click)
        )

        // optional) only necessary here because we do allow to change the settings object
        // normally this only needs to be done in the activity's onDestroy function or the fragments onDestroyView function
        // this clean up references to this old settings object (used to support DialogFragments and support screen rotation and update UI even after a dialog is shown and the screen is rotated)
        // without the need of implementing an interface in the dialogs parent
        // it also undos changes in your UI (adding an item decorator) from bind functions
        settings?.unbind()

        // 2) create a new instance of Settings to define which which settings should be displayed based on current selections
        // this automatically registers this object inside a global singleton
        // to makes sure that you don't need to implement an interface in your activities/fragments and settings will still support DialogFragments and will handle dialog events correctly
        // even after a screen rotation, activity/fragment restoration and so on => no need for an interface BUT you need to register + unregsiter the callbacks to update the UI correctly
        // after settings have changed => DON'T FORGET TO UNREGISTER OR YOU MAY LEAK THE ACTIVITY!
        settings = Settings(
                SettingsDefs.ALL_DEFINITIONS,
                if (selectedPrefItem == null) SettingsDefs.GLOBAL else selectedPrefItem!!,
                setup
        )

        val useList = SettingsDefs.appSettingLayoutStyle.read(SettingsDefs.GLOBAL).id == AppLayoutStyleEnum.List.id

        // 3) bind settings to the UI - this function does the "magic" => and you're done!
        val viewContext = Settings.ViewContext.Activity(this)
        if (useList) {
            settings?.bind(viewContext, settingsState, binding.rvSettings, binding.ivEmpty)
        } else {
            settings?.bind(viewContext, settingsState, binding.tabs, binding.vp)
        }

        // 4) re-apply any existing filter
        settings?.filter(filter)

        // 5) update demo UI based on which layout style is selected
        binding.tabs.visibility = if (useList) View.GONE else View.VISIBLE
        binding.vp.visibility = if (useList) View.GONE else View.VISIBLE
        binding.rvSettings.visibility = if (useList) View.VISIBLE else View.GONE
        if (!useList) {
            binding.ivEmpty.visibility = View.GONE
        }
    }

    // -----------
    // DEMO UI - User spinner
    // -----------

    fun initUserSpinner() {
        val items = ArrayList<String>()
        items.add("Global Settings")
        items.addAll(SettingsDefs.USERS.map { it.userName })

        val dataAdapter: ArrayAdapter<String> = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, items)
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spUserSpinner.adapter = dataAdapter
        binding.spUserSpinner.setSelection(getSelectedUserSpinnerIndex(), false)
        binding.spUserSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                if (pos != getSelectedUserSpinnerIndex()) {
                    selectedPrefItem = pos.takeIf { it > 0 }?.let { SettingsDefs.USERS[it - 1] }
                    updateUI()
                }
            }
        }
    }

    fun getSelectedUserSpinnerIndex(): Int {
        return selectedPrefItem?.let { SettingsDefs.USERS.indexOf(it) + 1 } ?: 0
    }
}