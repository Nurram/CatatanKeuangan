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

    @Query("select * from record_table order by tanggal asc")
    fun getAllData(): LiveData<List<Record>>

    @Query("select sum(jumlah) from record_table where keterangan = 'pengeluaran'")
    fun getJumlahPengeluaran(): LiveData<Int>

    @Query("select sum(jumlah) from record_table where keterangan = 'pemasukan'")
    fun getJumlahPemasukan(): LiveData<Int>
}