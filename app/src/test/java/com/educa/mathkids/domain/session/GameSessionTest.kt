package com.educa.mathkids.domain.session

import com.educa.mathkids.domain.model.Difficulty
import com.educa.mathkids.domain.model.OperationType
import com.educa.mathkids.domain.model.SessionConfig
import com.educa.mathkids.domain.usecase.CalculateAccuracyUseCase
import com.educa.mathkids.domain.usecase.NextExerciseUseCase
import com.educa.mathkids.domain.usecase.StartGameSessionUseCase
import com.educa.mathkids.domain.usecase.SubmitAnswerUseCase
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GameSessionTest {

    private lateinit var startGameSessionUseCase: StartGameSessionUseCase
    private lateinit var submitAnswerUseCase: SubmitAnswerUseCase
    private lateinit var nextExerciseUseCase: NextExerciseUseCase
    private lateinit var calculateAccuracyUseCase: CalculateAccuracyUseCase

    @Before
    fun setUp() {
        startGameSessionUseCase = StartGameSessionUseCase()
        submitAnswerUseCase = SubmitAnswerUseCase()
        nextExerciseUseCase = NextExerciseUseCase()
        calculateAccuracyUseCase = CalculateAccuracyUseCase()
    }

    // 1. INICIO DE SESIÓN
    @Test
    fun testStartSession_createsValidSessionWithDefaultCount() {
        val session = startGameSessionUseCase(
            operationType = OperationType.ADDITION,
            difficulty = Difficulty.EASY
        )

        assertNotNull(session.sessionId)
        assertEquals(SessionConfig.DEFAULT_EXERCISE_COUNT, session.totalExercises)
        assertEquals(10, session.totalExercises)
        assertEquals(0, session.currentExerciseIndex)
        assertEquals(1, session.currentExerciseNumber)
        assertNotNull(session.currentExercise)
        assertEquals(0, session.correctCount)
        assertEquals(0, session.incorrectCount)
        assertEquals(0, session.exercisesAnswered)
        assertFalse(session.isCompleted)
    }

    @Test
    fun testStartSession_configurableExerciseCount() {
        for (count in SessionConfig.AVAILABLE_SESSION_SIZES) {
            val session = startGameSessionUseCase(
                operationType = OperationType.MULTIPLICATION,
                difficulty = Difficulty.INTERMEDIATE,
                totalExercises = count
            )
            assertEquals(count, session.totalExercises)
        }
    }

    // 2. RESPUESTAS
    @Test
    fun testSubmitAnswer_correctAnswerRecordedCorrectly() {
        val session = startGameSessionUseCase(OperationType.ADDITION, Difficulty.EASY)
        val exercise = session.currentExercise!!
        val correctAnswer = exercise.correctAnswer

        val resultPair = submitAnswerUseCase(session, correctAnswer)
        assertNotNull(resultPair)

        val (updatedSession, answerResult) = resultPair!!
        assertTrue(answerResult.isCorrect)
        assertEquals(1, updatedSession.correctCount)
        assertEquals(0, updatedSession.incorrectCount)
        assertEquals(1, updatedSession.exercisesAnswered)
        assertEquals(1, updatedSession.results.size)
    }

    @Test
    fun testSubmitAnswer_incorrectAnswerRecordedCorrectly() {
        val session = startGameSessionUseCase(OperationType.ADDITION, Difficulty.EASY)
        val exercise = session.currentExercise!!
        // Encontrar una opción que no sea la respuesta correcta
        val incorrectAnswer = exercise.options.first { it != exercise.correctAnswer }

        val resultPair = submitAnswerUseCase(session, incorrectAnswer)
        assertNotNull(resultPair)

        val (updatedSession, answerResult) = resultPair!!
        assertFalse(answerResult.isCorrect)
        assertEquals(0, updatedSession.correctCount)
        assertEquals(1, updatedSession.incorrectCount)
        assertEquals(1, updatedSession.exercisesAnswered)
    }

    @Test
    fun testSubmitAnswer_preventsAnsweringTwiceForSameExercise() {
        val session = startGameSessionUseCase(OperationType.ADDITION, Difficulty.EASY)
        val exercise = session.currentExercise!!

        // Primera respuesta
        val firstResult = submitAnswerUseCase(session, exercise.correctAnswer)
        assertNotNull(firstResult)

        val (sessionAfterFirst, _) = firstResult!!

        // Intentar responder de nuevo en el mismo ejercicio debe ser rechazado
        val secondResult = submitAnswerUseCase(sessionAfterFirst, exercise.correctAnswer)
        assertNull(secondResult)
        assertEquals(1, sessionAfterFirst.results.size)
        assertEquals(1, sessionAfterFirst.correctCount)
    }

    // 3. PROGRESO
    @Test
    fun testNextExercise_advancesExerciseNumberAndGeneratesNewExercise() {
        var session = startGameSessionUseCase(OperationType.ADDITION, Difficulty.EASY, totalExercises = 5)
        val firstExercise = session.currentExercise!!

        val (sessionAfterAnswer, _) = submitAnswerUseCase(session, firstExercise.correctAnswer)!!
        session = nextExerciseUseCase(sessionAfterAnswer)

        assertEquals(1, session.currentExerciseIndex)
        assertEquals(2, session.currentExerciseNumber)
        assertNotNull(session.currentExercise)
        assertFalse(session.isCompleted)
    }

    // 4. FINALIZACIÓN DE LA SESIÓN
    @Test
    fun testCompleteSession_finishesAfterLastExerciseAndProducesAccurateResult() {
        val totalExercises = 5
        var session = startGameSessionUseCase(OperationType.SUBTRACTION, Difficulty.BASIC, totalExercises = totalExercises)

        // Simular responder 5 ejercicios (3 correctos, 2 incorrectos)
        for (i in 0 until totalExercises) {
            val currentEx = session.currentExercise!!
            val answer = if (i < 3) {
                currentEx.correctAnswer // 3 correctos
            } else {
                currentEx.options.first { it != currentEx.correctAnswer } // 2 incorrectos
            }

            val (sessionAnswered, _) = submitAnswerUseCase(session, answer)!!
            session = nextExerciseUseCase(sessionAnswered)
        }

        // Al terminar el ejercicio 5, la sesión debe quedar completada
        assertTrue(session.isCompleted)
        assertNull(session.currentExercise)
        assertEquals(3, session.correctCount)
        assertEquals(2, session.incorrectCount)

        val sessionResult = session.toSessionResult()
        assertEquals(totalExercises, sessionResult.totalExercises)
        assertEquals(3, sessionResult.correctCount)
        assertEquals(2, sessionResult.incorrectCount)
        assertEquals(60, sessionResult.accuracyPercentage)
    }

    // 5. CÁLCULO DE PORCENTAJE
    @Test
    fun testAccuracyCalculation() {
        // 10 ejercicios / 10 correctos -> 100%
        assertEquals(100, calculateAccuracyUseCase(correctCount = 10, totalExercises = 10))

        // 10 ejercicios / 8 correctos -> 80%
        assertEquals(80, calculateAccuracyUseCase(correctCount = 8, totalExercises = 10))

        // 10 ejercicios / 5 correctos -> 50%
        assertEquals(50, calculateAccuracyUseCase(correctCount = 5, totalExercises = 10))

        // 10 ejercicios / 0 correctos -> 0%
        assertEquals(0, calculateAccuracyUseCase(correctCount = 0, totalExercises = 10))

        // Evitar división por cero si totalExercises == 0 -> 0%
        assertEquals(0, calculateAccuracyUseCase(correctCount = 0, totalExercises = 0))
    }
}
