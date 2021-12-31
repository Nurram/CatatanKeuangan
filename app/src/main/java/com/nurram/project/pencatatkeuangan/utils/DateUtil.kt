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

        fun getCurrentMonthName(): String {
            val cal = Calendar.getInstance()
            val monthDate = SimpleDateFormat("MMMM", Locale.getDefault())
            return monthDate.format(cal.time)
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

        fun subtractDay(date: Date, day: Int): Date {
            val cal = GregorianCalendar()
            cal.time = date
            cal.add(Calendar.DAY_OF_MONTH, day)

            return cal.time
        }

        fun getCurrentMonthAndYearDate(): Date {
            return toDate(getCurrentMonthAndYear())
        }

        fun getMaxDateCalendar(): Calendar {
            val currentMonth = getCurrentMonthAndYear()
            val date = toDate(currentMonth)
            val calendar = Calendar.getInstance()
            calendar.time = date
            calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
            calendar.set(Calendar.HOUR_OF_DAY, 23)
            calendar.set(Calendar.MINUTE, 59)
            calendar.set(Calendar.SECOND, 59)

            return calendar
        }
    }
}