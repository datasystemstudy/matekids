package com.educa.mathkids.domain.engine

import com.educa.mathkids.domain.model.Difficulty
import com.educa.mathkids.domain.model.OperationType
import com.educa.mathkids.domain.model.SessionResult
import com.educa.mathkids.domain.model.VisualType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Pruebas unitarias de calidad para las 20 fases del Prompt Maestro:
 * - 10 niveles progresivos para Suma, Resta, Multiplicación y División.
 * - Validación matemática estricta (sin división por cero, restas no negativas).
 * - Generación de representaciones visuales (conteo, tachado, grupos, canastas, bloques, problemas).
 * - Umbrales de estrellas y desbloqueo progresivo.
 */
class TenLevelsProgressionTest {

    private val generator = DefaultExerciseGenerator.shared

    @Test
    fun testAddition_allTenLevelsGenerateValidExercises() {
        for (level in 1..10) {
            repeat(10) {
                val ex = generator.generateByLevel(OperationType.ADDITION, level, includeCompletion = false)
                assertEquals(OperationType.ADDITION, ex.operationType)
                assertEquals(level, ex.level)
                assertEquals(ex.operand1 + ex.operand2, ex.correctAnswer)
                assertTrue(generator.isValidExercise(ex))

                when (level) {
                    1 -> {
                        assertEquals(VisualType.OBJECT_COUNT, ex.visualRepresentation.visualType)
                        assertTrue(ex.operand1 in 1..2)
                        assertTrue(ex.operand2 in 1..2)
                    }
                    2 -> {
                        assertEquals(VisualType.OBJECT_COUNT, ex.visualRepresentation.visualType)
                    }
                    5, 6 -> {
                        assertEquals(VisualType.BASE_TEN_BLOCKS, ex.visualRepresentation.visualType)
                        assertTrue(ex.operand1 >= 10)
                        assertTrue(ex.operand2 >= 10)
                    }
                    7 -> {
                        assertTrue(ex.operand1 >= 100)
                        assertTrue(ex.operand2 >= 100)
                    }
                    8, 9 -> {
                        assertEquals(VisualType.WORD_PROBLEM, ex.visualRepresentation.visualType)
                        assertNotNull(ex.visualRepresentation.problemStory)
                    }
                }
            }
        }
    }

    @Test
    fun testSubtraction_allTenLevelsGenerateValidAndNonNegativeExercises() {
        for (level in 1..10) {
            repeat(10) {
                val ex = generator.generateByLevel(OperationType.SUBTRACTION, level, includeCompletion = false)
                assertEquals(OperationType.SUBTRACTION, ex.operationType)
                assertEquals(level, ex.level)
                assertEquals(ex.operand1 - ex.operand2, ex.correctAnswer)
                assertTrue("Resta no debe ser negativa", ex.correctAnswer >= 0)
                assertTrue("Minuendo debe ser mayor o igual que sustraendo", ex.operand1 >= ex.operand2)
                assertTrue(generator.isValidExercise(ex))

                when (level) {
                    1 -> {
                        assertEquals(VisualType.OBJECT_TAKEOUT, ex.visualRepresentation.visualType)
                        assertTrue(ex.operand1 in 2..5)
                    }
                    5, 6 -> {
                        assertEquals(VisualType.BASE_TEN_BLOCKS, ex.visualRepresentation.visualType)
                        assertTrue(ex.operand1 >= 10)
                    }
                    7 -> {
                        assertTrue(ex.operand1 >= 100)
                    }
                    8, 9 -> {
                        assertEquals(VisualType.WORD_PROBLEM, ex.visualRepresentation.visualType)
                        assertNotNull(ex.visualRepresentation.problemStory)
                    }
                }
            }
        }
    }

    @Test
    fun testMultiplication_allTenLevelsGenerateValidGroupsAndMatrices() {
        for (level in 1..10) {
            repeat(10) {
                val ex = generator.generateByLevel(OperationType.MULTIPLICATION, level, includeCompletion = false)
                assertEquals(OperationType.MULTIPLICATION, ex.operationType)
                assertEquals(level, ex.level)
                assertEquals(ex.operand1 * ex.operand2, ex.correctAnswer)
                assertTrue(generator.isValidExercise(ex))

                when (level) {
                    1 -> {
                        assertEquals(VisualType.GROUPS_GRID, ex.visualRepresentation.visualType)
                        assertEquals(2, ex.operand2) // Grupos de 2
                    }
                    2 -> {
                        assertEquals(VisualType.GROUPS_GRID, ex.visualRepresentation.visualType)
                        assertEquals(3, ex.operand2) // Grupos de 3
                    }
                    3 -> {
                        assertEquals(VisualType.GROUPS_GRID, ex.visualRepresentation.visualType)
                        assertTrue(ex.operand2 in listOf(4, 5)) // Grupos de 4 y 5
                    }
                    8, 9 -> {
                        assertEquals(VisualType.WORD_PROBLEM, ex.visualRepresentation.visualType)
                        assertNotNull(ex.visualRepresentation.problemStory)
                    }
                }
            }
        }
    }

    @Test
    fun testDivision_allTenLevelsPreventDivideByZeroAndEnsureExactDivision() {
        for (level in 1..10) {
            repeat(10) {
                val ex = generator.generateByLevel(OperationType.DIVISION, level, includeCompletion = false)
                assertEquals(OperationType.DIVISION, ex.operationType)
                assertEquals(level, ex.level)

                // Divisor nunca puede ser 0
                assertTrue("Divisor debe ser mayor a 0", ex.operand2 > 0)

                // División siempre exacta
                assertEquals("División debe ser exacta sin resto", 0, ex.operand1 % ex.operand2)
                assertEquals(ex.operand1 / ex.operand2, ex.correctAnswer)
                assertTrue(generator.isValidExercise(ex))

                when (level) {
                    1, 2, 3, 4 -> {
                        assertEquals(VisualType.SHARING_PARTITION, ex.visualRepresentation.visualType)
                        assertTrue(ex.visualRepresentation.containerCount > 0)
                        assertEquals(ex.operand1, ex.visualRepresentation.totalToShare)
                    }
                    8, 9 -> {
                        assertEquals(VisualType.WORD_PROBLEM, ex.visualRepresentation.visualType)
                        assertNotNull(ex.visualRepresentation.problemStory)
                    }
                }
            }
        }
    }

    @Test
    fun testAllTenLevels_completionExercises_areValidForAddition() {
        for (level in 1..10) {
            repeat(10) {
                val ex = generator.generateCompletionExercise(OperationType.ADDITION, level)
                assertEquals(OperationType.ADDITION, ex.operationType)
                assertEquals(level, ex.level)
                assertTrue(generator.isValidExercise(ex))
                assertTrue(ex.displayEquation?.contains("?") == true)
                assertTrue(ex.options.contains(ex.correctAnswer))
                assertEquals(4, ex.options.toSet().size)
            }
        }
    }

    @Test
    fun testAllTenLevels_completionExercises_areValidForSubtraction() {
        for (level in 1..10) {
            repeat(10) {
                val ex = generator.generateCompletionExercise(OperationType.SUBTRACTION, level)
                assertEquals(OperationType.SUBTRACTION, ex.operationType)
                assertEquals(level, ex.level)
                assertTrue(generator.isValidExercise(ex))
                assertTrue(ex.displayEquation?.contains("?") == true)
                assertTrue(ex.options.contains(ex.correctAnswer))
                assertEquals(4, ex.options.toSet().size)
            }
        }
    }

    @Test
    fun testAllTenLevels_completionExercises_areValidForMultiplication() {
        for (level in 1..10) {
            repeat(10) {
                val ex = generator.generateCompletionExercise(OperationType.MULTIPLICATION, level)
                assertEquals(OperationType.MULTIPLICATION, ex.operationType)
                assertEquals(level, ex.level)
                assertTrue(generator.isValidExercise(ex))
                assertTrue(ex.displayEquation?.contains("?") == true)
                assertTrue(ex.options.contains(ex.correctAnswer))
                assertEquals(4, ex.options.toSet().size)
            }
        }
    }

    @Test
    fun testAllTenLevels_completionExercises_areValidForDivision() {
        for (level in 1..10) {
            repeat(10) {
                val ex = generator.generateCompletionExercise(OperationType.DIVISION, level)
                assertEquals(OperationType.DIVISION, ex.operationType)
                assertEquals(level, ex.level)
                assertTrue(generator.isValidExercise(ex))
                assertTrue(ex.displayEquation?.contains("?") == true)
                assertTrue(ex.options.contains(ex.correctAnswer))
                assertEquals(4, ex.options.toSet().size)
            }
        }
    }

    @Test
    fun testAllFifteenLevels_generateValidExercisesForAllOperations() {
        for (op in OperationType.entries) {
            for (level in 1..com.educa.mathkids.domain.model.OperationLevelConfig.MAX_LEVELS_PER_OPERATION) {
                val ex = generator.generateByLevel(op, level, includeCompletion = false)
                assertEquals(op, ex.operationType)
                assertEquals(level, ex.level)
                assertTrue("Exercise for $op level $level must be valid", generator.isValidExercise(ex))
                assertTrue("Options must contain correct answer for $op level $level", ex.options.contains(ex.correctAnswer))
                assertEquals("Options must have 4 distinct values for $op level $level", 4, ex.options.toSet().size)

                when (op) {
                    OperationType.ADDITION -> {
                        assertEquals(ex.operand1 + ex.operand2, ex.correctAnswer)
                    }
                    OperationType.SUBTRACTION -> {
                        assertEquals(ex.operand1 - ex.operand2, ex.correctAnswer)
                        assertTrue("Subtraction must not be negative", ex.correctAnswer >= 0)
                        assertTrue("Minuend >= Subtrahend", ex.operand1 >= ex.operand2)
                    }
                    OperationType.MULTIPLICATION -> {
                        assertEquals(ex.operand1 * ex.operand2, ex.correctAnswer)
                    }
                    OperationType.DIVISION -> {
                        assertTrue("Divisor must be > 0", ex.operand2 > 0)
                        assertEquals("Division must be exact without remainder", 0, ex.operand1 % ex.operand2)
                        assertEquals(ex.operand1 / ex.operand2, ex.correctAnswer)
                    }
                }
            }
        }
    }

    @Test
    fun testAllFifteenLevels_completionExercises_areValidForAllOperations() {
        for (op in OperationType.entries) {
            for (level in 1..com.educa.mathkids.domain.model.OperationLevelConfig.MAX_LEVELS_PER_OPERATION) {
                val ex = generator.generateCompletionExercise(op, level)
                assertEquals(op, ex.operationType)
                assertEquals(level, ex.level)
                assertTrue("Completion exercise for $op level $level must be valid", generator.isValidExercise(ex))
                assertTrue(ex.displayEquation?.contains("?") == true)
                assertTrue(ex.options.contains(ex.correctAnswer))
                assertEquals(4, ex.options.toSet().size)
            }
        }
    }

    @Test
    fun testOperationLevelConfig_allOperationsProvideFifteenLevelsWithValidMetadata() {
        assertEquals(15, com.educa.mathkids.domain.model.OperationLevelConfig.MAX_LEVELS_PER_OPERATION)
        for (op in OperationType.entries) {
            val levels = com.educa.mathkids.domain.model.OperationLevelConfig.getDefaultLevels(op)
            assertEquals(15, levels.size)
            levels.forEachIndexed { index, lvl ->
                assertEquals(op, lvl.operationType)
                assertEquals(index + 1, lvl.levelNumber)
                assertTrue(lvl.title.isNotEmpty())
                assertTrue(lvl.subtitle.isNotEmpty())
                assertTrue(lvl.iconEmoji.isNotEmpty())
                if (index == 0) {
                    assertTrue(lvl.isUnlocked)
                } else {
                    org.junit.Assert.assertFalse(lvl.isUnlocked)
                }
            }
        }
    }

    @Test
    fun testSessionResult_starsCalculationMatchesFases() {
        val res3Stars = SessionResult(
            sessionId = "1",
            operationType = OperationType.ADDITION,
            totalExercises = 10,
            correctCount = 10,
            incorrectCount = 0,
            accuracyPercentage = 100
        )
        assertEquals(3, res3Stars.stars)

        val res2Stars = SessionResult(
            sessionId = "2",
            operationType = OperationType.ADDITION,
            totalExercises = 10,
            correctCount = 8,
            incorrectCount = 2,
            accuracyPercentage = 80
        )
        assertEquals(2, res2Stars.stars)

        val res1Star = SessionResult(
            sessionId = "3",
            operationType = OperationType.ADDITION,
            totalExercises = 10,
            correctCount = 6,
            incorrectCount = 4,
            accuracyPercentage = 60
        )
        assertEquals(1, res1Star.stars)

        val res0Stars = SessionResult(
            sessionId = "4",
            operationType = OperationType.ADDITION,
            totalExercises = 10,
            correctCount = 3,
            incorrectCount = 7,
            accuracyPercentage = 30
        )
        assertEquals(0, res0Stars.stars)
    }
}
