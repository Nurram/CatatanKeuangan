package com.nurram.project.pencatatkeuangan.db

import android.app.Application
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.nurram.project.pencatatkeuangan.db.daos.RecordDAO
import com.nurram.project.pencatatkeuangan.db.daos.WalletDao

@Database(entities = [Record::class, Debt::class, Wallet::class], version = 7)
abstract class RecordDb : RoomDatabase() {
    abstract val recordDao: RecordDAO
    abstract val walletDao: WalletDao

    companion object {
        @Volatile
        private var db: RecordDb? = null

        fun getDb(application: Application): RecordDb? {
            if (db == null) {
                synchronized(RecordDb::class.java) {
                    if (db == null) {
                        db = Room.databaseBuilder(
                            application.applicationContext,
                            RecordDb::class.java, "record_db"
                        )
                            .addMigrations(
                                MIGRATION_3_4, MIGRATION_4_5, MIGRATION_5_6, MIGRATION_6_7
                            )
                            .build()
                    }
                }
            }

            return db
        }

        private val MIGRATION_3_4: Migration = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "ALTER TABLE record_table ADD COLUMN wallet_id TEXT NOT NULL DEFAULT 'def'"
                )
            }
        }

        private val MIGRATION_4_5: Migration = object : Migration(4, 5) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "CREATE TABLE IF NOT EXISTS `wallet_table` (`id` TEXT NOT NULL, " +
                            "`name` TEXT NOT NULL, PRIMARY KEY(`id`))"
                )
            }
        }

        private val MIGRATION_5_6: Migration = object : Migration(5, 6) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "ALTER TABLE debt_table ADD COLUMN wallet_id TEXT NOT NULL DEFAULT 'def'"
                )
            }
        }

        private val MIGRATION_6_7: Migration = object : Migration(6, 7) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "ALTER TABLE record_table ADD COLUMN note TEXT NOT NULL DEFAULT ''"
                )
            }
        }
    }
}