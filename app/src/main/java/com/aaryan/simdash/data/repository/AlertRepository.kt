package com.aaryan.simdash.data.repository

import com.aaryan.simdash.data.db.AlertThreshold
import com.aaryan.simdash.data.db.AlertThresholdDao
import com.aaryan.simdash.data.db.PendingAlert
import com.aaryan.simdash.data.db.PendingAlertDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AlertRepository @Inject constructor(
    private val thresholdDao: AlertThresholdDao,
    private val pendingAlertDao: PendingAlertDao
) {
    fun getThresholdsForSim(subId: Int): Flow<List<AlertThreshold>> = thresholdDao.getThresholdsForSim(subId)

    suspend fun getEnabledThresholds(subId: Int): List<AlertThreshold> = thresholdDao.getEnabledThresholds(subId)

    suspend fun upsertThreshold(threshold: AlertThreshold) = thresholdDao.upsertThreshold(threshold)

    suspend fun deleteThreshold(threshold: AlertThreshold) = thresholdDao.deleteThreshold(threshold)

    suspend fun resetAllFiredToday(subId: Int) = thresholdDao.resetAllFiredToday(subId)

    suspend fun markFired(id: Int, time: Long) = thresholdDao.markFired(id, time)

    suspend fun getThresholdCount(subId: Int): Int = thresholdDao.getThresholdCount(subId)

    // Pending Alerts
    suspend fun getAllPendingAlerts(): List<PendingAlert> = pendingAlertDao.getAllPendingAlerts()

    suspend fun savePendingAlert(alert: PendingAlert) = pendingAlertDao.savePendingAlert(alert)

    suspend fun clearPendingAlerts() = pendingAlertDao.clearPendingAlerts()
}
