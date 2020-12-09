package com.michaelflisar.settings.demo.advanced.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.michaelflisar.settings.demo.advanced.DBManager
import com.michaelflisar.settings.demo.advanced.data.global.GlobalPreference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Entity(tableName = "desktops")
data class DBDesktopItem(
        @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "desktop_id")val desktopId: Long = 0L,
        @ColumnInfo(name = "label") var label: String,
        // settings data
        @ColumnInfo(name = "custom_color") var customBackgroundColor: Int,
        @ColumnInfo(name = "has_custom_color") var hasCustomBackgroundColor: Boolean
) {

    fun persist() {
        CoroutineScope(Dispatchers.IO).launch {
            DBManager.get().desktopDao().update(this@DBDesktopItem)
        }
    }

    fun calcColor() = if (hasCustomBackgroundColor) customBackgroundColor else GlobalPreference.desktopBackgroundColor
}