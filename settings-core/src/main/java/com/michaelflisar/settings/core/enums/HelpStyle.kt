package com.michaelflisar.settings.core.enums

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

sealed class HelpStyle : Parcelable {

    abstract val mode: Mode

    @Parcelize
    class Icon(override val mode: Mode) : HelpStyle()

//    @Parcelize
//    class TitleIcon(val mode: Mode): HelpStyle2()

    @Parcelize
    class Invisible(override val mode: Mode) : HelpStyle()

    enum class Mode {
        Click,
        LongPress
    }
}