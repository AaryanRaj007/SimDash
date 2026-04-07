package com.aaryan.simdash.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        SimProfile::class,
        AlertThreshold::class,
        UsageSession::class,
        PendingAlert::class
    ],
    version = 1,
    exportSchema = false
)
abstract class SimDashDatabase : RoomDatabase() {
    abstract fun simProfileDao(): SimProfileDao
    abstract fun alertThresholdDao(): AlertThresholdDao
    abstract fun usageSessionDao(): UsageSessionDao
    abstract fun pendingAlertDao(): PendingAlertDao
}
