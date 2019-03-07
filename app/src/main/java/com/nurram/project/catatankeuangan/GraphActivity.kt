package com.nurram.project.catatankeuangan

import android.Manifest
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.ajts.androidmads.library.SQLiteToExcel
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.MobileAds
import com.jjoe64.graphview.LegendRenderer
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import com.nurram.project.catatankeuangan.db.Record
import com.nurram.project.catatankeuangan.utils.CurencyFormatter
import com.nurram.project.catatankeuangan.utils.RiwayatAdapter
import kotlinx.android.synthetic.main.activity_graph.*
import kotlinx.android.synthetic.main.graph_dialog_layout.view.*

class GraphActivity : AppCompatActivity() {
    private lateinit var viewModel: GraphViewModel
    private lateinit var adapter: RiwayatAdapter
    private lateinit var adsRequest: AdRequest
    private lateinit var excelConverter: SQLiteToExcel
    private lateinit var interstitialAd: InterstitialAd

    private val directoryPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).path
    private val tableList = arrayListOf("record_table", "hutang_table")
    private var permissionGranted = false
    private var alreadyConverted = false

    private val dataPoint = mutableListOf<DataPoint>()
    private val recordListTemp = mutableListOf<Record>()
    private val recordList = mutableListOf<Record>()
    private val adapterData = mutableListOf<GraphModel>()
    private val datas = mutableListOf<Record>()
    private var records = mutableListOf<Record>()
    private var date = ""
    private var currentSum = 0
    private var pos = 0
    private var currentDate = ""
    private var limit = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_graph)
        setSupportActionBar(graph_toolbar)
        supportActionBar?.title = null
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        adsRequest = AdRequest.Builder().build()

        interstitialAd = InterstitialAd(this)
        interstitialAd.adUnitId = "ca-app-pub-7752391421212005/8475217550"
        interstitialAd.loadAd(adsRequest)

        MobileAds.initialize(this, "ca-app-pub-7752391421212005~2206162997")
        graph_adView.loadAd(adsRequest)

        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)

        excelConverter = SQLiteToExcel(this, "record_db", directoryPath)
        viewModel = ViewModelProviders.of(this).get(GraphViewModel::class.java)

        val incomeList = mutableListOf<Record>()
        val outcomeList = mutableListOf<Record>()

        val spinnerAdapter =
            ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, arrayOf(
                    getString(R.string.pengeluaran), getString(R.string.pemasukan)
                )
            )
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        graph_spinner.adapter = spinnerAdapter
        graph_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                if (position == 0 && outcomeList.size >= 2) {
                    graph_spinner.isClickable = true
                    initGraph(outcomeList, "out")
                    adapter.setData(outcomeList)
                } else if (position == 1 && incomeList.size >= 2) {
                    initGraph(incomeList, "in")
                    adapter.setData(incomeList)
                } else if (incomeList.size < 2 && outcomeList.size < 2) {
                    showDialog()
                } else if (incomeList.size < 2 || outcomeList.size < 2) {
                    Toast.makeText(this@GraphActivity, getString(R.string.spinner_peringatan_data), Toast.LENGTH_SHORT)
                        .show()
                }
            }

        }

        viewModel.getAllPengeluaran()?.observe(this, Observer {
            it?.let { it1 -> outcomeList.addAll(it1) }
        })

        viewModel.getAllPemasukan()?.observe(this, Observer {
            it?.let { it1 -> incomeList.addAll(it1) }
        })

        adapter = RiwayatAdapter(this, null, true) {}

        graph_recycler.adapter = adapter
        graph_recycler.layoutManager = LinearLayoutManager(this)
        graph_recycler.setHasFixedSize(true)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            1 -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                permissionGranted = true
            } else {
                Toast.makeText(this, getString(R.string.konversi_excel_ditolak), Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onStop() {
        super.onStop()
        finish()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.graph, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.convert_excel -> showConvertDialog()
            R.id.graph_ad_toolbar -> if (interstitialAd.isLoaded) {
                interstitialAd.show()
            } else {
                Toast.makeText(this, getString(R.string.tidak_ada_iklan), Toast.LENGTH_SHORT).show()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun initGraph(graphList: List<Record>, whereFrom: String) {
        resetGraph()
        var totalSum = 0
        graph_total.text = "Total: "

        currentDate = graphList[0].tanggal
        limit = graphList.size

        if (limit > 31) {
            limit = 31
        }

        graphList.asReversed().takeLast(limit).forEach {
            totalSum += it.jumlah

            if (currentDate == it.tanggal) {
                currentSum += it.jumlah
                recordListTemp.add(it)
            } else {
                recordList.addAll(recordListTemp)
                adapterData.add(GraphModel(currentDate, recordList))
                dataPoint.add(DataPoint(pos.toDouble(), currentSum.toDouble()))
                currentDate = it.tanggal
                currentSum = it.jumlah
                recordListTemp.clear()
                recordListTemp.add(it)
                pos++
            }
        }

        dataPoint.add(DataPoint(pos.toDouble(), currentSum.toDouble()))
        dataPoint.removeAt(0)
        adapterData.add(GraphModel(currentDate, recordListTemp))
        graph_total.append(CurencyFormatter.convertAndFormat(totalSum))

        val series = LineGraphSeries<DataPoint>(dataPoint.toTypedArray())
        series.isDrawDataPoints = true
        series.setAnimated(true)

        if (whereFrom == "in") {
            series.color = ContextCompat.getColor(this, R.color.colorPrimary)
            series.title = getString(R.string.pemasukan)
        } else {
            series.color = ContextCompat.getColor(this, R.color.colorRed)
            series.title = getString(R.string.pengeluaran)
        }


        series.setOnDataPointTapListener { _, dataPoint1 ->
            totalSum = 0
            graph_total.text = "Total: "

            records = adapterData[dataPoint1.x.toInt()].records
            date = adapterData[dataPoint1.x.toInt()].date
            datas.clear()

            records.forEach {
                totalSum += it.jumlah
                if (it.tanggal == date) {
                    datas.add(it)
                }
            }

            adapter.setData(datas)
            graph_total.append(CurencyFormatter.convertAndFormat(totalSum))
        }

        graph_chart.addSeries(series)
        graph_chart.gridLabelRenderer.textSize = 16f
        graph_chart.viewport.isXAxisBoundsManual = true
        graph_chart.viewport.setMaxX(31.0)
        graph_chart.legendRenderer.isVisible = true
        graph_chart.legendRenderer.align = LegendRenderer.LegendAlign.BOTTOM
        graph_chart.legendRenderer.backgroundColor = android.R.color.transparent
        graph_chart.legendRenderer.textSize = 24f
    }

    private fun resetGraph() {
        graph_chart.removeAllSeries()
        dataPoint.clear()
        recordListTemp.clear()
        adapterData.clear()
        recordList.clear()
        currentSum = 0
        pos = 0
    }

    private fun showDialog() {
        val dialog = AlertDialog.Builder(this)
        val view = layoutInflater.inflate(R.layout.graph_dialog_layout, null)
        view.graph_dialog_adView.loadAd(adsRequest)

        dialog.setTitle(getString(R.string.perhatian))
        dialog.setMessage(R.string.kamu_belum_memiliki_data_dalam_2_hari_atau_lebih)
        dialog.setCancelable(false)
        dialog.setPositiveButton(getString(R.string.mengerti_bawa_kembali)) { _, _ ->
            finish()
        }

        dialog.show()
    }

    private fun showConvertDialog() {
        val dialog = AlertDialog.Builder(this)
        val view = layoutInflater.inflate(R.layout.graph_dialog_layout, null)
        view.graph_dialog_adView.loadAd(adsRequest)

        dialog.setTitle(getString(R.string.perhatian))
        dialog.setMessage(getString(R.string.lakukan_konversi))
        dialog.setCancelable(true)
        dialog.setPositiveButton("Yes") { _, _ ->
            if (permissionGranted) {
                if (alreadyConverted) {
                    Toast.makeText(this, getString(R.string.data_sudah_pernah_dikonversi), Toast.LENGTH_LONG).show()
                } else {
                    convertDbToExcel()
                }
            } else {
                Toast.makeText(this, getString(R.string.konversi_tidak_diijinkan), Toast.LENGTH_SHORT).show()
            }
        }
        dialog.setNegativeButton("Cancel") { dialog1, _ ->
            dialog1.dismiss()
        }

        dialog.show()
    }

    private fun convertDbToExcel() {
        excelConverter.exportSpecificTables(tableList, "Catatan keuangan.xls", object : SQLiteToExcel.ExportListener {
            override fun onError(e: Exception?) {
                Toast.makeText(this@GraphActivity, e?.message, Toast.LENGTH_SHORT).show()
            }

            override fun onStart() {
                Toast.makeText(this@GraphActivity, getString(R.string.mulai_konversi), Toast.LENGTH_SHORT).show()
            }

            override fun onCompleted(filePath: String?) {
                Toast.makeText(this@GraphActivity, getString(R.string.selesai_konversi), Toast.LENGTH_SHORT).show()
                alreadyConverted = true
            }
        })
    }
}
