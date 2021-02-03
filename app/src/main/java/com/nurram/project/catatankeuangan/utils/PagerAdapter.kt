package com.nurram.project.catatankeuangan.utils

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.app.FragmentStatePagerAdapter
import com.nurram.project.catatankeuangan.HutangFragment
import com.nurram.project.catatankeuangan.RiwayatFragment

class PagerAdapter(fragmentManager: FragmentManager) : FragmentStatePagerAdapter(fragmentManager) {

    private val fragments = listOf(RiwayatFragment(), HutangFragment())

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