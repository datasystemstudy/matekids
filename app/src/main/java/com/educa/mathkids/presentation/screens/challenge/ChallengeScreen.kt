package com.educa.mathkids.presentation.screens.challenge

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import android.app.Activity
import androidx.compose.ui.platform.LocalContext
import com.educa.mathkids.presentation.ads.AdManager
import com.educa.mathkids.presentation.components.OutOfLivesDialog
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.educa.mathkids.R
import com.educa.mathkids.presentation.components.AnimatedGameButton
import com.educa.mathkids.presentation.components.AnswerButton
import com.educa.mathkids.presentation.components.AnswerButtonState
import com.educa.mathkids.presentation.components.ConfettiCelebration
import com.educa.mathkids.presentation.components.DynamicKidsBackground
import com.educa.mathkids.presentation.components.KeypadView
import com.educa.mathkids.presentation.components.KidCard
import com.educa.mathkids.presentation.components.ScreenHeader
import com.educa.mathkids.presentation.components.SecondaryGameButton
import com.educa.mathkids.presentation.components.VisualRepresentationView
import com.educa.mathkids.presentation.theme.Dimens
import com.educa.mathkids.presentation.theme.FredokaFont
import com.educa.mathkids.presentation.theme.GreenPrimary
import com.educa.mathkids.presentation.theme.GreenPrimaryDark
import com.educa.mathkids.presentation.theme.OrangeSecondary
import com.educa.mathkids.presentation.theme.OrangeSecondaryDark
import com.educa.mathkids.presentation.theme.SlateBorder
import com.educa.mathkids.presentation.theme.SlateDark
import com.educa.mathkids.presentation.theme.SlateMuted
import com.educa.mathkids.presentation.theme.SoftRed
import com.educa.mathkids.presentation.theme.SurfaceWhite
import com.educa.mathkids.presentation.theme.YellowStar

@Composable
fun ChallengeScreen(
    viewModel: ChallengeViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    adManager: AdManager? = null
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = androidx.compose.ui.platform.LocalContext.current

    LaunchedEffect(uiState.isCorrect) {
        if (uiState.isCorrect == true) {
            com.educa.mathkids.core.shop.CoinShopManager.addCoin(context, 1)
        }
    }

    DynamicKidsBackground(modifier = modifier) {
        if (uiState.isChallengeFinished) {
            ChallengeResultView(
                uiState = uiState,
                onRetry = { viewModel.retryChallenge() },
                onBack = onBackClick
            )
        } else {
            ChallengePlayingView(
                uiState = uiState,
                onAnswerClick = { viewModel.onAnswerSelected(it) },
                onKeypadDigit = { viewModel.onKeypadDigit(it) },
                onKeypadBackspace = { viewModel.onKeypadBackspace() },
                onKeypadSubmit = { viewModel.onKeypadSubmit() },
                onBackClick = onBackClick
            )
        }

        // Diálogo de 3 vidas agotadas (Ver Anuncio o Salir)
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
    }
}

@Composable
private fun ChallengePlayingView(
    uiState: ChallengeUiState,
    onAnswerClick: (Int) -> Unit,
    onKeypadDigit: (Char) -> Unit,
    onKeypadBackspace: () -> Unit,
    onKeypadSubmit: () -> Unit,
    onBackClick: () -> Unit
) {
    val strings = com.educa.mathkids.presentation.localization.appStrings()
    val exercise = uiState.currentExercise
    val opColor = if (exercise != null) Color(exercise.operationType.colorHex) else OrangeSecondary
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = Dimens.SpacingLarge, vertical = 6.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ScreenHeader(
            title = "🏆 ${strings.challengeTitle}",
            onBackClick = onBackClick
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Fila de Puntuación, Nivel y Vidas (3 Vidas)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Puntos
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(OrangeSecondary.copy(alpha = 0.12f))
                    .padding(horizontal = 9.dp, vertical = 5.dp)
            ) {
                Text(
                    text = "🏆 ${uiState.score}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    color = OrangeSecondaryDark
                )
            }

            // Indicador de Nivel 1..35
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFEDE7F6))
                    .border(1.dp, Color(0xFF673AB7).copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Text(
                    text = "${strings.levelLabel} ${uiState.levelNumber} / ${uiState.totalQuestions}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF512DA8)
                )
            }

            // Vidas (3 Vidas)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFFFEBEE))
                    .border(1.dp, Color(0xFFFFCDD2), RoundedCornerShape(12.dp))
                    .padding(horizontal = 7.dp, vertical = 4.dp)
            ) {
                repeat(uiState.maxLives) { idx ->
                    val hasLife = idx < uiState.lives
                    Text(
                        text = if (hasLife) "❤️" else "🖤",
                        fontSize = 14.sp,
                        modifier = Modifier.padding(horizontal = 1.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Barra del temporizador de 15 segundos
        val timerColor = if (uiState.timeLeftSeconds <= 4) SoftRed else OrangeSecondary
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "⏱️ ${strings.timeLabel} (15s)",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = SlateMuted
                )
                Text(
                    text = "${uiState.timeLeftSeconds}s",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    color = timerColor
                )
            }
            Spacer(modifier = Modifier.height(3.dp))
            LinearProgressIndicator(
                progress = { uiState.timeFraction },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = timerColor,
                trackColor = Color(0xFFE2E8F0)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Badge de Modalidad (Selección, Completación, Escritura)
        val modeBadgeText = when (uiState.currentModalMode) {
            ChallengeModalMode.SELECTION -> strings.challengeModeSelection
            ChallengeModalMode.COMPLETION -> strings.challengeModeCompletion
            ChallengeModalMode.WRITTEN -> strings.challengeModeWritten
        }
        val modeBadgeColor = when (uiState.currentModalMode) {
            ChallengeModalMode.SELECTION -> Color(0xFF0288D1)
            ChallengeModalMode.COMPLETION -> Color(0xFF7B1FA2)
            ChallengeModalMode.WRITTEN -> Color(0xFF2E7D32)
        }

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(modeBadgeColor.copy(alpha = 0.12f))
                .border(1.dp, modeBadgeColor.copy(alpha = 0.35f), RoundedCornerShape(10.dp))
                .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Text(
                text = modeBadgeText,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = modeBadgeColor
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (uiState.isProblemarioRound && uiState.currentProblemario != null) {
                // ======================================================
                // RONDA DE PROBLEMARIO — Tarjeta de Historia
                // ======================================================
                val prob = uiState.currentProblemario
                val catColor = Color(0xFF7B1FA2) // Morado para Problemario

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(Color(0xFFF3E5F5))
                        .border(2.dp, catColor.copy(alpha = 0.55f), RoundedCornerShape(18.dp))
                        .padding(14.dp)
                ) {
                    Column {
                        Text(
                            text = "📚 Historia: ${prob.title}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = catColor,
                            fontFamily = FredokaFont
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = prob.story,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = SlateDark,
                            fontFamily = FredokaFont,
                            lineHeight = 20.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Pregunta
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFFFFF8E1))
                        .border(1.5.dp, Color(0xFFFFD54F), RoundedCornerShape(14.dp))
                        .padding(10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = prob.question,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FredokaFont,
                        color = SlateDark,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Opciones barajadas del Problemario
                val probRows = uiState.problemarioOptions.chunked(2)
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    probRows.forEach { rowOpts ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            rowOpts.forEach { option ->
                                val buttonState = when {
                                    !uiState.isAnswerSubmitted -> AnswerButtonState.DEFAULT
                                    option == uiState.selectedAnswer && uiState.isCorrect == true -> AnswerButtonState.SELECTED_CORRECT
                                    option == uiState.selectedAnswer && uiState.isCorrect == false -> AnswerButtonState.SELECTED_INCORRECT
                                    option == prob.correctAnswer && uiState.isCorrect == false -> AnswerButtonState.HIGHLIGHT_CORRECT
                                    else -> AnswerButtonState.DISABLED
                                }
                                AnswerButton(
                                    text = "$option",
                                    onClick = { onAnswerClick(option) },
                                    state = buttonState,
                                    enabled = !uiState.isAnswerSubmitted,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            if (rowOpts.size == 1) Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }

        } else if (exercise != null) {
            // Ecuación central según la modalidad
            val equationDisplay = when (uiState.currentModalMode) {
                ChallengeModalMode.SELECTION ->
                    "${exercise.operand1} ${exercise.operationType.symbol} ${exercise.operand2} = ?"

                ChallengeModalMode.COMPLETION ->
                    if (uiState.missingOperandIndex == 0)
                        "? ${exercise.operationType.symbol} ${exercise.operand2} = ${exercise.correctAnswer}"
                    else
                        "${exercise.operand1} ${exercise.operationType.symbol} ? = ${exercise.correctAnswer}"

                ChallengeModalMode.WRITTEN ->
                    "${exercise.operand1} ${exercise.operationType.symbol} ${exercise.operand2} = " +
                            (if (uiState.keypadInput.isEmpty()) "¿?" else uiState.keypadInput)
            }

            KidCard(
                modifier = Modifier.fillMaxWidth(),
                cornerRadius = 20.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp, horizontal = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = equationDisplay,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FredokaFont,
                        color = opColor,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Apoyo visual
            VisualRepresentationView(
                visual = exercise.visualRepresentation,
                accentColor = opColor,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Entrada según modalidad
            when (uiState.currentModalMode) {
                ChallengeModalMode.SELECTION -> {
                    // 4 Opciones de selección
                    val options = exercise.options
                    val optionRows = options.chunked(2)
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        optionRows.forEach { rowOptions ->
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                rowOptions.forEach { option ->
                                    val buttonState = when {
                                        !uiState.isAnswerSubmitted -> AnswerButtonState.DEFAULT
                                        option == uiState.selectedAnswer && uiState.isCorrect == true -> AnswerButtonState.SELECTED_CORRECT
                                        option == uiState.selectedAnswer && uiState.isCorrect == false -> AnswerButtonState.SELECTED_INCORRECT
                                        option == exercise.correctAnswer && uiState.isCorrect == false -> AnswerButtonState.HIGHLIGHT_CORRECT
                                        else -> AnswerButtonState.DISABLED
                                    }

                                    AnswerButton(
                                        text = "$option",
                                        onClick = { onAnswerClick(option) },
                                        state = buttonState,
                                        enabled = !uiState.isAnswerSubmitted,
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

                ChallengeModalMode.COMPLETION -> {
                    // Opciones para completar el operando faltante
                    val options = uiState.completionOptions
                    val optionRows = options.chunked(2)
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        optionRows.forEach { rowOptions ->
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                rowOptions.forEach { option ->
                                    val buttonState = when {
                                        !uiState.isAnswerSubmitted -> AnswerButtonState.DEFAULT
                                        option == uiState.selectedAnswer && uiState.isCorrect == true -> AnswerButtonState.SELECTED_CORRECT
                                        option == uiState.selectedAnswer && uiState.isCorrect == false -> AnswerButtonState.SELECTED_INCORRECT
                                        option == uiState.completionTargetAnswer && uiState.isCorrect == false -> AnswerButtonState.HIGHLIGHT_CORRECT
                                        else -> AnswerButtonState.DISABLED
                                    }

                                    AnswerButton(
                                        text = "$option",
                                        onClick = { onAnswerClick(option) },
                                        state = buttonState,
                                        enabled = !uiState.isAnswerSubmitted,
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

                ChallengeModalMode.WRITTEN -> {
                    // Teclado numérico táctil
                    if (!uiState.isAnswerSubmitted) {
                        KeypadView(
                            currentInput = uiState.keypadInput,
                            onDigitPress = onKeypadDigit,
                            onBackspacePress = onKeypadBackspace,
                            onSubmitPress = onKeypadSubmit,
                            accentColor = opColor,
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        // Estado post-respuesta escrita
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(
                                    if (uiState.isCorrect == true) GreenPrimary.copy(alpha = 0.2f)
                                    else SoftRed.copy(alpha = 0.2f)
                                )
                                .border(
                                    2.dp,
                                    if (uiState.isCorrect == true) GreenPrimary else SoftRed,
                                    RoundedCornerShape(16.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (uiState.isCorrect == true) "¡Correcto! 🎉" else "Respuesta: ${exercise.correctAnswer}",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = if (uiState.isCorrect == true) GreenPrimaryDark else SoftRed
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ChallengeResultView(
    uiState: ChallengeUiState,
    onRetry: () -> Unit,
    onBack: () -> Unit
) {
    // Confeti visible si logró un buen resultado
    ConfettiCelebration(visible = uiState.stars >= 2 || uiState.isMasteryAchieved)

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = Dimens.SpacingLarge, vertical = 12.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Tarjeta de Trofeo y Reconocimiento Final
        KidCard(
            modifier = Modifier.fillMaxWidth(),
            cornerRadius = 28.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // TROFEO 3D
                Image(
                    painter = painterResource(id = R.drawable.ic_trophy_3d),
                    contentDescription = "Trofeo del Desafío",
                    modifier = Modifier
                        .size(130.dp)
                        .clip(RoundedCornerShape(20.dp))
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Palabras solicitadas explícitamente:
                // "al completar el desafio final que aparesca un trofeo y unas palabras de felicitaciones ya aprendiste a calcular"
                Text(
                    text = "🏆 ¡FELICITACIONES! 🏆",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FredokaFont,
                    color = Color(0xFFB78103),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "¡Ya aprendiste a calcular!",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FredokaFont,
                    color = Color(0xFFE65100),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Insignia de Victoria y Monedas
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFFFFF3E0))
                        .border(1.5.dp, Color(0xFFFFB74D), RoundedCornerShape(14.dp))
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "🏅 ¡Completado con Éxito! +50 🪙",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FredokaFont,
                        color = Color(0xFFE65100)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Estadísticas detalladas de los 35 niveles
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(Color(0xFFF8FAFC))
                        .border(1.dp, SlateBorder, RoundedCornerShape(18.dp))
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ResultRowItem(label = "Niveles superados", value = "${uiState.correctCount} de ${uiState.totalQuestions}")
                    ResultRowItem(label = "Puntos obtenidos", value = "${uiState.score} pts")
                    ResultRowItem(label = "Racha máxima", value = "${uiState.maxStreak} 🔥")
                    ResultRowItem(label = "Multiplicador récord", value = "×${uiState.multiplier}")
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        val strings = com.educa.mathkids.presentation.localization.appStrings()
        val isEnglish = strings is com.educa.mathkids.presentation.localization.EnglishStrings

        AnimatedGameButton(
            text = if (isEnglish) "Play Again 🔄" else "Jugar de Nuevo 🔄",
            onClick = onRetry,
            primaryColor = OrangeSecondary,
            darkColor = OrangeSecondaryDark,
            height = 50.dp,
            fontSize = 18.sp,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        SecondaryGameButton(
            text = if (isEnglish) "Back to Home 🏠" else "Volver al Inicio 🏠",
            onClick = onBack,
            backgroundColor = SurfaceWhite,
            borderColor = SlateBorder,
            textColor = SlateDark,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun ResultRowItem(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = SlateMuted)
        Text(text = value, fontSize = 15.sp, fontWeight = FontWeight.Black, color = SlateDark)
    }
}
