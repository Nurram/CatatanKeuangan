package com.nurram.project.pencatatkeuangan.db.repos

import android.app.Application
import com.nurram.project.pencatatkeuangan.db.RecordDb
import com.nurram.project.pencatatkeuangan.db.Wallet
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class WalletRepo(application: Application) {
    private val db = RecordDb.getDb(application)
    private val dao = db?.walletDao
    private val recordDao = db?.recordDao

    fun insert(wallet: Wallet) = dao?.let {
        GlobalScope.launch { it.insert(wallet) }
    }

    fun getWallets() = dao?.getWallets()
    fun getWalletById(id: String) = dao?.getWalletById(id)
    fun deleteWallet(wallet: Wallet) = dao?.let {
        GlobalScope.launch {
            it.deleteWallet(wallet)
            recordDao?.deleteWalletDataFromRecord(wallet.id)
            recordDao?.deleteWalletDataFromDebt(wallet.id)
        }
    }
}