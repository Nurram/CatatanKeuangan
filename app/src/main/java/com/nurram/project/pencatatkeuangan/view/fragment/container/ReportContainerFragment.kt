package com.nurram.project.pencatatkeuangan.view.fragment.container

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.nurram.project.pencatatkeuangan.databinding.FragmentReportContainerBinding
import com.nurram.project.pencatatkeuangan.utils.DateUtil

class ReportContainerFragment : Fragment() {
    private var _binding: FragmentReportContainerBinding? = null
    private val binding get() = _binding!!

    private var currentDate = DateUtil.getCurrentMonthAndYear()
    private var currentPosition = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReportContainerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val pagerAdapter = ViewPagerAdapter(childFragmentManager, lifecycle)
        currentPosition = pagerAdapter.firstElementPosition

        binding.viewPager.apply {
            adapter = pagerAdapter
            orientation = ViewPager2.ORIENTATION_HORIZONTAL
            currentItem = pagerAdapter.firstElementPosition
            offscreenPageLimit = 1
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {

                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)

                    val subtractedPosition = when {
                        currentPosition < position -> 1
                        currentPosition > position -> -1
                        else -> 0
                    }

                    moveDate(subtractedPosition, true)
                }
            })
        }

        binding.apply {
            reportDate.text = currentDate
            reportBack.setOnClickListener { moveDate(-1, false) }
            reportNext.setOnClickListener { moveDate(1, false) }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun moveDate(month: Int, userSwipe: Boolean) {
        currentPosition += month
        currentDate = DateUtil.subtractMonth(DateUtil.toDate(currentDate), month)

        binding.apply {
            reportDate.text = currentDate
            if (!userSwipe) viewPager.currentItem = currentPosition
        }
    }
}