package com.educa.mathkids.domain.model

/**
 * Modelo de dominio para representar el progreso del jugador.
 * Incluye métricas globales (puntos, rachas, nivel, recompensas)
 * y detalles por operación para las pantallas de progreso.
 */
data class PlayerProgress(
    val playerId: String = "default_player",
    val totalPoints: Int = 0,
    val currentStreak: Int = 0,
    val bestStreak: Int = 0,
    val level: Int = 1,
    val unlockedRewards: List<Reward> = emptyList(),
    val totalSessionsCompleted: Int = 0,
    val additionSolved: Int = 0,
    val subtractionSolved: Int = 0,
    val multiplicationSolved: Int = 0,
    val divisionSolved: Int = 0,

    // Campos por operación para vistas detalladas de progreso
    val operationType: OperationType = OperationType.ADDITION,
    val totalAnswered: Int = 0,
    val totalCorrect: Int = 0,
    val currentLevel: Difficulty = Difficulty.EASY
) {
    val accuracyPercentage: Float
        get() = if (totalAnswered == 0) 0f else (totalCorrect.toFloat() / totalAnswered) * 100f

    val pointsInCurrentLevel: Int
        get() = ScoringConfig.pointsInCurrentLevel(totalPoints)

    val levelProgressFraction: Float
        get() = ScoringConfig.levelProgressFraction(totalPoints)
}
