package com.educa.mathkids.presentation.components

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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.educa.mathkids.presentation.theme.FredokaFont
import com.educa.mathkids.presentation.theme.SlateBorder
import com.educa.mathkids.presentation.theme.SlateDark
import com.educa.mathkids.presentation.theme.SlateMuted
import com.educa.mathkids.presentation.theme.SurfaceWhite

/**
 * Diálogo interactivo infantil que ofrece recuperar la racha perdida
 * a cambio de visualizar un anuncio bonificado / recompensado (Rewarded Ad).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StreakRecoveryDialog(
    lostStreak: Int,
    isEnglish: Boolean = false,
    onWatchAd: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "flame_pulse")
    val flameScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(tween(700), RepeatMode.Reverse),
        label = "flame_scale"
    )

    BasicAlertDialog(
        onDismissRequest = onDismiss,
        modifier = modifier
    ) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = SurfaceWhite,
            tonalElevation = 14.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(3.dp, Color(0xFFFF7043).copy(alpha = 0.45f), RoundedCornerShape(28.dp))
                    .padding(22.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Icono central con fuego palpitante
                    Box(
                        modifier = Modifier
                            .size(84.dp)
                            .shadow(8.dp, CircleShape, spotColor = Color(0xFFFF5722).copy(alpha = 0.45f))
                            .clip(CircleShape)
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color(0xFFFFF3E0), Color(0xFFFFCCBC))
                                )
                            )
                            .border(2.5.dp, Color(0xFFFF7043), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "🔥",
                            fontSize = 44.sp,
                            modifier = Modifier.scale(flameScale)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = if (isEnglish) "Save Your Streak! 🔥" else "¡Salva tu Racha! 🔥",
                        fontSize = 23.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FredokaFont,
                        color = Color(0xFFD84315),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (isEnglish) {
                            "You didn't log in yesterday and your $lostStreak-day streak is frozen! 🥺\nWatch a sponsored video to recover and protect your streak!"
                        } else {
                            "¡No entraste ayer y tu racha de $lostStreak días se congeló! 🥺\n¿Quieres ver un video patrocinado para recuperar tus $lostStreak días 🔥 y seguir avanzando?"
                        },
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = FredokaFont,
                        color = SlateMuted,
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Píldora destacada con la racha que se salvará
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFFFBE9E7))
                            .border(1.5.dp, Color(0xFFFF8A65), RoundedCornerShape(16.dp))
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = if (isEnglish) "🔥 Streak to save: " else "🔥 Racha a salvar: ",
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FredokaFont,
                                color = Color(0xFFBF360C)
                            )
                            Text(
                                text = "$lostStreak " + if (isEnglish) "days" else "días",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FredokaFont,
                                color = Color(0xFFD84315)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Botón 1: Ver Video para Salvar Racha
                    AnimatedGameButton(
                        text = if (isEnglish) "🎬 Watch Video & Save Streak 🔥" else "🎬 Ver Video y Salvar Racha 🔥",
                        onClick = onWatchAd,
                        primaryColor = Color(0xFFFF5722),
                        darkColor = Color(0xFFD84315),
                        height = 54.dp,
                        fontSize = 16.sp,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Botón 2: Empezar de nuevo (descartar recuperación)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFFF1F5F9))
                            .border(1.5.dp, SlateBorder, RoundedCornerShape(16.dp))
                            .clickable { onDismiss() },
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "🔄", fontSize = 15.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isEnglish) "Start over (1 day)" else "Empezar de nuevo (1 día)",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FredokaFont,
                                color = SlateDark
                            )
                        }
                    }
                }
            }
        }
    }
}
