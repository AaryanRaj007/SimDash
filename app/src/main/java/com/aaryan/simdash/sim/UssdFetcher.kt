package com.aaryan.simdash.sim

import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.telephony.TelephonyManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

class UssdFetcher @Inject constructor(
    @ApplicationContext private val context: Context,
    private val activeSimDetector: ActiveSimDetector,
    private val ussdParser: UssdParser
) {
    fun fetchBalance(
        ussdCode: String,
        subId: Int,
        onResult: (UssdResult) -> Unit,
        onError: (String) -> Unit
    ) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            onError("USSD fetch requires Android 8.0+")
            return
        }

        val baseTm = context.getSystemService(TelephonyManager::class.java)
        val tm = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            baseTm.createForSubscriptionId(subId)
        } else {
            // Pre-Q fallback (usually requires the active data SIM)
            if (subId == activeSimDetector.getDefaultDataSubscriptionId()) baseTm else return onError("Cannot fetch USSD for non-default SIM on older Android")
        }

        val scope = CoroutineScope(Dispatchers.Main)
        var timeoutJob: Job? = null

        val callback = object : TelephonyManager.UssdResponseCallback() {
            override fun onReceiveUssdResponse(
                telephonyManager: TelephonyManager,
                request: String,
                response: CharSequence
            ) {
                timeoutJob?.cancel()
                val carrierName = activeSimDetector.getCarrierName()
                val result = ussdParser.parse(response.toString(), carrierName)
                onResult(result)
            }

            override fun onReceiveUssdResponseFailed(
                telephonyManager: TelephonyManager,
                request: String,
                failureCode: Int
            ) {
                timeoutJob?.cancel()
                onError("USSD failed with code $failureCode")
            }
        }

        timeoutJob = scope.launch {
            delay(15_000)
            onError("Timeout — enter plan details manually")
        }

        try {
            tm.sendUssdRequest(ussdCode, callback, Handler(Looper.getMainLooper()))
        } catch (e: SecurityException) {
            timeoutJob.cancel()
            onError("Permission denied to send USSD")
        } catch (e: Exception) {
            timeoutJob.cancel()
            onError("Failed to start USSD request")
        }
    }
}
