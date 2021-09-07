package com.nurram.project.pencatatkeuangan.view.fragment.report

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.jjoe64.graphview.LegendRenderer
import com.jjoe64.graphview.series.LineGraphSeries
import com.nurram.project.pencatatkeuangan.R
import com.nurram.project.pencatatkeuangan.databinding.FragmentReportBinding
import com.nurram.project.pencatatkeuangan.db.Record
import com.nurram.project.pencatatkeuangan.utils.*
import com.nurram.project.pencatatkeuangan.view.ViewModelFactory
import com.nurram.project.pencatatkeuangan.view.activity.wallet.WalletActivity
import java.util.*

class ReportFragment : Fragment() {
    private lateinit var binding: FragmentReportBinding
    private lateinit var adapter: ReportAdapter
    private lateinit var viewModel: ReportViewModel

    private var selectedMonth = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentReportBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val pref = PrefUtil(requireContext())
        val walletId = pref.getStringFromPref(WalletActivity.prefKey, "def")
        val factory = ViewModelFactory(requireActivity().application, walletId)
        viewModel = ViewModelProvider(this, factory).get(ReportViewModel::class.java)
        adapter = ReportAdapter(requireContext()) { _, _ -> }
        selectedMonth = binding.reportDate.text.toString()

        moveDate(0)
        binding.apply {
            reportBack.setOnClickListener { moveDate(-1) }
            reportNext.setOnClickListener { moveDate(1) }
        }

        val spinnerAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            arrayOf(getString(R.string.expenses), getString(R.string.income))
        )
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        binding.apply {
            graphSpinner.adapter = spinnerAdapter
            graphSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {}

                override fun onItemSelected(
                    parent: AdapterView<*>?, view: View?, position: Int,
                    id: Long
                ) {
                    binding.graphChart.removeAllSeries()
                    getData(position)
                }
            }

            graphRecycler.adapter = adapter
            graphRecycler.layoutManager = LinearLayoutManager(requireContext())
            graphRecycler.setHasFixedSize(true)
        }
    }

    private fun moveDate(month: Int) {
        binding.graphSpinner.setSelection(0)
        adapter.clearList()

        if (selectedMonth.isEmpty()) {
            val currentMonth = DateUtil.getCurrentMonthAndYear()
            binding.reportDate.text = DateUtil.subtractMonth(DateUtil.toDate(currentMonth), month)
        } else {
            binding.reportDate.text = DateUtil.subtractMonth(DateUtil.toDate(selectedMonth), month)
        }

        selectedMonth = binding.reportDate.text.toString()
        getData(0)
    }

    private fun getData(position: Int) {
        val date = DateUtil.toDate(selectedMonth)
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)

        if (position == 0) {
            viewModel.getAllExpenses(date, calendar.time)?.observe(viewLifecycleOwner, {
                it?.let { it1 -> setData(it1, "out") }
            })
        } else if (position == 1) {
            viewModel.getAllIncome(date, calendar.time)?.observe(viewLifecycleOwner, {
                it?.let { it1 -> setData(it1, "in") }
            })
        }
    }

    private fun submitList(records: List<Record>) {
        val mappedData = viewModel.mapData(records as ArrayList<Record>)
        adapter.setData(mappedData)
    }

    private fun setData(data: List<Record>, whereFrom: String) {
        if (data.isNotEmpty()) {
            initGraph(data, whereFrom)
            submitList(data)

            binding.dataEmpty.GONE()
        } else {
            binding.dataEmpty.VISIBLE()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initGraph(graphList: List<Record>, whereFrom: String) {
        viewModel.setGraphData(graphList)

        val series = LineGraphSeries(viewModel.getDataPoint())
        series.isDrawDataPoints = true
        series.setAnimated(true)

        if (whereFrom == "in") {
            series.color = ContextCompat.getColor(requireContext(), R.color.colorAccent)
            series.title = getString(R.string.income)
        } else {
            series.color = ContextCompat.getColor(requireContext(), R.color.colorRed)
            series.title = getString(R.string.expenses)
        }

        series.setOnDataPointTapListener { _, dataPoint1 ->
            val datas = viewModel.getRecords(dataPoint1.x.toInt())
            submitList(datas)
        }

        binding.graphChart.apply {
            addSeries(series)
            gridLabelRenderer.textSize = 16f
            viewport.isXAxisBoundsManual = true
            viewport.setMaxX(30.0)
            viewport.setScalableY(true)
            legendRenderer.isVisible = true
            legendRenderer.align = LegendRenderer.LegendAlign.BOTTOM
            legendRenderer.backgroundColor = android.R.color.transparent
            legendRenderer.textSize = 24f
        }
    }
}