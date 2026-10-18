package com.educa.mathkids.domain.repository

import com.educa.mathkids.domain.model.AppSettings
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun getSettings(): Flow<AppSettings>
    suspend fun updateSoundsGeneral(enabled: Boolean)
    suspend fun updateVoice(enabled: Boolean)
    suspend fun updateSoundEffects(enabled: Boolean)
    suspend fun updateMusic(enabled: Boolean)
    suspend fun updateHaptics(enabled: Boolean)
    suspend fun updateAnimations(enabled: Boolean)
    suspend fun updateLanguage(language: String)
}
