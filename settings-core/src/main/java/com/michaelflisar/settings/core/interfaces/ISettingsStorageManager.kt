package com.michaelflisar.settings.core.interfaces

interface ISettingsStorageManager {

    val supportedModules: List<ISettingsModule>

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

    fun <T> readGlobal(setting: ISetting<*>): T
    fun <T> readCustom(setting: ISetting<*>, customItem: Any): T
    fun readCustomEnabled(setting: ISetting<*>, customItem: Any): Boolean
    fun <T> writeGlobal(setting: ISetting<*>, value: T): Boolean
    fun <T> writeCustom(setting: ISetting<*>, value: T, customItem: Any): Boolean
    fun writeCustomEnabled(setting: ISetting<*>, value: Boolean, customItem: Any): Boolean
}