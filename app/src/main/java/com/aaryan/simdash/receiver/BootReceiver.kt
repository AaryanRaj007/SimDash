package com.aaryan.simdash.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.aaryan.simdash.widget.WidgetUpdateManager
// import com.aaryan.simdash.alert.ValidityAlertScheduler
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            // Re-schedule widget update alarms
            WidgetUpdateManager.scheduleUpdates(context)
            
            // Re-schedule validity alarms
            // ValidityAlertScheduler.rescheduleAll(context)
        }
    }
}
