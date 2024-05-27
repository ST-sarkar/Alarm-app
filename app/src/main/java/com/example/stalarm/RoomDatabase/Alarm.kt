package com.example.stalarm.RoomDatabase
import kotlinx.coroutines.*
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.stalarm.broadCastReceiver.AlarmBroadcastReceiver
import java.util.Calendar

@Entity(tableName = "alarm_table")
data class Alarm(

    @PrimaryKey(autoGenerate = true)
    var alarmId: Long,
    var hour: Int,
    var minute: Int,
    var title: String,
    var created: Long,
    var started: Boolean,
    var recurring: Boolean,
    var monday: Boolean,
    var tuesday: Boolean,
    var wednesday: Boolean,
    var thursday: Boolean,
    var friday: Boolean,
    var saturday: Boolean,
    var sunday: Boolean
) {

    @RequiresApi(Build.VERSION_CODES.S)
    @SuppressLint("DefaultLocale")
    fun schedule(context: Context, list_days: List<Int>){
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmBroadcastReceiver::class.java)

        intent.putExtra("RECURRING", recurring)
        intent.putExtra("ALARM_ID", alarmId)
        //Toast.makeText(context,"alrm_id:$alarmId",Toast.LENGTH_SHORT).show()
        intent.putExtra("TITLE", title)
        intent.putExtra("HOUR", hour)
        intent.putExtra("MINUTE", minute)
        intent.action = "com.example.ALARM_TRIGGER"

        val alarmPendingIntent = PendingIntent.getBroadcast(
            context,
            alarmId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar: Calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        // If alarm is not recurring then set one-time alarm
        if (!recurring) {
            val toastText = String.format(
                "One Time Alarm %s scheduled for %02d:%02d",
                title,
                hour,
                minute
            )
            Toast.makeText(context, toastText, Toast.LENGTH_LONG).show()

            // Adjust alarm day to next day if alarm time pass
            if (calendar.timeInMillis <= System.currentTimeMillis()) {
                calendar.add(Calendar.DAY_OF_MONTH, 1)
            }
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                alarmPendingIntent
            )
        } else {

           // Log.e("list_items","list days: "+list_days)
            val scope = CoroutineScope(Dispatchers.Default)
            scope.launch {
                scheduleRepeatingAlarmsForWeekdays(context, alarmManager, intent, list_days,scope)
            }
        }

        started = true
    }
    private fun scheduleRepeatingAlarmsForWeekdays(
        context: Context,
        alarmManager: AlarmManager,
        intent: Intent,
        list_days: List<Int>,
        scope: CoroutineScope
    ) {
        val selectedWeekdays = listOf(monday, tuesday, wednesday, thursday, friday, saturday, sunday)

        try {
            val currentTime = System.currentTimeMillis()

            for (dayOfWeek in selectedWeekdays.indices) {
                if (selectedWeekdays[dayOfWeek]) {
                    val calendar = Calendar.getInstance().apply {
                        set(Calendar.HOUR_OF_DAY, hour)
                        set(Calendar.MINUTE, minute)
                        set(Calendar.SECOND, 0)
                        set(Calendar.MILLISECOND, 0)
                        firstDayOfWeek = Calendar.MONDAY

                        val currentDayOfWeek = get(Calendar.DAY_OF_WEEK)
                        var daysToAdd = dayOfWeek - (currentDayOfWeek - Calendar.MONDAY)

                        if (daysToAdd < 0) {
                            daysToAdd += 7  // Adjust daysToAdd to be positive if necessary
                        }
                        if (daysToAdd == 0 && timeInMillis <= currentTime) {
                            daysToAdd += 7  // Add a week if the alarm time is in the past
                        }

                        add(Calendar.DAY_OF_YEAR, daysToAdd)
                    }

                    val alarmTime = calendar.timeInMillis

                    scope.launch {
                        val alarmPendingIntent = PendingIntent.getBroadcast(
                            context,
                            list_days[dayOfWeek], // Use a unique request code for each alarm
                            intent,
                            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                        )

                        alarmManager.setExactAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP,
                            alarmTime,
                            alarmPendingIntent
                        )
                    }
                }
            }
            val toastText = String.format(
                "Recurring Alarm scheduled at time %02d:%02d",
                hour,
                minute
            )
            Toast.makeText(context, toastText, Toast.LENGTH_LONG).show()
        } catch (e: Exception) {

            Log.e("Error finding", "Exception: ${e.message} ${e.localizedMessage} ${e.stackTrace}")
        }
    }



    fun cancelAlarm(context: Context, list_days: List<Int>) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmBroadcastReceiver::class.java)

        if (!recurring) {
            val alarmPendingIntent = PendingIntent.getBroadcast(
                context,
                alarmId.toInt(),
                intent,
                PendingIntent.FLAG_IMMUTABLE
            )
            alarmManager.cancel(alarmPendingIntent)
        } else {
            val selectedWeekdays = listOf(monday, tuesday, wednesday, thursday, friday, saturday, sunday)

            for ((index, selected) in selectedWeekdays.withIndex()) {
                if (selected) {
                    // checking list_days has enough element to selectedWeekdays
                    if (index < list_days.size) {
                        val alarmPendingIntent = PendingIntent.getBroadcast(
                            context,
                            list_days[index], // Use a unique request code for each alarm
                            intent,
                            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                        )
                        alarmManager.cancel(alarmPendingIntent)
                    } else {
                        throw IndexOutOfBoundsException("list_days does not have enough elements.")
                    }
                }
            }
        }

        started = false

        val toastText = String.format(
            "Alarm cancelled for %02d:%02d with id %d",
            hour,
            minute,
            alarmId
        )
        Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show()
    }

}
