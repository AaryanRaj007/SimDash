package com.aaryan.simdash.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aaryan.simdash.data.db.SimProfile
import com.aaryan.simdash.data.prefs.SettingsDataStore
import com.aaryan.simdash.data.repository.SimRepository
import com.aaryan.simdash.sim.ActiveSimDetector
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val settingsDataStore: SettingsDataStore,
    private val simRepository: SimRepository,
    private val activeSimDetector: ActiveSimDetector
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingState())
    val uiState: StateFlow<OnboardingState> = _uiState

    init {
        detectSim()
    }

    fun detectSim() {
        val carrierName = activeSimDetector.getCarrierName()
        val mccMnc = activeSimDetector.getMccMnc()
        val subId = activeSimDetector.getDefaultDataSubscriptionId()
        
        _uiState.value = _uiState.value.copy(
            detectedCarrier = carrierName,
            mccMnc = mccMnc,
            subId = subId
        )
    }

    fun selectPlanType(planType: String) {
        _uiState.value = _uiState.value.copy(selectedPlanType = planType)
    }

    fun completeOnboarding(onSuccess: () -> Unit) {
        viewModelScope.launch {
            val state = _uiState.value
            if (state.subId != -1 && state.subId != -1) {
                // Initialize default SIM Profile
                val profile = SimProfile(
                    subscriptionId = state.subId,
                    simSlotIndex = activeSimDetector.getSimSlotIndex(),
                    carrierName = state.detectedCarrier,
                    displayName = state.detectedCarrier,
                    mccMnc = state.mccMnc,
                    ussdCode = "", 
                    planType = state.selectedPlanType.ifEmpty { "UNKNOWN" },
                    dailyLimitBytes = 0L, 
                    totalPoolBytes = 0L,
                    validityEndDate = 0L,
                    lastUssdFetchAt = 0L,
                    todayUsedBytes = 0L,
                    lastUpdatedAt = System.currentTimeMillis(),
                    canSeparateSims = true,
                    morningAlertEnabled = false,
                    morningAlertTimeHour = 8,
                    morningAlertTimeMinute = 0,
                    validityAlertEnabled = true,
                    widgetTheme = "system"
                )
                simRepository.upsertProfile(profile)
            }
            settingsDataStore.setSetupComplete(true)
            onSuccess()
        }
    }
}

data class OnboardingState(
    val detectedCarrier: String = "Unknown",
    val mccMnc: String = "",
    val subId: Int = -1,
    val selectedPlanType: String = "" 
)
