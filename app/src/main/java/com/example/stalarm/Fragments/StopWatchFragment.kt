package com.example.stalarm.Fragments

import android.os.Bundle
import android.os.SystemClock
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.example.stalarm.R

class StopWatchFragment : Fragment() {

    private lateinit var timerTV: TextView
    private lateinit var startBtn: Button
    private lateinit var pauseBtn: Button
    private lateinit var resetBtn: Button

    private var startTime: Long = 0L
    private var timeInMilliseconds: Long = 0L
    private var timeSwapBuff: Long = 0L
    private var updateTime: Long = 0L
    private val handler = android.os.Handler()
    private var isRunning = false

    private val updateTimerThread = object : Runnable {
        override fun run() {
            timeInMilliseconds = SystemClock.uptimeMillis() - startTime
            updateTime = timeSwapBuff + timeInMilliseconds

            val secs = (updateTime / 1000).toInt()
            val mins = secs / 60
            val hours = mins / 60
            val milliseconds = (updateTime % 1000).toInt()

            timerTV.text = String.format(
                "%02d:%02d:%02d:%03d", hours, mins % 60, secs % 60, milliseconds
            )
            handler.postDelayed(this, 10)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view:View=inflater.inflate(R.layout.fragment_stop_watch, container, false)

        timerTV = view.findViewById(R.id.timerTextView)
        startBtn = view.findViewById(R.id.startButton)
        pauseBtn = view.findViewById(R.id.pauseButton)
        resetBtn = view.findViewById(R.id.resetButton)

        startBtn.setOnClickListener {
            if (!isRunning) {
                startTime = SystemClock.uptimeMillis()
                handler.postDelayed(updateTimerThread, 0)
                isRunning = true
            }
        }

        pauseBtn.setOnClickListener {
            if (isRunning) {
                timeSwapBuff += timeInMilliseconds
                handler.removeCallbacks(updateTimerThread)
                isRunning = false
            }
        }

        resetBtn.setOnClickListener {
            startTime = 0L
            timeInMilliseconds = 0L
            timeSwapBuff = 0L
            updateTime = 0L
            timerTV.text = "00:00:00:000"
            handler.removeCallbacks(updateTimerThread)
            isRunning = false
        }

        return view
    }

}