package com.michaelflisar.settings.core.classes

import android.os.Parcelable
import com.michaelflisar.settings.core.settings.base.BaseSetting
import com.michaelflisar.settings.core.interfaces.ISetting
import kotlinx.android.parcel.Parcelize

@Parcelize
class SettingsDependency(
        val childThatDependsOnParent: ISetting<*>,
        private val parent: ISetting<Boolean>
) : Parcelable {

    fun getParent() = parent as BaseSetting<Boolean, *, *>
}