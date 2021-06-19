package com.nurram.project.pencatatkeuangan.view.activity.graph

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.jjoe64.graphview.LegendRenderer
import com.jjoe64.graphview.series.LineGraphSeries
import com.nurram.project.pencatatkeuangan.R
import com.nurram.project.pencatatkeuangan.databinding.ActivityGraphBinding
import com.nurram.project.pencatatkeuangan.db.Record
import com.nurram.project.pencatatkeuangan.utils.CurrencyFormatter
import com.nurram.project.pencatatkeuangan.utils.DateUtil
import com.nurram.project.pencatatkeuangan.utils.PrefUtil
import com.nurram.project.pencatatkeuangan.view.ViewModelFactory
import com.nurram.project.pencatatkeuangan.view.activity.wallet.WalletActivity
import com.nurram.project.pencatatkeuangan.view.fragment.history.HistoryAdapter

class GraphActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGraphBinding
    private lateinit var viewModel: GraphViewModel
    private lateinit var adapter: HistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGraphBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.graphToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        val pref = PrefUtil(this)
        val walletId = pref.getStringFromPref(WalletActivity.prefKey, "def")
        val factory = ViewModelFactory(application, walletId!!)
        viewModel = ViewModelProvider(this, factory).get(GraphViewModel::class.java)

        adapter = HistoryAdapter(this, null, true) { _, _ -> }

        val spinnerAdapter =
            ArrayAdapter(
                this, android.R.layout.simple_spinner_item,
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
                    if (position == 0) {
                        viewModel.getAllExpenses()?.observe(this@GraphActivity, {
                            it?.let { it1 -> setData(it1, "out") }
                        })
                    } else if (position == 1) {
                        viewModel.getAllIncome()?.observe(this@GraphActivity, {
                            it?.let { it1 -> setData(it1, "in") }
                        })
                    }
                }
            }

            graphRecycler.adapter = adapter
            graphRecycler.layoutManager =
                LinearLayoutManager(this@GraphActivity)
            graphRecycler.setHasFixedSize(true)

            MobileAds.initialize(this@GraphActivity) { }
            val adRequest = AdRequest.Builder().build()

            viewModel.getAllRecordCount()?.observe(this@GraphActivity, {
                if (it > 3) {
                    binding.adView.loadAd(adRequest)
                }
            })
        }
    }

    override fun onStop() {
        super.onStop()
        finish()
    }

    private fun setData(data: List<Record>, whereFrom: String) {
        if (data.isNotEmpty()) {
            initGraph(data, whereFrom)
            adapter.setData(data.toMutableList())
        } else {
            Toast.makeText(
                this@GraphActivity, getString(R.string.data_empty),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initGraph(graphList: List<Record>, whereFrom: String) {
        viewModel.setGraphData(graphList)

        binding.graphTotal.text = "Total: ${viewModel.getTotalSum()}"
        val series = LineGraphSeries(viewModel.getDataPoint())
        series.isDrawDataPoints = true
        series.setAnimated(true)

        if (whereFrom == "in") {
            series.color = ContextCompat.getColor(this, R.color.colorAccent)
            series.title = getString(R.string.income)
        } else {
            series.color = ContextCompat.getColor(this, R.color.colorRed)
            series.title = getString(R.string.expenses)
        }

        series.setOnDataPointTapListener { _, dataPoint1 ->
            var datas = mutableListOf<Record>()
            datas.addAll(viewModel.getRecords())
            datas = datas.filter {
                DateUtil.formatDate(it.date!!) == viewModel.getDates()[dataPoint1.x.toInt()]
            }.toMutableList()

            adapter.setData(datas)
            binding.graphTotal.text =
                "Total: ${CurrencyFormatter.convertAndFormat(dataPoint1.y.toLong())}"
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
