package com.example.stalarm.RoomDatabase

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface codeDao {
    @Insert
    fun insertCode(codes: Codes)

    @Query("SELECT :day FROM Codes WHERE Id=:id")
    fun getCodes(id:Long,day:String):Int

    @Query("SELECT CASE WHEN COUNT(Id) > 0 THEN 1 ELSE 0 END FROM Codes WHERE monday = :generatedInteger OR tuesday = :generatedInteger OR wednesday = :generatedInteger OR thursday = :generatedInteger OR friday = :generatedInteger OR saturday = :generatedInteger OR sunday = :generatedInteger")
    fun checkId(generatedInteger: Int): LiveData<Int>

}