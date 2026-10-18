package com.educa.mathkids.domain.model

/**
 * Modelo de dominio para representar el registro de una sesión completada en el historial (Fase 8).
 */
data class GameSessionRecord(
    val sessionId: String,
    val timestamp: Long,
    val operationType: OperationType,
    val difficulty: Difficulty,
    val totalExercises: Int,
    val correctCount: Int,
    val incorrectCount: Int,
    val accuracyPercentage: Int,
    val pointsEarned: Int,
    val maxStreak: Int
)
