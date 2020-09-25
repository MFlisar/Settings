package com.michaelflisar.settings.core.classes

import java.lang.RuntimeException

object NoValidStorageException : RuntimeException(
        "No valid SettingsStorage for this setting found!"
)