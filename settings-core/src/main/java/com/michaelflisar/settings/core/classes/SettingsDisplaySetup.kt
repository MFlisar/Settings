package com.michaelflisar.settings.core.classes

import android.graphics.Color
import android.os.Parcelable
import android.view.View
import com.michaelflisar.settings.core.R
import com.michaelflisar.settings.core.enums.CountDisplayType
import com.michaelflisar.settings.core.enums.CustomLayoutStyle
import com.michaelflisar.settings.core.enums.HelpStyle
import com.michaelflisar.settings.core.enums.IntentionMode
import com.michaelflisar.settings.core.interfaces.ISettingsFilter
import com.michaelflisar.settings.core.interfaces.ISettingsFeedbackHandler
import com.michaelflisar.settings.core.interfaces.ISettingsItem
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SettingsDisplaySetup(
        // ID
        val showID: Boolean = false,

        // Strings
        val defaultSettingsLabel: Int = R.string.settings_default_setting,
        val noGroupTitle: Int = R.string.settings_no_group_title,
        val noSearchResults: Int = R.string.settings_no_search_results,

        // Numbering
        val showNumbersForItems: Boolean = false,
        val showNumbersForGroups: Boolean = false,
        val showNumbersForViewPager: Boolean = false,
        val subItemsCountDisplayType: CountDisplayType = CountDisplayType.None,
        val countInfoItems: Boolean = false,

        // Expandable
        val expandable: Boolean = true,
        val expandSingleOnly: Boolean = false,
        val expandAllOnFilter: Boolean = true,

        // Intention
        val intentionMode: IntentionMode = IntentionMode.SubGroups,
        val intentInDp: Int = 12,

        // UI
        val customLayout: CustomLayoutStyle = CustomLayoutStyle.LargeInfo,
        val useDarkTheme: Boolean = false,
        val highlightBackgroundColor: Int = Color.YELLOW,
        val highlightForegroundColor: Int = Color.BLACK,
        val noDataFoundIcon: Int? = null,
        val helpStyle: HelpStyle = HelpStyle.Icon(HelpStyle.Mode.Click),

        // Groups
        val showEmptyGroups: Boolean = false,

        // Group Headers
        val style: SettingsStyle = SettingsStyle(),

        // Filter
        val filter: ISettingsFilter = SettingsFilter(true),

        // Feedback
        val feedbackHandler: ISettingsFeedbackHandler = ToastFeedbackHandler()

) : Parcelable {

    fun showCantChangeSettingInfo(item: ISettingsItem<*, *, *>, view: View) {
        if (item.item.clickable && item.item.editable) {
            feedbackHandler.showCantChangeSettingInfo(item, view)
        }
    }
}