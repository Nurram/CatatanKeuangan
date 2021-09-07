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
    suspend fun insert(record: Record)

    @Query("Delete from record_table where wallet_id=:walletId")
    suspend fun deleteAll(walletId: String)

    @Delete
    suspend fun delete(record: Record)

    @Query("delete from record_table where wallet_id=:walletId")
    suspend fun deleteWalletDataFromRecord(walletId: String)

    @Query("delete from debt_table where wallet_id=:walletId")
    suspend fun deleteWalletDataFromDebt(walletId: String)

    @Update
    suspend fun update(record: Record)

    @TypeConverters(DateConverter::class)
    @Query("select * from record_table where wallet_id=:walletId and date between :startDate and :endDate order by date desc")
    fun getAllDataDesc(walletId: String, startDate: Date, endDate: Date): LiveData<List<Record>>

    @TypeConverters(DateConverter::class)
    @Query("select * from record_table where wallet_id=:walletId and date between :startDate and :endDate order by date asc")
    fun getAllDataAsc(walletId: String, startDate: Date, endDate: Date): LiveData<List<Record>>

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

    @TypeConverters(DateConverter::class)
    @Query("select (sub.income - coalesce(sub2.expense, 0)) from " +
            "(select sum(total) as income from record_table where wallet_id=:walletId and description = 'income' and date between :startDate and :endDate) sub, " +
            "(select sum(total) as expense from record_table where wallet_id=:walletId and description = 'expenses' and date between :startDate and :endDate) sub2")
    fun getBalance(walletId: String, startDate: Date, endDate: Date): LiveData<Long>

    @TypeConverters(DateConverter::class)
    @Query("select * from record_table  where wallet_id=:walletId and description = 'expenses' and date between :startDate and :endDate order by date desc")
    fun getCurrentExpenses(walletId: String, startDate: Date, endDate: Date): LiveData<List<Record>>

    @TypeConverters(DateConverter::class)
    @Query("select * from record_table  where wallet_id=:walletId and description = 'income' and date between :startDate and :endDate order by date desc")
    fun getCurrentIncome(walletId: String, startDate: Date, endDate: Date): LiveData<List<Record>>

    @TypeConverters(DateConverter::class)
    @Query("select sum(total) from record_table  where wallet_id=:walletId and description = 'expenses' and date between :startDate and :endDate")
    fun getCurrentTotalExpenses(walletId: String, startDate: Date, endDate: Date): LiveData<Long>

    @TypeConverters(DateConverter::class)
    @Query("select sum(total) from record_table  where wallet_id=:walletId and description = 'income' and date between :startDate and :endDate")
    fun getCurrentTotalIncome(walletId: String, startDate: Date, endDate: Date): LiveData<Long>

    @Query("select sum(total) from record_table  where wallet_id=:walletId and description = 'expenses'")
    fun getTotalExpenses(walletId: String): LiveData<Long>

    @Query("select sum(total) from record_table  where wallet_id=:walletId and description = 'income'")
    fun getTotalIncome(walletId: String): LiveData<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDebt(debt: Debt)

    @Query("Delete from debt_table where wallet_id=:walletId")
    suspend fun deleteAllDebt(walletId: String)

    @Delete
    suspend fun deleteDebt(debt: Debt)

    @Update
    suspend fun updateDebt(debt: Debt)

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
    fun getTotalDebt(walletId: String): LiveData<Long>
}