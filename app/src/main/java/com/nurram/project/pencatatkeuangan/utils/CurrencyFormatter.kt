package com.nurram.project.pencatatkeuangan.utils

import android.content.Context
import android.widget.Toast
import com.nurram.project.pencatatkeuangan.R
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

    fun isAmountValid(context: Context, amount: String): Int {
        return try {
            val value = amount.toInt()

            if (value > 1000000000) {
                throw Exception()
            }

            value
        } catch (e: Exception) {
            Toast.makeText(context, context.getString(R.string.max_amount), Toast.LENGTH_SHORT)
                .show()
            0
        }
    }

    fun isAmountValidLong(context: Context, amount: String): Long {
        return try {
            val value = amount.toLong()

            if (value > 1000000000) {
                throw Exception()
            }

            value
        } catch (e: Exception) {
            Toast.makeText(context, context.getString(R.string.max_amount), Toast.LENGTH_SHORT)
                .show()
            0
        }
    }

    fun getNumber(value: String): Long {
        var newValue = value.replace("Rp", "")
        newValue = newValue.replace(".", "")
        return newValue.toLong()
    }

    fun getNumberAsString(value: String): String {
        var newValue = value.replace("Rp", "")
        newValue = newValue.replace(".", "")
        return newValue
    }
}