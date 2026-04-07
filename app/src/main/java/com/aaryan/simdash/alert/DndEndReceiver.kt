package com.aaryan.simdash.alert

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.aaryan.simdash.data.repository.AlertRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class DndEndReceiver : BroadcastReceiver() {

    @Inject
    lateinit var alertRepository: AlertRepository
    
    @Inject
    lateinit var notifier: SimDashNotifier

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == NotificationManager.ACTION_INTERRUPTION_FILTER_CHANGED) {
            val nm = context.getSystemService(NotificationManager::class.java)
            if (nm?.currentInterruptionFilter == NotificationManager.INTERRUPTION_FILTER_ALL) {
                CoroutineScope(Dispatchers.IO).launch {
                    val pending = alertRepository.getAllPendingAlerts()
                    pending.forEach { 
                        notifier.fireQueuedAlert(it) 
                    }
                    if (pending.isNotEmpty()) {
                        alertRepository.clearPendingAlerts()
                    }
                }
            }
        }
    }
}
