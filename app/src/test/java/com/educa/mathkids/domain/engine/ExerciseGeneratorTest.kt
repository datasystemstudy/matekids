package com.educa.mathkids.domain.engine

import com.educa.mathkids.domain.model.Difficulty
import com.educa.mathkids.domain.model.OperationType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ExerciseGeneratorTest {

    private lateinit var generator: ExerciseGenerator

    @Before
    fun setUp() {
        generator = DefaultExerciseGenerator()
    }

    @Test
    fun testAddition_generatesValidExerciseAndAccurateResult() {
        repeat(50) {
            val exercise = generator.generate(OperationType.ADDITION, Difficulty.EASY)
            assertEquals(OperationType.ADDITION, exercise.operationType)
            assertEquals(exercise.operand1 + exercise.operand2, exercise.correctAnswer)
            assertEquals(exercise.leftNumber + exercise.rightNumber, exercise.correctAnswer)
            assertEquals(4, exercise.options.size)
            assertEquals(4, exercise.options.toSet().size) // Todas únicas
            assertTrue(exercise.options.contains(exercise.correctAnswer))
            assertTrue(exercise.options.all { it >= 0 })
            assertTrue(generator.isValidExercise(exercise))
        }
    }

    @Test
    fun testSubtraction_neverProducesNegativeResults() {
        repeat(100) {
            val exercise = generator.generate(OperationType.SUBTRACTION, Difficulty.INTERMEDIATE)
            assertEquals(OperationType.SUBTRACTION, exercise.operationType)
            assertTrue(
                "Minuendo debe ser mayor o igual que sustraendo: ${exercise.operand1} >= ${exercise.operand2}",
                exercise.operand1 >= exercise.operand2
            )
            assertTrue(
                "Resultado debe ser positivo o cero: ${exercise.correctAnswer}",
                exercise.correctAnswer >= 0
            )
            assertEquals(exercise.operand1 - exercise.operand2, exercise.correctAnswer)
            assertTrue(exercise.options.contains(exercise.correctAnswer))
            assertTrue(exercise.options.all { it >= 0 })
            assertTrue(generator.isValidExercise(exercise))
        }
    }

    @Test
    fun testMultiplication_generatesAccurateResults() {
        repeat(50) {
            val exercise = generator.generate(OperationType.MULTIPLICATION, Difficulty.BASIC)
            assertEquals(OperationType.MULTIPLICATION, exercise.operationType)
            assertEquals(exercise.operand1 * exercise.operand2, exercise.correctAnswer)
            assertTrue(exercise.options.contains(exercise.correctAnswer))
            assertEquals(4, exercise.options.toSet().size)
            assertTrue(generator.isValidExercise(exercise))
        }
    }

    @Test
    fun testDivision_isAlwaysExactWithoutDecimalsAndDivisorNonZero() {
        repeat(100) {
            val exercise = generator.generate(OperationType.DIVISION, Difficulty.INTERMEDIATE)
            assertEquals(OperationType.DIVISION, exercise.operationType)
            assertTrue("Divisor debe ser mayor que cero", exercise.operand2 > 0)
            assertEquals(
                "La división debe ser exacta (resto 0)",
                0,
                exercise.operand1 % exercise.operand2
            )
            assertEquals(
                exercise.operand1 / exercise.operand2,
                exercise.correctAnswer
            )
            assertTrue(exercise.options.contains(exercise.correctAnswer))
            assertEquals(4, exercise.options.toSet().size)
            assertTrue(generator.isValidExercise(exercise))
        }
    }

    @Test
    fun testDifficultyRanges_respectsConfiguredRanges() {
        // EASY Addition: 1..10
        repeat(30) {
            val easyEx = generator.generate(OperationType.ADDITION, Difficulty.EASY)
            assertTrue(easyEx.operand1 in 1..10)
            assertTrue(easyEx.operand2 in 1..10)
        }

        // ADVANCED Addition: 25..100
        repeat(30) {
            val advEx = generator.generate(OperationType.ADDITION, Difficulty.ADVANCED)
            assertTrue(advEx.operand1 in 25..100)
            assertTrue(advEx.operand2 in 25..100)
        }
    }

    @Test
    fun testRandomness_producesDiverseExercises() {
        val generated = mutableSetOf<Pair<Int, Int>>()
        repeat(20) {
            val ex = generator.generate(OperationType.ADDITION, Difficulty.INTERMEDIATE)
            generated.add(Pair(ex.operand1, ex.operand2))
        }
        // En 20 generaciones en rango 10..50, debe haber al menos 10 pares distintos
        assertTrue("Debe generar ejercicios variados", generated.size >= 10)
    }

    @Test
    fun testPreventionOfConsecutiveDuplicates() {
        var previous: Pair<Int, Int>? = null
        repeat(20) {
            val ex = generator.generate(OperationType.ADDITION, Difficulty.BASIC)
            val current = Pair(ex.operand1, ex.operand2)
            if (previous != null) {
                assertNotEquals("No debe repetir consecutivamente el mismo ejercicio", previous, current)
            }
            previous = current
        }
    }

    @Test
    fun testIsValidExercise_detectsInvalidScenarios() {
        val validEx = generator.generate(OperationType.ADDITION, Difficulty.EASY)
        assertTrue(generator.isValidExercise(validEx))

        // Ejercicio con división por cero
        val zeroDivEx = validEx.copy(
            operationType = OperationType.DIVISION,
            operand1 = 10,
            operand2 = 0,
            correctAnswer = 0
        )
        assertFalse(generator.isValidExercise(zeroDivEx))

        // Ejercicio con opciones duplicadas
        val dupOptionsEx = validEx.copy(
            options = listOf(10, 10, 12, 13)
        )
        assertFalse(generator.isValidExercise(dupOptionsEx))

        // Ejercicio donde la respuesta correcta no está en las opciones
        val missingCorrectEx = validEx.copy(
            correctAnswer = 99,
            options = listOf(10, 11, 12, 13)
        )
        assertFalse(generator.isValidExercise(missingCorrectEx))
    }
}
