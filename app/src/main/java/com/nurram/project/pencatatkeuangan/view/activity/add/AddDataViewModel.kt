package com.nurram.project.pencatatkeuangan.view.activity.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nurram.project.pencatatkeuangan.db.Record
import com.nurram.project.pencatatkeuangan.db.repos.RecordRepo
import kotlinx.coroutines.launch

class AddDataViewModel(
    private val recordRepo: RecordRepo
) : ViewModel() {

    fun insertRecord(record: Record) = viewModelScope.launch { recordRepo.insertRecord(record) }

    fun updateRecord(record: Record) = viewModelScope.launch { recordRepo.updateRecord(record) }

    fun deleteRecord(record: Record) = viewModelScope.launch { recordRepo.deleteRecord(record) }
}