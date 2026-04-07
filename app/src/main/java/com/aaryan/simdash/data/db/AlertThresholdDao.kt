package com.aaryan.simdash.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AlertThresholdDao {
    @Query("SELECT * FROM alert_thresholds WHERE subscriptionId = :subId ORDER BY sortOrder ASC")
    fun getThresholdsForSim(subId: Int): Flow<List<AlertThreshold>>

    @Query("SELECT * FROM alert_thresholds WHERE subscriptionId = :subId AND isEnabled = 1 ORDER BY percentageThreshold ASC")
    suspend fun getEnabledThresholds(subId: Int): List<AlertThreshold>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertThreshold(threshold: AlertThreshold)

    @Delete
    suspend fun deleteThreshold(threshold: AlertThreshold)

    @Query("UPDATE alert_thresholds SET firedTodayAt = NULL WHERE subscriptionId = :subId")
    suspend fun resetAllFiredToday(subId: Int)

    @Query("UPDATE alert_thresholds SET firedTodayAt = :time WHERE id = :id")
    suspend fun markFired(id: Int, time: Long)

    @Query("SELECT COUNT(*) FROM alert_thresholds WHERE subscriptionId = :subId")
    suspend fun getThresholdCount(subId: Int): Int
}
