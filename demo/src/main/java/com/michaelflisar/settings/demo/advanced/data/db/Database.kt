package com.michaelflisar.settings.demo.advanced.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.michaelflisar.settings.demo.advanced.data.db.daos.DBDesktopDao
import com.michaelflisar.settings.demo.advanced.data.db.daos.DBFolderDao
import com.michaelflisar.settings.demo.advanced.data.db.entities.DBDesktopItem
import com.michaelflisar.settings.demo.advanced.data.db.entities.DBFolderItem

@Database(entities = arrayOf(DBDesktopItem::class, DBFolderItem::class), version = 1)
abstract class Database : RoomDatabase() {
    abstract fun desktopDao(): DBDesktopDao
    abstract fun folderDao(): DBFolderDao
}
