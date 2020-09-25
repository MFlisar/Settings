package com.michaelflisar.settings.core

import android.content.Context
import android.content.res.Resources
import android.content.res.TypedArray
import android.graphics.Color
import android.util.TypedValue
import androidx.annotation.ColorInt
import androidx.annotation.Size
import com.michaelflisar.settings.core.settings.SettingsGroup
import com.michaelflisar.settings.core.interfaces.ISetting


object SettingsUtils {
    fun dpToPx(dp: Int): Int {
        return (dp * Resources.getSystem().displayMetrics.density).toInt()
    }

    fun pxToDp(px: Int): Int {
        return (px / Resources.getSystem().displayMetrics.density).toInt()
    }

    fun dimen(context: Context, settingsIconMarginRight: Int): Int {
        return context.resources.getDimension(settingsIconMarginRight).toInt()
    }

    fun attrColor(context: Context, color: Int): Int {
//        val typedValue = TypedValue()
//        val theme: Theme = context.theme
//        theme.resolveAttribute(color, typedValue, true)
//        @ColorInt val color = typedValue.data
//        return color

        val typedValue = TypedValue()
        val a: TypedArray = context.obtainStyledAttributes(typedValue.data, intArrayOf(color))
        val c = a.getColor(0, 0)
        a.recycle()
        return c
    }

    fun getAllIds(settings: List<ISetting<*>>): List<Long> {
        val data = ArrayList<Long>()
        for (setting in settings) {
            data.add(setting.id)
            if (setting is SettingsGroup) {
                data.addAll(getAllIds(setting.getItems()))
            }
        }
        return data
    }

    fun checkDistinctIds(settings: List<ISetting<*>>): Boolean {
        val ids = getAllIds(settings)
        val distinctIds = ids.distinct()
        return ids.size == distinctIds.size
    }

    @ColorInt
    fun lightenColor(@ColorInt color: Int, value: Float): Int {
        val hsl = colorToHSL(color)
        hsl[2] += value
        hsl[2] = Math.max(0f, Math.min(hsl[2], 1f))
        return hslToColor(hsl)
    }

    @Size(3)
    private fun colorToHSL(@ColorInt color: Int,
                           @Size(3) hsl: FloatArray = FloatArray(3)): FloatArray {
        val r = Color.red(color) / 255f
        val g = Color.green(color) / 255f
        val b = Color.blue(color) / 255f

        val max = Math.max(r, Math.max(g, b))
        val min = Math.min(r, Math.min(g, b))
        hsl[2] = (max + min) / 2

        if (max == min) {
            hsl[1] = 0f
            hsl[0] = hsl[1]

        } else {
            val d = max - min

            hsl[1] = if (hsl[2] > 0.5f) d / (2f - max - min) else d / (max + min)
            when (max) {
                r -> hsl[0] = (g - b) / d + (if (g < b) 6 else 0)
                g -> hsl[0] = (b - r) / d + 2
                b -> hsl[0] = (r - g) / d + 4
            }
            hsl[0] /= 6f
        }
        return hsl
    }

    @ColorInt
    private fun hslToColor(@Size(3) hsl: FloatArray): Int {
        val r: Float
        val g: Float
        val b: Float

        val h = hsl[0]
        val s = hsl[1]
        val l = hsl[2]

        if (s == 0f) {
            b = l
            g = b
            r = g
        } else {
            val q = if (l < 0.5f) l * (1 + s) else l + s - l * s
            val p = 2 * l - q
            r = hue2rgb(p, q, h + 1f / 3)
            g = hue2rgb(p, q, h)
            b = hue2rgb(p, q, h - 1f / 3)
        }

        return Color.rgb((r * 255).toInt(), (g * 255).toInt(), (b * 255).toInt())
    }

    private fun hue2rgb(p: Float, q: Float, t: Float): Float {
        var valueT = t
        if (valueT < 0) valueT += 1f
        if (valueT > 1) valueT -= 1f
        if (valueT < 1f / 6) return p + (q - p) * 6f * valueT
        if (valueT < 1f / 2) return q
        return if (valueT < 2f / 3) p + (q - p) * (2f / 3 - valueT) * 6f else p
    }
}