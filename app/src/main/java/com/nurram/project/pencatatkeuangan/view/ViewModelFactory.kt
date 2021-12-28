package com.nurram.project.pencatatkeuangan.view

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.nurram.project.pencatatkeuangan.db.repos.DebtRepo
import com.nurram.project.pencatatkeuangan.db.repos.RecordRepo
import com.nurram.project.pencatatkeuangan.db.repos.WalletRepo
import com.nurram.project.pencatatkeuangan.view.activity.add.AddDataViewModel
import com.nurram.project.pencatatkeuangan.view.activity.main.MainActivityViewModel
import com.nurram.project.pencatatkeuangan.view.activity.wallet.WalletViewModel
import com.nurram.project.pencatatkeuangan.view.fragment.main.MainViewModel
import com.nurram.project.pencatatkeuangan.view.fragment.report.ReportViewModel

class ViewModelFactory(
    application: Application,
    walletId: String
) : ViewModelProvider.NewInstanceFactory() {

    private val recordRepo = RecordRepo(application, walletId)
    private val debtRepo = DebtRepo(application, walletId)
    private val walletRepo = WalletRepo(application)

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainActivityViewModel::class.java) -> {
                MainActivityViewModel(walletRepo, recordRepo, debtRepo) as T
            }
            modelClass.isAssignableFrom(AddDataViewModel::class.java) -> {
                AddDataViewModel(recordRepo) as T
            }
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(recordRepo, debtRepo, walletRepo) as T
            }
            modelClass.isAssignableFrom(ReportViewModel::class.java) -> {
                ReportViewModel(recordRepo) as T
            }
            else -> {
                WalletViewModel(walletRepo) as T
            }
        }
    }
}