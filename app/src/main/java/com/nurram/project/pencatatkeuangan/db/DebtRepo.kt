package com.nurram.project.pencatatkeuangan.db

import android.app.Application
import androidx.lifecycle.LiveData
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

class DebtRepo(application: Application) {

    private val debtDb = RecordDb.getDb(application)
    private val debtDao = debtDb?.recordDao

    fun getAllDebtDesc(): LiveData<List<Debt>>? {
        return debtDao?.getAllDataDebtDesc()
    }

    fun getAllDebtAsc(): LiveData<List<Debt>>? {
        return debtDao?.getAllDataDebtAsc()
    }

    fun getFilteredDebtDesc(startDate: Date, endDate: Date, isDesc: Boolean): LiveData<List<Debt>>? {
        return if(isDesc) {
            debtDao?.getFilteredDebtDesc(startDate, endDate)
        } else {
            debtDao?.getFilteredDebtAsc(startDate, endDate)
        }
    }

    fun getTotalDebt(): LiveData<Int>? {
        return debtDao?.getTotalDebt()
    }

    fun insertDebt(debt: Debt) {
        debtDao?.let { GlobalScope.launch {
            debtDao.insertDebt(debt)
            }
        }
    }

    fun updateDebt(debt: Debt) {
        debtDao?.let {  GlobalScope.launch {
            debtDao.updateDebt(debt)
            }
        }
    }

    fun deleteAllDebt() {
        debtDao?.let {
            GlobalScope.launch {
                debtDao.deleteAllDebt()
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