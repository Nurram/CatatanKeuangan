package com.nurram.project.pencatatkeuangan.view.fragment.report

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.jjoe64.graphview.LegendRenderer
import com.jjoe64.graphview.series.LineGraphSeries
import com.nurram.project.pencatatkeuangan.R
import com.nurram.project.pencatatkeuangan.databinding.FragmentReportBinding
import com.nurram.project.pencatatkeuangan.db.Record
import com.nurram.project.pencatatkeuangan.utils.*
import com.nurram.project.pencatatkeuangan.view.ViewModelFactory
import com.nurram.project.pencatatkeuangan.view.activity.add.AddDataActivity
import com.nurram.project.pencatatkeuangan.view.activity.main.MainActivity
import com.nurram.project.pencatatkeuangan.view.activity.wallet.WalletActivity
import com.nurram.project.pencatatkeuangan.view.fragment.main.MainFragment
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

        val activity = activity as MainActivity
        activity.hideMenu()
        activity.setTitle(getString(R.string.report))

        val pref = PrefUtil(requireContext())
        val walletId = pref.getStringFromPref(WalletActivity.prefKey, MainFragment.DEFAULT_WALLET)
        val factory = ViewModelFactory(requireActivity().application, walletId)
        viewModel = ViewModelProvider(this, factory)[ReportViewModel::class.java]
        adapter = ReportAdapter(requireContext()) { data, view ->
            val bundle = Bundle()
            bundle.putParcelable(MainFragment.RECORD_DATA, data)
            view.findNavController().navigate(
                R.id.action_navigation_report_to_addDataActivity,
                bundle
            )
        }
        selectedMonth = binding.reportDate.text.toString()

        moveDate(0)
        binding.apply {
            reportBack.setOnClickListener { moveDate(-1) }
            reportNext.setOnClickListener { moveDate(1) }
        }

        binding.apply {
            tvIncome.setOnClickListener {
                binding.graphChart.removeAllSeries()
                setBtnBg(0)
                getData(0)
            }
            tvOutcome.setOnClickListener {
                binding.graphChart.removeAllSeries()
                setBtnBg(1)
                getData(1)
            }
            tvDebt.setOnClickListener {
                binding.graphChart.removeAllSeries()
                setBtnBg(2)
                getData(2)
            }


            graphRecycler.adapter = adapter
            graphRecycler.layoutManager = LinearLayoutManager(requireContext())
            graphRecycler.setHasFixedSize(true)
        }
    }

    private fun moveDate(month: Int) {
        binding.tvIncome.setBackgroundResource(R.drawable.rounded_primary_rectangle)
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

        when (position) {
            0 -> {
                viewModel.getAllIncome(date, calendar.time)?.observe(viewLifecycleOwner, {
                    it?.let { it1 -> setData(it1, AddDataActivity.INCOME) }
                })
            }
            1 -> {
                viewModel.getAllExpenses(date, calendar.time)?.observe(viewLifecycleOwner, {
                    it?.let { it1 -> setData(it1, AddDataActivity.EXPENSE) }
                })
            }
            else -> {
                viewModel.getAllDebt(date, calendar.time)?.observe(viewLifecycleOwner, {
                    it?.let { it1 -> setData(it1, AddDataActivity.DEBT) }
                })
            }
        }

        viewModel.getMaxIncome(date, calendar.time)?.observe(viewLifecycleOwner, {
            if (it != null) {
                binding.incomeTitle.text = it.judul
                binding.incomeDate.text = it.date?.let { it1 -> DateUtil.formatDate(it1) }
                binding.incomeTotal.text = CurrencyFormatter.convertAndFormat(it.total)
            } else {
                binding.incomeTitle.text = "-"
                binding.incomeDate.text = "-"
                binding.incomeTotal.text = CurrencyFormatter.convertAndFormat(0)
            }
        })

        viewModel.getMaxExpense(date, calendar.time)?.observe(viewLifecycleOwner, {
            if (it != null) {
                binding.expenseTitle.text = it.judul
                binding.expenseDate.text = it.date?.let { it1 -> DateUtil.formatDate(it1) }
                binding.expenseTotal.text = CurrencyFormatter.convertAndFormat(it.total)
            } else {
                binding.expenseTitle.text = "-"
                binding.expenseDate.text = "-"
                binding.expenseTotal.text = CurrencyFormatter.convertAndFormat(0)
            }
        })
    }

    private fun submitList(records: List<Record>) {
        val mappedData = viewModel.mapData(records as ArrayList<Record>)
        adapter.setData(mappedData)
    }

    private fun setData(data: List<Record>, whereFrom: String) {
        initGraph(data, whereFrom)
        submitList(data)

        if (data.isNotEmpty()) {
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

        when (whereFrom) {
            AddDataActivity.INCOME -> {
                series.color = ContextCompat.getColor(requireContext(), R.color.colorAccent)
                series.title = getString(R.string.income)
            }
            AddDataActivity.EXPENSE -> {
                series.color = ContextCompat.getColor(requireContext(), R.color.colorRed)
                series.title = getString(R.string.expenses)
            }
            else -> {
                series.color = ContextCompat.getColor(requireContext(), R.color.colorGreen)
                series.title = getString(R.string.debt)
            }
        }

        series.setOnDataPointTapListener { _, dataPoint1 ->
            val datas = viewModel.getRecords(dataPoint1.x.toInt())
            submitList(datas)
        }

        binding.graphChart.apply {
            removeAllSeries()
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

    private fun setBtnBg(position: Int) {
        binding.apply {
            when (position) {
                0 -> {
                    tvIncome.apply {
                        setBackgroundResource(R.drawable.rounded_primary_rectangle)
                        setTextColor(resources.getColor(R.color.colorAccent))
                    }
                    tvOutcome.apply {
                        setBackgroundResource(R.drawable.rounded_gray_rectangle)
                        setTextColor(resources.getColor(R.color.cardText))
                    }
                    tvDebt.apply {
                        setBackgroundResource(R.drawable.rounded_gray_rectangle)
                        setTextColor(resources.getColor(R.color.cardText))
                    }
                }
                1 -> {
                    tvIncome.apply {
                        setBackgroundResource(R.drawable.rounded_gray_rectangle)
                        setTextColor(resources.getColor(R.color.cardText))
                    }
                    tvOutcome.apply {
                        setBackgroundResource(R.drawable.rounded_primary_rectangle)
                        setTextColor(resources.getColor(R.color.colorAccent))
                    }
                    tvDebt.apply {
                        setBackgroundResource(R.drawable.rounded_gray_rectangle)
                        setTextColor(resources.getColor(R.color.cardText))
                    }
                }
                else -> {
                    tvIncome.apply {
                        setBackgroundResource(R.drawable.rounded_gray_rectangle)
                        setTextColor(resources.getColor(R.color.cardText))
                    }
                    tvOutcome.apply {
                        setBackgroundResource(R.drawable.rounded_gray_rectangle)
                        setTextColor(resources.getColor(R.color.cardText))
                    }
                    tvDebt.apply {
                        setBackgroundResource(R.drawable.rounded_primary_rectangle)
                        setTextColor(resources.getColor(R.color.colorAccent))
                    }
                }
            }
        }
    }

}