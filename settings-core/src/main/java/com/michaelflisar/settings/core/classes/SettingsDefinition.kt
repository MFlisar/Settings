package com.michaelflisar.settings.core.classes

import com.michaelflisar.settings.core.interfaces.ISetting
import com.michaelflisar.settings.core.settings.InfoSetting
import com.michaelflisar.settings.core.settings.SettingsGroup

@Suppress("NAME_SHADOWING")
class SettingsDefinition(
        val settings: List<ISetting<*>>,
        val dependencies: List<SettingsDependency<*>>,
        updateNumbering: Boolean = true,
        includeInfoSettingsInNumbering: Boolean = false
) {
    init {
        if (updateNumbering) {
            settings.forEachIndexed { index, item ->
                updateNumbering(item, index, 0, emptyList(), includeInfoSettingsInNumbering)
            }
        }
    }

    private fun updateNumbering(setting: ISetting<*>, index: Int, indexAdjustment: Int, parentNumbering: List<Int>, includeInfoSettingsInNumbering: Boolean): Int {
        val indexAdjustment = calcNumbering(setting, index, indexAdjustment, parentNumbering, includeInfoSettingsInNumbering)
        if (setting is SettingsGroup) {
            var nestedIndexAdjustment = 0
            setting.getItems().forEachIndexed { index, item ->
                nestedIndexAdjustment = updateNumbering(item, index, nestedIndexAdjustment, setting.numbering, includeInfoSettingsInNumbering)
            }
        }
        return indexAdjustment
    }

    private fun calcNumbering(setting: ISetting<*>, index: Int, indexAdjustment: Int, parentNumbering: List<Int>, includeInfoSettingsInNumbering: Boolean): Int {
        if (includeInfoSettingsInNumbering || setting !is InfoSetting) {
            setting.numbering = parentNumbering.plus(index + indexAdjustment)
            return indexAdjustment
        } else {
            return indexAdjustment - 1
        }
    }
}