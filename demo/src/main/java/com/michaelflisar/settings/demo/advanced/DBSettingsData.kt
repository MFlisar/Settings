package com.michaelflisar.settings.demo.advanced

import android.util.Log
import com.michaelflisar.settings.demo.advanced.data.db.DesktopWithFolders
import com.michaelflisar.settings.demo.advanced.data.db.FolderWithFolders
import com.michaelflisar.settings.utils.SettingsData
import kotlinx.parcelize.Parcelize
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

@Parcelize
class DBSettingsData(
        override val itemId: Long
) : SettingsData.Custom() {

    override fun <T> loadItem(clazz: Class<T>): T {
        Log.d("AdvancedStorageManager", "load: $clazz")
        return runBlocking(Dispatchers.IO) {
            when (clazz) {
                DesktopWithFolders::class.java -> DBManager.get().desktopDao().getWithData(itemId) as T
                FolderWithFolders::class.java -> DBManager.get().folderDao().getWithData(itemId) as T
                else -> throw RuntimeException("Class $clazz not handled!")
            }
        }
    }

}