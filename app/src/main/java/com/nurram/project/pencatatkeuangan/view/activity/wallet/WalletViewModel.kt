package com.nurram.project.pencatatkeuangan.view.activity.wallet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nurram.project.pencatatkeuangan.db.Wallet
import com.nurram.project.pencatatkeuangan.db.repos.WalletRepo
import kotlinx.coroutines.launch

class WalletViewModel(private val repo: WalletRepo) : ViewModel() {

    fun insertWallet(wallet: Wallet) = viewModelScope.launch { repo.insert(wallet) }

    fun getWallet() = repo.getWallets()

    fun deleteWallet(wallet: Wallet) = viewModelScope.launch { repo.deleteWallet(wallet) }
}