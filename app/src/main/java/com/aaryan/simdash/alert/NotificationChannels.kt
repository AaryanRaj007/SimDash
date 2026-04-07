package com.aaryan.simdash.alert

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

object NotificationChannels {
    const val THRESHOLD_ALERTS = "threshold_alerts"
    const val VALIDITY_ALERTS = "validity_alerts"
    const val MORNING_SUMMARY = "morning_summary"

    fun createAll(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nm = context.getSystemService(NotificationManager::class.java)

            val threshold = NotificationChannel(
                THRESHOLD_ALERTS,
                "Usage Threshold Alerts",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Alerts when you cross specific data percentage thresholds."
            }

            val validity = NotificationChannel(
                VALIDITY_ALERTS,
                "Plan Validity Expiry",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Alerts before your data plan expires."
            }

            val morning = NotificationChannel(
                MORNING_SUMMARY,
                "Morning Summary",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Daily morning data balance summary."
            }

            nm?.createNotificationChannels(listOf(threshold, validity, morning))
        }
    }
}
