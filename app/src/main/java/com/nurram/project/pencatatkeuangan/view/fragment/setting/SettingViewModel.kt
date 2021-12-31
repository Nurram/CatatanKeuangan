package com.nurram.project.pencatatkeuangan.view.fragment.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nurram.project.pencatatkeuangan.db.repos.RecordRepo
import kotlinx.coroutines.launch

class SettingViewModel(
    private val recordRepo: RecordRepo
) : ViewModel() {

    fun deleteAllRecords() = viewModelScope.launch { recordRepo.deleteAllRecord() }
}