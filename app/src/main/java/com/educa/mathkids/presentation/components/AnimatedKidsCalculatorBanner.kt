package com.educa.mathkids.presentation.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.educa.mathkids.R
import com.educa.mathkids.presentation.theme.FredokaFont
import com.educa.mathkids.presentation.theme.SlateDark
import com.educa.mathkids.presentation.theme.SlateMuted

/**
 * Botón / Tarjeta Animada y Llamativa para acceder a la Calculadora Infantil.
 * Cuenta con animación de respiración (pulse), giro de destello y respuesta táctil 3D.
 */
@Composable
fun AnimatedKidsCalculatorBanner(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isEnglish: Boolean = false
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // 1. Animación continua sutil (Breathing / Pulse)
    val infiniteTransition = rememberInfiniteTransition(label = "calc_pulse_transition")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.032f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "calc_pulse_scale"
    )

    // Rotación suave del destello ✨
    val sparkleAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "calc_sparkle_angle"
    )

    // Respuesta al tacto (se encoge al presionar)
    val pressScale by animateFloatAsState(
        targetValue = if (isPressed) 0.94f else 1.0f,
        animationSpec = spring(dampingRatio = 0.55f, stiffness = 600f),
        label = "calc_press_scale"
    )

    val shape = RoundedCornerShape(22.dp)

    Box(
        modifier = modifier
            .scale(pulseScale * pressScale)
            .fillMaxWidth()
            .shadow(
                elevation = if (isPressed) 3.dp else 8.dp,
                shape = shape,
                spotColor = Color(0xFF8E24AA).copy(alpha = 0.35f)
            )
            .clip(shape)
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xFF6A1B9A),
                        Color(0xFF8E24AA),
                        Color(0xFFAB47BC)
                    )
                )
            )
            .border(
                width = 2.dp,
                brush = Brush.linearGradient(
                    listOf(
                        Color(0xFFFFD54F),
                        Color(0xFFFFCA28),
                        Color(0xFFFFFFFF).copy(alpha = 0.8f)
                    )
                ),
                shape = shape
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Lado Izquierdo: Icono 3D de Calculadora con Squircle brillante y destello
            Box(
                modifier = Modifier.size(54.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .shadow(4.dp, RoundedCornerShape(16.dp), spotColor = Color.Black.copy(alpha = 0.25f))
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White)
                        .padding(4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_nav_calculadora),
                        contentDescription = "Calculadora Infantil",
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Destello giratorio decorativo
                Text(
                    text = "✨",
                    fontSize = 18.sp,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .rotate(sparkleAngle)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Centro: Textos amigables y badge animado
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFFFD54F))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if (isEnglish) "⭐ FUN & NEW" else "⭐ NUEVO Y MÁGICO",
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FredokaFont,
                            color = SlateDark
                        )
                    }
                }

                Text(
                    text = if (isEnglish) "KIDS CALCULATOR" else "CALCULADORA INFANTIL",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FredokaFont,
                    color = Color.White
                )

                Text(
                    text = if (isEnglish) "Count apples, stars & cars!" else "¡Suma con manzanas, estrellas y carritos!",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FredokaFont,
                    color = Color(0xFFF3E5F5)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Lado Derecho: Botón de Acción Pulsante "¡ABRIR! ▶"
            Box(
                modifier = Modifier
                    .shadow(3.dp, RoundedCornerShape(14.dp), spotColor = Color(0xFFFF6F00).copy(alpha = 0.4f))
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color(0xFFFFB300),
                                Color(0xFFFB8C00)
                            )
                        )
                    )
                    .border(1.5.dp, Color.White.copy(alpha = 0.6f), RoundedCornerShape(14.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isEnglish) "OPEN 🧮" else "¡ABRIR! 🧮",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FredokaFont,
                    color = Color.White
                )
            }
        }
    }
}
