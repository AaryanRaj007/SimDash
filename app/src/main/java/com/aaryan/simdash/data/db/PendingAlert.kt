package com.aaryan.simdash.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pending_alerts")
data class PendingAlert(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val thresholdId: Int,
    val message: String,
    val createdAt: Long
)
