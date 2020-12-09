package com.michaelflisar.settings.view

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.TimeInterpolator
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import android.view.ViewPropertyAnimator
import androidx.annotation.Dimension
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import com.google.android.material.animation.AnimationUtils

// Code: https://github.com/material-components/material-components-android/blob/master/lib/java/com/google/android/material/behavior/HideBottomViewOnScrollBehavior.java
class AdvancedHideBottomViewOnScrollBehaviour<V : View>(
        context: Context, attrs: AttributeSet
) : CoordinatorLayout.Behavior<V>(context, attrs) {

    companion object {
        private const val ENTER_ANIMATION_DURATION = 225
        private const val EXIT_ANIMATION_DURATION = 175

        private const val STATE_SCROLLED_DOWN = 1
        private const val STATE_SCROLLED_UP = 2
    }

    private var callback: ((begin: Boolean, hide: Boolean) -> Unit)? = null
    private var showHideInSmallList = true

    private var height = 0
    private var currentState = STATE_SCROLLED_UP
    private var additionalHiddenOffsetY = 0
    private var currentAnimator: ViewPropertyAnimator? = null

    override fun onLayoutChild(
            parent: CoordinatorLayout, child: V, layoutDirection: Int): Boolean {
        val paramsCompat = child.layoutParams as MarginLayoutParams
        height = child.measuredHeight + paramsCompat.bottomMargin
        return super.onLayoutChild(parent, child, layoutDirection)
    }

    /**
     * Sets an additional offset for the y position used to hide the view.
     *
     * @param child the child view that is hidden by this behavior
     * @param offset the additional offset in pixels that should be added when the view slides away
     */
    fun setAdditionalHiddenOffsetY(child: V, @Dimension offset: Int) {
        additionalHiddenOffsetY = offset
        if (currentState == STATE_SCROLLED_DOWN) {
            child.translationY = height + additionalHiddenOffsetY.toFloat()
        }
    }

    fun setCallback(callback: (begin: Boolean, hide: Boolean) -> Unit) {
        this.callback = callback
    }

    fun setShowHideInSmallList(enabled: Boolean) {
        showHideInSmallList = enabled
    }

    override fun onStartNestedScroll(
            coordinatorLayout: CoordinatorLayout,
            child: V,
            directTargetChild: View,
            target: View,
            nestedScrollAxes: Int,
            type: Int): Boolean {
        return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL
    }

    override fun onNestedScroll(
            coordinatorLayout: CoordinatorLayout,
            child: V,
            target: View,
            dxConsumed: Int,
            dyConsumed: Int,
            dxUnconsumed: Int,
            dyUnconsumed: Int,
            type: Int,
            consumed: IntArray) {
        if (dyConsumed > 0) {
            slideDown(child)
        } else if (dyConsumed < 0) {
            slideUp(child)
        }
        // Extension: also hide child if we are at the top and the list is small and not scrollable
        // same for scroll down
        // => if we handle the scroll we consume the total unconsumed amount
        else if (showHideInSmallList && dyUnconsumed > 0) {
            if (slideDown(child))
                consumed[1] = dyUnconsumed
        } else if (showHideInSmallList && dyUnconsumed < 0) {
            if (slideUp(child))
                consumed[1] = dyUnconsumed
        }
        //Log.d("SCROLL", "$dxConsumed, $dyConsumed, $dxUnconsumed, $dyUnconsumed")
    }

    /**
     * Perform an animation that will slide the child from it's current position to be totally on the
     * screen.
     */
    fun slideUp(child: V): Boolean {
        if (currentState == STATE_SCROLLED_UP) {
            return false
        }
        if (currentAnimator != null) {
            currentAnimator!!.cancel()
            child.clearAnimation()
        }
        currentState = STATE_SCROLLED_UP
        animateChildTo(
                child, false, 0, ENTER_ANIMATION_DURATION.toLong(), AnimationUtils.LINEAR_OUT_SLOW_IN_INTERPOLATOR)
        return true
    }

    /**
     * Perform an animation that will slide the child from it's current position to be totally off the
     * screen.
     */
    fun slideDown(child: V): Boolean {
        if (currentState == STATE_SCROLLED_DOWN) {
            return false
        }
        if (currentAnimator != null) {
            currentAnimator!!.cancel()
            child.clearAnimation()
        }
        currentState = STATE_SCROLLED_DOWN
        animateChildTo(
                child,
                true,
                height + additionalHiddenOffsetY,
                EXIT_ANIMATION_DURATION.toLong(),
                AnimationUtils.FAST_OUT_LINEAR_IN_INTERPOLATOR)
        return true
    }

    private fun animateChildTo(
            child: V, hide: Boolean, targetY: Int, duration: Long, interpolator: TimeInterpolator) {
        currentAnimator = child
                .animate()
                .translationY(targetY.toFloat())
                .setInterpolator(interpolator)
                .setDuration(duration)
                .setListener(
                        object : AnimatorListenerAdapter() {
                            override fun onAnimationStart(animation: Animator?) {
                                super.onAnimationStart(animation)
                                callback?.invoke(true, hide)
                            }

                            override fun onAnimationEnd(animation: Animator) {
                                currentAnimator = null
                                callback?.invoke(false, hide)
                            }
                        })
    }
}