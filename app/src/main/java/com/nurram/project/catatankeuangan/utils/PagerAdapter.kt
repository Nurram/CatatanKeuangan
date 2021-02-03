package com.nurram.project.catatankeuangan.utils

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
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