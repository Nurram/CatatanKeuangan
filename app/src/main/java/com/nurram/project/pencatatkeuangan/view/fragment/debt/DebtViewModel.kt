package com.nurram.project.pencatatkeuangan.view.fragment.debt

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nurram.project.pencatatkeuangan.db.Debt
import com.nurram.project.pencatatkeuangan.db.Record
import com.nurram.project.pencatatkeuangan.db.repos.DebtRepo
import com.nurram.project.pencatatkeuangan.utils.DateUtil
import kotlinx.coroutines.launch
import java.util.*

class DebtViewModel(private val debtRepo: DebtRepo) : ViewModel() {

    fun getAllDebts(isNewest: Boolean): LiveData<List<Debt>>? =
        if (isNewest) {
            debtRepo.getAllDebtDesc()
        } else {
            debtRepo.getAllDebtAsc()
        }

    fun getFilteredDebt(startDate: Date, endDate: Date, isDesc: Boolean): LiveData<List<Debt>>? =
        debtRepo.getFilteredDebtDesc(DateUtil.subtractDays(startDate, 1), endDate, isDesc)

    fun deleteDebt(debt: Debt) = viewModelScope.launch { debtRepo.deleteDebt(debt) }

    fun updateDebt(debt: Debt) = viewModelScope.launch { debtRepo.updateDebt(debt) }

    fun mapData(debts: ArrayList<Debt>): List<Debt> =
        if (debts.isNotEmpty()) {
            var date = DateUtil.formatDate(debts[0].date!!)
            debts.add(0, Debt(type = 1, date = debts[0].date))

            var i = 0
            while (i <= debts.size - 1) {
                val formattedDate = DateUtil.formatDate(debts[i].date!!)

                if (date != formattedDate) {
                    date = formattedDate
                    debts.add(i, Debt(type = 1, date = debts[i].date))
                } else {
                    i++
                }
            }

            debts
        } else {
            listOf()
        }
}