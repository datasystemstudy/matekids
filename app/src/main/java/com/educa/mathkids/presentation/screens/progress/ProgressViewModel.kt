package com.educa.mathkids.presentation.screens.progress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.educa.mathkids.domain.model.GameSessionRecord
import com.educa.mathkids.domain.model.OperationStats
import com.educa.mathkids.domain.model.OperationType
import com.educa.mathkids.domain.model.ProgressStats
import com.educa.mathkids.domain.usecase.GetOperationStatsUseCase
import com.educa.mathkids.domain.usecase.GetProgressStatsUseCase
import com.educa.mathkids.domain.usecase.GetSessionHistoryUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

sealed interface ProgressUiState {
    object Loading : ProgressUiState

    data class Empty(
        val totalPoints: Int = 0,
        val currentLevel: Int = 1
    ) : ProgressUiState

    data class Success(
        val stats: ProgressStats,
        val operationStats: List<OperationStats>,
        val recentSessions: List<GameSessionRecord>,
        val selectedFilter: OperationType? = null
    ) : ProgressUiState

    data class Error(val message: String) : ProgressUiState
}

class ProgressViewModel(
    private val getProgressStatsUseCase: GetProgressStatsUseCase,
    private val getOperationStatsUseCase: GetOperationStatsUseCase,
    private val getSessionHistoryUseCase: GetSessionHistoryUseCase
) : ViewModel() {

    private val _selectedFilter = MutableStateFlow<OperationType?>(null)

    val uiState: StateFlow<ProgressUiState> = combine(
        getProgressStatsUseCase(),
        getOperationStatsUseCase(),
        getSessionHistoryUseCase(limit = 5),
        _selectedFilter
    ) { stats, opStats, recentSessions, filter ->
        if (stats.totalSessions == 0 && stats.totalExercises == 0) {
            ProgressUiState.Empty(
                totalPoints = stats.totalPoints,
                currentLevel = stats.currentLevel
            )
        } else {
            val filteredOps = if (filter == null) {
                opStats
            } else {
                opStats.filter { it.operationType == filter }
            }

            val filteredSessions = if (filter == null) {
                recentSessions
            } else {
                recentSessions.filter { it.operationType == filter }
            }

            ProgressUiState.Success(
                stats = stats,
                operationStats = filteredOps,
                recentSessions = filteredSessions,
                selectedFilter = filter
            )
        }
    }
    .catch { e ->
        emit(ProgressUiState.Error(e.localizedMessage ?: "Error al cargar el progreso"))
    }
    .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ProgressUiState.Loading
    )

    fun onFilterSelected(operationType: OperationType?) {
        _selectedFilter.value = operationType
    }
}
