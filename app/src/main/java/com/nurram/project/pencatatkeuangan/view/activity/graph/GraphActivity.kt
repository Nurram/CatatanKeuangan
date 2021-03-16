package com.nurram.project.pencatatkeuangan.view.activity.graph

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.ajts.androidmads.library.SQLiteToExcel
import com.jjoe64.graphview.LegendRenderer
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import com.nurram.project.pencatatkeuangan.model.GraphModel
import com.nurram.project.pencatatkeuangan.R
import com.nurram.project.pencatatkeuangan.databinding.ActivityGraphBinding
import com.nurram.project.pencatatkeuangan.db.Record
import com.nurram.project.pencatatkeuangan.utils.CurencyFormatter
import com.nurram.project.pencatatkeuangan.utils.DateUtil
import com.nurram.project.pencatatkeuangan.view.fragment.history.HistoryAdapter

class GraphActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGraphBinding
    private lateinit var viewModel: GraphViewModel
    private lateinit var adapter: HistoryAdapter
    private lateinit var excelConverter: SQLiteToExcel

    private val directoryPath =
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).path
    private val tableList = arrayListOf("record_table", "debt_table")
    private var permissionGranted = false
    private var alreadyConverted = false

    private val dataPoint = mutableListOf<DataPoint>()
    private val recordListTemp = mutableListOf<Record>()
    private val recordList = mutableListOf<Record>()
    private val adapterData = mutableListOf<GraphModel>()
    private val datas = mutableListOf<Record>()
    private var records = mutableListOf<Record>()
    private var date = ""
    private var currentSum = 0L
    private var pos = 0
    private var currentDateString = ""
    private var limit = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGraphBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.graphToolbar)
        supportActionBar?.title = null
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            1
        )

        excelConverter = SQLiteToExcel(this, "record_db", directoryPath)

        val incomeList = mutableListOf<Record>()
        val outcomeList = mutableListOf<Record>()
        viewModel = ViewModelProvider(this).get(GraphViewModel::class.java)
        viewModel.getAllExpenses()?.observe(this, {
            it?.let { it1 -> outcomeList.addAll(it1) }
        })

        viewModel.getAllIncome()?.observe(this, {
            it?.let { it1 -> incomeList.addAll(it1) }
        })

        adapter = HistoryAdapter(this, null, true) { _, _ -> }

        val spinnerAdapter =
            ArrayAdapter(
                this, android.R.layout.simple_spinner_item, arrayOf(
                    getString(R.string.expenses), getString(R.string.income)
                )
            )
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
       binding.apply {
           graphSpinner.adapter = spinnerAdapter
           graphSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
               override fun onNothingSelected(parent: AdapterView<*>?) {}

               override fun onItemSelected(
                   parent: AdapterView<*>?,
                   view: View?,
                   position: Int,
                   id: Long
               ) {
                   if (position == 0 && outcomeList.size >= 2) {
                       graphSpinner.isClickable = true
                       initGraph(outcomeList, "out")
                       adapter.setData(outcomeList)
                   } else if (position == 1 && incomeList.size >= 2) {
                       initGraph(incomeList, "in")
                       adapter.setData(incomeList)
                   } else if (incomeList.size < 2 && outcomeList.size < 2) {
                       showDialog()
                   } else if (incomeList.size < 2 || outcomeList.size < 2) {
                       Toast.makeText(
                           this@GraphActivity,
                           getString(R.string.spinner_peringatan_data),
                           Toast.LENGTH_SHORT
                       )
                           .show()
                   }
               }

           }

           graphRecycler.adapter = adapter
           graphRecycler.layoutManager =
               LinearLayoutManager(this@GraphActivity)
           graphRecycler.setHasFixedSize(true)
       }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            1 -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                permissionGranted = true
            } else {
                Toast.makeText(this, getString(R.string.konversi_excel_ditolak), Toast.LENGTH_LONG)
                    .show()
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.convert_excel -> showConvertDialog()
        }

        return true
    }

    private fun initGraph(graphList: List<Record>, whereFrom: String) {
        resetGraph()
        var totalSum = 0L
        binding.graphTotal.text = getString(R.string.total)

        val currentDate = graphList[0].date
        currentDateString = DateUtil.formatDate(currentDate!!)
        limit = graphList.size

        if (limit > 31) {
            limit = 31
        }

        graphList.asReversed().takeLast(limit).forEach {
            totalSum += it.total
            recordList.addAll(recordListTemp)
            adapterData.add(GraphModel(it.date!!, recordList))
            dataPoint.add(DataPoint(pos.toDouble(), currentSum.toDouble()))
            currentDateString = DateUtil.formatDate(it.date!!)
            currentSum = it.total
            recordListTemp.clear()
            recordListTemp.add(it)
            pos++
        }

        dataPoint.add(DataPoint(pos.toDouble(), currentSum.toDouble()))
        dataPoint.removeAt(0)
        adapterData.add(GraphModel(currentDate, recordListTemp))
        binding.graphTotal.append(CurencyFormatter.convertAndFormat(totalSum))

        val series = LineGraphSeries(dataPoint.toTypedArray())
        series.isDrawDataPoints = true
        series.setAnimated(true)

        if (whereFrom == "in") {
            series.color = ContextCompat.getColor(this, R.color.colorPrimary)
            series.title = getString(R.string.income)
        } else {
            series.color = ContextCompat.getColor(this, R.color.colorRed)
            series.title = getString(R.string.expenses)
        }


        series.setOnDataPointTapListener { _, dataPoint1 ->
            totalSum = 0
            binding.graphTotal.text = getString(R.string.total)

            records = adapterData[dataPoint1.x.toInt()].records
            date = DateUtil.formatDate(adapterData[dataPoint1.x.toInt() - 1].date)
            datas.clear()

            records.forEach {
                val txDate = DateUtil.formatDate(it.date!!)
                if (txDate == date) {
                    totalSum += it.total
                    datas.add(it)
                }
            }

            adapter.setData(datas)
            binding.graphTotal.append(CurencyFormatter.convertAndFormat(totalSum))
        }

        binding.graphChart.apply {
            addSeries(series)
            gridLabelRenderer.textSize = 16f
            viewport.isXAxisBoundsManual = true
            viewport.setMaxX(31.0)
            legendRenderer.isVisible = true
            legendRenderer.align = LegendRenderer.LegendAlign.BOTTOM
            legendRenderer.backgroundColor = android.R.color.transparent
            legendRenderer.textSize = 24f
        }
    }

    private fun resetGraph() {
        binding.graphChart.removeAllSeries()
        dataPoint.clear()
        recordListTemp.clear()
        adapterData.clear()
        recordList.clear()
        currentSum = 0
        pos = 0
    }

    private fun showDialog() {
        val dialog = AlertDialog.Builder(this)

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

        dialog.setTitle(getString(R.string.perhatian))
        dialog.setMessage(getString(R.string.lakukan_konversi))
        dialog.setCancelable(true)
        dialog.setPositiveButton("Yes") { _, _ ->
            if (permissionGranted) {
                if (alreadyConverted) {
                    Toast.makeText(
                        this,
                        getString(R.string.data_sudah_pernah_dikonversi),
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    convertDbToExcel()
                }
            } else {
                Toast.makeText(
                    this,
                    getString(R.string.konversi_tidak_diijinkan),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        dialog.setNegativeButton("Cancel") { dialog1, _ ->
            dialog1.dismiss()
        }

        dialog.show()
    }

    private fun convertDbToExcel() {
        excelConverter.exportSpecificTables(
            tableList,
            "Catatan keuangan.xls",
            object : SQLiteToExcel.ExportListener {
                override fun onError(e: Exception?) {
                    Toast.makeText(this@GraphActivity, e?.message, Toast.LENGTH_SHORT).show()
                }

                override fun onStart() {
                    Toast.makeText(
                        this@GraphActivity,
                        getString(R.string.mulai_konversi),
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onCompleted(filePath: String?) {
                    Toast.makeText(
                        this@GraphActivity,
                        getString(R.string.selesai_konversi),
                        Toast.LENGTH_SHORT
                    ).show()
                    alreadyConverted = true
                }
            })
    }
}
