package com.example.stalarm.adapter

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Switch
import android.widget.TextView
import android.widget.TimePicker
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.stalarm.R
import com.example.stalarm.RoomDatabase.Alarm
import com.example.stalarm.RoomDatabase.AlarmViewModel
import com.example.stalarm.Util.AlarmUtils
import java.util.Calendar
import java.util.concurrent.TimeUnit

class AlarmAdapter(
    var mList: MutableList<Alarm>,
    val context:Context,
    var viewModel: AlarmViewModel
) :
    RecyclerView.Adapter<AlarmAdapter.ViewHolder>() {

    @RequiresApi(Build.VERSION_CODES.S)
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var days = itemView.findViewById(R.id.days) as TextView
        var time = itemView.findViewById(R.id.time) as TextView
        var remain_time = itemView.findViewById(R.id.tx_remaining_time) as TextView
        var description = itemView.findViewById(R.id.description) as TextView
        var switch = itemView.findViewById(R.id.switch1) as Switch

        init {
            itemView.setOnClickListener {
                showEditAlarmDialog(adapterPosition)
            }
        }

        private fun showEditAlarmDialog(position: Int) {
            val alarm = mList[position]
            val alarmUtils=AlarmUtils(viewModel)

            // Inflate custom dialog layout
            val dialogView = LayoutInflater.from(itemView.context)
                .inflate(R.layout.dialog_edit_alarm, null)

            // Initialize views in the dialog
            val timePicker = dialogView.findViewById<TimePicker>(R.id.timePicker)
            val editTextLabel = dialogView.findViewById<EditText>(R.id.editTextLabel)
            val switchEnable = dialogView.findViewById<Switch>(R.id.switchEnable)

            // Set initial values based on alarm object
            timePicker.hour = alarm.hour
            timePicker.minute = alarm.minute
            editTextLabel.setText(alarm.title)
            switchEnable.isChecked = alarm.started

            // Build the dialog
            val builder = AlertDialog.Builder(itemView.context)
                .setTitle("Edit Alarm")
                .setView(dialogView)
                .setPositiveButton("Save") { dialog, _ ->
                    if(alarm.started){
                        if(alarm.recurring){

                            val dayList = alarmUtils.getIntDay(alarm)
                            alarm.cancelAlarm(context,dayList)
                        }else{
                            alarm.cancelAlarm(context, listOf())
                        }
                    }
                    // Update alarm details based on user input
                    val newHour = timePicker.hour
                    val newMinute = timePicker.minute
                    val newLabel = editTextLabel.text.toString()
                    val newStarted = switchEnable.isChecked

                    // Update alarm object in the list
                    alarm.hour = newHour
                    alarm.minute = newMinute
                    alarm.title = newLabel
                    alarm.started = newStarted

                    // Schedule or cancel alarm based on updated status
                    if(alarm.recurring) {

                        val dayList = alarmUtils.getIntDay(alarm)
                        if (newStarted) {

                            alarm.schedule(context, dayList)
                        } else {
                            alarm.cancelAlarm(context, dayList)
                        }
                    }else{
                        if (newStarted) {
                            alarm.schedule(context, listOf())
                        } else {
                            alarm.cancelAlarm(context, listOf())
                        }
                    }

                    viewModel.update(alarm)

                    // Update RecyclerView item view
                    notifyItemChanged(position)
                    dialog.dismiss()
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }

            // Show the dialog
            builder.create().show()
        }


    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val rootView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_alarm, parent, false)
        return ViewHolder(rootView)
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mList[position]
        with(holder) {
            time.text = "${item.hour}:${item.minute}"

            val dayList = getDays(item)
            days.text = dayList.joinToString(", ")

            description.text = item.title

            // Check if the alarm is started
            if (item.started) {
                switch.isChecked = true
                // Calculate and display the remaining time until the alarm triggers
                val remainingTime = calculateRemainingTime(item.hour, item.minute)
                remain_time.text = "Alarm in $remainingTime"
                remain_time.visibility = View.VISIBLE
            } else {
                switch.isChecked = false
                remain_time.visibility = View.GONE
            }

            val alarmUtils=AlarmUtils(viewModel)

            // Add a listener to the switch
            switch.setOnClickListener {
                val position_ = adapterPosition
                if (position_ != RecyclerView.NO_POSITION) {
                    val alarm = mList[position_]
                    if(alarm.recurring) {

                        var day_list=alarmUtils.getIntDay(alarm)
                        // Toggle the alarm state
                        if (!alarm.started) {
                            alarm.schedule(context,day_list)
                            alarm.started = true
                        } else {
                            alarm.cancelAlarm(context,day_list)
                            alarm.started = false
                        }
                    }else{
                        if (!alarm.started) {
                            alarm.schedule(context, listOf())
                            alarm.started = true
                        } else {
                            alarm.cancelAlarm(context, listOf())
                            alarm.started = false
                        }
                    }

                    notifyDataSetChanged()
                }
            }
        }
    }

    private fun calculateRemainingTime(hour: Int, minute: Int): String {
        val currentTime = Calendar.getInstance()
        val alarmTime = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            // If the alarm time has already passed, increment the day by 1
            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.DAY_OF_MONTH, 1)
            }
        }

        val diffInMillis = alarmTime.timeInMillis - currentTime.timeInMillis
        val hours = TimeUnit.MILLISECONDS.toHours(diffInMillis)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(diffInMillis) % 60
        return "${hours}h ${minutes}m"
    }


    @SuppressLint("NotifyDataSetChanged")
    fun updateData(list: MutableList<Alarm>) {
        mList = list
        notifyDataSetChanged()
    }

    fun getDays(item:Alarm):MutableList<String>{
        var day_ls: MutableList<String> = mutableListOf()
        var flag=0

        if(item.monday) {
            day_ls.add("Mon")
            flag++
        }
        if(item.tuesday){
            day_ls.add("Tue")
            flag++
        }
        if(item.wednesday) {
            day_ls.add("Wed")
            flag++
        }
        if(item.thursday){
            day_ls.add("Thu")
            flag++
        }
        if(item.friday) {
            day_ls.add("Fri")
            flag++
        }
        if(item.saturday){
            day_ls.add("Sat")
            flag++
        }
        if(item.sunday) {
            day_ls.add("Sun")
            flag++
        }
        if(flag==7){
            day_ls.clear()
            day_ls.add("Daily")
        }
        if(flag==0){
            day_ls.add("Once")
        }

        return day_ls
    }


}