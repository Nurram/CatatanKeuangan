package com.nurram.project.catatankeuangan.utils

import java.text.SimpleDateFormat

class DateUtil {
    companion object {
        fun formatDate(input: String): String {
            val dateFormat = SimpleDateFormat("dd MM yyyy")
            val date = dateFormat.parse(input)
            val result = SimpleDateFormat("dd MMMM yyyy")
            return result.format(date)
        }
    }
}