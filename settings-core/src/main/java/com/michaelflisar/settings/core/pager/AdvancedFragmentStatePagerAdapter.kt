package com.michaelflisar.settings.core.pager

import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import java.util.*

/**
 * Created by Michael on 21.05.2017.
 */
abstract class AdvancedFragmentStatePagerAdapter(fragmentManager: FragmentManager) : FragmentStatePagerAdapter(fragmentManager) {

    private val mPageReferenceMap = HashMap<Int, Fragment>()

    override fun getItem(position: Int): Fragment {
        val f = createItem(position)
        mPageReferenceMap[position] = f
        return f
    }

    protected abstract fun createItem(position: Int): Fragment

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val fragment = super.instantiateItem(container, position) as Fragment
        mPageReferenceMap[position] = fragment
        return fragment
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        super.destroyItem(container, position, `object`)
        mPageReferenceMap.remove(position)
    }

    fun getFragmentAt(position: Int): Fragment? {
        return mPageReferenceMap[position]
    }

    val allCachedFragments: Collection<Fragment>
        get() = mPageReferenceMap.values
}