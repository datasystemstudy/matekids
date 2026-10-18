package com.educa.mathkids.domain.engine

import com.educa.mathkids.domain.model.OperationType
import kotlin.math.max
import kotlin.random.Random

object DistractorGenerator {

    /**
     * Genera exactamente 3 distractores plausibles distintos de la respuesta correcta.
     * Retorna una lista con las 4 opciones (incluyendo la respuesta correcta) barajadas.
     */
    fun generateOptions(
        correctAnswer: Int,
        operand1: Int,
        operand2: Int,
        operationType: OperationType
    ): List<Int> {
        val distractors = mutableSetOf<Int>()

        // 1. Candidatos basados en errores típicos de cálculo
        val candidates = mutableListOf<Int>()

        // Variaciones cercanas (+1, -1, +2, -2)
        candidates.add(correctAnswer + 1)
        if (correctAnswer - 1 >= 0) candidates.add(correctAnswer - 1)
        candidates.add(correctAnswer + 2)
        if (correctAnswer - 2 >= 0) candidates.add(correctAnswer - 2)

        // Variaciones de decena si el número es >= 10
        if (correctAnswer >= 10) {
            candidates.add(correctAnswer + 10)
            if (correctAnswer - 10 >= 0) candidates.add(correctAnswer - 10)
        }

        // Error por confusión de operación
        when (operationType) {
            OperationType.ADDITION -> {
                val sub = operand1 - operand2
                if (sub >= 0) candidates.add(sub)
            }
            OperationType.SUBTRACTION -> {
                candidates.add(operand1 + operand2)
            }
            OperationType.MULTIPLICATION -> {
                candidates.add(operand1 + operand2)
            }
            OperationType.DIVISION -> {
                candidates.add(operand1 - operand2)
            }
        }

        // Barajar candidatos y filtrar
        candidates.shuffled().forEach { candidate ->
            if (candidate >= 0 && candidate != correctAnswer && distractors.size < 3) {
                distractors.add(candidate)
            }
        }

        // Si aún faltan distractores (por colisiones o números pequeños), generar offsets controlados
        var offset = 3
        while (distractors.size < 3) {
            val high = correctAnswer + offset
            if (high != correctAnswer && !distractors.contains(high)) {
                distractors.add(high)
            }
            val low = correctAnswer - offset
            if (distractors.size < 3 && low >= 0 && low != correctAnswer && !distractors.contains(low)) {
                distractors.add(low)
            }
            offset++
        }

        // Combinar con la respuesta correcta y barajar
        val allOptions = (distractors.toList() + correctAnswer).shuffled()
        return allOptions
    }
}
