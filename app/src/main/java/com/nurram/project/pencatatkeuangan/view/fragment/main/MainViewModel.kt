package com.nurram.project.pencatatkeuangan.view.fragment.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nurram.project.pencatatkeuangan.db.Record
import com.nurram.project.pencatatkeuangan.db.Wallet
import com.nurram.project.pencatatkeuangan.db.repos.DebtRepo
import com.nurram.project.pencatatkeuangan.db.repos.RecordRepo
import com.nurram.project.pencatatkeuangan.db.repos.WalletRepo
import com.nurram.project.pencatatkeuangan.utils.DateUtil
import kotlinx.coroutines.launch
import java.util.*

class MainViewModel(
    private val recordRepo: RecordRepo,
    private val debtRepo: DebtRepo,
    private val walletRepo: WalletRepo
) : ViewModel() {

    init {
        viewModelScope.launch { walletRepo.insert(Wallet("def", "Default")) }
    }

    fun getAllRecords(isNewest: Boolean, startDate: Date, endDate: Date): LiveData<List<Record>>? =
        if (isNewest) {
            recordRepo.getAllRecordsDesc(startDate, endDate)
        } else {
            recordRepo.getAllRecordsAsc(startDate, endDate)
        }

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

    fun getBalance(): LiveData<Long>? = recordRepo.getBalance()

    fun getCurrentTotalExpenses(startDate: Date, endDate: Date): LiveData<Long>? =
        recordRepo.getTotalCurrentExpenses(startDate, endDate)

    fun getCurrentTotalIncome(startDate: Date, endDate: Date): LiveData<Long>? =
        recordRepo.getTotalCurrentIncome(startDate, endDate)

    fun getFilteredRecordWithDate(
        category: String, startDate: Date, endDate: Date, isDesc: Boolean
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
        return recordRepo.getFilteredRecordWithDate(category, startDates, endDates, isDesc)
    }

    fun getFilteredRecord(category: String, isDesc: Boolean): LiveData<List<Record>>? =
        recordRepo.getFilteredRecord(category, isDesc)
}