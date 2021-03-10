package com.nurram.project.pencatatkeuangan.view.fragment.report

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.lifecycle.ViewModelProvider
import com.nurram.project.pencatatkeuangan.R
import com.nurram.project.pencatatkeuangan.databinding.FragmentReportBinding
import com.nurram.project.pencatatkeuangan.view.fragment.main.MainViewModel

class ReportFragment : Fragment() {
    private lateinit var binding: FragmentReportBinding
    private var viewModel: MainViewModel? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentReportBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, resources.getStringArray(R.array.months))
        binding.reportMonth.adapter = adapter

        viewModel = activity?.let { ViewModelProvider(it).get(MainViewModel::class.java) }
    }
}