package com.nurram.project.pencatatkeuangan.db

import android.app.Application
import androidx.lifecycle.LiveData
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

class RecordRepo(application: Application) {
    private val recordDb = RecordDb.getDb(application)
    private val recordDao = recordDb?.recordDao

    fun getAllRecordCount() = recordDao?.getAllDataCount()

    fun getAllRecordsDesc(): LiveData<List<Record>>? {
        return recordDao?.getAllDataDesc()
    }

    fun getAllRecordsAsc(): LiveData<List<Record>>? {
        return recordDao?.getAllDataAsc()
    }

    fun getFilteredRecord(
        startDate: Date,
        endDate: Date,
        isDesc: Boolean
    ): LiveData<List<Record>>? {
        return if (isDesc) {
            recordDao?.getFilteredRecordDesc(startDate, endDate)
        } else {
            recordDao?.getFilteredRecordAsc(startDate, endDate)
        }
    }

    fun getAllIncome(): LiveData<List<Record>>? {
        return recordDao?.getAllIncome()
    }

    fun getAllExpenses(): LiveData<List<Record>>? {
        return recordDao?.getAllExpenses()
    }

    fun getTotalExpenses(): LiveData<Int>? {
        return recordDao?.getTotalExpenses()
    }

    fun getTotalIncome(): LiveData<Int>? {
        return recordDao?.getTotalIncome()
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
                recordDao.deleteAll()
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