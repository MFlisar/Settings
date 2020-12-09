package com.michaelflisar.settings.core.enums

enum class SupportType {
    All,
    CustomOnly,
    GlobalOnly;

    companion object {
        fun calc(custom: Boolean, global: Boolean): SupportType {
            return if (custom && global) {
                All
            } else if (custom) {
                CustomOnly
            } else if (global) {
                GlobalOnly
            } else throw RuntimeException("A setting must at least support global or custom values!")
        }
    }
}