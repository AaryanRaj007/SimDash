package com.aaryan.simdash.data.repository

import com.aaryan.simdash.data.db.SimProfile
import com.aaryan.simdash.data.db.SimProfileDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SimRepository @Inject constructor(
    private val simProfileDao: SimProfileDao
) {
    fun getAllProfiles(): Flow<List<SimProfile>> = simProfileDao.getAllProfiles()

    suspend fun getProfile(subId: Int): SimProfile? = simProfileDao.getProfile(subId)

    suspend fun upsertProfile(profile: SimProfile) = simProfileDao.upsertProfile(profile)

    suspend fun updateUsage(subId: Int, bytes: Long, time: Long) =
        simProfileDao.updateUsage(subId, bytes, time)

    suspend fun updatePlanInfo(subId: Int, limit: Long, pool: Long, validity: Long, type: String, fetchTime: Long) =
        simProfileDao.updatePlanInfo(subId, limit, pool, validity, type, fetchTime)
}
