package com.michaelflisar.settings.core

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.res.ColorStateList
import android.text.Spannable
import android.text.SpannableString
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.ImageViewCompat
import androidx.fragment.app.Fragment
import com.michaelflisar.dialogs.base.MaterialDialogFragment
import com.michaelflisar.settings.core.classes.DialogContext
import com.michaelflisar.settings.core.classes.SettingsColor
import com.michaelflisar.settings.core.classes.SettingsIcon
import com.michaelflisar.text.Text


fun Text.get() = this.get(SettingsManager.context)

fun Int.asSettingsIcon() = SettingsIcon(this)

fun Int.asSettingsColor() = SettingsColor.Color(this)
fun Int.asSettingsColorRes() = SettingsColor.ColorRes(this)

internal fun View.enableRecursively(enabled: Boolean) {
    isEnabled = enabled
    if (this is ViewGroup) {
        for (i in 0 until childCount) {
            getChildAt(i).enableRecursively(enabled)
        }
    }
}

internal fun View.animateVisibility(target: Int) {
    animate().cancel()
    val targetValue = if (target == View.VISIBLE) 1f else 0f
    if (alpha == targetValue) {
        visibility = target
        return
    }
    animate()
            .alpha(alpha)
            .setListener(object: AnimatorListenerAdapter() {
                override fun onAnimationEnd(p0: Animator?) {
                    visibility = target
                }
            })
            .start()
}

internal fun ImageView.tint(color: Int, tintingEnabled: Boolean = true) {
    val csl = if (tintingEnabled) ColorStateList.valueOf(color) else null
    ImageViewCompat.setImageTintList(this, csl)
}

internal fun View.setSelectableBackground(borderless: Boolean, enabled: Boolean) {
    if (enabled) {
        with(TypedValue()) {
            context.theme.resolveAttribute(if (borderless) R.attr.selectableItemBackgroundBorderless else R.attr.selectableItemBackground, this, true)
            setBackgroundResource(resourceId)
        }
    } else {
        background = null
    }
}

fun MaterialDialogFragment<*>.show(context: DialogContext) {
    context.show(this)
}

val Fragment.parentDialogContext: DialogContext
        get() = DialogContext.getParentContext(this)

internal fun Context.getActivity(): AppCompatActivity? {
    if (this is ContextWrapper) {
        return if (this is AppCompatActivity) {
            this
        } else {
            baseContext.getActivity()
        }
    }
    return null
}