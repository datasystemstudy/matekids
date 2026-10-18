package com.educa.mathkids.presentation.screens.adventure

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.educa.mathkids.core.game.AdventureGameEngine
import com.educa.mathkids.core.game.GameSoundEvent
import com.educa.mathkids.core.game.rememberGameLoop
import com.educa.mathkids.domain.model.OperationType
import com.educa.mathkids.domain.model.adventure.AdventurePhase
import com.educa.mathkids.presentation.screens.adventure.components.AdventureChallengeCard
import com.educa.mathkids.presentation.screens.adventure.components.AdventureGameCanvas
import com.educa.mathkids.presentation.screens.adventure.components.AdventureGameOverOverlay
import com.educa.mathkids.presentation.screens.adventure.components.AdventureHintDialog
import com.educa.mathkids.presentation.screens.adventure.components.AdventureTopBar
import com.educa.mathkids.presentation.screens.adventure.components.AdventureVictoryOverlay
import com.educa.mathkids.presentation.utils.HapticUtil
import com.educa.mathkids.presentation.utils.SoundManager

/**
 * Pantalla principal del minijuego 2D de Aventura y Aprendizaje.
 * Conecta el motor de juego en tiempo real (AdventureGameEngine) con el lienzo Compose Canvas,
 * el bucle de juego reactivo y los efectos sonoros y hápticos offline.
 */
@Composable
fun AdventureScreen(
    operationType: OperationType = OperationType.ADDITION,
    levelNumber: Int = 1,
    soundManager: SoundManager? = null,
    hapticUtil: HapticUtil? = null,
    adManager: com.educa.mathkids.presentation.ads.AdManager? = null,
    onNavigateBack: () -> Unit,
    onNavigateHome: () -> Unit = onNavigateBack,
    onLevelCompleted: (level: Int, stars: Int, score: Int) -> Unit = { _, _, _ -> }
) {
    val context = androidx.compose.ui.platform.LocalContext.current

    // 1. Motor de juego con emisión desacoplada de sonido y háptica
    val gameEngine = remember {
        AdventureGameEngine(
            onEmitSound = { soundEvent ->
                when (soundEvent) {
                    GameSoundEvent.CORRECT_ANSWER -> {
                        soundManager?.playCorrect()
                        hapticUtil?.vibrateSuccess()
                        com.educa.mathkids.core.shop.CoinShopManager.addCoin(context, 1)
                    }
                    GameSoundEvent.INCORRECT_ANSWER -> {
                        soundManager?.playIncorrect()
                        hapticUtil?.vibrateEncourage()
                    }
                    GameSoundEvent.COMBO_STREAK -> {
                        soundManager?.playReward()
                        hapticUtil?.vibrateReward()
                    }
                    GameSoundEvent.LEVEL_CLEARED -> {
                        soundManager?.playVictory()
                        hapticUtil?.vibrateReward()
                    }
                    GameSoundEvent.GAME_OVER -> {
                        soundManager?.playIncorrect()
                        hapticUtil?.vibrateEncourage()
                    }
                    GameSoundEvent.ORB_POP -> {
                        hapticUtil?.vibrateClick()
                    }
                    GameSoundEvent.BUTTON_CLICK -> {
                        soundManager?.playButtonClick()
                        hapticUtil?.vibrateClick()
                    }
                }
            }
        )
    }

    val gameState by gameEngine.state.collectAsState()

    val isEnglish = com.educa.mathkids.presentation.localization.LocalAppLanguage.current == com.educa.mathkids.presentation.localization.AppLanguage.INGLES
    var currentOp by remember { mutableStateOf(operationType) }
    var showLevelSelectDialog by remember { mutableStateOf(false) }

    // Interceptar botón atrás del sistema para abrir o cerrar niveles de Aventura Globos
    androidx.activity.compose.BackHandler(enabled = true) {
        if (showLevelSelectDialog) {
            soundManager?.playButtonClick()
            hapticUtil?.vibrateClick()
            showLevelSelectDialog = false
            if (gameState.phase == AdventurePhase.GAME_OVER) {
                gameEngine.restoreLivesAndContinue(3)
            } else if (gameState.phase == AdventurePhase.ROUND_WON) {
                gameEngine.startLevel(level = (gameState.currentLevel + 1).coerceAtMost(15), operation = currentOp)
            }
        } else {
            soundManager?.playButtonClick()
            hapticUtil?.vibrateClick()
            showLevelSelectDialog = true
        }
    }

    // 2. Inicializar nivel
    LaunchedEffect(levelNumber, operationType) {
        currentOp = operationType
        gameEngine.startLevel(level = levelNumber, operation = operationType)
    }

    // 3. Bucle de juego a 60/120 FPS (se pausa si está abierto el diálogo de selección de niveles)
    rememberGameLoop(isRunning = gameState.phase == AdventurePhase.PLAYING && !showLevelSelectDialog) { delta ->
        gameEngine.onTick(delta)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Lienzo 2D interactivo (Renderiza mundo, orbes, héroe y partículas)
        AdventureGameCanvas(
            gameState = gameState,
            onCanvasTapped = { x, y ->
                gameEngine.onCanvasTapped(x, y)
            },
            onSizeChanged = { width, height ->
                gameEngine.setScreenDimensions(width, height)
            }
        )

        // HUD Superior e información de desafío
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AdventureTopBar(
                gameState = gameState,
                onBackClick = {
                    soundManager?.playButtonClick()
                    hapticUtil?.vibrateClick()
                    showLevelSelectDialog = true
                },
                onHomeClick = {
                    com.educa.mathkids.core.game.AdventureProgressManager.clearPendingDialog()
                    onNavigateHome()
                },
                onHintClick = {
                    soundManager?.playButtonClick()
                    hapticUtil?.vibrateClick()
                    gameEngine.toggleHintDialog(true)
                }
            )

            Spacer(modifier = Modifier.height(4.dp))

            AdventureChallengeCard(
                equation = gameState.question.equation,
                prompt = gameState.question.prompt,
                progressText = "${gameState.totalAnsweredCorrectly}/${gameState.totalRequiredToWin}"
            )
        }

        // Diálogo de Pista Didáctica
        if (gameState.showHintDialog) {
            AdventureHintDialog(
                hint = gameState.question.hint,
                explanation = gameState.question.explanation,
                onDismiss = {
                    soundManager?.playButtonClick()
                    gameEngine.toggleHintDialog(false)
                }
            )
        }

        // Superposición de Victoria
        if (gameState.phase == AdventurePhase.ROUND_WON) {
            LaunchedEffect(gameState.phase, gameState.currentLevel) {
                onLevelCompleted(gameState.currentLevel, gameState.stars, gameState.score)
            }

            AdventureVictoryOverlay(
                gameState = gameState,
                onNextLevel = {
                    soundManager?.playButtonClick()
                    onLevelCompleted(gameState.currentLevel, gameState.stars, gameState.score)
                    if (gameState.currentLevel >= 15) {
                        showLevelSelectDialog = true
                    } else {
                        gameEngine.startLevel(level = gameState.currentLevel + 1, operation = currentOp)
                    }
                },
                onBackToMap = {
                    soundManager?.playButtonClick()
                    hapticUtil?.vibrateClick()
                    onLevelCompleted(gameState.currentLevel, gameState.stars, gameState.score)
                    showLevelSelectDialog = true
                }
            )
        }

        // Superposición de Reintento / Game Over con opción de Ver Anuncio para Continuar
        if (gameState.phase == AdventurePhase.GAME_OVER) {
            AdventureGameOverOverlay(
                gameState = gameState,
                isEnglish = isEnglish,
                onWatchAdToContinue = {
                    val activity = context as? android.app.Activity
                    if (activity != null && adManager != null) {
                        adManager.showRewarded(
                            activity = activity,
                            onRewardEarned = {
                                soundManager?.playReward()
                                hapticUtil?.vibrateSuccess()
                                gameEngine.restoreLivesAndContinue(3)
                            },
                            onAdDismissed = {
                                // Ad dismissed
                            }
                        )
                    } else {
                        // Fallback si no hay anuncio disponible
                        soundManager?.playReward()
                        hapticUtil?.vibrateSuccess()
                        gameEngine.restoreLivesAndContinue(3)
                    }
                },
                onCloseClick = {
                    // Al darle a la X, sigue en el juego
                    soundManager?.playButtonClick()
                    hapticUtil?.vibrateClick()
                    gameEngine.restoreLivesAndContinue(3)
                },
                onRetry = {
                    soundManager?.playButtonClick()
                    gameEngine.startLevel(level = gameState.currentLevel, operation = currentOp)
                },
                onBackToMap = {
                    soundManager?.playButtonClick()
                    hapticUtil?.vibrateClick()
                    showLevelSelectDialog = true
                }
            )
        }

        // Diálogo de Selección de Niveles y Operaciones dentro del juego
        if (showLevelSelectDialog) {
            com.educa.mathkids.presentation.screens.home.AdventureLevelSelectDialog(
                initialOp = currentOp,
                isEnglish = isEnglish,
                soundManager = soundManager,
                onDismiss = {
                    soundManager?.playButtonClick()
                    hapticUtil?.vibrateClick()
                    showLevelSelectDialog = false
                    // Al darle a la X, sigue en el juego
                    if (gameState.phase == AdventurePhase.GAME_OVER) {
                        gameEngine.restoreLivesAndContinue(3)
                    } else if (gameState.phase == AdventurePhase.ROUND_WON) {
                        gameEngine.startLevel(level = (gameState.currentLevel + 1).coerceAtMost(15), operation = currentOp)
                    }
                },
                onSelectLevel = { selectedOp, selectedLevel ->
                    showLevelSelectDialog = false
                    currentOp = selectedOp
                    gameEngine.startLevel(level = selectedLevel, operation = selectedOp)
                }
            )
        }
    }
}
