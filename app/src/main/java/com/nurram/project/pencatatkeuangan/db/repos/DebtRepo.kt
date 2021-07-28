package com.nurram.project.pencatatkeuangan.db.repos

import android.app.Application
import androidx.lifecycle.LiveData
import com.nurram.project.pencatatkeuangan.db.Debt
import com.nurram.project.pencatatkeuangan.db.RecordDb
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.util.*

class DebtRepo(
    application: Application,
    private val walletId: String
) {

    private val debtDb = RecordDb.getDb(application)
    private val debtDao = debtDb?.recordDao

    fun getAllDebtDesc(): LiveData<List<Debt>>? = debtDao?.getAllDataDebtDesc(walletId)

    fun getAllDebtAsc(): LiveData<List<Debt>>? = debtDao?.getAllDataDebtAsc(walletId)

    fun getFilteredDebtDesc(
        startDate: Date,
        endDate: Date,
        isDesc: Boolean
    ): LiveData<List<Debt>>? =
        if (isDesc) {
            debtDao?.getFilteredDebtDesc(walletId, startDate, endDate)
        } else {
            debtDao?.getFilteredDebtAsc(walletId, startDate, endDate)
        }

    fun getTotalDebt(): LiveData<Long>? = debtDao?.getTotalDebt(walletId)

    suspend fun insertDebt(debt: Debt) = debtDao?.insertDebt(debt)
//        debtDao?.let {
//            coroutineScope {
//                launch { debtDao.insertDebt(debt) }
//            }
//        }

    suspend fun updateDebt(debt: Debt) = debtDao?.updateDebt(debt)
//        debtDao?.let {
//            coroutineScope {
//                launch { debtDao.updateDebt(debt) }
//            }
//        }

    suspend fun deleteAllDebt() = debtDao?.deleteAllDebt(walletId)
//        debtDao?.let {
//            coroutineScope {
//                launch { debtDao.deleteAll(walletId) }
//            }
//        }

    suspend fun deleteDebt(debt: Debt) = debtDao?.deleteDebt(debt)
//        debtDao?.let {
//            coroutineScope {
//                launch { debtDao.deleteDebt(debt) }
//            }
//        }
}