package com.michaelflisar.settings.demo.advanced.data.db.daos

import androidx.room.*
import com.michaelflisar.settings.demo.advanced.data.db.DesktopWithFolders
import com.michaelflisar.settings.demo.advanced.data.db.entities.DBDesktopItem

@Dao
interface DBDesktopDao {

    @Query("SELECT * FROM desktops")
    fun getAll(): List<DBDesktopItem>

    @Insert
    fun insert(vararg desktops: DBDesktopItem): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun update(vararg desktops: DBDesktopItem)

    @Delete
    fun delete(vararg desktops: DBDesktopItem)

    @Transaction
    @Query("SELECT * FROM desktops where desktop_id = :id")
    fun getWithData(id: Long): DesktopWithFolders

}
