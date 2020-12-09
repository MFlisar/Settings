package com.michaelflisar.settings.core.items.setups

import android.os.Parcelable
import com.michaelflisar.settings.core.SettingsManager
import com.michaelflisar.text.Text
import kotlinx.parcelize.Parcelize

sealed class IntSetup : Parcelable {

    companion object {
        var DEFAULT_SHOW_INFO_IN_DIALOG: Boolean = false
    }

    abstract val min: Int?
    abstract val max: Int?
    abstract val valueFormatRes: Int?
    abstract val showInfoInDialog: Boolean

    @Parcelize
    class Input(
            override val min: Int? = null,
            override val max: Int? = null,
            val errorMessage: Text,
            override val valueFormatRes: Int? = null,
            override val showInfoInDialog: Boolean = DEFAULT_SHOW_INFO_IN_DIALOG
    ) : IntSetup()

    @Parcelize
    class Picker(
            override val min: Int? = null,
            override val max: Int? = null,
            val step: Int = 1,
            override val valueFormatRes: Int? = null,
            override val showInfoInDialog: Boolean = DEFAULT_SHOW_INFO_IN_DIALOG
    ) : IntSetup()

    fun formatValue(value: Int): String {
        return valueFormatRes?.let { SettingsManager.context.getString(it, value) }
                ?: value.toString()
    }
}