package com.aaryan.simdash.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aaryan.simdash.data.db.SimProfile
import com.aaryan.simdash.data.repository.SimRepository
import com.aaryan.simdash.sim.ActiveSimDetector
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SimInfoViewModel @Inject constructor(
    private val simRepository: SimRepository,
    private val activeSimDetector: ActiveSimDetector
) : ViewModel() {

    private val _uiState = MutableStateFlow(SimInfoState())
    val uiState: StateFlow<SimInfoState> = _uiState

    init {
        val subId = activeSimDetector.getDefaultDataSubscriptionId()
        if (subId != -1) {
            viewModelScope.launch {
                simRepository.getAllProfiles().collectLatest { profiles ->
                    profiles.find { it.subscriptionId == subId }?.let { profile ->
                        _uiState.value = _uiState.value.copy(profile = profile)
                    }
                }
            }
        }
    }

    fun updatePlanLimit(limitMb: Long) {
        val profile = _uiState.value.profile ?: return
        viewModelScope.launch {
            val limitBytes = limitMb * 1024L * 1024L
            simRepository.upsertProfile(profile.copy(dailyLimitBytes = limitBytes))
        }
    }

    fun updateUssdCode(code: String) {
        val profile = _uiState.value.profile ?: return
        viewModelScope.launch {
            simRepository.upsertProfile(profile.copy(ussdCode = code))
        }
    }
}

data class SimInfoState(
    val profile: SimProfile? = null
)
