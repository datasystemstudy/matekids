package com.educa.mathkids.domain.usecase

import org.junit.Assert.assertEquals
import org.junit.Test

class CalculateScoreUseCaseTest {

    private val calculateScoreUseCase = CalculateScoreUseCase()

    @Test
    fun testBaseScore_noStreak() {
        val score = calculateScoreUseCase(newStreak = 1)
        assertEquals(10, score)
    }

    @Test
    fun testTier1Bonus_streakThree() {
        assertEquals(15, calculateScoreUseCase(newStreak = 3))
    }

    @Test
    fun testTier2Bonus_streakFive() {
        assertEquals(20, calculateScoreUseCase(newStreak = 5))
    }

    @Test
    fun testTier3Bonus_streakTen() {
        assertEquals(30, calculateScoreUseCase(newStreak = 10))
    }
}
