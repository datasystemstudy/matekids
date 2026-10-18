package com.educa.mathkids.domain.stats

import com.educa.mathkids.domain.usecase.CalculateAccuracyUseCase
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class AccuracyCalculationTest {

    private lateinit var calculateAccuracyUseCase: CalculateAccuracyUseCase

    @Before
    fun setUp() {
        calculateAccuracyUseCase = CalculateAccuracyUseCase()
    }

    @Test
    fun testAccuracy_standardCalculations() {
        // 10 ejercicios / 8 correctos -> 80%
        assertEquals(80, calculateAccuracyUseCase(correctCount = 8, totalExercises = 10))

        // 20 ejercicios / 15 correctos -> 75%
        assertEquals(75, calculateAccuracyUseCase(correctCount = 15, totalExercises = 20))

        // 100 ejercicios / 100 correctos -> 100%
        assertEquals(100, calculateAccuracyUseCase(correctCount = 100, totalExercises = 100))

        // 10 ejercicios / 0 correctos -> 0%
        assertEquals(0, calculateAccuracyUseCase(correctCount = 0, totalExercises = 10))
    }

    @Test
    fun testAccuracy_zeroExercisesAvoidsDivisionByZero() {
        // 0 ejercicios -> 0%
        assertEquals(0, calculateAccuracyUseCase(correctCount = 0, totalExercises = 0))

        // Casos negativos o inválidos
        assertEquals(0, calculateAccuracyUseCase(correctCount = -1, totalExercises = 0))
        assertEquals(0, calculateAccuracyUseCase(correctCount = 5, totalExercises = -5))
    }
}
