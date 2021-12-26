package com.nurram.project.pencatatkeuangan.db

import androidx.annotation.NonNull
import androidx.room.*
import com.nurram.project.pencatatkeuangan.db.converter.DateConverter
import java.util.*

@Entity(tableName = "record_table")
@TypeConverters(DateConverter::class)
data class Record(
    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "id")
    var id: Int = 0,
    @ColumnInfo(name = "judul")
    var judul: String = "None",
    @ColumnInfo(name = "total")
    var total: Long = 0,
    @ColumnInfo(name = "date")
    var date: Date? = null,
    @ColumnInfo(name = "notes")
    var notes: String = "",
    @ColumnInfo(name = "wallet_id")
    var walletId: String = "def",
    @ColumnInfo(name = "description")
    var description: String = "expenses",
    @Ignore
    var type: Int = 0
)

@Entity(tableName = "debt_table")
@TypeConverters(DateConverter::class)
data class Debt(
    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "id")
    var id: Int = 0,
    @ColumnInfo(name = "judul")
    var judul: String = "None",
    @ColumnInfo(name = "total")
    var total: Int = 0,
    @ColumnInfo(name = "date")
    var date: Date? = null,
    @ColumnInfo(name = "wallet_id")
    var walletId: String = "def",
    @Ignore
    var type: Int = 0
)

@Entity(tableName = "wallet_table")
data class Wallet(
    @PrimaryKey(autoGenerate = false)
    @NonNull
    @ColumnInfo(name = "id")
    var id: String = "def",
    @ColumnInfo(name = "name")
    var name: String = ""
)
