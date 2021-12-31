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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(record: List<Record>)

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

    @Transaction
    suspend fun moveDebtsToRecords(walletId: String, records: List<Record>) {
        deleteAllDebt(walletId)
        insertAll(records)
    }

    @TypeConverters(DateConverter::class)
    @Query("select * from record_table where wallet_id=:walletId and date between :startDate and :endDate order by date desc")
    fun getAllDataDesc(walletId: String, startDate: Date, endDate: Date): LiveData<List<Record>>

    @TypeConverters(DateConverter::class)
    @Query("select * from record_table where wallet_id=:walletId and date between :startDate and :endDate order by date asc")
    fun getAllDataAsc(walletId: String, startDate: Date, endDate: Date): LiveData<List<Record>>

    @TypeConverters(DateConverter::class)
    @Query("select * from record_table where wallet_id=:walletId and description like :category and date between :startDate and :endDate order by date desc")
    fun getFilteredRecordWithDateDesc(
        category: String,
        walletId: String,
        startDate: Date,
        endDate: Date
    ): LiveData<List<Record>>

    @TypeConverters(DateConverter::class)
    @Query("select * from record_table where wallet_id=:walletId and description like :category and date between :startDate and :endDate order by date asc")
    fun getFilteredRecordWithDateAsc(
        category: String,
        walletId: String,
        startDate: Date,
        endDate: Date
    ): LiveData<List<Record>>

    @TypeConverters(DateConverter::class)
    @Query("select * from record_table where wallet_id=:walletId and description like :category order by date desc")
    fun getFilteredRecordDesc(category: String, walletId: String): LiveData<List<Record>>

    @TypeConverters(DateConverter::class)
    @Query("select * from record_table where wallet_id=:walletId and description like :category order by date asc")
    fun getFilteredRecordAsc(category: String, walletId: String): LiveData<List<Record>>

    @TypeConverters(DateConverter::class)
    @Query(
        "select (sub.income - coalesce(sub2.expense, 0)) from " +
                "(select sum(total) as income from record_table where wallet_id=:walletId and description = 'income') sub, " +
                "(select sum(total) as expense from record_table where wallet_id=:walletId and description = 'expenses') sub2"
    )
    fun getBalance(walletId: String): LiveData<Long>

    @TypeConverters(DateConverter::class)
    @Query("select * from record_table  where wallet_id=:walletId and description = 'expenses' and date between :startDate and :endDate order by date desc")
    fun getCurrentExpenses(walletId: String, startDate: Date, endDate: Date): LiveData<List<Record>>

    @TypeConverters(DateConverter::class)
    @Query("select * from record_table  where wallet_id=:walletId and description = 'income' and date between :startDate and :endDate order by date desc")
    fun getCurrentIncome(walletId: String, startDate: Date, endDate: Date): LiveData<List<Record>>

    @TypeConverters(DateConverter::class)
    @Query("select * from record_table  where wallet_id=:walletId and description = 'debt' and date between :startDate and :endDate order by date desc")
    fun getCurrentDebt(walletId: String, startDate: Date, endDate: Date): LiveData<List<Record>>

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

    @TypeConverters(DateConverter::class)
    @Query("select * from record_table where wallet_id=:walletId and description = 'income' and date between :startDate and :endDate ORDER BY total DESC LIMIT 1")
    fun getMaxIncome(walletId: String, startDate: Date, endDate: Date): LiveData<Record>

    @TypeConverters(DateConverter::class)
    @Query("select * from record_table where wallet_id=:walletId and description = 'expenses' and date between :startDate and :endDate ORDER BY total DESC LIMIT 1")
    fun getMaxExpense(walletId: String, startDate: Date, endDate: Date): LiveData<Record>

    @Query("Delete from debt_table where wallet_id=:walletId")
    suspend fun deleteAllDebt(walletId: String)

    @Query("select * from debt_table where wallet_id=:walletId order by date asc")
    fun getAllDataDebtAsc(walletId: String): LiveData<List<Debt>>
}