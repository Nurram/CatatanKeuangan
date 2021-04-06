package com.nurram.project.pencatatkeuangan.view.fragment.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.nurram.project.pencatatkeuangan.db.Debt
import com.nurram.project.pencatatkeuangan.db.DebtRepo
import com.nurram.project.pencatatkeuangan.db.Record
import com.nurram.project.pencatatkeuangan.db.RecordRepo
import com.nurram.project.pencatatkeuangan.utils.DateUtil
import java.util.*

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val recordRepo = RecordRepo(application)
    private val debtRepo = DebtRepo(application)

    fun getAllRecords(isNewest: Boolean): LiveData<List<Record>>? {
        return if (isNewest) {
            recordRepo.getAllRecordsDesc()
        } else {
            recordRepo.getAllRecordsAsc()
        }
    }

    fun getFilteredRecord(
        startDate: Date,
        endDate: Date,
        isDesc: Boolean
    ): LiveData<List<Record>>? =
        recordRepo.getFilteredRecord(DateUtil.subtractDays(startDate, 1), endDate, isDesc)

    fun getAllDebts(isNewest: Boolean): LiveData<List<Debt>>? {
        return if (isNewest) {
            debtRepo.getAllDebtDesc()
        } else {
            debtRepo.getAllDebtAsc()
        }
    }

    fun getFilteredDebt(startDate: Date, endDate: Date, isDesc: Boolean): LiveData<List<Debt>>? =
        debtRepo.getFilteredDebtDesc(DateUtil.subtractDays(startDate, 1), endDate, isDesc)

    fun getTotalExpenses(): LiveData<Int>? {
        return recordRepo.getTotalExpenses()
    }

    fun getTotalDebt(): LiveData<Int>? {
        return debtRepo.getTotalDebt()
    }

    fun getTotalIncome(): LiveData<Int>? {
        return recordRepo.getTotalIncome()
    }

    fun insertRecord(record: Record) {
        recordRepo.insertRecord(record)
    }

    fun updateRecord(record: Record) {
        recordRepo.updateRecord(record)
    }

    fun insertDebt(debt: Debt) {
        return debtRepo.insertDebt(debt)
    }

    fun deleteRecord(record: Record) {
        recordRepo.deleteRecord(record)
    }

    fun deleteDebt(debt: Debt) {
        debtRepo.deleteDebt(debt)
    }

    fun updateDebt(debt: Debt) {
        debtRepo.updateDebt(debt)
    }

    fun deleteAllRecord() {
        recordRepo.deleteAllRecord()
    }

    fun deleteAllDebt() {
        debtRepo.deleteAllDebt()
    }
}