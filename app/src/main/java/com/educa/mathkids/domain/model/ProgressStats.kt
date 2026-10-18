package com.educa.mathkids.domain.model

/**
 * Modelo de dominio con las estadísticas globales del jugador (Fase 8).
 */
data class ProgressStats(
    val totalPoints: Int = 0,
    val currentLevel: Int = 1,
    val currentStreak: Int = 0,
    val bestStreak: Int = 0,
    val totalSessions: Int = 0,
    val totalExercises: Int = 0,
    val correctAnswers: Int = 0,
    val incorrectAnswers: Int = 0,
    val overallAccuracy: Int = 0,
    val lastSessionDate: Long? = null
)
