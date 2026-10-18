package com.educa.mathkids.presentation.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.educa.mathkids.domain.model.AppSettings
import com.educa.mathkids.domain.repository.PlayerProgressRepository
import com.educa.mathkids.domain.usecase.GetSettingsUseCase
import com.educa.mathkids.domain.usecase.UpdateSettingsUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    getSettingsUseCase: GetSettingsUseCase,
    private val updateSettingsUseCase: UpdateSettingsUseCase,
    private val playerProgressRepository: PlayerProgressRepository
) : ViewModel() {

    val settingsState: StateFlow<AppSettings> = getSettingsUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AppSettings()
        )

    fun onSoundsGeneralToggled(enabled: Boolean) {
        viewModelScope.launch { updateSettingsUseCase.setSoundsGeneral(enabled) }
    }

    fun onVoiceToggled(enabled: Boolean) {
        viewModelScope.launch { updateSettingsUseCase.setVoice(enabled) }
    }

    fun onSoundEffectsToggled(enabled: Boolean) {
        viewModelScope.launch { updateSettingsUseCase.setSoundEffects(enabled) }
    }

    fun onMusicToggled(enabled: Boolean) {
        viewModelScope.launch { updateSettingsUseCase.setMusic(enabled) }
    }

    fun onHapticsToggled(enabled: Boolean) {
        viewModelScope.launch { updateSettingsUseCase.setHaptics(enabled) }
    }

    fun onAnimationsToggled(enabled: Boolean) {
        viewModelScope.launch { updateSettingsUseCase.setAnimations(enabled) }
    }

    fun onLanguageChanged(language: String) {
        viewModelScope.launch { updateSettingsUseCase.setLanguage(language) }
    }

    fun onResetProgressConfirmed() {
        viewModelScope.launch {
            playerProgressRepository.resetAllProgress()
        }
    }
}
