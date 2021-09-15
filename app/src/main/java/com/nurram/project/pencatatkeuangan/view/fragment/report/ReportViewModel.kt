package com.nurram.project.pencatatkeuangan.view.fragment.report

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jjoe64.graphview.series.DataPoint
import com.nurram.project.pencatatkeuangan.db.Record
import com.nurram.project.pencatatkeuangan.db.repos.RecordRepo
import com.nurram.project.pencatatkeuangan.utils.CurrencyFormatter
import com.nurram.project.pencatatkeuangan.utils.DateUtil
import kotlinx.coroutines.launch
import java.util.*

class ReportViewModel(private val recordRepo: RecordRepo) : ViewModel() {
    private val dataPoint = mutableListOf<DataPoint>()
    private var records = mutableListOf<Record>()
    private var dates = mutableListOf<String>()
    private var currentSum = 0L
    private var pos = 0
    private var totalSum = 0L

    fun getAllExpenses(startDate: Date, endDate: Date): LiveData<List<Record>>? = recordRepo.getAllExpenses(startDate, endDate)

    fun getAllIncome(startDate: Date, endDate: Date): LiveData<List<Record>>? = recordRepo.getAllIncome(startDate, endDate)

    fun getMaxIncome(startDate: Date, endDate: Date) = recordRepo.getMaxIncome(startDate, endDate)

    fun getMaxExpense(startDate: Date, endDate: Date) = recordRepo.getMaxExpenses(startDate, endDate)

    fun setGraphData(graphList: List<Record>) {
        resetGraph()
        var currentDateString =
            if(graphList.isNotEmpty()) DateUtil.formatDate(graphList[0].date!!)
            else ""

        records.addAll(graphList)
        Log.d("TAG", "VM GraphList $records")
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

    fun getDataPoint() = dataPoint.toTypedArray()

    fun getRecords(position: Int) = records.filter {
        DateUtil.formatDate(it.date!!) == getDates()[position]
    }.toMutableList()

    private fun getDates() = dates

    fun mapData(records: ArrayList<Record>): List<Record> =
        if (!records.isNullOrEmpty()) {
            var date = DateUtil.formatDate(records[0].date!!)
            records.add(0, Record(type = 1, date = records[0].date))

            var i = 0
            while (i <= records.size - 1) {
                val formattedDate = DateUtil.formatDate(records[i].date!!)

                if (date != formattedDate) {
                    date = formattedDate
                    records.add(i, Record(type = 1, date = records[i].date))
                } else {
                    i++
                }
            }

            records
        } else {
            listOf()
        }

    fun deleteRecord(record: Record) = viewModelScope.launch { recordRepo.deleteRecord(record) }
}