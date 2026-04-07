package com.aaryan.simdash.receiver

import android.appwidget.AppWidgetManager
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.telephony.SubscriptionManager
import com.aaryan.simdash.widget.SimDashWidget
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SimChangedReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "android.intent.action.ACTION_DEFAULT_DATA_SUBSCRIPTION_CHANGED") {
            // Active DATA SIM changed -> trigger instant widget refresh
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(
                ComponentName(context, SimDashWidget::class.java)
            )
            SimDashWidget.updateAppWidget(context, appWidgetManager, appWidgetIds.firstOrNull() ?: return)
        }
    }
}
