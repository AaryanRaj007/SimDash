package com.aaryan.simdash.data.prefs

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "simdash_settings")

class SettingsDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        val SETUP_COMPLETE = booleanPreferencesKey("setup_complete")
        val LAST_RESET_DATE = stringPreferencesKey("last_reset_date")
        val REFRESH_INTERVAL_HOURS = intPreferencesKey("refresh_interval_hours") // -1 = manual only
    }

    val isSetupComplete: Flow<Boolean> = context.dataStore.data.map { it[SETUP_COMPLETE] ?: false }
    val lastResetDateStr: Flow<String> = context.dataStore.data.map { it[LAST_RESET_DATE] ?: "" }
    val refreshIntervalHours: Flow<Int> = context.dataStore.data.map { it[REFRESH_INTERVAL_HOURS] ?: 12 } // 12 hours default

    suspend fun setSetupComplete(complete: Boolean) {
        context.dataStore.edit { it[SETUP_COMPLETE] = complete }
    }

    suspend fun setLastResetDate(date: String) {
        context.dataStore.edit { it[LAST_RESET_DATE] = date }
    }

    suspend fun setRefreshIntervalHours(hours: Int) {
        context.dataStore.edit { it[REFRESH_INTERVAL_HOURS] = hours }
    }
    
    // Imperative fallback where flow isn't appropriate
    suspend fun getLastResetDateSync(): String {
        var date = ""
        context.dataStore.edit {
            date = it[LAST_RESET_DATE] ?: ""
        }
        return date
    }
}
