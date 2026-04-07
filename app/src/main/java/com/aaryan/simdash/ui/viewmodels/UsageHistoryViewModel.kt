package com.aaryan.simdash.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aaryan.simdash.data.db.UsageSession
import com.aaryan.simdash.data.repository.UsageRepository
import com.aaryan.simdash.sim.ActiveSimDetector
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UsageHistoryViewModel @Inject constructor(
    private val usageRepository: UsageRepository,
    private val activeSimDetector: ActiveSimDetector
) : ViewModel() {

    private val _uiState = MutableStateFlow(UsageHistoryState())
    val uiState: StateFlow<UsageHistoryState> = _uiState

    init {
        val subId = activeSimDetector.getDefaultDataSubscriptionId()
        if (subId != -1) {
            viewModelScope.launch {
                usageRepository.getLast30Days(subId).collectLatest { sessions ->
                    _uiState.value = _uiState.value.copy(
                        sessions = sessions.sortedByDescending { it.date }
                    )
                }
            }
        }
    }
}

data class UsageHistoryState(
    val sessions: List<UsageSession> = emptyList()
)
