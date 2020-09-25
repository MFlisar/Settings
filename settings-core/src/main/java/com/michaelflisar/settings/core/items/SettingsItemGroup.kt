package com.michaelflisar.settings.core.items

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import com.michaelflisar.settings.core.*
import com.michaelflisar.settings.core.classes.*
import com.michaelflisar.settings.core.databinding.SettingsItemBaseBinding
import com.michaelflisar.settings.core.databinding.SettingsItemTextBinding
import com.michaelflisar.settings.core.enums.CountDisplayType
import com.michaelflisar.settings.core.interfaces.ISettingsItem
import com.michaelflisar.settings.core.internal.Test
import com.michaelflisar.settings.core.items.base.BaseBaseSettingsItem
import com.michaelflisar.settings.core.settings.base.BaseSettingsGroup
import com.mikepenz.fastadapter.ClickListener
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.IAdapter
import com.mikepenz.fastadapter.binding.BindingViewHolder

internal class SettingsItemGroup(
        override var parentSetting: ISettingsItem<*, *, *>?,
        override var index: Int,
        override var item: BaseSettingsGroup<*>,
        override var itemData: SettingsMetaData,
        override var settingsCustomItem: SettingsCustomObject,
        setup: SettingsDisplaySetup
) : BaseBaseSettingsItem<Unit, SettingsItemTextBinding, BaseSettingsGroup<*>>(setup) {

    companion object {
        val ROT_CLOSED = -90f
        val ROT_OPENED = 0f
    }

    override val type = R.id.settings_item_group
    override val noStartIconMode = NoStartIconMode.Gone
    override val endIconType = EndIcon.None
    override val supportsBottomBinding = false

    private var mOnClickListener: ClickListener<ISettingsItem<Unit, BindingViewHolder<SettingsItemBaseBinding>, BaseSettingsGroup<*>>>? = null

    @Suppress("SetterBackingFieldAssignment")
    override var onItemClickListener: ClickListener<ISettingsItem<Unit, BindingViewHolder<SettingsItemBaseBinding>, BaseSettingsGroup<*>>>? = { v: View?, adapter: IAdapter<ISettingsItem<Unit, BindingViewHolder<SettingsItemBaseBinding>, BaseSettingsGroup<*>>>, item: ISettingsItem<Unit, BindingViewHolder<SettingsItemBaseBinding>, BaseSettingsGroup<*>>, position: Int ->
        if (item.subItems.isNotEmpty()) {
            v?.findViewById<View>(R.id.ivExpandIcon)?.let {
                if (!item.isExpanded) {
                    ViewCompat.animate(it).rotation(ROT_CLOSED).start()
                } else {
                    ViewCompat.animate(it).rotation(ROT_OPENED).start()
                }
            }
        }
        mOnClickListener?.invoke(v, adapter, item, position) ?: true
    }
        set(onClickListener) {
            this.mOnClickListener = onClickListener // on purpose
        }

    override var onPreItemClickListener: ClickListener<ISettingsItem<Unit, BindingViewHolder<SettingsItemBaseBinding>, BaseSettingsGroup<*>>>?
        get() = null
        set(_) {}

    override fun onBindViewsFinished(binding: SettingsItemBaseBinding, subBindingTop: SettingsItemTextBinding, subBindingBottom: SettingsItemTextBinding?, payloads: List<Any>) {

        applyStyle(binding, true, getLevel() == 0)

        // text should stretch to the right/end as we do not a have a view for the not existing data!
        //move23GuidelineForItemsWithoutData(binding)

        val level = getLevel()
        val tint = level == 0 || setup.style.group.subGroupStyle is SettingsStyle.SubGroupColorMode.Full
        val tintBgColor = setup.style.group.color.getColor(binding.root.context)
        val tintFgColor = setup.style.group.textColor.getColor(binding.root.context)
        val defaultTextColor = SettingsUtils.attrColor(binding.root.context, android.R.attr.textColorSecondary)
        val fgColor = if (tint) tintFgColor else defaultTextColor
        val tintFactor = (setup.style.group.subGroupStyle as? SettingsStyle.SubGroupColorMode.Full)?.lighteningFactorPerLevel
        applyTinting(binding, setup.style.group, fgColor, listOf(subBindingTop.tvDisplayValue, subBindingBottom?.tvDisplayValue), tintFactor)

        if (setup.style.group.subGroupStyle == SettingsStyle.SubGroupColorMode.Border) {

//            if (level > 0) {
//                val fgColor2 = SettingsUtils.attrColor(binding.root.context, R.attr.colorPrimary)
//                binding.tvTitle.setTextColor(fgColor2)
//                binding.tvSubTitle.setTextColor(fgColor2)
//                binding.ivEndIcon.setColorFilter(fgColor2)
//                subBindingTop.tvDisplayValue.setTextColor(fgColor2)
//
//                binding.tvTitle.paintFlags = binding.tvTitle.getPaintFlags() or Paint.UNDERLINE_TEXT_FLAG
//                binding.tvSubTitle.setTextColor(fgColor2)
//                binding.ivIcon.setColorFilter(fgColor2)
//            } else {
//                binding.ivIcon.setColorFilter(null)
//            }

            if (Test.SHOW_SUB_HEADER_BG_BEHIND_COUNTER) {
                binding.vGroupImageBackground.visibility = if (level == 0) View.GONE else View.VISIBLE
                binding.vGroupImageBackground.setCardBackgroundColor(tintBgColor)
                if (level > 0) {
                    subBindingTop.tvDisplayValue.setTextColor(tintFgColor)
                    binding.ivEndIcon.setColorFilter(tintFgColor)
                }
            }

            if (Test.SHOW_SUB_HEADER_BG_BORDER) {
                binding.cardView.strokeColor = if (level == 0) Color.TRANSPARENT else tintBgColor
                binding.cardView.strokeWidth = if (level == 0) 0 else SettingsUtils.dpToPx(4)
            }
        }

        binding.ivExpandIcon.visibility = View.VISIBLE
        binding.ivExpandIcon.tint(fgColor, true)
        if (isExpanded) {
            binding.ivExpandIcon.rotation = ROT_OPENED
        } else {
            binding.ivExpandIcon.rotation = ROT_CLOSED
        }
    }

    override fun loadIcon(binding: SettingsItemBaseBinding) {
        if (item.iconOpened == null || !isExpanded)
            item.icon?.display(binding.ivIcon) ?: binding.ivIcon.setImageDrawable(null)
        else
            item.iconOpened?.display(binding.ivIcon) ?: binding.ivIcon.setImageDrawable(null)
    }

    override fun bindSubViews(subBindingTop: SettingsItemTextBinding, subBindingBottom: SettingsItemTextBinding?, payloads: List<Any>) {
        // Top View

        val count = when (setup.subItemsCountDisplayType) {
            is CountDisplayType.None -> {
                null
            }
            is CountDisplayType.DirectChildren -> {
                val filteredCount = subBaseItems.filter { setup.subItemsCountDisplayType.isValid(it) }.size
                val count = unfilteredSubItems.filter { setup.subItemsCountDisplayType.isValid(it as ISettingsItem<*, *, *>) }.size
                Pair(filteredCount, count)
            }
            is CountDisplayType.AllChildren -> {
                val filteredCount = getAllSubItems(true).filter { setup.subItemsCountDisplayType.isValid(it) }.size
                val count = getAllSubItems(false).filter { setup.subItemsCountDisplayType.isValid(it) }.size
                Pair(filteredCount, count)
            }
            /*
            CountDisplayType.DirectChildren -> {
                Pair(subItems.size, unfilteredSubItems.size)
            }
            CountDisplayType.AllChildrenWithGroups -> {
                Pair(getAllSubItems(true).size, getAllSubItems(true, false).size)
            }
            CountDisplayType.AllChildrenWithoutGroups -> {
                Pair(getAllSubItems(false).size, getAllSubItems(false, false).size)
            }*/
        }
        if (count != null) {
            if (isFiltered) {
                subBindingTop.tvDisplayValue.text = "(${count.first}/${count.second})"
            } else {
                subBindingTop.tvDisplayValue.text = "(${count.first})"
            }
        }
    }

    override fun createSubBinding(inflater: LayoutInflater, parent: ViewGroup?, topBinding: Boolean): SettingsItemTextBinding {
        val binding = SettingsItemTextBinding.inflate(inflater, parent, false)
        // set tag for event hook
        setViewTag(binding.tvDisplayValue, topBinding)
        return binding
    }

    override fun onClickEvent(view: View, dialogContext: DialogContext, fastAdapter: FastAdapter<*>, pos: Int): Boolean {
        return false
    }
}