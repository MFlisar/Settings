package com.michaelflisar.settings.core.interfaces

interface ISetting<T>: IBaseSetting  {

    val parent: IBaseSetting

    fun read(): T
    fun save(value: T)
}