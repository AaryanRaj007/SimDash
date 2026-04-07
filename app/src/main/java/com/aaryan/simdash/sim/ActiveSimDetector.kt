package com.aaryan.simdash.sim

import android.content.Context
import android.os.Build
import android.telephony.SubscriptionInfo
import android.telephony.SubscriptionManager
import android.telephony.TelephonyManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ActiveSimDetector @Inject constructor(
    @ApplicationContext private val context: Context
) {

    fun getDefaultDataSubscriptionId(): Int {
        return SubscriptionManager.getDefaultDataSubscriptionId()
    }

    fun getActiveSimInfo(subId: Int = getDefaultDataSubscriptionId()): SubscriptionInfo? {
        if (subId == SubscriptionManager.INVALID_SUBSCRIPTION_ID) return null
        val sm = context.getSystemService(SubscriptionManager::class.java)
        try {
            return sm.getActiveSubscriptionInfo(subId)
        } catch (e: SecurityException) {
            return null
        }
    }

    fun getCarrierName(): String {
        return getActiveSimInfo()?.carrierName?.toString() ?: "Unknown"
    }

    fun getMccMnc(): String {
        // Try getting MCC+MNC from TelephonyManager first
        val tm = context.getSystemService(TelephonyManager::class.java)
        val defaultMccMnc = tm.simOperator ?: ""
        if (defaultMccMnc.isNotEmpty()) return defaultMccMnc
        
        // Fallback to active SIM info if available
        val info = getActiveSimInfo() ?: return ""
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            "${info.mccString}${info.mncString}"
        } else {
            @Suppress("DEPRECATION")
            "${info.mcc}${info.mnc}"
        }
    }

    fun getSimSlotIndex(): Int {
        return getActiveSimInfo()?.simSlotIndex ?: 0
    }

    fun getSubscriberIdSafe(subId: Int): String? {
        val tm = context.getSystemService(TelephonyManager::class.java)
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // createForSubscriptionId requires Android Q
                tm.createForSubscriptionId(subId).subscriberId
            } else {
                @Suppress("DEPRECATION")
                if (subId == getDefaultDataSubscriptionId()) tm.subscriberId else null
            }
        } catch (e: SecurityException) {
            null // Permission denied
        } catch (e: Exception) {
            null // Device doesn't support this
        }
    }
}
