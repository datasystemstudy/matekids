package com.educa.mathkids.domain.model

/**
 * Resumen inmutable del resultado de una sesión terminada (Fase 7).
 */
data class SessionResult(
    val sessionId: String,
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
) {
    val stars: Int
        get() = when {
            accuracyPercentage >= 90 -> 3
            accuracyPercentage >= 70 -> 2
            accuracyPercentage >= 50 -> 1
            else -> 0
        }
}
