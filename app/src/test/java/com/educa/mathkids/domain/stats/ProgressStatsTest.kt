package com.educa.mathkids.domain.stats

import com.educa.mathkids.domain.model.Difficulty
import com.educa.mathkids.domain.model.GameSessionRecord
import com.educa.mathkids.domain.model.OperationType
import com.educa.mathkids.domain.model.ProgressStats
import com.educa.mathkids.domain.model.ScoringConfig
import com.educa.mathkids.domain.usecase.CalculateAccuracyUseCase
import org.junit.Assert.assertEquals
import org.junit.Test

class ProgressStatsTest {

    private val calculateAccuracyUseCase = CalculateAccuracyUseCase()

    @Test
    fun testProgressStats_aggregation() {
        val records = listOf(
            GameSessionRecord(
                sessionId = "s1",
                timestamp = 1000L,
                operationType = OperationType.ADDITION,
                difficulty = Difficulty.EASY,
                totalExercises = 10,
                correctCount = 8,
                incorrectCount = 2,
                accuracyPercentage = 80,
                pointsEarned = 85,
                maxStreak = 4
            ),
            GameSessionRecord(
                sessionId = "s2",
                timestamp = 2000L,
                operationType = OperationType.SUBTRACTION,
                difficulty = Difficulty.BASIC,
                totalExercises = 10,
                correctCount = 10,
                incorrectCount = 0,
                accuracyPercentage = 100,
                pointsEarned = 120,
                maxStreak = 10
            )
        )

        val totalPoints = records.sumOf { it.pointsEarned } // 85 + 120 = 205
        val currentLevel = ScoringConfig.calculateLevel(totalPoints) // 205 pts -> Nivel 3
        val bestStreak = records.maxOf { it.maxStreak } // 10
        val totalSessions = records.size // 2
        val totalExercises = records.sumOf { it.totalExercises } // 20
        val correctAnswers = records.sumOf { it.correctCount } // 18
        val incorrectAnswers = records.sumOf { it.incorrectCount } // 2
        val overallAccuracy = calculateAccuracyUseCase(correctAnswers, totalExercises) // 18/20 -> 90%

        val stats = ProgressStats(
            totalPoints = totalPoints,
            currentLevel = currentLevel,
            currentStreak = 10,
            bestStreak = bestStreak,
            totalSessions = totalSessions,
            totalExercises = totalExercises,
            correctAnswers = correctAnswers,
            incorrectAnswers = incorrectAnswers,
            overallAccuracy = overallAccuracy,
            lastSessionDate = records.last().timestamp
        )

        assertEquals(205, stats.totalPoints)
        assertEquals(3, stats.currentLevel)
        assertEquals(10, stats.bestStreak)
        assertEquals(2, stats.totalSessions)
        assertEquals(20, stats.totalExercises)
        assertEquals(18, stats.correctAnswers)
        assertEquals(2, stats.incorrectAnswers)
        assertEquals(90, stats.overallAccuracy)
        assertEquals(2000L, stats.lastSessionDate)
    }
}
