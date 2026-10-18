package com.educa.mathkids.domain.model

/**
 * Configuración centralizada de reglas de puntuación, rachas, bonificaciones y niveles.
 */
object ScoringConfig {
    const val CORRECT_ANSWER_POINTS = 10
    const val INCORRECT_ANSWER_POINTS = 0

    // Bonificaciones por racha consecutiva
    const val STREAK_TIER_1_COUNT = 3
    const val STREAK_TIER_1_BONUS = 5

    const val STREAK_TIER_2_COUNT = 5
    const val STREAK_TIER_2_BONUS = 10

    const val STREAK_TIER_3_COUNT = 10
    const val STREAK_TIER_3_BONUS = 20

    // Puntos necesarios por nivel (cada 100 puntos = +1 nivel)
    const val POINTS_PER_LEVEL = 100

    /**
     * Calcula el nivel del jugador a partir de sus puntos totales acumulados.
     * Nivel 1: 0–99
     * Nivel 2: 100–199
     * Nivel 3: 200–299
     * Nivel N: (puntos / 100) + 1
     */
    fun calculateLevel(totalPoints: Int): Int {
        if (totalPoints <= 0) return 1
        return (totalPoints / POINTS_PER_LEVEL) + 1
    }

    /**
     * Puntos acumulados en el nivel actual (0 a 99).
     * Ejemplo: 250 puntos -> 50 puntos dentro del Nivel 3.
     */
    fun pointsInCurrentLevel(totalPoints: Int): Int {
        if (totalPoints <= 0) return 0
        return totalPoints % POINTS_PER_LEVEL
    }

    /**
     * Fracción de progreso hacia el siguiente nivel (0.0f a 1.0f).
     */
    fun levelProgressFraction(totalPoints: Int): Float {
        if (totalPoints <= 0) return 0f
        return (pointsInCurrentLevel(totalPoints).toFloat() / POINTS_PER_LEVEL).coerceIn(0f, 1f)
    }

    /**
     * Retorna la bonificación por alcanzar exactamente una racha determinada.
     */
    fun getStreakBonus(streak: Int): Int {
        return when (streak) {
            STREAK_TIER_1_COUNT -> STREAK_TIER_1_BONUS
            STREAK_TIER_2_COUNT -> STREAK_TIER_2_BONUS
            STREAK_TIER_3_COUNT -> STREAK_TIER_3_BONUS
            else -> 0
        }
    }

    /**
     * Puntos base según dificultad (preparado para futuras extensiones).
     */
    fun pointsForDifficulty(difficulty: Difficulty): Int {
        return when (difficulty) {
            Difficulty.EASY -> CORRECT_ANSWER_POINTS
            Difficulty.BASIC -> CORRECT_ANSWER_POINTS
            Difficulty.INTERMEDIATE -> CORRECT_ANSWER_POINTS
            Difficulty.ADVANCED -> CORRECT_ANSWER_POINTS
        }
    }
}
