package com.michaelflisar.settings.core.classes

import android.content.Intent

class SettingsActivityResultEvent(
        val requestCode: Int,
        val resultCode: Int,
        val resultData: Intent?
)