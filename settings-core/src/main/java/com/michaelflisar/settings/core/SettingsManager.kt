package com.michaelflisar.settings.core

import android.content.Context

object SettingsManager {

    lateinit var context: Context
        private set

    fun init(context: Context) {
        this.context = context.applicationContext
    }
}