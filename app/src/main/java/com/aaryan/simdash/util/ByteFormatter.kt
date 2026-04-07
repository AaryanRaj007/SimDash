package com.aaryan.simdash.util

object ByteFormatter {
    fun format(bytes: Long, approximate: Boolean = false): String {
        return formatMB(bytes, approximate)
    }

    fun formatMB(bytes: Long, approximate: Boolean = false): String {
        val prefix = if (approximate) "~ " else ""
        return when {
            bytes < 0 -> "${prefix}0 MB"
            bytes < 1024 * 1024 -> "${prefix}${bytes / 1024} KB"
            bytes < 1024 * 1024 * 1024 -> "${prefix}${"%.1f".format(bytes / (1024.0 * 1024.0))} MB"
            else -> "${prefix}${"%.2f".format(bytes / (1024.0 * 1024.0 * 1024.0))} GB"
        }
    }
}
