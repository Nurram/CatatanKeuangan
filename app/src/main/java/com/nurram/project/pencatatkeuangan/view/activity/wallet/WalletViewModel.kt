package com.nurram.project.pencatatkeuangan.view.activity.wallet

import androidx.lifecycle.ViewModel
import com.nurram.project.pencatatkeuangan.db.Wallet
import com.nurram.project.pencatatkeuangan.db.repos.WalletRepo

class WalletViewModel(private val repo: WalletRepo) : ViewModel() {

    fun insertWallet(wallet: Wallet) = repo.insert(wallet)
    fun getWallet() = repo.getWallets()
    fun deleteWallet(wallet: Wallet) = repo.deleteWallet(wallet)
}