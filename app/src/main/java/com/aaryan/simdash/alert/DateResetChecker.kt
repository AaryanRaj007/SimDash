package com.aaryan.simdash.alert

import com.aaryan.simdash.data.prefs.SettingsDataStore
import com.aaryan.simdash.data.repository.AlertRepository
import com.aaryan.simdash.util.DateUtils
import javax.inject.Inject

class DateResetChecker @Inject constructor(
    private val settingsDataStore: SettingsDataStore,
    private val alertRepository: AlertRepository
) {
    suspend fun checkAndRunMidnightResetIfNeeded(activeSubId: Int) {
        val lastReset = settingsDataStore.getLastResetDateSync()
        val todayIst = DateUtils.getTodayDateStringIst()

        if (lastReset != todayIst) {
            // Run reset
            if (activeSubId != -1) {
                alertRepository.resetAllFiredToday(activeSubId)
            }
            settingsDataStore.setLastResetDate(todayIst)
        }
    }
}
