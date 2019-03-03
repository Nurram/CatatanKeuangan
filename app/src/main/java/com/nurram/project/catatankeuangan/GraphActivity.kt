package com.nurram.project.catatankeuangan

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.jjoe64.graphview.LegendRenderer
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import com.nurram.project.catatankeuangan.db.Record
import com.nurram.project.catatankeuangan.utils.RiwayatAdapter
import kotlinx.android.synthetic.main.activity_graph.*

class GraphActivity : AppCompatActivity() {
    private lateinit var viewModel: GraphViewModel
    private lateinit var adapter: RiwayatAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_graph)
        setSupportActionBar(graph_toolbar)
        supportActionBar?.title = null
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        viewModel = ViewModelProviders.of(this).get(GraphViewModel::class.java)
        viewModel.getAllPemasukan()?.observe(this, Observer {
            if (it != null) {
                if (it.size >= 2) {
                    initGraph(it, "pemasukan")
                }
            }
        })

        viewModel.getAllPengeluaran()?.observe(this, Observer {
            if (it != null) {
                if (it.size >= 2) {
                    initGraph(it, "pengeluaran")
                }
            }
        })

        adapter = RiwayatAdapter(this, null, true) {}
        graph_recycler.adapter = adapter
        graph_recycler.layoutManager = LinearLayoutManager(this)
        graph_recycler.setHasFixedSize(true)

        viewModel.getAllRecords()?.observe(this, Observer {
            adapter.setData(it?.toMutableList())
        })
    }

    private fun initGraph(graphList: List<Record>, whatData: String) {
        graph_layout.visibility = View.VISIBLE
        graph_msg.visibility = View.GONE

        val dataPoint = mutableListOf<DataPoint>()
        val adapterData = mutableListOf<Record>()

        var currentDate = graphList[0].tanggal
        var currentSum = 0
        var pos = 0
        var limit = graphList.size

        if (limit > 14) {
            limit = 14
        }

        graphList.takeLast(limit).forEach {
            if (currentDate == it.tanggal) {
                currentSum += it.jumlah
            } else {
                dataPoint.add(DataPoint(pos.toDouble(), currentSum.toDouble()))

                currentDate = it.tanggal
                currentSum = it.jumlah
                pos++
            }
        }

        dataPoint.add(DataPoint(pos.toDouble(), currentSum.toDouble()))

        val series = LineGraphSeries<DataPoint>(dataPoint.toTypedArray())
        series.thickness = 8
        series.isDrawDataPoints = true
        series.setAnimated(true)
        series.setOnDataPointTapListener { _, dataPoint1 ->
            val record = graphList[dataPoint1.x.toInt()]
            adapterData.clear()

            for (list in graphList) {
                if (list.tanggal == record.tanggal) {
                    adapterData.add(record)
                }
            }

            adapter.setData(adapterData)
        }

        if (whatData == "pemasukan") {
            series.color = ContextCompat.getColor(this, R.color.colorPrimary)
            series.title = "Pemasukan"
        } else {
            series.color = ContextCompat.getColor(this, R.color.colorRed)
            series.title = "Pengeluaran"
        }

        graph_chart.addSeries(series)
        graph_chart.gridLabelRenderer.textSize = 16f
        graph_chart.viewport.isXAxisBoundsManual = true
        graph_chart.viewport.setMaxX(14.0)
        graph_chart.legendRenderer.isVisible = true
        graph_chart.legendRenderer.align = LegendRenderer.LegendAlign.BOTTOM
        graph_chart.legendRenderer.backgroundColor = android.R.color.transparent
        graph_chart.legendRenderer.textSize = 24f
    }
}
