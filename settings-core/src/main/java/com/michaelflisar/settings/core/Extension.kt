package com.michaelflisar.settings.core

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.res.ColorStateList
import android.text.Spannable
import android.text.SpannableString
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.widget.ImageViewCompat
import com.michaelflisar.dialogs.base.MaterialDialogFragment
import com.michaelflisar.settings.core.classes.DialogContext
import com.michaelflisar.text.Text


fun Text.get() = this.get(SettingsManager.context)

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
    animate()
            .alpha(if (target == View.VISIBLE) 1f else 0f)
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