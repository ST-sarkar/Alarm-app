package com.example.stalarm

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TimePicker
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.stalarm.RoomDatabase.Alarm
import com.example.stalarm.RoomDatabase.AlarmViewModel
import com.example.stalarm.RoomDatabase.Codes
import com.example.stalarm.Util.TimePickerUtil

class AddAlarmActivity : AppCompatActivity() {
    lateinit var buttonSave: Button
    lateinit var timePicker: TimePicker
    lateinit var viewModel: AlarmViewModel
    lateinit var monday: CheckBox
    lateinit var tuesday: CheckBox
    lateinit var wednesday: CheckBox
    lateinit var thursday: CheckBox
    lateinit var friday: CheckBox
    lateinit var saturday: CheckBox
    lateinit var sunday: CheckBox
    lateinit var recurring: CheckBox
    lateinit var linearLayoutRecurringOptions: LinearLayout
    lateinit var editTextTitle: EditText

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_alarm)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id._container)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        buttonSave = findViewById(R.id.buttonSave)
        timePicker = findViewById(R.id.timePicker)
        monday = findViewById(R.id.checkBoxMonday)
        tuesday =findViewById(R.id.checkBoxTuesday)
        wednesday = findViewById(R.id.checkBoxWednesday)
        thursday = findViewById(R.id.checkBoxThursday)
        friday = findViewById(R.id.checkBoxFriday)
        saturday = findViewById(R.id.checkBoxSartuday)
        sunday =findViewById(R.id.checkBoxSunday)
        recurring = findViewById(R.id.checkBoxRecurring)
        linearLayoutRecurringOptions = findViewById(R.id.linearLayoutRecurringOptions)
        editTextTitle = findViewById(R.id.editTextTitle)


        recurring.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                linearLayoutRecurringOptions.visibility = View.VISIBLE
            } else {
                linearLayoutRecurringOptions.visibility = View.GONE
            }
        }

        viewModel = ViewModelProvider(this)[AlarmViewModel::class.java]

        buttonSave.setOnClickListener {
            if(recurring.isChecked){
                if(!monday.isChecked && !tuesday.isChecked && !wednesday.isChecked && !thursday.isChecked && !friday.isChecked && !saturday.isChecked && !sunday.isChecked){
                    recurring.isChecked=false
                }
            }
            scheduleAlarm()

            finish()
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)

    private fun scheduleAlarm() {
        try {
            val alarm = Alarm(
                0,
                TimePickerUtil.getTimePickerHour(timePicker),
                TimePickerUtil.getTimePickerMinute(timePicker),
                editTextTitle.text.toString(),
                System.currentTimeMillis(),
                true,
                recurring.isChecked,
                monday.isChecked,
                tuesday.isChecked,
                wednesday.isChecked,
                thursday.isChecked,
                friday.isChecked,
                saturday.isChecked,
                sunday.isChecked
            )

            val id = viewModel.addAlarm(alarm)
            alarm.alarmId = id

            if (alarm.recurring) {
                var numId: Int
                val codes = Codes(alarm.alarmId, 0, 0, 0, 0, 0, 0, 0)
                val selectedWeekdays =
                    listOf(monday, tuesday, wednesday, thursday, friday, saturday, sunday)
                for ((index, day) in selectedWeekdays.withIndex()) {

                    numId = kotlin.random.Random.nextInt(0, Int.MAX_VALUE)
                    if (day.isChecked) {
                        while (!checkEmpty(numId)) {
                            numId = kotlin.random.Random.nextInt(0, Int.MAX_VALUE)
                        }

                        if (index == 0) {
                            codes.monday = numId
                        } else if (index == 1) {
                            codes.tuesday = numId
                        } else if (index == 2) {
                            codes.wednesday = numId
                        } else if (index == 3) {
                            codes.thursday = numId
                        } else if (index == 4) {
                            codes.friday = numId
                        } else if (index == 5) {
                            codes.saturday = numId
                        } else {
                            codes.sunday = numId
                        }
                    }
                }

                viewModel.addCodes(codes)

                val uniq_days = listOf(
                    codes.monday,
                    codes.tuesday,
                    codes.wednesday,
                    codes.thursday,
                    codes.friday,
                    codes.saturday,
                    codes.sunday
                )
                alarm.schedule(applicationContext, uniq_days)
            } else {
                alarm.schedule(applicationContext, listOf())
            }
        }catch (e:Exception){
            Log.e("error finding1","exception:  "+e.message+"     "+e.localizedMessage+"  "+e.stackTrace)
        }


    }

    fun checkEmpty(numId: Int): Boolean {
        try {
            val liveData = viewModel.checkCode(numId)

            val isEmptyLiveData = MutableLiveData<Boolean>()

            // Observer to set the value of isEmptyLiveData based on the result of the query
            val observer = Observer<Int> { count ->
                isEmptyLiveData.value = count == 0
            }

            // add the observer
            liveData.observe(this, observer)


            // Return the value of isEmptyLiveData
            return isEmptyLiveData.value ?: true
        }catch (e:Exception){
            Log.e("error finding2","exception:"+e.message+"  "+e.stackTrace+"  "+e.localizedMessage)
        }
        return false
    }

}