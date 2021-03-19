package com.michaelflisar.settings.core.items.base

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import androidx.constraintlayout.widget.Barrier
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.github.zagum.switchicon.SwitchIconView
import com.michaelflisar.settings.core.*
import com.michaelflisar.settings.core.R
import com.michaelflisar.settings.core.classes.SettingsDisplaySetup
import com.michaelflisar.settings.core.classes.SettingsMetaData
import com.michaelflisar.settings.core.classes.SettingsStyle
import com.michaelflisar.settings.core.databinding.SettingsItemBaseBinding
import com.michaelflisar.settings.core.enums.CustomLayoutStyle
import com.michaelflisar.settings.core.enums.HelpStyle
import com.michaelflisar.settings.core.enums.SupportType
import com.michaelflisar.settings.core.interfaces.ISetting
import com.michaelflisar.settings.core.interfaces.ISettingsData
import com.michaelflisar.settings.core.interfaces.ISettingsItem
import com.michaelflisar.settings.core.internal.SettingsPayload
import com.michaelflisar.settings.core.internal.Test
import com.michaelflisar.settings.core.decorator.CardGroupDecorator
import com.michaelflisar.settings.core.settings.base.BaseSetting
import com.michaelflisar.settings.core.settings.base.BaseSettingsGroup
import com.mikepenz.fastadapter.*
import com.mikepenz.fastadapter.binding.AbstractBindingItem
import com.mikepenz.fastadapter.binding.BindingViewHolder
import com.mikepenz.fastadapter.listeners.ClickEventHook
import com.mikepenz.fastadapter.listeners.CustomEventHook


abstract class BaseBaseSettingsItem<ValueType, SubViewBinding : ViewBinding, Setting : ISetting<ValueType>>(
        override val setup: SettingsDisplaySetup
) : AbstractBindingItem<SettingsItemBaseBinding>(), IClickable<ISettingsItem<ValueType, BindingViewHolder<SettingsItemBaseBinding>, Setting>>, ISettingsItem<ValueType, BindingViewHolder<SettingsItemBaseBinding>, Setting> {

    abstract override val type: Int
    abstract val noStartIconMode: NoStartIconMode
    abstract val endIconType: EndIcon
    abstract override var parentSetting: ISettingsItem<*, *, *>?
    abstract override var index: Int
    abstract override val item: Setting
    abstract override val itemData: SettingsMetaData
    abstract val settingsData: ISettingsData
    abstract protected val supportsBottomBinding: Boolean

    enum class NoStartIconMode {
        Gone,
        Invisible
    }

    enum class EndIcon(
            val icon: Int,
            val rotation: Float
    ) {
        None(-1, 0f),
        More(R.drawable.ic_keyboard_arrow_down_24dp, 270f),
        Popup(R.drawable.ic_baseline_arrow_drop_down_24, 0f)
    }

    private var filter: String = ""

    override var identifier: Long
        get() = item.id
        set(_) {}

    final override fun createBinding(inflater: LayoutInflater, parent: ViewGroup?): SettingsItemBaseBinding {

        val start = Test.measureTimeStart()

        // BaseBinding
        val baseBinding = SettingsItemBaseBinding.inflate(inflater, parent, false)
        baseBinding.cardView.setUsesDarkTheme(setup.useDarkTheme)

        // Top Binding
        val subBindingTop = createSubBinding(inflater, parent, true)
        baseBinding.root.setTag(R.id.tag_settings_subview_top, subBindingTop)
        subBindingTop.root.id = R.id.vState
        replaceView(baseBinding.vState, subBindingTop)
        updateBarrierBottom(baseBinding.barrierBottom, subBindingTop)

        // Bottom Binding
        if (supportsBottomBinding) {
            val subBindingBottom = createSubBinding(inflater, parent, false)
            baseBinding.root.setTag(R.id.tag_settings_subview_bottom, subBindingBottom)
            baseBinding.root.id = R.id.vStateBottom
            replaceView(baseBinding.vStateBottom, subBindingBottom)
        } else {
            // we can already hide all bottom views in this case
            updateBottomViewsVisibility(baseBinding, null, View.GONE)
        }

        onCreateBindingFinished(baseBinding)

        Test.measureTimeStop(start, "BaseBaseSettingsItem.createBinding")

        return baseBinding
    }

    open fun onCreateBindingFinished(baseBinding: SettingsItemBaseBinding) {

    }

    final override fun bindView(binding: SettingsItemBaseBinding, payloads: List<Any>) {

        val start = Test.measureTimeStart()
        val containsUnknownPayload = payloads.count { it !is SettingsPayload } > 0

        if (payloads.isEmpty() || payloads.contains(SettingsPayload.DependencyChanged)) {
            var viewIsEnabled = true
            itemData.dependencies.forEach {
                viewIsEnabled = viewIsEnabled && it.isChildEnabled(settingsData)
            }
            binding.cardView.setViewState(viewIsEnabled)
            if (!containsUnknownPayload && payloads.size == 1) {
                return
            }
        }
        val onlyValueChanged = !containsUnknownPayload && (payloads.contains(SettingsPayload.IsCustomEnabledChanged) || payloads.contains(SettingsPayload.ValueChanged))

        val itemSupportsBottomBinding = supportsBottomBinding && item.supportType != SupportType.CustomOnly
        val isGlobal = settingsData.isGlobal
        val isBottomBindingVisible = itemSupportsBottomBinding && !isGlobal && setup.customLayout != CustomLayoutStyle.Compact
        val isCheckableIcon = itemSupportsBottomBinding && !isGlobal

        // -----------
        // 1 - Base Binding
        // -----------

        if (!onlyValueChanged) {
            // 1.1 - prepare base binding data
            val text = item.getDisplayLabel(setup)
            val subText = item.info?.get(SettingsManager.context)
            val subTextVisibility = if (subText?.isNotEmpty() == true) View.VISIBLE else View.GONE
            val startIconWidth = if (item.icon != null) SettingsUtils.dimen(binding.root.context, R.dimen.settings_left_icon_width) else (if (noStartIconMode == NoStartIconMode.Gone) 0 else SettingsUtils.dimen(binding.root.context, R.dimen.settings_left_icon_width))
            val paddingForViewsRightOfIcon = if (item.icon != null || isBottomBindingVisible) SettingsUtils.dimen(binding.root.context, R.dimen.settings_icon_margin_right) else (if (noStartIconMode == NoStartIconMode.Gone) 0 else SettingsUtils.dimen(binding.root.context, R.dimen.settings_icon_margin_right))
            val supportsEndIcon = (item as? BaseSetting<*, *, *>)?.editable ?: true

            // 1.2 - update base binding views
            binding.ivIcon.layoutParams.width = startIconWidth
            binding.sivIcon.layoutParams.width = startIconWidth
            setMarginLeft(binding.tvTitle, paddingForViewsRightOfIcon)
            setMarginLeft(binding.tvSubTitle, paddingForViewsRightOfIcon)
            setMarginLeft(binding.tvBottomTitle, paddingForViewsRightOfIcon)
            loadIcon(binding)
            loadSwitchIcon(binding)
            binding.tvTitle.text = setup.filter.highlight(true, text, filter, setup)
            binding.tvSubTitle.text = subText?.let { setup.filter.highlight(false, it, filter, setup) }
            binding.tvSubTitle.visibility = subTextVisibility
            applyEndIcon(binding.ivEndIcon, endIconType, supportsEndIcon)

            binding.root.isClickable = item.clickable
            binding.ivHelp.visibility = if (item.help != null && setup.helpStyle is HelpStyle.Icon) View.VISIBLE else View.GONE
            (item.help != null && setup.helpStyle.mode == HelpStyle.Mode.Click).let {
                binding.ivHelp.isClickable = it
                binding.ivHelp.setSelectableBackground(true, it)
            }

            updateIconAndSwitchVisibility(binding, isCheckableIcon, item.icon != null)
        }

        // -----------
        // 2 - Top Binding
        // -----------

        val subBindingTop = binding.root.getTag(R.id.tag_settings_subview_top) as SubViewBinding

        // 2.1 - prepare top binding data

        // 2.2 - update top binding views

        // -----------
        // 3 - Bottom Binding
        // -----------

        val subBindingBottom = if (supportsBottomBinding) binding.root.getTag(R.id.tag_settings_subview_bottom) as SubViewBinding else null

        if (!onlyValueChanged) {
            if (isBottomBindingVisible) {

                // 3.1 - prepare bottom binding data
                val defaultSettingsLabel = binding.root.context.getString(setup.defaultSettingsLabel)

                // 3.2 - update bottom binding views
                updateBottomViewsVisibility(binding, subBindingBottom, View.VISIBLE)
                binding.tvBottomTitle.text = defaultSettingsLabel

            } else {
                updateBottomViewsVisibility(binding, subBindingBottom, View.GONE)
            }
        }
        onBindViewsFinished(binding, subBindingTop, subBindingBottom, payloads)

        // -----------
        // 4 - Sub Bindings
        // -----------

        bindSubViewTop(subBindingTop, payloads)
        if (isBottomBindingVisible) {
            bindSubViewBottom(subBindingBottom!!, payloads)
        }

        // -----------
        // 5 - update enabled/disabled states
        // -----------

        //binding.tvBottomTitle.isEnabled = globalValueInBottomViewIsEditable
//        subBindingBottom?.root?.let { enableViewsRecursively(it, globalValueInBottomViewIsEditable) }

        Test.measureTimeStop(start, "BaseBaseSettingsItem.bindView")
    }

    override fun unbindView(binding: SettingsItemBaseBinding) {

    }

    open fun onBindViewsFinished(binding: SettingsItemBaseBinding, subBindingTop: SubViewBinding, subBindingBottom: SubViewBinding?, payloads: List<Any>) {

    }

    abstract fun createSubBinding(inflater: LayoutInflater, parent: ViewGroup?, topBinding: Boolean): SubViewBinding
    abstract fun bindSubViewTop(subBindingTop: SubViewBinding, payloads: List<Any>)
    abstract fun bindSubViewBottom(subBindingBottom: SubViewBinding, payloads: List<Any>)

    override fun addSubItems(items: List<ISettingsItem<*, *, *>>) {
        _subItems.addAll(items)
        _unfilteredSubItems.addAll(items)
    }

    // ----------
    // functions
    // ----------

    protected fun applyStyle(baseBinding: SettingsItemBaseBinding, isGroup: Boolean, isTopLevel: Boolean) {
        if (CardGroupDecorator.APPLY_CARD_STYLE_IN_DECORATOR) {
            return
        }
        CardGroupDecorator.applyStyle(setup.style.topLevelStyle, setup.style.subLevelStyle, setup.style.elevationInDp, baseBinding.cardView, baseBinding.llRoot, isTopLevel)
    }

    protected fun applyTinting(baseBinding: SettingsItemBaseBinding, style: SettingsStyle.BaseStyle, customForegroundColor: Int? = null, textViews: List<TextView?> = emptyList(), lighteningFactorPerLevel: Float? = null) {

        val tintBgColor = style.color.getColor(baseBinding.root.context)
        val tintFgColor = style.textColor.getColor(baseBinding.root.context)

        val level = getLevel()
        val bgColor = lighteningFactorPerLevel.takeIf { it != 0f }?.let { SettingsUtils.lightenColor(tintBgColor, it * level) }
                ?: tintBgColor
        val fgColor = customForegroundColor ?: tintFgColor

        baseBinding.cardView.setCardBackgroundColor(ColorStateList.valueOf(bgColor))

        baseBinding.tvTitle.setTextColor(fgColor)
        baseBinding.tvSubTitle.setTextColor(fgColor)
        textViews.forEach {
            it?.setTextColor(fgColor)
        }

        baseBinding.ivEndIcon.setColorFilter(fgColor)
        baseBinding.ivHelp.setColorFilter(fgColor)
        baseBinding.ivIcon.tint(fgColor, style.tintIconWithTextColor)
    }

    private fun applyEndIcon(imageView: ImageView, icon: EndIcon, endIconSupported: Boolean) {
        if (endIconSupported && icon.icon != -1) {
            imageView.setImageResource(icon.icon)
            imageView.rotation = icon.rotation
            imageView.visibility = View.VISIBLE
        } else {
            imageView.visibility = View.GONE
        }
    }

    private fun replaceView(viewToReplace: View, bindingToReplaceWith: SubViewBinding) {
        val lp = viewToReplace.layoutParams
        val parent = viewToReplace.parent as ViewGroup
        parent.removeView(viewToReplace)
        parent.addView(bindingToReplaceWith.root, lp)
    }

    private fun updateBarrierBottom(barrierBottom: Barrier, subBindingTop: SubViewBinding) {
        val referenceIds = barrierBottom.referencedIds.toMutableList()
        referenceIds.add(subBindingTop.root.id)
        barrierBottom.referencedIds = referenceIds.toIntArray()
        barrierBottom.invalidate()
    }

    private fun setMarginLeft(view: View, px: Int) {
        val lp = view.layoutParams as ViewGroup.MarginLayoutParams
        lp.leftMargin = px
//        view.layoutParams = lp
        view.requestLayout()
    }

    override fun getLevel(): Int = parentSetting?.getLevel()?.plus(1) ?: 0

    override fun validForFilter(text: String, checkParents: Boolean, checkSubItems: Boolean): Boolean {

        var valid = setup.filter.isValid(text, item)

        // if any parent fits the filter we keep this item as well
        if (!valid && checkParents && parentSetting != null)
            valid = parentSetting!!.validForFilter(text, true, false)

        // if any child fits the filter we keep this item as well
        if (!valid && checkSubItems) {
            unfilteredSubItems.forEach {
                if ((it as BaseBaseSettingsItem<*, *, *>).validForFilter(text, false, true)) {
                    valid = true
                }
            }
        }

        //Log.d("TEST", "Item: ${item.label.get(SettingsManager.context)} | filter: $text | checkSubItems: $checkSubItems | subItems: ${subItems.size} | valid: $valid")

        return valid
    }

    override fun filterSubItems(text: String) {
        filter = text
        if (text.isEmpty()) {
            unfilteredSubItems.forEach {
                (it as BaseBaseSettingsItem<*, *, *>).apply {
                    filterSubItems(text)
                }
            }
            _subItems.setNewList(unfilteredSubItems)
        } else {
            val filtered = unfilteredSubItems.filter {
                (it as BaseBaseSettingsItem<*, *, *>).validForFilter(text, true, true)
            }.apply {
                forEach {
                    (it as BaseBaseSettingsItem<*, *, *>).apply {
                        filterSubItems(text)
                    }
                }
            }
            _subItems.setNewList(filtered)
        }
    }

    fun setViewTag(view: View, topBinding: Boolean) {
        view.setTag(R.id.tag_settings_view, if (topBinding) R.id.tag_settings_subview_top else R.id.tag_settings_subview_bottom)
    }

    private fun updateBottomViewsVisibility(binding: SettingsItemBaseBinding, subBindingBottom: SubViewBinding?, visibility: Int) {
        //binding.barrierBottom.visibility = visibility
        binding.vBottomSeparator.visibility = visibility
        binding.tvBottomTitle.visibility = visibility
        if (subBindingBottom == null) {
            binding.vStateBottom.visibility = visibility
        } else {
            subBindingBottom.root.visibility = visibility
        }
    }

    protected open fun loadIcon(binding: SettingsItemBaseBinding) {
        item.icon?.display(binding.ivIcon) ?: binding.ivIcon.setImageDrawable(null)
    }

    private fun loadSwitchIcon(binding: SettingsItemBaseBinding) {
        item.icon?.display(binding.sivIcon) ?: binding.sivIcon.setImageDrawable(null)
    }

    private fun updateIconAndSwitchVisibility(binding: SettingsItemBaseBinding, isCheckableIcon: Boolean, hasIcon: Boolean) {
        binding.ivIcon.visibility = if (isCheckableIcon) View.INVISIBLE else View.VISIBLE
        binding.swIcon.visibility = if (isCheckableIcon && !hasIcon) View.VISIBLE else View.INVISIBLE
        binding.sivIcon.visibility = if (isCheckableIcon && hasIcon) View.VISIBLE else View.INVISIBLE
    }

    protected fun move23GuidelineForItemsWithoutData(binding: SettingsItemBaseBinding) {
        // text should stretch to the right/end as we do not a have a view for the not existing data!
        binding.guideline23.apply {
            val params = layoutParams as ConstraintLayout.LayoutParams
            params.guidePercent = 1f
            layoutParams = params
        }
    }

    // ----------
    // IExpandable
    // ----------

    private val _unfilteredSubItems = ArrayList<ISubItem<*>>()
    private val _subItems = MutableSubItemList<ISubItem<*>>(this)

    final override var isExpanded: Boolean = !setup.expandable

    final override var parent: IParentItem<*>?
        get() = parentSetting
        set(value) {
            parentSetting = value as BaseBaseSettingsItem<*, *, *>?
        }

    final override var subItems: MutableList<ISubItem<*>>
        set(value) {
            _subItems.setNewList(value)
            _unfilteredSubItems.clear()
            _unfilteredSubItems.addAll(value)
        }
        get() = _subItems

    val unfilteredSubItems: List<ISubItem<*>>
        get() = _unfilteredSubItems

    val isFiltered: Boolean
        get() = filter.isNotEmpty()

    final override val isAutoExpanding: Boolean
        get() = setup.expandable

    final override var isSelectable: Boolean
        get() = subItems.isEmpty()
        set(_) {}

    @Suppress("UNCHECKED_CAST")
    override val subBaseItems
        get() = subItems as MutableSubItemList<ISettingsItem<*, *, *>>

    override fun getAllSubItems(filtered: Boolean): List<ISettingsItem<*, *, *>> {
        val items: ArrayList<ISettingsItem<*, *, *>> = ArrayList()
        val subItems = if (filtered) subBaseItems else (unfilteredSubItems as List<ISettingsItem<*, *, *>>)
        for (item in subItems) {
            if (item.item is BaseSettingsGroup<*>) {
                items.add(item)
                items.addAll(item.getAllSubItems(filtered))
            } else {
                items.add(item)
            }
        }
        return items
    }

    // ----------
    // Companion object
    // ----------

    companion object {
        private fun getViewTag(view: View): Int {
            return view.getTag(R.id.tag_settings_view) as Int
        }

        fun hasTopViewTag(view: View) = getViewTag(view) == R.id.tag_settings_subview_top

        internal val EVENT_HOOK_HELP_CLICKED = object : ClickEventHook<ISettingsItem<*, *, *>>() {

            override fun onBind(viewHolder: RecyclerView.ViewHolder): View? {
                val baseBinding = (viewHolder as? BindingViewHolder<*>)?.binding as? SettingsItemBaseBinding
                return baseBinding?.ivHelp
            }

            override fun onClick(v: View, position: Int, fastAdapter: FastAdapter<ISettingsItem<*, *, *>>, item: ISettingsItem<*, *, *>) {
                item.setup.feedbackHandler.showHelp(item, v)
            }
        }

        internal val EVENT_HOOK_IS_CUSTOM_ENABLED = object : CustomEventHook<ISettingsItem<*, *, *>>() {

            override fun onBindMany(viewHolder: RecyclerView.ViewHolder): List<View>? {
                val baseBinding = (viewHolder as? BindingViewHolder<*>)?.binding as? SettingsItemBaseBinding
                return baseBinding?.let { listOf(it.sivIcon, it.swIcon) }
            }

            override fun attachEvent(view: View, viewHolder: RecyclerView.ViewHolder) {
                if (view is SwitchIconView) {
                    view.setOnClickListener {
                        val switchIconView = it as SwitchIconView
                        onCheckChange(
                                viewHolder,
                                { switchIconView.switchState(true) },
                                { switchIconView.isIconEnabled }
                        )
                    }
                } else if (view is Switch) {
                    view.setOnCheckedChangeListener { button, _ ->
                        val switch = button as Switch
                        onCheckChange(
                                viewHolder,
                                null,
                                { switch.isChecked }
                        )
                    }
                }
            }

            private fun onCheckChange(vh: RecyclerView.ViewHolder, switchStateFunction: ((value: Boolean) -> Unit)?, getValueFunction: () -> Boolean) {
                val item = FastAdapter.getHolderAdapterItem<BaseSettingsItem<Boolean, *, BaseSetting<Boolean, *, *>>>(vh)
                item?.let {
                    val settingsData = item.settingsData
                    val data = item.item

                    if (!settingsData.isGlobal) {
                        switchStateFunction?.invoke(true)
                        val newValue = getValueFunction()
                        data.writeIsEnabled(settingsData as ISettingsData.Element, newValue)

                        // we need to update this items UI (enable/disable views, ...)
                        val adapter = FastAdapter.getFromHolderTag<IItem<*>>(vh)
                        adapter?.notifyAdapterItemChanged(vh.adapterPosition, SettingsPayload.IsCustomEnabledChanged)
                    }
                }
            }
        }
    }
}