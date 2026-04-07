package com.aaryan.simdash.alert

import android.app.NotificationManager
import android.content.Context
import com.aaryan.simdash.data.db.AlertThreshold
import com.aaryan.simdash.data.db.PendingAlert
import com.aaryan.simdash.data.repository.AlertRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class ThresholdAlertEngine @Inject constructor(
    @ApplicationContext private val context: Context,
    private val alertRepository: AlertRepository,
    private val notifier: SimDashNotifier
) {
    fun checkAndFireAlerts(percentUsed: Float, thresholds: List<AlertThreshold>) {
        val currentPercentInt = (percentUsed * 100).toInt()
        val nm = context.getSystemService(NotificationManager::class.java)

        thresholds.sortedBy { it.percentageThreshold }.forEach { threshold ->
            if (threshold.isEnabled && threshold.firedTodayAt == null && currentPercentInt >= threshold.percentageThreshold) {
                
                val msg = threshold.customMessage ?: "You have used ${threshold.percentageThreshold}% of your daily data."
                
                if (nm?.currentInterruptionFilter == NotificationManager.INTERRUPTION_FILTER_ALL) {
                    // DND is off - fire immediately
                    notifier.fireThresholdAlert(threshold)
                } else {
                    // DND is on - queue alert
                    CoroutineScope(Dispatchers.IO).launch {
                        alertRepository.savePendingAlert(
                            PendingAlert(
                                thresholdId = threshold.id,
                                message = msg,
                                createdAt = System.currentTimeMillis()
                            )
                        )
                    }
                }
                
                // Mark fired
                CoroutineScope(Dispatchers.IO).launch {
                    alertRepository.markFired(threshold.id, System.currentTimeMillis())
                }
            }
        }
    }
}
