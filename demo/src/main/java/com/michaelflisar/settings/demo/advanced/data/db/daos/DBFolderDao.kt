package com.michaelflisar.settings.demo.advanced.data.db.daos

import androidx.room.*
import com.michaelflisar.settings.demo.advanced.data.db.DesktopWithFolders
import com.michaelflisar.settings.demo.advanced.data.db.FolderWithFolders
import com.michaelflisar.settings.demo.advanced.data.db.entities.DBFolderItem

@Dao
interface DBFolderDao {

    @Query("SELECT * FROM folders")
    fun getAll(): List<DBFolderItem>

    @Insert
    fun insert(vararg folders: DBFolderItem): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun update(vararg folders: DBFolderItem)

    @Delete
    fun delete(vararg folders: DBFolderItem)

    @Transaction
    @Query("SELECT * FROM folders where folder_id = :id")
    fun getWithData(id: Long): FolderWithFolders
}
