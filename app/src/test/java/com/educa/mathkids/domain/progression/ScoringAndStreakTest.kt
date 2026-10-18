package com.educa.mathkids.domain.progression

import com.educa.mathkids.domain.model.Difficulty
import com.educa.mathkids.domain.model.OperationType
import com.educa.mathkids.domain.model.ScoringConfig
import com.educa.mathkids.domain.usecase.CalculateScoreUseCase
import com.educa.mathkids.domain.usecase.StartGameSessionUseCase
import com.educa.mathkids.domain.usecase.SubmitAnswerUseCase
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ScoringAndStreakTest {

    private lateinit var calculateScoreUseCase: CalculateScoreUseCase
    private lateinit var startGameSessionUseCase: StartGameSessionUseCase
    private lateinit var submitAnswerUseCase: SubmitAnswerUseCase

    @Before
    fun setUp() {
        calculateScoreUseCase = CalculateScoreUseCase()
        startGameSessionUseCase = StartGameSessionUseCase()
        submitAnswerUseCase = SubmitAnswerUseCase(calculateScoreUseCase)
    }

    // 1. PUNTOS
    @Test
    fun testScoring_correctAnswerGrantsTenPoints() {
        val result = calculateScoreUseCase(isCorrect = true, currentStreak = 1)
        assertEquals(10, result.basePoints)
        assertEquals(0, result.streakBonus)
        assertEquals(10, result.totalPoints)
    }

    @Test
    fun testScoring_incorrectAnswerGrantsZeroPoints() {
        val result = calculateScoreUseCase(isCorrect = false, currentStreak = 0)
        assertEquals(0, result.basePoints)
        assertEquals(0, result.streakBonus)
        assertEquals(0, result.totalPoints)
        assertNull(result.streakBonusMessage)
    }

    // 2. RACHA
    @Test
    fun testStreak_incrementsOnCorrectAndResetsOnIncorrect() {
        val session = startGameSessionUseCase(OperationType.ADDITION, Difficulty.EASY)
        val ex1 = session.currentExercise!!

        // Primera respuesta correcta -> racha 1
        val (session1, res1) = submitAnswerUseCase(session, ex1.correctAnswer)!!
        assertTrue(res1.isCorrect)
        assertEquals(1, session1.currentStreak)
        assertEquals(1, session1.bestStreak)
        assertEquals(10, session1.sessionPoints)

        // Simular siguiente ejercicio y segunda respuesta correcta -> racha 2
        val sessionWithNextEx = session1.copy(
            currentExerciseIndex = 1,
            currentExercise = ex1 // usamos el mismo ejercicio para el test
        )
        val (session2, res2) = submitAnswerUseCase(sessionWithNextEx, ex1.correctAnswer)!!
        assertTrue(res2.isCorrect)
        assertEquals(2, session2.currentStreak)
        assertEquals(2, session2.bestStreak)
        assertEquals(20, session2.sessionPoints)

        // Simular tercer ejercicio y respuesta incorrecta -> racha vuelve a 0
        val sessionWithThirdEx = session2.copy(
            currentExerciseIndex = 2,
            currentExercise = ex1
        )
        val incorrectAnswer = ex1.options.first { it != ex1.correctAnswer }
        val (session3, res3) = submitAnswerUseCase(sessionWithThirdEx, incorrectAnswer)!!
        assertEquals(0, session3.currentStreak)
        // La mejor racha permanece intacta en 2
        assertEquals(2, session3.bestStreak)
        // Los puntos no disminuyen (no hay penalización)
        assertEquals(20, session3.sessionPoints)
    }

    // 3. MEJOR RACHA
    @Test
    fun testBestStreak_persistsEvenWhenCurrentStreakResets() {
        var session = startGameSessionUseCase(OperationType.ADDITION, Difficulty.EASY)
        val ex = session.currentExercise!!

        // Alcanzar racha de 5
        session = session.copy(currentStreak = 4, bestStreak = 4)
        val (sessionAt5, _) = submitAnswerUseCase(session, ex.correctAnswer)!!
        assertEquals(5, sessionAt5.currentStreak)
        assertEquals(5, sessionAt5.bestStreak)

        // Fallar la siguiente pregunta
        val sessionAfter5 = sessionAt5.copy(currentExerciseIndex = sessionAt5.currentExerciseIndex + 1)
        val incorrect = ex.options.first { it != ex.correctAnswer }
        val (sessionFailed, _) = submitAnswerUseCase(sessionAfter5, incorrect)!!

        assertEquals(0, sessionFailed.currentStreak)
        assertEquals(5, sessionFailed.bestStreak)
    }

    // 4. BONIFICACIÓN POR RACHA
    @Test
    fun testStreakBonus_grantsConfiguredBonusAndMessage() {
        // Racha 3: +5 bonificación
        val res3 = calculateScoreUseCase(isCorrect = true, currentStreak = 3)
        assertEquals(ScoringConfig.STREAK_TIER_1_BONUS, res3.streakBonus)
        assertEquals(15, res3.totalPoints)
        assertNotNull(res3.streakBonusMessage)
        assertTrue(res3.streakBonusMessage!!.contains("3"))

        // Racha 5: +10 bonificación
        val res5 = calculateScoreUseCase(isCorrect = true, currentStreak = 5)
        assertEquals(ScoringConfig.STREAK_TIER_2_BONUS, res5.streakBonus)
        assertEquals(20, res5.totalPoints)
        assertTrue(res5.streakBonusMessage!!.contains("5"))

        // Racha 10: +20 bonificación
        val res10 = calculateScoreUseCase(isCorrect = true, currentStreak = 10)
        assertEquals(ScoringConfig.STREAK_TIER_3_BONUS, res10.streakBonus)
        assertEquals(30, res10.totalPoints)
        assertTrue(res10.streakBonusMessage!!.contains("10"))
    }

    // 5. ANTI-EXPLOIT (Prevención de doble puntuación)
    @Test
    fun testAntiExploit_doubleAnswerBlocked() {
        val session = startGameSessionUseCase(OperationType.ADDITION, Difficulty.EASY)
        val ex = session.currentExercise!!

        val firstSubmit = submitAnswerUseCase(session, ex.correctAnswer)
        assertNotNull(firstSubmit)

        val (sessionAfterFirst, _) = firstSubmit!!

        // Segundo intento en el mismo ejercicio debe ser rechazado
        val secondSubmit = submitAnswerUseCase(sessionAfterFirst, ex.correctAnswer)
        assertNull(secondSubmit)
        assertEquals(10, sessionAfterFirst.sessionPoints)
        assertEquals(1, sessionAfterFirst.results.size)
    }
}
