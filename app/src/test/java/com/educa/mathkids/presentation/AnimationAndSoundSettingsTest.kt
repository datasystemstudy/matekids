package com.educa.mathkids.presentation

import com.educa.mathkids.domain.model.Difficulty
import com.educa.mathkids.domain.model.OperationType
import com.educa.mathkids.domain.model.RewardsConfig
import com.educa.mathkids.presentation.components.AnswerButtonState
import com.educa.mathkids.presentation.components.MascotMood
import com.educa.mathkids.presentation.screens.exercise.ExerciseViewModel
import com.educa.mathkids.presentation.theme.AnimationConfig
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Pruebas unitarias para las configuraciones y lógica de la Fase 9:
 * Animaciones, Sonidos, Mascota, Respuestas y Recompensas.
 */
class AnimationAndSoundSettingsTest {

    @Test
    fun `AnimationConfig centralizes FAST, NORMAL and SLOW durations correctly`() {
        assertEquals(150, AnimationConfig.FAST)
        assertEquals(250, AnimationConfig.NORMAL)
        assertEquals(400, AnimationConfig.SLOW)
        assertEquals(1000, AnimationConfig.CELEBRATION_BURST)
        assertEquals(800, AnimationConfig.COUNT_UP)
        assertEquals(750, AnimationConfig.FLOATING_POINTS)
    }

    @Test
    fun `AnimationConfig getEffectiveDuration returns 0 when animations are disabled`() {
        assertEquals(0, AnimationConfig.getEffectiveDuration(AnimationConfig.NORMAL, false))
        assertEquals(0, AnimationConfig.getEffectiveDuration(AnimationConfig.SLOW, false))
        assertEquals(AnimationConfig.NORMAL, AnimationConfig.getEffectiveDuration(AnimationConfig.NORMAL, true))
        assertEquals(AnimationConfig.FAST, AnimationConfig.getEffectiveDuration(AnimationConfig.FAST, true))
    }

    @Test
    fun `MascotMood contains all 6 required emotional states`() {
        val moods = MascotMood.values().map { it.name }
        assertTrue(moods.contains("NORMAL"))
        assertTrue(moods.contains("HAPPY"))
        assertTrue(moods.contains("CELEBRATING"))
        assertTrue(moods.contains("THINKING"))
        assertTrue(moods.contains("SURPRISED"))
        assertTrue(moods.contains("ENCOURAGING"))
        assertEquals(6, MascotMood.values().size)
    }

    @Test
    fun `AnswerButtonState contains all required states`() {
        val states = AnswerButtonState.values().map { it.name }
        assertTrue(states.contains("DEFAULT"))
        assertTrue(states.contains("SELECTED_CORRECT"))
        assertTrue(states.contains("SELECTED_INCORRECT"))
        assertTrue(states.contains("HIGHLIGHT_CORRECT"))
        assertTrue(states.contains("DISABLED"))
    }

    @Test
    fun `ExerciseViewModel updates state correctly on correct answer`() {
        val viewModel = ExerciseViewModel(
            operationType = OperationType.ADDITION,
            difficulty = Difficulty.EASY,
            levelNumber = 1,
            totalQuestions = 5
        )

        val exercise = viewModel.uiState.value.currentExercise!!
        viewModel.onAnswerSelected(exercise.correctAnswer)

        val state = viewModel.uiState.value
        assertTrue(state.isAnswerSubmitted)
        assertEquals(true, state.isCorrect)
        assertTrue(state.pointsAwardedThisExercise > 0)
        assertEquals(1, state.currentStreak)
        assertTrue(state.feedbackMessage?.contains("¡") == true)
    }

    @Test
    fun `ExerciseViewModel updates state correctly on incorrect answer`() {
        val viewModel = ExerciseViewModel(
            operationType = OperationType.SUBTRACTION,
            difficulty = Difficulty.EASY,
            levelNumber = 1,
            totalQuestions = 5
        )

        val exercise = viewModel.uiState.value.currentExercise!!
        val wrongAnswer = exercise.correctAnswer + 99
        viewModel.onAnswerSelected(wrongAnswer)

        val state = viewModel.uiState.value
        assertTrue(state.isAnswerSubmitted)
        assertEquals(false, state.isCorrect)
        assertEquals(0, state.pointsAwardedThisExercise)
        assertEquals(0, state.currentStreak)
        assertTrue(state.feedbackMessage?.contains("💡") == true)
    }

    @Test
    fun `ExerciseViewModel triggers milestone on high streak`() {
        val viewModel = ExerciseViewModel(
            operationType = OperationType.MULTIPLICATION,
            difficulty = Difficulty.EASY,
            levelNumber = 1,
            totalQuestions = 10
        )

        // Responder 3 preguntas correctas consecutivas
        repeat(3) {
            val ex = viewModel.uiState.value.currentExercise!!
            viewModel.onAnswerSelected(ex.correctAnswer)
            if (it < 2) {
                viewModel.onNextExercise()
            }
        }

        val state = viewModel.uiState.value
        assertEquals(3, state.currentStreak)
        assertTrue(state.isSpecialStreakMilestone)
    }

    @Test
    fun `TtsManager processTextForPronunciation places accent on kids correctly`() {
        // Frase oficial de bienvenida
        val welcome = com.educa.mathkids.presentation.utils.TtsManager.processTextForPronunciation(
            "¡Bienvenido a MateKids! ¡Aprender matemáticas es una aventura!"
        )
        assertEquals("¡Bienvenido a Matekíds! ¡Aprender matemáticas es una aventura!", welcome)

        // Variaciones con espacios y mayúsculas/minúsculas
        val spaceVariation = com.educa.mathkids.presentation.utils.TtsManager.processTextForPronunciation(
            "Toca para jugar Mate Kids"
        )
        assertEquals("Toca para jugar Matekíds", spaceVariation)

        val lowercase = com.educa.mathkids.presentation.utils.TtsManager.processTextForPronunciation(
            "disfruta matekids con tus amigos"
        )
        assertEquals("disfruta Matekíds con tus amigos", lowercase)

        val uppercase = com.educa.mathkids.presentation.utils.TtsManager.processTextForPronunciation(
            "LOGO MATEKIDS OFICIAL"
        )
        assertEquals("LOGO Matekíds OFICIAL", uppercase)

        val question = com.educa.mathkids.presentation.utils.TtsManager.processTextForPronunciation(
            "¿Salir de MateKids?"
        )
        assertEquals("¿Salir de Matekíds?", question)
    }
}
