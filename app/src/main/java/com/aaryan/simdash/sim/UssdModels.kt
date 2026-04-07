package com.aaryan.simdash.sim

data class CarrierInfo(
    val name: String,
    val primaryUssd: String,    // Most reliable USSD code for data balance
    val fallbackUssd: String    // Backup if primary fails
)

data class UssdResult(
    val dailyLimitBytes: Long? = null,     // for daily limit plans (Jio 1.5GB/day)
    val totalPoolBytes: Long? = null,       // for total pool plans (Airtel 84GB/month)
    val validityDateString: String? = null, // "15-May-2026" or "28 days"
    val validityDaysRemaining: Int? = null, // computed from validityDateString
    val rawResponse: String = "",
    val parseSuccess: Boolean = false,
    val planType: PlanType = PlanType.UNKNOWN
)

enum class PlanType {
    DAILY_LIMIT,    // Jio-style: X GB per day, resets daily
    TOTAL_POOL,     // Airtel-style: X GB total for N days
    TRULY_UNLIMITED,// No data cap at all
    UNKNOWN         // Could not parse
}
