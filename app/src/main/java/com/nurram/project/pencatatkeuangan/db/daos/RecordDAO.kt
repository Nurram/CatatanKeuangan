package com.nurram.project.pencatatkeuangan.db.daos

import androidx.lifecycle.LiveData
import androidx.room.*
import com.nurram.project.pencatatkeuangan.db.Debt
import com.nurram.project.pencatatkeuangan.db.Record
import com.nurram.project.pencatatkeuangan.db.converter.DateConverter
import java.util.*

@Dao
interface RecordDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(record: Record)

    @Query("Delete from record_table where wallet_id=:walletId")
    fun deleteAll(walletId: String)

    @Delete
    fun delete(record: Record)

    @Query("delete from record_table where wallet_id=:walletId")
    fun deleteWalletDataFromRecord(walletId: String)

    @Query("delete from debt_table where wallet_id=:walletId")
    fun deleteWalletDataFromDebt(walletId: String)

    @Update
    fun update(record: Record)

    @Query("select count(*) from record_table WHERE wallet_id=:walletId")
    fun getAllDataCount(walletId: String): LiveData<Int>

    @Query("select * from record_table where wallet_id=:walletId order by date desc")
    fun getAllDataDesc(walletId: String): LiveData<List<Record>>

    @Query("select * from record_table where wallet_id=:walletId order by date asc")
    fun getAllDataAsc(walletId: String): LiveData<List<Record>>

    @TypeConverters(DateConverter::class)
    @Query("select * from record_table where wallet_id=:walletId and date between :startDate and :endDate order by date desc")
    fun getFilteredRecordDesc(
        walletId: String,
        startDate: Date,
        endDate: Date
    ): LiveData<List<Record>>

    @TypeConverters(DateConverter::class)
    @Query("select * from record_table where wallet_id=:walletId and date between :startDate and :endDate order by date asc")
    fun getFilteredRecordAsc(
        walletId: String,
        startDate: Date,
        endDate: Date
    ): LiveData<List<Record>>

    @Query("select * from record_table  where wallet_id=:walletId and description = 'expenses' order by date desc")
    fun getAllExpenses(walletId: String): LiveData<List<Record>>

    @Query("select * from record_table  where wallet_id=:walletId and description = 'income' order by date desc")
    fun getAllIncome(walletId: String): LiveData<List<Record>>

    @Query("select sum(total) from record_table  where wallet_id=:walletId and description = 'expenses'")
    fun getTotalExpenses(walletId: String): LiveData<Int>

    @Query("select sum(total) from record_table  where wallet_id=:walletId and description = 'income'")
    fun getTotalIncome(walletId: String): LiveData<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDebt(debt: Debt)

    @Query("Delete from debt_table where wallet_id=:walletId")
    fun deleteAllDebt(walletId: String)

    @Delete
    fun deleteDebt(debt: Debt)

    @Update
    fun updateDebt(debt: Debt)

    @Query("select * from debt_table where wallet_id=:walletId order by date desc")
    fun getAllDataDebtDesc(walletId: String): LiveData<List<Debt>>

    @Query("select * from debt_table where wallet_id=:walletId order by date asc")
    fun getAllDataDebtAsc(walletId: String): LiveData<List<Debt>>

    @TypeConverters(DateConverter::class)
    @Query("select * from debt_table  where wallet_id=:walletId and date between :startDate and :endDate order by date desc")
    fun getFilteredDebtDesc(walletId: String, startDate: Date, endDate: Date): LiveData<List<Debt>>

    @TypeConverters(DateConverter::class)
    @Query("select * from debt_table  where wallet_id=:walletId and date between :startDate and :endDate order by date asc")
    fun getFilteredDebtAsc(walletId: String, startDate: Date, endDate: Date): LiveData<List<Debt>>

    @Query("select sum(total) from debt_table where wallet_id=:walletId")
    fun getTotalDebt(walletId: String): LiveData<Int>
}