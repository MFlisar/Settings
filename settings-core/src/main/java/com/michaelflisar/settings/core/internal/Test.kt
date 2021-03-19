package com.michaelflisar.settings.core.internal

import android.util.Log

internal object Test {

    val ENABLE_LOGS = false

    // testing
    val SHOW_SUB_HEADER_BG_BORDER = true
    val SHOW_SUB_HEADER_BG_BEHIND_COUNTER = false


    enum class ADAPTER {
        FAST_ADAPTER,
        FAST_ADAPTER_WITH_CUSTOM_FILTER
    }

    val ADAPTER_TO_USE = ADAPTER.FAST_ADAPTER_WITH_CUSTOM_FILTER

    fun measureTimeStart(): Long = if (ENABLE_LOGS) System.currentTimeMillis() else 0
    fun measureTimeStop(start: Long, tag: String) {
        if (ENABLE_LOGS) {
            val time = System.currentTimeMillis() - start
            Log.d("SETTINGS", "[$tag] Time: $time")
        }
    }

    fun log(tag: String, msg: String) {
        if (ENABLE_LOGS) {
            Log.d("SETTINGS", "[$tag] $msg")
        }
    }
}