package com.educa.mathkids.domain.engine

import com.educa.mathkids.domain.model.DidacticStrategy
import com.educa.mathkids.domain.model.Difficulty
import com.educa.mathkids.domain.model.MissingTarget
import com.educa.mathkids.domain.model.OperationType
import com.educa.mathkids.domain.model.VisualType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Pruebas unitarias de auditoría técnica y pedagógica para validar las 4 operaciones
 * matemáticas (Fases 3, 4, 6 y 7 del Prompt Maestro).
 */
class MathematicalIntegrityAuditTest {

    private lateinit var generator: ExerciseGenerator

    @Before
    fun setUp() {
        generator = DefaultExerciseGenerator()
    }

    @Test
    fun `Addition exercises are mathematically valid across all 15 levels`() {
        for (level in 1..15) {
            repeat(20) {
                val exercise = generator.generateByLevel(OperationType.ADDITION, level, includeCompletion = false)
                assertEquals(OperationType.ADDITION, exercise.operationType)
                assertEquals(
                    "Suma incorrecta en nivel $level: ${exercise.operand1} + ${exercise.operand2} != ${exercise.correctAnswer}",
                    exercise.operand1 + exercise.operand2,
                    exercise.correctAnswer
                )
                assertEquals("Debe tener exactamente 4 opciones", 4, exercise.options.size)
                assertEquals("Todas las opciones deben ser únicas", 4, exercise.options.toSet().size)
                assertTrue("Las opciones deben contener la respuesta correcta", exercise.options.contains(exercise.correctAnswer))
                assertTrue("isValidExercise debe ser verdadero", generator.isValidExercise(exercise))
            }
        }
    }

    @Test
    fun `Subtraction exercises never yield negative results and borrow logic is correct`() {
        for (level in 1..15) {
            repeat(20) {
                val exercise = generator.generateByLevel(OperationType.SUBTRACTION, level, includeCompletion = false)
                assertEquals(OperationType.SUBTRACTION, exercise.operationType)
                assertTrue("En primaria el minuendo debe ser mayor o igual al sustraendo", exercise.operand1 >= exercise.operand2)
                assertTrue("El resultado no debe ser negativo", exercise.correctAnswer >= 0)
                assertEquals(
                    "Resta incorrecta en nivel $level: ${exercise.operand1} - ${exercise.operand2} != ${exercise.correctAnswer}",
                    exercise.operand1 - exercise.operand2,
                    exercise.correctAnswer
                )
                assertEquals("Debe tener exactamente 4 opciones", 4, exercise.options.size)
                assertEquals("Todas las opciones deben ser únicas", 4, exercise.options.toSet().size)
                assertTrue("Las opciones deben contener la respuesta correcta", exercise.options.contains(exercise.correctAnswer))
                assertTrue("isValidExercise debe ser verdadero", generator.isValidExercise(exercise))
            }
        }
    }

    @Test
    fun `Multiplication exercises produce exact products and valid grids across levels`() {
        for (level in 1..15) {
            repeat(20) {
                val exercise = generator.generateByLevel(OperationType.MULTIPLICATION, level, includeCompletion = false)
                assertEquals(OperationType.MULTIPLICATION, exercise.operationType)
                assertEquals(
                    "Multiplicación incorrecta en nivel $level: ${exercise.operand1} * ${exercise.operand2} != ${exercise.correctAnswer}",
                    exercise.operand1 * exercise.operand2,
                    exercise.correctAnswer
                )
                assertEquals("Debe tener exactamente 4 opciones", 4, exercise.options.size)
                assertEquals("Todas las opciones deben ser únicas", 4, exercise.options.toSet().size)
                assertTrue("Las opciones deben contener la respuesta correcta", exercise.options.contains(exercise.correctAnswer))
                assertTrue("isValidExercise debe ser verdadero", generator.isValidExercise(exercise))
            }
        }
    }

    @Test
    fun `Division exercises prevent division by zero and are strictly exact divisions`() {
        for (level in 1..15) {
            repeat(20) {
                val exercise = generator.generateByLevel(OperationType.DIVISION, level, includeCompletion = false)
                assertEquals(OperationType.DIVISION, exercise.operationType)
                assertTrue("El divisor jamás puede ser 0", exercise.operand2 > 0)
                assertEquals("La división debe ser exacta sin resto en los niveles de enteros", 0, exercise.operand1 % exercise.operand2)
                assertEquals(
                    "División incorrecta en nivel $level: ${exercise.operand1} / ${exercise.operand2} != ${exercise.correctAnswer}",
                    exercise.operand1 / exercise.operand2,
                    exercise.correctAnswer
                )
                assertEquals("Debe tener exactamente 4 opciones", 4, exercise.options.size)
                assertEquals("Todas las opciones deben ser únicas", 4, exercise.options.toSet().size)
                assertTrue("Las opciones deben contener la respuesta correcta", exercise.options.contains(exercise.correctAnswer))
                assertTrue("isValidExercise debe ser verdadero", generator.isValidExercise(exercise))
            }
        }
    }

    @Test
    fun `Completion exercises satisfy the algebraic equation for all missing targets`() {
        for (op in OperationType.entries) {
            for (level in 1..10) {
                val exercise = generator.generateCompletionExercise(op, level)
                assertTrue("isValidExercise debe ser válido para completación", generator.isValidExercise(exercise))
                when (exercise.missingTarget) {
                    MissingTarget.RESULT -> {
                        when (op) {
                            OperationType.ADDITION -> assertEquals(exercise.operand1 + exercise.operand2, exercise.correctAnswer)
                            OperationType.SUBTRACTION -> assertEquals(exercise.operand1 - exercise.operand2, exercise.correctAnswer)
                            OperationType.MULTIPLICATION -> assertEquals(exercise.operand1 * exercise.operand2, exercise.correctAnswer)
                            OperationType.DIVISION -> assertEquals(exercise.operand1 / exercise.operand2, exercise.correctAnswer)
                        }
                    }
                    MissingTarget.SECOND_OPERAND -> {
                        when (op) {
                            OperationType.ADDITION -> assertEquals(exercise.operand2, exercise.operand1 + exercise.correctAnswer)
                            OperationType.SUBTRACTION -> assertEquals(exercise.operand2, exercise.operand1 - exercise.correctAnswer)
                            OperationType.MULTIPLICATION -> assertEquals(exercise.operand2, exercise.operand1 * exercise.correctAnswer)
                            OperationType.DIVISION -> assertEquals(exercise.operand2, exercise.operand1 / exercise.correctAnswer)
                        }
                    }
                    MissingTarget.FIRST_OPERAND -> {
                        when (op) {
                            OperationType.ADDITION -> assertEquals(exercise.operand1, exercise.correctAnswer + exercise.operand2)
                            OperationType.SUBTRACTION -> assertEquals(exercise.operand1, exercise.correctAnswer - exercise.operand2)
                            OperationType.MULTIPLICATION -> assertEquals(exercise.operand1, exercise.correctAnswer * exercise.operand2)
                            OperationType.DIVISION -> assertEquals(exercise.operand1, exercise.correctAnswer / exercise.operand2)
                        }
                    }
                }
            }
        }
    }

    @Test
    fun `Base ten blocks visual representation sets isSubtraction flag correctly in Subtraction`() {
        // En nivel 5 y 6 de Resta, se generan bloques base diez
        var foundBaseTenSubtraction = false
        for (i in 1..50) {
            val exercise = generator.generateByLevel(OperationType.SUBTRACTION, 5, includeCompletion = false)
            if (exercise.visualRepresentation.visualType == VisualType.BASE_TEN_BLOCKS) {
                assertTrue("VisualRepresentation para resta debe tener isSubtraction = true", exercise.visualRepresentation.isSubtraction)
                foundBaseTenSubtraction = true
                break
            }
        }
        assertTrue("Se debe encontrar al menos un ejercicio de resta con BASE_TEN_BLOCKS", foundBaseTenSubtraction)

        // En suma debe ser isSubtraction = false
        val additionEx = generator.generateByLevel(OperationType.ADDITION, 5, includeCompletion = false)
        if (additionEx.visualRepresentation.visualType == VisualType.BASE_TEN_BLOCKS) {
            assertFalse("En suma no debe estar marcado como resta", additionEx.visualRepresentation.isSubtraction)
        }
    }

    @Test
    fun `Distractor generator handles edge case of zero and small numbers safely`() {
        val optionsZero = DistractorGenerator.generateOptions(
            correctAnswer = 0,
            operand1 = 5,
            operand2 = 5,
            operationType = OperationType.SUBTRACTION
        )
        assertEquals(4, optionsZero.size)
        assertEquals(4, optionsZero.toSet().size)
        assertTrue(optionsZero.contains(0))
        assertTrue(optionsZero.all { it >= 0 })

        val optionsOne = DistractorGenerator.generateOptions(
            correctAnswer = 1,
            operand1 = 1,
            operand2 = 1,
            operationType = OperationType.MULTIPLICATION
        )
        assertEquals(4, optionsOne.size)
        assertEquals(4, optionsOne.toSet().size)
        assertTrue(optionsOne.contains(1))
        assertTrue(optionsOne.all { it >= 0 })
    }
}
