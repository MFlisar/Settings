package com.michaelflisar.settings.demo.advanced.data.db

import androidx.room.Embedded
import androidx.room.Relation
import com.michaelflisar.settings.demo.advanced.data.db.entities.DBDesktopItem
import com.michaelflisar.settings.demo.advanced.data.db.entities.DBFolderItem

data class DesktopWithFolders(
        @Embedded var desktop: DBDesktopItem,
        @Relation(
                parentColumn = "desktop_id",
                entityColumn = "fk_desktop_id"
        )
        var folders: List<DBFolderItem>
)