package com.aaryan.simdash.sim

import android.app.usage.NetworkStatsManager
import android.content.Context
import android.net.ConnectivityManager
import android.net.TrafficStats
import com.aaryan.simdash.util.DateUtils
import com.aaryan.simdash.util.PermissionUtils
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

enum class UsageSource { PRECISE, APPROXIMATE, UNAVAILABLE }

data class UsageResult(val bytes: Long, val source: UsageSource, val canSeparateSims: Boolean)

class DataUsageTracker @Inject constructor(
    @ApplicationContext private val context: Context,
    private val activeSimDetector: ActiveSimDetector
) {

    fun getTodayUsageBytes(subId: Int): UsageResult {
        // Layer 1 & 2: NetworkStatsManager (Usage Access granted)
        if (PermissionUtils.hasUsageAccessPermission(context)) {
            val statsManager = context.getSystemService(NetworkStatsManager::class.java)
            val startTime = DateUtils.getStartOfTodayIst()
            val endTime = System.currentTimeMillis()

            val subscriberId = activeSimDetector.getSubscriberIdSafe(subId)
            
            if (subscriberId != null) {
                // Layer 1: PRECISE per-SIM usage
                try {
                    val bucket = statsManager.querySummaryForDevice(
                        ConnectivityManager.TYPE_MOBILE,
                        subscriberId,
                        startTime,
                        endTime
                    )
                    return UsageResult(
                        bytes = bucket.rxBytes + bucket.txBytes,
                        source = UsageSource.PRECISE,
                        canSeparateSims = true
                    )
                } catch (e: Exception) {
                    // Fallthrough to Layer 2/3
                }
            }
            
            // Layer 2: PRECISE combined-SIM usage (no subscriberId available)
            try {
                val bucket = statsManager.querySummaryForDevice(
                    ConnectivityManager.TYPE_MOBILE,
                    null, // null = all mobile data across all SIMs
                    startTime,
                    endTime
                )
                return UsageResult(
                    bytes = bucket.rxBytes + bucket.txBytes,
                    source = UsageSource.PRECISE,
                    canSeparateSims = false
                )
            } catch (e: Exception) {
                // Ignore, fall through to TrafficStats
            }
        }

        // Layer 3: TrafficStats (ZERO permissions fallback)
        val rxBytes = TrafficStats.getMobileRxBytes()
        val txBytes = TrafficStats.getMobileTxBytes()
        if (rxBytes != TrafficStats.UNSUPPORTED.toLong()) {
            return UsageResult(
                bytes = rxBytes + txBytes,
                source = UsageSource.APPROXIMATE,
                canSeparateSims = false
            )
        }

        // Layer 4: UNAVAILABLE
        return UsageResult(-1L, UsageSource.UNAVAILABLE, false)
    }
}
