package com.nurram.project.pencatatkeuangan.db

import android.app.Application
import androidx.lifecycle.LiveData
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class RecordRepo(application: Application) {
    private val recordDb = RecordDb.getDb(application)
    private val recordDao = recordDb?.recordDao

    fun getAllRecords(): LiveData<List<Record>>? {
        return recordDao?.getAllData()
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