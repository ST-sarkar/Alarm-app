package com.example.stalarm.Fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.stalarm.AddAlarmActivity
import com.example.stalarm.MainActivity
import com.example.stalarm.R
import com.example.stalarm.RoomDatabase.Alarm
import com.example.stalarm.RoomDatabase.AlarmViewModel
import com.example.stalarm.Util.AlarmUtils
import com.example.stalarm.Util.OnDatasetChangedListener
import com.example.stalarm.adapter.AlarmAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton


class AlarmFragment : Fragment(),OnDatasetChangedListener {

    private lateinit var viewModel: AlarmViewModel
    private var mAdapter: AlarmAdapter? = null

    private lateinit var rvAlarms: RecyclerView
    private lateinit var floatingActionButton: FloatingActionButton
    lateinit var item_list:MutableList<Alarm>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view:View = inflater.inflate(R.layout.fragment_alarm, container, false)


        (activity as AppCompatActivity?)!!.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        (activity as AppCompatActivity?)!!.supportActionBar?.setDisplayShowHomeEnabled(false)

        rvAlarms = view.findViewById(R.id.rvAlarms)
        floatingActionButton = view.findViewById(R.id.floatingActionButton)

        rvAlarms.adapter = mAdapter
        rvAlarms.layoutManager = LinearLayoutManager(context)

        viewModel = ViewModelProvider(this).get(AlarmViewModel::class.java)
        viewModel.getAlarms()?.observe(viewLifecycleOwner) { alarms ->
            item_list=alarms as MutableList<Alarm>
            loadResults(item_list)
        }

        floatingActionButton.setOnClickListener {

            val intent = Intent(activity, AddAlarmActivity::class.java)
            startActivity(intent)

        }

        return view
    }

    private fun loadResults(results: MutableList<Alarm>) {
        mAdapter = AlarmAdapter(results,requireContext(),viewModel)
        rvAlarms.adapter = mAdapter
        mAdapter?.updateData(results)
    }

    override fun onDatasetChanged() {

        for (alarm in item_list){
            if(alarm.started){
                if (alarm.recurring){
                    val alarmUtils=AlarmUtils(viewModel)
                    val id_list=alarmUtils.getIntDay(alarm)
                    alarm.cancelAlarm(context?.applicationContext!!,id_list)
                }else{
                    alarm.cancelAlarm(context?.applicationContext!!, listOf())
                }
            }
        }

        mAdapter?.notifyDataSetChanged()

    }

}
