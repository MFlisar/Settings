package com.michaelflisar.settings.demo.advanced

import android.content.Context
import android.graphics.Color
import androidx.room.Room
import com.michaelflisar.settings.demo.advanced.data.db.Database
import com.michaelflisar.settings.demo.advanced.data.db.entities.DBDesktopItem
import com.michaelflisar.settings.demo.advanced.data.db.entities.DBFolderItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object DBManager {
    private lateinit var db: Database

    fun init(context: Context) {
        db = Room.databaseBuilder(
            context.applicationContext,
            Database::class.java, "database"
        )
            .build()

        CoroutineScope(Dispatchers.IO).launch {
            if (db.desktopDao().getAll().isEmpty()) {
                createInitialData()
            }
        }
    }

    fun createInitialData() {

        // ------------------------
        // we create some test data
        // ------------------------

        // setup
        val desktopsToCreate = 5
        val foldersPerDesktop = { index: Int -> index + 1 }
        val subFolderPerFolder = { indexDesktop: Int, indexFolder: Int -> (indexDesktop + 1) * 3 }

        // insert data
        for (i in 0 until desktopsToCreate) {

            val desktop = DBDesktopItem(
                label = "Desktop ${i + 1}",
                customBackgroundColor = Color.RED,
                hasCustomBackgroundColor = false
            )
            val desktopId = db.desktopDao().insert(desktop).first()

            val folders = foldersPerDesktop(i)
            for (i2 in 0 until folders) {
                val folder = DBFolderItem(
                    label = "Folder ${i2 + 1}",
                    customColor = Color.GRAY,
                    hasCustomColor = false,
                    customTag = "",
                    hasCustomTag = false,
                    fkDesktopId = desktopId,
                    fkFolderId = null,
                    imageUri = ""
                )
                val folderId = db.folderDao().insert(folder).first()

                val subFolders = subFolderPerFolder(i, i2)
                for (i3 in 0 until subFolders) {
                    val subFolder = DBFolderItem(
                        label = "SubFolder ${i3 + 1}",
                        customColor = Color.GRAY,
                        hasCustomColor = false,
                        customTag = "",
                        hasCustomTag = false,
                        fkDesktopId = null,
                        fkFolderId = folderId,
                        imageUri = ""
                    )
                    db.folderDao().insert(subFolder)
                }
            }
        }
    }

    fun get() = db
}