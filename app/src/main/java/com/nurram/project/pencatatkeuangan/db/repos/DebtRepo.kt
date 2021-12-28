package com.nurram.project.pencatatkeuangan.db.repos

import android.app.Application
import androidx.lifecycle.LiveData
import com.nurram.project.pencatatkeuangan.db.Debt
import com.nurram.project.pencatatkeuangan.db.RecordDb

class DebtRepo(
    application: Application,
    private val walletId: String
) {

    private val debtDb = RecordDb.getDb(application)
    private val debtDao = debtDb?.recordDao

    fun getAllDebtAsc(): LiveData<List<Debt>>? = debtDao?.getAllDataDebtAsc(walletId)
}