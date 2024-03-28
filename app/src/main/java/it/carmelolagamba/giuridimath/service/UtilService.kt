package it.carmelolagamba.giuridimath.service

import android.widget.NumberPicker
import javax.inject.Inject

class UtilService @Inject constructor() {

    fun getNumberClockFormatter(): NumberPicker.Formatter {
        return NumberPicker.Formatter { String.format("%02d", it) }
    }
}