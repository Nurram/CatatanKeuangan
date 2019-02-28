package com.nurram.project.catatankeuangan

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import com.nurram.project.catatankeuangan.db.Record
import com.nurram.project.catatankeuangan.db.RecordRepo

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val recordRepo = RecordRepo(application)

    fun getAllDatas(): LiveData<List<Record>>? {
        return recordRepo.getAllRecords()
    }

    fun getJumlahPengeluaran(): LiveData<Int>? {
        return recordRepo.getJumlahPengeluaran()
    }

    fun getJumlahPemasukan(): LiveData<Int>? {
        return recordRepo.getJumlahPemasukan()
    }

    fun insertData(record: Record) {
        recordRepo.insertRecord(record)
    }

    fun deleteData(record: Record){
        recordRepo.deleteRecord(record)
    }

    fun deleteAll() {
        recordRepo.deleteAllRecord()
    }
}