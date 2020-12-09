package com.michaelflisar.settings.demo.advanced.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.michaelflisar.settings.demo.advanced.DBManager
import com.michaelflisar.settings.demo.advanced.data.global.GlobalPreference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Entity(tableName = "folders")
data class DBFolderItem(
        @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "folder_id") val folderId: Long = 0L,
        @ColumnInfo(name = "label") var label: String,

        // db relations
        @ColumnInfo(name = "fk_desktop_id") var fkDesktopId: Long?,
        @ColumnInfo(name = "fk_folder_id") var fkFolderId: Long?,

        // settings data
        @ColumnInfo(name = "custom_color") var customColor: Int,
        @ColumnInfo(name = "has_custom_color") var hasCustomColor: Boolean,

        @ColumnInfo(name = "image_uri") var imageUri: String
) {

    fun persist() {
        CoroutineScope(Dispatchers.IO).launch {
            DBManager.get().folderDao().update(this@DBFolderItem)
        }
    }

    fun calcColor() = if (hasCustomColor) customColor else GlobalPreference.folderColor
}