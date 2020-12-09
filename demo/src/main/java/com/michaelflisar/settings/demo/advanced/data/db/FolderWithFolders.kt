package com.michaelflisar.settings.demo.advanced.data.db

import androidx.room.Embedded
import androidx.room.Relation
import com.michaelflisar.settings.demo.advanced.data.db.entities.DBFolderItem

data class FolderWithFolders(
        @Embedded var folder: DBFolderItem,
        @Relation(
                parentColumn = "folder_id",
                entityColumn = "fk_folder_id"
        )
        var subFolders: List<DBFolderItem>
)