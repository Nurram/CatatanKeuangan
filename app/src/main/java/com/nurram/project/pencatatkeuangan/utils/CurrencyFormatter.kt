@file:Suppress("unused")

package com.nurram.project.pencatatkeuangan.utils

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

object CurrencyFormatter {
    fun convertAndFormat(s: Long): String {
        val format = DecimalFormat.getCurrencyInstance() as DecimalFormat
        val formatRp = DecimalFormatSymbols()
        formatRp.currencySymbol = "Rp"
        formatRp.monetaryDecimalSeparator = ','
        formatRp.groupingSeparator = '.'
        format.decimalFormatSymbols = formatRp
        val result = format.format(s)
        return result.substring(0, result.length - 3)
    }
}