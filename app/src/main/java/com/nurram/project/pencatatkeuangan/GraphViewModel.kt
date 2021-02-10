package com.nurram.project.pencatatkeuangan

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.nurram.project.pencatatkeuangan.db.Record
import com.nurram.project.pencatatkeuangan.db.RecordRepo

class GraphViewModel(application: Application) : AndroidViewModel(application) {
    private val recordRepo = RecordRepo(application)

    fun getAllPengeluaran(): LiveData<List<Record>>? {
        return recordRepo.getAllPengeluaran()
    }

    fun getAllPemasukan(): LiveData<List<Record>>? {
        return recordRepo.getAllPemasukan()
    }
}