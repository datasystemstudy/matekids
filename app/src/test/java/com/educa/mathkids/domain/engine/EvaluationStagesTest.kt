package com.educa.mathkids.domain.engine

import com.educa.mathkids.domain.model.EvaluationStage
import com.educa.mathkids.domain.model.OperationLevelConfig
import com.educa.mathkids.domain.model.OperationType
import com.educa.mathkids.domain.usecase.NextExerciseUseCase
import com.educa.mathkids.domain.usecase.StartGameSessionUseCase
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Pruebas unitarias para la división de las evaluaciones en 3 etapas pedagógicas:
 * 1. Selección Simple
 * 2. Completación con Selección
 * 3. Completación donde el estudiante escribe la cantidad correcta (con teclado)
 * Verificadas a lo largo de los niveles (1 al 50) y las 4 operaciones.
 */
class EvaluationStagesTest {

    private lateinit var generator: DefaultExerciseGenerator
    private lateinit var startGameSessionUseCase: StartGameSessionUseCase
    private lateinit var nextExerciseUseCase: NextExerciseUseCase

    @Before
    fun setUp() {
        generator = DefaultExerciseGenerator()
        startGameSessionUseCase = StartGameSessionUseCase()
        nextExerciseUseCase = NextExerciseUseCase()
    }

    @Test
    fun testStagePartitioningForSessionLengths() {
        // Para sesión estándar de 10 ejercicios:
        // Etapa 1: Selección simple (índices 0, 1, 2)
        assertEquals(EvaluationStage.SIMPLE_SELECTION, EvaluationStage.fromIndex(0, 10))
        assertEquals(EvaluationStage.SIMPLE_SELECTION, EvaluationStage.fromIndex(1, 10))
        assertEquals(EvaluationStage.SIMPLE_SELECTION, EvaluationStage.fromIndex(2, 10))

        // Etapa 2: Completación con selección (índices 3, 4, 5)
        assertEquals(EvaluationStage.COMPLETION_SELECTION, EvaluationStage.fromIndex(3, 10))
        assertEquals(EvaluationStage.COMPLETION_SELECTION, EvaluationStage.fromIndex(4, 10))
        assertEquals(EvaluationStage.COMPLETION_SELECTION, EvaluationStage.fromIndex(5, 10))

        // Etapa 3: Completación escrita (índices 6, 7, 8, 9)
        assertEquals(EvaluationStage.COMPLETION_WRITTEN, EvaluationStage.fromIndex(6, 10))
        assertEquals(EvaluationStage.COMPLETION_WRITTEN, EvaluationStage.fromIndex(7, 10))
        assertEquals(EvaluationStage.COMPLETION_WRITTEN, EvaluationStage.fromIndex(8, 10))
        assertEquals(EvaluationStage.COMPLETION_WRITTEN, EvaluationStage.fromIndex(9, 10))

        // Para sesión corta de 3 ejercicios:
        assertEquals(EvaluationStage.SIMPLE_SELECTION, EvaluationStage.fromIndex(0, 3))
        assertEquals(EvaluationStage.COMPLETION_SELECTION, EvaluationStage.fromIndex(1, 3))
        assertEquals(EvaluationStage.COMPLETION_WRITTEN, EvaluationStage.fromIndex(2, 3))
    }

    @Test
    fun testStage1SimpleSelectionForAllOperationsAndLevels() {
        val sampleLevels = listOf(1, 5, 10, 20, 35, 50)
        for (op in OperationType.values()) {
            for (level in sampleLevels) {
                val exercise = generator.generateByStage(op, level, EvaluationStage.SIMPLE_SELECTION)
                assertEquals(EvaluationStage.SIMPLE_SELECTION, exercise.evaluationStage)
                assertFalse("En selección simple el teclado debe estar deshabilitado", exercise.isKeypadAllowed)
                assertEquals("Debe tener 4 opciones", 4, exercise.options.size)
                assertTrue("Las opciones deben contener la respuesta correcta", exercise.options.contains(exercise.correctAnswer))
                assertTrue("El ejercicio debe ser matemáticamente válido", generator.isValidExercise(exercise))
            }
        }
    }

    @Test
    fun testStage2CompletionSelectionForAllOperationsAndLevels() {
        val sampleLevels = listOf(1, 5, 10, 20, 35, 50)
        for (op in OperationType.values()) {
            for (level in sampleLevels) {
                val exercise = generator.generateByStage(op, level, EvaluationStage.COMPLETION_SELECTION)
                assertEquals(EvaluationStage.COMPLETION_SELECTION, exercise.evaluationStage)
                assertFalse("En completación por selección el teclado debe estar deshabilitado", exercise.isKeypadAllowed)
                assertEquals("Debe tener 4 opciones", 4, exercise.options.size)
                assertTrue("Las opciones deben contener la respuesta correcta", exercise.options.contains(exercise.correctAnswer))
                assertNotNull("Debe especificar missingTarget", exercise.missingTarget)
                assertTrue("La ecuación debe contener '?'", exercise.displayEquation?.contains("?") == true)
                assertTrue("El ejercicio debe ser matemáticamente válido", generator.isValidExercise(exercise))
            }
        }
    }

    @Test
    fun testStage3CompletionWrittenForAllOperationsAndLevels() {
        val sampleLevels = listOf(1, 5, 10, 20, 35, 50)
        for (op in OperationType.values()) {
            for (level in sampleLevels) {
                val exercise = generator.generateByStage(op, level, EvaluationStage.COMPLETION_WRITTEN)
                assertEquals(EvaluationStage.COMPLETION_WRITTEN, exercise.evaluationStage)
                assertTrue("En completación escrita el teclado debe estar permitido", exercise.isKeypadAllowed)
                assertNotNull("Debe especificar missingTarget", exercise.missingTarget)
                assertTrue("La ecuación debe contener '?'", exercise.displayEquation?.contains("?") == true)
                assertTrue("El ejercicio debe ser matemáticamente válido", generator.isValidExercise(exercise))
            }
        }
    }

    @Test
    fun testFullGameSessionTraversesAllThreeStages() {
        for (op in OperationType.values()) {
            val level = 15
            var session = startGameSessionUseCase(operationType = op, level = level, totalExercises = 10)

            // Primer ejercicio debe ser etapa 1
            assertEquals(0, session.currentExerciseIndex)
            assertEquals(EvaluationStage.SIMPLE_SELECTION, session.currentExercise?.evaluationStage)
            assertFalse(session.currentExercise!!.isKeypadAllowed)

            val stageHistory = mutableListOf<EvaluationStage>()
            session.currentExercise?.evaluationStage?.let { stageHistory.add(it) }

            // Recorrer los 9 ejercicios restantes
            while (!session.isCompleted) {
                session = nextExerciseUseCase(session)
                session.currentExercise?.let { ex ->
                    ex.evaluationStage?.let { stageHistory.add(it) }
                }
            }

            assertEquals("Debe haber 10 etapas registradas", 10, stageHistory.size)
            assertTrue("Debe contener etapa SIMPLE_SELECTION", stageHistory.contains(EvaluationStage.SIMPLE_SELECTION))
            assertTrue("Debe contener etapa COMPLETION_SELECTION", stageHistory.contains(EvaluationStage.COMPLETION_SELECTION))
            assertTrue("Debe contener etapa COMPLETION_WRITTEN", stageHistory.contains(EvaluationStage.COMPLETION_WRITTEN))

            // Verificar la transición ordenada
            assertEquals(EvaluationStage.SIMPLE_SELECTION, stageHistory[0])
            assertEquals(EvaluationStage.SIMPLE_SELECTION, stageHistory[1])
            assertEquals(EvaluationStage.SIMPLE_SELECTION, stageHistory[2])
            assertEquals(EvaluationStage.COMPLETION_SELECTION, stageHistory[3])
            assertEquals(EvaluationStage.COMPLETION_SELECTION, stageHistory[4])
            assertEquals(EvaluationStage.COMPLETION_SELECTION, stageHistory[5])
            assertEquals(EvaluationStage.COMPLETION_WRITTEN, stageHistory[6])
            assertEquals(EvaluationStage.COMPLETION_WRITTEN, stageHistory[7])
            assertEquals(EvaluationStage.COMPLETION_WRITTEN, stageHistory[8])
            assertEquals(EvaluationStage.COMPLETION_WRITTEN, stageHistory[9])
        }
    }

    @Test
    fun testAll50LevelsHaveValidStage3WrittenEquations() {
        for (level in 1..OperationLevelConfig.MAX_LEVELS_PER_OPERATION) {
            val exercise = generator.generateByStage(OperationType.ADDITION, level, EvaluationStage.COMPLETION_WRITTEN)
            assertTrue(generator.isValidExercise(exercise))
            assertTrue(exercise.isKeypadAllowed)
            assertTrue(exercise.correctAnswer >= 0)
        }
    }
}
