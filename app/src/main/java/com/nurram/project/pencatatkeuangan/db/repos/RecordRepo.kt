package com.nurram.project.pencatatkeuangan.db.repos

import android.app.Application
import androidx.lifecycle.LiveData
import com.nurram.project.pencatatkeuangan.db.Record
import com.nurram.project.pencatatkeuangan.db.RecordDb
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

class RecordRepo(
    application: Application,
    private val walletId: String
) {
    private val recordDb = RecordDb.getDb(application)
    private val recordDao = recordDb?.recordDao

    fun getAllRecordCount() = recordDao?.getAllDataCount(walletId)

    fun getAllRecordsDesc(): LiveData<List<Record>>? {
        return recordDao?.getAllDataDesc(walletId)
    }

    fun getAllRecordsAsc(): LiveData<List<Record>>? {
        return recordDao?.getAllDataAsc(walletId)
    }

    fun getFilteredRecord(
        startDate: Date,
        endDate: Date,
        isDesc: Boolean
    ): LiveData<List<Record>>? {
        return if (isDesc) {
            recordDao?.getFilteredRecordDesc(walletId, startDate, endDate)
        } else {
            recordDao?.getFilteredRecordAsc(walletId, startDate, endDate)
        }
    }

    fun getAllIncome(): LiveData<List<Record>>? {
        return recordDao?.getAllIncome(walletId)
    }

    fun getAllExpenses(): LiveData<List<Record>>? {
        return recordDao?.getAllExpenses(walletId)
    }

    fun getTotalExpenses(): LiveData<Int>? {
        return recordDao?.getTotalExpenses(walletId)
    }

    fun getTotalIncome(): LiveData<Int>? {
        return recordDao?.getTotalIncome(walletId)
    }

    fun insertRecord(record: Record) {
        recordDao?.let {
            GlobalScope.launch {
                recordDao.insert(record)
            }
        }
    }

    fun updateRecord(record: Record) {
        recordDao?.let {
            GlobalScope.launch {
                recordDao.update(record)
            }
        }
    }

    fun deleteAllRecord() {
        recordDao?.let {
            GlobalScope.launch {
                recordDao.deleteAll(walletId)
            }
        }
    }

    fun deleteRecord(record: Record) {
        recordDao?.let {
            GlobalScope.launch {
                recordDao.delete(record)
            }
        }
    }
}