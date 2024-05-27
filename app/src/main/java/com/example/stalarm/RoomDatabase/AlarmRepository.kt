package com.example.stalarm.RoomDatabase

import android.content.Context
import androidx.lifecycle.LiveData
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class AlarmRepository(private val alarmDao: AlarmDao,private val codeDao: codeDao) {

    fun insert(alarm: Alarm):Long {
        return alarmDao.insert(alarm)
    }

    fun deleteAll() {
        alarmDao.deleteAll()
    }

    fun getAlarms(): LiveData<List<Alarm>> {
        return alarmDao.getAlarms()
    }

    fun update(alarm: Alarm) {
        alarmDao.update(alarm)
    }

    fun updateAlarm(id:Long){
        alarmDao.updateAlarm(id)
    }

    fun insertCode(codes: Codes){
        codeDao.insertCode(codes)
    }
    fun getCodes(id:Long,day:String):Int{
        return codeDao.getCodes(id,day)
    }

    fun checkCode(id:Int):LiveData<Int>{
        return codeDao.checkId(id)
    }
    /*
    companion object {
        @Volatile
        private var instance: AlarmRepository? = null

        fun getInstance(context: Context): AlarmRepository {
            return instance ?: synchronized(this) {

                AlarmRepository(AppDatabase.getInstance(context).alarmDao())

            }
        }

    }
    
     */
}
