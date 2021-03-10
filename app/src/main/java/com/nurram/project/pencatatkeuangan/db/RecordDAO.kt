package com.nurram.project.pencatatkeuangan.db

import androidx.lifecycle.LiveData
import androidx.room.*

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

    @Query("select * from record_table where description = 'expenses' order by id desc")
    fun getAllExpenses(): LiveData<List<Record>>

    @Query("select * from record_table where description = 'income' order by id desc")
    fun getAllIncome(): LiveData<List<Record>>

    @Query("select sum(total) from record_table where description = 'expenses'")
    fun getTotalExpenses(): LiveData<Int>

    @Query("select sum(total) from record_table where description = 'income'")
    fun getTotalIncome(): LiveData<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDebt(debt: Debt)

    @Query("Delete from debt_table")
    fun deleteAllDebt()

    @Delete
    fun deleteDebt(debt: Debt)

    @Update
    fun updateDebt(debt: Debt)

    @Query("select * from debt_table order by id desc")
    fun getAllDataDebt(): LiveData<List<Debt>>

    @Query("select sum(total) from debt_table")
    fun getTotalDebt(): LiveData<Int>
}