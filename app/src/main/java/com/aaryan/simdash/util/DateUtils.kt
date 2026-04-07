package com.aaryan.simdash.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit

object DateUtils {
    val IST: TimeZone = TimeZone.getTimeZone("Asia/Kolkata")

    // Get current time in ms
    fun now(): Long = System.currentTimeMillis()

    // Used everywhere for day resetting and boundary calculation
    fun getStartOfTodayIst(): Long {
        val cal = Calendar.getInstance(IST).apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return cal.timeInMillis
    }

    fun getTodayDateStringIst(): String {
        val cal = Calendar.getInstance(IST)
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH) + 1
        val day = cal.get(Calendar.DAY_OF_MONTH)
        // Format to YYYY-MM-DD
        return String.format(Locale.US, "%04d-%02d-%02d", year, month, day)
    }

    // Convert string like "15-May-2026" to epoch ms end-of-day
    fun parseValidityDate(dateString: String?): Long? {
        if (dateString.isNullOrBlank()) return null
        val formats = listOf("dd-MMM-yyyy", "dd/MM/yyyy", "dd-MM-yyyy")
        for (pattern in formats) {
            try {
                val sdf = SimpleDateFormat(pattern, Locale.US)
                sdf.timeZone = IST
                val date = sdf.parse(dateString)
                if (date != null) {
                    val cal = Calendar.getInstance(IST)
                    cal.time = date
                    cal.set(Calendar.HOUR_OF_DAY, 23)
                    cal.set(Calendar.MINUTE, 59)
                    cal.set(Calendar.SECOND, 59)
                    cal.set(Calendar.MILLISECOND, 999)
                    return cal.timeInMillis
                }
            } catch (e: Exception) {
                // Ignore, try next
            }
        }
        return null
    }

    // Validity days left
    fun getDaysLeft(validityEndDate: Long): Int {
        if (validityEndDate <= 0) return 0
        val diff = validityEndDate - now()
        if (diff <= 0) return 0
        return TimeUnit.MILLISECONDS.toDays(diff).toInt() + 1
    }

    fun getFormattedExpiry(validityEndDate: Long): String {
        if (validityEndDate <= 0) return ""
        val sdf = SimpleDateFormat("dd MMM yyyy", Locale.US)
        sdf.timeZone = IST
        return sdf.format(validityEndDate)
    }
}
