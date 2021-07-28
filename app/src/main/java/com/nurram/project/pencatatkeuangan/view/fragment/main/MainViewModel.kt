package com.nurram.project.pencatatkeuangan.view.fragment.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nurram.project.pencatatkeuangan.db.Debt
import com.nurram.project.pencatatkeuangan.db.Record
import com.nurram.project.pencatatkeuangan.db.Wallet
import com.nurram.project.pencatatkeuangan.db.repos.DebtRepo
import com.nurram.project.pencatatkeuangan.db.repos.RecordRepo
import com.nurram.project.pencatatkeuangan.db.repos.WalletRepo
import kotlinx.coroutines.launch

class MainViewModel(
    private val recordRepo: RecordRepo,
    private val debtRepo: DebtRepo,
    private val walletRepo: WalletRepo
) : ViewModel() {

    init {
        viewModelScope.launch { walletRepo.insert(Wallet("def", "Default")) }
    }

    fun getWalletById(id: String) = walletRepo.getWalletById(id)

    fun getAllRecordCount() = recordRepo.getAllRecordCount()

    fun getTotalExpenses(): LiveData<Long>? = recordRepo.getTotalExpenses()

    fun getTotalIncome(): LiveData<Long>? = recordRepo.getTotalIncome()

    fun insertRecord(record: Record) = viewModelScope.launch { recordRepo.insertRecord(record) }

    fun deleteAllRecord() = viewModelScope.launch { recordRepo.deleteAllRecord() }

    fun getTotalDebt(): LiveData<Long>? = debtRepo.getTotalDebt()

    fun insertDebt(debt: Debt) = viewModelScope.launch { debtRepo.insertDebt(debt) }

    fun deleteAllDebt() = viewModelScope.launch { debtRepo.deleteAllDebt() }
}