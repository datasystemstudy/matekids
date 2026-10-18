package com.educa.mathkids.domain.engine

import com.educa.mathkids.domain.model.Difficulty
import kotlin.random.Random

data class NumberRange(val min: Int, val max: Int) {
    fun random(): Int = Random.nextInt(min, max + 1)
}

/**
 * Configuración centralizada de dificultad para evitar números mágicos.
 */
data class DifficultyConfig(
    val additionRange1: NumberRange,
    val additionRange2: NumberRange,
    val subtractionRange1: NumberRange,
    val subtractionRange2: NumberRange,
    val multiplicationRange1: NumberRange,
    val multiplicationRange2: NumberRange,
    val divisionDivisorRange: NumberRange,
    val divisionQuotientRange: NumberRange,
    val allowNegative: Boolean = false,
    val allowRemainder: Boolean = false
) {
    companion object {
        fun forDifficulty(difficulty: Difficulty): DifficultyConfig {
            return when (difficulty) {
                Difficulty.EASY -> DifficultyConfig(
                    additionRange1 = NumberRange(1, 10),
                    additionRange2 = NumberRange(1, 10),
                    subtractionRange1 = NumberRange(2, 10),
                    subtractionRange2 = NumberRange(1, 10),
                    multiplicationRange1 = NumberRange(1, 5),
                    multiplicationRange2 = NumberRange(1, 5),
                    divisionDivisorRange = NumberRange(1, 3),
                    divisionQuotientRange = NumberRange(1, 5),
                    allowNegative = false,
                    allowRemainder = false
                )
                Difficulty.BASIC -> DifficultyConfig(
                    additionRange1 = NumberRange(2, 25),
                    additionRange2 = NumberRange(2, 25),
                    subtractionRange1 = NumberRange(5, 25),
                    subtractionRange2 = NumberRange(2, 25),
                    multiplicationRange1 = NumberRange(2, 9),
                    multiplicationRange2 = NumberRange(2, 5),
                    divisionDivisorRange = NumberRange(2, 5),
                    divisionQuotientRange = NumberRange(2, 8),
                    allowNegative = false,
                    allowRemainder = false
                )
                Difficulty.INTERMEDIATE -> DifficultyConfig(
                    additionRange1 = NumberRange(10, 50),
                    additionRange2 = NumberRange(10, 50),
                    subtractionRange1 = NumberRange(15, 50),
                    subtractionRange2 = NumberRange(5, 50),
                    multiplicationRange1 = NumberRange(3, 9),
                    multiplicationRange2 = NumberRange(3, 9),
                    divisionDivisorRange = NumberRange(3, 9),
                    divisionQuotientRange = NumberRange(3, 10),
                    allowNegative = false,
                    allowRemainder = false
                )
                Difficulty.ADVANCED -> DifficultyConfig(
                    additionRange1 = NumberRange(25, 100),
                    additionRange2 = NumberRange(25, 100),
                    subtractionRange1 = NumberRange(30, 100),
                    subtractionRange2 = NumberRange(10, 100),
                    multiplicationRange1 = NumberRange(6, 12),
                    multiplicationRange2 = NumberRange(4, 12),
                    divisionDivisorRange = NumberRange(4, 12),
                    divisionQuotientRange = NumberRange(4, 12),
                    allowNegative = false,
                    allowRemainder = false
                )
            }
        }
    }
}
