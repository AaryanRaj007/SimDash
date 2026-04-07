package com.aaryan.simdash.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.widget.RemoteViews
import com.aaryan.simdash.MainActivity
import com.aaryan.simdash.R
import com.aaryan.simdash.sim.ActiveSimDetector
import com.aaryan.simdash.data.repository.SimRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SimDashWidget : AppWidgetProvider() {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface WidgetEntryPoint {
        fun getSimRepository(): SimRepository
        fun getActiveSimDetector(): ActiveSimDetector
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == "com.aaryan.simdash.WIDGET_UPDATE_TIMER" || 
            intent.action == android.net.ConnectivityManager.CONNECTIVITY_ACTION ||
            intent.action == Intent.ACTION_SCREEN_ON) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(
                ComponentName(context, SimDashWidget::class.java)
            )
            onUpdate(context, appWidgetManager, appWidgetIds)
        }
    }

    companion object {
        fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            // Setup intent to launch app on click
            val intent = Intent(context, MainActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(
                context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val views = RemoteViews(context.packageName, R.layout.widget_medium)
            views.setOnClickPendingIntent(R.id.widget_root, pendingIntent)

            // Dynamic logic inside coroutine
            CoroutineScope(Dispatchers.IO).launch {
                val entryPoint = EntryPointAccessors.fromApplication(context, WidgetEntryPoint::class.java)
                val simRepo = entryPoint.getSimRepository()
                val activeSimDetector = entryPoint.getActiveSimDetector()
                
                val subId = activeSimDetector.getDefaultDataSubscriptionId()
                val profile = if (subId != -1) simRepo.getProfile(subId) else null

                val isDark = (context.resources.configuration.uiMode and 
                    android.content.res.Configuration.UI_MODE_NIGHT_MASK) == android.content.res.Configuration.UI_MODE_NIGHT_YES

                val options = appWidgetManager.getAppWidgetOptions(appWidgetId)
                var widthDp = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH)
                if (widthDp == 0) widthDp = 250 // fallback to minWidth
                val widthPx = (widthDp * context.resources.displayMetrics.density).toInt()

                var percent = 0f
                var displayCarrier = "Unknown SIM"
                var dataDesc = "No data available"
                var validity = ""
                var status = "Tap to setup"
                var ringColor = Color.parseColor("#534AB7") // Primary

                if (profile != null && profile.dailyLimitBytes > 0) {
                    displayCarrier = profile.displayName.ifEmpty { profile.carrierName }
                    percent = (profile.todayUsedBytes.toFloat() / profile.dailyLimitBytes.toFloat()).coerceIn(0f, 1f)
                    
                    val usedMB = profile.todayUsedBytes / (1024f * 1024f)
                    val limitMB = profile.dailyLimitBytes / (1024f * 1024f)
                    val totalLimitGb = profile.dailyLimitBytes / (1024f * 1024f * 1024f)
                    val remainingMb = limitMB - usedMB
                    
                    if (remainingMb > 0) {
                        dataDesc = "${"%.1f".format(remainingMb / 1024f)} GB left of ${"%.1f".format(totalLimitGb)} GB"
                    } else {
                        dataDesc = "Daily limit reached"
                    }

                    ringColor = when {
                        percent < 0.5f -> Color.parseColor("#1D9E75") // Green
                        percent < 0.9f -> Color.parseColor("#BA7517") // Amber
                        else -> Color.parseColor("#E24B4A") // Red
                    }

                    val cal = java.util.Calendar.getInstance()
                    cal.timeInMillis = profile.validityEndDate
                    val sdf = java.text.SimpleDateFormat("dd MMM", java.util.Locale.US)
                    validity = "Valid till ${sdf.format(cal.time)}"
                    status = "Updated now"
                }

                val bitmap = WidgetRingDrawer.drawRingBitmap(percent, ringColor, isDark, context, widthPx)
                
                CoroutineScope(Dispatchers.Main).launch {
                    views.setImageViewBitmap(R.id.widget_ring_image, bitmap)
                    views.setTextViewText(R.id.widget_percent_text, "${(percent * 100).toInt()}%")
                    views.setTextViewText(R.id.widget_carrier_text, displayCarrier)
                    views.setTextViewText(R.id.widget_data_desc, dataDesc)
                    views.setTextViewText(R.id.widget_validity_text, validity)
                    views.setTextViewText(R.id.widget_status_text, status)
                    
                    if (isDark) {
                        views.setTextColor(R.id.widget_carrier_text, Color.WHITE)
                        views.setTextColor(R.id.widget_percent_text, Color.WHITE)
                        views.setInt(R.id.widget_root, "setBackgroundResource", R.drawable.widget_bg)
                    } else {
                        views.setTextColor(R.id.widget_carrier_text, Color.BLACK)
                        views.setTextColor(R.id.widget_percent_text, Color.BLACK)
                        views.setInt(R.id.widget_root, "setBackgroundResource", R.drawable.widget_bg_light)
                    }

                    appWidgetManager.updateAppWidget(appWidgetId, views)
                }
            }
        }
    }
}
