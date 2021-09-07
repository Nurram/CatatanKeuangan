package com.nurram.project.pencatatkeuangan.view.fragment.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nurram.project.pencatatkeuangan.db.Record
import com.nurram.project.pencatatkeuangan.db.repos.RecordRepo
import com.nurram.project.pencatatkeuangan.utils.DateUtil
import kotlinx.coroutines.launch
import java.util.*

class HistoryViewModel(private val recordRepo: RecordRepo) : ViewModel() {

    fun getAllRecords(isNewest: Boolean, startDate: Date, endDate: Date): LiveData<List<Record>>? =
        if (isNewest) {
            recordRepo.getAllRecordsDesc(startDate, endDate)
        } else {
            recordRepo.getAllRecordsAsc(startDate, endDate)
        }

    fun getFilteredRecord(
        startDate: Date, endDate: Date, isDesc: Boolean
    ): LiveData<List<Record>>? {
        val startDateString = DateUtil.formatDate(startDate)
        val endDateString = DateUtil.formatDate(endDate)

        var startDates = startDate

        if (startDateString == endDateString) {
            val startCalendar = Calendar.getInstance()
            startCalendar.time = endDate
            startCalendar.set(Calendar.HOUR_OF_DAY, 0)
            startCalendar.set(Calendar.MINUTE, 0)
            startCalendar.set(Calendar.SECOND, 0)

            startDates = startCalendar.time
        }

        val endCalendar = Calendar.getInstance()
        endCalendar.time = endDate
        endCalendar.set(Calendar.HOUR_OF_DAY, 23)

        val endDates = endCalendar.time
        return recordRepo.getFilteredRecord(startDates, endDates, isDesc)
    }

    fun updateRecord(record: Record) = viewModelScope.launch { recordRepo.updateRecord(record) }

    fun deleteRecord(record: Record) = viewModelScope.launch { recordRepo.deleteRecord(record) }

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
}