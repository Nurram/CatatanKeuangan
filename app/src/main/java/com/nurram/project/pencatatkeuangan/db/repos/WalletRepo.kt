package com.nurram.project.pencatatkeuangan.db.repos

import android.app.Application
import com.nurram.project.pencatatkeuangan.db.RecordDb
import com.nurram.project.pencatatkeuangan.db.Wallet

class WalletRepo(application: Application) {
    private val db = RecordDb.getDb(application)
    private val walletDao = db?.walletDao
    private val recordDao = db?.recordDao

    suspend fun insert(wallet: Wallet) = walletDao?.insert(wallet)

    suspend fun update(wallet: Wallet) = walletDao?.update(wallet)

    fun getWallets() = walletDao?.getWallets()

    fun getWalletById(id: String) = walletDao?.getWalletById(id)

    suspend fun deleteWallet(wallet: Wallet) {
        walletDao?.deleteWallet(wallet)
        recordDao?.deleteWalletDataFromRecord(wallet.id)
        recordDao?.deleteWalletDataFromDebt(wallet.id)
    }
}