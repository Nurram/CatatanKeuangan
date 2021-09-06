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

    fun getAllRecordCount() = recordDao?.getAllDataCount(walletId)

    fun getAllRecordsDesc(): LiveData<List<Record>>? = recordDao?.getAllDataDesc(walletId)

    fun getAllRecordsAsc(): LiveData<List<Record>>? = recordDao?.getAllDataAsc(walletId)

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

    fun getBalance(): LiveData<Long>? = recordDao?.getBalance(walletId)

    fun getAllIncome(): LiveData<List<Record>>? = recordDao?.getAllIncome(walletId)

    fun getAllExpenses(): LiveData<List<Record>>? = recordDao?.getAllExpenses(walletId)

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