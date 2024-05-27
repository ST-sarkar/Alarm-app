package com.example.stalarm.adapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.stalarm.Fragments.AlarmFragment
import com.example.stalarm.Fragments.StopWatchFragment

class PagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int {
        return 2 // Number of tabs
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> AlarmFragment() // Fragment for first tab
            1 -> StopWatchFragment() // Fragment for second tab
            else -> throw IllegalArgumentException("Invalid position")
        }
    }
}