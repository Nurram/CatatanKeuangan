package com.nurram.project.pencatatkeuangan.view.fragment.discount

import androidx.lifecycle.ViewModel

class DiscCalcViewModel: ViewModel() {

    fun calculateDiscount(price: Long, amount: Int): Map<String, Long> {
        val save = ((price * amount) / 100)
        val result = (price - amount)

        return mapOf("save" to save, "result" to result)
    }
}