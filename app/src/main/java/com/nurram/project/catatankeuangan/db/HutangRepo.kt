package com.nurram.project.catatankeuangan.db

import android.app.Application
import androidx.lifecycle.LiveData
import android.os.AsyncTask

class HutangRepo(application: Application) {

    private val hutangDb = RecordDb.getDb(application)
    private val hutangDao = hutangDb?.recordDao

    fun getAllHutang(): LiveData<List<Hutang>>? {
        return hutangDao?.getAllDataHutang()
    }

    fun getJumlahHutang(): LiveData<Int>? {
        return hutangDao?.getJumlahHutang()
    }

    fun insertHutang(hutang: Hutang) {
        hutangDao?.let { InsertAsync(it).execute(hutang) }
    }

    fun updateHutang(hutang: Hutang) {
        hutangDao?.let { UpdateAsync(it).execute(hutang) }
    }

    fun deleteAllHutang() {
        hutangDao?.let { DeleteAsync(it, "all").execute() }
    }

    fun deleteHutang(hutang: Hutang) {
        hutangDao?.let { DeleteAsync(it, "").execute(hutang) }
    }

    private class InsertAsync(hutangDAO: RecordDAO) : AsyncTask<Hutang, Void, Void>() {
        private val dao = hutangDAO

        override fun doInBackground(vararg params: Hutang?): Void? {
            params[0]?.let { dao.insertHutang(it) }
            return null
        }
    }

    private class UpdateAsync(hutangDAO: RecordDAO) : AsyncTask<Hutang, Void, Void>() {
        private val dao = hutangDAO

        override fun doInBackground(vararg params: Hutang?): Void? {
            params[0]?.let { dao.updateHutang(it) }
            return null
        }
    }

    private class DeleteAsync(hutangDAO: RecordDAO, val key: String) :
        AsyncTask<Hutang, Void, Void>() {
        private val dao = hutangDAO

        override fun doInBackground(vararg params: Hutang?): Void? {
            if (key == "all") {
                dao.deleteAllHutang()
            } else {
                params[0]?.let { dao.deleteHutang(it) }
            }

            return null
        }
    }
}