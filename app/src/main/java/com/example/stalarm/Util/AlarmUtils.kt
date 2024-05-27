package com.example.stalarm.Util

import com.example.stalarm.RoomDatabase.Alarm
import com.example.stalarm.RoomDatabase.AlarmViewModel

class AlarmUtils(private val viewModel: AlarmViewModel) {

    fun getIntDay(alarm: Alarm): List<Int> {
        val dayCodes = mutableListOf<Int>()
        if (alarm.monday) {
            dayCodes.add(viewModel.getCodes(alarm.alarmId, "monday"))
        } else {
            dayCodes.add(0)
        }
        if (alarm.tuesday) {
            dayCodes.add(viewModel.getCodes(alarm.alarmId, "tuesday"))
        } else {
            dayCodes.add(0)
        }
        if (alarm.wednesday) {
            dayCodes.add(viewModel.getCodes(alarm.alarmId, "wednesday"))
        } else {
            dayCodes.add(0)
        }
        if (alarm.thursday) {
            dayCodes.add(viewModel.getCodes(alarm.alarmId, "thursday"))
        } else {
            dayCodes.add(0)
        }
        if (alarm.friday) {
            dayCodes.add(viewModel.getCodes(alarm.alarmId, "friday"))
        } else {
            dayCodes.add(0)
        }
        if (alarm.saturday) {
            dayCodes.add(viewModel.getCodes(alarm.alarmId, "saturday"))
        } else {
            dayCodes.add(0)
        }
        if (alarm.sunday) {
            dayCodes.add(viewModel.getCodes(alarm.alarmId, "sunday"))
        } else {
            dayCodes.add(0)
        }
        return dayCodes
    }
}
