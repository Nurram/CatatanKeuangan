package com.nurram.project.pencatatkeuangan.utils

import android.app.Application
import android.os.Environment
import android.widget.Toast
import com.nurram.project.pencatatkeuangan.R
import com.nurram.project.pencatatkeuangan.db.RecordDb
import ir.androidexception.roomdatabasebackupandrestore.Backup
import ir.androidexception.roomdatabasebackupandrestore.Restore
import java.io.File


class DbUtils {

    companion object {
        private val PATH =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).path

        private const val SECRET_KEY = "myNameIsNurram"
        private const val FILE_NAME = "record-db.txt"

        fun backUp(application: Application) {
            val recordDb = RecordDb.getDb(application)
            Backup.Init()
                .database(recordDb)
                .path(PATH)
                .fileName(FILE_NAME)
                .secretKey(SECRET_KEY)
                .onWorkFinishListener { _, message ->
                    Toast.makeText(application, message, Toast.LENGTH_SHORT).show()
                }
                .execute()
        }

        fun restore(application: Application, callback: (Boolean) -> Unit) {
            val file = File("$PATH/$FILE_NAME")
            if (file.exists()) {
                val recordDb = RecordDb.getDb(application)
                Restore.Init()
                    .database(recordDb)
                    .backupFilePath("$PATH/$FILE_NAME")
                    .secretKey(SECRET_KEY)
                    .onWorkFinishListener { success, message ->
                        Toast.makeText(application, message, Toast.LENGTH_SHORT).show()
                        deleteFile()

                        callback(success)
                    }
                    .execute()
            } else {
                Toast.makeText(
                    application,
                    application.getString(R.string.no_backup),
                    Toast.LENGTH_SHORT).show()
            }
        }

        private fun deleteFile() {
            val file = File("$PATH/$FILE_NAME")
            if (file.exists()) {
                file.delete()
            }
        }
    }
}