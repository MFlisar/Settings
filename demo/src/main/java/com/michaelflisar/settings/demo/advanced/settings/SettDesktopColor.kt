package com.michaelflisar.settings.demo.advanced.settings

import com.michaelflisar.settings.utils.interfaces.ICustomSetting
import com.michaelflisar.settings.utils.interfaces.IGlobalSetting
import com.michaelflisar.settings.color.ColorSetting
import com.michaelflisar.settings.color.ColorSetup
import com.michaelflisar.settings.core.classes.SettingsIcon
import com.michaelflisar.settings.core.enums.ChangeType
import com.michaelflisar.settings.core.interfaces.ISettingsData
import com.michaelflisar.settings.demo.R
import com.michaelflisar.settings.demo.advanced.data.db.DesktopWithFolders
import com.michaelflisar.settings.demo.advanced.data.global.GlobalPreference
import com.michaelflisar.settings.utils.SettingsData
import com.michaelflisar.text.asText
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.runBlocking
import kotlinx.parcelize.Parcelize

@Parcelize
object SettDesktopColor : ColorSetting(
        1L, // TODO
        "Desktop color".asText(),
        null,
        null,
        SettingsIcon(R.drawable.ic_color_lens_black_24dp),
        ColorSetup(true)
), IGlobalSetting<Int, GlobalPreference>, ICustomSetting<Int, DesktopWithFolders> {

    override val onValueChanged = { settingsData: ISettingsData, change: ChangeType ->
        // callback... unused in the demo but may be useful in a real world example
        // e.g. to forward an event to an observable that is used in different UI parts of the app
    }

    // ------------
    // Global
    // ------------

    override val globalItem: GlobalPreference = GlobalPreference
    override val globalReadFunc: (GlobalPreference.() -> Int) = { desktopBackgroundColor.value }
    override val globalWriteFunc: (GlobalPreference.(Int) -> Boolean) = {
        runBlocking {
            desktopBackgroundColor.update(it)
        }
        true
    }

    override val onAfterGlobalWrite = null

    // ------------
    // Custom
    // ------------

    override fun getCustomItem(settingsData: SettingsData.Custom): DesktopWithFolders = settingsData.loadItem(DesktopWithFolders::class.java)

    override val customReadFunc: (DesktopWithFolders.() -> Int) = { desktop.customBackgroundColor }
    override val customWriteFunc: (DesktopWithFolders.(Int) -> Boolean) = {
        desktop.customBackgroundColor = it
        true
    }

    override val customReadIsEnabledFunc: (DesktopWithFolders.() -> Boolean) = { desktop.hasCustomBackgroundColor }
    override val customWriteIsEnabledFunc: (DesktopWithFolders.(Boolean) -> Boolean) = {
        desktop.hasCustomBackgroundColor = it
        true
    }

    override val onAfterCustomWrite= { item: DesktopWithFolders, change: ChangeType ->
        item.desktop.persist()
    }
}