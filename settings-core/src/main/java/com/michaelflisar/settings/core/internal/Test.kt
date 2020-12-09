package com.michaelflisar.settings.core.internal

internal object Test {


    // testing
    val SHOW_SUB_HEADER_BG_BORDER = true
    val SHOW_SUB_HEADER_BG_BEHIND_COUNTER = false


    enum class ADAPTER {
        FAST_ADAPTER,
        FAST_ADAPTER_WITH_CUSTOM_FILTER
    }

    val ADAPTER_TO_USE = ADAPTER.FAST_ADAPTER_WITH_CUSTOM_FILTER

}