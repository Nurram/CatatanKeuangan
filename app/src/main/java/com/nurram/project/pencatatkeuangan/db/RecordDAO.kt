package com.nurram.project.pencatatkeuangan.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.nurram.project.pencatatkeuangan.db.converter.DateConverter
import java.util.*

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

    @Query("select * from record_table order by date desc")
    fun getAllDataDesc(): LiveData<List<Record>>

    @Query("select * from record_table order by date asc")
    fun getAllDataAsc(): LiveData<List<Record>>

    @TypeConverters(DateConverter::class)
    @Query("select * from record_table where date between :startDate and :endDate order by date desc")
    fun getFilteredRecordDesc(startDate: Date, endDate: Date): LiveData<List<Record>>

    @TypeConverters(DateConverter::class)
    @Query("select * from record_table where date between :startDate and :endDate order by date asc")
    fun getFilteredRecordAsc(startDate: Date, endDate: Date): LiveData<List<Record>>

    @Query("select * from record_table where description = 'expenses' order by date desc")
    fun getAllExpenses(): LiveData<List<Record>>

    @Query("select * from record_table where description = 'income' order by date desc")
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

    @Query("select * from debt_table order by date desc")
    fun getAllDataDebtDesc(): LiveData<List<Debt>>

    @Query("select * from debt_table order by date asc")
    fun getAllDataDebtAsc(): LiveData<List<Debt>>

    @TypeConverters(DateConverter::class)
    @Query("select * from debt_table where date between :startDate and :endDate order by date desc")
    fun getFilteredDebtDesc(startDate: Date, endDate: Date): LiveData<List<Debt>>

    @TypeConverters(DateConverter::class)
    @Query("select * from debt_table where date between :startDate and :endDate order by date asc")
    fun getFilteredDebtAsc(startDate: Date, endDate: Date): LiveData<List<Debt>>

    @Query("select sum(total) from debt_table")
    fun getTotalDebt(): LiveData<Int>
}