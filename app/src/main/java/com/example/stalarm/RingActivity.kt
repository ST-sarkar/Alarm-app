package com.example.stalarm

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.stalarm.Fragments.AlarmFragment
import com.example.stalarm.RoomDatabase.Alarm
import com.example.stalarm.RoomDatabase.AlarmViewModel
import com.example.stalarm.broadCastReceiver.AlarmBroadcastReceiver
import java.util.Calendar
import kotlin.random.Random

class RingActivity : AppCompatActivity() {
    lateinit var buttonDismiss: Button
    lateinit var buttonSnooze: Button
    lateinit var title: TextView
     lateinit var time: TextView
    var hour:Int=0
    var minute:Int=0
    var alarmId:Long=0
    var isRecurring:Boolean=false
    var fromStop:Boolean=false
    lateinit var viewModel: AlarmViewModel

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ring)

        buttonDismiss = findViewById(R.id.buttonDismiss)
        buttonSnooze = findViewById(R.id.buttonSnooze)
        time=findViewById(R.id.tx_time)
        title=findViewById(R.id.tx_title)


        alarmId = intent.getLongExtra("ALARM_ID", 0)
        var d=intent.getLongExtra("ALARM_ID", 0)
        //Toast.makeText(this,"alrm_id ring up:$d",Toast.LENGTH_SHORT).show()
        hour= intent?.getIntExtra("HOUR",1)!!
        minute= intent?.getIntExtra("MINUTE",1)!!
        time.text="${hour}:${minute}"
        title.text=intent?.getStringExtra("TITLE")
        isRecurring=intent.getBooleanExtra("RECURRING",false)

        // Initialize the ViewModel
        viewModel = ViewModelProvider(this).get(AlarmViewModel::class.java)

        buttonDismiss.setOnClickListener {

            stopAlarm()
        }

        buttonSnooze.setOnClickListener {

            snoozeAlarm()
        }

        if(!isRecurring){
            this.viewModel.updateAlarm(alarmId)//set started attribute to 0
            (supportFragmentManager.findFragmentById(R.id.fragment_container) as? AlarmFragment)?.onDatasetChanged()
        }

        }
    @RequiresApi(Build.VERSION_CODES.S)
    private fun stopAlarm() {
        // Stop the ringtone
        AlarmBroadcastReceiver.stopRingtone()

        if(isRecurring){
            fromStop=true
            setAlarm()
        }

        // Dismiss the notification
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(alarmId.toInt())

        // Finish the activity
        finish()
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun snoozeAlarm() {
        // Stop the ringtone
        AlarmBroadcastReceiver.stopRingtone()

        // Dismiss the notification
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(alarmId.toInt())

        setAlarm()

        finish()
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun setAlarm( ) {

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, AlarmBroadcastReceiver::class.java)

        val calendar: Calendar = Calendar.getInstance()
        if(fromStop){
            val hr=intent.getIntExtra("ORG_HOUR",0)
            val min=intent.getIntExtra("ORG_MINUTE",0)
            calendar.set(Calendar.HOUR_OF_DAY,hr)
            calendar.set(Calendar.MINUTE,min)
            calendar.set(Calendar.MILLISECOND,0)
            calendar.add(Calendar.DAY_OF_YEAR,7)

        }else {

            calendar.timeInMillis = System.currentTimeMillis()
            calendar.add(Calendar.MINUTE, 5)
        }

        var new_hour=calendar.get(Calendar.HOUR_OF_DAY)
        var new_minute=calendar.get((Calendar.MINUTE))
        intent.putExtra("TITLE", "Snooze Alarm !!!!")
        intent.putExtra("ORG_HOUR",hour)
        intent.putExtra("ORG_MINUTE", minute)
        intent.putExtra("HOUR",new_hour)
        intent.putExtra("MINUTE", new_minute)
        intent.putExtra("ALARM_ID", alarmId) // Pass the alarm ID
        intent.action = "com.example.ALARM_TRIGGER" // Custom action to identify alarm intent



        val pendingIntent = PendingIntent.getBroadcast(
            this,
            alarmId.toInt(), // request code
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE // Specify immutability flag
        )

        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)

        //Toast.makeText(this, "Alarm Snooze for 5 Min!", Toast.LENGTH_SHORT).show()
    }

}