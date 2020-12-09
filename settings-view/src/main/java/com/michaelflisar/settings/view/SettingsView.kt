package com.michaelflisar.settings.view

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Color
import android.os.Parcelable
import android.util.AttributeSet
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IntDef
import androidx.cardview.widget.CardView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.graphics.ColorUtils
import androidx.core.widget.doOnTextChanged
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.transition.Transition
import androidx.transition.TransitionManager
import com.google.android.material.color.MaterialColors
import com.google.android.material.transition.MaterialContainerTransform
import com.google.android.material.transition.MaterialFade
import com.michaelflisar.settings.core.Settings
import com.michaelflisar.settings.core.SettingsUtils
import com.michaelflisar.settings.core.classes.SettingsState
import com.michaelflisar.settings.view.databinding.SettingsViewBinding
import kotlinx.parcelize.Parcelize

class SettingsView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = R.attr.coordinatorLayoutStyle
) : CoordinatorLayout(context, attrs, defStyleAttr) {

    companion object {

        @IntDef(TYPE_LIST, TYPE_PAGER)
        @Retention(AnnotationRetention.SOURCE)
        annotation class LayoutStyle

        const val TYPE_LIST = 0
        const val TYPE_PAGER = 1
    }

    val binding: SettingsViewBinding
    private val searchEnabled: Boolean
    private var hideSearchOnScroll: Boolean

    private val showHideSearchInSmallList = true

    private val switchToListOnSearch = true

    private val cvFilterBehaviour: Behavior<View>?
    private val fabFilterBehaviour: Behavior<View>?

    @LayoutStyle
    private var originalLayoutStyle: Int

    @LayoutStyle
    private var layoutStyle: Int

    private var viewContext: Settings.ViewContext? = null
    private var settings: Settings? = null

    init {

        // 1) inflate view + adjust view styles
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val v = inflater.inflate(R.layout.settings_view, this)
        binding = SettingsViewBinding.bind(v)
        binding.tilFilter.editText?.apply {
            highlightColor = ColorUtils.setAlphaComponent(textColors?.defaultColor
                    ?: SettingsUtils.attrColor(context, R.attr.colorOnPrimary), 100)
        }

        // 2) read custom settings
        val typedArray = if (attrs != null) context.obtainStyledAttributes(attrs, R.styleable.SettingsView) else null
        searchEnabled = readAttrBool(typedArray, R.styleable.SettingsView_searchEnabled)
        hideSearchOnScroll = readAttrBool(typedArray, R.styleable.SettingsView_hideSearchOnScroll)
        layoutStyle = readAttrInt(typedArray, R.styleable.SettingsView_layoutStyle)
        originalLayoutStyle = layoutStyle
        typedArray?.recycle()

        // 3) apply custom settings
        cvFilterBehaviour = (binding.cvFilter.layoutParams as LayoutParams).behavior
        fabFilterBehaviour = (binding.fabFilter.layoutParams as LayoutParams).behavior
        if (!hideSearchOnScroll) {
            (binding.cvFilter.layoutParams as LayoutParams).behavior = null
            (binding.fabFilter.layoutParams as LayoutParams).behavior = null
        }
        initSearchBar()

        // 4) apply style
        requestStyleChanged(layoutStyle, true, true)

        binding.tilFilter.editText?.doOnTextChanged { text, _, _, _ ->
            // forward filter string to setting objects
            val filter = text?.toString() ?: ""
            val oldFilter = settings?.filter ?: ""
            settings?.filter(filter)
            onFilterChanged(filter, oldFilter, true)
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        Log.d("SettingsView", "onDetachedFromWindow")
        unbind(true)
    }

    // ------------------------
    // state saving/restoring
    // ------------------------

    override fun onSaveInstanceState(): Parcelable? {
        val superState = super.onSaveInstanceState()
        val state = settings?.getViewState()
        Log.d("SettingsView", "onSaveInstanceState: $state")
        return SavedState(
                superState,
                state
        )
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state !is SavedState) {
            super.onRestoreInstanceState(state)
            return
        }
        val s = state.state
        Log.d("SettingsView", "onRestoreInstanceState: $s")
        s?.let {
            restoreState(it)
        }
        super.onRestoreInstanceState(state.superState)
    }

    private fun restoreState(state: SettingsState) {
        settings?.let { s ->
            // all pages do restore theirself automatically (SettingsFragment takes care of this!)
            // we only need to restore the sourrounding state
            // => changed to use ViewPager2 => now we must call rebind
//            rebind(state, s)
            if (layoutStyle == TYPE_LIST) {
                state.restore(s, binding.rvSettings)
                s.filter(state.filter)
                onFilterChanged(state.filter, state.filter, false)
            } else {
                state.restore(s, binding.vp)
                s.filter(state.filter)
                onFilterChanged(state.filter, state.filter, false)
            }
        }
    }

    @Parcelize
    internal class SavedState(
            var superState: Parcelable? = null,
            var state: SettingsState? = null
    ) : Parcelable

    // ------------------------
    // functions
    // ------------------------

    fun bind(viewContext: Settings.ViewContext, state: SettingsState, settings: Settings) {
        unbind(true)
        this.viewContext = viewContext
        this.settings = settings
        settings.setup.noDataFoundIcon?.takeIf { it > 0 }?.let {
            binding.vEmpty.ivEmpty.setImageResource(it)
        }
        settings.setup.noSearchResults.takeIf { it > 0 }?.let {
            binding.vEmpty.tvEmpty.setText(it)
        }
        settings.setup.additionalListBottomSpace = getBottomDecoratorSpace()

        if (layoutStyle == TYPE_LIST) {
            settings.bind(viewContext, state, binding.rvSettings, binding.vEmpty.root)
        } else {
            settings.bind(viewContext, state, binding.tabs, binding.vp)
        }
        Log.d("SettingsView", "bind")
    }

    fun unbind(resetViewContext: Boolean) {
        if (settings == null) {
            return
        }
        Log.d("SettingsView", "unbind")
        settings?.unbind()
        settings = null
        if (resetViewContext)
            viewContext = null
    }

    fun updateStyle(@LayoutStyle style: Int) {
        requestStyleChanged(style, true, false)
        settings?.let {
            val state = it.getViewState()!!
            unbind(false)
            bind(viewContext!!, state, it)
        }
    }

    fun updateHideSearchOnScroll(enabled: Boolean) {
        hideSearchOnScroll = enabled
        if (!hideSearchOnScroll) {
            (binding.cvFilter.layoutParams as LayoutParams).behavior = null
            (binding.fabFilter.layoutParams as LayoutParams).behavior = null
        } else {
            (binding.cvFilter.layoutParams as LayoutParams).behavior = cvFilterBehaviour
            (binding.fabFilter.layoutParams as LayoutParams).behavior = fabFilterBehaviour
        }
    }

    fun updateFilter(filter: String) {
        if (!searchEnabled) {
            return
        }
        val oldFilter = settings?.filter ?: ""
        settings?.filter(filter)
        onFilterChanged(filter, oldFilter, false)
    }

    // ------------------------
    // list / search bar functions
    // ------------------------

    private fun getBottomDecoratorSpace(): Int {
        if (!searchEnabled || hideSearchOnScroll) {
            return 0
        } else {
            val paramsCompat = binding.cvFilter.layoutParams as MarginLayoutParams
            val space = SettingsUtils.dimen(context, R.dimen.settings_search_bar_height) + paramsCompat.bottomMargin * 2 /* 2x for extra padding above search bar as well */
            return space
        }
    }

    private fun buildContainerTransformation() =
            MaterialContainerTransform().apply {
                containerColor = MaterialColors.getColor(binding.root, R.attr.colorSecondary)
                scrimColor = Color.TRANSPARENT
                duration = 300
                interpolator = FastOutSlowInInterpolator()
                fadeMode = MaterialContainerTransform.FADE_MODE_IN
            }

    private fun initSearchBar() {
        if (searchEnabled) {

//            if (hideSearchOnScroll) {
                val searchBarBehaviour = fabFilterBehaviour /*(binding.fabFilter.layoutParams as LayoutParams).behavior*/ as AdvancedHideBottomViewOnScrollBehaviour
                searchBarBehaviour.setShowHideInSmallList(showHideSearchInSmallList)
                val searchBarBehaviour2 = cvFilterBehaviour /* (binding.cvFilter.layoutParams as LayoutParams).behavior*/ as AdvancedHideBottomViewOnScrollBehaviour
                searchBarBehaviour2.setShowHideInSmallList(showHideSearchInSmallList)
                searchBarBehaviour.setCallback { begin, hide ->
                    if (!begin && hide) {
                        hideSearchBar(true)
                    }
                }
//            }
            binding.fabFilter.post {
                val transition = MaterialFade().apply {
                    duration = 2000
                }
                TransitionManager.beginDelayedTransition(binding.root as ViewGroup, transition)
                binding.fabFilter.visibility = View.VISIBLE
            }
            binding.fabFilter.setOnClickListener {
                showSearchBar()
            }

            binding.tilFilter.editText?.setOnKeyListener { view, keyCode, event ->
                if (event.action == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    hideSearchBar(false)
                    true
                } else
                    false
            }
        } else {
            val visibilitySearch = View.GONE
            updateViewVisibility(binding.fabFilter, visibilitySearch)
            updateViewVisibility(binding.cvFilter, visibilitySearch)
        }
    }

    private fun hideSearchBar(instantly: Boolean) {
        if (binding.cvFilter.visibility != View.VISIBLE) {
            return
        }
        if (!instantly) {
            val transition = buildContainerTransformation()
            transition.startView = binding.cvFilter
            transition.endView = binding.fabFilter
            transition.addTarget(binding.fabFilter)
            TransitionManager.beginDelayedTransition(binding.root as ViewGroup, transition)
        }
        binding.cvFilter.visibility = View.INVISIBLE
        binding.fabFilter.visibility = View.VISIBLE
    }

    private fun showSearchBar() {
        if (binding.cvFilter.visibility == View.VISIBLE) {
            return
        }
        val transition = buildContainerTransformation()
        transition.startView = binding.fabFilter
        transition.endView = binding.cvFilter
        transition.addTarget(binding.cvFilter)
        transition.addListener(object : Transition.TransitionListener {
            override fun onTransitionStart(transition: Transition) {
            }

            override fun onTransitionEnd(transition: Transition) {
                binding.tilFilter.requestFocus()
            }

            override fun onTransitionCancel(transition: Transition) {
            }

            override fun onTransitionPause(transition: Transition) {
            }

            override fun onTransitionResume(transition: Transition) {
            }
        })
        TransitionManager.beginDelayedTransition(binding.root as ViewGroup, transition)
        binding.cvFilter.visibility = View.VISIBLE
        binding.fabFilter.visibility = View.INVISIBLE
    }

    // ------------------------
    // private helper functions
    // ------------------------

    private fun onFilterChanged(filter: String, oldFilter: String, calledFromEditText: Boolean) {
        if (!searchEnabled) {
            return
        }
        if (oldFilter != filter && !calledFromEditText) {
            binding.tilFilter.editText?.setText(filter)
            if (filter.isNotEmpty()) {
                showSearchBar()
            }
//            binding.tilFilter.isHelperTextEnabled = true
//            binding.tilFilter.helperText = "Showing x/y settings..."
//            binding.tilFilter.hint = "Hint..."
        }

        if (switchToListOnSearch && originalLayoutStyle == TYPE_PAGER) {
            if (oldFilter.isEmpty() && filter.isNotEmpty()) {
                requestStyleChanged(TYPE_LIST, false, false)
                bind(viewContext!!, SettingsState(settings!!.filter, settings!!.getViewState()?.expandedIds?.let { ArrayList(it) }
                        ?: ArrayList(), 0, 0), settings!!)
            } else if (filter.isEmpty() && oldFilter.isNotEmpty()) {
                requestStyleChanged(TYPE_PAGER, false, false)
                bind(viewContext!!, SettingsState(settings!!.filter, settings!!.getViewState()?.expandedIds?.let { ArrayList(it) }
                        ?: ArrayList(), 0, 0), settings!!)
            }
        }
    }

    private fun requestStyleChanged(@LayoutStyle style: Int, updateOriginalStyle: Boolean, init: Boolean) {
        if (!init && layoutStyle == style) {
            return
        }
        layoutStyle = style
        if (updateOriginalStyle) {
            originalLayoutStyle = style
        }

        val visibilityList = if (layoutStyle == TYPE_LIST) View.VISIBLE else View.GONE
        val visibilityViewPager = if (layoutStyle == TYPE_PAGER) View.VISIBLE else View.GONE

        updateViewVisibility(binding.rvSettings, visibilityList)
        updateViewVisibility(binding.vEmpty.tvEmpty, visibilityList)
        updateViewVisibility(binding.vEmpty.ivEmpty, visibilityList)
        updateViewVisibility(binding.tabs, visibilityViewPager)
        updateViewVisibility(binding.vp, visibilityViewPager)
    }

    private fun readAttrInt(typedArray: TypedArray?, id: Int, defaultValue: Int = -1): Int {
        if (typedArray?.hasValue(id) == true) {
            return typedArray.getInt(id, defaultValue)
        }
        return defaultValue
    }

    private fun readAttrBool(typedArray: TypedArray?, id: Int, defaultValue: Boolean = false): Boolean {
        if (typedArray?.hasValue(id) == true) {
            return typedArray.getBoolean(id, defaultValue)
        }
        return defaultValue
    }

    private fun readAttrString(typedArray: TypedArray?, id: Int, defaultValue: String? = null): String? {
        if (typedArray?.hasValue(id) == true) {
            return typedArray.getString(id)
        }
        return defaultValue
    }

    private fun readAttrResId(typedArray: TypedArray?, id: Int, defaultValue: Int = -1): Int {
        if (typedArray?.hasValue(id) == true) {
            return typedArray.getResourceId(id, defaultValue)
        }
        return defaultValue
    }

    private fun updateViewVisibility(view: View, visibility: Int) {
        if (view.visibility != visibility) {
            view.visibility = visibility
        }
    }
}