package com.educa.mathkids.presentation.screens.exercise

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.draw.shadow
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import com.educa.mathkids.presentation.theme.TextDark
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import kotlinx.coroutines.delay
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.educa.mathkids.R
import com.educa.mathkids.domain.model.DidacticStrategy
import com.educa.mathkids.domain.model.EvaluationStage
import com.educa.mathkids.domain.model.MissingTarget
import com.educa.mathkids.domain.model.OperationType
import com.educa.mathkids.domain.model.SessionResult
import android.app.Activity
import androidx.compose.ui.platform.LocalContext
import com.educa.mathkids.presentation.ads.AdManager
import com.educa.mathkids.presentation.components.OutOfLivesDialog
import com.educa.mathkids.presentation.components.AnimatedGameButton
import com.educa.mathkids.presentation.components.AnswerButton
import com.educa.mathkids.presentation.components.AnswerButtonState
import com.educa.mathkids.presentation.components.ConfettiCelebration
import com.educa.mathkids.presentation.components.DynamicKidsBackground
import com.educa.mathkids.presentation.components.KeypadView
import com.educa.mathkids.presentation.components.KidCard
import com.educa.mathkids.presentation.components.Mascot
import com.educa.mathkids.presentation.components.MascotMood
import com.educa.mathkids.presentation.components.PedagogicalFeedbackDialog
import com.educa.mathkids.presentation.components.RewardUnlockDialog
import com.educa.mathkids.presentation.components.ScoreDisplay
import com.educa.mathkids.presentation.components.ScreenHeader
import com.educa.mathkids.presentation.components.StreakDisplay
import com.educa.mathkids.presentation.components.VisualRepresentationView
import com.educa.mathkids.presentation.theme.AnimationConfig
import com.educa.mathkids.presentation.theme.BackgroundCream
import com.educa.mathkids.presentation.theme.CoralEncouragement
import com.educa.mathkids.presentation.theme.Dimens
import com.educa.mathkids.presentation.theme.FredokaFont
import com.educa.mathkids.presentation.theme.GreenPrimary
import com.educa.mathkids.presentation.theme.GreenPrimaryDark
import com.educa.mathkids.presentation.theme.LocalAnimationsEnabled
import com.educa.mathkids.presentation.theme.OrangeSecondary
import com.educa.mathkids.presentation.theme.SlateBorder
import com.educa.mathkids.presentation.theme.SlateDark
import com.educa.mathkids.presentation.theme.SlateMuted
import com.educa.mathkids.presentation.theme.SurfaceWhite
import kotlin.math.roundToInt

import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import com.educa.mathkids.presentation.components.MetaProgressiveHintDialog
import com.educa.mathkids.presentation.components.OperationLevelTutorialDialog
import com.educa.mathkids.presentation.components.OperationLevelTutorialScreen
import com.educa.mathkids.presentation.theme.OrangeSecondaryDark
import com.educa.mathkids.presentation.utils.SoundManager
import com.educa.mathkids.presentation.utils.TtsManager

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.ui.text.style.TextOverflow
import com.educa.mathkids.presentation.components.KidsGameNavDock
import com.educa.mathkids.presentation.components.KidsExitDialog

/**
 * Pantalla principal interactiva de ejercicio (Fases 8, 9, 14).
 * Integra representación visual dinámica, entrada por opciones o teclado táctil,
 * retroalimentación pedagógica explicativa paso a paso y diseño tecnológico.
 */
@Composable
fun ExerciseScreen(
    viewModel: ExerciseViewModel,
    onSessionCompleted: (SessionResult) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    onInicioClick: (() -> Unit)? = null,
    onNivelesClick: (() -> Unit)? = null,
    ttsManager: TtsManager? = null,
    soundManager: SoundManager? = null,
    adManager: AdManager? = null
) {
    val uiState by viewModel.uiState.collectAsState()
    val animationsEnabled = LocalAnimationsEnabled.current
    val exercise = uiState.currentExercise
    val operationType = exercise?.operationType ?: viewModel.operationType
    val opColor = Color(operationType.colorHex)
    var showProgressiveHintDialog by remember { mutableStateOf(false) }
    var showLevelTutorial by remember { mutableStateOf(true) }
    var showExitDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val activity = context as? Activity

    val strings = com.educa.mathkids.presentation.localization.appStrings()
    val isEnglish = com.educa.mathkids.presentation.localization.LocalAppLanguage.current == com.educa.mathkids.presentation.localization.AppLanguage.INGLES

    // Interceptar botón atrás físico/gestual del sistema durante la partida
    BackHandler(enabled = !showLevelTutorial) {
        showExitDialog = true
        ttsManager?.speak(strings.exitGameDialogTitle)
    }

    // Navegación automática a resultados
    LaunchedEffect(uiState.isSessionFinished, uiState.sessionResult) {
        if (uiState.isSessionFinished && uiState.sessionResult != null) {
            onSessionCompleted(uiState.sessionResult!!)
        }
    }

    // Estado animado de la mascota
    val currentMascotMood = when {
        uiState.isSpecialStreakMilestone -> MascotMood.CELEBRATING
        uiState.isAnswerSubmitted && uiState.isCorrect == true -> MascotMood.HAPPY
        uiState.isAnswerSubmitted && uiState.isCorrect == false -> MascotMood.ENCOURAGING
        else -> MascotMood.NORMAL
    }

    val animatedProgress by animateFloatAsState(
        targetValue = uiState.progressFraction,
        animationSpec = if (animationsEnabled) AnimationConfig.normalTween() else tween(0),
        label = "exercise_progress"
    )

    // Puntos flotantes
    val floatPointsY = remember { Animatable(0f) }
    val floatPointsAlpha = remember { Animatable(0f) }

    LaunchedEffect(uiState.isAnswerSubmitted, uiState.pointsAwardedThisExercise) {
        if (uiState.isAnswerSubmitted && uiState.pointsAwardedThisExercise > 0 && animationsEnabled) {
            floatPointsY.snapTo(0f)
            floatPointsAlpha.snapTo(1f)
            floatPointsY.animateTo(
                targetValue = -35f,
                animationSpec = tween(durationMillis = AnimationConfig.FLOATING_POINTS)
            )
            floatPointsAlpha.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = 200)
            )
        } else {
            floatPointsAlpha.snapTo(0f)
        }
    }

    val scrollState = rememberScrollState()

    // Auto-scroll al final para que el botón "Siguiente" quede siempre visible sin deslizar manualmente
    LaunchedEffect(uiState.isAnswerSubmitted) {
        if (uiState.isAnswerSubmitted) {
            delay(100)
            scrollState.animateScrollTo(scrollState.maxValue)
        }
    }

    LaunchedEffect(uiState.currentExerciseNumber) {
        scrollState.scrollTo(0)
    }

    DynamicKidsBackground(modifier = modifier) {
        // Confeti en rachas destacadas
        ConfettiCelebration(visible = uiState.isSpecialStreakMilestone)

        // Tutorial pedagógico con audio y voz antes de comenzar el nivel (Pantalla completa oficial)
        if (showLevelTutorial) {
            OperationLevelTutorialScreen(
                operationType = operationType,
                levelNumber = uiState.levelNumber,
                onDismiss = { showLevelTutorial = false },
                onBackClick = onBackClick,
                ttsManager = ttsManager,
                soundManager = soundManager
            )
        } else {
            // ── BOX raíz que permite superponer el dock y el botón Siguiente ──
            Box(modifier = Modifier.fillMaxSize()) {

                // ── Contenido principal con scroll ──
                // padding bottom = 82dp (dock) + 52dp (botón Siguiente) + 8dp extra = 142dp
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(start = Dimens.SpacingLarge, end = Dimens.SpacingLarge, top = 4.dp, bottom = 148.dp)
                        .verticalScroll(scrollState),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                // Cabecera compacta con título y Mascota animada
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 2.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = operationType.getAdventureName(isEnglish),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FredokaFont,
                            color = TextDark
                        )

                        // Mascota integrada en la cabecera
                        Mascot(
                            size = 38.dp,
                            mood = currentMascotMood
                        )
                    }

                // Fila de motivación y métricas: Nivel, Tutorial, Puntos, Racha y Conteo
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Badge de Nivel y Botón de Tutorial
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(opColor.copy(alpha = 0.12f))
                                    .border(1.dp, opColor.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = "Nivel ${uiState.levelNumber}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Black,
                                    color = opColor
                                )
                            }

                            Spacer(modifier = Modifier.width(4.dp))

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(OrangeSecondary.copy(alpha = 0.15f))
                                    .border(1.dp, OrangeSecondary.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                                    .clickable {
                                        soundManager?.playButtonClick()
                                        showLevelTutorial = true
                                    }
                                    .padding(horizontal = 6.dp, vertical = 3.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = "📖", fontSize = 11.sp)
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text(
                                        text = "Tutorial",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Black,
                                        color = OrangeSecondaryDark
                                    )
                                }
                            }
                        }

                        // Indicador de 3 Vidas
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFFFFEBEE))
                                .border(1.dp, Color(0xFFFFCDD2), RoundedCornerShape(10.dp))
                                .padding(horizontal = 6.dp, vertical = 3.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                repeat(uiState.maxLives) { index ->
                                    val isAlive = index < uiState.lives
                                    Text(
                                        text = if (isAlive) "❤️" else "🖤",
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }

                        // Puntos
                        Box {
                            ScoreDisplay(
                                points = uiState.sessionPoints,
                                showLabel = false,
                                fontSize = 14.sp
                            )
                            if (floatPointsAlpha.value > 0f) {
                                Text(
                                    text = "+${uiState.pointsAwardedThisExercise} 🎯",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Black,
                                    color = OrangeSecondary,
                                    modifier = Modifier
                                        .offset { IntOffset(0, floatPointsY.value.roundToInt()) }
                                        .alpha(floatPointsAlpha.value)
                                )
                            }
                        }

                        // Racha
                        StreakDisplay(
                            streak = uiState.currentStreak,
                            showLabel = false,
                            fontSize = 13.sp
                        )

                        // Número de pregunta
                        Text(
                            text = "${uiState.currentExerciseNumber}/${uiState.totalExercises}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            color = SlateDark
                        )
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    // Barra de progreso interactiva
                    LinearProgressIndicator(
                        progress = { animatedProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(5.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = opColor,
                        trackColor = Color(0xFFE2E8F0)
                    )

                    Spacer(modifier = Modifier.height(3.dp))

                    // Barra de Etapas de Evaluación
                    EvaluationStagesBar(
                        currentStage = uiState.currentStage,
                        accentColor = opColor,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 2.dp)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Transición animada del ejercicio
                    AnimatedContent(
                        targetState = exercise,
                        transitionSpec = {
                            if (animationsEnabled) {
                                (fadeIn(AnimationConfig.normalTween()) + scaleIn(initialScale = 0.94f))
                                    .togetherWith(fadeOut(AnimationConfig.fastTween()))
                            } else {
                                fadeIn(tween(0)).togetherWith(fadeOut(tween(0)))
                            }
                        },
                        label = "exercise_transition",
                        modifier = Modifier.fillMaxWidth()
                    ) { currentEx ->
                if (currentEx != null) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Tarjeta Principal de la Ecuación Matemática con Representación Visual Integrada
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .shadow(
                                    elevation = 5.dp,
                                    shape = RoundedCornerShape(22.dp),
                                    spotColor = opColor.copy(alpha = 0.18f)
                                )
                                .clip(RoundedCornerShape(22.dp))
                                .background(
                                    Brush.verticalGradient(
                                        listOf(
                                            SurfaceWhite,
                                            opColor.copy(alpha = 0.035f)
                                        )
                                    )
                                )
                                .border(
                                    width = 2.dp,
                                    color = opColor.copy(alpha = 0.28f),
                                    shape = RoundedCornerShape(22.dp)
                                )
                                .padding(horizontal = 14.dp, vertical = 10.dp)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                // Fila superior: Badges a la izquierda + Botones de audio y Ayuda a la derecha
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.weight(1f, fill = false),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(opColor.copy(alpha = 0.09f))
                                                .border(1.dp, opColor.copy(alpha = 0.28f), RoundedCornerShape(8.dp))
                                                .padding(horizontal = 6.dp, vertical = 3.dp)
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(text = currentEx.didacticStrategy.iconEmoji, fontSize = 12.sp)
                                                Spacer(modifier = Modifier.width(3.dp))
                                                Text(
                                                    text = currentEx.didacticStrategy.title,
                                                    fontSize = 10.5.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = opColor,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                            }
                                        }

                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(OrangeSecondary.copy(alpha = 0.12f))
                                                .border(1.dp, OrangeSecondary.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
                                                .padding(horizontal = 6.dp, vertical = 3.dp)
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(text = uiState.currentStage.badgeIcon, fontSize = 11.sp)
                                                Spacer(modifier = Modifier.width(3.dp))
                                                Text(
                                                    text = uiState.currentStage.shortName,
                                                    fontSize = 10.5.sp,
                                                    fontWeight = FontWeight.ExtraBold,
                                                    color = OrangeSecondaryDark,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(6.dp))

                                    Row(
                                        modifier = Modifier.wrapContentWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        // Botón para escuchar la operación en voz de Mate
                                        if (ttsManager != null) {
                                            Box(
                                                modifier = Modifier
                                                    .size(28.dp)
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .background(opColor.copy(alpha = 0.12f))
                                                    .border(1.dp, opColor.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
                                                    .clickable {
                                                        soundManager?.playButtonClick()
                                                        ttsManager.speakExercise(currentEx)
                                                    },
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(text = "🔊", fontSize = 13.sp)
                                            }
                                        }

                                        // Botón de Ayuda Progresiva horizontal y compacto
                                        Box(
                                            modifier = Modifier
                                                .shadow(2.dp, RoundedCornerShape(8.dp), spotColor = Color.Black.copy(alpha = 0.10f))
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(Color(0xFFFFF3E0))
                                                .border(1.2.dp, OrangeSecondary, RoundedCornerShape(8.dp))
                                                .clickable {
                                                    soundManager?.playHint()
                                                    showProgressiveHintDialog = true
                                                }
                                                .padding(horizontal = 7.dp, vertical = 4.dp)
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.Center
                                            ) {
                                                Text(text = "💡", fontSize = 12.sp)
                                                Spacer(modifier = Modifier.width(3.dp))
                                                Text(
                                                    text = "Ayuda",
                                                    fontSize = 11.5.sp,
                                                    fontWeight = FontWeight.Black,
                                                    fontFamily = FredokaFont,
                                                    color = Color(0xFFE65100),
                                                    maxLines = 1,
                                                    softWrap = false
                                                )
                                            }
                                        }
                                    }
                                }

                                // Mostrar enunciado contextual sólo si no es una duplicación de la ecuación simple
                                val equationStr = currentEx.displayEquation ?: "${currentEx.operand1} ${currentEx.operationType.symbol} ${currentEx.operand2} = ?"
                                val cleanQuestion = currentEx.questionText.trim().replace(" ", "").replace("¿", "").replace("?", "")
                                val cleanEq = equationStr.trim().replace(" ", "").replace("¿", "").replace("?", "")
                                val shouldShowQuestionText = cleanQuestion.isNotEmpty() &&
                                    cleanQuestion != cleanEq &&
                                    cleanQuestion != "${currentEx.operand1}${currentEx.operationType.symbol}${currentEx.operand2}"

                                if (shouldShowQuestionText) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(Color(0xFFF1F5F9))
                                            .padding(horizontal = 10.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = currentEx.questionText,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = SlateDark,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                // Representación visual interactiva
                                VisualRepresentationView(
                                    visual = currentEx.visualRepresentation,
                                    accentColor = opColor,
                                    modifier = Modifier.fillMaxWidth(),
                                    withContainer = false,
                                    keypadInput = uiState.keypadInput,
                                    soundManager = soundManager
                                )

                                val isVisualAlreadyEquation = currentEx.visualRepresentation.visualType in listOf(
                                    com.educa.mathkids.domain.model.VisualType.BALANCE_SCALE,
                                    com.educa.mathkids.domain.model.VisualType.SKIP_COUNTING
                                )

                                if (!isVisualAlreadyEquation) {
                                    Spacer(modifier = Modifier.height(6.dp))

                                    // Ecuación animada con la Caja Misteriosa "?" táctil
                                    if (equationStr.contains("?")) {
                                        val parts = equationStr.split("?")
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.Center,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            if (parts[0].isNotEmpty()) {
                                                Text(
                                                    text = parts[0],
                                                    fontSize = if (equationStr.length > 16) 26.sp else 32.sp,
                                                    fontWeight = FontWeight.Black,
                                                    color = SlateDark
                                                )
                                            }

                                            // Caja Misteriosa "?" con animación de respiración y relieve 3D
                                            val infiniteTransition = rememberInfiniteTransition(label = "mystery_box_pulse")
                                            val pulseScale by infiniteTransition.animateFloat(
                                                initialValue = 1.0f,
                                                targetValue = if (animationsEnabled && (!currentEx.isKeypadAllowed || uiState.keypadInput.isEmpty())) 1.08f else 1.0f,
                                                animationSpec = infiniteRepeatable(
                                                    animation = tween(700, easing = EaseInOutCubic),
                                                    repeatMode = RepeatMode.Reverse
                                                ),
                                                label = "mystery_scale"
                                            )

                                            val isKeypadFilled = currentEx.isKeypadAllowed && uiState.keypadInput.isNotEmpty()
                                            val mysteryBoxColor = if (isKeypadFilled) opColor else OrangeSecondary

                                            Box(
                                                modifier = Modifier
                                                    .padding(horizontal = 6.dp)
                                                    .scale(pulseScale)
                                                    .shadow(3.dp, RoundedCornerShape(14.dp), spotColor = mysteryBoxColor.copy(alpha = 0.4f))
                                                    .clip(RoundedCornerShape(14.dp))
                                                    .background(
                                                        Brush.verticalGradient(
                                                            if (isKeypadFilled) {
                                                                listOf(opColor.copy(alpha = 0.2f), opColor.copy(alpha = 0.10f))
                                                            } else {
                                                                listOf(Color(0xFFFFB74D), OrangeSecondary)
                                                            }
                                                        )
                                                    )
                                                    .border(2.dp, mysteryBoxColor, RoundedCornerShape(14.dp))
                                                    .clickable {
                                                        ttsManager?.speakExercise(currentEx)
                                                    }
                                                    .padding(horizontal = 14.dp, vertical = 4.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = if (isKeypadFilled) uiState.keypadInput else "?",
                                                    fontSize = if (equationStr.length > 16) 24.sp else 30.sp,
                                                    fontWeight = FontWeight.Black,
                                                    color = if (isKeypadFilled) opColor else Color.White
                                                )
                                            }

                                            if (parts.size > 1 && parts[1].isNotEmpty()) {
                                                Text(
                                                    text = parts[1],
                                                    fontSize = if (equationStr.length > 16) 26.sp else 32.sp,
                                                    fontWeight = FontWeight.Black,
                                                    color = SlateDark
                                                )
                                            }
                                        }
                                    } else {
                                        Text(
                                            text = equationStr,
                                            fontSize = if (equationStr.length > 16) 26.sp else 32.sp,
                                            fontWeight = FontWeight.Black,
                                            color = SlateDark,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Entrada de respuesta: Teclado numérico o Selección de 4 opciones
                        if (currentEx.isKeypadAllowed) {
                            // Modo Teclado Táctil
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                // Pantalla de entrada del número
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(0.6f)
                                        .height(42.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(
                                            if (uiState.isAnswerSubmitted) {
                                                if (uiState.isCorrect == true) GreenPrimary.copy(alpha = 0.2f)
                                                else CoralEncouragement.copy(alpha = 0.2f)
                                            } else SurfaceWhite
                                        )
                                        .border(
                                            2.dp,
                                            if (uiState.isAnswerSubmitted) {
                                                if (uiState.isCorrect == true) GreenPrimary else CoralEncouragement
                                            } else opColor,
                                            RoundedCornerShape(12.dp)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = uiState.keypadInput.ifEmpty { "¿ ?" },
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Black,
                                        color = if (uiState.isAnswerSubmitted) {
                                            if (uiState.isCorrect == true) GreenPrimaryDark else CoralEncouragement
                                        } else if (uiState.keypadInput.isEmpty()) SlateMuted.copy(alpha = 0.5f) else opColor
                                    )
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                if (!uiState.isAnswerSubmitted) {
                                    KeypadView(
                                        currentInput = uiState.keypadInput,
                                        onDigitPress = { viewModel.onKeypadDigit(it) },
                                        onBackspacePress = { viewModel.onKeypadBackspace() },
                                        onSubmitPress = { viewModel.onKeypadSubmit() },
                                        accentColor = opColor
                                    )
                                }
                            }
                        } else {
                            // Modo Selección Múltiple (4 Botones 3D Táctiles en cuadrícula 2x2)
                            Column(
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                val optionRows = currentEx.options.chunked(2)
                                optionRows.forEachIndexed { rowIndex, rowOptions ->
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        rowOptions.forEachIndexed { colIndex, option ->
                                            val buttonIndex = rowIndex * 2 + colIndex
                                            val buttonState = when {
                                                !uiState.isAnswerSubmitted -> AnswerButtonState.DEFAULT
                                                option == uiState.selectedAnswer && uiState.isCorrect == true -> AnswerButtonState.SELECTED_CORRECT
                                                option == uiState.selectedAnswer && uiState.isCorrect == false -> AnswerButtonState.SELECTED_INCORRECT
                                                option == uiState.correctAnswer && uiState.isCorrect == false -> AnswerButtonState.HIGHLIGHT_CORRECT
                                                else -> AnswerButtonState.DISABLED
                                            }

                                            AnswerButton(
                                                text = "$option",
                                                onClick = { viewModel.onAnswerSelected(option) },
                                                state = buttonState,
                                                enabled = !uiState.isAnswerSubmitted,
                                                buttonIndex = buttonIndex,
                                                modifier = Modifier.weight(1f)
                                            )
                                        }
                                        if (rowOptions.size == 1) {
                                            Spacer(modifier = Modifier.weight(1f))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Sección de Feedback (sin el botón Siguiente — ahora está fijo abajo)
                    AnimatedVisibility(
                        visible = uiState.isAnswerSubmitted,
                        enter = if (animationsEnabled) {
                            fadeIn(AnimationConfig.normalTween()) + slideInVertically(
                                initialOffsetY = { it / 2 },
                                animationSpec = AnimationConfig.quickSpring()
                            )
                        } else {
                            fadeIn(tween(0))
                        }
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(
                                    if (uiState.isCorrect == true) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
                                )
                                .padding(vertical = 6.dp, horizontal = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = uiState.feedbackMessage ?: "",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (uiState.isCorrect == true) GreenPrimaryDark else CoralEncouragement,
                                    textAlign = TextAlign.Center
                                )

                                if (uiState.isCorrect == false) {
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "💡 Toca aquí para ver la Ayuda de Mate paso a paso",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FredokaFont,
                                        color = OrangeSecondary,
                                        modifier = Modifier.clickable {
                                            soundManager?.playHint()
                                            showProgressiveHintDialog = true
                                        }
                                    )
                                }

                                if (uiState.streakBonusMessage != null) {
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = uiState.streakBonusMessage!!,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color(0xFFE65100),
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                } // fin Column scroll

                // ── BOTÓN SIGUIENTE FIJO — encima del dock de navegación ──
                // Visible sólo cuando hay respuesta enviada
                AnimatedVisibility(
                    visible = uiState.isAnswerSubmitted,
                    enter = if (animationsEnabled) {
                        fadeIn(AnimationConfig.normalTween()) + slideInVertically { it }
                    } else fadeIn(tween(0)),
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .navigationBarsPadding()
                        // 82dp = altura del dock + su padding
                        .padding(bottom = 88.dp, start = 16.dp, end = 16.dp)
                ) {
                    val isLastExercise = uiState.currentExerciseNumber >= uiState.totalExercises
                    AnimatedGameButton(
                        text = if (isLastExercise) "Ver Resultados 🏆" else "Siguiente ➜",
                        onClick = { viewModel.onNextExercise() },
                        enabled = uiState.isNextButtonEnabled,
                        primaryColor = GreenPrimary,
                        darkColor = GreenPrimaryDark,
                        height = 50.dp,
                        fontSize = 18.sp,
                        modifier = Modifier.fillMaxWidth()
                    )
                }


                // Sistema de Pistas Progresivas de Mate (Sección 13)
                if (showProgressiveHintDialog && exercise != null) {
                    MetaProgressiveHintDialog(
                        exercise = exercise,
                        accentColor = opColor,
                        ttsManager = ttsManager,
                        onDismiss = { showProgressiveHintDialog = false }
                    )
                }

                // Diálogo pedagógico cuando falla ("Vamos a resolverlo juntos")
                if (uiState.showPedagogicalExplanation && exercise != null) {
                    PedagogicalFeedbackDialog(
                        exercise = exercise,
                        accentColor = opColor,
                        onContinue = { viewModel.onDismissPedagogicalExplanation() }
                    )
                }

                // Diálogo de recompensa desbloqueada
                if (uiState.newlyUnlockedReward != null) {
                    RewardUnlockDialog(
                        reward = uiState.newlyUnlockedReward!!,
                        onDismiss = { viewModel.onRewardDismissed() }
                    )
                }

                // Diálogo de 3 vidas agotadas (Ver Anuncio o Salir al Inicio)
                if (uiState.isOutOfLives) {
                    val context = LocalContext.current
                    val activity = context as? Activity
                    val activeAdManager = adManager ?: AdManager.getInstance(context)

                    OutOfLivesDialog(
                        onWatchAd = {
                            if (activity != null) {
                                activeAdManager.showRewarded(
                                    activity = activity,
                                    onRewardEarned = {
                                        viewModel.restoreLivesAfterAd()
                                    }
                                )
                            } else {
                                viewModel.restoreLivesAfterAd()
                            }
                        },
                        onExitToHome = {
                            if (activity != null) {
                                activeAdManager.showInterstitial(activity, onBackClick)
                            } else {
                                onBackClick()
                            }
                        }
                    )
                }

                // Barra de navegación inferior oficial (INICIO, ATRÁS, NIVELES, SALIDA)
                if (!showLevelTutorial) {
                    KidsGameNavDock(
                        onInicioClick = {
                            soundManager?.playButtonClick()
                            onInicioClick?.invoke() ?: onBackClick()
                        },
                        onAtrasClick = {
                            soundManager?.playButtonClick()
                            onBackClick()
                        },
                        onNivelesClick = {
                            soundManager?.playButtonClick()
                            onNivelesClick?.invoke() ?: onBackClick()
                        },
                        // FIX: Botón Salida cierra la app directamente
                        onSalidaClick = {
                            soundManager?.playButtonClick()
                            activity?.finishAffinity()
                        },
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .navigationBarsPadding()
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                    )
                }

                // Diálogo de Confirmación para Salir con voz
                if (showExitDialog) {
                    KidsExitDialog(
                        title = strings.exitGameDialogTitle,
                        message = strings.exitGameDialogMessage,
                        confirmButtonText = strings.exitConfirm,
                        dismissButtonText = strings.exitDismiss,
                        ttsManager = ttsManager,
                        onConfirm = {
                            showExitDialog = false
                            (onInicioClick ?: onBackClick).invoke()
                        },
                        onDismiss = {
                            showExitDialog = false
                        }
                    )
                }

            } // fin Box raíz
        } // fin else (no tutorial)
    } // fin DynamicKidsBackground
}

/**
 * Barra visual de 3 etapas de evaluación (Selección Simple -> Completar Opción -> Completar Escrito).
 */
@Composable
fun EvaluationStagesBar(
    currentStage: EvaluationStage,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    val stages = EvaluationStage.entries
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White.copy(alpha = 0.9f))
            .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(16.dp))
            .padding(horizontal = 8.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        stages.forEachIndexed { index, stage ->
            val isCurrent = stage == currentStage
            val isPassed = stage.ordinal < currentStage.ordinal

            val stageBg = when {
                isCurrent -> accentColor.copy(alpha = 0.14f)
                isPassed -> GreenPrimary.copy(alpha = 0.12f)
                else -> Color.Transparent
            }
            val stageBorder = when {
                isCurrent -> accentColor
                isPassed -> GreenPrimary
                else -> Color(0xFFE2E8F0)
            }
            val contentColor = when {
                isCurrent -> accentColor
                isPassed -> GreenPrimaryDark
                else -> SlateMuted.copy(alpha = 0.7f)
            }

            Row(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(stageBg)
                    .border(if (isCurrent || isPassed) 1.5.dp else 1.dp, stageBorder, RoundedCornerShape(10.dp))
                    .padding(vertical = 5.dp, horizontal = 4.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isPassed) "✅" else stage.badgeIcon,
                    fontSize = 11.sp
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                    text = stage.shortName,
                    fontSize = 11.sp,
                    fontWeight = if (isCurrent) FontWeight.ExtraBold else FontWeight.Bold,
                    color = contentColor,
                    maxLines = 1
                )
            }

            if (index < stages.size - 1) {
                Text(
                    text = "›",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    color = if (isPassed) GreenPrimary else Color(0xFFCBD5E1),
                    modifier = Modifier.padding(horizontal = 2.dp)
                )
            }
        }
    }
}
