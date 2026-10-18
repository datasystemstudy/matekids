package com.educa.mathkids.data.repository

import com.educa.mathkids.data.local.datastore.SettingsDataStore
import com.educa.mathkids.domain.model.AppSettings
import com.educa.mathkids.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow

class SettingsRepositoryImpl(
    private val dataStore: SettingsDataStore
) : SettingsRepository {

    override fun getSettings(): Flow<AppSettings> = dataStore.settingsFlow

    override suspend fun updateSoundsGeneral(enabled: Boolean) {
        dataStore.setSoundsGeneral(enabled)
    }

    override suspend fun updateVoice(enabled: Boolean) {
        dataStore.setVoice(enabled)
    }

    override suspend fun updateSoundEffects(enabled: Boolean) {
        dataStore.setSoundEffects(enabled)
    }

    override suspend fun updateMusic(enabled: Boolean) {
        dataStore.setMusic(enabled)
    }

    override suspend fun updateHaptics(enabled: Boolean) {
        dataStore.setHaptics(enabled)
    }

    override suspend fun updateAnimations(enabled: Boolean) {
        dataStore.setAnimations(enabled)
    }

    override suspend fun updateLanguage(language: String) {
        dataStore.setLanguage(language)
    }
}
