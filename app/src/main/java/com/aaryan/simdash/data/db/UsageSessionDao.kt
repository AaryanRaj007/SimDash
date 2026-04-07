package com.aaryan.simdash.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UsageSessionDao {
    @Query("SELECT * FROM usage_sessions WHERE subscriptionId = :subId ORDER BY date DESC LIMIT 30")
    fun getLast30Days(subId: Int): Flow<List<UsageSession>>

    @Query("SELECT * FROM usage_sessions WHERE subscriptionId = :subId AND date = :date LIMIT 1")
    suspend fun getForDate(subId: Int, date: String): UsageSession?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertSession(session: UsageSession)
}
