package com.nurram.project.pencatatkeuangan.db.daos

import androidx.lifecycle.LiveData
import androidx.room.*
import com.nurram.project.pencatatkeuangan.db.Wallet

@Dao
interface WalletDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(wallet: Wallet)

    @Query("SELECT * FROM wallet_table")
    fun getWallets(): LiveData<List<Wallet>>

    @Query("SELECT * FROM wallet_table WHERE id=:id")
    fun getWalletById(id: String): LiveData<Wallet>

    @Delete
    fun deleteWallet(wallet: Wallet)
}