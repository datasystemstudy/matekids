package com.educa.mathkids.domain.engine

import com.educa.mathkids.domain.model.DidacticStrategy
import com.educa.mathkids.domain.model.Difficulty
import com.educa.mathkids.domain.model.MissingTarget
import com.educa.mathkids.domain.model.OperationType
import com.educa.mathkids.domain.model.VisualType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Pruebas unitarias para las 11 estrategias didácticas integradas
 * en Suma, Resta, Multiplicación y División.
 */
class DidacticStrategiesTest {

    private lateinit var generator: ExerciseGenerator

    @Before
    fun setUp() {
        generator = DefaultExerciseGenerator()
    }

    @Test
    fun testAllStrategiesGenerateValidExercisesForAllOperations() {
        val operations = OperationType.values()
        val strategies = DidacticStrategy.values()
        val difficulties = listOf(Difficulty.EASY, Difficulty.INTERMEDIATE, Difficulty.ADVANCED)

        for (op in operations) {
            for (strat in strategies) {
                for (diff in difficulties) {
                    repeat(3) {
                        val exercise = generator.generateDidacticExercise(op, strat, diff)

                        assertNotNull("Ejercicio no debe ser nulo para $op, $strat", exercise)
                        assertEquals("Tipo de operación debe coincidir", op, exercise.operationType)
                        assertEquals("Estrategia didáctica debe coincidir", strat, exercise.didacticStrategy)
                        assertEquals(4, exercise.options.size)
                        assertEquals("Todas las opciones deben ser únicas", 4, exercise.options.toSet().size)
                        assertTrue(
                            "Las opciones deben contener la respuesta correcta ${exercise.correctAnswer}",
                            exercise.options.contains(exercise.correctAnswer)
                        )
                        assertTrue(
                            "isValidExercise falló para op=$op, strat=$strat, diff=$diff, target=${exercise.missingTarget}, op1=${exercise.operand1}, op2=${exercise.operand2}, ans=${exercise.correctAnswer}, eq=${exercise.displayEquation}",
                            generator.isValidExercise(exercise)
                        )
                        assertTrue(
                            "displayEquation debe contener incógnita '?'",
                            exercise.displayEquation?.contains("?") == true
                        )
                        assertTrue(
                            "questionText no debe estar vacío",
                            exercise.questionText.isNotBlank()
                        )
                        assertTrue(
                            "explanation pedagógica no debe estar vacía",
                            exercise.explanation.isNotBlank()
                        )
                    }
                }
            }
        }
    }

    @Test
    fun testNumberLineStrategy_hasValidVisualParameters() {
        OperationType.values().forEach { op ->
            repeat(10) {
                val exercise = generator.generateDidacticExercise(op, DidacticStrategy.NUMBER_LINE, Difficulty.EASY)
                val visual = exercise.visualRepresentation

                assertNotNull(visual)
                assertEquals(VisualType.NUMBER_LINE, visual!!.visualType)
                assertTrue("numberLineEnd debe ser mayor que numberLineStart", visual.numberLineEnd > visual.numberLineStart)
                assertTrue("numberLineJump debe ser positivo", visual.numberLineJump > 0)
            }
        }
    }

    @Test
    fun testTenFrameStrategy_hasValidFrameCounts() {
        listOf(OperationType.ADDITION, OperationType.SUBTRACTION, OperationType.MULTIPLICATION, OperationType.DIVISION).forEach { op ->
            repeat(10) {
                val exercise = generator.generateDidacticExercise(op, DidacticStrategy.TEN_FRAME, Difficulty.EASY)
                val visual = exercise.visualRepresentation

                assertNotNull(visual)
                assertEquals(VisualType.TEN_FRAME, visual!!.visualType)
                assertTrue("Capacidad del marco debe ser 10 o 20", visual.tenFrameCapacity == 10 || visual.tenFrameCapacity == 20)
                assertTrue("CountA debe ser positivo", visual.tenFrameCountA > 0)
            }
        }
    }

    @Test
    fun testNumberBondsStrategy_partWholeConsistency() {
        listOf(OperationType.ADDITION, OperationType.SUBTRACTION).forEach { op ->
            repeat(10) {
                val exercise = generator.generateDidacticExercise(op, DidacticStrategy.NUMBER_BONDS, Difficulty.EASY)
                val visual = exercise.visualRepresentation

                assertNotNull(visual)
                assertEquals(VisualType.NUMBER_BONDS, visual!!.visualType)
                val whole = visual.bondWhole
                val partA = visual.bondPartA
                val partB = visual.bondPartB
                assertEquals("El Todo debe ser igual a la suma de las partes: $whole = $partA + $partB", whole, partA + partB)
            }
        }
    }

    @Test
    fun testMissingNumberStrategy_equationStructure() {
        OperationType.values().forEach { op ->
            repeat(10) {
                val exercise = generator.generateDidacticExercise(op, DidacticStrategy.MISSING_NUMBER, Difficulty.INTERMEDIATE)

                assertNotNull(exercise.missingTarget)
                assertTrue(
                    "MissingTarget debe ser FIRST_OPERAND o SECOND_OPERAND",
                    exercise.missingTarget == MissingTarget.FIRST_OPERAND || exercise.missingTarget == MissingTarget.SECOND_OPERAND
                )
                assertTrue(
                    "Ecuación debe contener incógnita ?: ${exercise.displayEquation}",
                    exercise.displayEquation?.contains("?") == true
                )
            }
        }
    }

    @Test
    fun testArrayGridStrategy_dimensionsAndProducts() {
        listOf(OperationType.MULTIPLICATION, OperationType.DIVISION).forEach { op ->
            repeat(10) {
                val exercise = generator.generateDidacticExercise(op, DidacticStrategy.ARRAY_GRID, Difficulty.EASY)
                val visual = exercise.visualRepresentation

                assertNotNull(visual)
                assertEquals(VisualType.ARRAY_GRID, visual!!.visualType)
                val rows = visual.arrayRows
                val cols = visual.arrayCols
                assertTrue("Filas deben ser >= 2", rows >= 2)
                assertTrue("Columnas deben ser >= 2", cols >= 2)
            }
        }
    }

    @Test
    fun testEqualSharingStrategy_fairDistribution() {
        repeat(15) {
            val exercise = generator.generateDidacticExercise(OperationType.DIVISION, DidacticStrategy.EQUAL_SHARING, Difficulty.EASY)
            val visual = exercise.visualRepresentation

            assertNotNull(visual)
            assertEquals(VisualType.SHARING_PARTITION, visual!!.visualType)
            val total = visual.totalToShare
            val groups = visual.containerCount
            val perGroup = visual.itemsPerContainer

            assertEquals("El total debe ser el reparto exacto de recipientes * elementos", total, groups * perGroup)
            assertEquals("La respuesta correcta debe ser los elementos por recipiente", perGroup, exercise.correctAnswer)
        }
    }

    @Test
    fun testSkipCountingStrategy_patternProgression() {
        repeat(15) {
            val exercise = generator.generateDidacticExercise(OperationType.MULTIPLICATION, DidacticStrategy.SKIP_COUNTING, Difficulty.EASY)
            val visual = exercise.visualRepresentation

            assertNotNull(visual)
            assertEquals(VisualType.SKIP_COUNTING, visual!!.visualType)
            val sequence = visual.sequenceItems
            val step = visual.sequenceStep

            assertTrue("Secuencia debe tener al menos 4 números", sequence.size >= 4)
            assertTrue("Paso debe ser positivo", step > 0)
            assertTrue(
                "La respuesta correcta debe estar en las opciones del ejercicio",
                exercise.options.contains(exercise.correctAnswer)
            )
        }
    }

    @Test
    fun testBalanceScaleStrategy_equilibriumBalance() {
        OperationType.values().forEach { op ->
            repeat(10) {
                val exercise = generator.generateDidacticExercise(op, DidacticStrategy.BALANCE_SCALE, Difficulty.EASY)
                val visual = exercise.visualRepresentation

                assertNotNull(visual)
                assertEquals(VisualType.BALANCE_SCALE, visual!!.visualType)
                assertTrue("scaleLeftText no debe estar vacío", visual.scaleLeftText.isNotBlank())
                assertTrue("scaleRightText no debe estar vacío", visual.scaleRightText.isNotBlank())
            }
        }
    }
}
