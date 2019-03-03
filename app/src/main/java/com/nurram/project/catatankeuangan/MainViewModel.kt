package com.nurram.project.catatankeuangan

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import com.nurram.project.catatankeuangan.db.Hutang
import com.nurram.project.catatankeuangan.db.HutangRepo
import com.nurram.project.catatankeuangan.db.Record
import com.nurram.project.catatankeuangan.db.RecordRepo

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val recordRepo = RecordRepo(application)
    private val hutangRepo = HutangRepo(application)

    fun getAllRecords(): LiveData<List<Record>>? {
        return recordRepo.getAllRecords()
    }

    fun getAllHutang(): LiveData<List<Hutang>>? {
        return hutangRepo.getAllHutang()
    }

    fun getJumlahPengeluaran(): LiveData<Int>? {
        return recordRepo.getJumlahPengeluaran()
    }

    fun getJumlahHutang(): LiveData<Int>? {
        return hutangRepo.getJumlahHutang()
    }

    fun getJumlahPemasukan(): LiveData<Int>? {
        return recordRepo.getJumlahPemasukan()
    }

    fun insertRecord(record: Record) {
        recordRepo.insertRecord(record)
    }

    fun insertHutang(hutang: Hutang) {
        return hutangRepo.insertHutang(hutang)
    }

    fun deleteRecord(record: Record) {
        recordRepo.deleteRecord(record)
    }

    fun deleteHutang(hutang: Hutang) {
        hutangRepo.deleteHutang(hutang)
    }

    fun deleteAllRecord() {
        recordRepo.deleteAllRecord()
    }

    fun deleteAllHutang() {
        hutangRepo.deleteAllHutang()
    }
}