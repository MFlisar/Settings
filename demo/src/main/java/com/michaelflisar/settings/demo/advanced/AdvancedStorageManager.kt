package com.michaelflisar.settings.demo.advanced

import android.util.Log
import com.michaelflisar.settings.color.SettingsColorModule
import com.michaelflisar.settings.core.SettingsCoreModule
import com.michaelflisar.settings.core.SettingsManager
import com.michaelflisar.settings.core.SettingsUtils
import com.michaelflisar.settings.core.enums.ChangeType
import com.michaelflisar.settings.core.get
import com.michaelflisar.settings.core.interfaces.ISetting
import com.michaelflisar.settings.core.interfaces.ISettingsChangedCallback
import com.michaelflisar.settings.core.interfaces.ISettingsData
import com.michaelflisar.settings.core.interfaces.ISettingsStorageManager
import com.michaelflisar.settings.image.SettingsImageModule
import com.michaelflisar.settings.utils.SettingsData
import com.michaelflisar.settings.utils.interfaces.ICustomSetting
import com.michaelflisar.settings.utils.interfaces.IGlobalSetting

@Suppress("UNCHECKED_CAST")
object AdvancedStorageManager : ISettingsStorageManager {

    init {
        // TODO: Items sollten onValueChanged selbst aufrufen, sie wissen schließlich wann etwas geändert wurde!!!
        SettingsManager.registerCallback(object : ISettingsChangedCallback {
            override fun onSettingChanged(changeType: ChangeType, setting: ISetting<*>, settingsData: ISettingsData, oldValue: Any?, newValue: Any?) {
                val settingsData = settingsData as SettingsData
                Log.d("AdvancedStorageManager", "Setting changed: $setting | ${setting.label.get()} | ${setting.id} - ${settingsData.itemId} | $changeType")
            }
        })

        // lets check that all settings hacve unique ids
        val allSettings = SettingsDefinitions.SETTINGS_FOR_DESKTOPS
                .plus(SettingsDefinitions.SETTINGS_FOR_FOLDERS)
        SettingsUtils.checkUniqueIds(allSettings, true)
    }

    override val supportedModules = listOf(
            SettingsCoreModule,
            SettingsColorModule,
            SettingsImageModule(123)
    )

    override fun <T> readGlobal(setting: ISetting<*>, settingsData: ISettingsData.Global): T = getGlobalSettingsItem<T>(setting).readGlobal(settingsData as SettingsData.Global)
    override fun <T> readCustom(setting: ISetting<*>, settingsData: ISettingsData.Element): T = getCustomSettingsItem<T>(setting).readCustom(settingsData as DBSettingsData)
    override fun readCustomEnabled(setting: ISetting<*>, settingsData: ISettingsData.Element): Boolean = getCustomSettingsItemGeneric(setting).readIsCustomEnabled(settingsData as DBSettingsData)

    override fun <T> writeGlobal(setting: ISetting<*>, value: T, settingsData: ISettingsData.Global): Boolean = getGlobalSettingsItem<T>(setting).writeGlobal(settingsData as SettingsData.Global, value)
    override fun <T> writeCustom(setting: ISetting<*>, value: T, settingsData: ISettingsData.Element): Boolean = getCustomSettingsItem<T>(setting).writeCustom(settingsData as DBSettingsData, value)
    override fun writeCustomEnabled(setting: ISetting<*>, value: Boolean, settingsData: ISettingsData.Element): Boolean = getCustomSettingsItemGeneric(setting).writeIsCustomEnabled(settingsData as DBSettingsData, value)

    // ---------
    // SettDatas
    // ---------

    private fun <T> getGlobalSettingsItem(setting: ISetting<*>) = setting as IGlobalSetting<T, *>
    private fun <T> getCustomSettingsItem(setting: ISetting<*>) = setting as ICustomSetting<T, *>
    private fun getCustomSettingsItemGeneric(setting: ISetting<*>) = setting as ICustomSetting<*, *>
}