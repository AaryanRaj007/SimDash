package com.aaryan.simdash.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PendingAlertDao {
    @Query("SELECT * FROM pending_alerts ORDER BY createdAt ASC")
    suspend fun getAllPendingAlerts(): List<PendingAlert>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun savePendingAlert(alert: PendingAlert)

    @Query("DELETE FROM pending_alerts")
    suspend fun clearPendingAlerts()
}
