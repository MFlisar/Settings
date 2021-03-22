package com.michaelflisar.settings.demo.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.michaelflisar.settings.core.Settings
import com.michaelflisar.settings.core.SettingsUtils
import com.michaelflisar.settings.core.classes.SettingsColor
import com.michaelflisar.settings.core.classes.SettingsDisplaySetup
import com.michaelflisar.settings.core.classes.SettingsFilter
import com.michaelflisar.settings.core.classes.SettingsStyle
import com.michaelflisar.settings.core.enums.CountDisplayType
import com.michaelflisar.settings.core.enums.HelpStyle
import com.michaelflisar.settings.core.decorator.IntentionMode
import com.michaelflisar.settings.core.decorator.Style
import com.michaelflisar.settings.demo.R
import com.michaelflisar.settings.demo.databinding.ActivitySimpleDemo1Binding
import com.michaelflisar.settings.demo.simple.SettingsDefs
import com.michaelflisar.settings.demo.simple.classes.CustomLayoutStyleEnum

class SimpleDemo1Activity : AppCompatActivity() {

    lateinit var binding: ActivitySimpleDemo1Binding

    val setup by lazy {
        SettingsDisplaySetup(
                showID = false,
                defaultSettingsLabel = R.string.custom_default_setting_label,
                showNumbersForGroups = true,
                showNumbersForItems = true,
                showNumbersForViewPager = true,
                subItemsCountDisplayType = CountDisplayType.AllChildren(false, false),
                expandable = true,
                expandSingleOnly = SettingsDefs.appSettingExpandSingleGroupsOnly.read(SettingsDefs.GLOBAL),
                expandAllOnFilter = true,
                customLayout = CustomLayoutStyleEnum.getById(SettingsDefs.appSettingCustomLayoutStyle.read(SettingsDefs.GLOBAL).id).wrapped,
                style = SettingsStyle(
                        Style.CardsRect,
                        Style.CardsRect,
                        SettingsStyle.GroupStyle(
                                subGroupStyle = SettingsStyle.SubGroupColorMode.Full(0.0f),
                                color = SettingsColor.Color(SettingsUtils.attrColor(this, android.R.attr.colorBackground)),
                                textColor = SettingsColor.Color(SettingsUtils.attrColor(this, android.R.attr.colorAccent))
                        ),
                        SettingsStyle.ItemStyle(
//                                color = SettingsColor.Color(Color.GREEN),
//                                textColor = SettingsColor.Color(Color.BLUE)
                        )
                ),
                groupTopLevelItemsWithoutGroups = true,
                showEmptyGroups = false,
                intentionMode = IntentionMode.SubGroups,
                noDataFoundIcon = R.drawable.nodatafound,
                filter = SettingsFilter(true, true, true),
                helpStyle = HelpStyle.Icon(HelpStyle.Mode.Click),
        )
    }

    val settings by lazy {
        Settings(
                SettingsDefs.ALL_DEFINITIONS,
                SettingsDefs.GLOBAL,
                setup
        )
    }
    val initialState = SettingsDefs.INITIAL_SETTINGS_STATE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySimpleDemo1Binding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        // that's all you need to do
        binding.settingsView.bind(Settings.ViewContext.Activity(this), initialState, settings)
    }
}