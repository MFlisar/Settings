package com.michaelflisar.settings.core.internal.pager

import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import java.lang.reflect.Field
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by Michael on 21.05.2017.
 */
internal abstract class AdvancedFragmentStatePagerAdapter<F : Fragment>(
        fragmentManager: FragmentManager
) : FragmentStatePagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    private enum class Mode {
        HashMap,
        Reflection
    }

    private val mode: Mode = Mode.Reflection

    private val mPageReferenceMap = HashMap<Int, F>()

    override fun getItem(position: Int): F {
        val f = createItem(position)
        if (mode == Mode.HashMap) {
            mPageReferenceMap[position] = f
        }
        return f
    }

    protected abstract fun createItem(position: Int): F

    @Suppress("UNCHECKED_CAST")
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val fragment = super.instantiateItem(container, position) as F
//        Log.d("SettingsFragment", "instantiateItem: $position")
        if (mode == Mode.HashMap) {
            mPageReferenceMap[position] = fragment
        }
        return fragment
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        super.destroyItem(container, position, `object`)
        if (mode == Mode.HashMap) {
            mPageReferenceMap.remove(position)
        }
    }

    private fun getFragmentTag(viewPagerId: Int, fragmentPosition: Int): String? {
        return "android:switcher:$viewPagerId:$fragmentPosition"
    }

    private fun getFragmentViaReflection(position: Int): F? {
        return try {
            val f: Field = FragmentStatePagerAdapter::class.java.getDeclaredField("mFragments")
            f.isAccessible = true
            val fragments = f.get(this) as ArrayList<F>
            if (fragments.size > position) {
                fragments[position] as F?
            } else null
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    fun getFragmentAt(position: Int): F? {
        return if (mode == Mode.HashMap) {
            mPageReferenceMap[position]
        } else {
            getFragmentViaReflection(position)
        }
    }

    val allCreatedFragments: Collection<F>
        get() {
            return if (mode == Mode.HashMap) {
                mPageReferenceMap.values
            } else {
                val items = ArrayList<F>()
                for (i in 0 until count) {
                    getFragmentAt(i)?.let {
                        items.add(it)
                    }
                }
                items
            }
        }
}