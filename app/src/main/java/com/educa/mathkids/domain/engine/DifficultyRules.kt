package com.educa.mathkids.domain.engine

import com.educa.mathkids.domain.model.Difficulty
import com.educa.mathkids.domain.model.OperationType

object DifficultyRules {

    fun getRanges(
        operationType: OperationType,
        difficulty: Difficulty
    ): Pair<NumberRange, NumberRange> {
        return when (operationType) {
            OperationType.ADDITION -> when (difficulty) {
                Difficulty.EASY -> Pair(NumberRange(1, 10), NumberRange(1, 10))
                Difficulty.BASIC -> Pair(NumberRange(2, 25), NumberRange(2, 25))
                Difficulty.INTERMEDIATE -> Pair(NumberRange(10, 50), NumberRange(10, 50))
                Difficulty.ADVANCED -> Pair(NumberRange(25, 100), NumberRange(25, 100))
            }
            OperationType.SUBTRACTION -> when (difficulty) {
                Difficulty.EASY -> Pair(NumberRange(2, 10), NumberRange(1, 10))
                Difficulty.BASIC -> Pair(NumberRange(5, 25), NumberRange(2, 25))
                Difficulty.INTERMEDIATE -> Pair(NumberRange(15, 50), NumberRange(5, 50))
                Difficulty.ADVANCED -> Pair(NumberRange(30, 100), NumberRange(10, 100))
            }
            OperationType.MULTIPLICATION -> when (difficulty) {
                Difficulty.EASY -> Pair(NumberRange(1, 5), NumberRange(1, 5))
                Difficulty.BASIC -> Pair(NumberRange(2, 9), NumberRange(2, 5))
                Difficulty.INTERMEDIATE -> Pair(NumberRange(3, 9), NumberRange(3, 9))
                Difficulty.ADVANCED -> Pair(NumberRange(6, 12), NumberRange(4, 12))
            }
            OperationType.DIVISION -> when (difficulty) {
                // Para división: Pair(divisorRange, quotientRange)
                Difficulty.EASY -> Pair(NumberRange(1, 3), NumberRange(1, 5))
                Difficulty.BASIC -> Pair(NumberRange(2, 5), NumberRange(2, 8))
                Difficulty.INTERMEDIATE -> Pair(NumberRange(3, 9), NumberRange(3, 10))
                Difficulty.ADVANCED -> Pair(NumberRange(4, 12), NumberRange(4, 12))
            }
        }
    }
}
