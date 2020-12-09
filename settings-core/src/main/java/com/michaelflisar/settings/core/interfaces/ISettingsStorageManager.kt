package com.michaelflisar.settings.core.interfaces

import android.os.Parcelable

interface ISettingsStorageManager {

    val supportedModules: List<ISettingsModule>

    // TODO: zusätzlich auch SettingsDataHolder DataItem prüfen??? dann kann man mehrere Manager gleichzeitig registrieren...
    fun supports(setting: ISetting<*>) = getModule(setting) != null

    fun getModule(setting: ISetting<*>): ISettingsModule? {
        supportedModules.forEach { module ->
            module.supportedTypes.forEach {
                if (it.isAssignableFrom(setting::class.java)) {
                    return module
                }
            }
        }
        return null
    }

    fun init() {
        supportedModules.forEach { it.init() }
    }

    fun <T> readGlobal(setting: ISetting<*>, settingsData: ISettingsData.Global): T
    fun <T> readCustom(setting: ISetting<*>, settingsData: ISettingsData.Element): T
    fun readCustomEnabled(setting: ISetting<*>, settingsDataHolder: ISettingsData.Element): Boolean
    fun <T> writeGlobal(setting: ISetting<*>, value: T, settingsData: ISettingsData.Global): Boolean
    fun <T> writeCustom(setting: ISetting<*>, value: T, settingsData: ISettingsData.Element): Boolean
    fun writeCustomEnabled(setting: ISetting<*>, value: Boolean, settingsData: ISettingsData.Element): Boolean
}