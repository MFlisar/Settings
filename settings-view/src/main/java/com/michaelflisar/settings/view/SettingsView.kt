package com.michaelflisar.settings.view

import android.content.Context
import android.content.res.TypedArray
import android.os.Parcelable
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.IntDef
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.graphics.ColorUtils
import androidx.core.widget.doOnTextChanged
import com.michaelflisar.settings.core.Settings
import com.michaelflisar.settings.core.SettingsUtils
import com.michaelflisar.settings.core.classes.SettingsState
import com.michaelflisar.settings.view.databinding.SettingsViewBinding
import kotlinx.android.parcel.Parcelize


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

    private val binding: SettingsViewBinding
    private val searchEnabled: Boolean
    private val hideSearchOnScroll: Boolean

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
        typedArray?.recycle()

        // 3) apply custom settings
        if (!hideSearchOnScroll) {
            (binding.cvFilter.layoutParams as LayoutParams).behavior = null
        }

        // 4) apply style
        requestStyleChanged(layoutStyle, true)

        binding.tilFilter.editText?.doOnTextChanged { text, _, _, _ ->
            // forward filter string to setting objects
            val filter = text?.toString() ?: ""
            settings?.filter(filter)
            onFilterChanged(filter)
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

    fun restoreState(state: SettingsState) {
        settings?.let { s ->
            // all pages do restore theirself automatically (SettingsFragment takes care of this!)
            // we only need to restore the sourrounding state
            // => changed to use ViewPager2 => now we must call rebind
//            rebind(state, s)
            if (layoutStyle == TYPE_LIST) {
                state.restore(s, binding.rvSettings)
                s.filter(state.filter)
                onFilterChanged(state.filter)
            } else {
                state.restore(s, binding.vp)
                s.filter(state.filter)
                onFilterChanged(state.filter)
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
        this.viewContext = viewContext
        this.settings = settings
        settings.setup.noDataFoundIcon?.takeIf { it > 0 }?.let {
            binding.vEmpty.ivEmpty.setImageResource(it)
        }
        settings.setup.noSearchResults.takeIf { it > 0 }?.let {
            binding.vEmpty.tvEmpty.setText(it)
        }

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

    fun rebind(state: SettingsState, settings: Settings) {
        unbind(false)
        bind(viewContext!!, state, settings)
    }

    fun updateStyle(@LayoutStyle style: Int) {
        requestStyleChanged(style)
        settings?.let {
            val state = it.getViewState()!!
            unbind(false)
            bind(viewContext!!, state, it)
        }
    }

    fun updateFilter(filter: String) {
        if (!searchEnabled) {
            return
        }
        settings?.filter(filter)
        onFilterChanged(filter)
    }

    // ------------------------
    // private helper functions
    // ------------------------

    private fun onFilterChanged(filter: String) {
        if (!searchEnabled) {
            return
        }
        val f = filter ?: ""
        if (binding.tilFilter.editText?.text.toString() != f) {
            binding.tilFilter.editText?.setText(f)
        }
    }

    private fun requestStyleChanged(@LayoutStyle style: Int, init: Boolean = false) {
        if (!init && layoutStyle == style) {
            return
        }
        layoutStyle = style

        val visibilitySearch = if (searchEnabled) View.VISIBLE else View.GONE
        val visibilityList = if (layoutStyle == TYPE_LIST) View.VISIBLE else View.GONE
        val visibilityViewPager = if (layoutStyle == TYPE_PAGER) View.VISIBLE else View.GONE

        updateViewVisibility(binding.cvFilter, visibilitySearch)
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