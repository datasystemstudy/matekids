package com.educa.mathkids.presentation.components

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.educa.mathkids.domain.model.OperationTutorialInfo
import com.educa.mathkids.domain.model.OperationTutorialProvider
import com.educa.mathkids.domain.model.OperationType
import com.educa.mathkids.presentation.theme.GreenPrimary
import com.educa.mathkids.presentation.theme.OrangeSecondary
import com.educa.mathkids.presentation.theme.OrangeSecondaryDark
import com.educa.mathkids.presentation.theme.SlateBorder
import com.educa.mathkids.presentation.theme.SlateDark
import com.educa.mathkids.presentation.theme.SlateMuted
import com.educa.mathkids.presentation.theme.SurfaceWhite
import com.educa.mathkids.presentation.utils.SoundManager
import com.educa.mathkids.presentation.utils.TtsManager

/**
 * Pantalla completa interactiva de tutorial previo a comenzar cada nivel (Sección 14).
 * Presenta a Mate explicando la operación y el objetivo del nivel
 * mediante voz cálida y amigable, efectos sonoros y representación visual clara,
 * con el botón principal "¡EMPEZAR A JUGAR!" fijado al fondo para que NUNCA quede recortado.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun OperationLevelTutorialScreen(
    operationType: OperationType,
    levelNumber: Int,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    onBackClick: (() -> Unit)? = null,
    ttsManager: TtsManager? = null,
    soundManager: SoundManager? = null
) {
    val tutorial: OperationTutorialInfo = OperationTutorialProvider.getTutorial(operationType, levelNumber)
    val opColor = Color(operationType.colorHex)

    // Animación de flotación suave de Mate
    val infiniteTransition = rememberInfiniteTransition(label = "mate_tutorial_float")
    val floatOffset by infiniteTransition.animateFloat(
        initialValue = -4f,
        targetValue = 4f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "mate_float"
    )

    // Reproducción de audio y voz oficial al abrir el tutorial del nivel
    LaunchedEffect(operationType, levelNumber) {
        soundManager?.playOperationSelect(operationType)
        ttsManager?.speak(tutorial.spokenTutorial)
    }

    // Detener la voz si se sale de la pantalla
    DisposableEffect(Unit) {
        onDispose {
            ttsManager?.stop()
        }
    }

    // Manejo del botón atrás físico/gestual del sistema
    BackHandler {
        ttsManager?.stop()
        if (onBackClick != null) {
            onBackClick()
        } else {
            onDismiss()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // 1. Barra Superior con navegación y nivel
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Botón Flecha Atrás (Volver al mapa de niveles)
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .shadow(3.dp, CircleShape)
                    .clip(CircleShape)
                    .background(SurfaceWhite)
                    .border(1.5.dp, SlateBorder, CircleShape)
                    .clickable {
                        ttsManager?.stop()
                        soundManager?.playButtonClick()
                        if (onBackClick != null) onBackClick() else onDismiss()
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "←",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    color = SlateDark
                )
            }

            // Etiqueta del Nivel y Operación
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(18.dp))
                    .background(opColor.copy(alpha = 0.12f))
                    .border(1.5.dp, opColor.copy(alpha = 0.35f), RoundedCornerShape(18.dp))
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Text(
                    text = operationType.symbol,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = opColor
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "${operationType.displayName} • Nivel ${tutorial.levelNumber}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = opColor
                )
            }

            // Botón Cerrar "✕" (Saltar directo al juego)
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .shadow(3.dp, CircleShape)
                    .clip(CircleShape)
                    .background(SurfaceWhite)
                    .border(1.5.dp, SlateBorder, CircleShape)
                    .clickable {
                        ttsManager?.stop()
                        soundManager?.playButtonClick()
                        onDismiss()
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "✕",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = SlateMuted
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 2. Contenido Pedagógico Desplazable (Ocupa todo el espacio disponible)
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Mascota Mate con animación y bocadillo explicativo
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(5.dp, RoundedCornerShape(22.dp), spotColor = Color.Black.copy(alpha = 0.08f))
                    .clip(RoundedCornerShape(22.dp))
                    .background(SurfaceWhite)
                    .border(2.dp, opColor.copy(alpha = 0.25f), RoundedCornerShape(22.dp))
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.offset(y = floatOffset.dp)) {
                        Mascot(
                            size = 80.dp,
                            mood = MascotMood.CELEBRATING
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFFFFFBEB))
                            .border(1.5.dp, OrangeSecondary.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                            .padding(10.dp)
                    ) {
                        Column {
                            Text(
                                text = "Mate te guía:",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = OrangeSecondaryDark
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "¡Aprende cómo funciona antes de jugar! 🌟",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = SlateDark
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Título y Subtítulo del Nivel
            Text(
                text = tutorial.levelTitle,
                fontSize = 21.sp,
                fontWeight = FontWeight.Black,
                color = SlateDark,
                textAlign = TextAlign.Center
            )

            Text(
                text = tutorial.levelSubtitle,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = SlateMuted,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Tarjeta 1: ¿Qué significa la operación?
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(3.dp, RoundedCornerShape(18.dp), spotColor = Color.Black.copy(alpha = 0.05f))
                    .clip(RoundedCornerShape(18.dp))
                    .background(SurfaceWhite)
                    .border(1.5.dp, SlateBorder, RoundedCornerShape(18.dp))
                    .padding(14.dp)
            ) {
                Column {
                    Text(
                        text = "💡 ${tutorial.conceptTitle}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = SlateDark
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = tutorial.conceptDescription,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = SlateDark,
                        lineHeight = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Tarjeta 2: Ejemplo Visual Animado
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(3.dp, RoundedCornerShape(18.dp), spotColor = Color.Black.copy(alpha = 0.05f))
                    .clip(RoundedCornerShape(18.dp))
                    .background(SurfaceWhite)
                    .border(2.dp, opColor.copy(alpha = 0.35f), RoundedCornerShape(18.dp))
                    .padding(14.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Ejemplo visual:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = SlateMuted
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Bloques visuales de la operación
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Grupo Izquierdo
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(opColor.copy(alpha = 0.1f))
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                repeat(tutorial.exampleLeft) {
                                    Text(text = tutorial.exampleEmoji, fontSize = 20.sp)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // Operador (+, −, ×, ÷)
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .clip(CircleShape)
                                .background(opColor),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = tutorial.exampleOperator,
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // Grupo Derecho
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(opColor.copy(alpha = 0.1f))
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                repeat(tutorial.exampleRight) {
                                    Text(text = tutorial.exampleEmoji, fontSize = 20.sp)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = "=",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            color = SlateDark
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        // Resultado
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(GreenPrimary.copy(alpha = 0.15f))
                                .border(1.dp, GreenPrimary.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(2.dp),
                                maxItemsInEachRow = 4
                            ) {
                                repeat(tutorial.exampleResult) {
                                    Text(text = tutorial.exampleEmoji, fontSize = 18.sp)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = tutorial.exampleExplanation,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = SlateDark,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Botón interactivo para re-escuchar la explicación de Mate
            Box(
                modifier = Modifier
                    .shadow(2.dp, RoundedCornerShape(14.dp), spotColor = Color.Black.copy(alpha = 0.04f))
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFFFFF7ED))
                    .border(1.5.dp, OrangeSecondary.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                    .clickable {
                        soundManager?.playButtonClick()
                        ttsManager?.speak(tutorial.spokenTutorial)
                    }
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "🔊", fontSize = 17.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Escuchar explicación de Mate",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = OrangeSecondaryDark
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 3. Botón principal de acción: Fijado al fondo (SIEMPRE 100% VISIBLE Y COMPLETO)
        AnimatedGameButton(
            text = "🚀 ¡EMPEZAR A JUGAR!",
            onClick = {
                ttsManager?.stop()
                soundManager?.playGameStart()
                onDismiss()
            },
            primaryColor = opColor,
            darkColor = opColor.copy(alpha = 0.85f),
            height = 54.dp,
            fontSize = 18.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp)
        )
    }
}

/**
 * Compatibilidad con invocaciones en formato diálogo.
 * Delega en la pantalla completa de tutorial para garantizar visualización al 100%.
 */
@Composable
fun OperationLevelTutorialDialog(
    operationType: OperationType,
    levelNumber: Int,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    onBackClick: (() -> Unit)? = null,
    ttsManager: TtsManager? = null,
    soundManager: SoundManager? = null
) {
    OperationLevelTutorialScreen(
        operationType = operationType,
        levelNumber = levelNumber,
        onDismiss = onDismiss,
        onBackClick = onBackClick,
        modifier = modifier,
        ttsManager = ttsManager,
        soundManager = soundManager
    )
}
