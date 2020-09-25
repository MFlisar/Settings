package com.michaelflisar.settings.demo.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.michaelflisar.settings.core.Settings
import com.michaelflisar.settings.core.classes.SettingsDisplaySetup
import com.michaelflisar.settings.core.SettingsUtils
import com.michaelflisar.settings.core.classes.SettingsColor
import com.michaelflisar.settings.core.classes.SettingsCustomObject
import com.michaelflisar.settings.core.classes.SettingsFilter
import com.michaelflisar.settings.core.classes.SettingsStyle
import com.michaelflisar.settings.core.enums.CountDisplayType
import com.michaelflisar.settings.core.enums.HelpStyle
import com.michaelflisar.settings.core.enums.IntentionMode
import com.michaelflisar.settings.demo.R
import com.michaelflisar.settings.demo.SettingsDefs
import com.michaelflisar.settings.demo.classes.CustomLayoutStyleEnum
import com.michaelflisar.settings.demo.databinding.ActivityDemo1Binding

class Demo1Activity : AppCompatActivity() {

    lateinit var binding: ActivityDemo1Binding

    val setup by lazy {
        SettingsDisplaySetup(
                showID = false,
                useDarkTheme = false, /* this activity does not use this setting but is light instead */ // SettingsDefs.appSettingDarkTheme.readGlobal(),
                defaultSettingsLabel = R.string.custom_default_setting_label,
                showNumbersForGroups = true,
                showNumbersForItems = true,
                showNumbersForViewPager = true,
                subItemsCountDisplayType = CountDisplayType.AllChildren(false, false),
                expandable = true,
                expandSingleOnly = SettingsDefs.appSettingExpandSingleGroupsOnly.readGlobal(),
                expandAllOnFilter = true,
                customLayout = CustomLayoutStyleEnum.getById(SettingsDefs.appSettingCustomLayoutStyle.readGlobal().id).wrapped,
                style = SettingsStyle(
                        SettingsStyle.Style.CardsRect,
                        SettingsStyle.Style.CardsRect,
                        SettingsStyle.GroupStyle(
                                subGroupStyle = SettingsStyle.SubGroupColorMode.Full(0.0f),
                                color = SettingsColor.Color(SettingsUtils.attrColor(this, android.R.attr.colorBackground)),
                                textColor = SettingsColor.Color(SettingsUtils.attrColor(this, android.R.attr.textColorSecondary))
                        ),
                        SettingsStyle.ItemStyle(
//                                color = SettingsColor.Color(Color.GREEN),
//                                textColor = SettingsColor.Color(Color.BLUE)
                        )
                ),
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
                SettingsCustomObject.None,
                setup
        )
    }
    val initialState =  SettingsDefs.INITIAL_SETTINGS_STATE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDemo1Binding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        // that's all you need to do
        binding.settingsView.bind(Settings.ViewContext.Activity(this), initialState, settings)
    }
}