package com.aaryan.simdash.data.repository

import com.aaryan.simdash.data.db.UsageSession
import com.aaryan.simdash.data.db.UsageSessionDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UsageRepository @Inject constructor(
    private val usageSessionDao: UsageSessionDao
) {
    fun getLast30Days(subId: Int): Flow<List<UsageSession>> = usageSessionDao.getLast30Days(subId)

    suspend fun getForDate(subId: Int, date: String): UsageSession? = usageSessionDao.getForDate(subId, date)

    suspend fun upsertSession(session: UsageSession) = usageSessionDao.upsertSession(session)
}
