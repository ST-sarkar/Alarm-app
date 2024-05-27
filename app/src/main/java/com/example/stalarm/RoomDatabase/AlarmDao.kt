package com.example.stalarm.RoomDatabase

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface AlarmDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(alarm: Alarm):Long

    @Query("DELETE FROM alarm_table")
    fun deleteAll()

    @Query("SELECT * FROM alarm_table ORDER BY created ASC")
    fun getAlarms(): LiveData<List<Alarm>>

    @Update
    fun update(alarm: Alarm)

    @Query("UPDATE alarm_table SET started=0 WHERE alarmId=:id")
    fun updateAlarm(id:Long)

    @Query("DELETE FROM alarm_table WHERE alarmId=:id")
    fun deletSingleAlarm(id:Long)
}