package com.aaryan.simdash.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alert_thresholds")
data class AlertThreshold(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val subscriptionId: Int,
    val percentageThreshold: Int,
    val customMessage: String?,
    val isEnabled: Boolean,
    val firedTodayAt: Long?,
    val colorHex: String,
    val sortOrder: Int
)
