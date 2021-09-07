package com.nurram.project.pencatatkeuangan.utils

import java.text.SimpleDateFormat
import java.util.*

class DateUtil {
    companion object {
        fun formatDate(input: Date): String {
            val result = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault())
            return result.format(input)
        }

        private fun formatDateMonthOnly(input: Date): String {
            val result = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
            return result.format(input)
        }

        fun formatDate2(input: Date): String {
            val result = SimpleDateFormat("dd MMMM yyyy HH:mm", Locale.getDefault())
            return result.format(input)
        }

        fun toDate(string: String): Date {
            val simpleDate = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
            return simpleDate.parse(string)!!
        }

        fun getCurrentMonthAndYear(): String {
            val formatter = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
            return formatter.format(Date())
        }

        fun subtractMonth(date: Date, month: Int): String {
            val cal = GregorianCalendar()
            cal.time = date
            cal.add(Calendar.MONTH, month)
            return formatDateMonthOnly(cal.time)
        }
    }
}