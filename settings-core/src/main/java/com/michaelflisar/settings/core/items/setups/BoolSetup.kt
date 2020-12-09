package com.michaelflisar.settings.core.items.setups

import android.os.Parcelable
import com.michaelflisar.settings.core.enums.BooleanStyle
import kotlinx.parcelize.Parcelize

@Parcelize
class BoolSetup(
        val style: BooleanStyle = DEFAULT_BOOLEAN_STYLE,
        val changeStateOnClick: Boolean = DEFAULT_BOOLEAN_CHANGE_STATE_ON_ITEM_CLICK
): Parcelable {

    companion object {
        var DEFAULT_BOOLEAN_STYLE: BooleanStyle = BooleanStyle.Switch
        var DEFAULT_BOOLEAN_CHANGE_STATE_ON_ITEM_CLICK = false
    }
}