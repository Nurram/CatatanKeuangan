package com.nurram.project.pencatatkeuangan.db.repos

import android.app.Application
import androidx.lifecycle.LiveData
import com.nurram.project.pencatatkeuangan.db.Record
import com.nurram.project.pencatatkeuangan.db.RecordDb
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

    fun getFilteredRecord(
        startDate: Date,
        endDate: Date,
        isDesc: Boolean
    ): LiveData<List<Record>>? =
        if (isDesc) {
            recordDao?.getFilteredRecordDesc(walletId, startDate, endDate)
        } else {
            recordDao?.getFilteredRecordAsc(walletId, startDate, endDate)
        }

    fun getBalance(startDate: Date, endDate: Date): LiveData<Long>? = recordDao?.getBalance(walletId, startDate, endDate)

    fun getAllIncome(startDate: Date, endDate: Date): LiveData<List<Record>>? = recordDao?.getCurrentIncome(walletId, startDate, endDate)

    fun getAllExpenses(startDate: Date, endDate: Date): LiveData<List<Record>>? = recordDao?.getCurrentExpenses(walletId, startDate, endDate)

    fun getTotalCurrentExpenses(startDate: Date, endDate: Date): LiveData<Long>? = recordDao?.getCurrentTotalExpenses(walletId, startDate, endDate)

    fun getTotalCurrentIncome(startDate: Date, endDate: Date): LiveData<Long>? = recordDao?.getCurrentTotalIncome(walletId, startDate, endDate)

    fun getTotalExpenses(): LiveData<Long>? = recordDao?.getTotalExpenses(walletId)

    fun getTotalIncome(): LiveData<Long>? = recordDao?.getTotalIncome(walletId)

    suspend fun insertRecord(record: Record) = recordDao?.insert(record)
//        recordDao?.let {
//            coroutineScope {
//                launch { recordDao.insert(record) }
//            }
//        }

    suspend fun updateRecord(record: Record) = recordDao?.update(record)
//        recordDao?.let {
//            coroutineScope {
//                launch { recordDao.update(record) }
//            }
//        }

    suspend fun deleteAllRecord() = recordDao?.deleteAll(walletId)
//        recordDao?.let {
//            coroutineScope {
//                launch { recordDao.deleteAll(walletId) }
//            }
//        }

    suspend fun deleteRecord(record: Record) = recordDao?.delete(record)
//        recordDao?.let {
//            coroutineScope {
//                launch { recordDao.delete(record) }
//            }
//        }
}