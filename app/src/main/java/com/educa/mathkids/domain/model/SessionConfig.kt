package com.educa.mathkids.domain.model

/**
 * Configuración centralizada para sesiones de ejercicios matemáticos.
 * Permite modificar la cantidad de ejercicios por sesión desde un único lugar.
 */
object SessionConfig {
    const val DEFAULT_EXERCISE_COUNT = 10
    val AVAILABLE_SESSION_SIZES = listOf(5, 10, 15, 20)
}
