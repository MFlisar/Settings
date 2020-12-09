package com.michaelflisar.settings.core.decorator

import android.graphics.Canvas
import android.graphics.Rect
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.google.android.material.card.MaterialCardView
import com.google.android.material.shape.CornerFamily
import com.michaelflisar.settings.core.SettingsUtils
import com.mikepenz.fastadapter.FastAdapter

class CardGroupDecorator(
        private val cornerRadiusInDp: Int,
        private val topLevelStyle: Style,
        private val subLevelStyle: Style,
        private val groupTopLevelItemsWithoutGroups: Boolean,
        private val elevationInDp: Int,
        private val intentionMode: IntentionMode,
        private val intentInDp: Int,
        private val groupItemViewType: Int
) : ItemDecoration() {

    private val padding = SettingsUtils.dpToPx(cornerRadiusInDp)

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
            // following check is probably necessary because of a bug / sepcial behaviour in FastAdapter...
            val item = getSettingsItem(parent, id) ?: continue
            val isGroup = item.isCardGroupHeader()
            val parentSetting = item.getCardGroupParentItem()
            val hasParent = parentSetting != null
            val index = if (hasParent) parentSetting!!.getSubCardGroupItems().indexOf(item) else -1 // item.index ist ABSOLUTER Index ohne Filterung!
            val level = item.getLevel()
            //val setup = item.setup
            val isExpanded = item.isExpanded
            val currentIsHeader = isGroup
//            val currentIsFirstGroupItem = !isGroup && index == 0
//            val currentIsLastGroupItem = hasParent && index == parentSetting!!.subItems.size - 1
            val prevItem = getSettingsItemAt(parent, i - 1)
            val prevIsGroup = prevItem?.isCardGroupHeader() ?: false
            val prevLevel = prevItem?.getLevel()
            val prevItemOfSameGroup = if (hasParent && index > 0) parentSetting!!.getSubCardGroupItems()[index - 1] else null
            val prevItemOfSameGroupIsGroup = prevItemOfSameGroup?.isCardGroupHeader() ?: false
            val prevItemOfSameGroupIsExpandedSubGroup = prevItemOfSameGroupIsGroup && prevItemOfSameGroup?.isExpanded == true
            val nextItem = getSettingsItemAt(parent, i + 1)
            val nextIsGroup = nextItem?.isCardGroupHeader() ?: false
//            val prevIsSubGroup = prevItem?.item is BaseSettingsGroup
//            val nextIsSubGroup = nextItem?.item is BaseSettingsGroup
            val nextLevel = nextItem?.getLevel()
//            val prevIsExpandedSubGroup = prevIsSubGroup && prevItem!!.isExpanded

            val isFirstAndIsHeader = currentIsHeader && level == 0
            val isNextATopGroup = nextIsGroup && nextLevel == 0
            val isNextTopLevel = nextLevel == 0
            val isTopLevelAndIsItem = !currentIsHeader && level == 0
            val isLastItem = i == childCount - 1
            val intention = calcIntentionInDp(intentionMode, intentInDp, level)
            val prevIntention = prevItem?.let { calcIntentionInDp(intentionMode, intentInDp, prevLevel!!) }
            val nextIntention = nextItem?.let { calcIntentionInDp(intentionMode, intentInDp, nextLevel!!) }

            // ----------------------
            // b) calculate corners
            // ----------------------

            val radius: Float = SettingsUtils.dpToPx(cornerRadiusInDp).toFloat()
            val cardStyle = CardStyle(radius)

            // 1) check if item has both corners at top
            // 1.1) Headers at top levels always have round corners at top
            // 1.2) Top level items may have round corners as well bases on flag
            if ((currentIsHeader && level == 0) ||
                    (!groupTopLevelItemsWithoutGroups && isTopLevelAndIsItem)) {
                cardStyle.setTopCorners()
            }

            // 2) check if item has both corners at bottom
            // 2.1) Headers at top levels have round corners if they are collapsed or do not have children
            // 2.2) Items before a new top level header have round corners
            // 2.3) Items inside a group that are before a top level item (last visible item of top group) have round corners
            // 2.4) Top level items may have round corners as well bases on flag
            // 2.5) Item inside a group that is the very last item has round corners
            if ((currentIsHeader && isTopLevelAndIsItem && (!isExpanded || item.getSubCardGroupItems().isEmpty())) ||
                    isNextATopGroup ||
                    isNextTopLevel ||
                    (!groupTopLevelItemsWithoutGroups && isTopLevelAndIsItem) ||
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

            when (topLevelStyle) {
                Style.Flat -> {
                    when (subLevelStyle) {
                        Style.Flat,
                        Style.CardsRect -> {
                            cardStyle.setAll(false)
                        }
                        Style.CardsRounded -> {
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
                Style.CardsRounded -> {
                    when (subLevelStyle) {
                        Style.Flat -> {
                            if (level == 0) {
                                cardStyle.setAll(true)
                            } else {
                                cardStyle.setAll(false)
                            }
                        }
                        Style.CardsRect -> {
                            if (level == 0) {
                                cardStyle.setAll(true)
                            } else {
                                cardStyle.setAll(false)
                            }
                            if (isGroup && level == 0 && !nextIsGroup && nextLevel == 1) {
                                cardStyle.setBottomCorners(false)
                            }
                        }
                        Style.CardsRounded -> {
                            // nothing to do
                        }
                    }
                }
                Style.CardsRect -> {
                    when (subLevelStyle) {
                        Style.Flat,
                        Style.CardsRect -> {
                            cardStyle.setAll(false)
                        }
                        Style.CardsRounded -> {
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
            // d) set corners
            // ----------------------

//            Log.d("Decorator", "Index = $i | cardStyle: $cardStyle")

            val cardView = child as MaterialCardView
            cardStyle.apply(cardView)

            if (APPLY_CARD_STYLE_IN_DECORATOR) {
                val rootView = cardView.getChildAt(0)
                applyStyle(topLevelStyle, subLevelStyle, elevationInDp, cardView, rootView, level == 0)
            }
        }
    }


    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {

        val index = parent.getChildAdapterPosition(view)
        if (index == RecyclerView.NO_POSITION) {
            return
        }

        // ----------------------
        // a) calculate variables
        // ----------------------

        val item = (parent.adapter as FastAdapter<*>).getItem(index) as? ICardGroupItem ?: return
        val prevItem = index.takeIf { it > 0 }?.let { (parent.adapter as FastAdapter<*>).getItem(it - 1) as? ICardGroupItem }
        val childCount = (parent.adapter as FastAdapter<*>).itemCount
        val isLastIndex = index == childCount - 1
        val type = getViewType(parent, index)
        val typePrev = if (index == 0) null else getViewType(parent, index - 1)
//        val typeNext = if (index + 1 == childCount) null else getViewType(parent, index + 1)
        val level = item.getLevel()
//        val setup = item.setup
        val isGroup = type == groupItemViewType
        val prevIsGroup = typePrev == groupItemViewType
        val prevLevel = prevItem?.getLevel()
        val isTopLevel = level == 0
        val isFirstItem = index == 0
        val isFirstItemAndIsNotAGroup = !isGroup && index == 0
        val isItemUnderneathAGroup = !isGroup && prevIsGroup
        val isItemUnderneathNotTopLevelItem = !isGroup && prevLevel != null && prevLevel > 0
        val isGroupAndNotFirstItem = index > 0 && isGroup

        // ----------------------
        // b) calculate paddings
        // ----------------------

        var hasTopPadding = isTopLevel && (isFirstItem /*isFirstItemAndIsNotAGroup*/ || isItemUnderneathAGroup || isItemUnderneathNotTopLevelItem || isGroupAndNotFirstItem)
        if (!groupTopLevelItemsWithoutGroups) {
            hasTopPadding = hasTopPadding || (isTopLevel && !isGroup)
        }
        val hasBottomPadding = isLastIndex
        val paddingLeft = SettingsUtils.dpToPx(calcIntentionInDp(intentionMode, intentInDp, level))

        // ----------------------
        // c) check style
        // ----------------------

        // TODO: handle all cases..
        if (topLevelStyle == Style.Flat && subLevelStyle == Style.Flat) {
            hasTopPadding = false
        }

        // ----------------------
        // d) set paddings
        // ----------------------

        outRect.top = if (hasTopPadding) padding else 0
        outRect.bottom = if (hasBottomPadding) padding else 0
        outRect.left = paddingLeft
        outRect.right = 0
    }

    // -----------------
    // helper functions
    // -----------------

    companion object {

        val APPLY_CARD_STYLE_IN_DECORATOR = false // leads to flicker because this is happening too late

        fun applyStyle(topLevelStyle: Style, subLevelStyle: Style, elevationInDp: Int, cardView: MaterialCardView, contentView: View, isTopLevel: Boolean) {

            val style = if (isTopLevel) topLevelStyle else subLevelStyle
            val dp1 = SettingsUtils.dpToPx(1)

            // Padding vs Margin:
            // we change this so that both styles do look the same but only their touch feedback will look different depending on the style
            val elevation = if (style == Style.Flat) 0f else (dp1 * elevationInDp).toFloat()
            val useMargin = style == Style.CardsRounded
            val margin = if (useMargin) (dp1 * 16) else 0
            val paddingHorizontal = if (useMargin) (dp1 * 8) else (dp1 * 16)
            cardView.cardElevation = elevation
            contentView.setPadding(paddingHorizontal, dp1 * 8, paddingHorizontal, dp1 * 8)
//            cardView.setContentPadding(paddingHorizontal, 0, paddingHorizontal, 0)
            (cardView.layoutParams as? ViewGroup.MarginLayoutParams)?.apply {
                leftMargin = margin
                rightMargin = margin
            }
        }
    }

    private fun getViewType(parent: RecyclerView, viewPosition: Int): Int {
        return parent.adapter!!.getItemViewType(viewPosition)
    }

    private fun getSettingsItem(parent: RecyclerView, id: Long): ICardGroupItem? {
        return (parent.adapter as FastAdapter<*>).getItemById(id)?.first as? ICardGroupItem
    }

    private fun getSettingsItemAt(parent: RecyclerView, pos: Int): ICardGroupItem? {
        if (pos < 0 || pos >= parent.childCount) {
            return null
        }
        val child = parent.getChildAt(pos)
        val id = parent.getChildItemId(child)
        return getSettingsItem(parent, id)
    }

    private fun calcIntentionInDp(intentionMode: IntentionMode, intentInDp: Int, level: Int): Int {
        return when (intentionMode) {
            IntentionMode.None -> 0
            IntentionMode.AllSubItems -> intentInDp * level
            IntentionMode.SubGroups -> if (level == 0) 0 else (intentInDp * (level - 1))
        }
    }

    private data class CardStyle(
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