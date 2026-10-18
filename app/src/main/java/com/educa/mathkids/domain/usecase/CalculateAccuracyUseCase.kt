package com.educa.mathkids.domain.usecase

class CalculateAccuracyUseCase {
    /**
     * Calcula el porcentaje de aciertos de forma segura evitando división por cero.
     * Ejemplo: 8 correctas de 10 ejercicios -> 80%
     */
    operator fun invoke(correctCount: Int, totalExercises: Int): Int {
        if (totalExercises <= 0) return 0
        val percentage = (correctCount.toFloat() / totalExercises) * 100f
        return percentage.toInt().coerceIn(0, 100)
    }
}
