package com.educa.mathkids.domain.model

/**
 * Modelo de dominio para representar una recompensa virtual educativa.
 */
data class Reward(
    val id: String,
    val title: String,
    val description: String,
    val icon: String,
    val isUnlocked: Boolean = false,
    val unlockedAt: Long? = null
)

/**
 * Catálogo centralizado de recompensas educativas de la aplicación.
 */
object RewardsConfig {
    const val ID_FIRST_STAR = "first_star"
    const val ID_FIRE_STREAK = "fire_streak"
    const val ID_STREAK_10 = "streak_10"
    const val ID_CENTURY = "century"
    const val ID_ADDITION_MASTER = "addition_master"
    const val ID_SUBTRACTION_MASTER = "subtraction_master"
    const val ID_MULTIPLICATION_MASTER = "multiplication_master"
    const val ID_DIVISION_MASTER = "division_master"
    const val ID_HALF_THOUSAND = "half_thousand"
    const val ID_ALL_LEVELS = "all_levels_master"

    val INITIAL_REWARDS = listOf(
        Reward(
            id = ID_FIRST_STAR,
            title = "Primer ejercicio",
            description = "¡Has completado tu primera sesión matemática!",
            icon = "🌟"
        ),
        Reward(
            id = ID_FIRE_STREAK,
            title = "Racha de 5",
            description = "¡Conseguiste 5 respuestas correctas seguidas!",
            icon = "🔥"
        ),
        Reward(
            id = ID_STREAK_10,
            title = "Racha de 10",
            description = "¡Imparable! 10 respuestas correctas consecutivas.",
            icon = "⚡"
        ),
        Reward(
            id = ID_CENTURY,
            title = "Centenario",
            description = "¡Alcanzaste 100 puntos acumulados!",
            icon = "🚀"
        ),
        Reward(
            id = ID_ADDITION_MASTER,
            title = "Maestro de la suma",
            description = "¡Dominaste ejercicios de suma!",
            icon = "🟦"
        ),
        Reward(
            id = ID_SUBTRACTION_MASTER,
            title = "Maestro de la resta",
            description = "¡Dominaste ejercicios de resta!",
            icon = "🟩"
        ),
        Reward(
            id = ID_MULTIPLICATION_MASTER,
            title = "Maestro de la multiplicación",
            description = "¡Dominaste ejercicios de multiplicación!",
            icon = "🟪"
        ),
        Reward(
            id = ID_DIVISION_MASTER,
            title = "Maestro de la división",
            description = "¡Dominaste ejercicios de división!",
            icon = "🟧"
        ),
        Reward(
            id = ID_HALF_THOUSAND,
            title = "Gran Campeón",
            description = "¡Alcanzaste 500 puntos acumulados!",
            icon = "💎"
        ),
        Reward(
            id = ID_ALL_LEVELS,
            title = "Maestro Supremo",
            description = "¡Completaste desafíos en todas las operaciones!",
            icon = "👑"
        )
    )
}
