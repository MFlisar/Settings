package com.michaelflisar.settings.core.classes

import android.os.Parcelable
import com.michaelflisar.settings.core.interfaces.ISetting
import com.michaelflisar.settings.core.interfaces.ISettingsData
import com.michaelflisar.settings.core.interfaces.ISettingsListItem
import kotlinx.parcelize.Parcelize

@Parcelize
class SettingsDependency<ValueType>(
        val childThatDependsOnParent: ISetting<*>,
        private val parent: ISetting<ValueType>,
        private val validator: Validator<ValueType>
) : Parcelable {

    fun checkParent(parent: ISetting<*>) = this.parent == parent
    fun isChildEnabled(settingsData: ISettingsData) = validator.isChildEnabled(parent, settingsData)

    interface Validator<DataType> : Parcelable {
        fun isChildEnabled(parent: ISetting<DataType>, settingsData: ISettingsData): Boolean
    }

    // ------------------------------
    // simple default implementations
    // ------------------------------

    @Parcelize
    class BooleanValidator(
            private val validIfDependencyIsTrue: Boolean = true
    ) : Validator<Boolean> {
        override fun isChildEnabled(parent: ISetting<Boolean>, settingsData: ISettingsData): Boolean {
            val value = parent.readRealValue(settingsData)
            return if (validIfDependencyIsTrue) value else !value
        }
    }

    @Parcelize
    class ListValidator(
            private val validIds: List<Long>
    ) : Validator<ISettingsListItem> {
        override fun isChildEnabled(parent: ISetting<ISettingsListItem>, settingsData: ISettingsData): Boolean {
            val value = parent.readRealValue(settingsData)
            return validIds.contains(value.id)
        }
    }
}