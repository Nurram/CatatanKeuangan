package com.nurram.project.pencatatkeuangan.utils

import java.text.SimpleDateFormat
import java.util.*

class DateUtil {
    companion object {
        fun formatDate(input: Date): String {
            val result = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault())
            return result.format(input)
        }

        fun getCurrentDate(): String {
            val formatter = SimpleDateFormat("dd MM yyyy", Locale.getDefault())
            return formatter.format(Date())
        }

        fun subtractDays(date: Date, days: Int): Date {
            val cal = GregorianCalendar()
            cal.time = date
            cal.add(Calendar.DATE, -days)
            return cal.time
        }
    }
}