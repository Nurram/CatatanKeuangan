package com.nurram.project.pencatatkeuangan.view.fragment.discount

import androidx.lifecycle.ViewModel

class DiscCalcViewModel : ViewModel() {

    fun calculateDiscount(amount: Long, discount: Long): Map<String, Long> {
        val priceAfterDiscount = amount - (amount * discount) / 100
        val save = (amount * discount) / 100
        return mapOf("save" to save, "priceAfterDiscount" to priceAfterDiscount)
    }
}