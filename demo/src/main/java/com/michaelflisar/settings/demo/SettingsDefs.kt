package com.michaelflisar.settings.demo

import android.graphics.Color
import com.michaelflisar.settings.color.ColorSetting
import com.michaelflisar.settings.color.ColorSetup
import com.michaelflisar.settings.core.classes.SettingsDefinition
import com.michaelflisar.settings.core.classes.SettingsCustomObject
import com.michaelflisar.settings.core.classes.SettingsDependency
import com.michaelflisar.settings.core.classes.SettingsState
import com.michaelflisar.settings.core.enums.SupportType
import com.michaelflisar.settings.core.items.setups.InfoSetup
import com.michaelflisar.settings.core.items.setups.IntSetup
import com.michaelflisar.settings.core.items.setups.ListSetup
import com.michaelflisar.settings.core.items.setups.MultiListSetup
import com.michaelflisar.settings.core.settings.*
import com.michaelflisar.settings.demo.classes.AppLayoutStyleEnum
import com.michaelflisar.settings.demo.classes.AppStyleEnum
import com.michaelflisar.settings.demo.classes.BookmarkEnum
import com.michaelflisar.settings.demo.classes.CustomLayoutStyleEnum
import com.michaelflisar.text.asText

object SettingsDefs {

    // IMPORTANT:
    // use positive IDs, negative IDs may be used in some rare cases inside the library!

    private val groupIconRes = R.drawable.folder_closed
    private val groupIconOpenedRes = R.drawable.folder_opened // optional

    // -------------
    // 1) Create all settings and add them to groups
    // ------------

    // 1.1) we create some groups
    private val gDemoAppSettings = SettingsGroup(0L, "Demo App", "Settings for this demo app", "These settings do have an effect in this app", groupIconRes, groupIconOpenedRes)
    private val gStringSettings = SettingsGroup(10000L, "Strings", "String Settings", null, groupIconRes, groupIconOpenedRes)
    private val gIntSettings = SettingsGroup(20000L, "Integers", "Integer Settings", null, groupIconRes, groupIconOpenedRes)
    private val gBoolSettings = SettingsGroup(30000L, "Booleans", "Boolean Settings", null, groupIconRes, groupIconOpenedRes)
    private val gListSettings = SettingsGroup(40000L, "Lists", "List Settings", null, groupIconRes, groupIconOpenedRes)
    private val gOtherSettingsGlobal = SettingsGroup(50000L, "Others Global", "Global Other Settings", null, groupIconRes, groupIconOpenedRes)
    private val gOtherSettings = SettingsGroup(60000L, "Others", "Other Settings", null, groupIconRes, groupIconOpenedRes)

    // 1.2) we add our demo app settings to the demo app group - we use global settings only here!
    val appInfo = InfoSetting(5L, "Information", "This group contains settings that will be applied in this demo app!", R.drawable.ic_baseline_info_24)
            .addToGroup(gDemoAppSettings)
            .apply {
                setup = InfoSetup(Color.RED)
            }
    val appSettingDarkTheme = BooleanSetting(1L, "Dark Theme", "Enable dark theme", null, R.drawable.ic_style_black_24dp, false)
            .addToGroup(gDemoAppSettings)
            .apply {
                supportType = SupportType.GlobalOnly
            }
    val appSettingLayoutStyle = ListSetting(2L, "Style", "Select between List and ViewPager style", null, R.drawable.ic_style_black_24dp, AppLayoutStyleEnum.List,
            ListSetup(AppLayoutStyleEnum.LIST, ListSetup.Mode.Popup, ListSetup.IconPosition.Right, ListSetup.Style.IconOnly))
            .addToGroup(gDemoAppSettings)
            .apply {
                supportType = SupportType.GlobalOnly
            }
    val appSettingCustomLayoutStyle = ListSetting(3L, "Custom Layout Style", "Style that is used for the per item settings", null, R.drawable.ic_style_black_24dp, CustomLayoutStyleEnum.Compact,
            ListSetup(CustomLayoutStyleEnum.LIST, ListSetup.Mode.Dialog, ListSetup.IconPosition.Right, ListSetup.Style.TextOnly))
            .addToGroup(gDemoAppSettings)
            .apply {
                supportType = SupportType.GlobalOnly
            }
    val appSettingExpandSingleGroupsOnly = BooleanSetting(4L, "Expand single groups only", "If enabled only one group inside the settings can be expanded at once", "If enabled, only one single header can be expanded at once", R.drawable.ic_style_black_24dp, false)
            .addToGroup(gDemoAppSettings)
            .apply {
                supportType = SupportType.GlobalOnly
            }

    // 1.3) we add some string settings to the string settings group
    private val stringSettingUserName = StringSetting(10001L, "User Name", null, "Help User Name", R.drawable.ic_person_outline_black_24dp, "Global User Name")
            .addToGroup(gStringSettings)
    private val stringSetting2 = StringSetting(10002L, "User Location", null, "Help User Location", R.drawable.ic_baseline_my_location_24, "Home")
            .addToGroup(gStringSettings)
    private val stringSetting3 = StringSetting(10003L, "User Hobbys", null, "Help User Hobbys", R.drawable.ic_baseline_sports_soccer_24, "Soccer, Football")
            .addToGroup(gStringSettings)

    // 1.4) we add some int settings to the int settings group
    private val intSetting1 = IntSetting(20001L, "Music Phone", "Setting that uses a picker", null, R.drawable.ic_volume_up_black_24dp, 50)
            .addToGroup(gIntSettings)
            .apply {
                setup = IntSetup.Picker(min = 0, max = 100, step = 5)
            }
    private val intSetting2 = IntSetting(20002L, "Volume Music (Input)", "Setting that uses a TextInput with rules", null, R.drawable.ic_volume_up_black_24dp, 50)
            .addToGroup(gIntSettings)
            .apply {
                setup = IntSetup.Input(min = 0, max = 100, errorMessage = "Error, value not in range [0, 100]".asText())
            }

    // 1.5) we add some bool settings to the bool settings group
    private val boolSetting1 = BooleanSetting(30001L, "Likes Coffee", "Do you like coffee?", null, R.drawable.ic_baseline_local_drink_24, false)
            .addToGroup(gBoolSettings)
    private val boolSetting2 = BooleanSetting(30002L, "Likes Software Development", "Do you like to write code?", null, R.drawable.ic_baseline_code_24, true)
            .addToGroup(gBoolSettings)

    // 1.6) we add some list settings to the list settings group
    private val listSetting1 = ListSetting(40001L, "App Style", "Customise the general app appearance", null, R.drawable.ic_style_black_24dp, AppStyleEnum.Classic,
            ListSetup(AppStyleEnum.LIST, ListSetup.Mode.Popup, ListSetup.IconPosition.Right))
            .addToGroup(gListSettings)
    private val listSetting2 = MultiListSetting(40002L, "Bookmarks", "Select multiple bookmarks at once", null, R.drawable.ic_baseline_bookmarks_24, MultiListSetting.MultiListData(BookmarkEnum.values().toList()),
            MultiListSetup(BookmarkEnum.LIST, MultiListSetup.DisplayType.CommaSeparated(3)))
            .addToGroup(gListSettings)

    // 1.7) we add some other settings to other settings group (global only!)
    private val otherSettingAppStarts = IntSetting(50001L, "App starts", "This item will only be visible in global settings and not for user settings + it is not editable", null, null, 0)
            .addToGroup(gOtherSettingsGlobal)
            .apply {
                // this setting only allows a global value + it is only for displaying purposes and not editable
                editable = false
                supportType = SupportType.GlobalOnly
            }

    // 1.8) we add some other settings to other settings group
    private val otherSetting2 = ColorSetting(60001L, "App Color", null, null, R.drawable.ic_color_lens_black_24dp, Color.RED).apply {
        setup = ColorSetup(true)
    }
            .addToGroup(gOtherSettings)
    private val otherSettingsHasChildren = BooleanSetting(60002L, "Children", "Do you have children? This setting will enable/disable the items inside the group below", null, R.drawable.ic_person_outline_black_24dp, false)
            .addToGroup(gOtherSettings)
    val otherSettingsHasChildrenInfo = InfoSetting(60003L, "Information", "Nested info to test numbering!", R.drawable.ic_baseline_info_24)
            .addToGroup(gOtherSettings)
    private val otherSettingNestedGroup1 = SettingsGroup(60100L, "Children Information", null, null, groupIconRes, groupIconOpenedRes)
            .addToGroup(gOtherSettings)
    val otherSettingsHasChildrenInfo2 = InfoSetting(60101L, "Information", "Another nested info to test numbering!", R.drawable.ic_baseline_info_24)
            .addToGroup(otherSettingNestedGroup1)
    private val otherSettingAmountFemaleChildren = IntSetting(60102L, "Daughters", "How many daughters do you have?", null, R.drawable.ic_person_outline_black_24dp, 0)
            .addToGroup(otherSettingNestedGroup1)
            .apply {
                setup = IntSetup.Input(0, null, "Only positive values are allowed".asText())
            }
    private val otherSettingAmountMaleChildren = IntSetting(60103L, "Sons", "How many sons do you have?", null, R.drawable.ic_person_outline_black_24dp, 0)
            .addToGroup(otherSettingNestedGroup1)
            .apply {
                setup = IntSetup.Input(0, null, "Only positive values are allowed".asText())
            }

    // 1.9) we create some items without groups
    private val itemWithoutGroup1 = StringSetting(70000L, "Top level 1", "Top level item without parent", null, R.drawable.ic_settings_black_24dp, "Test")
    private val itemWithoutGroup2 = StringSetting(80000L, "Top level 2", "Top level item without parent", null, R.drawable.ic_settings_black_24dp, "Test")

    // -------------
    // 2.1) create a SettingsDefinition object with all top settings
    // 2.2) optional: define dependencies between settings
    // ------------

    val ALL_DEFINITIONS = SettingsDefinition(
            listOf(gDemoAppSettings, gStringSettings, gIntSettings, gBoolSettings, gListSettings, gOtherSettingsGlobal, gOtherSettings, itemWithoutGroup1, itemWithoutGroup2),
            listOf(
                    // SettingsDependency(otherSettingNestedGroup1, otherSettingsHasChildren), // alternatively you can also disable a whole a group instead of it's items
                    SettingsDependency(otherSettingAmountFemaleChildren, otherSettingsHasChildren), // amount of female children depends on has children bool setting
                    SettingsDependency(otherSettingAmountMaleChildren, otherSettingsHasChildren) // amount of male children depends on has children bool setting
            )
    )

    // -------------
    // 3) optional - we define which settings are expanded by default (only used if items are expandable)
    // ------------

    val INITIAL_SETTINGS_STATE = SettingsState(
            // list of initially expanded ids
            expandedIds = ArrayList(listOf(gDemoAppSettings.id))
    )

    // ----------------------
    // 4) list of objects for per item settings - this are our items for the per items settings
    // ----------------------

    val USERS = listOf(
            PrefUserSettings(1, "User 1"),
            PrefUserSettings(2, "User 2"),
            PrefUserSettings(3, "User 3")
    )

    // ----------------------
    // 5) optional initialisation code - we write some initial values to make this example logically working
    // ----------------------

    fun onAppStart() {

        // update app counter
        val counter = otherSettingAppStarts.readSetting(SettingsCustomObject.None)
        otherSettingAppStarts.writeSetting(SettingsCustomObject.None, counter + 1)

        if (counter > 0)
            return

        // change some initial values
        for (user in USERS)
            stringSettingUserName.writeSetting(SettingsCustomObject.Element(user), user.userName)

    }
}