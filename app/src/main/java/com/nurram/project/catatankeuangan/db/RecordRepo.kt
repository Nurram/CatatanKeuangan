package com.nurram.project.catatankeuangan.db

import android.app.Application
import androidx.lifecycle.LiveData
import android.os.AsyncTask

class RecordRepo(application: Application) {
    private val recordDb = RecordDb.getDb(application)
    private val recordDao = recordDb?.recordDao

    fun getAllRecords(): LiveData<List<Record>>? {
        return recordDao?.getAllData()
    }

    fun getAllPemasukan(): LiveData<List<Record>>? {
        return recordDao?.getAllPemasukan()
    }

    fun getAllPengeluaran(): LiveData<List<Record>>? {
        return recordDao?.getAllPengeluaran()
    }

    fun getJumlahPengeluaran(): LiveData<Int>? {
        return recordDao?.getJumlahPengeluaran()
    }

    fun getJumlahPemasukan(): LiveData<Int>? {
        return recordDao?.getJumlahPemasukan()
    }

    fun insertRecord(record: Record) {
        recordDao?.let { InsertAsync(it).execute(record) }
    }

    fun updateRecord(record: Record) {
        recordDao?.let { UpdateAsync(it).execute(record) }
    }

    fun deleteAllRecord() {
        recordDao?.let { DeleteAsync(it, "all").execute() }
    }

    fun deleteRecord(record: Record) {
        recordDao?.let { DeleteAsync(it, "").execute(record) }
    }

    private class InsertAsync(recordDAO: RecordDAO) : AsyncTask<Record, Void, Void>() {
        private val dao = recordDAO

        override fun doInBackground(vararg params: Record?): Void? {
            params[0]?.let { dao.insert(it) }
            return null
        }
    }

    private class UpdateAsync(recordDAO: RecordDAO) : AsyncTask<Record, Void, Void>() {
        private val dao = recordDAO

        override fun doInBackground(vararg params: Record?): Void? {
            params[0]?.let { dao.update(it) }
            return null
        }
    }

    private class DeleteAsync(recordDAO: RecordDAO, val key: String) :
        AsyncTask<Record, Void, Void>() {
        private val dao = recordDAO

        override fun doInBackground(vararg params: Record?): Void? {
            if (key == "all") {
                dao.deleteAll()
            } else {
                params[0]?.let { dao.delete(it) }
            }

            return null
        }
    }
}