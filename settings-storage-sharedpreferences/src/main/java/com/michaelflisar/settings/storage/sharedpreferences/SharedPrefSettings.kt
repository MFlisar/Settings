package com.michaelflisar.settings.storage.sharedpreferences

import android.content.Context
import com.michaelflisar.settings.color.ColorSetting
import com.michaelflisar.settings.color.SettingsColorModule
import com.michaelflisar.settings.core.SettingsCoreModule
import com.michaelflisar.settings.core.interfaces.ISetting
import com.michaelflisar.settings.core.interfaces.ISettingsListItem
import com.michaelflisar.settings.core.interfaces.ISettingsStorageManager
import com.michaelflisar.settings.core.settings.*

open class SharedPrefSettings(
        context: Context,
        sharedPreferencesName: String
) : ISettingsStorageManager {

    private val sharedPreferences = context.applicationContext.getSharedPreferences(sharedPreferencesName, Context.MODE_PRIVATE)

    // ------------------
    // ISettingsStorageManager
    // ------------------

    override val supportedModules = listOf(
            SettingsCoreModule,
            SettingsColorModule
    )

    @Suppress("UNCHECKED_CAST")
    override fun <T> readGlobal(setting: ISetting<*>): T {
        val key = getGlobalKey(setting)
        castIntSetting(setting)?.let {
            return sharedPreferences.getInt(key, it.defaultValue) as T
        }
        castBooleanSetting(setting)?.let {
            return sharedPreferences.getBoolean(key, it.defaultValue) as T
        }
        castStringSetting(setting)?.let {
            return sharedPreferences.getString(key, it.defaultValue) as T
        }
        castListSetting(setting)?.let {
            val id = sharedPreferences.getLong(key, it.defaultValue.id)
            return it.setup.getItemByID(id) as T
        }
        castMultiListSetting(setting)?.let {
            val ids = sharedPreferences.getStringSet(key, it.defaultValue.items.map { it.id.toString() }.toSet())!!
            val data: List<ISettingsListItem> = ids.map { id -> it.setup.getItemByID(id.toLong()) }
            return MultiListSetting.MultiListData(data) as T
        }
        castColorSetting(setting)?.let {
            return sharedPreferences.getInt(key, it.defaultValue) as T
        }
        throw RuntimeException("Unsupported setting received!")
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> readCustom(setting: ISetting<*>, customItem: Any): T {
        val key = getCustomKey(setting, customItem as IPrefSubIDProvider)
        castIntSetting(setting)?.let {
            return sharedPreferences.getInt(key, it.defaultValue) as T
        }
        castBooleanSetting(setting)?.let {
            return sharedPreferences.getBoolean(key, it.defaultValue) as T
        }
        castStringSetting(setting)?.let {
            return sharedPreferences.getString(key, it.defaultValue) as T
        }
        castListSetting(setting)?.let {
            val id = sharedPreferences.getLong(key, it.defaultValue.id)
            return it.setup.getItemByID(id) as T
        }
        castMultiListSetting(setting)?.let {
            val ids = sharedPreferences.getStringSet(key, it.defaultValue.items.map { it.id.toString() }.toSet())!!
            val data: List<ISettingsListItem> = ids.map { id -> it.setup.getItemByID(id.toLong()) }
            return MultiListSetting.MultiListData(data) as T
        }
        castColorSetting(setting)?.let {
            return sharedPreferences.getInt(key, it.defaultValue) as T
        }
        throw RuntimeException("Unsupported setting received!")
    }

    override fun readCustomEnabled(setting: ISetting<*>, customItem: Any): Boolean {
        val key = getCustomIsEnabledKey(setting, customItem as IPrefSubIDProvider)
        val defaultIsCustomEnabled = setting.defaultIsCustomEnabled
        return sharedPreferences.getBoolean(key, defaultIsCustomEnabled)
    }

    override fun <T> writeGlobal(setting: ISetting<*>, value: T): Boolean {
        val key = getGlobalKey(setting)
        castIntSetting(setting)?.let {
            sharedPreferences.edit().putInt(key, value as Int).apply()
            return true
        }
        castBooleanSetting(setting)?.let {
            sharedPreferences.edit().putBoolean(key, value as Boolean).apply()
            return true
        }
        castStringSetting(setting)?.let {
            sharedPreferences.edit().putString(key, value as String).apply()
            return true
        }
        castListSetting(setting)?.let {
            sharedPreferences.edit().putLong(key, (value as ISettingsListItem).id).apply()
            return true
        }
        castMultiListSetting(setting)?.let {
            val ids = (value as MultiListSetting.MultiListData).items.map { it.id.toString() }.toSet()
            sharedPreferences.edit().putStringSet(key, ids).apply()
            return true
        }
        castColorSetting(setting)?.let {
            sharedPreferences.edit().putInt(key, value as Int).apply()
            return true
        }
        throw RuntimeException("Unsupported setting received!")
    }

    override fun <T> writeCustom(setting: ISetting<*>, value: T, customItem: Any): Boolean {
        val key = getCustomKey(setting, customItem as IPrefSubIDProvider)
        castIntSetting(setting)?.let {
            sharedPreferences.edit().putInt(key, value as Int).apply()
            return true
        }
        castBooleanSetting(setting)?.let {
            sharedPreferences.edit().putBoolean(key, value as Boolean).apply()
            return true
        }
        castStringSetting(setting)?.let {
            sharedPreferences.edit().putString(key, value as String).apply()
            return true
        }
        castListSetting(setting)?.let {
            sharedPreferences.edit().putLong(key, (value as ISettingsListItem).id).apply()
            return true
        }
        castMultiListSetting(setting)?.let {
            val ids = (value as MultiListSetting.MultiListData).items.map { it.id.toString() }.toSet()
            sharedPreferences.edit().putStringSet(key, ids).apply()
            return true
        }
        castColorSetting(setting)?.let {
            sharedPreferences.edit().putInt(key, value as Int).apply()
            return true
        }
        throw RuntimeException("Unsupported setting received!")
    }

    override fun writeCustomEnabled(setting: ISetting<*>, value: Boolean, customItem: Any): Boolean {
        val key = getCustomIsEnabledKey(setting, customItem as IPrefSubIDProvider)
        sharedPreferences.edit().putBoolean(key, value).apply()
        return true
    }

    // ----------------
    // private ISettingsStorageManager helper functions (cast functions for supported types + functions to calculate unique ids)
    // ----------------

    private fun castIntSetting(setting: ISetting<*>) = setting as? IntSetting
    private fun castBooleanSetting(setting: ISetting<*>) = setting as? BooleanSetting
    private fun castStringSetting(setting: ISetting<*>) = setting as? StringSetting
    private fun castListSetting(setting: ISetting<*>) = setting as? ListSetting
    private fun castMultiListSetting(setting: ISetting<*>) = setting as? MultiListSetting
    private fun castColorSetting(setting: ISetting<*>) = setting as? ColorSetting

    private fun getGlobalKey(setting: ISetting<*>): String = setting.id.toString()
    private fun getCustomKey(setting: ISetting<*>, item: IPrefSubIDProvider) = "${setting.id}|${item.id}"
    private fun getCustomIsEnabledKey(setting: ISetting<*>, item: IPrefSubIDProvider) = "E|${setting.id}|${item.id}"
}