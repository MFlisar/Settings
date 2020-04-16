package com.michaelflisar.settings.core.classes

import android.os.Parcelable
import android.widget.TextView
import com.michaelflisar.settings.core.SettingsManager
import kotlinx.android.parcel.Parcelize

@Parcelize
class SettingsText private constructor(
        val resText: Int = -1,
        val textString: String? = null
) : Parcelable {

    constructor(resText: Int) : this(resText, null)

    constructor(text: String) : this(-1, text)

    val text: String?
        get() = if (resText != -1) {
            SettingsManager.context.getString(resText)
        } else {
            textString
        }

    fun display(tv: TextView) {
        if (resText != -1) {
            tv.setText(resText)
        } else {
            tv.text = textString
        }
    }
}
