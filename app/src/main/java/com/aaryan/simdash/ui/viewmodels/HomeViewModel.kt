package com.aaryan.simdash.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aaryan.simdash.data.db.SimProfile
import com.aaryan.simdash.data.repository.SimRepository
import com.aaryan.simdash.sim.ActiveSimDetector
import com.aaryan.simdash.sim.DataUsageTracker
import com.aaryan.simdash.sim.UssdFetcher
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val simRepository: SimRepository,
    private val activeSimDetector: ActiveSimDetector,
    private val dataUsageTracker: DataUsageTracker,
    private val ussdFetcher: UssdFetcher
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeState())
    val uiState: StateFlow<HomeState> = _uiState

    init {
        val subId = activeSimDetector.getDefaultDataSubscriptionId()
        if (subId != -1) {
            viewModelScope.launch {
                simRepository.getProfile(subId)?.let { profile ->
                    _uiState.value = _uiState.value.copy(profile = profile)
                }
                
                // Observe changes
                simRepository.getAllProfiles().collectLatest { profiles ->
                    profiles.find { it.subscriptionId == subId }?.let { updated ->
                        _uiState.value = _uiState.value.copy(profile = updated)
                    }
                }
            }
        }
    }

    fun refreshUsage() {
        val profile = _uiState.value.profile ?: return
        _uiState.value = _uiState.value.copy(isRefreshing = true)

        viewModelScope.launch {
            // Check DataUsageTracker
            val usageResult = dataUsageTracker.getTodayUsageBytes(profile.subscriptionId)
            if (usageResult.bytes >= 0) {
                simRepository.updateUsage(
                    profile.subscriptionId, 
                    usageResult.bytes, 
                    System.currentTimeMillis()
                )
            }
            _uiState.value = _uiState.value.copy(isRefreshing = false)
        }
    }

    fun triggerUssd() {
        val profile = _uiState.value.profile ?: return
        _uiState.value = _uiState.value.copy(isRefreshing = true, ussdRawMessage = null)
        
        ussdFetcher.fetchBalance(
            ussdCode = profile.ussdCode,
            subId = profile.subscriptionId,
            onResult = { result ->
                _uiState.value = _uiState.value.copy(
                    ussdRawMessage = result.rawResponse,
                    isRefreshing = false
                )
            },
            onError = { errorMsg ->
                _uiState.value = _uiState.value.copy(
                    ussdRawMessage = "USSD failed: $errorMsg",
                    isRefreshing = false
                )
            }
        )
    }
}

data class HomeState(
    val profile: SimProfile? = null,
    val isRefreshing: Boolean = false,
    val ussdRawMessage: String? = null
)
