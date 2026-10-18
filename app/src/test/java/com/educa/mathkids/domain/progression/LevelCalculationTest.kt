package com.educa.mathkids.domain.progression

import com.educa.mathkids.domain.model.ScoringConfig
import com.educa.mathkids.domain.usecase.CalculateLevelUseCase
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class LevelCalculationTest {

    private lateinit var calculateLevelUseCase: CalculateLevelUseCase

    @Before
    fun setUp() {
        calculateLevelUseCase = CalculateLevelUseCase()
    }

    @Test
    fun testLevelCalculation_thresholds() {
        // 0 puntos -> nivel 1
        assertEquals(1, ScoringConfig.calculateLevel(0))
        assertEquals(1, calculateLevelUseCase(0).level)

        // 50 puntos -> nivel 1
        assertEquals(1, ScoringConfig.calculateLevel(50))
        assertEquals(1, calculateLevelUseCase(50).level)

        // 99 puntos -> nivel 1
        assertEquals(1, ScoringConfig.calculateLevel(99))
        assertEquals(1, calculateLevelUseCase(99).level)

        // 100 puntos -> nivel 2
        assertEquals(2, ScoringConfig.calculateLevel(100))
        assertEquals(2, calculateLevelUseCase(100).level)

        // 200 puntos -> nivel 3
        assertEquals(3, ScoringConfig.calculateLevel(200))
        assertEquals(3, calculateLevelUseCase(200).level)

        // 500 puntos -> nivel 6
        assertEquals(6, ScoringConfig.calculateLevel(500))
        assertEquals(6, calculateLevelUseCase(500).level)
    }

    @Test
    fun testPointsInLevelAndProgressFraction() {
        val info0 = calculateLevelUseCase(0)
        assertEquals(0, info0.pointsInCurrentLevel)
        assertEquals(0f, info0.progressFraction, 0.001f)

        val info50 = calculateLevelUseCase(50)
        assertEquals(50, info50.pointsInCurrentLevel)
        assertEquals(0.5f, info50.progressFraction, 0.001f)

        val info250 = calculateLevelUseCase(250)
        assertEquals(3, info250.level)
        assertEquals(50, info250.pointsInCurrentLevel)
        assertEquals(0.5f, info250.progressFraction, 0.001f)
    }
}
