package com.michaelflisar.settings.demo_simple

import com.michaelflisar.settings.core.classes.SettingsGroup
import com.michaelflisar.settings.core.classes.SettingsIcon
import com.michaelflisar.settings.core.classes.SettingsText
import com.michaelflisar.settings.sharedpreferences.BooleanPrefSetting

object SettingsDefs {

    // IMPORTANT:
    // use positive IDs, negative IDs may be used in some rare cases inside the library!

    // ----------------------
    // Group 1 - App Settings
    // ----------------------

    private val groupApp = SettingsGroup(
            1L,
            SettingsText("App Settings"),
            SettingsText("General App Settings"),
            SettingsIcon(R.mipmap.ic_launcher)
    ).apply {

        // add all sub items
        add(
                // Setting 1 - App Style
                BooleanPrefSetting(
                        this,
                        1000L,
                        SettingsText("App Style"),
                        SettingsText("Customise the general app appearance"),
                        SettingsIcon(R.mipmap.ic_launcher),
                        true
                ),
                // Setting 2 - App Color
                BooleanPrefSetting(
                        this,
                        1001L,
                        SettingsText("App Color"),
                        null,
                        SettingsIcon(R.mipmap.ic_launcher),
                        true
                ),
                // Setting 3 - Sub Group
                SettingsGroup(
                        1002L,
                        SettingsText("App Settings Sub Group"),
                        null,
                        SettingsIcon(R.mipmap.ic_launcher)
                ).apply {

                    // add all sub items
                    add(
                            // Setting 3.1 - Sub Setting 1
                            BooleanPrefSetting(
                                    this,
                                    1003L,
                                    SettingsText("App Sub Setting 1"),
                                    SettingsText("Enable sub setting 1"),
                                    SettingsIcon(R.mipmap.ic_launcher),
                                    true
                            ),
                            // Setting 3.2 - Sub Setting 2
                            BooleanPrefSetting(
                                    this,
                                    1004L,
                                    SettingsText("App Sub Setting 2"),
                                    SettingsText("Enable sub setting 2"),
                                    SettingsIcon(R.mipmap.ic_launcher),
                                    true
                            )
                    )
                }

        )

    }

    // ----------------------
    // Group 2 - Sound Settings
    // ----------------------

    private val groupSounds = SettingsGroup(
            2L,
            SettingsText("Sound Settings"),
            SettingsText("General Sound Settings"),
            SettingsIcon(R.mipmap.ic_launcher)
    ).apply {

        // add all sub items
        add(
                // Setting 1 - App Style
                BooleanPrefSetting(
                        this,
                        2000L,
                        SettingsText("Sound"),
                        null,
                        SettingsIcon(R.mipmap.ic_launcher),
                        true
                ),
                // Setting 2 - App Color
                BooleanPrefSetting(
                        this,
                        2001L,
                        SettingsText("Volume"),
                        null,
                        SettingsIcon(R.mipmap.ic_launcher),
                        true
                )
        )
    }

    // ----------------------
    // List of all groups
    // ----------------------

    val settings = listOf(groupApp, groupSounds)

}