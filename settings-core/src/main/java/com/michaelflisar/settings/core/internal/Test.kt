package com.michaelflisar.settings.core.internal

internal object Test {
    // finished
    val SHOW_SINGLE_CARDS_FOR_NON_GROUPED_TOP_LEVEL_ITEMS = true // looks better
    val APPLY_CARD_STYLE_IN_DECORATOR = false // leads to flicker because this is happening too late

    // testing
    val SHOW_SUB_HEADER_BG_BORDER = true
    val SHOW_SUB_HEADER_BG_BEHIND_COUNTER = false


    enum class ADAPTER {
        FAST_ADAPTER,
        FAST_ADAPTER_WITH_CUSTOM_FILTER
    }

    val ADAPTER_TO_USE = ADAPTER.FAST_ADAPTER_WITH_CUSTOM_FILTER

}