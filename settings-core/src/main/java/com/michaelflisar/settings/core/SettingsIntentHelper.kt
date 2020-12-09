package com.michaelflisar.settings.core

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.michaelflisar.settings.core.classes.DialogContext
import com.michaelflisar.settings.core.classes.SettingsActivityResultEvent
import com.michaelflisar.settings.core.interfaces.ISetting
import com.michaelflisar.settings.core.interfaces.ISettingsData
import com.michaelflisar.settings.core.interfaces.ISettingsDialogEventHandler

object SettingsIntentHelper {

    enum class Mode {
        HelperActivity,
        ManualActivityResultForwarding
    }

    var MODE = Mode.HelperActivity

    // we do use a trick to support SystemIntents in the settings library, that's why following is necessary
    // not very beautiful but it works
    // PROS:
    // - no need to integrate custom code inside the parent activity/fragment of the settings view
    // - event based automatic solution, just like the dialog does it

    private val intentSettings: HashMap<Int, Pair<ISetting<*>, ISettingsData>> = HashMap()

    fun rememberIntentSetting(dialogEventHandler: ISettingsDialogEventHandler<*, *>, settingsItem: ISetting<*>, settingsData: ISettingsData) {
        intentSettings.put(dialogEventHandler.dialogType, Pair(settingsItem, settingsData))
    }

    fun <T> getIntentSetting(dialogEventHandler: ISettingsDialogEventHandler<*, *>): Pair<ISetting<T>, ISettingsData> {
        return intentSettings.remove(dialogEventHandler.dialogType) as Pair<ISetting<T>, ISettingsData>
    }

    fun startHelperActivity(context: Context, intent: Intent, requestCode: Int) {
        val activityIntent = Intent(context, IntentActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            putExtra("intent", intent)
            putExtra("requestCode", requestCode)
        }
        when (MODE) {
            Mode.HelperActivity -> context.startActivity(activityIntent)
            Mode.ManualActivityResultForwarding -> {
                // parent activity must forward the result to SettingsManager.pushDialogEvent manually!
                context.getActivity()?.startActivityForResult(activityIntent, requestCode)
            }
        }
    }

    internal class IntentActivity : AppCompatActivity() {

        private var started = false

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            started = savedInstanceState?.containsKey("started")?.let { savedInstanceState.getBoolean("started") }
                    ?: false

            if (!started) {
                val activityIntent: Intent = intent.extras!!.getParcelable("intent")!!
                val requestCode = intent.extras!!.getInt("requestCode")
                startActivityForResult(activityIntent, requestCode)
                started = true
            }
        }

        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            super.onActivityResult(requestCode, resultCode, data)
            SettingsManager.pushDialogEvent(SettingsActivityResultEvent(requestCode, resultCode, data), DialogContext.Activity(this))
            finish()
        }

        override fun onSaveInstanceState(outState: Bundle) {
            super.onSaveInstanceState(outState)
            outState.putBoolean("started", started)
        }
    }
}