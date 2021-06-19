package com.nurram.project.pencatatkeuangan.view

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.nurram.project.pencatatkeuangan.db.repos.DebtRepo
import com.nurram.project.pencatatkeuangan.db.repos.RecordRepo
import com.nurram.project.pencatatkeuangan.db.repos.WalletRepo
import com.nurram.project.pencatatkeuangan.view.activity.graph.GraphViewModel
import com.nurram.project.pencatatkeuangan.view.activity.wallet.WalletViewModel
import com.nurram.project.pencatatkeuangan.view.fragment.main.MainViewModel

class ViewModelFactory(
    application: Application,
    walletId: String
) : ViewModelProvider.NewInstanceFactory() {

    private val recordRepo = RecordRepo(application, walletId)
    private val debtRepo = DebtRepo(application, walletId)
    private val walletRepo = WalletRepo(application)

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(recordRepo, debtRepo, walletRepo) as T
            }
            modelClass.isAssignableFrom(GraphViewModel::class.java) -> {
                GraphViewModel(recordRepo) as T
            }
            else -> {
                WalletViewModel(walletRepo) as T
            }
        }
    }
}