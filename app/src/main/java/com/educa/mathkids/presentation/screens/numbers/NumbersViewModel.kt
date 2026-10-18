package com.educa.mathkids.presentation.screens.numbers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.educa.mathkids.domain.engine.NumberChallengeGenerator
import com.educa.mathkids.domain.model.NumberCatalog
import com.educa.mathkids.domain.model.NumberChallenge
import com.educa.mathkids.domain.model.NumberGameType
import com.educa.mathkids.domain.model.NumberItem
import com.educa.mathkids.domain.repository.PlayerProgressRepository
import com.educa.mathkids.presentation.components.MascotMood
import com.educa.mathkids.presentation.components.MetaPhrases
import com.educa.mathkids.presentation.utils.HapticUtil
import com.educa.mathkids.presentation.utils.SoundManager
import com.educa.mathkids.presentation.utils.TtsManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class OrderFilterMode {
    ALL,
    ASCENDING,
    DESCENDING
}

data class NumbersUiState(
    val activeTab: NumberGameType = NumberGameType.EXPLORE,
    val numberCatalog: List<NumberItem> = NumberCatalog.NUMBERS_1_TO_20,
    val selectedNumber: NumberItem = NumberCatalog.NUMBERS_1_TO_20[0],
    val currentChallenge: NumberChallenge? = null,
    val selectedAnswer: Int? = null,
    val isAnswerSubmitted: Boolean = false,
    val isCorrect: Boolean? = null,
    val feedbackMessage: String = "",
    val metaMood: MascotMood = MascotMood.HAPPY,
    val metaDialog: String = "¡Toca cualquier número para descubrir sus objetos y escuchar su nombre! 🌟",
    val starsEarned: Int = 0,
    val streak: Int = 0,
    val orderFilter: OrderFilterMode = OrderFilterMode.ALL
)

class NumbersViewModel(
    private val playerProgressRepository: PlayerProgressRepository,
    private val soundManager: SoundManager,
    private val ttsManager: TtsManager,
    private val hapticUtil: HapticUtil
) : ViewModel() {

    private val _uiState = MutableStateFlow(NumbersUiState())
    val uiState: StateFlow<NumbersUiState> = _uiState.asStateFlow()

    init {
        // Inicializar con el número 1
        selectNumber(_uiState.value.selectedNumber, playAudio = false)
    }

    fun selectTab(tab: NumberGameType) {
        hapticUtil.vibrateClick()
        soundManager.playButtonClick()

        val isEn = ttsManager.currentLanguageCode.equals("en", ignoreCase = true)
        if (tab == NumberGameType.EXPLORE) {
            _uiState.update {
                it.copy(
                    activeTab = tab,
                    currentChallenge = null,
                    isAnswerSubmitted = false,
                    metaMood = MascotMood.HAPPY,
                    metaDialog = if (isEn) "Tap any number to discover its magic secrets! 🍎" else "¡Toca cualquier número para descubrir sus secretos mágicos! 🍎"
                )
            }
            ttsManager.speak(if (isEn) "Explore the numbers by tapping each one!" else "¡Explora los números tocando cada uno!")
        } else {
            startChallenge(tab, isEn)
        }
    }

    fun setOrderFilter(filter: OrderFilterMode) {
        hapticUtil.vibrateClick()
        soundManager.playButtonClick()
        _uiState.update { it.copy(orderFilter = filter) }
        val isEn = ttsManager.currentLanguageCode.equals("en", ignoreCase = true)
        startChallenge(NumberGameType.ORDER, isEn)
    }

    fun selectNumber(item: NumberItem, playAudio: Boolean = true) {
        if (playAudio) {
            hapticUtil.vibrateClick()
            soundManager.playButtonClick()
            ttsManager.speakNumber(item.number)
        }

        val isEn = ttsManager.currentLanguageCode.equals("en", ignoreCase = true)
        val dialog = if (isEn) {
            val englishWord = item.getWord(true)
            val funFact = item.getFunFact(true)
            "Number ${item.number}! Written as \"$englishWord\". $funFact ✨"
        } else {
            "¡El número ${item.number}! Se escribe \"${item.wordSpanish}\". ${item.funFact} ✨"
        }

        _uiState.update {
            it.copy(
                selectedNumber = item,
                metaMood = MascotMood.HAPPY,
                metaDialog = dialog
            )
        }
    }

    fun startChallenge(type: NumberGameType, isEn: Boolean = ttsManager.currentLanguageCode.equals("en", ignoreCase = true)) {
        val orderDescending = if (type == NumberGameType.ORDER) {
            when (_uiState.value.orderFilter) {
                OrderFilterMode.ASCENDING -> false
                OrderFilterMode.DESCENDING -> true
                OrderFilterMode.ALL -> null
            }
        } else null

        val challenge = NumberChallengeGenerator.generate(type, isEn, orderDescending)
        _uiState.update {
            it.copy(
                activeTab = type,
                currentChallenge = challenge,
                selectedAnswer = null,
                isAnswerSubmitted = false,
                isCorrect = null,
                feedbackMessage = "",
                metaMood = MascotMood.NORMAL,
                metaDialog = challenge.question
            )
        }
        ttsManager.speak(challenge.spokenQuestion)
    }

    fun onAnswerSelected(answer: Int) {
        val challenge = _uiState.value.currentChallenge ?: return
        if (_uiState.value.isAnswerSubmitted) return

        val isCorrect = answer == challenge.correctAnswer
        if (isCorrect) {
            hapticUtil.vibrateSuccess()
        } else {
            hapticUtil.vibrateEncourage()
        }

        val isEn = ttsManager.currentLanguageCode.equals("en", ignoreCase = true)
        if (isCorrect) {
            val newStreak = _uiState.value.streak + 1
            if (newStreak in listOf(3, 5, 10)) {
                soundManager.playApplause()
                hapticUtil.vibrateReward()
            } else {
                soundManager.playCorrect()
            }
            val praise = MetaPhrases.getPraise(isEn)
            ttsManager.speak("$praise ${challenge.explanation}")

            viewModelScope.launch {
                playerProgressRepository.addPoints(1)
            }

            _uiState.update {
                it.copy(
                    selectedAnswer = answer,
                    isAnswerSubmitted = true,
                    isCorrect = true,
                    feedbackMessage = "$praise ${challenge.explanation}",
                    metaMood = MascotMood.CELEBRATING,
                    metaDialog = praise,
                    starsEarned = it.starsEarned + 1,
                    streak = newStreak
                )
            }
        } else {
            soundManager.playIncorrect()
            val encouragement = MetaPhrases.getEncouragement(isEn)
            ttsManager.speak(encouragement)

            _uiState.update {
                it.copy(
                    selectedAnswer = answer,
                    isAnswerSubmitted = true,
                    isCorrect = false,
                    feedbackMessage = encouragement,
                    metaMood = MascotMood.ENCOURAGING,
                    metaDialog = encouragement,
                    streak = 0
                )
            }
        }
    }

    fun onNextChallenge() {
        hapticUtil.vibrateClick()
        soundManager.playButtonClick()
        val isEn = ttsManager.currentLanguageCode.equals("en", ignoreCase = true)
        startChallenge(_uiState.value.activeTab, isEn)
    }

    fun replayAudio() {
        val state = _uiState.value
        if (state.activeTab == NumberGameType.EXPLORE) {
            ttsManager.speakNumber(state.selectedNumber.number)
        } else {
            state.currentChallenge?.let {
                ttsManager.speak(it.spokenQuestion)
            }
        }
    }
}
