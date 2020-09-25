package com.michaelflisar.settings.core.interfaces

interface ISettingsModule {
    fun init()
    val supportedTypes: List<Class<out ISetting<*>>>
}