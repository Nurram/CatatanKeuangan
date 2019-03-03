package com.nurram.project.catatankeuangan.db

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.support.annotation.NonNull

@Entity(tableName = "record_table")
class Record(
    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "id")
    var id: Int = 0,
    @ColumnInfo(name = "judul")
    var judul: String = "None",
    @ColumnInfo(name = "jumlah")
    var jumlah: Int = 0,
    @ColumnInfo(name = "tanggal")
    var tanggal: String = "27-02-2019",
    @ColumnInfo(name = "keterangan")
    var keterangan: String = "pengeluaran"
)

@Entity(tableName = "hutang_table")
class Hutang(
    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "id")
    var id: Int = 0,
    @ColumnInfo(name = "judul")
    var judul: String = "None",
    @ColumnInfo(name = "jumlah")
    var jumlah: Int = 0,
    @ColumnInfo(name = "tanggal")
    var tanggal: String = "27-02-2019"
)
