package com.nurram.project.pencatatkeuangan.view.activity.graph

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.nurram.project.pencatatkeuangan.db.Record
import com.nurram.project.pencatatkeuangan.db.RecordRepo

class GraphViewModel(application: Application) : AndroidViewModel(application) {
    private val recordRepo = RecordRepo(application)

    fun getAllExpenses(): LiveData<List<Record>>? {
        return recordRepo.getAllExpenses()
    }

    fun getAllIncome(): LiveData<List<Record>>? {
        return recordRepo.getAllIncome()
    }
}