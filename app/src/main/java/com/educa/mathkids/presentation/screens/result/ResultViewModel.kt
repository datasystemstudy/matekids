package com.educa.mathkids.presentation.screens.result

import androidx.lifecycle.ViewModel
import com.educa.mathkids.domain.model.Difficulty
import com.educa.mathkids.domain.model.OperationType
import com.educa.mathkids.domain.model.Reward

data class ResultUiState(
    val operationType: OperationType,
    val difficulty: Difficulty,
    val totalExercises: Int,
    val correctCount: Int,
    val incorrectCount: Int,
    val accuracyPercentage: Int,
    val sessionPoints: Int,
    val totalPoints: Int,
    val maxStreak: Int,
    val currentLevel: Int,
    val unlockedRewards: List<Reward>,
    val celebrationMessage: String,
    val levelNumber: Int = 1,
    val nextLevelUnlocked: Boolean = false,
    val stars: Int = 0
)

class ResultViewModel(
    val operationType: OperationType,
    val difficulty: Difficulty = Difficulty.EASY,
    val totalExercises: Int,
    val correctCount: Int,
    val incorrectCount: Int,
    val accuracyPercentage: Int,
    val sessionPoints: Int = 0,
    val totalPoints: Int = 0,
    val maxStreak: Int = 0,
    val currentLevel: Int = 1,
    val unlockedRewards: List<Reward> = emptyList(),
    val levelNumber: Int = 1,
    val nextLevelUnlocked: Boolean = false
) : ViewModel() {

    val starsCalculated: Int = when {
        accuracyPercentage >= 90 -> 3
        accuracyPercentage >= 70 -> 2
        accuracyPercentage >= 50 -> 1
        else -> 0
    }

    val uiState: ResultUiState = ResultUiState(
        operationType = operationType,
        difficulty = difficulty,
        totalExercises = totalExercises,
        correctCount = correctCount,
        incorrectCount = incorrectCount,
        accuracyPercentage = accuracyPercentage,
        sessionPoints = sessionPoints,
        totalPoints = totalPoints,
        maxStreak = maxStreak,
        currentLevel = currentLevel,
        unlockedRewards = unlockedRewards,
        levelNumber = levelNumber,
        nextLevelUnlocked = nextLevelUnlocked,
        stars = starsCalculated,
        celebrationMessage = when {
            accuracyPercentage >= 90 -> "¡Extraordinario trabajo! 🌟 ¡Eres un genio matemático!"
            accuracyPercentage >= 70 -> "¡Muy bien hecho! 🎉 ¡Sigue practicando así!"
            accuracyPercentage >= 50 -> "¡Buen esfuerzo! 💪 ¡Cada día aprendes más!"
            else -> "¡Buen intento! 🚀 ¡La práctica te hará un campeón!"
        }
    )
}
