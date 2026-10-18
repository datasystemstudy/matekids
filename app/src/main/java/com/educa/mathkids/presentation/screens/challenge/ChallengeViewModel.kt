package com.educa.mathkids.presentation.screens.challenge

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.educa.mathkids.domain.engine.DefaultExerciseGenerator
import com.educa.mathkids.domain.model.Exercise
import com.educa.mathkids.domain.model.OperationType
import com.educa.mathkids.domain.model.problemario.MathProblem
import com.educa.mathkids.domain.model.problemario.ProblemCategory
import com.educa.mathkids.domain.model.problemario.ProblemDifficulty
import com.educa.mathkids.domain.repository.PlayerProgressRepository
import com.educa.mathkids.domain.repository.ProblemarioRepository
import com.educa.mathkids.presentation.utils.HapticUtil
import com.educa.mathkids.presentation.utils.SoundManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class ChallengeModalMode {
    SELECTION,       // Selección de 4 opciones
    COMPLETION,      // Completación de ecuación con incógnita y 4 opciones
    WRITTEN          // Completación escrita con teclado táctil en pantalla
}

data class ChallengeUiState(
    val currentQuestionIndex: Int = 0,
    val totalQuestions: Int = 35,
    val currentExercise: Exercise? = null,
    // Problema de Problemario intercalado cada 7 preguntas
    val currentProblemario: MathProblem? = null,
    val isProblemarioRound: Boolean = false,
    val problemarioOptions: List<Int> = emptyList(),
    val currentModalMode: ChallengeModalMode = ChallengeModalMode.SELECTION,
    val missingOperandIndex: Int = 1, // 0: operando1, 1: operando2
    val completionTargetAnswer: Int = 0,
    val completionOptions: List<Int> = emptyList(),
    val keypadInput: String = "",
    val timeLeftSeconds: Int = 15,
    val totalTimePerQuestion: Int = 15,
    val score: Int = 0,
    val currentStreak: Int = 0,
    val maxStreak: Int = 0,
    val correctCount: Int = 0,
    val multiplier: Float = 1.0f,
    val lives: Int = 3,
    val maxLives: Int = 3,
    val isOutOfLives: Boolean = false,
    val isAnswerSubmitted: Boolean = false,
    val selectedAnswer: Int? = null,
    val isCorrect: Boolean? = null,
    val isChallengeFinished: Boolean = false
) {
    val levelNumber: Int get() = currentQuestionIndex + 1
    val progressFraction: Float get() = (currentQuestionIndex + 1).toFloat() / totalQuestions
    val timeFraction: Float get() = timeLeftSeconds.toFloat() / totalTimePerQuestion
    val stars: Int
        get() = when {
            correctCount >= 30 -> 3
            correctCount >= 20 -> 2
            correctCount >= 10 -> 1
            else -> 0
        }
    val isMasteryAchieved: Boolean get() = correctCount >= 18
}

class ChallengeViewModel(
    private val playerProgressRepository: PlayerProgressRepository? = null,
    private val soundManager: SoundManager? = null,
    private val hapticUtil: HapticUtil? = null,
    private val problemarioRepository: ProblemarioRepository? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChallengeUiState())
    val uiState: StateFlow<ChallengeUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null
    private val generator = DefaultExerciseGenerator.shared

    init {
        startChallenge()
    }

    private fun startChallenge() {
        _uiState.value = ChallengeUiState(
            totalQuestions = 35,
            totalTimePerQuestion = 15,
            timeLeftSeconds = 15,
            lives = 3,
            maxLives = 3,
            isOutOfLives = false
        )
        loadNextQuestion(index = 0)
    }

    private fun loadNextQuestion(index: Int) {
        if (index >= 35) {
            finishChallenge()
            return
        }

        // Cada 7 preguntas (índices 6, 13, 20, 27, 34) se inserta un Problemario
        val isProblemarioRound = problemarioRepository != null && index % 7 == 6

        if (isProblemarioRound) {
            loadProblemarioRound(index)
            return
        }

        val levelNumber = index + 1
        // Alternar las 4 operaciones ordenadamente: Suma, Resta, Multiplicación, División
        val op = OperationType.entries[index % OperationType.entries.size]

        // Dificultad progresiva a través de los 35 niveles
        val exercise = generator.generateByLevel(op, levelNumber)

        // Modalidad alternada en 3 variantes
        val modalMode = when (index % 3) {
            0 -> ChallengeModalMode.SELECTION
            1 -> ChallengeModalMode.COMPLETION
            else -> ChallengeModalMode.WRITTEN
        }

        // Para modalidad COMPLETION: elegir operando faltante (1 o 2) y opciones
        val missingIndex = if (index % 2 == 0) 1 else 0
        val targetAnswer = if (modalMode == ChallengeModalMode.COMPLETION) {
            if (missingIndex == 0) exercise.operand1 else exercise.operand2
        } else {
            exercise.correctAnswer
        }

        val completionOpts = if (modalMode == ChallengeModalMode.COMPLETION) {
            val distractorSet = mutableSetOf(targetAnswer)
            var offset = 1
            while (distractorSet.size < 4 && offset < 10) {
                if (targetAnswer - offset > 0) distractorSet.add(targetAnswer - offset)
                distractorSet.add(targetAnswer + offset)
                offset++
            }
            distractorSet.shuffled()
        } else {
            exercise.options
        }

        _uiState.update {
            it.copy(
                currentQuestionIndex = index,
                currentExercise = exercise,
                isProblemarioRound = false,
                currentProblemario = null,
                problemarioOptions = emptyList(),
                currentModalMode = modalMode,
                missingOperandIndex = missingIndex,
                completionTargetAnswer = targetAnswer,
                completionOptions = completionOpts,
                keypadInput = "",
                timeLeftSeconds = 20,
                totalTimePerQuestion = 20,
                isAnswerSubmitted = false,
                selectedAnswer = null,
                isCorrect = null
            )
        }

        startTimer()
    }

    private fun loadProblemarioRound(index: Int) {
        val repo = problemarioRepository ?: return
        // Rotar categorías según el índice
        val categories = ProblemCategory.entries
        val cat = categories[(index / 7) % categories.size]
        val diff = when {
            index < 14 -> ProblemDifficulty.FACIL
            index < 28 -> ProblemDifficulty.MEDIO
            else -> ProblemDifficulty.AVANZADO
        }
        val problem = try {
            repo.getProblems(cat, diff).randomOrNull() ?: repo.generateProblem(cat, diff)
        } catch (_: Throwable) {
            loadNextQuestion(index + 1)
            return
        }
        val shuffledOpts = problem.options.shuffled()

        _uiState.update {
            it.copy(
                currentQuestionIndex = index,
                currentExercise = null,
                isProblemarioRound = true,
                currentProblemario = problem,
                problemarioOptions = shuffledOpts,
                keypadInput = "",
                timeLeftSeconds = 30,
                totalTimePerQuestion = 30,
                isAnswerSubmitted = false,
                selectedAnswer = null,
                isCorrect = null
            )
        }
        startTimer()
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_uiState.value.timeLeftSeconds > 0 && !_uiState.value.isAnswerSubmitted) {
                delay(1000)
                _uiState.update { it.copy(timeLeftSeconds = (it.timeLeftSeconds - 1).coerceAtLeast(0)) }
            }
            if (_uiState.value.timeLeftSeconds == 0 && !_uiState.value.isAnswerSubmitted) {
                onTimeExpired()
            }
        }
    }

    private fun onTimeExpired() {
        soundManager?.playIncorrect()
        hapticUtil?.vibrateEncourage()

        val newLives = (_uiState.value.lives - 1).coerceAtLeast(0)
        val outOfLives = newLives == 0

        _uiState.update {
            it.copy(
                lives = newLives,
                isOutOfLives = outOfLives,
                isAnswerSubmitted = true,
                isCorrect = false,
                currentStreak = 0,
                multiplier = 1.0f
            )
        }

        if (!outOfLives) {
            scheduleNextQuestion()
        }
    }

    fun onKeypadDigit(char: Char) {
        if (_uiState.value.isAnswerSubmitted) return
        val current = _uiState.value.keypadInput
        if (current.length < 4) {
            _uiState.update { it.copy(keypadInput = current + char) }
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

    fun onAnswerSelected(answer: Int) {
        if (_uiState.value.isAnswerSubmitted) return
        timerJob?.cancel()

        val state = _uiState.value

        // Determinar la respuesta esperada según el tipo de ronda
        val expectedAnswer = when {
            state.isProblemarioRound -> state.currentProblemario?.correctAnswer ?: return
            state.currentModalMode == ChallengeModalMode.COMPLETION -> state.completionTargetAnswer
            else -> state.currentExercise?.correctAnswer ?: return
        }

        val isCorrect = answer == expectedAnswer
        val newLives = if (isCorrect) state.lives else (state.lives - 1).coerceAtLeast(0)
        val outOfLives = !isCorrect && newLives == 0

        val newStreak = if (isCorrect) state.currentStreak + 1 else 0
        val isStreakMilestone = isCorrect && (newStreak == 3 || newStreak == 5 || newStreak == 10 || (newStreak > 10 && newStreak % 5 == 0))

        if (isCorrect) {
            if (isStreakMilestone) {
                soundManager?.playApplause()
                hapticUtil?.vibrateReward()
            } else {
                soundManager?.playCorrect()
                hapticUtil?.vibrateSuccess()
            }
        } else {
            soundManager?.playIncorrect()
            hapticUtil?.vibrateEncourage()
        }

        val newMaxStreak = maxOf(state.maxStreak, newStreak)
        val newMultiplier = when {
            newStreak >= 5 -> 2.0f
            newStreak >= 3 -> 1.5f
            else -> 1.0f
        }
        val pointsToAdd = if (isCorrect) (100 * newMultiplier).toInt() else 0
        val newScore = state.score + pointsToAdd
        val newCorrectCount = if (isCorrect) state.correctCount + 1 else state.correctCount


        _uiState.update {
            it.copy(
                selectedAnswer = answer,
                isAnswerSubmitted = true,
                isCorrect = isCorrect,
                lives = newLives,
                isOutOfLives = outOfLives,
                score = newScore,
                currentStreak = newStreak,
                maxStreak = newMaxStreak,
                multiplier = newMultiplier,
                correctCount = newCorrectCount
            )
        }

        if (!outOfLives) {
            scheduleNextQuestion()
        }
    }

    fun restoreLivesAfterAd() {
        _uiState.update {
            it.copy(
                lives = 3,
                isOutOfLives = false,
                isAnswerSubmitted = false,
                timeLeftSeconds = 15
            )
        }
        startTimer()
    }

    private fun scheduleNextQuestion() {
        viewModelScope.launch {
            delay(1200)
            loadNextQuestion(_uiState.value.currentQuestionIndex + 1)
        }
    }

    private fun finishChallenge() {
        timerJob?.cancel()
        soundManager?.playVictory()
        val earnedScore = _uiState.value.score
        if (earnedScore > 0) {
            viewModelScope.launch {
                playerProgressRepository?.addPoints(earnedScore)
            }
        }
        _uiState.update { it.copy(isChallengeFinished = true) }
    }

    fun retryChallenge() {
        startChallenge()
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
