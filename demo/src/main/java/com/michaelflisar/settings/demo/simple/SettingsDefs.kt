package com.michaelflisar.settings.demo.simple

import android.graphics.Color
import com.michaelflisar.settings.color.ColorSetting
import com.michaelflisar.settings.color.ColorSetup
import com.michaelflisar.settings.core.asSettingsColor
import com.michaelflisar.settings.core.asSettingsIcon
import com.michaelflisar.settings.core.classes.SettingsDefinition
import com.michaelflisar.settings.core.classes.SettingsDependency
import com.michaelflisar.settings.core.classes.SettingsState
import com.michaelflisar.settings.core.enums.SupportType
import com.michaelflisar.settings.core.items.setups.InfoSetup
import com.michaelflisar.settings.core.items.setups.IntSetup
import com.michaelflisar.settings.core.items.setups.ListSetup
import com.michaelflisar.settings.core.items.setups.MultiListSetup
import com.michaelflisar.settings.core.settings.*
import com.michaelflisar.settings.demo.R
import com.michaelflisar.settings.demo.simple.classes.AppLayoutStyleEnum
import com.michaelflisar.settings.demo.simple.classes.AppStyleEnum
import com.michaelflisar.settings.demo.simple.classes.BookmarkEnum
import com.michaelflisar.settings.demo.simple.classes.CustomLayoutStyleEnum
import com.michaelflisar.settings.utils.SettingsData
import com.michaelflisar.text.asText

object SettingsDefs {

    // IMPORTANT:
    // use positive IDs, negative IDs may be used in some rare cases inside the library!

    private val groupIconRes = R.drawable.folder_closed.asSettingsIcon()
    private val groupIconOpenedRes = R.drawable.folder_opened.asSettingsIcon() // optional

    // -------------
    // 1) Create all settings and add them to groups
    // ------------

    // 1.1) we create some groups
    private val gDemoAppSettings = SettingsGroup(0L, "Demo App".asText(), "Settings for this demo app".asText(), "These settings do have an effect in this app".asText(), groupIconRes, groupIconOpenedRes)
    private val gStringSettings = SettingsGroup(10000L, "Strings".asText(), "String Settings".asText(), null, groupIconRes, groupIconOpenedRes)
    private val gIntSettings = SettingsGroup(20000L, "Integers".asText(), "Integer Settings".asText(), null, groupIconRes, groupIconOpenedRes)
    private val gBoolSettings = SettingsGroup(30000L, "Booleans".asText(), "Boolean Settings".asText(), null, groupIconRes, groupIconOpenedRes)
    private val gListSettings = SettingsGroup(40000L, "Lists".asText(), "List Settings".asText(), null, groupIconRes, groupIconOpenedRes)
    private val gOtherSettingsGlobal = SettingsGroup(50000L, "Others Global".asText(), "Global Other Settings".asText(), null, groupIconRes, groupIconOpenedRes)
    private val gOtherSettings = SettingsGroup(60000L, "Others".asText(), "Other Settings".asText(), null, groupIconRes, groupIconOpenedRes)

    // 1.2) we add our demo app settings to the demo app group - we use global settings only here!
    val appInfo = InfoSetting(1L, "Information".asText(), "This group contains settings that will be applied in this demo app!".asText(), null, R.drawable.ic_baseline_info_24.asSettingsIcon(),
            setup = InfoSetup(Color.RED.asSettingsColor())
    )
            .addToGroup(gDemoAppSettings)
    val appSettingDarkTheme = BooleanSetting(2L, "Dark Theme".asText(), "Enable dark theme".asText(), null, R.drawable.ic_style_black_24dp.asSettingsIcon(),
            supportType = SupportType.GlobalOnly
    )
            .addToGroup(gDemoAppSettings)
    val appSettingLayoutStyle = ListSetting(3L, "Style".asText(), "Select between List and ViewPager style".asText(), null, R.drawable.ic_style_black_24dp.asSettingsIcon(),
            setup = ListSetup(AppLayoutStyleEnum.LIST, ListSetup.Mode.Popup, ListSetup.IconPosition.Right, ListSetup.Style.IconOnly),
            supportType = SupportType.GlobalOnly
    )
            .addToGroup(gDemoAppSettings)
    val appSettingCustomLayoutStyle = ListSetting(4L, "Custom Layout Style".asText(), "Style that is used for the per item settings".asText(), null, R.drawable.ic_style_black_24dp.asSettingsIcon(),
            setup = ListSetup(CustomLayoutStyleEnum.LIST, ListSetup.Mode.Dialog, ListSetup.IconPosition.Right, ListSetup.Style.TextOnly),
            supportType = SupportType.GlobalOnly
    )
            .addToGroup(gDemoAppSettings)
    val appSettingExpandSingleGroupsOnly = BooleanSetting(5L, "Expand single groups only".asText(), "If enabled only one group inside the settings can be expanded at once".asText(), "If enabled, only one single header can be expanded at once".asText(), R.drawable.ic_style_black_24dp.asSettingsIcon(),
            supportType = SupportType.GlobalOnly
    )
            .addToGroup(gDemoAppSettings)

    // 1.3) we add some string settings to the string settings group
    private val stringSettingUserName = StringSetting(10001L, "User Name".asText(), null, "Help User Name".asText(), R.drawable.ic_person_outline_black_24dp.asSettingsIcon())
            .addToGroup(gStringSettings)
    private val stringSettingUserLocation = StringSetting(10002L, "User Location".asText(), null, "Help User Location".asText(), R.drawable.ic_baseline_my_location_24.asSettingsIcon())
            .addToGroup(gStringSettings)
    private val stringSettingUserHobbys = StringSetting(10003L, "User Hobbys".asText(), null, "Help User Hobbys".asText(), R.drawable.ic_baseline_sports_soccer_24.asSettingsIcon())
            .addToGroup(gStringSettings)

    // 1.4) we add some int settings to the int settings group
    private val intSetting1 = IntSetting(20001L, "Music Phone".asText(), "Setting that uses a picker".asText(), null, R.drawable.ic_volume_up_black_24dp.asSettingsIcon(),
            setup = IntSetup.Picker(min = 0, max = 100, step = 5)
    )
            .addToGroup(gIntSettings)
    private val intSetting2 = IntSetting(20002L, "Volume Music (Input)".asText(), "Setting that uses a TextInput with rules".asText(), null, R.drawable.ic_volume_up_black_24dp.asSettingsIcon(),
            setup = IntSetup.Input(min = 0, max = 100, errorMessage = "Error, value not in range [0, 100]".asText())
    )
            .addToGroup(gIntSettings)

    // 1.5) we add some bool settings to the bool settings group
    private val boolSetting1 = BooleanSetting(30001L, "Likes Coffee".asText(), "Do you like coffee?".asText(), null, R.drawable.ic_baseline_local_drink_24.asSettingsIcon())
            .addToGroup(gBoolSettings)
    private val boolSetting2 = BooleanSetting(30002L, "Likes Software Development".asText(), "Do you like to write code?".asText(), null, R.drawable.ic_baseline_code_24.asSettingsIcon())
            .addToGroup(gBoolSettings)

    // 1.6) we add some list settings to the list settings group
    private val listSetting1 = ListSetting(40001L, "App Style".asText(), "Customise the general app appearance".asText(), null, R.drawable.ic_style_black_24dp.asSettingsIcon(),
            setup = ListSetup(AppStyleEnum.LIST, ListSetup.Mode.Popup, ListSetup.IconPosition.Right)
    )
            .addToGroup(gListSettings)
    private val listSetting2 = MultiListSetting(40002L, "Bookmarks".asText(), "Select multiple bookmarks at once".asText(), null, R.drawable.ic_baseline_bookmarks_24.asSettingsIcon(),
            setup = MultiListSetup(BookmarkEnum.LIST, MultiListSetup.DisplayType.CommaSeparated(3))
    )
            .addToGroup(gListSettings)

    // 1.7) we add some other settings to other settings group (global only!)
    private val otherSettingAppStarts = IntSetting(50001L, "App starts".asText(), "This item will only be visible in global settings and not for user settings + it is not editable".asText(), null, null,
            // this setting only allows a global value + it is only for displaying purposes and not editable
            supportType = SupportType.GlobalOnly,
            editable = false
    )
            .addToGroup(gOtherSettingsGlobal)

    // 1.8) we add some other settings to other settings group
    private val otherSetting2 = ColorSetting(60001L, "App Color".asText(), null, null, R.drawable.ic_color_lens_black_24dp.asSettingsIcon(),
            setup = ColorSetup(true)
    )
            .addToGroup(gOtherSettings)
    private val otherSettingsHasChildren = BooleanSetting(60002L, "Children".asText(), "Do you have children? This setting will enable/disable the items inside the group below".asText(), null, R.drawable.ic_person_outline_black_24dp.asSettingsIcon())
            .addToGroup(gOtherSettings)
    val otherSettingsHasChildrenInfo = InfoSetting(60003L, "Information".asText(), "Nested info to test numbering!".asText(), null, R.drawable.ic_baseline_info_24.asSettingsIcon())
            .addToGroup(gOtherSettings)
    private val otherSettingNestedGroup1 = SettingsGroup(60100L, "Children Information".asText(), null, null, groupIconRes, groupIconOpenedRes)
            .addToGroup(gOtherSettings)
    val otherSettingsHasChildrenInfo2 = InfoSetting(60101L, "Information".asText(), "Another nested info to test numbering!".asText(), null, R.drawable.ic_baseline_info_24.asSettingsIcon())
            .addToGroup(otherSettingNestedGroup1)
    private val otherSettingAmountFemaleChildren = IntSetting(60102L, "Daughters".asText(), "How many daughters do you have?".asText(), null, R.drawable.ic_person_outline_black_24dp.asSettingsIcon(),
            setup = IntSetup.Input(0, null, "Only positive values are allowed".asText())
    )
            .addToGroup(otherSettingNestedGroup1)
    private val otherSettingAmountMaleChildren = IntSetting(60103L, "Sons".asText(), "How many sons do you have?".asText(), null, R.drawable.ic_person_outline_black_24dp.asSettingsIcon(),
            setup = IntSetup.Input(0, null, "Only positive values are allowed".asText())
    )
            .addToGroup(otherSettingNestedGroup1)

    // 1.9) we create some items without groups
    private val itemWithoutGroup1 = StringSetting(70000L, "Top level 1".asText(), "Top level item without parent".asText(), null, R.drawable.ic_settings_black_24dp.asSettingsIcon())
    private val itemWithoutGroup2 = StringSetting(80000L, "Top level 2".asText(), "Top level item without parent".asText(), null, R.drawable.ic_settings_black_24dp.asSettingsIcon())

    // -------------
    // 2.1) create a SettingsDefinition object with all top settings
    // 2.2) optional: define dependencies between settings
    // ------------

    val ALL_DEFINITIONS = SettingsDefinition(
            listOf(gDemoAppSettings, gStringSettings, gIntSettings, gBoolSettings, gListSettings, gOtherSettingsGlobal, gOtherSettings, itemWithoutGroup1, itemWithoutGroup2),
            listOf(
                    // SettingsDependency(otherSettingNestedGroup1, otherSettingsHasChildren, SettingsDependency.BooleanValidator()), // alternatively you can also disable a whole a group instead of it's items
                    SettingsDependency(otherSettingAmountFemaleChildren, otherSettingsHasChildren, SettingsDependency.BooleanValidator()), // amount of female children depends on has children bool setting
                    SettingsDependency(otherSettingAmountMaleChildren, otherSettingsHasChildren, SettingsDependency.BooleanValidator()) // amount of male children depends on has children bool setting
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

    // we can use one global item for all settings here but we could optionally provide one per setting as well
    val GLOBAL = SettingsData.Global

    // ----------------------
    // 5) optional initialisation code - we write some initial values to make this example logically working
    // ----------------------

    fun onAppStart() {

        // update app counter
        val counter = otherSettingAppStarts.read(GLOBAL)
        otherSettingAppStarts.write(GLOBAL, counter + 1)

        if (counter > 0)
            return

        // initialise some global values
        GLOBAL.let {
            stringSettingUserName.write(it, "Global User Name")
            stringSettingUserHobbys.write(it, "Football, Soccer, ...")
            stringSettingUserLocation.write(it, "New York")
            itemWithoutGroup1.write(it, "Test 1")
            itemWithoutGroup2.write(it, "Test 2")
            appSettingLayoutStyle.write(it, AppLayoutStyleEnum.List)
            appSettingCustomLayoutStyle.write(it, CustomLayoutStyleEnum.LargeInfo)
            listSetting1.write(it, AppStyleEnum.Classic)
            listSetting2.write(it, MultiListSetting.MultiListData(listOf(BookmarkEnum.Bookmark1)))
        }

        // change some user initial values
        for (user in USERS) {
            stringSettingUserName.write(user, user.userName)
        }
    }
}