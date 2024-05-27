package com.example.stalarm.broadCastReceiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.icu.util.Calendar
import android.media.Ringtone
import android.media.RingtoneManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import com.example.stalarm.RingActivity
import com.example.stalarm.RoomDatabase.AlarmViewModel
import com.example.stalarm.reschedul.AlarmsReschedul

class AlarmBroadcastReceiver : BroadcastReceiver() {
    val CHANNAL_ID="alarm_channel_id"

    companion object {
        private var ringtone: Ringtone? = null
        private var vibrator: Vibrator? = null

        // Method to start the ringtone
        fun startRingtone(context: Context?) {
            val ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            ringtone = RingtoneManager.getRingtone(context, ringtoneUri)
            ringtone?.play()

            // Start vibrating with pattern (vibrate for 1 second, wait for 1 second, repeat)
            val pattern = longArrayOf(1000, 1000)
            vibrator = context?.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            vibrator?.vibrate(VibrationEffect.createWaveform(pattern, 0))
        }

        // Method to stop the ringtone
        fun stopRingtone() {
            ringtone?.stop()
            vibrator?.cancel()
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onReceive(context: Context, intent: Intent) {

        if (Intent.ACTION_BOOT_COMPLETED == intent.action) {
            //val toastText = String.format("Alarm Reboot")
            //Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show()

            val viewModelStore = ViewModelStore()
            val viewModelProvider = ViewModelProvider(viewModelStore, ViewModelProvider.NewInstanceFactory())
            val alarmsReschedul = AlarmsReschedul()
            alarmsReschedul.viewModel = viewModelProvider.get(AlarmViewModel::class.java)
            alarmsReschedul.reschedulAlarms(context)
        } else {

            if (intent?.action == "com.example.ALARM_TRIGGER") {

                showNotification(context, intent)

                startRingtone(context)
            }

        }
    }

    private fun showNotification(context: Context?,intent: Intent?) {
        val notificationManager =
            context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channel = NotificationChannel(
            CHANNAL_ID,
            "Alarm Channel",
            NotificationManager.IMPORTANCE_HIGH
        )
        notificationManager.createNotificationChannel(channel)

        // Create an intent to open your activity
        val resultIntent = Intent(context, RingActivity::class.java)
        val alarmId = intent?.getLongExtra("ALARM_ID", 0)
        //Toast.makeText(context,"alrm_id brod:$alarmId",Toast.LENGTH_SHORT).show()
        resultIntent.putExtra("ALARM_ID",alarmId)
        resultIntent.putExtra("RECURRING",intent?.getBooleanExtra("RECURRING",false))
        resultIntent.putExtra("TITLE",intent?.getStringExtra("TITLE"))
        resultIntent.putExtra("HOUR",intent?.getIntExtra("HOUR",1))
        resultIntent.putExtra("MINUTE",intent?.getIntExtra("MINUTE",1))
        resultIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        val pendingIntent = PendingIntent.getActivity(
            context,
            1,
            resultIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNAL_ID)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentText(intent?.getIntExtra("HOUR",1).toString()+" : "+intent?.getIntExtra("MINUTE",1)+"\n"+intent?.getStringExtra("TITLE"))
            .setContentIntent(pendingIntent) // Set the pending intent
            .setAutoCancel(false) // Auto-cancel the notification when clicked
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        notificationManager.notify(123, notification)
    }

}