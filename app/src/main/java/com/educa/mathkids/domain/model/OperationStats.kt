package com.educa.mathkids.domain.model

/**
 * Modelo de dominio para las estadísticas individuales por cada operación matemática (Fase 8).
 */
data class OperationStats(
    val operationType: OperationType,
    val exercisesCompleted: Int = 0,
    val correctAnswers: Int = 0,
    val incorrectAnswers: Int = 0,
    val accuracy: Int = 0,
    val bestStreak: Int = 0
)
