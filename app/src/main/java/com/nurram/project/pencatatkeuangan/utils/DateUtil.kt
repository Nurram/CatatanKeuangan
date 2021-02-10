package com.nurram.project.pencatatkeuangan.utils

import java.text.SimpleDateFormat
import java.util.*

class DateUtil {
    companion object {
        fun formatDate(input: String): String {
            val dateFormat = SimpleDateFormat("dd MM yyyy")
            val date = dateFormat.parse(input)
            val result = SimpleDateFormat("dd MMMM yyyy")
            return result.format(date)
        }

        fun getCurrentDate(): String {
            val formatter = SimpleDateFormat("dd MM yyyy")
            return formatter.format(Date())
        }
    }
}