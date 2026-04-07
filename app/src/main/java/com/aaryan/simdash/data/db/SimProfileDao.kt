package com.aaryan.simdash.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SimProfileDao {
    @Query("SELECT * FROM sim_profiles")
    fun getAllProfiles(): Flow<List<SimProfile>>

    @Query("SELECT * FROM sim_profiles WHERE subscriptionId = :subId")
    suspend fun getProfile(subId: Int): SimProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertProfile(profile: SimProfile)

    @Query("UPDATE sim_profiles SET todayUsedBytes = :bytes, lastUpdatedAt = :time WHERE subscriptionId = :subId")
    suspend fun updateUsage(subId: Int, bytes: Long, time: Long)

    @Query("UPDATE sim_profiles SET dailyLimitBytes = :limit, totalPoolBytes = :pool, validityEndDate = :validity, planType = :type, lastUssdFetchAt = :fetchTime WHERE subscriptionId = :subId")
    suspend fun updatePlanInfo(subId: Int, limit: Long, pool: Long, validity: Long, type: String, fetchTime: Long)
}
