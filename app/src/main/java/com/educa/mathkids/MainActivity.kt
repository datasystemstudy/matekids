package com.educa.mathkids

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.educa.mathkids.data.local.datastore.SettingsDataStore
import com.educa.mathkids.data.local.db.MathKidsDatabase
import com.educa.mathkids.data.repository.PlayerProgressRepositoryImpl
import com.educa.mathkids.data.repository.ProgressRepositoryImpl
import com.educa.mathkids.data.repository.SettingsRepositoryImpl
import com.educa.mathkids.domain.model.AppSettings
import com.educa.mathkids.presentation.navigation.MathKidsNavGraph
import com.educa.mathkids.presentation.theme.BackgroundCream
import com.educa.mathkids.presentation.theme.LocalAnimationsEnabled
import com.educa.mathkids.presentation.theme.MathKidsTheme
import com.educa.mathkids.presentation.utils.HapticUtil
import com.educa.mathkids.presentation.utils.SoundManager
import com.educa.mathkids.presentation.utils.TtsManager

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Inicialización de la base de datos local SQLite (Room)
        val database = MathKidsDatabase.getInstance(applicationContext)
        val playerProgressRepository = PlayerProgressRepositoryImpl(
            playerProgressDao = database.playerProgressDao(),
            sessionHistoryDao = database.sessionHistoryDao(),
            playerProfileDao = database.playerProfileDao(),
            rewardDao = database.rewardDao(),
            gameSessionDao = database.gameSessionDao(),
            operationLevelDao = database.operationLevelDao()
        )
        val progressRepository = ProgressRepositoryImpl(
            playerProfileDao = database.playerProfileDao(),
            playerProgressDao = database.playerProgressDao(),
            gameSessionDao = database.gameSessionDao()
        )

        // 2. Inicialización del almacén de preferencias locales (DataStore)
        val settingsDataStore = SettingsDataStore(applicationContext)
        val settingsRepository = SettingsRepositoryImpl(settingsDataStore)

        setContent {
            val settings by settingsRepository.getSettings().collectAsState(initial = AppSettings())

            val soundManager = remember { SoundManager(applicationContext) }
            val ttsManager = remember { TtsManager(applicationContext) }
            val hapticUtil = remember { HapticUtil(applicationContext) }

            // Sincronizar dinámicamente las preferencias de audio, voz y háptica
            LaunchedEffect(settings.soundsGeneralEnabled, settings.soundEffectsEnabled) {
                soundManager.isSoundEnabled = settings.soundsGeneralEnabled && settings.soundEffectsEnabled
            }
            LaunchedEffect(settings.voiceEnabled) {
                ttsManager.isVoiceEnabled = settings.voiceEnabled
            }
            LaunchedEffect(settings.hapticsEnabled) {
                hapticUtil.isHapticsEnabled = settings.hapticsEnabled
            }
            LaunchedEffect(settings.language) {
                ttsManager.setLanguage(settings.language)
            }

            DisposableEffect(Unit) {
                onDispose {
                    soundManager.release()
                    ttsManager.shutdown()
                }
            }

            val adManager = remember { com.educa.mathkids.presentation.ads.AdManager.getInstance(applicationContext) }

            val appLanguage = remember(settings.language) {
                com.educa.mathkids.presentation.localization.AppLanguage.fromCode(settings.language)
            }

            CompositionLocalProvider(
                LocalAnimationsEnabled provides settings.animationsEnabled,
                com.educa.mathkids.presentation.localization.LocalAppLanguage provides appLanguage
            ) {
                MathKidsTheme {
                    val navController = rememberNavController()

                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = BackgroundCream
                    ) {
                        MathKidsNavGraph(
                            navController = navController,
                            playerProgressRepository = playerProgressRepository,
                            progressRepository = progressRepository,
                            settingsRepository = settingsRepository,
                            soundManager = soundManager,
                            ttsManager = ttsManager,
                            hapticUtil = hapticUtil,
                            adManager = adManager
                        )
                    }
                }
            }
        }
    }
}
