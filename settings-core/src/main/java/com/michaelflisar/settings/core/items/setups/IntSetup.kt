package com.michaelflisar.settings.core.items.setups

import android.os.Parcelable
import com.michaelflisar.text.Text
import kotlinx.android.parcel.Parcelize

sealed class IntSetup : Parcelable {

    abstract val min: Int?
    abstract val max: Int?

    @Parcelize
    class Input(
            override val min: Int? = null,
            override val max: Int? = null,
            val errorMessage: Text
    ) : IntSetup()

    @Parcelize
    class Picker(
            override val min: Int? = null,
            override val max: Int? = null,
            val step: Int = 1
    ) : IntSetup()
}