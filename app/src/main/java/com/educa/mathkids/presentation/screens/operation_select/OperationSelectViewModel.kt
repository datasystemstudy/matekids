package com.educa.mathkids.presentation.screens.operation_select

import androidx.lifecycle.ViewModel
import com.educa.mathkids.domain.model.Difficulty
import com.educa.mathkids.domain.model.OperationType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class OperationSelectUiState(
    val selectedDifficulty: Difficulty = Difficulty.EASY,
    val availableDifficulties: List<Difficulty> = Difficulty.entries
)

class OperationSelectViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(OperationSelectUiState())
    val uiState: StateFlow<OperationSelectUiState> = _uiState.asStateFlow()

    fun selectDifficulty(difficulty: Difficulty) {
        _uiState.update { it.copy(selectedDifficulty = difficulty) }
    }
}
