package com.aaryan.simdash.sim

import javax.inject.Inject

class UssdParser @Inject constructor() {

    fun parse(response: String, carrierName: String): UssdResult {
        return when {
            carrierName.contains("Jio", ignoreCase = true) -> parseJioResponse(response)
            carrierName.contains("Airtel", ignoreCase = true) -> parseAirtelResponse(response)
            carrierName.contains("Vi", ignoreCase = true) || carrierName.contains("Vodafone", ignoreCase = true) || carrierName.contains("Idea", ignoreCase = true) -> parseViResponse(response)
            carrierName.contains("BSNL", ignoreCase = true) -> parseBsnlResponse(response)
            else -> parseGenericResponse(response)
        }
    }

    private fun parseJioResponse(response: String): UssdResult {
        val patterns = listOf(
            Triple(
                Regex("""(\d+\.?\d*)\s*GB/day""", RegexOption.IGNORE_CASE),
                Regex("""valid till (\d{2}-\w{3}-\d{4})""", RegexOption.IGNORE_CASE),
                PlanType.DAILY_LIMIT
            ),
            Triple(
                Regex("""(\d+\.?\d*)\s*GB/day""", RegexOption.IGNORE_CASE),
                Regex("""Validity:\s*(\d+)\s*days""", RegexOption.IGNORE_CASE),
                PlanType.DAILY_LIMIT
            ),
            Triple(
                Regex("""(\d+\.?\d*)\s*GB/din""", RegexOption.IGNORE_CASE),
                Regex("""Meyad:\s*(\d{2}/\d{2}/\d{4})""", RegexOption.IGNORE_CASE),
                PlanType.DAILY_LIMIT
            ),
            Triple(
                Regex("""(\d+\.?\d*)\s*GB""", RegexOption.IGNORE_CASE),
                null,
                PlanType.TOTAL_POOL
            )
        )

        return executePatterns(response, patterns)
    }

    private fun parseAirtelResponse(response: String): UssdResult {
        val patterns = listOf(
            Triple(
                Regex("""(\d+\.?\d*)\s*GB/Day""", RegexOption.IGNORE_CASE),
                Regex("""Valid.*?(?:till|date)\s*(\d{2}-\w{3}-\d{4}|\d{2}/\d{2}/\d{4})""", RegexOption.IGNORE_CASE),
                PlanType.DAILY_LIMIT
            ),
            Triple(
                Regex("""(\d+\.?\d*)\s*GB/Day""", RegexOption.IGNORE_CASE),
                Regex("""Val.*?(?:till|date)\s*\w+.*?(\d{2}-\w{3}-\d{4}|\d{2}/\d{2}/\d{4})""", RegexOption.IGNORE_CASE),
                PlanType.DAILY_LIMIT
            ),
            Triple(
                Regex("""(\d+\.?\d*)\s*GB""", RegexOption.IGNORE_CASE),
                null,
                PlanType.TOTAL_POOL
            )
        )
        return executePatterns(response, patterns)
    }

    private fun parseViResponse(response: String): UssdResult {
        val patterns = listOf(
            Triple(
                Regex("""(\d+\.?\d*)\s*GB/Day""", RegexOption.IGNORE_CASE),
                Regex("""Valid till\s*(\d{2}-\w{3}-\d{4})""", RegexOption.IGNORE_CASE),
                PlanType.DAILY_LIMIT
            ),
            Triple(
                Regex("""(\d+\.?\d*)\s*GB""", RegexOption.IGNORE_CASE),
                null,
                PlanType.TOTAL_POOL
            )
        )
        return executePatterns(response, patterns)
    }

    private fun parseBsnlResponse(response: String): UssdResult {
        val patterns = listOf(
            Triple(
                Regex("""(\d+\.?\d*)\s*GB/Day""", RegexOption.IGNORE_CASE),
                Regex("""Validity\s*(\d{2}-\d{2}-\d{4}|\d{2}/\d{2}/\d{4})""", RegexOption.IGNORE_CASE),
                PlanType.DAILY_LIMIT
            ),
            Triple(
                Regex("""(\d+\.?\d*)\s*GB""", RegexOption.IGNORE_CASE),
                null,
                PlanType.TOTAL_POOL
            )
        )
        return executePatterns(response, patterns)
    }

    private fun parseGenericResponse(response: String): UssdResult {
        return executePatterns(response, listOf(
            Triple(Regex("""(\d+\.?\d*)\s*GB/day""", RegexOption.IGNORE_CASE), null, PlanType.DAILY_LIMIT),
            Triple(Regex("""(\d+\.?\d*)\s*GB""", RegexOption.IGNORE_CASE), null, PlanType.UNKNOWN)
        ))
    }

    private fun executePatterns(response: String, patterns: List<Triple<Regex, Regex?, PlanType>>): UssdResult {
        for ((dataPattern, validityPattern, planType) in patterns) {
            val dataMatch = dataPattern.find(response)
            if (dataMatch != null) {
                val validityMatch = validityPattern?.find(response)
                val bytes = dataMatch.groupValues[1].toDoubleOrNull()?.let { (it * 1024 * 1024 * 1024).toLong() }
                
                return UssdResult(
                    dailyLimitBytes = if (planType == PlanType.DAILY_LIMIT) bytes else null,
                    totalPoolBytes = if (planType != PlanType.DAILY_LIMIT) bytes else null,
                    validityDateString = validityMatch?.groupValues?.get(1),
                    rawResponse = response,
                    planType = planType,
                    parseSuccess = planType != PlanType.UNKNOWN && bytes != null
                )
            }
        }
        return UssdResult(rawResponse = response, parseSuccess = false)
    }
}
