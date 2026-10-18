package com.educa.mathkids.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.educa.mathkids.domain.model.AppSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "mathkids_settings")

class SettingsDataStore(private val context: Context) {

    companion object {
        val KEY_SOUNDS_GENERAL = booleanPreferencesKey("sounds_general")
        val KEY_VOICE = booleanPreferencesKey("voice_tts")
        val KEY_MUSIC = booleanPreferencesKey("music")
        val KEY_SOUND_EFFECTS = booleanPreferencesKey("sound_effects")
        val KEY_HAPTICS = booleanPreferencesKey("haptics")
        val KEY_ANIMATIONS = booleanPreferencesKey("animations")
        val KEY_LANGUAGE = stringPreferencesKey("app_language")
    }

    val settingsFlow: Flow<AppSettings> = context.dataStore.data.map { preferences ->
        AppSettings(
            soundsGeneralEnabled = preferences[KEY_SOUNDS_GENERAL] ?: true,
            voiceEnabled = preferences[KEY_VOICE] ?: true,
            musicEnabled = preferences[KEY_MUSIC] ?: true,
            soundEffectsEnabled = preferences[KEY_SOUND_EFFECTS] ?: true,
            hapticsEnabled = preferences[KEY_HAPTICS] ?: true,
            animationsEnabled = preferences[KEY_ANIMATIONS] ?: true,
            language = preferences[KEY_LANGUAGE] ?: "es"
        )
    }

    suspend fun setSoundsGeneral(enabled: Boolean) {
        context.dataStore.edit { it[KEY_SOUNDS_GENERAL] = enabled }
    }

    suspend fun setVoice(enabled: Boolean) {
        context.dataStore.edit { it[KEY_VOICE] = enabled }
    }

    suspend fun setSoundEffects(enabled: Boolean) {
        context.dataStore.edit { it[KEY_SOUND_EFFECTS] = enabled }
    }

    suspend fun setMusic(enabled: Boolean) {
        context.dataStore.edit { it[KEY_MUSIC] = enabled }
    }

    suspend fun setHaptics(enabled: Boolean) {
        context.dataStore.edit { it[KEY_HAPTICS] = enabled }
    }

    suspend fun setAnimations(enabled: Boolean) {
        context.dataStore.edit { it[KEY_ANIMATIONS] = enabled }
    }

    suspend fun setLanguage(language: String) {
        context.dataStore.edit { it[KEY_LANGUAGE] = language }
    }
}
