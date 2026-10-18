package com.educa.mathkids.domain.engine

import com.educa.mathkids.domain.model.DidacticStrategy
import com.educa.mathkids.domain.model.MissingTarget
import com.educa.mathkids.domain.model.OperationType
import com.educa.mathkids.domain.usecase.GenerateExerciseUseCase
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Pruebas unitarias exhaustivas para los ejercicios de completación
 * en cada uno de los 10 niveles y en las 4 operaciones (Suma, Resta, Multiplicación y División).
 */
class CompletionExercisesTest {

    private lateinit var generator: ExerciseGenerator
    private lateinit var generateExerciseUseCase: GenerateExerciseUseCase

    @Before
    fun setUp() {
        generator = DefaultExerciseGenerator.shared
        generateExerciseUseCase = GenerateExerciseUseCase(generator)
    }

    @Test
    fun testAddition_completion_allTenLevelsProduceValidEquations() {
        for (level in 1..10) {
            repeat(15) {
                val exercise = generator.generateCompletionExercise(OperationType.ADDITION, level)
                assertEquals(OperationType.ADDITION, exercise.operationType)
                assertEquals(level, exercise.level)
                assertEquals(DidacticStrategy.MISSING_NUMBER, exercise.didacticStrategy)
                assertTrue(
                    "El target debe ser FIRST_OPERAND o SECOND_OPERAND",
                    exercise.missingTarget == MissingTarget.FIRST_OPERAND || exercise.missingTarget == MissingTarget.SECOND_OPERAND
                )
                assertNotNull(exercise.displayEquation)
                assertTrue("Debe contener el símbolo '?'", exercise.displayEquation!!.contains("?"))
                assertTrue("Opciones deben contener respuesta correcta", exercise.options.contains(exercise.correctAnswer))
                assertEquals("Debe tener 4 opciones únicas", 4, exercise.options.toSet().size)
                assertTrue("Todas las opciones deben ser no negativas", exercise.options.all { it >= 0 })
                assertTrue("El ejercicio debe ser matemáticamente válido", generator.isValidExercise(exercise))

                // Verificar la ecuación resuelta al sustituir '?'
                val solved = exercise.displayEquation!!.replace("?", "${exercise.correctAnswer}")
                if (exercise.missingTarget == MissingTarget.SECOND_OPERAND) {
                    assertEquals(exercise.operand2, exercise.operand1 + exercise.correctAnswer)
                    assertEquals("${exercise.operand1} + ${exercise.correctAnswer} = ${exercise.operand2}", solved)
                } else {
                    assertEquals(exercise.operand1, exercise.correctAnswer + exercise.operand2)
                    assertEquals("${exercise.correctAnswer} + ${exercise.operand2} = ${exercise.operand1}", solved)
                }
            }
        }
    }

    @Test
    fun testSubtraction_completion_allTenLevelsProduceValidEquations() {
        for (level in 1..10) {
            repeat(15) {
                val exercise = generator.generateCompletionExercise(OperationType.SUBTRACTION, level)
                assertEquals(OperationType.SUBTRACTION, exercise.operationType)
                assertEquals(level, exercise.level)
                assertEquals(DidacticStrategy.MISSING_NUMBER, exercise.didacticStrategy)
                assertTrue(
                    "El target debe ser FIRST_OPERAND o SECOND_OPERAND",
                    exercise.missingTarget == MissingTarget.FIRST_OPERAND || exercise.missingTarget == MissingTarget.SECOND_OPERAND
                )
                assertNotNull(exercise.displayEquation)
                assertTrue("Debe contener el símbolo '?'", exercise.displayEquation!!.contains("?"))
                assertTrue("Respuesta correcta debe ser no negativa", exercise.correctAnswer >= 0)
                assertTrue("Opciones deben contener respuesta correcta", exercise.options.contains(exercise.correctAnswer))
                assertEquals("Debe tener 4 opciones únicas", 4, exercise.options.toSet().size)
                assertTrue("Todas las opciones deben ser no negativas", exercise.options.all { it >= 0 })
                assertTrue("El ejercicio debe ser matemáticamente válido", generator.isValidExercise(exercise))

                val solved = exercise.displayEquation!!.replace("?", "${exercise.correctAnswer}")
                if (exercise.missingTarget == MissingTarget.SECOND_OPERAND) {
                    assertEquals(exercise.operand2, exercise.operand1 - exercise.correctAnswer)
                    assertEquals("${exercise.operand1} - ${exercise.correctAnswer} = ${exercise.operand2}", solved)
                } else {
                    assertEquals(exercise.operand1, exercise.correctAnswer - exercise.operand2)
                    assertEquals("${exercise.correctAnswer} - ${exercise.operand2} = ${exercise.operand1}", solved)
                }
            }
        }
    }

    @Test
    fun testMultiplication_completion_allTenLevelsProduceValidEquations() {
        for (level in 1..10) {
            repeat(15) {
                val exercise = generator.generateCompletionExercise(OperationType.MULTIPLICATION, level)
                assertEquals(OperationType.MULTIPLICATION, exercise.operationType)
                assertEquals(level, exercise.level)
                assertEquals(DidacticStrategy.MISSING_NUMBER, exercise.didacticStrategy)
                assertTrue(
                    "El target debe ser FIRST_OPERAND o SECOND_OPERAND",
                    exercise.missingTarget == MissingTarget.FIRST_OPERAND || exercise.missingTarget == MissingTarget.SECOND_OPERAND
                )
                assertNotNull(exercise.displayEquation)
                assertTrue("Debe contener el símbolo '?'", exercise.displayEquation!!.contains("?"))
                assertTrue("Opciones deben contener respuesta correcta", exercise.options.contains(exercise.correctAnswer))
                assertEquals("Debe tener 4 opciones únicas", 4, exercise.options.toSet().size)
                assertTrue("El ejercicio debe ser matemáticamente válido", generator.isValidExercise(exercise))

                val solved = exercise.displayEquation!!.replace("?", "${exercise.correctAnswer}")
                if (exercise.missingTarget == MissingTarget.SECOND_OPERAND) {
                    assertEquals(exercise.operand2, exercise.operand1 * exercise.correctAnswer)
                    assertEquals("${exercise.operand1} × ${exercise.correctAnswer} = ${exercise.operand2}", solved)
                } else {
                    assertEquals(exercise.operand1, exercise.correctAnswer * exercise.operand2)
                    assertEquals("${exercise.correctAnswer} × ${exercise.operand2} = ${exercise.operand1}", solved)
                }
            }
        }
    }

    @Test
    fun testDivision_completion_allTenLevelsProduceValidEquations() {
        for (level in 1..10) {
            repeat(15) {
                val exercise = generator.generateCompletionExercise(OperationType.DIVISION, level)
                assertEquals(OperationType.DIVISION, exercise.operationType)
                assertEquals(level, exercise.level)
                assertEquals(DidacticStrategy.MISSING_NUMBER, exercise.didacticStrategy)
                assertTrue(
                    "El target debe ser FIRST_OPERAND o SECOND_OPERAND",
                    exercise.missingTarget == MissingTarget.FIRST_OPERAND || exercise.missingTarget == MissingTarget.SECOND_OPERAND
                )
                assertNotNull(exercise.displayEquation)
                assertTrue("Debe contener el símbolo '?'", exercise.displayEquation!!.contains("?"))
                assertTrue("Opciones deben contener respuesta correcta", exercise.options.contains(exercise.correctAnswer))
                assertEquals("Debe tener 4 opciones únicas", 4, exercise.options.toSet().size)
                assertTrue("El ejercicio debe ser matemáticamente válido", generator.isValidExercise(exercise))

                val solved = exercise.displayEquation!!.replace("?", "${exercise.correctAnswer}")
                if (exercise.missingTarget == MissingTarget.SECOND_OPERAND) {
                    assertTrue("Divisor misterioso no debe ser 0", exercise.correctAnswer > 0)
                    assertEquals(0, exercise.operand1 % exercise.correctAnswer)
                    assertEquals(exercise.operand2, exercise.operand1 / exercise.correctAnswer)
                    assertEquals("${exercise.operand1} ÷ ${exercise.correctAnswer} = ${exercise.operand2}", solved)
                } else {
                    assertTrue("Divisor no debe ser 0", exercise.operand2 > 0)
                    assertEquals(0, exercise.correctAnswer % exercise.operand2)
                    assertEquals(exercise.operand1, exercise.correctAnswer / exercise.operand2)
                    assertEquals("${exercise.correctAnswer} ÷ ${exercise.operand2} = ${exercise.operand1}", solved)
                }
            }
        }
    }

    @Test
    fun testGenerateExerciseUseCase_completion_worksForAllOperations() {
        for (op in OperationType.entries) {
            for (level in 1..10) {
                val exercise = generateExerciseUseCase.generateCompletion(op, level)
                assertEquals(op, exercise.operationType)
                assertEquals(level, exercise.level)
                assertTrue(generator.isValidExercise(exercise))
            }
        }
    }
}
