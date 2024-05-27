package com.example.stalarm.Util

import android.os.Build
import android.widget.TimePicker

object TimePickerUtil {
    fun getTimePickerHour(tp: TimePicker): Int {
        return tp.hour
    }

    fun getTimePickerMinute(tp: TimePicker): Int {
        return tp.minute
    }
}