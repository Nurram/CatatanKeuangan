package com.nurram.project.pencatatkeuangan.view.activity.graph

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.jjoe64.graphview.series.DataPoint
import com.nurram.project.pencatatkeuangan.db.Record
import com.nurram.project.pencatatkeuangan.db.RecordRepo
import com.nurram.project.pencatatkeuangan.utils.CurrencyFormatter
import com.nurram.project.pencatatkeuangan.utils.DateUtil

class GraphViewModel(application: Application) : AndroidViewModel(application) {
    private val recordRepo = RecordRepo(application)
    private val dataPoint = mutableListOf<DataPoint>()
    private var records = mutableListOf<Record>()
    private var dates = mutableListOf<String>()
    private var currentSum = 0L
    private var pos = 0
    private var totalSum = 0L

    fun getAllExpenses(): LiveData<List<Record>>? {
        return recordRepo.getAllExpenses()
    }

    fun getAllIncome(): LiveData<List<Record>>? {
        return recordRepo.getAllIncome()
    }

    fun setGraphData(graphList: List<Record>) {
        resetGraph()

        var currentDateString = DateUtil.formatDate(graphList[0].date!!)

        records.addAll(graphList)
        records.forEach {
            val recordDate = DateUtil.formatDate(it.date!!)
            totalSum += it.total

            if (recordDate !in dates) {
                dates.add(recordDate)
            }

            if (recordDate != currentDateString) {
                currentDateString = recordDate
                dataPoint.add(DataPoint(pos.toDouble(), currentSum.toDouble()))

                pos++
                currentSum = it.total
            } else {
                currentSum += it.total
            }
        }

        dataPoint.add(DataPoint(pos.toDouble(), currentSum.toDouble()))
    }

    private fun resetGraph() {
        dataPoint.clear()
        records.clear()
        dates.clear()
        currentSum = 0
        totalSum = 0
        pos = 0
    }

    fun getTotalSum(): String = CurrencyFormatter.convertAndFormat(totalSum)
    fun getDataPoint() = dataPoint.toTypedArray()
    fun getRecords() = records
    fun getDates() = dates
}