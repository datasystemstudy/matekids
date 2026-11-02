package com.educa.mathkids.presentation.screens.problemario

import android.app.Activity
import com.educa.mathkids.presentation.ads.AdManager
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.educa.mathkids.domain.model.problemario.ProblemCategory
import com.educa.mathkids.domain.model.problemario.ProblemDifficulty
import com.educa.mathkids.domain.model.problemario.ProblemSolveMode
import com.educa.mathkids.presentation.components.ConfettiCelebration
import com.educa.mathkids.presentation.components.DynamicKidsBackground
import com.educa.mathkids.presentation.components.KeypadView
import com.educa.mathkids.presentation.components.KidsExitDialog
import com.educa.mathkids.presentation.components.KidsGameNavDock
import com.educa.mathkids.presentation.components.Mascot
import com.educa.mathkids.presentation.components.MascotMood
import com.educa.mathkids.presentation.screens.problemario.components.ProblemVisualRepresentation
import com.educa.mathkids.presentation.theme.Dimens
import com.educa.mathkids.presentation.theme.FredokaFont
import com.educa.mathkids.presentation.theme.GreenPrimary
import com.educa.mathkids.presentation.theme.OrangeSecondary
import com.educa.mathkids.presentation.theme.OrangeSecondaryDark
import com.educa.mathkids.presentation.theme.SlateBorder
import com.educa.mathkids.presentation.theme.SlateDark
import com.educa.mathkids.presentation.theme.SlateMuted
import com.educa.mathkids.presentation.theme.SoftRed
import com.educa.mathkids.presentation.theme.SurfaceWhite
import com.educa.mathkids.presentation.utils.SoundManager
import com.educa.mathkids.presentation.utils.TtsManager

@Composable
fun ProblemarioPlayScreen(
    category: ProblemCategory,
    difficulty: ProblemDifficulty,
    viewModel: ProblemarioViewModel,
    onBackClick: () -> Unit,
    onInicioClick: () -> Unit,
    onJuegosClick: () -> Unit,
    ttsManager: TtsManager,
    soundManager: SoundManager?,
    modifier: Modifier = Modifier
) {
    val strings = com.educa.mathkids.presentation.localization.appStrings()
    val isEnglish = com.educa.mathkids.presentation.localization.LocalAppLanguage.current == com.educa.mathkids.presentation.localization.AppLanguage.INGLES
    val uiState by viewModel.uiState.collectAsState()
    var showExitDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val activity = context as? Activity

    // ── Pulsación del botón Siguiente para atraer atención ──
    val infiniteTransition = rememberInfiniteTransition(label = "next_btn_pulse")
    val nextBtnPulse by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "next_pulse"
    )

    LaunchedEffect(category, difficulty, isEnglish) {
        viewModel.startSession(category, difficulty, isEnglish)
    }

    LaunchedEffect(uiState.isAnswerCorrect) {
        if (uiState.isAnswerCorrect == true) {
            com.educa.mathkids.core.shop.CoinShopManager.addCoin(context, 1)
        }
    }

    // Interceptar botón atrás físico/gestual del sistema durante la partida
    BackHandler(enabled = true) {
        showExitDialog = true
        try { ttsManager.speak(strings.exitProblemarioTitle) } catch (_: Throwable) {}
    }

    DynamicKidsBackground(modifier = modifier) {
        // Confeti cuando responde correctamente
        ConfettiCelebration(visible = uiState.isAnswerCorrect == true)

        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = Dimens.SpacingLarge, vertical = 6.dp)
                    .padding(bottom = 90.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // ============================================================
                // BARRA SUPERIOR: Categoría | Progreso | Voz
                // ============================================================
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Badge Categoría
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(category.colorHex).copy(alpha = 0.15f))
                            .border(1.5.dp, Color(category.colorHex).copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = category.iconEmoji, fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = strings.getCategoryTitle(category),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FredokaFont,
                            color = Color(category.colorHex)
                        )
                    }

                    // Contador de progreso central
                    val progressText = uiState.getProgressText(isEnglish)
                    AnimatedContent(
                        targetState = progressText,
                        transitionSpec = {
                            fadeIn(tween(300)) + slideInVertically { -it } togetherWith fadeOut(tween(200))
                        },
                        label = "progress_text"
                    ) { progText ->
                        Text(
                            text = progText,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FredokaFont,
                            color = SlateDark
                        )
                    }

                    // Botón de audio
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .shadow(3.dp, CircleShape)
                            .clip(CircleShape)
                            .background(SurfaceWhite)
                            .border(1.5.dp, OrangeSecondary.copy(alpha = 0.7f), CircleShape)
                            .clickable {
                                soundManager?.playButtonClick()
                                viewModel.speakCurrentStory(isEnglish)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "🔊", fontSize = 16.sp)
                    }
                }

                // ============================================================
                // BARRA DE PROGRESO VISUAL + VIDAS
                // ============================================================
                if (uiState.problems.isNotEmpty()) {
                    val progress = (uiState.currentIndex.toFloat() /
                        uiState.problems.size.toFloat()).coerceIn(0f, 1f)
                    val animatedProgress by animateFloatAsState(
                        targetValue = progress,
                        animationSpec = spring(stiffness = Spring.StiffnessLow),
                        label = "progress_bar"
                    )
                    LinearProgressIndicator(
                        progress = { animatedProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = Color(category.colorHex),
                        trackColor = Color(category.colorHex).copy(alpha = 0.15f),
                        strokeCap = StrokeCap.Round
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                }

                // Vidas (3 corazones)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFFFEBEE))
                        .border(1.dp, SoftRed.copy(alpha = 0.25f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${strings.livesLabel}:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FredokaFont,
                        color = SlateMuted
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        repeat(uiState.maxLives) { idx ->
                            val hasLife = idx < uiState.lives
                            // FIX: animación de pérdida de vida
                            val heartScale by animateFloatAsState(
                                targetValue = if (hasLife) 1f else 0.75f,
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessMedium
                                ),
                                label = "heart_$idx"
                            )
                            Text(
                                text = if (hasLife) "❤️" else "🖤",
                                fontSize = 20.sp,
                                modifier = Modifier.scale(heartScale)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                val currentProblem = uiState.currentProblem

                if (currentProblem != null && !uiState.isSessionFinished) {

                    // ============================================================
                    // TARJETA DE HISTORIA (Story) con animación al cambiar problema
                    // ============================================================
                    AnimatedContent(
                        targetState = currentProblem,
                        transitionSpec = {
                            fadeIn(tween(350)) + slideInVertically { it / 3 } togetherWith
                                fadeOut(tween(200))
                        },
                        label = "story_card"
                    ) { targetProblem ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .shadow(6.dp, RoundedCornerShape(20.dp), spotColor = Color.Black.copy(alpha = 0.08f))
                                .clip(RoundedCornerShape(20.dp))
                                .background(SurfaceWhite)
                                .border(2.dp, SlateBorder, RoundedCornerShape(20.dp))
                                .padding(14.dp)
                        ) {
                            Column {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "📖 ${targetProblem.getTitle(isEnglish)}",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Black,
                                        fontFamily = FredokaFont,
                                        color = Color(category.colorHex)
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = targetProblem.getStory(isEnglish),
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FredokaFont,
                                    color = SlateDark,
                                    lineHeight = 21.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // ============================================================
                    // REPRESENTACIÓN VISUAL PEDAGÓGICA
                    // ============================================================
                    ProblemVisualRepresentation(
                        problem = currentProblem,
                        showSolution = uiState.isAnswerCorrect == true
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // ============================================================
                    // BADGE DEL MODO DE RESOLUCIÓN
                    // ============================================================
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(category.colorHex).copy(alpha = 0.12f))
                            .border(1.dp, Color(category.colorHex).copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = currentProblem.solveMode.iconEmoji, fontSize = 13.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        val modeLabel = when (currentProblem.solveMode) {
                            ProblemSolveMode.SELECCION -> strings.modeSelection
                            ProblemSolveMode.COMPLETACION -> strings.modeCompletion
                            ProblemSolveMode.ESCRITA -> strings.modeWritten
                        }
                        Text(
                            text = modeLabel,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FredokaFont,
                            color = Color(category.colorHex)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // ============================================================
                    // PREGUNTA con animación al cambiar
                    // ============================================================
                    AnimatedContent(
                        targetState = currentProblem.getQuestion(isEnglish),
                        transitionSpec = {
                            fadeIn(tween(300)) togetherWith fadeOut(tween(150))
                        },
                        label = "question_card"
                    ) { question ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0xFFFFF8E1))
                                .border(1.5.dp, Color(0xFFFFD54F), RoundedCornerShape(16.dp))
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = question,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FredokaFont,
                                color = SlateDark,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // ============================================================
                    // RESOLUCIÓN SEGÚN EL MODO (SELECCIÓN / COMPLETACIÓN / ESCRITA)
                    // FIX: displayOptions siempre viene de shuffledOptions del estado,
                    // nunca se recalcula aquí para evitar cambio de posición en recomposición.
                    // ============================================================
                    val displayOptions = uiState.shuffledOptions.ifEmpty { currentProblem.options }

                    when (currentProblem.solveMode) {
                        ProblemSolveMode.SELECCION -> {
                            // MODO 1: SELECCIÓN SIMPLE (3 opciones con posición aleatoria)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                displayOptions.forEachIndexed { idx, optionValue ->
                                    val isSelected = uiState.selectedAnswer == optionValue
                                    val isAnswered = uiState.selectedAnswer != null
                                    val isThisCorrect = optionValue == currentProblem.correctAnswer

                                    val btnColor = when {
                                        isSelected && uiState.isAnswerCorrect == true -> GreenPrimary
                                        isSelected && uiState.isAnswerCorrect == false -> SoftRed
                                        isAnswered && isThisCorrect -> GreenPrimary
                                        else -> SurfaceWhite
                                    }

                                    val textColor = when {
                                        isSelected || (isAnswered && isThisCorrect) -> Color.White
                                        else -> SlateDark
                                    }

                                    val scale by animateFloatAsState(
                                        targetValue = when {
                                            isSelected && uiState.isAnswerCorrect == true -> 1.08f
                                            isSelected && uiState.isAnswerCorrect == false -> 0.96f
                                            isSelected -> 1.05f
                                            else -> 1.0f
                                        },
                                        animationSpec = spring(
                                            dampingRatio = Spring.DampingRatioMediumBouncy,
                                            stiffness = Spring.StiffnessMediumLow
                                        ),
                                        label = "btn_scale_$idx"
                                    )

                                    // FIX: Animación de entrada escalonada para cada opción
                                    var visible by remember { mutableStateOf(false) }
                                    LaunchedEffect(currentProblem.id) {
                                        kotlinx.coroutines.delay(80L * idx)
                                        visible = true
                                    }
                                    AnimatedVisibility(
                                        visible = visible,
                                        enter = fadeIn(tween(300)) + scaleIn(
                                            initialScale = 0.85f,
                                            animationSpec = spring(
                                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                                stiffness = Spring.StiffnessMedium
                                            )
                                        ),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .scale(scale)
                                                .shadow(6.dp, RoundedCornerShape(20.dp), spotColor = btnColor.copy(alpha = 0.3f))
                                                .clip(RoundedCornerShape(20.dp))
                                                .background(btnColor)
                                                .border(
                                                    width = if (isSelected) 3.dp else 2.dp,
                                                    color = if (isSelected || (isAnswered && isThisCorrect)) btnColor
                                                            else Color(category.colorHex).copy(alpha = 0.4f),
                                                    shape = RoundedCornerShape(20.dp)
                                                )
                                                .clickable(enabled = !isAnswered && !uiState.isOutOfLives) {
                                                    viewModel.submitAnswer(optionValue, isEnglish)
                                                }
                                                .padding(vertical = 14.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "$optionValue",
                                                fontSize = 28.sp,
                                                fontWeight = FontWeight.Black,
                                                fontFamily = FredokaFont,
                                                color = textColor
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        ProblemSolveMode.COMPLETACION -> {
                            // MODO 2: COMPLETACIÓN (Ecuación con casilla faltante interactiva)
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(18.dp))
                                    .background(SurfaceWhite)
                                    .border(2.dp, Color(category.colorHex).copy(alpha = 0.35f), RoundedCornerShape(18.dp))
                                    .padding(vertical = 10.dp, horizontal = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = "${currentProblem.num1} ${category.symbol} ${currentProblem.num2} = ",
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Black,
                                        fontFamily = FredokaFont,
                                        color = SlateDark
                                    )
                                    Box(
                                        modifier = Modifier
                                            .size(width = 56.dp, height = 46.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(
                                                when {
                                                    uiState.isAnswerCorrect == true -> GreenPrimary.copy(alpha = 0.15f)
                                                    uiState.isAnswerCorrect == false -> SoftRed.copy(alpha = 0.15f)
                                                    uiState.selectedAnswer != null -> Color(category.colorHex).copy(alpha = 0.15f)
                                                    else -> Color(0xFFFFF3E0)
                                                }
                                            )
                                            .border(
                                                2.dp,
                                                when {
                                                    uiState.isAnswerCorrect == true -> GreenPrimary
                                                    uiState.isAnswerCorrect == false -> SoftRed
                                                    uiState.selectedAnswer != null -> Color(category.colorHex)
                                                    else -> OrangeSecondary
                                                },
                                                RoundedCornerShape(12.dp)
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        // FIX: AnimatedContent para que el número aparezca animado
                                        AnimatedContent(
                                            targetState = uiState.selectedAnswer,
                                            transitionSpec = {
                                                (fadeIn(tween(200)) + scaleIn(initialScale = 0.7f)) togetherWith
                                                    (fadeOut(tween(100)) + scaleOut(targetScale = 1.2f))
                                            },
                                            label = "completion_answer"
                                        ) { answer ->
                                            Text(
                                                text = if (answer != null) "$answer" else "❓",
                                                fontSize = 22.sp,
                                                fontWeight = FontWeight.Black,
                                                fontFamily = FredokaFont,
                                                color = when {
                                                    uiState.isAnswerCorrect == true -> GreenPrimary
                                                    uiState.isAnswerCorrect == false -> SoftRed
                                                    answer != null -> SlateDark
                                                    else -> OrangeSecondary
                                                }
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = strings.promptCompletion,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FredokaFont,
                                color = SlateMuted
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                displayOptions.forEachIndexed { idx, optionValue ->
                                    val isSelected = uiState.selectedAnswer == optionValue
                                    val isAnswered = uiState.selectedAnswer != null
                                    val isThisCorrect = optionValue == currentProblem.correctAnswer

                                    val btnColor = when {
                                        isSelected && uiState.isAnswerCorrect == true -> GreenPrimary
                                        isSelected && uiState.isAnswerCorrect == false -> SoftRed
                                        isAnswered && isThisCorrect -> GreenPrimary
                                        else -> SurfaceWhite
                                    }

                                    val textColor = when {
                                        isSelected || (isAnswered && isThisCorrect) -> Color.White
                                        else -> SlateDark
                                    }

                                    val scale by animateFloatAsState(
                                        targetValue = when {
                                            isSelected && uiState.isAnswerCorrect == true -> 1.08f
                                            isSelected && uiState.isAnswerCorrect == false -> 0.95f
                                            isSelected -> 1.05f
                                            else -> 1.0f
                                        },
                                        animationSpec = spring(
                                            dampingRatio = Spring.DampingRatioMediumBouncy,
                                            stiffness = Spring.StiffnessMediumLow
                                        ),
                                        label = "comp_btn_scale_$idx"
                                    )

                                    var visible by remember { mutableStateOf(false) }
                                    LaunchedEffect(currentProblem.id) {
                                        kotlinx.coroutines.delay(80L * idx)
                                        visible = true
                                    }
                                    AnimatedVisibility(
                                        visible = visible,
                                        enter = fadeIn(tween(300)) + scaleIn(
                                            initialScale = 0.85f,
                                            animationSpec = spring(
                                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                                stiffness = Spring.StiffnessMedium
                                            )
                                        ),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .scale(scale)
                                                .shadow(5.dp, RoundedCornerShape(18.dp), spotColor = btnColor.copy(alpha = 0.25f))
                                                .clip(RoundedCornerShape(18.dp))
                                                .background(btnColor)
                                                .border(
                                                    width = if (isSelected) 3.dp else 2.dp,
                                                    color = if (isSelected || (isAnswered && isThisCorrect)) btnColor
                                                            else Color(category.colorHex).copy(alpha = 0.4f),
                                                    shape = RoundedCornerShape(18.dp)
                                                )
                                                .clickable(enabled = !isAnswered && !uiState.isOutOfLives) {
                                                    viewModel.submitAnswer(optionValue, isEnglish)
                                                }
                                                .padding(vertical = 12.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "$optionValue",
                                                fontSize = 26.sp,
                                                fontWeight = FontWeight.Black,
                                                fontFamily = FredokaFont,
                                                color = textColor
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        ProblemSolveMode.ESCRITA -> {
                            // MODO 3: COMPLETACIÓN ESCRITA (Ecuación con teclado numérico)
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(18.dp))
                                    .background(SurfaceWhite)
                                    .border(2.dp, Color(category.colorHex).copy(alpha = 0.35f), RoundedCornerShape(18.dp))
                                    .padding(vertical = 10.dp, horizontal = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = "${currentProblem.num1} ${category.symbol} ${currentProblem.num2} = ",
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Black,
                                        fontFamily = FredokaFont,
                                        color = SlateDark
                                    )
                                    Box(
                                        modifier = Modifier
                                            .size(width = 64.dp, height = 46.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(
                                                when {
                                                    uiState.isAnswerCorrect == true -> GreenPrimary.copy(alpha = 0.15f)
                                                    uiState.isAnswerCorrect == false -> SoftRed.copy(alpha = 0.15f)
                                                    uiState.keypadInput.isNotEmpty() -> Color(category.colorHex).copy(alpha = 0.12f)
                                                    else -> Color(0xFFF1F5F9)
                                                }
                                            )
                                            .border(
                                                2.dp,
                                                when {
                                                    uiState.isAnswerCorrect == true -> GreenPrimary
                                                    uiState.isAnswerCorrect == false -> SoftRed
                                                    uiState.keypadInput.isNotEmpty() -> Color(category.colorHex)
                                                    else -> Color(0xFFCBD5E1)
                                                },
                                                RoundedCornerShape(12.dp)
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        // FIX: AnimatedContent para mostrar cada dígito con animación
                                        AnimatedContent(
                                            targetState = when {
                                                uiState.keypadInput.isNotEmpty() -> uiState.keypadInput
                                                uiState.selectedAnswer != null -> "${uiState.selectedAnswer}"
                                                else -> "?"
                                            },
                                            transitionSpec = {
                                                (fadeIn(tween(150)) + slideInVertically { -it }) togetherWith
                                                    (fadeOut(tween(100)))
                                            },
                                            label = "keypad_display"
                                        ) { displayText ->
                                            Text(
                                                text = displayText,
                                                fontSize = 24.sp,
                                                fontWeight = FontWeight.Black,
                                                fontFamily = FredokaFont,
                                                color = when {
                                                    uiState.isAnswerCorrect == true -> GreenPrimary
                                                    uiState.isAnswerCorrect == false -> SoftRed
                                                    uiState.keypadInput.isNotEmpty() -> SlateDark
                                                    else -> SlateMuted
                                                }
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = strings.promptWritten,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FredokaFont,
                                color = SlateMuted
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            KeypadView(
                                currentInput = uiState.keypadInput,
                                onDigitPress = { viewModel.onKeypadDigit(it) },
                                onBackspacePress = { viewModel.onKeypadBackspace() },
                                onSubmitPress = { viewModel.onKeypadSubmit(isEnglish) },
                                accentColor = Color(category.colorHex),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // ============================================================
                    // RETROALIMENTACIÓN PEDAGÓGICA CON MASCOTA
                    // FIX: AnimatedVisibility usa la condición correcta como `visible`
                    // para que la animación de entrada realmente se ejecute.
                    // ============================================================
                    val showFeedback = uiState.isAnswerCorrect != null || uiState.isOutOfLives
                    AnimatedVisibility(
                        visible = showFeedback,
                        enter = fadeIn(tween(350)) + scaleIn(
                            initialScale = 0.88f,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessMedium
                            )
                        ),
                        exit = fadeOut(tween(200)) + scaleOut(targetScale = 0.9f)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .shadow(8.dp, RoundedCornerShape(20.dp))
                                .clip(RoundedCornerShape(20.dp))
                                .background(
                                    when {
                                        uiState.isAnswerCorrect == true -> Color(0xFFE8F5E9)
                                        uiState.isOutOfLives -> Color(0xFFFFEBEE)
                                        else -> Color(0xFFFFF3E0)
                                    }
                                )
                                .border(
                                    2.dp,
                                    when {
                                        uiState.isAnswerCorrect == true -> GreenPrimary
                                        uiState.isOutOfLives -> SoftRed
                                        else -> OrangeSecondary
                                    },
                                    RoundedCornerShape(20.dp)
                                )
                                .padding(14.dp)
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Mascot(
                                        mood = uiState.mascotMood,
                                        size = 60.dp,
                                        onClick = { viewModel.speakCurrentStory(isEnglish) }
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = when {
                                                uiState.isAnswerCorrect == true -> strings.feedbackAwesome
                                                uiState.isOutOfLives -> strings.feedbackOutOfLives
                                                else -> strings.feedbackAlmost
                                            },
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Black,
                                            fontFamily = FredokaFont,
                                            color = when {
                                                uiState.isAnswerCorrect == true -> GreenPrimary
                                                uiState.isOutOfLives -> SoftRed
                                                else -> OrangeSecondaryDark
                                            }
                                        )
                                        Text(
                                            text = when {
                                                uiState.isAnswerCorrect == true ->
                                                    strings.starEarnedFeedback(currentProblem.equationText)
                                                uiState.isOutOfLives ->
                                                    strings.theAnswerWasFeedback(currentProblem.correctAnswer)
                                                else ->
                                                    // FIX: Mostrar mensaje de mascota con pista útil
                                                    uiState.mascotMessage
                                            },
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = FredokaFont,
                                            color = SlateDark
                                        )
                                    }
                                }

                                // FIX: Mostrar la operación correcta como pista cuando hay error
                                if (uiState.isAnswerCorrect == false && !uiState.isOutOfLives) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(OrangeSecondary.copy(alpha = 0.12f))
                                            .border(1.dp, OrangeSecondary.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                                            .padding(horizontal = 10.dp, vertical = 5.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Text(text = "💡", fontSize = 14.sp)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "${if (isEnglish) "Hint" else "Pista"}: ${currentProblem.num1} ${category.symbol} ${currentProblem.num2}",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Black,
                                            fontFamily = FredokaFont,
                                            color = OrangeSecondaryDark
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // ── Botón de acción (Siguiente / Reintentar / Restaurar vidas) ──
                                // FIX: Código deduplicado en un helper composable interno
                                when {
                                    uiState.isAnswerCorrect == true -> {
                                        // Siguiente problema con animación de pulso
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .scale(nextBtnPulse)
                                                .shadow(6.dp, RoundedCornerShape(14.dp))
                                                .clip(RoundedCornerShape(14.dp))
                                                .background(GreenPrimary)
                                                .clickable {
                                                    soundManager?.playButtonClick()
                                                    viewModel.nextProblem(isEnglish)
                                                }
                                                .padding(vertical = 12.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = strings.btnNextProblem,
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Black,
                                                fontFamily = FredokaFont,
                                                color = Color.White
                                            )
                                        }
                                    }
                                    uiState.isOutOfLives -> {
                                        // Sin vidas: reintentar con vidas restauradas
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .shadow(4.dp, RoundedCornerShape(14.dp))
                                                .clip(RoundedCornerShape(14.dp))
                                                .background(SoftRed)
                                                .clickable {
                                                    soundManager?.playButtonClick()
                                                    if (activity != null) {
                                                        AdManager.getInstance(context).showInterstitial(activity) {
                                                            viewModel.restoreLives(isEnglish)
                                                        }
                                                    } else {
                                                        viewModel.restoreLives(isEnglish)
                                                    }
                                                }
                                                .padding(vertical = 12.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = strings.btnTryAgain,
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.Black,
                                                fontFamily = FredokaFont,
                                                color = Color.White
                                            )
                                        }
                                    }
                                    else -> {
                                        // Respuesta incorrecta, aún tiene vidas
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .shadow(4.dp, RoundedCornerShape(14.dp))
                                                .clip(RoundedCornerShape(14.dp))
                                                .background(OrangeSecondary)
                                                .clickable {
                                                    soundManager?.playButtonClick()
                                                    viewModel.retryCurrentProblem(isEnglish)
                                                }
                                                .padding(vertical = 12.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = strings.btnTryAgain,
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.Black,
                                                fontFamily = FredokaFont,
                                                color = Color.White
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else if (uiState.isSessionFinished) {
                    // ============================================================
                    // PANTALLA DE FELICITACIÓN FINAL
                    // ============================================================
                    Spacer(modifier = Modifier.height(20.dp))
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn(tween(400)) + scaleIn(
                            initialScale = 0.8f,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessLow
                            )
                        )
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .shadow(12.dp, RoundedCornerShape(26.dp))
                                .clip(RoundedCornerShape(26.dp))
                                .background(SurfaceWhite)
                                .border(2.5.dp, GreenPrimary.copy(alpha = 0.6f), RoundedCornerShape(26.dp))
                                .padding(20.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Mascot(
                                    mood = MascotMood.CELEBRATING,
                                    size = 110.dp,
                                    onClick = { soundManager?.playButtonClick() }
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = strings.problemCompletedTitle,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Black,
                                    fontFamily = FredokaFont,
                                    color = GreenPrimary,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = strings.problemCompletedMsg(strings.getCategoryTitle(category), uiState.starsEarned),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FredokaFont,
                                    color = SlateDark,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(16.dp))

                                // Estrellas ganadas
                                Row(
                                    horizontalArrangement = Arrangement.Center,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    repeat(uiState.starsEarned.coerceAtMost(5)) {
                                        Text(text = "⭐", fontSize = 28.sp)
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(OrangeSecondary)
                                        .clickable {
                                            soundManager?.playButtonClick()
                                            onBackClick()
                                        }
                                        .padding(vertical = 12.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = strings.btnChooseAnotherAdventure,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Black,
                                        fontFamily = FredokaFont,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Barra de navegación inferior (INICIO | ATRÁS | JUEGOS | SALIDA=cierra app)
            KidsGameNavDock(
                onInicioClick = onInicioClick,
                onAtrasClick = onBackClick,
                onNivelesClick = onJuegosClick,
                onSalidaClick = {
                    soundManager?.playButtonClick()
                    // SALIDA cierra la app
                    activity?.finishAffinity()
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            )

            // Diálogo de Confirmación para Salir
            if (showExitDialog) {
                KidsExitDialog(
                    title = strings.exitProblemarioTitle,
                    message = strings.exitProblemarioMsg,
                    confirmButtonText = strings.exitConfirm,
                    dismissButtonText = strings.exitDismiss,
                    ttsManager = ttsManager,
                    onConfirm = {
                        showExitDialog = false
                        onBackClick()
                    },
                    onDismiss = {
                        showExitDialog = false
                    }
                )
            }
        }
    }
}
