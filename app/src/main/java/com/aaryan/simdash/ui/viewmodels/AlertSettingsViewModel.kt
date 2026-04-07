package com.aaryan.simdash.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aaryan.simdash.data.db.AlertThreshold
import com.aaryan.simdash.data.repository.AlertRepository
import com.aaryan.simdash.sim.ActiveSimDetector
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlertSettingsViewModel @Inject constructor(
    private val alertRepository: AlertRepository,
    private val activeSimDetector: ActiveSimDetector
) : ViewModel() {

    private val _uiState = MutableStateFlow(AlertSettingsState())
    val uiState: StateFlow<AlertSettingsState> = _uiState

    init {
        val subId = activeSimDetector.getDefaultDataSubscriptionId()
        if (subId != -1) {
            _uiState.value = _uiState.value.copy(subId = subId)
            viewModelScope.launch {
                alertRepository.getThresholdsForSim(subId).collectLatest { thresholds ->
                    _uiState.value = _uiState.value.copy(thresholds = thresholds)
                }
            }
        }
    }

    fun addThreshold(percent: Int, msg: String, colorHex: String) {
        val subId = _uiState.value.subId
        if (subId == -1) return
        
        viewModelScope.launch {
            val newThreshold = AlertThreshold(
                subscriptionId = subId,
                percentageThreshold = percent,
                customMessage = msg.ifEmpty { null },
                isEnabled = true,
                firedTodayAt = null,
                colorHex = colorHex,
                sortOrder = percent // Default sort by percentage
            )
            alertRepository.upsertThreshold(newThreshold)
        }
    }

    fun toggleThreshold(threshold: AlertThreshold, isEnabled: Boolean) {
        viewModelScope.launch {
            alertRepository.upsertThreshold(threshold.copy(isEnabled = isEnabled))
        }
    }

    fun deleteThreshold(threshold: AlertThreshold) {
        viewModelScope.launch {
            alertRepository.deleteThreshold(threshold)
        }
    }
}

data class AlertSettingsState(
    val subId: Int = -1,
    val thresholds: List<AlertThreshold> = emptyList()
)
