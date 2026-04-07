package com.aaryan.simdash.widget

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build

object WidgetUpdateManager {
    fun scheduleUpdates(context: Context, intervalHours: Int = 1) {
        val alarmManager = context.getSystemService(AlarmManager::class.java)
        
        val intent = Intent(context, SimDashWidget::class.java).apply {
            action = "com.aaryan.simdash.WIDGET_UPDATE_TIMER" // Can be custom action
        }
        
        val pendingIntent = PendingIntent.getBroadcast(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Cancel existing
        alarmManager.cancel(pendingIntent)

        if (intervalHours <= 0) return

        val intervalMillis = intervalHours * 60 * 60 * 1000L
        
        // We use inexact repeating for battery battery, Doze mode might defer it.
        // As per PRD, 30 min / 1 hour background updates.
        alarmManager.setInexactRepeating(
            AlarmManager.RTC,
            System.currentTimeMillis() + intervalMillis,
            intervalMillis,
            pendingIntent
        )
    }
}
