package com.michaelflisar.settings.core.interfaces

interface ISettingsGroup : IBaseSetting  {

    val items: ArrayList<IBaseSetting>

    fun add(vararg item: IBaseSetting) {
        items.addAll(item)
    }
}