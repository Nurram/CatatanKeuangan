package com.nurram.project.pencatatkeuangan.view.fragment.container

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.nurram.project.pencatatkeuangan.view.fragment.report.ReportFragment

class ViewPagerAdapter(
    fm: FragmentManager,
    lifecycle: Lifecycle
) : FragmentStateAdapter(fm, lifecycle) {

    val firstElementPosition = Int.MAX_VALUE / 2

    override fun getItemCount(): Int = Int.MAX_VALUE

    override fun createFragment(position: Int): Fragment {
        val sendPosition = position - firstElementPosition
        return ReportFragment.newInstance(sendPosition)
    }
}
