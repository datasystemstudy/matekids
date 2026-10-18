package com.educa.mathkids.presentation.screens.problemario

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.educa.mathkids.domain.model.problemario.MathProblem
import com.educa.mathkids.domain.model.problemario.ProblemCategory
import com.educa.mathkids.domain.model.problemario.ProblemDifficulty
import com.educa.mathkids.domain.model.problemario.ProblemSolveMode
import com.educa.mathkids.domain.repository.PlayerProgressRepository
import com.educa.mathkids.domain.repository.ProblemarioRepository
import com.educa.mathkids.presentation.components.MascotMood
import com.educa.mathkids.presentation.utils.HapticUtil
import com.educa.mathkids.presentation.utils.SoundManager
import com.educa.mathkids.presentation.utils.TtsManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProblemarioUiState(
    val category: ProblemCategory = ProblemCategory.SUMA,
    val difficulty: ProblemDifficulty = ProblemDifficulty.FACIL,
    val problems: List<MathProblem> = emptyList(),
    val currentIndex: Int = 0,
    val selectedAnswer: Int? = null,
    val isAnswerCorrect: Boolean? = null,
    val mascotMood: MascotMood = MascotMood.HAPPY,
    val mascotMessage: String = "¡Hola! Lee la historia o toca el parlante para escucharla.",
    val starsEarned: Int = 0,
    val isSessionFinished: Boolean = false,
    // Sistema de 3 vidas
    val lives: Int = 3,
    val maxLives: Int = 3,
    val isOutOfLives: Boolean = false,
    // Opciones barajadas para posición aleatoria de respuesta correcta
    val shuffledOptions: List<Int> = emptyList(),
    // Entrada de teclado para modo COMPLETACION_ESCRITA
    val keypadInput: String = ""
) {
    val currentProblem: MathProblem?
        get() = problems.getOrNull(currentIndex)

    val currentSolveMode: ProblemSolveMode
        get() = currentProblem?.solveMode ?: ProblemSolveMode.SELECCION

    val progressText: String
        get() = getProgressText(false)

    fun getProgressText(isEnglish: Boolean): String =
        if (problems.isNotEmpty()) {
            if (isEnglish) "Problem ${currentIndex + 1} of ${problems.size}"
            else "Problema ${currentIndex + 1} de ${problems.size}"
        } else ""
}

class ProblemarioViewModel(
    private val problemarioRepository: ProblemarioRepository,
    private val playerProgressRepository: PlayerProgressRepository,
    private val soundManager: SoundManager? = null,
    private val ttsManager: TtsManager? = null,
    private val hapticUtil: HapticUtil? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProblemarioUiState())
    val uiState: StateFlow<ProblemarioUiState> = _uiState.asStateFlow()

    var isEnglishSession: Boolean = false
        private set

    fun startSession(
        category: ProblemCategory,
        difficulty: ProblemDifficulty,
        isEnglish: Boolean = false
    ) {
        isEnglishSession = isEnglish
        val problems = problemarioRepository.getProblems(category, difficulty)
        val firstProblem = problems.firstOrNull()
        val firstOptions = firstProblem?.options?.shuffled() ?: emptyList()
        val initialMessage = when (firstProblem?.solveMode) {
            ProblemSolveMode.COMPLETACION ->
                if (isEnglish) "Complete the operation with the missing number!"
                else "¡Completa la operación con el número que falta!"
            ProblemSolveMode.ESCRITA ->
                if (isEnglish) "Type the answer on the keypad!"
                else "¡Escribe la respuesta en el teclado numérico!"
            else ->
                if (isEnglish) "Welcome to ${firstProblem?.category?.name ?: "Story Problems"}! Listen to or read the story."
                else "¡Bienvenido a ${category.title}! Escucha o lee la historia."
        }

        _uiState.update {
            it.copy(
                category = category,
                difficulty = difficulty,
                problems = problems,
                currentIndex = 0,
                selectedAnswer = null,
                isAnswerCorrect = null,
                mascotMood = MascotMood.HAPPY,
                mascotMessage = initialMessage,
                isSessionFinished = false,
                lives = 3,
                maxLives = 3,
                isOutOfLives = false,
                shuffledOptions = firstOptions,
                keypadInput = ""
            )
        }
        speakCurrentStory(isEnglish)
    }

    fun speakCurrentStory(isEnglish: Boolean = isEnglishSession) {
        val problem = _uiState.value.currentProblem ?: return
        val story = problem.getStory(isEnglish)
        val question = problem.getQuestion(isEnglish)
        val speech = "$story $question"
        try { ttsManager?.speak(speech) } catch (_: Throwable) {}
    }

    fun submitAnswer(answer: Int, isEnglish: Boolean = isEnglishSession) {
        val state = _uiState.value
        if (state.selectedAnswer != null) return // ya respondió, ignorar
        val problem = state.currentProblem ?: return

        hapticUtil?.vibrateClick()
        val isCorrect = answer == problem.correctAnswer

        if (isCorrect) {
            soundManager?.playCorrect()
            hapticUtil?.vibrateSuccess()
            viewModelScope.launch {
                playerProgressRepository.addPoints(1)
            }

            _uiState.update {
                it.copy(
                    selectedAnswer = answer,
                    keypadInput = answer.toString(),
                    isAnswerCorrect = true,
                    mascotMood = MascotMood.CELEBRATING,
                    mascotMessage = if (isEnglish) "AWESOME! 🎉 ${problem.equationText}" else "¡MUY BIEN! 🎉 ${problem.equationText}",
                    starsEarned = it.starsEarned + 1
                )
            }
            val explanation = problem.getPedagogicalExplanation(isEnglish)
            try {
                ttsManager?.speak(if (isEnglish) "Great job! Excellent! $explanation" else "¡Muy bien! ¡Excelente! $explanation")
            } catch (_: Throwable) {}
        } else {
            soundManager?.playIncorrect()
            hapticUtil?.vibrateEncourage()

            val newLives = (state.lives - 1).coerceAtLeast(0)
            val outOfLives = newLives == 0

            val incorrectMessage = if (outOfLives) {
                if (isEnglish) "Out of lives! 💔 The answer was ${problem.correctAnswer}."
                else "¡Sin vidas! 💔 La respuesta era ${problem.correctAnswer}."
            } else {
                if (isEnglish) "Almost! 🙂 You have $newLives ${if (newLives == 1) "chance" else "chances"} left."
                else "¡Casi! 🙂 Te queda${if (newLives == 1) " 1 oportunidad" else "n $newLives oportunidades"}."
            }

            _uiState.update {
                it.copy(
                    selectedAnswer = answer,
                    keypadInput = answer.toString(),
                    isAnswerCorrect = false,
                    lives = newLives,
                    isOutOfLives = outOfLives,
                    mascotMood = MascotMood.THINKING,
                    mascotMessage = incorrectMessage
                )
            }
            try {
                ttsManager?.speak(
                    if (outOfLives) {
                        if (isEnglish) "Oh no! You ran out of lives. Let's try again!"
                        else "¡Oh no! Se acabaron las vidas. Vamos a intentarlo de nuevo."
                    } else {
                        if (isEnglish) "Almost! Let's count together. Look closely at the pictures."
                        else "¡Casi! Vamos a contarlo juntos. Mira los dibujos con atención."
                    }
                )
            } catch (_: Throwable) {}
        }
    }

    fun onKeypadDigit(digit: Char) {
        val state = _uiState.value
        if (state.selectedAnswer != null || state.isOutOfLives) return
        if (state.keypadInput.length < 3) {
            soundManager?.playButtonClick()
            hapticUtil?.vibrateClick()
            _uiState.update { it.copy(keypadInput = it.keypadInput + digit) }
        }
    }

    fun onKeypadBackspace() {
        val state = _uiState.value
        if (state.selectedAnswer != null || state.isOutOfLives) return
        if (state.keypadInput.isNotEmpty()) {
            soundManager?.playButtonClick()
            hapticUtil?.vibrateClick()
            _uiState.update { it.copy(keypadInput = it.keypadInput.dropLast(1)) }
        }
    }

    fun onKeypadSubmit(isEnglish: Boolean = isEnglishSession) {
        val state = _uiState.value
        if (state.selectedAnswer != null || state.isOutOfLives) return
        val inputVal = state.keypadInput.toIntOrNull() ?: return
        submitAnswer(inputVal, isEnglish)
    }

    fun retryCurrentProblem(isEnglish: Boolean = isEnglishSession) {
        val problem = _uiState.value.currentProblem ?: return
        _uiState.update {
            it.copy(
                selectedAnswer = null,
                isAnswerCorrect = null,
                mascotMood = MascotMood.HAPPY,
                mascotMessage = if (isEnglish) "You can do it! Try to solve it." else "¡Tú puedes! Intenta resolverlo.",
                shuffledOptions = problem.options.shuffled(),
                keypadInput = ""
            )
        }
    }

    fun restoreLives(isEnglish: Boolean = isEnglishSession) {
        val problem = _uiState.value.currentProblem ?: return
        _uiState.update {
            it.copy(
                lives = 3,
                isOutOfLives = false,
                selectedAnswer = null,
                isAnswerCorrect = null,
                mascotMood = MascotMood.HAPPY,
                mascotMessage = if (isEnglish) "Lives restored! You can do it!" else "¡Vidas restauradas! ¡Tú puedes!",
                shuffledOptions = problem.options.shuffled(),
                keypadInput = ""
            )
        }
    }

    fun nextProblem(isEnglish: Boolean = isEnglishSession) {
        val state = _uiState.value
        if (state.currentIndex + 1 < state.problems.size) {
            val nextProblem = state.problems[state.currentIndex + 1]
            val promptMsg = when (nextProblem.solveMode) {
                ProblemSolveMode.COMPLETACION ->
                    if (isEnglish) "Complete the operation with the missing number!"
                    else "¡Completa la operación con el número que falta!"
                ProblemSolveMode.ESCRITA ->
                    if (isEnglish) "Type the answer on the keypad!"
                    else "¡Escribe la respuesta en el teclado numérico!"
                else ->
                    if (isEnglish) "Next challenge! Read the new story."
                    else "¡Siguiente desafío! Lee la nueva historia."
            }

            _uiState.update {
                it.copy(
                    currentIndex = it.currentIndex + 1,
                    selectedAnswer = null,
                    isAnswerCorrect = null,
                    mascotMood = MascotMood.HAPPY,
                    mascotMessage = promptMsg,
                    // Restaurar vidas al pasar al siguiente problema
                    lives = it.maxLives,
                    isOutOfLives = false,
                    shuffledOptions = nextProblem.options.shuffled(),
                    keypadInput = ""
                )
            }
            speakCurrentStory(isEnglish)
        } else {
            soundManager?.playGoalComplete()
            _uiState.update {
                it.copy(
                    isSessionFinished = true,
                    mascotMood = MascotMood.CELEBRATING,
                    mascotMessage = if (isEnglish)
                        "Congratulations! You completed all the stories in story problems. 🌟"
                    else
                        "¡Felicitaciones! Has completado todas las historias del problemario. 🌟",
                    keypadInput = ""
                )
            }
            try {
                ttsManager?.speak(
                    if (isEnglish) "Congratulations! You completed all the stories in story problems! You are a math champion!"
                    else "¡Felicitaciones! ¡Has completado todas las historias del problemario! ¡Eres un campeón matemático!"
                )
            } catch (_: Throwable) {}
        }
    }
}

