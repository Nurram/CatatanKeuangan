package com.nurram.project.pencatatkeuangan.db.repos

import android.app.Application
import androidx.lifecycle.LiveData
import com.nurram.project.pencatatkeuangan.db.Debt
import com.nurram.project.pencatatkeuangan.db.RecordDb
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

class DebtRepo(
    application: Application,
    private val walletId: String
) {

    private val debtDb = RecordDb.getDb(application)
    private val debtDao = debtDb?.recordDao

    fun getAllDebtDesc(): LiveData<List<Debt>>? {
        return debtDao?.getAllDataDebtDesc(walletId)
    }

    fun getAllDebtAsc(): LiveData<List<Debt>>? {
        return debtDao?.getAllDataDebtAsc(walletId)
    }

    fun getFilteredDebtDesc(
        startDate: Date,
        endDate: Date,
        isDesc: Boolean
    ): LiveData<List<Debt>>? {
        return if (isDesc) {
            debtDao?.getFilteredDebtDesc(walletId, startDate, endDate)
        } else {
            debtDao?.getFilteredDebtAsc(walletId, startDate, endDate)
        }
    }

    fun getTotalDebt(): LiveData<Int>? {
        return debtDao?.getTotalDebt(walletId)
    }

    fun insertDebt(debt: Debt) {
        debtDao?.let {
            GlobalScope.launch {
                debtDao.insertDebt(debt)
            }
        }
    }

    fun updateDebt(debt: Debt) {
        debtDao?.let {
            GlobalScope.launch {
                debtDao.updateDebt(debt)
            }
        }
    }

    fun deleteAllDebt() {
        debtDao?.let {
            GlobalScope.launch {
                debtDao.deleteAllDebt(walletId)
            }
        }
    }

    fun deleteDebt(debt: Debt) {
        debtDao?.let {
            GlobalScope.launch {
                debtDao.deleteDebt(debt)
            }
        }
    }
}