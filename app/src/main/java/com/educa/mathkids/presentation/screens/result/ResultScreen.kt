package com.educa.mathkids.presentation.screens.result

import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import com.educa.mathkids.R
import com.educa.mathkids.domain.model.OperationType
import com.educa.mathkids.presentation.theme.FredokaFont
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.app.Activity
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import kotlinx.coroutines.flow.MutableStateFlow
import com.educa.mathkids.presentation.ads.AdManager
import com.educa.mathkids.presentation.ads.BannerAdView
import com.educa.mathkids.presentation.theme.OrangeSecondaryDark
import com.educa.mathkids.domain.model.OperationLevelConfig
import com.educa.mathkids.presentation.components.AnimatedGameButton
import com.educa.mathkids.presentation.components.ConfettiCelebration
import com.educa.mathkids.presentation.components.KidCard
import com.educa.mathkids.presentation.components.Mascot
import com.educa.mathkids.presentation.components.MascotMood
import com.educa.mathkids.presentation.theme.AnimationConfig
import com.educa.mathkids.presentation.theme.BackgroundCream
import com.educa.mathkids.presentation.theme.CoralEncouragement
import com.educa.mathkids.presentation.theme.Dimens
import com.educa.mathkids.presentation.theme.GreenPrimary
import com.educa.mathkids.presentation.theme.GreenPrimaryDark
import com.educa.mathkids.presentation.theme.LocalAnimationsEnabled
import com.educa.mathkids.presentation.theme.OrangeSecondary
import com.educa.mathkids.presentation.theme.SlateDark
import com.educa.mathkids.presentation.theme.SlateMuted
import com.educa.mathkids.presentation.theme.TextDark
import com.educa.mathkids.presentation.theme.TextMuted

/**
 * Pantalla de resultados de la sesión de ejercicios con experiencia infantil (Fase 9).
 * Incluye conteo animado del porcentaje (0% → X%), confeti festivo,
 * mascota en celebración y botones con microinteracciones.
 */
@Composable
fun ResultScreen(
    viewModel: ResultViewModel,
    onPlayAgainClick: () -> Unit,
    onHomeClick: () -> Unit,
    modifier: Modifier = Modifier,
    onNextLevelClick: (() -> Unit)? = null,
    adManager: AdManager? = null,
    onRewardEarned: ((Int) -> Unit)? = null
) {
    val state = viewModel.uiState
    val animationsEnabled = LocalAnimationsEnabled.current
    val opColor = Color(state.operationType.colorHex)
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    var rewardClaimed by remember { mutableStateOf(false) }
    var isNavigating by remember { mutableStateOf(false) }
    val isRewardedLoaded by (adManager?.isRewardedLoaded ?: MutableStateFlow(false)).collectAsState()

    val mascotMood = if (state.accuracyPercentage >= 70) {
        MascotMood.CELEBRATING
    } else {
        MascotMood.HAPPY
    }

    // Conteo animado suave del porcentaje de aciertos (0% -> X%)
    val animatedPercentage by animateIntAsState(
        targetValue = state.accuracyPercentage,
        animationSpec = if (animationsEnabled) {
            tween(durationMillis = AnimationConfig.COUNT_UP)
        } else {
            tween(0)
        },
        label = "percentage_count_up"
    )

    val isLevel50Completed = state.levelNumber >= 50 && (state.accuracyPercentage >= 50 || state.correctCount >= state.totalExercises / 2)
    val level50PraiseText = when (state.operationType) {
        OperationType.ADDITION -> "¡Ya sabes sumar!"
        OperationType.SUBTRACTION -> "¡Ya sabes restar!"
        OperationType.MULTIPLICATION -> "¡Ya sabes multiplicar!"
        OperationType.DIVISION -> "¡Ya sabes dividir!"
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundCream)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(Dimens.SpacingLarge)
    ) {
        // Confeti de celebración si obtuvo un buen resultado (>= 70%) o si completó el Nivel 50
        ConfettiCelebration(visible = state.accuracyPercentage >= 70 || isLevel50Completed)

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Contenido superior con scroll para adaptarse a cualquier tamaño de pantalla
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(4.dp))

                // Encabezado
                Text(
                    text = if (isLevel50Completed) "🏆 ¡MAESTRÍA TOTAL LOGRADA! 🏆" else "🎉 ¡Sesión completada!",
                    fontSize = if (isLevel50Completed) 22.sp else 26.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (isLevel50Completed) Color(0xFFE65100) else GreenPrimaryDark,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = if (isLevel50Completed) "¡Felicitaciones! $level50PraiseText" else state.celebrationMessage,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = SlateDark,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp)
                )

                // Mascota en estado de celebración (tamaño optimizado para evitar desbordamiento)
                Mascot(
                    size = 80.dp,
                    mood = mascotMood
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Tarjeta Principal de Resultados
                KidCard(
                    modifier = Modifier.fillMaxWidth(),
                    cornerRadius = 24.dp,
                    elevation = 6.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Badge de Operación y Dificultad
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(opColor.copy(alpha = 0.15f))
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "${state.operationType.symbol} ${state.operationType.displayName}",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = opColor
                                )
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color(0xFFFFF3E0))
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "${state.difficulty.title}",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFE65100)
                                )
                            }
                        }

                        // Insignia de Victoria y Aciertos (Sin estrellas)
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(Color(0xFFE8F5E9))
                                    .border(2.dp, Color(0xFF4CAF50), RoundedCornerShape(20.dp))
                                    .padding(horizontal = 16.dp, vertical = 6.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = "🏅", fontSize = 20.sp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "${state.correctCount}/${state.totalExercises} Aciertos",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = GreenPrimaryDark
                                    )
                                }
                            }
                        }

                        if (isLevel50Completed) {
                            // SECCIÓN DESTACADA: TROFEO NIVEL 50 CON PALABRAS DE FELICITACIÓN EXACTAS
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(
                                        Brush.verticalGradient(
                                            listOf(Color(0xFFFFF9C4), Color(0xFFFFECB3), Color(0xFFFFE082))
                                        )
                                    )
                                    .border(2.dp, Color(0xFFFFB300), RoundedCornerShape(20.dp))
                                    .padding(vertical = 12.dp, horizontal = 12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_trophy_3d),
                                    contentDescription = "Trofeo Nivel 50",
                                    modifier = Modifier
                                        .size(115.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "🏆 ¡FELICITACIONES! 🏆",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black,
                                    fontFamily = FredokaFont,
                                    color = Color(0xFFB78103),
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    text = level50PraiseText,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Black,
                                    fontFamily = FredokaFont,
                                    color = Color(0xFFE65100),
                                    textAlign = TextAlign.Center
                                )
                            }
                        } else if (state.nextLevelUnlocked) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(Color(0xFFE8F5E9))
                                    .border(1.5.dp, GreenPrimary, RoundedCornerShape(14.dp))
                                    .padding(vertical = 8.dp, horizontal = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "🔓 ¡Nivel ${state.levelNumber + 1} Desbloqueado!",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = GreenPrimaryDark
                                )
                            }
                        }

                        // Porcentaje de Aciertos Destacado con Conteo Animado (optimizado a 88.dp)
                        Box(
                            modifier = Modifier
                                .size(88.dp)
                                .clip(CircleShape)
                                .background(
                                    brush = Brush.radialGradient(
                                        colors = listOf(
                                            if (state.accuracyPercentage >= 70) Color(0xFFE8F5E9) else Color(0xFFFFF8E1),
                                            if (state.accuracyPercentage >= 70) GreenPrimary.copy(alpha = 0.2f) else OrangeSecondary.copy(alpha = 0.2f)
                                        )
                                    )
                                )
                                .border(
                                    width = 3.dp,
                                    color = if (state.accuracyPercentage >= 70) GreenPrimary else OrangeSecondary,
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "$animatedPercentage%",
                                    fontSize = 26.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (state.accuracyPercentage >= 70) GreenPrimaryDark else OrangeSecondary
                                )
                                Text(
                                    text = "de aciertos",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextMuted
                                )
                            }
                        }

                        // Métricas de Puntos, Racha Máxima y Nivel
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Puntos ganados en sesión
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "+${state.sessionPoints}",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFFF57F17)
                                )
                                Text(
                                    text = "Puntos sesión",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextMuted
                                )
                            }

                            // Racha máxima
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "🔥 ${state.maxStreak}",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFFE65100)
                                )
                                Text(
                                    text = "Mejor racha",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextMuted
                                )
                            }

                            // Nivel actual
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "🏆 Nivel ${state.currentLevel}",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = TextDark
                                )
                                Text(
                                    text = "🎯 Total: ${state.totalPoints} pts",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextMuted
                                )
                            }
                        }

                        // Resumen numérico de ejercicios
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Text(
                                text = "📝 ${state.totalExercises} Ejercicios",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextDark
                            )
                            Text(
                                text = "✅ ${state.correctCount} Correctas",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = GreenPrimaryDark
                            )
                            Text(
                                text = "💪 ${state.incorrectCount} Por mejorar",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = CoralEncouragement
                            )
                        }

                        // Desglose de las 3 Etapas de Evaluación Superadas
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0xFFF8FAFC))
                                .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(16.dp))
                                .padding(vertical = 10.dp, horizontal = 10.dp)
                        ) {
                            val stage1Passed = state.correctCount >= 1 || state.accuracyPercentage >= 30
                            val stage2Passed = state.correctCount >= 4 || state.accuracyPercentage >= 60
                            val stage3Passed = (state.correctCount >= 7 || state.accuracyPercentage >= 70) && state.nextLevelUnlocked
                            val allStagesPassed = stage1Passed && stage2Passed && stage3Passed

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = if (allStagesPassed) "🏆 ¡Superaste las 3 etapas pedagógicas!" else "📚 Progreso de etapas pedagógicas",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = TextDark
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    EvaluationStageResultBadge(
                                        icon = "🎯",
                                        title = "Selección Simple",
                                        isCompleted = stage1Passed,
                                        modifier = Modifier.weight(1f)
                                    )
                                    EvaluationStageResultBadge(
                                        icon = "🧩",
                                        title = "Completar Opción",
                                        isCompleted = stage2Passed,
                                        modifier = Modifier.weight(1f)
                                    )
                                    EvaluationStageResultBadge(
                                        icon = "✍️",
                                        title = "Completar Escrito",
                                        isCompleted = stage3Passed,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }
                }

                // Tarjeta de Recompensas Desbloqueadas
                if (state.unlockedRewards.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(14.dp))

                    KidCard(
                        modifier = Modifier.fillMaxWidth(),
                        cornerRadius = 24.dp,
                        elevation = 6.dp
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFFFF9C4).copy(alpha = 0.5f))
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "🎉 ¡Nueva recompensa desbloqueada!",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFFF57F17)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            state.unlockedRewards.forEach { reward ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(vertical = 4.dp)
                                ) {
                                    Text(text = reward.icon, fontSize = 28.sp)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = reward.title,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextDark
                                        )
                                        Text(
                                            text = reward.description,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextMuted
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Tarjeta de Anuncio Bonificado (Rewarded Video)
                if (adManager != null) {
                    Spacer(modifier = Modifier.height(14.dp))

                    KidCard(
                        modifier = Modifier.fillMaxWidth(),
                        cornerRadius = 24.dp,
                        elevation = 6.dp
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(if (rewardClaimed) Color(0xFFE8F5E9) else Color(0xFFFFF8E1))
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            if (rewardClaimed) {
                                Text(
                                    text = "🎉 ¡Recompensa entregada!",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = GreenPrimaryDark
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Has recibido +50 monedas bonus 🪙",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GreenPrimaryDark
                                )
                            } else {
                                Text(
                                    text = "🎁 ¡Cofre Bonificado de Mate!",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFFE65100)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Mira un video patrocinado para ganar +50 monedas 🪙",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextMuted,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                AnimatedGameButton(
                                    text = if (isRewardedLoaded) "Ver Video (+50 🪙) 🎬" else "Cargando Recompensa ⏳",
                                    onClick = {
                                        val activity = context as? Activity
                                        if (activity != null) {
                                            adManager.showRewarded(
                                                activity = activity,
                                                onRewardEarned = { amount ->
                                                    rewardClaimed = true
                                                    onRewardEarned?.invoke(amount)
                                                }
                                            )
                                        }
                                    },
                                    enabled = isRewardedLoaded,
                                    primaryColor = OrangeSecondary,
                                    darkColor = OrangeSecondaryDark,
                                    height = 46.dp,
                                    fontSize = 16.sp,
                                    modifier = Modifier.fillMaxWidth(0.85f)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // PANEL INFERIOR FIJO: Botón "Siguiente Nivel ➜" SIEMPRE visible sin deslizar
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (onNextLevelClick != null && state.nextLevelUnlocked && state.levelNumber < OperationLevelConfig.MAX_LEVELS_PER_OPERATION) {
                    AnimatedGameButton(
                        text = "Siguiente Nivel ➜",
                        onClick = {
                            if (!isNavigating) {
                                isNavigating = true
                                onNextLevelClick()
                            }
                        },
                        enabled = !isNavigating,
                        primaryColor = Color(0xFF1E88E5),
                        darkColor = Color(0xFF1565C0),
                        height = 52.dp,
                        fontSize = 20.sp,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                } else if (isLevel50Completed) {
                    AnimatedGameButton(
                        text = "🏆 ¡Completaste los 50 Niveles! 🏆",
                        onClick = {
                            if (!isNavigating) {
                                isNavigating = true
                                onHomeClick()
                            }
                        },
                        enabled = !isNavigating,
                        primaryColor = Color(0xFFFFB300),
                        darkColor = Color(0xFFF57F17),
                        height = 52.dp,
                        fontSize = 18.sp,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AnimatedGameButton(
                        text = "Reintentar 🔄",
                        onClick = {
                            if (!isNavigating) {
                                isNavigating = true
                                onPlayAgainClick()
                            }
                        },
                        enabled = !isNavigating,
                        primaryColor = GreenPrimary,
                        darkColor = GreenPrimaryDark,
                        height = 44.dp,
                        fontSize = 15.sp,
                        modifier = Modifier.weight(1f)
                    )

                    AnimatedGameButton(
                        text = "Inicio 🏠",
                        onClick = {
                            if (!isNavigating) {
                                isNavigating = true
                                onHomeClick()
                            }
                        },
                        enabled = !isNavigating,
                        primaryColor = Color(0xFF5C6BC0),
                        darkColor = Color(0xFF3949AB),
                        height = 44.dp,
                        fontSize = 15.sp,
                        modifier = Modifier.weight(1f)
                    )
                }

                // Banner Publicitario AdMob
                BannerAdView(modifier = Modifier.padding(top = 4.dp))
            }
        }
    }
}

@Composable
private fun EvaluationStageResultBadge(
    icon: String,
    title: String,
    isCompleted: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (isCompleted) Color.White else Color(0xFFF1F5F9))
            .border(1.dp, if (isCompleted) GreenPrimary.copy(alpha = 0.5f) else Color(0xFFCBD5E1), RoundedCornerShape(12.dp))
            .padding(vertical = 6.dp, horizontal = 4.dp)
    ) {
        Text(text = icon, fontSize = 16.sp)
        Text(
            text = title,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = if (isCompleted) TextDark else SlateMuted,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
        Text(
            text = if (isCompleted) "✅ Listo" else "⏳ En curso",
            fontSize = 9.sp,
            fontWeight = FontWeight.ExtraBold,
            color = if (isCompleted) GreenPrimaryDark else SlateMuted
        )
    }
}

