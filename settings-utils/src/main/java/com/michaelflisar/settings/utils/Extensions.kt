package com.michaelflisar.settings.utils

import com.michaelflisar.settings.utils.interfaces.ICustomSetting
import com.michaelflisar.settings.utils.interfaces.IGlobalSetting
import com.michaelflisar.settings.core.enums.SupportType

fun SupportType.Companion.calc(item: Any): SupportType {
    return calc(item is ICustomSetting<*, *>, item is IGlobalSetting<*, *>)
}