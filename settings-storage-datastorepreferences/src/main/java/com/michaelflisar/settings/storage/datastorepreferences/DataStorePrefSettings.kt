package com.michaelflisar.settings.storage.datastorepreferences

import android.content.Context
import androidx.datastore.DataStore
import androidx.datastore.preferences.*
import com.michaelflisar.settings.color.ColorSetting
import com.michaelflisar.settings.color.SettingsColorModule
import com.michaelflisar.settings.core.SettingsCoreModule
import com.michaelflisar.settings.core.interfaces.ISetting
import com.michaelflisar.settings.core.interfaces.ISettingsListItem
import com.michaelflisar.settings.core.interfaces.ISettingsStorageManager
import com.michaelflisar.settings.core.settings.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

open class DataStorePrefSettings(
        context: Context,
        storeName: String
) : ISettingsStorageManager {

    private val dataStore: DataStore<Preferences> = context.createDataStore(storeName)

    // ------------------
    // ISettingsStorageManager
    // ------------------

    override val supportedModules = listOf(
            SettingsCoreModule,
            SettingsColorModule
    )

    private fun <T> readValue(key: String, setting: ISetting<*>): T {
        castIntSetting(setting)?.let {
            return read(key, it.defaultValue) as T
        }
        castBooleanSetting(setting)?.let {
            return read(key, it.defaultValue) as T
        }
        castStringSetting(setting)?.let {
            return read(key, it.defaultValue) as T
        }
        castListSetting(setting)?.let {
            val id = read(key, it.defaultValue.id)
            return it.setup.getItemByID(id) as T
        }
        castMultiListSetting(setting)?.let {
            val ids = readSet(key, it.defaultValue.items.map { it.id.toString() }.toSet())
            val data: List<ISettingsListItem> = ids.map { id -> it.setup.getItemByID(id.toLong()) }
            return MultiListSetting.MultiListData(data) as T
        }
        castColorSetting(setting)?.let {
            return read(key, it.defaultValue) as T
        }
        throw RuntimeException("Unsupported setting received!")
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> readGlobal(setting: ISetting<*>): T {
        val key = getGlobalKey(setting)
        return readValue(key, setting)
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> readCustom(setting: ISetting<*>, customItem: Any): T {
        val key = getCustomKey(setting, customItem as IPrefSubIDProvider)
        return readValue(key, setting)
    }

    override fun readCustomEnabled(setting: ISetting<*>, customItem: Any): Boolean {
        val key = getCustomIsEnabledKey(setting, customItem as IPrefSubIDProvider)
        val defaultIsCustomEnabled = setting.defaultIsCustomEnabled
        return read(key, defaultIsCustomEnabled)
    }

    private fun <T> writeValue(key: String, setting: ISetting<*>, value: T) {
        castIntSetting(setting)?.let {
            write(key, value as Int)
            return
        }
        castBooleanSetting(setting)?.let {
            write(key, value as Boolean)
            return
        }
        castStringSetting(setting)?.let {
            write(key, value as String)
            return
        }
        castListSetting(setting)?.let {
            write(key, (value as ISettingsListItem).id)
            return
        }
        castMultiListSetting(setting)?.let {
            val ids = (value as MultiListSetting.MultiListData).items.map { it.id.toString() }.toSet()
            writeSet(key, ids)
            return
        }
        castColorSetting(setting)?.let {
            write(key, value as Int)
            return
        }
        throw RuntimeException("Unsupported setting received!")
    }

    override fun <T> writeGlobal(setting: ISetting<*>, value: T): Boolean {
        val key = getGlobalKey(setting)
        writeValue(key, setting, value)
        return true
    }

    override fun <T> writeCustom(setting: ISetting<*>, value: T, customItem: Any): Boolean {
        val key = getCustomKey(setting, customItem as IPrefSubIDProvider)
        writeValue(key, setting, value)
        return true
    }

    override fun writeCustomEnabled(setting: ISetting<*>, value: Boolean, customItem: Any): Boolean {
        val key = getCustomIsEnabledKey(setting, customItem as IPrefSubIDProvider)
        write(key, value)
        return true
    }

    // ------------------
    // DataStorage functions
    // ------------------

    private inline fun <reified T : Any> read(key: String, defaultValue: T): T {
        val k = preferencesKey<T>(key)
        val value = runBlocking {
            dataStore.data.first()[k] ?: defaultValue
        }
        return value
    }

    private fun readSet(key: String, defaultValue: Set<String>): Set<String> {
        val k = preferencesSetKey<String>(key)
        val value = runBlocking {
            dataStore.data.first()[k] ?: defaultValue
        }
        return value
    }

    private inline fun <reified T : Any> write(key: String, value: T) {
        val k = preferencesKey<T>(key)
        runBlocking {
            dataStore.edit {
                it[k] = value
            }
        }
    }

    private fun writeSet(key: String, value: Set<String>) {
        val k = preferencesSetKey<String>(key)
        runBlocking {
            dataStore.edit {
                it[k] = value
            }
        }
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