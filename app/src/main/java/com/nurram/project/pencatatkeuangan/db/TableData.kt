package com.nurram.project.pencatatkeuangan.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.annotation.NonNull
import androidx.room.TypeConverters
import com.nurram.project.pencatatkeuangan.db.converter.DateConverter
import java.util.*

@Entity(tableName = "record_table")
@TypeConverters(DateConverter::class)
class Record(
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
    @ColumnInfo(name = "description")
    var description: String = "expenses"
)

@Entity(tableName = "debt_table")
@TypeConverters(DateConverter::class)
class Debt(
    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "id")
    var id: Int = 0,
    @ColumnInfo(name = "judul")
    var judul: String = "None",
    @ColumnInfo(name = "total")
    var total: Int = 0,
    @ColumnInfo(name = "date")
    var date: Date? = null
)
