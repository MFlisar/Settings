package com.michaelflisar.settings.core.items.setups

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class TextSetup (
        val allowEmptyText: Boolean = DEFAULT_SHOW_EMPTY_TEXT,
        val selectText: Boolean = DEFAULT_SELECT_TEXT
): Parcelable {

    companion object {
        var DEFAULT_SHOW_EMPTY_TEXT: Boolean = true
        var DEFAULT_SELECT_TEXT: Boolean = true
    }
}