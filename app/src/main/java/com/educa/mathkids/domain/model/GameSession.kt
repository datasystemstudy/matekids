package com.educa.mathkids.domain.model

/**
 * Modelo de dominio para representar una sesión activa de ejercicios matemáticos en memoria.
 * Incluye seguimiento de puntuación de la sesión, racha actual y mejor racha (Fase 7).
 */
data class GameSession(
    val sessionId: String,
    val operationType: OperationType,
    val difficulty: Difficulty = Difficulty.EASY,
    val levelNumber: Int = 1,
    val totalExercises: Int = SessionConfig.DEFAULT_EXERCISE_COUNT,
    val currentExerciseIndex: Int = 0,
    val currentExercise: Exercise? = null,
    val correctCount: Int = 0,
    val incorrectCount: Int = 0,
    val results: List<AnswerResult> = emptyList(),
    val isCompleted: Boolean = false,
    val sessionPoints: Int = 0,
    val currentStreak: Int = 0,
    val bestStreak: Int = 0,
    val lastPointsEarned: Int = 0,
    val lastStreakBonus: Int = 0,
    val streakBonusMessage: String? = null
) {
    val currentExerciseNumber: Int get() = currentExerciseIndex + 1
    val exercisesAnswered: Int get() = results.size
    val maxStreak: Int get() = bestStreak
    val score: Int get() = sessionPoints
    val accuracyPercentage: Int
        get() = if (totalExercises == 0) 0 else ((correctCount.toFloat() / totalExercises) * 100).toInt()

    fun toSessionResult(
        totalPoints: Int = sessionPoints,
        currentLevel: Int = ScoringConfig.calculateLevel(totalPoints),
        unlockedRewards: List<Reward> = emptyList(),
        nextLevelUnlocked: Boolean = false
    ): SessionResult {
        return SessionResult(
            sessionId = sessionId,
            operationType = operationType,
            difficulty = difficulty,
            totalExercises = totalExercises,
            correctCount = correctCount,
            incorrectCount = incorrectCount,
            accuracyPercentage = accuracyPercentage,
            sessionPoints = sessionPoints,
            totalPoints = totalPoints,
            maxStreak = bestStreak,
            currentLevel = currentLevel,
            unlockedRewards = unlockedRewards,
            levelNumber = levelNumber,
            nextLevelUnlocked = nextLevelUnlocked
        )
    }
}
