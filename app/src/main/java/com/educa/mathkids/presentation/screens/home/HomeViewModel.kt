package com.educa.mathkids.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.educa.mathkids.domain.repository.PlayerProgressRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class HomeUiState(
    val totalPoints: Int = 0,
    val currentStreak: Int = 0,
    val bestStreak: Int = 0,
    val level: Int = 1
)

class HomeViewModel(
    val playerProgressRepository: PlayerProgressRepository
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = playerProgressRepository.getPlayerProfile()
        .map { profile ->
            HomeUiState(
                totalPoints = profile.totalPoints,
                currentStreak = profile.currentStreak,
                bestStreak = profile.bestStreak,
                level = profile.level
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = HomeUiState()
        )

    fun checkStreak(context: android.content.Context, onRecoveryNeeded: (Int) -> Unit) {
        viewModelScope.launch {
            com.educa.mathkids.core.streak.DailyStreakManager.init(context)
            val isPending = com.educa.mathkids.core.streak.DailyStreakManager.checkDailyStreak(context, playerProgressRepository)
            if (isPending) {
                val lostStreak = com.educa.mathkids.core.streak.DailyStreakManager.lostStreakState.value
                onRecoveryNeeded(lostStreak)
            }
        }
    }

    fun recoverStreak(context: android.content.Context, onCompleted: () -> Unit) {
        viewModelScope.launch {
            com.educa.mathkids.core.streak.DailyStreakManager.recoverStreak(context, playerProgressRepository)
            onCompleted()
        }
    }

    fun dismissStreakRecovery(context: android.content.Context) {
        viewModelScope.launch {
            com.educa.mathkids.core.streak.DailyStreakManager.dismissRecovery(context, playerProgressRepository)
        }
    }
}
