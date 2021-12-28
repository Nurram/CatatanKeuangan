package com.nurram.project.pencatatkeuangan.view.activity.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nurram.project.pencatatkeuangan.db.Record
import com.nurram.project.pencatatkeuangan.db.repos.DebtRepo
import com.nurram.project.pencatatkeuangan.db.repos.RecordRepo
import com.nurram.project.pencatatkeuangan.db.repos.WalletRepo
import kotlinx.coroutines.launch

class MainActivityViewModel(
    private val walletRepo: WalletRepo,
    private val recordRepo: RecordRepo,
    private val debtRepo: DebtRepo
) : ViewModel() {

    fun getWalletById(id: String) = walletRepo.getWalletById(id)

    fun getAllDebts() = debtRepo.getAllDebtAsc()

    fun moveDebtsToRecord(records: List<Record>) =
        viewModelScope.launch { recordRepo.moveDebtsToRecord(records) }
}