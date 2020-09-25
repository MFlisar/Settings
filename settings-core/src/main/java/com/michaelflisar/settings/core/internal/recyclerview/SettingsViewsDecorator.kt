package com.michaelflisar.settings.core.internal.recyclerview

import android.graphics.Canvas
import android.graphics.Rect
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.google.android.material.card.MaterialCardView
import com.google.android.material.shape.CornerFamily
import com.michaelflisar.settings.core.settings.base.BaseSettingsGroup
import com.michaelflisar.settings.core.R
import com.michaelflisar.settings.core.SettingsUtils
import com.michaelflisar.settings.core.internal.Test
import com.michaelflisar.settings.core.classes.SettingsStyle
import com.michaelflisar.settings.core.enums.IntentionMode
import com.michaelflisar.settings.core.interfaces.ISettingsItem
import com.michaelflisar.settings.core.items.SettingsItemGroup
import com.mikepenz.fastadapter.FastAdapter

internal class SettingsViewsDecorator(
        private val settingsStyle: SettingsStyle
) : ItemDecoration() {

    private val padding = SettingsUtils.dpToPx(settingsStyle.cornerRadiusInDp)

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val childCount = parent.childCount
        Log.d("Decorator", "childCount = $childCount")
        for (i in 0 until childCount) {

            // ----------------------
            // a) calculate variables
            // ----------------------

            val childAbove = if (i > 0) parent.getChildAt(i - 1) else null
            val child = parent.getChildAt(i)
            val id = parent.getChildItemId(child)
//            val pos = parent.getChildAdapterPosition(child)
//            val viewType = getViewType(parent, pos)
            // TODO: probably necessary because of a bug in FastAdapter????
            val item = getSettingsItem(parent, id) ?: continue
            val isGroup = item is SettingsItemGroup //viewType == R.id.settings_item_header
            val parentSetting = item.parentSetting
            val hasParent = parentSetting != null
            val index = if (hasParent) parentSetting!!.subBaseItems.indexOf(item) else -1 // item.index ist ABSOLUTER Index ohne Filterung!
            val level = item.getLevel()
            val setup = item.setup
            val isExpanded = item.isExpanded
            val currentIsHeader = isGroup
//            val currentIsFirstGroupItem = !isGroup && index == 0
//            val currentIsLastGroupItem = hasParent && index == parentSetting!!.subItems.size - 1
            val prevItem = getSettingsItemAt(parent, i - 1)
            val prevIsGroup = prevItem?.item is BaseSettingsGroup<*>
            val prevLevel = prevItem?.getLevel()
            val prevItemOfSameGroup = if (hasParent && index > 0) parentSetting!!.subBaseItems[index - 1] else null
            val prevItemOfSameGroupIsGroup = prevItemOfSameGroup?.item is BaseSettingsGroup<*>
            val prevItemOfSameGroupIsExpandedSubGroup = prevItemOfSameGroupIsGroup && prevItemOfSameGroup?.isExpanded == true
            val nextItem = getSettingsItemAt(parent, i + 1)
            val nextIsGroup = nextItem?.item is BaseSettingsGroup<*>
//            val prevIsSubGroup = prevItem?.item is BaseSettingsGroup
//            val nextIsSubGroup = nextItem?.item is BaseSettingsGroup
            val nextLevel = nextItem?.getLevel()
//            val prevIsExpandedSubGroup = prevIsSubGroup && prevItem!!.isExpanded

            val isFirstAndIsHeader = currentIsHeader && level == 0
            val isNextATopGroup = nextIsGroup && nextLevel == 0
            val isNextTopLevel = nextLevel == 0
            val isTopLevelAndIsItem = !currentIsHeader && level == 0
            val isLastItem = i == childCount - 1
            val intention = calcIntentionInDp(item, level)
            val prevIntention = prevItem?.let { calcIntentionInDp(item, prevLevel!!) }
            val nextIntention = nextItem?.let { calcIntentionInDp(item, nextLevel!!) }

            // ----------------------
            // b) calculate corners
            // ----------------------

            val radiusInDp = 12
            val radius: Float = SettingsUtils.dpToPx(radiusInDp).toFloat()
            val cardStyle = CardStyle(radius)

            // 1) check if item has both corners at top
            // 1.1) Headers at top levels always have round corners at top
            // 1.2) Top level items may have round corners as well bases on flag
            if ((currentIsHeader && level == 0) ||
                    (Test.SHOW_SINGLE_CARDS_FOR_NON_GROUPED_TOP_LEVEL_ITEMS && isTopLevelAndIsItem)) {
                cardStyle.setTopCorners()
            }

            // 2) check if item has both corners at bottom
            // 2.1) Headers at top levels have round corners if they are collapsed or do not have children
            // 2.2) Items before a new top level header have round corners
            // 2.3) Items inside a group that are before a top level item (last visible item of top group) have round corners
            // 2.4) Top level items may have round corners as well bases on flag
            // 2.5) Item inside a group that is the very last item has round corners
            if ((currentIsHeader && isTopLevelAndIsItem && (!isExpanded || item.subItems.size == 0)) ||
                    isNextATopGroup ||
                    isNextTopLevel ||
                    (Test.SHOW_SINGLE_CARDS_FOR_NON_GROUPED_TOP_LEVEL_ITEMS && isTopLevelAndIsItem) ||
                    (!isTopLevelAndIsItem && isLastItem)) {
                cardStyle.setBottomCorners()
            }

            // 3) check if item has a coners at top/bottom because of intention
            if (prevIntention != null && prevIntention > intention) {
                cardStyle.setTopLeftCorner()
            }
            if (nextIntention != null && nextIntention > intention) {
                cardStyle.setBottomLeftCorner()
            }

            // ----------------------
            // c) check style
            // ----------------------

            // Cases:
            // Header => Item
            // Item => Header
            // Header => Header
            // Item => Item

            when (setup.style.topLevelStyle) {
                SettingsStyle.Style.Flat -> {
                    when (setup.style.subLevelStyle) {
                        SettingsStyle.Style.Flat,
                        SettingsStyle.Style.CardsRect -> {
                            cardStyle.setAll(false)
                        }
                        SettingsStyle.Style.CardsRounded -> {
                            if (level == 0) {
                                cardStyle.setAll(false)
                            } else {
                                if (!isGroup && prevIsGroup && level == 1) {
                                    cardStyle.setTopCorners()
                                }
                            }
                        }
                    }
                }
                SettingsStyle.Style.CardsRounded -> {
                    when (setup.style.subLevelStyle) {
                        SettingsStyle.Style.Flat -> {
                            if (level == 0) {
                                cardStyle.setAll(true)
                            } else {
                                cardStyle.setAll(false)
                            }
                        }
                        SettingsStyle.Style.CardsRect -> {
                            if (level == 0) {
                                cardStyle.setAll(true)
                            } else {
                                cardStyle.setAll(false)
                            }
                            if (isGroup && level == 0 && !nextIsGroup && nextLevel == 1) {
                                cardStyle.setBottomCorners(false)
                            }
                        }
                        SettingsStyle.Style.CardsRounded -> {
                            // nothing to do
                        }
                    }
                }
                SettingsStyle.Style.CardsRect -> {
                    when (setup.style.subLevelStyle) {
                        SettingsStyle.Style.Flat,
                        SettingsStyle.Style.CardsRect -> {
                            cardStyle.setAll(false)
                        }
                        SettingsStyle.Style.CardsRounded -> {
                            if (level == 0) {
                                cardStyle.setAll(false)
                            }
                            if (!isGroup && level == 1 && prevIsGroup && prevLevel == 0) {
                                cardStyle.setTopCorners(false)
                            }
                        }
                    }
                }
            }

            // ----------------------
            // d) calculate corners
            // ----------------------

            //Log.d("Decorator", "Index = $i | corners: ($cornerTopLeft, $cornerTopRight, $cornerBottomRight, $cornerBottomLeft)")

            val cardView = child as MaterialCardView
            cardStyle.apply(cardView)

            if (Test.APPLY_CARD_STYLE_IN_DECORATOR) {
                applyStyle(settingsStyle, cardView, level == 0)
            }
        }
    }


    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {

        val index = parent.getChildAdapterPosition(view)
        if (index == RecyclerView.NO_POSITION) {
            return
        }

        val item = (parent.adapter as FastAdapter<*>).getItem(index) as ISettingsItem<*, *, *>
        val prevItem = index.takeIf { it > 0 }?.let { (parent.adapter as FastAdapter<*>).getItem(it - 1) as ISettingsItem<*, *, *> }

        val childCount = (parent.adapter as FastAdapter<*>).itemCount
        val isLastIndex = index == childCount - 1
        val type = getViewType(parent, index)
        val typePrev = if (index == 0) null else getViewType(parent, index - 1)
//        val typeNext = if (index + 1 == childCount) null else getViewType(parent, index + 1)
        val level = item.getLevel()
//        val setup = item.setup
        val isGroup = type == R.id.settings_item_group
        val prevIsGroup = typePrev == R.id.settings_item_group
        val prevLevel = prevItem?.getLevel()

        val isTopLevel = level == 0
        val isFirstItem = index == 0
        val isFirstItemAndIsNotAGroup = !isGroup && index == 0
        val isItemUnderneathAGroup = !isGroup && prevIsGroup
        val isItemUnderneathNotTopLevelItem = !isGroup && prevLevel != null && prevLevel > 0
        val isGroupAndNotFirstItem = index > 0 && isGroup

        // calc paddings
        var hasTopPadding = isTopLevel && (isFirstItem /*isFirstItemAndIsNotAGroup*/ || isItemUnderneathAGroup || isItemUnderneathNotTopLevelItem || isGroupAndNotFirstItem)
        if (Test.SHOW_SINGLE_CARDS_FOR_NON_GROUPED_TOP_LEVEL_ITEMS) {
            hasTopPadding = hasTopPadding || (isTopLevel && !isGroup)
        }
        val hasBottomPadding = isLastIndex

        val paddingLeft = SettingsUtils.dpToPx(calcIntentionInDp(item, level))

        // set paddings
        outRect.top = if (hasTopPadding) padding else 0
        outRect.bottom = if (hasBottomPadding) padding else 0
        outRect.left = paddingLeft
        outRect.right = 0
    }

// -----------------
// helper functions
// -----------------

    companion object {

        fun applyStyle(settingsStyle: SettingsStyle, cardView: MaterialCardView, isTopLevel: Boolean) {

            val style = if (isTopLevel) settingsStyle.topLevelStyle else settingsStyle.subLevelStyle
            val dp1 = SettingsUtils.dpToPx(1)

            // Padding vs Margin:
            // we change this so that both styles do look the same but only their touch feedback will look different depending on the style
            val elevation = if (style == SettingsStyle.Style.Flat) 0f else (dp1 * settingsStyle.elevationInDp).toFloat()
            val useMargin = style == SettingsStyle.Style.CardsRounded
            val margin = if (useMargin) (dp1 * 16) else 0
            val paddingHorizontal = if (useMargin) 0 else (dp1 * 16)
            cardView.cardElevation = elevation
            cardView.setContentPadding(paddingHorizontal, 0, paddingHorizontal, 0)
            (cardView.layoutParams as? ViewGroup.MarginLayoutParams)?.apply {
                leftMargin = margin
                rightMargin = margin
            }
        }
    }

    private fun getViewType(parent: RecyclerView, viewPosition: Int): Int {
        return parent.adapter!!.getItemViewType(viewPosition)
    }

    private fun getSettingsItem(parent: RecyclerView, id: Long): ISettingsItem<*, *, *>? {
        return (parent.adapter as FastAdapter<*>).getItemById(id)?.first as ISettingsItem<*, *, *>?
    }

    private fun getSettingsItemAt(parent: RecyclerView, pos: Int): ISettingsItem<*, *, *>? {
        if (pos < 0 || pos >= parent.childCount) {
            return null
        }
        val child = parent.getChildAt(pos)
        val id = parent.getChildItemId(child)
        return getSettingsItem(parent, id)
    }

    private fun calcIntentionInDp(item: ISettingsItem<*, *, *>, level: Int): Int {
        return when (item.setup.intentionMode) {
            IntentionMode.None -> 0
            IntentionMode.AllSubItems -> item.setup.intentInDp * level
            IntentionMode.SubGroups -> if (level == 0) 0 else (item.setup.intentInDp * (level - 1))
        }
    }

    private class CardStyle(
            private val radius: Float,
            private var cornerTopLeft: Float = 0f,
            private var cornerTopRight: Float = 0f,
            private var cornerBottomRight: Float = 0f,
            private var cornerBottomLeft: Float = 0f
    ) {

        fun setAll(enabled: Boolean = true) {
            (if (enabled) radius else 0f).let {
                cornerTopLeft = it
                cornerTopRight = it
                cornerBottomRight = it
                cornerBottomLeft = it
            }
        }

        fun setTopLeftCorner(enabled: Boolean = true) {
            (if (enabled) radius else 0f).let {
                cornerTopLeft = it
            }
        }

        fun setBottomLeftCorner(enabled: Boolean = true) {
            (if (enabled) radius else 0f).let {
                cornerBottomLeft = it
            }
        }

        fun setTopCorners(enabled: Boolean = true) {
            (if (enabled) radius else 0f).let {
                cornerTopLeft = it
                cornerTopRight = it
            }
        }

        fun setBottomCorners(enabled: Boolean = true) {
            (if (enabled) radius else 0f).let {
                cornerBottomLeft = it
                cornerBottomRight = it
            }
        }

        fun apply(cardView: MaterialCardView) {
            cardView.shapeAppearanceModel = cardView.shapeAppearanceModel
                    .toBuilder()
                    .setTopLeftCorner(CornerFamily.ROUNDED, cornerTopLeft)
                    .setTopRightCorner(CornerFamily.ROUNDED, cornerTopRight)
                    .setBottomRightCorner(CornerFamily.ROUNDED, cornerBottomRight)
                    .setBottomLeftCorner(CornerFamily.ROUNDED, cornerBottomLeft)
                    .build()
        }
    }
}