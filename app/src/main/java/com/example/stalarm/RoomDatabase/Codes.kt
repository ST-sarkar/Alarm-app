package com.example.stalarm.RoomDatabase

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Codes")
class Codes(
            @PrimaryKey
            var Id: Long,
            var monday: Int,
            var tuesday: Int,
            var wednesday: Int,
            var thursday: Int,
            var friday: Int,
            var saturday: Int,
            var sunday: Int) {


}