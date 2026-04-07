package com.aaryan.simdash.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aaryan.simdash.data.prefs.SettingsDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsDataStore: SettingsDataStore
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsState())
    val uiState: StateFlow<SettingsState> = _uiState

    init {
        viewModelScope.launch {
            settingsDataStore.refreshIntervalHours.collectLatest { hours ->
                _uiState.value = _uiState.value.copy(refreshIntervalHours = hours)
            }
        }
    }

    fun updateRefreshInterval(hours: Int) {
        viewModelScope.launch {
            settingsDataStore.setRefreshIntervalHours(hours)
        }
    }

    fun resetOnboarding() {
        viewModelScope.launch {
            settingsDataStore.setSetupComplete(false)
        }
    }
}

data class SettingsState(
    val refreshIntervalHours: Int = 12
)
