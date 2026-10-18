package com.educa.mathkids.domain.usecase

import com.educa.mathkids.domain.repository.SettingsRepository

class UpdateSettingsUseCase(
    private val repository: SettingsRepository
) {
    suspend fun setSoundsGeneral(enabled: Boolean) {
        repository.updateSoundsGeneral(enabled)
    }

    suspend fun setVoice(enabled: Boolean) {
        repository.updateVoice(enabled)
    }

    suspend fun setSoundEffects(enabled: Boolean) {
        repository.updateSoundEffects(enabled)
    }

    suspend fun setMusic(enabled: Boolean) {
        repository.updateMusic(enabled)
    }

    suspend fun setHaptics(enabled: Boolean) {
        repository.updateHaptics(enabled)
    }

    suspend fun setAnimations(enabled: Boolean) {
        repository.updateAnimations(enabled)
    }

    suspend fun setLanguage(language: String) {
        repository.updateLanguage(language)
    }
}
