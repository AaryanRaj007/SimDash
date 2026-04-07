package com.aaryan.simdash.alert

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.aaryan.simdash.MainActivity
import com.aaryan.simdash.R
import com.aaryan.simdash.data.db.AlertThreshold
import com.aaryan.simdash.data.db.PendingAlert
import com.aaryan.simdash.util.PermissionUtils
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import android.graphics.Color

class SimDashNotifier @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val nm = NotificationManagerCompat.from(context)

    private fun getPendingIntent(): PendingIntent {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        return PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    fun fireThresholdAlert(threshold: AlertThreshold) {
        if (!PermissionUtils.hasNotificationPermission(context)) return

        val msg = threshold.customMessage ?: "You have used ${threshold.percentageThreshold}% of your daily data."

        val builder = NotificationCompat.Builder(context, NotificationChannels.THRESHOLD_ALERTS)
            .setSmallIcon(R.mipmap.ic_launcher) // TODO: update once logo icon is provided
            .setContentTitle("Data Alert: ${threshold.percentageThreshold}% Used")
            .setContentText(msg)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(getPendingIntent())
            .setAutoCancel(true)
            .setColor(Color.parseColor(threshold.colorHex))

        try {
            nm.notify(threshold.id, builder.build())
        } catch (e: SecurityException) {
            // Permission missing
        }
    }

    fun fireQueuedAlert(pendingAlert: PendingAlert) {
        if (!PermissionUtils.hasNotificationPermission(context)) return

        val builder = NotificationCompat.Builder(context, NotificationChannels.THRESHOLD_ALERTS)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Data Alert Passed")
            .setContentText(pendingAlert.message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(getPendingIntent())
            .setAutoCancel(true)

        try {
            nm.notify(pendingAlert.id + 1000, builder.build()) // offset id
        } catch (e: SecurityException) {
            // Permission missing
        }
    }

    fun fireValidityAlert(daysLeft: Int, planDesc: String) {
        if (!PermissionUtils.hasNotificationPermission(context)) return

        val title = if (daysLeft == 0) "Plan Expires Today" else "Plan Expires Tomorrow"
        val builder = NotificationCompat.Builder(context, NotificationChannels.VALIDITY_ALERTS)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText("Your $planDesc plan will expire soon. Tap to view details.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(getPendingIntent())
            .setAutoCancel(true)

        try {
            nm.notify(2000, builder.build())
        } catch (e: SecurityException) {
            // Permission missing
        }
    }

    fun fireMorningSummary(desc: String) {
        if (!PermissionUtils.hasNotificationPermission(context)) return

        val builder = NotificationCompat.Builder(context, NotificationChannels.MORNING_SUMMARY)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Morning Data Summary")
            .setContentText(desc)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setContentIntent(getPendingIntent())
            .setAutoCancel(true)

        try {
            nm.notify(3000, builder.build())
        } catch (e: SecurityException) {
            // Permission missing
        }
    }
}
