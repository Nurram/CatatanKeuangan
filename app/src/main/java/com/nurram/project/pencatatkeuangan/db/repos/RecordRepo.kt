package com.nurram.project.pencatatkeuangan.db.repos

import android.app.Application
import androidx.lifecycle.LiveData
import com.nurram.project.pencatatkeuangan.db.Record
import com.nurram.project.pencatatkeuangan.db.RecordDb
import com.nurram.project.pencatatkeuangan.utils.DateUtil
import java.util.*

class RecordRepo(
    application: Application,
    private val walletId: String
) {
    private val recordDb = RecordDb.getDb(application)
    private val recordDao = recordDb?.recordDao

    fun getAllRecordsDesc(startDate: Date, endDate: Date): LiveData<List<Record>>? =
        recordDao?.getAllDataDesc(walletId, startDate, endDate)

    fun getAllRecordsAsc(startDate: Date, endDate: Date): LiveData<List<Record>>? =
        recordDao?.getAllDataAsc(walletId, startDate, endDate)

    fun getFilteredRecordWithDate(
        category: String,
        startDate: Date,
        endDate: Date,
        isDesc: Boolean
    ): LiveData<List<Record>>? =
        if (isDesc) {
            recordDao?.getFilteredRecordWithDateDesc(
                category,
                walletId,
                DateUtil.subtractDay(startDate, -1),
                endDate
            )
        } else {
            recordDao?.getFilteredRecordWithDateAsc(
                category,
                walletId,
                DateUtil.subtractDay(startDate, -1),
                endDate
            )
        }

    fun getFilteredRecord(
        category: String, isDesc: Boolean
    ): LiveData<List<Record>>? =
        if (isDesc) {
            recordDao?.getFilteredRecordDesc(category, walletId)
        } else {
            recordDao?.getFilteredRecordAsc(category, walletId)
        }

    fun getBalance(): LiveData<Long>? = recordDao?.getBalance(walletId)

    fun getAllIncome(startDate: Date, endDate: Date): LiveData<List<Record>>? =
        recordDao?.getCurrentIncome(walletId, startDate, endDate)

    fun getAllDebt(startDate: Date, endDate: Date): LiveData<List<Record>>? =
        recordDao?.getCurrentDebt(walletId, startDate, endDate)

    fun getAllExpenses(startDate: Date, endDate: Date): LiveData<List<Record>>? =
        recordDao?.getCurrentExpenses(walletId, startDate, endDate)

    fun getTotalCurrentExpenses(startDate: Date, endDate: Date): LiveData<Long>? =
        recordDao?.getCurrentTotalExpenses(walletId, startDate, endDate)

    fun getTotalCurrentIncome(startDate: Date, endDate: Date): LiveData<Long>? =
        recordDao?.getCurrentTotalIncome(walletId, startDate, endDate)

    fun getMaxExpenses(startDate: Date, endDate: Date): LiveData<Record>? =
        recordDao?.getMaxExpense(walletId, startDate, endDate)

    fun getMaxIncome(startDate: Date, endDate: Date): LiveData<Record>? =
        recordDao?.getMaxIncome(walletId, startDate, endDate)

    suspend fun insertRecord(record: Record) = recordDao?.insert(record)

    suspend fun moveDebtsToRecord(record: List<Record>) =
        recordDao?.moveDebtsToRecords(walletId, record)

    suspend fun updateRecord(record: Record) = recordDao?.update(record)

    suspend fun deleteRecord(record: Record) = recordDao?.delete(record)

    suspend fun deleteAllRecord() = recordDao?.deleteAll(walletId)
}