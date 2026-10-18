package com.educa.mathkids.presentation.screens.exercise

import com.educa.mathkids.domain.model.EvaluationStage
import com.educa.mathkids.domain.model.Exercise
import com.educa.mathkids.domain.model.GameSession
import com.educa.mathkids.domain.model.Reward
import com.educa.mathkids.domain.model.SessionConfig
import com.educa.mathkids.domain.model.SessionResult

/**
 * Estado inmutable de la pantalla interactiva de ejercicio (Fase 9).
 * Incluye datos para animaciones de puntos flotantes, hitos de racha y recompensas.
 */
data class ExerciseUiState(
    val session: GameSession? = null,
    val currentExercise: Exercise? = null,
    val currentExerciseNumber: Int = 1,
    val totalExercises: Int = SessionConfig.DEFAULT_EXERCISE_COUNT,
    val progressFraction: Float = 0.1f,
    val selectedAnswer: Int? = null,
    val isAnswerSubmitted: Boolean = false,
    val isCorrect: Boolean? = null,
    val correctAnswer: Int? = null,
    val feedbackMessage: String? = null,
    val streakBonusMessage: String? = null,
    val isNextButtonEnabled: Boolean = false,
    val isSessionFinished: Boolean = false,
    val sessionResult: SessionResult? = null,
    val sessionPoints: Int = 0,
    val currentStreak: Int = 0,
    val pointsAwardedThisExercise: Int = 0,
    val isSpecialStreakMilestone: Boolean = false,
    val newlyUnlockedReward: Reward? = null,
    val keypadInput: String = "",
    val showPedagogicalExplanation: Boolean = false,
    val consecutiveErrors: Int = 0,
    val levelNumber: Int = 1,
    val lives: Int = 3,
    val maxLives: Int = 3,
    val isOutOfLives: Boolean = false
) {
    val currentStage: EvaluationStage
        get() = currentExercise?.evaluationStage
            ?: EvaluationStage.fromIndex(currentExerciseNumber - 1, totalExercises)
}
