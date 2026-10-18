package com.educa.mathkids.domain.stats

import com.educa.mathkids.domain.model.Difficulty
import com.educa.mathkids.domain.model.GameSessionRecord
import com.educa.mathkids.domain.model.OperationStats
import com.educa.mathkids.domain.model.OperationType
import com.educa.mathkids.domain.usecase.CalculateAccuracyUseCase
import org.junit.Assert.assertEquals
import org.junit.Test

class OperationStatsTest {

    private val calculateAccuracyUseCase = CalculateAccuracyUseCase()

    @Test
    fun testOperations_isolateByOperationType() {
        val sessions = listOf(
            GameSessionRecord(
                sessionId = "s1",
                timestamp = 1000L,
                operationType = OperationType.ADDITION,
                difficulty = Difficulty.EASY,
                totalExercises = 10,
                correctCount = 9,
                incorrectCount = 1,
                accuracyPercentage = 90,
                pointsEarned = 95,
                maxStreak = 5
            ),
            GameSessionRecord(
                sessionId = "s2",
                timestamp = 2000L,
                operationType = OperationType.ADDITION,
                difficulty = Difficulty.BASIC,
                totalExercises = 10,
                correctCount = 8,
                incorrectCount = 2,
                accuracyPercentage = 80,
                pointsEarned = 80,
                maxStreak = 4
            ),
            GameSessionRecord(
                sessionId = "s3",
                timestamp = 3000L,
                operationType = OperationType.MULTIPLICATION,
                difficulty = Difficulty.INTERMEDIATE,
                totalExercises = 10,
                correctCount = 7,
                incorrectCount = 3,
                accuracyPercentage = 70,
                pointsEarned = 70,
                maxStreak = 3
            )
        )

        // Filtrar y calcular para suma
        val additionSessions = sessions.filter { it.operationType == OperationType.ADDITION }
        val addTotal = additionSessions.sumOf { it.totalExercises }
        val addCorrect = additionSessions.sumOf { it.correctCount }
        val addAccuracy = calculateAccuracyUseCase(addCorrect, addTotal)

        val addStats = OperationStats(
            operationType = OperationType.ADDITION,
            exercisesCompleted = addTotal,
            correctAnswers = addCorrect,
            incorrectAnswers = addTotal - addCorrect,
            accuracy = addAccuracy,
            bestStreak = additionSessions.maxOf { it.maxStreak }
        )

        assertEquals(20, addStats.exercisesCompleted)
        assertEquals(17, addStats.correctAnswers)
        assertEquals(3, addStats.incorrectAnswers)
        assertEquals(85, addStats.accuracy)
        assertEquals(5, addStats.bestStreak)

        // Filtrar y calcular para multiplicación
        val multSessions = sessions.filter { it.operationType == OperationType.MULTIPLICATION }
        val multTotal = multSessions.sumOf { it.totalExercises }
        val multCorrect = multSessions.sumOf { it.correctCount }
        val multAccuracy = calculateAccuracyUseCase(multCorrect, multTotal)

        val multStats = OperationStats(
            operationType = OperationType.MULTIPLICATION,
            exercisesCompleted = multTotal,
            correctAnswers = multCorrect,
            incorrectAnswers = multTotal - multCorrect,
            accuracy = multAccuracy,
            bestStreak = multSessions.maxOf { it.maxStreak }
        )

        assertEquals(10, multStats.exercisesCompleted)
        assertEquals(7, multStats.correctAnswers)
        assertEquals(3, multStats.incorrectAnswers)
        assertEquals(70, multStats.accuracy)
        assertEquals(3, multStats.bestStreak)

        // Resta y División no tienen sesiones registradas
        val subSessions = sessions.filter { it.operationType == OperationType.SUBTRACTION }
        assertEquals(0, subSessions.size)

        val divSessions = sessions.filter { it.operationType == OperationType.DIVISION }
        assertEquals(0, divSessions.size)
    }
}
