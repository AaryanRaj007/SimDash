package com.aaryan.simdash.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usage_sessions")
data class UsageSession(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val subscriptionId: Int,
    val date: String,
    val dailyLimitBytes: Long,
    val usedBytes: Long,
    val peakHour: Int?,
    val alertsFiredCount: Int
)
