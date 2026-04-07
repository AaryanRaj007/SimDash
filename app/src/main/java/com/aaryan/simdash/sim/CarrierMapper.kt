package com.aaryan.simdash.sim

object CarrierMapper {
    private val carrierMap = hashMapOf(
        // Jio
        "40502" to CarrierInfo("Jio", "*333#", "*121#"),
        "40503" to CarrierInfo("Jio", "*333#", "*121#"),
        "40504" to CarrierInfo("Jio", "*333#", "*121#"),
        "40550" to CarrierInfo("Jio", "*333#", "*121#"),
        "40551" to CarrierInfo("Jio", "*333#", "*121#"),
        // Airtel
        "40410" to CarrierInfo("Airtel", "*121#", "*123#"),
        "40416" to CarrierInfo("Airtel", "*121#", "*123#"),
        "40449" to CarrierInfo("Airtel", "*121#", "*123#"),
        "40470" to CarrierInfo("Airtel", "*121#", "*123#"),
        "40490" to CarrierInfo("Airtel", "*121#", "*123#"),
        "40492" to CarrierInfo("Airtel", "*121#", "*123#"),
        "40493" to CarrierInfo("Airtel", "*121#", "*123#"),
        "40494" to CarrierInfo("Airtel", "*121#", "*123#"),
        "40495" to CarrierInfo("Airtel", "*121#", "*123#"),
        "40496" to CarrierInfo("Airtel", "*121#", "*123#"),
        "40497" to CarrierInfo("Airtel", "*121#", "*123#"),
        "40498" to CarrierInfo("Airtel", "*121#", "*123#"),
        // Vi (Vodafone Idea)
        "40420" to CarrierInfo("Vi", "*199#", "*111#"),
        "40422" to CarrierInfo("Vi", "*199#", "*111#"),
        "40460" to CarrierInfo("Vi", "*199#", "*111#"),
        "40484" to CarrierInfo("Vi", "*199#", "*111#"),
        "40486" to CarrierInfo("Vi", "*199#", "*111#"),
        // BSNL
        "40401" to CarrierInfo("BSNL", "*123#", "*124#"),
        "40430" to CarrierInfo("BSNL", "*123#", "*124#"),
        "40466" to CarrierInfo("BSNL", "*123#", "*124#"),
        "40474" to CarrierInfo("BSNL", "*123#", "*124#"),
    )

    fun getCarrierInfo(mccMnc: String): CarrierInfo? = carrierMap[mccMnc]

    fun findByName(name: String): CarrierInfo? {
        val lower = name.lowercase()
        return when {
            lower.contains("jio") -> CarrierInfo("Jio", "*333#", "*121#")
            lower.contains("airtel") -> CarrierInfo("Airtel", "*121#", "*123#")
            lower.contains("vi") || lower.contains("vodafone") || lower.contains("idea") -> CarrierInfo("Vi", "*199#", "*111#")
            lower.contains("bsnl") -> CarrierInfo("BSNL", "*123#", "*124#")
            else -> null
        }
    }
}
