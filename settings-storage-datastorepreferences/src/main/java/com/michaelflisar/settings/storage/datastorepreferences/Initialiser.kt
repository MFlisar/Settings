package com.michaelflisar.settings.storage.datastorepreferences

import android.content.Context
import androidx.startup.Initializer

internal class Initialiser : Initializer<Unit> {
    override fun create(context: Context) {
        DataStorePrefSettings.context = context
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return emptyList()
    }
}