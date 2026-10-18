package com.educa.mathkids.presentation

import com.educa.mathkids.domain.model.Difficulty
import com.educa.mathkids.domain.model.OperationType
import com.educa.mathkids.presentation.screens.exercise.ExerciseViewModel
import com.educa.mathkids.presentation.screens.result.ResultViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CompleteSessionFlowTest {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testCompleteSession_fromStartToResultScreen() = runTest {
        val totalExercises = 10

        val exerciseViewModel = ExerciseViewModel(
            operationType = OperationType.ADDITION,
            difficulty = Difficulty.EASY,
            levelNumber = 1,
            totalQuestions = totalExercises
        )

        testDispatcher.scheduler.advanceUntilIdle()

        // Simular responder los 10 ejercicios (ejercicios 1..8 correctos, 9 y 10 incorrectos)
        for (i in 1..totalExercises) {
            val stateBefore = exerciseViewModel.uiState.value
            assertEquals(i, stateBefore.currentExerciseNumber)
            assertFalse(stateBefore.isAnswerSubmitted)

            val currentEx = stateBefore.currentExercise!!
            val correctAns = currentEx.correctAnswer
            val answerToSubmit = if (i <= 8) correctAns else correctAns + 99 // 8 aciertos, 2 fallos

            exerciseViewModel.onAnswerSelected(answerToSubmit)

            val stateAfterAnswer = exerciseViewModel.uiState.value
            assertTrue(stateAfterAnswer.isAnswerSubmitted)
            assertEquals(i <= 8, stateAfterAnswer.isCorrect)

            // Anti-exploit: comprobar que pulsar de nuevo la respuesta no cambia los puntos
            val pointsBeforeExploit = stateAfterAnswer.sessionPoints
            exerciseViewModel.onAnswerSelected(answerToSubmit)
            assertEquals(pointsBeforeExploit, exerciseViewModel.uiState.value.sessionPoints)

            // Avanzar al siguiente ejercicio
            exerciseViewModel.onNextExercise()
            testDispatcher.scheduler.advanceUntilIdle()
        }

        // Verificar estado final de la sesión
        val finalState = exerciseViewModel.uiState.value
        assertTrue(finalState.isSessionFinished)
        assertNotNull(finalState.sessionResult)

        val result = finalState.sessionResult!!
        assertEquals(10, result.totalExercises)
        assertEquals(8, result.correctCount)
        assertEquals(2, result.incorrectCount)
        assertEquals(80, result.accuracyPercentage)

        // Verificar que ResultViewModel procesa adecuadamente el resultado de la sesión
        val resultViewModel = ResultViewModel(
            operationType = result.operationType,
            difficulty = result.difficulty,
            totalExercises = result.totalExercises,
            correctCount = result.correctCount,
            incorrectCount = result.incorrectCount,
            accuracyPercentage = result.accuracyPercentage,
            sessionPoints = result.sessionPoints,
            totalPoints = result.totalPoints,
            maxStreak = result.maxStreak,
            currentLevel = result.currentLevel,
            levelNumber = result.levelNumber,
            nextLevelUnlocked = result.nextLevelUnlocked
        )

        assertEquals(80, resultViewModel.uiState.accuracyPercentage)
        assertEquals(2, resultViewModel.uiState.stars) // 80% = 2 estrellas
        assertTrue(resultViewModel.uiState.celebrationMessage.contains("¡Muy bien hecho!"))
    }
}
