package com.example.stalarm.reschedul

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.example.stalarm.RoomDatabase.Alarm
import com.example.stalarm.RoomDatabase.AlarmViewModel
import com.example.stalarm.Util.AlarmUtils

class AlarmsReschedul {

    lateinit var viewModel: AlarmViewModel

    fun reschedulAlarms(context: Context) {
        val alarmsLiveData = viewModel.getAlarms()
        val alarmUtils=AlarmUtils(viewModel)
        val observer = object : Observer<List<Alarm>> {

            @RequiresApi(Build.VERSION_CODES.S)
            override fun onChanged(alarms: List<Alarm>) {
                if (alarms != null) {
                    for (a in alarms) {
                        if (a.started) {

                            if(a.recurring){

                                val dayIntList=alarmUtils.getIntDay(a)
                                a.schedule(context,dayIntList)
                            }else{
                                a.schedule(context, listOf())
                            }
                        }
                    }
                }

                alarmsLiveData?.removeObserver(this)
            }
        }
        alarmsLiveData?.observeForever(observer)
    }


}