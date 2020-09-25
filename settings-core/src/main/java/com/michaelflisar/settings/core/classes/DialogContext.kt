package com.michaelflisar.settings.core.classes

import android.content.Context
import androidx.fragment.app.FragmentActivity
import com.michaelflisar.dialogs.base.MaterialDialogFragment

sealed class DialogContext(
        val setup: SettingsDisplaySetup,
        val context: Context
) {
    abstract fun show(dlg: MaterialDialogFragment<*>)

    class Activity(setup: SettingsDisplaySetup, private val activity: FragmentActivity) : DialogContext(setup, activity) {
        override fun show(dlg: MaterialDialogFragment<*>) {
            dlg.show(activity)
        }
    }

    class Fragment(setup: SettingsDisplaySetup, private val fragment: androidx.fragment.app.Fragment) : DialogContext(setup, fragment.requireContext()) {
        override fun show(dlg: MaterialDialogFragment<*>) {
            dlg.show(fragment)
        }
    }
}