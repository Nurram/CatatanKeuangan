package com.nurram.project.catatankeuangan.db

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*

@Dao
interface RecordDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(record: Record)

    @Query("Delete from record_table")
    fun deleteAll()

    @Delete
    fun delete(record: Record)

    @Update
    fun update(record: Record)

    @Query("select * from record_table order by id desc")
    fun getAllData(): LiveData<List<Record>>

    @Query("select * from record_table where keterangan = 'pengeluaran' order by id desc")
    fun getAllPengeluaran(): LiveData<List<Record>>

    @Query("select * from record_table where keterangan = 'pemasukan' order by id desc")
    fun getAllPemasukan(): LiveData<List<Record>>

    @Query("select sum(jumlah) from record_table where keterangan = 'pengeluaran'")
    fun getJumlahPengeluaran(): LiveData<Int>

    @Query("select sum(jumlah) from record_table where keterangan = 'pemasukan'")
    fun getJumlahPemasukan(): LiveData<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertHutang(hutang: Hutang)

    @Query("Delete from hutang_table")
    fun deleteAllHutang()

    @Delete
    fun deleteHutang(hutang: Hutang)

    @Update
    fun updateHutang(hutang: Hutang)

    @Query("select * from hutang_table order by id desc")
    fun getAllDataHutang(): LiveData<List<Hutang>>

    @Query("select sum(jumlah) from hutang_table")
    fun getJumlahHutang(): LiveData<Int>
}