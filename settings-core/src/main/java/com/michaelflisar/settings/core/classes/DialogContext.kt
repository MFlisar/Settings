package com.michaelflisar.settings.core.classes

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import com.michaelflisar.dialogs.base.MaterialDialogFragment
import com.michaelflisar.settings.core.getActivity

sealed class DialogContext(
        val context: Context
) {
    companion object {
        internal fun getParentContext(fragment: androidx.fragment.app.Fragment): DialogContext {
            return if (fragment.parentFragment != null)
                Fragment(fragment.parentFragment!!)
            else
                Activity(fragment.requireActivity())
        }
    }

    val activity: AppCompatActivity
            get() = context.getActivity()!!

    abstract fun show(dlg: MaterialDialogFragment<*>)

    class Activity(private val a: FragmentActivity) : DialogContext(a) {
        override fun show(dlg: MaterialDialogFragment<*>) {
            dlg.show(a)
        }
    }

    class Fragment(private val f: androidx.fragment.app.Fragment) : DialogContext(f.requireContext()) {
        override fun show(dlg: MaterialDialogFragment<*>) {
            dlg.show(f)
        }
    }
}