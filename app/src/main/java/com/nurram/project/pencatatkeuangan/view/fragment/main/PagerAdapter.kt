package com.nurram.project.pencatatkeuangan.view.fragment.main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.nurram.project.pencatatkeuangan.view.fragment.debt.DebtFragment
import com.nurram.project.pencatatkeuangan.view.fragment.history.HistoryFragment

class PagerAdapter(fragmentManager: FragmentManager) : FragmentStatePagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    private val fragments = listOf(HistoryFragment(), DebtFragment())

    override fun getItem(p0: Int): Fragment = fragments[p0]

    override fun getCount(): Int = fragments.size

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            0 -> "History"
            1 -> "Debt"
            else -> super.getPageTitle(position)
        }
    }
}