package com.example.stalarm.RoomDatabase

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData

class AlarmViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: AlarmRepository
    val alarmsLiveData: LiveData<List<Alarm>>


    init {
        val alarmDao = AppDatabase.getInstance(application).alarmDao()
        val codeDao = AppDatabase.getInstance(application).codeDao()
        repository = AlarmRepository(alarmDao,codeDao)
        alarmsLiveData = repository.getAlarms()
    }

    fun getCodes(id:Long,day:String):Int{
        return repository.getCodes(id,day)
    }

    fun checkCode(id:Int):LiveData<Int>{
        return repository.checkCode(id)
    }

    fun addCodes(codes: Codes){
        repository.insertCode(codes)
    }


    fun getAlarms(): LiveData<List<Alarm>>? {
        return alarmsLiveData
    }


    fun addAlarm(alarm: Alarm):Long {
        return repository.insert(alarm)
    }

    fun deleteAll() {
        repository.deleteAll()
    }

    fun update(alarm: Alarm) {
        repository.update(alarm)
    }

    fun updateAlarm(id:Long){
        repository.updateAlarm(id)
    }
}
