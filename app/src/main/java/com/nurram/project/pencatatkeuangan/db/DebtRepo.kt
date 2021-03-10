package com.nurram.project.pencatatkeuangan.db

import android.app.Application
import androidx.lifecycle.LiveData
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class DebtRepo(application: Application) {

    private val debtDb = RecordDb.getDb(application)
    private val debtDao = debtDb?.recordDao

    fun getAllDebt(): LiveData<List<Debt>>? {
        return debtDao?.getAllDataDebt()
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