package com.educa.mathkids.presentation.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.runtime.remember
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
import com.educa.mathkids.presentation.theme.AnimationConfig
import com.educa.mathkids.presentation.theme.Dimens
import com.educa.mathkids.presentation.theme.GreenPrimary
import com.educa.mathkids.presentation.theme.GreenPrimaryDark
import com.educa.mathkids.presentation.theme.OrangeSecondary
import com.educa.mathkids.presentation.theme.SlateBorder
import com.educa.mathkids.presentation.theme.SlateDark
import com.educa.mathkids.presentation.theme.SlateMuted
import com.educa.mathkids.presentation.theme.SoftRed
import com.educa.mathkids.presentation.theme.SurfaceWhite

/**
 * Diálogo interactivo infantil cuando el jugador pierde las 3 vidas.
 * Presenta dos opciones claras:
 * 1. Ver un anuncio bonificado para recargar 3 vidas completas y continuar.
 * 2. Salir a la ventana de inicio.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OutOfLivesDialog(
    onWatchAd: () -> Unit,
    onExitToHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    BasicAlertDialog(
        onDismissRequest = { /* No cerrar al pulsar fuera para forzar decisión consciente */ },
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
                    .border(3.dp, SoftRed.copy(alpha = 0.35f), RoundedCornerShape(28.dp))
                    .padding(22.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Icono central: Corazón roto animado con resplandor
                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .shadow(6.dp, CircleShape, spotColor = SoftRed.copy(alpha = 0.4f))
                            .clip(CircleShape)
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color(0xFFFFEBEE), Color(0xFFFFCDD2))
                                )
                            )
                            .border(2.dp, SoftRed.copy(alpha = 0.5f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "💔", fontSize = 38.sp)
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "¡Te quedaste sin vidas!",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        color = SlateDark,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Has fallado 3 veces en esta ronda. 🥺\n¿Quieres ver un video para recuperar tus 3 vidas ❤️ y continuar?",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = SlateMuted,
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Píldora de recompensa destacada
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFFE8F5E9))
                            .border(1.5.dp, GreenPrimary.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(text = "🎁 Recompensa: ", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = GreenPrimaryDark)
                            Text(text = "❤️ ❤️ ❤️", fontSize = 16.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Botón 1: Ver anuncio para continuar (+3 vidas)
                    AnimatedGameButton(
                        text = "📺 Ver Anuncio (+3 ❤️)",
                        onClick = onWatchAd,
                        primaryColor = GreenPrimary,
                        darkColor = GreenPrimaryDark,
                        height = 58.dp,
                        fontSize = 18.sp,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Botón 2: Salir al inicio
                    val interactionSource = remember { MutableInteractionSource() }
                    val isPressed by interactionSource.collectIsPressedAsState()
                    val scale by animateFloatAsState(
                        targetValue = if (isPressed) 0.95f else 1.0f,
                        animationSpec = AnimationConfig.quickSpring(),
                        label = "exit_btn_scale"
                    )

                    Box(
                        modifier = Modifier
                            .scale(scale)
                            .fillMaxWidth()
                            .height(48.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFFF1F5F9))
                            .border(1.5.dp, SlateBorder, RoundedCornerShape(16.dp))
                            .clickable(
                                interactionSource = interactionSource,
                                indication = null,
                                onClick = onExitToHome
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "🏠", fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Salir al Inicio",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = SlateDark
                            )
                        }
                    }
                }
            }
        }
    }
}
