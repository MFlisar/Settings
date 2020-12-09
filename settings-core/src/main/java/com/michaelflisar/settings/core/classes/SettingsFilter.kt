package com.michaelflisar.settings.core.classes

import android.text.Spannable
import android.text.SpannableString
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan
import com.michaelflisar.settings.core.SettingsManager
import com.michaelflisar.settings.core.interfaces.ISetting
import com.michaelflisar.settings.core.interfaces.ISettingsFilter
import kotlinx.parcelize.Parcelize

@Parcelize
open class SettingsFilter(
        private val ignoreCase: Boolean,
        private val searchInLabel: Boolean = true,
        private val searchInInfo: Boolean = true,
        private val searchInNumber: Boolean = true
) : ISettingsFilter {

    override fun isValid(item: ISetting<*>): Boolean = true

    override fun transformFilter(filter: String?): String = filter?.trim() ?: ""

    override fun isValid(filter: String, item: ISetting<*>): Boolean {
        if (filter.isEmpty()) {
            return true
        }
        return (searchInLabel && item.label.get(SettingsManager.context).contains(filter, ignoreCase)) ||
                (searchInInfo && item.info?.get(SettingsManager.context)?.contains(filter, ignoreCase) ?: false) ||
                (searchInNumber && item.getNumberingInfo().contains(filter, ignoreCase))
    }

    override fun highlight(label: Boolean, text: String, filter: String, setup: SettingsDisplaySetup): CharSequence {
        if (filter.isEmpty() || (label && (!searchInLabel && !searchInNumber)) || (!label && !searchInInfo)) {
            return text
        }
        val wordToSpan: Spannable = SpannableString(text)
        var ofe: Int = text.indexOf(filter, 0, ignoreCase)
        var offset = 0
        while (offset < text.length && ofe != -1) {
            ofe = text.indexOf(filter, offset, ignoreCase)
            if (ofe == -1) {
                break
            }
            wordToSpan.setSpan(BackgroundColorSpan(setup.highlightBackgroundColor), ofe, ofe + filter.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            wordToSpan.setSpan(ForegroundColorSpan(setup.highlightForegroundColor), ofe, ofe + filter.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            offset = ofe + filter.length
        }
        return wordToSpan
    }
}