package com.educa.mathkids.presentation.screens.exercise

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.educa.mathkids.domain.model.DidacticStrategy
import com.educa.mathkids.domain.model.Difficulty
import com.educa.mathkids.domain.model.OperationType
import com.educa.mathkids.domain.model.Reward
import com.educa.mathkids.domain.model.RewardsConfig
import com.educa.mathkids.domain.model.ScoringConfig
import com.educa.mathkids.domain.model.SessionConfig
import com.educa.mathkids.domain.repository.PlayerProgressRepository
import com.educa.mathkids.domain.usecase.NextExerciseUseCase
import com.educa.mathkids.domain.usecase.StartGameSessionUseCase
import com.educa.mathkids.domain.usecase.SubmitAnswerUseCase
import com.educa.mathkids.presentation.utils.HapticUtil
import com.educa.mathkids.presentation.utils.SoundManager
import com.educa.mathkids.presentation.utils.TtsManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

class ExerciseViewModel(
    val operationType: OperationType,
    val difficulty: Difficulty = Difficulty.EASY,
    val levelNumber: Int = 1,
    val totalQuestions: Int = SessionConfig.DEFAULT_EXERCISE_COUNT,
    val didacticStrategy: DidacticStrategy? = null,
    private val startGameSessionUseCase: StartGameSessionUseCase = StartGameSessionUseCase(),
    private val submitAnswerUseCase: SubmitAnswerUseCase = SubmitAnswerUseCase(),
    private val nextExerciseUseCase: NextExerciseUseCase = NextExerciseUseCase(),
    private val playerProgressRepository: PlayerProgressRepository? = null,
    private val soundManager: SoundManager? = null,
    private val hapticUtil: HapticUtil? = null,
    private val ttsManager: TtsManager? = null,
    private val context: android.content.Context? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExerciseUiState(levelNumber = levelNumber))
    val uiState: StateFlow<ExerciseUiState> = _uiState.asStateFlow()

    private val positiveFeedbackMessages = listOf(
        "¡Excelente! 🌟",
        "¡Lo hiciste genial! 🎯",
        "¡Tu cerebro está creciendo! 🧠✨",
        "¡Increíble trabajo! ⭐",
        "¡Genial cálculo! ⚡"
    )

    private val positiveFeedbackMessagesEn = listOf(
        "Excellent! 🌟",
        "You did great! 🎯",
        "Your brain is growing! 🧠✨",
        "Awesome job! ⭐",
        "Great calculation! ⚡"
    )

    init {
        startSession()
    }

    private fun startSession() {
        val session = startGameSessionUseCase(
            operationType = operationType,
            level = levelNumber,
            totalExercises = totalQuestions,
            strategy = didacticStrategy
        )

        _uiState.update {
            it.copy(
                session = session,
                currentExercise = session.currentExercise,
                currentExerciseNumber = 1,
                totalExercises = session.totalExercises,
                progressFraction = 1f / session.totalExercises,
                selectedAnswer = null,
                isAnswerSubmitted = false,
                isCorrect = null,
                correctAnswer = session.currentExercise?.correctAnswer,
                feedbackMessage = null,
                streakBonusMessage = null,
                isNextButtonEnabled = false,
                isSessionFinished = false,
                sessionResult = null,
                sessionPoints = 0,
                currentStreak = 0,
                pointsAwardedThisExercise = 0,
                isSpecialStreakMilestone = false,
                newlyUnlockedReward = null,
                keypadInput = "",
                showPedagogicalExplanation = false,
                consecutiveErrors = 0,
                levelNumber = levelNumber,
                lives = 3,
                maxLives = 3,
                isOutOfLives = false
            )
        }
    }

    fun onAnswerSelected(selectedAnswer: Int) {
        val currentState = _uiState.value
        val session = currentState.session ?: return
        val currentExercise = currentState.currentExercise ?: return

        // Evitar doble respuesta o si ya no tiene vidas
        if (currentState.isAnswerSubmitted || currentState.isOutOfLives) return

        val resultPair = submitAnswerUseCase(session, selectedAnswer) ?: return
        val (updatedSession, answerResult) = resultPair

        val pointsDiff = updatedSession.sessionPoints - currentState.sessionPoints
        val isMilestone = answerResult.isCorrect && (updatedSession.currentStreak in listOf(3, 5, 10))

        val newConsecutiveErrors = if (answerResult.isCorrect) 0 else currentState.consecutiveErrors + 1
        val shouldAutoShowPedagogical = !answerResult.isCorrect && newConsecutiveErrors >= 2

        // Gestión de 3 Vidas: Al fallar la operación, se descuenta una vida
        val newLives = if (answerResult.isCorrect) currentState.lives else (currentState.lives - 1).coerceAtLeast(0)
        val isOutOfLives = !answerResult.isCorrect && newLives == 0

        val isEn = ttsManager?.currentLanguageCode?.equals("en", ignoreCase = true) == true
        if (answerResult.isCorrect) {
            context?.let { com.educa.mathkids.core.shop.CoinShopManager.addCoin(it, 1) }
            if (isMilestone) {
                soundManager?.playApplause()
                hapticUtil?.vibrateReward()
            } else {
                soundManager?.playCorrect()
                hapticUtil?.vibrateSuccess()
            }
            ttsManager?.speakExerciseResult(currentExercise)
        } else {
            soundManager?.playIncorrect()
            hapticUtil?.vibrateEncourage()
            if (!isOutOfLives) {
                ttsManager?.speak(if (isEn) "Almost there! 💡 Tap Mate's hint to solve it together!" else "Casi lo tienes. 💡 ¡Toca la pista de Mate para descubrirlo juntos!")
            }
        }

        var directReward: Reward? = null
        if (updatedSession.currentStreak == 5) {
            directReward = RewardsConfig.INITIAL_REWARDS.find { it.id == RewardsConfig.ID_FIRE_STREAK }
        }

        val feedbackMsg = if (answerResult.isCorrect) {
            val list = if (isEn) positiveFeedbackMessagesEn else positiveFeedbackMessages
            list[Random.nextInt(list.size)]
        } else if (isOutOfLives) {
            if (isEn) "You ran out of lives! 💔 Watch a video to continue." else "¡Te quedaste sin vidas! 💔 Mira un video para continuar."
        } else {
            if (isEn) "Almost there! 💡 Tap Mate's hint to solve it together!" else "Casi lo tienes. 💡 ¡Toca la pista de Mate para resolverlo juntos!"
        }

        _uiState.update {
            it.copy(
                session = updatedSession,
                selectedAnswer = selectedAnswer,
                isAnswerSubmitted = true,
                isCorrect = answerResult.isCorrect,
                correctAnswer = currentExercise.correctAnswer,
                feedbackMessage = feedbackMsg,
                streakBonusMessage = updatedSession.streakBonusMessage,
                sessionPoints = updatedSession.sessionPoints,
                currentStreak = updatedSession.currentStreak,
                pointsAwardedThisExercise = if (answerResult.isCorrect) pointsDiff else 0,
                isSpecialStreakMilestone = isMilestone,
                newlyUnlockedReward = directReward,
                isNextButtonEnabled = !isOutOfLives,
                consecutiveErrors = newConsecutiveErrors,
                showPedagogicalExplanation = shouldAutoShowPedagogical && !isOutOfLives,
                lives = newLives,
                isOutOfLives = isOutOfLives
            )
        }
    }

    /**
     * Restaura 3 vidas completas tras visualizar exitosamente un anuncio recompensado.
     */
    fun restoreLivesAfterAd() {
        soundManager?.playReward()
        hapticUtil?.vibrateReward()
        _uiState.update {
            it.copy(
                lives = 3,
                isOutOfLives = false,
                isAnswerSubmitted = false,
                selectedAnswer = null,
                isCorrect = null,
                isNextButtonEnabled = false,
                keypadInput = "",
                feedbackMessage = null,
                showPedagogicalExplanation = false,
                consecutiveErrors = 0
            )
        }
    }

    fun onDismissOutOfLives() {
        _uiState.update { it.copy(isOutOfLives = false) }
    }

    // Handlers para el teclado numérico en pantalla
    fun onKeypadDigit(digit: Char) {
        if (_uiState.value.isAnswerSubmitted) return
        val current = _uiState.value.keypadInput
        if (current.length < 5) {
            _uiState.update { it.copy(keypadInput = current + digit) }
        }
    }

    fun onKeypadBackspace() {
        if (_uiState.value.isAnswerSubmitted) return
        val current = _uiState.value.keypadInput
        if (current.isNotEmpty()) {
            _uiState.update { it.copy(keypadInput = current.dropLast(1)) }
        }
    }

    fun onKeypadSubmit() {
        val current = _uiState.value.keypadInput
        val answer = current.toIntOrNull()
        if (answer == null) {
            hapticUtil?.vibrateEncourage()
            soundManager?.playIncorrect()
            return
        }
        onAnswerSelected(answer)
    }

    fun onShowPedagogicalExplanation() {
        _uiState.update { it.copy(showPedagogicalExplanation = true) }
    }

    fun onDismissPedagogicalExplanation() {
        _uiState.update { it.copy(showPedagogicalExplanation = false) }
    }

    fun onRewardDismissed() {
        _uiState.update { it.copy(newlyUnlockedReward = null) }
    }

    fun onNextExercise() {
        val currentState = _uiState.value
        val session = currentState.session ?: return

        if (!currentState.isNextButtonEnabled) return

        val updatedSession = nextExerciseUseCase(session, didacticStrategy)

        if (updatedSession.isCompleted) {
            soundManager?.playVictory()

            viewModelScope.launch {
                val progressResult = playerProgressRepository?.saveSession(updatedSession)
                val totalPoints = progressResult?.updatedProgress?.totalPoints ?: updatedSession.sessionPoints
                val currentLevel = progressResult?.updatedProgress?.level ?: ScoringConfig.calculateLevel(totalPoints)
                val unlockedRewards = progressResult?.newlyUnlockedRewards ?: emptyList()

                val accuracy = updatedSession.accuracyPercentage
                val stars = when {
                    accuracy >= 90 -> 3
                    accuracy >= 70 -> 2
                    accuracy >= 50 -> 1
                    else -> 0
                }

                val nextUnlocked = playerProgressRepository?.recordLevelResult(
                    operationType = operationType,
                    levelNumber = levelNumber,
                    stars = stars,
                    score = updatedSession.sessionPoints,
                    exercisesCount = updatedSession.results.size
                ) ?: false

                val sessionResult = updatedSession.toSessionResult(
                    totalPoints = totalPoints,
                    currentLevel = currentLevel,
                    unlockedRewards = unlockedRewards,
                    nextLevelUnlocked = nextUnlocked
                )

                _uiState.update {
                    it.copy(
                        session = updatedSession,
                        isSessionFinished = true,
                        sessionResult = sessionResult
                    )
                }
            }
        } else {
            val nextExercise = updatedSession.currentExercise
            val nextNumber = updatedSession.currentExerciseNumber
            val progressFraction = nextNumber.toFloat() / updatedSession.totalExercises

            _uiState.update {
                it.copy(
                    session = updatedSession,
                    currentExercise = nextExercise,
                    currentExerciseNumber = nextNumber,
                    progressFraction = progressFraction,
                    selectedAnswer = null,
                    isAnswerSubmitted = false,
                    isCorrect = null,
                    correctAnswer = nextExercise?.correctAnswer,
                    feedbackMessage = null,
                    streakBonusMessage = null,
                    pointsAwardedThisExercise = 0,
                    isSpecialStreakMilestone = false,
                    isNextButtonEnabled = false,
                    keypadInput = "",
                    showPedagogicalExplanation = false
                )
            }
        }
    }
}
