package com.educa.mathkids.presentation.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.educa.mathkids.R
import com.educa.mathkids.presentation.theme.LocalAnimationsEnabled
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin

/**
 * Estados emocionales de la mascota de MateKids ("Mate").
 */
enum class MascotMood {
    NORMAL,
    HAPPY,
    CELEBRATING,
    THINKING,
    SURPRISED,
    ENCOURAGING
}

/**
 * Mascota principal de MateKids: "Mate", un elefantito 3D adorable y expresivo.
 * Incluye animaciones fluidas de flotación, balanceo, respiración, destellos de estrellas
 * y sombra interactiva con respuesta al tacto.
 */
@Composable
fun Mascot(
    modifier: Modifier = Modifier,
    mood: MascotMood = MascotMood.NORMAL,
    size: Dp = 160.dp,
    onClick: (() -> Unit)? = null
) {
    val animationsEnabled = LocalAnimationsEnabled.current
    val coroutineScope = rememberCoroutineScope()
    var isTapped by remember { mutableStateOf(false) }

    val tapScale by animateFloatAsState(
        targetValue = if (isTapped) 1.15f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "mascot_tap_scale"
    )

    // Configuración de tiempos según el estado de ánimo
    val bounceDuration = when (mood) {
        MascotMood.CELEBRATING -> 420
        MascotMood.SURPRISED -> 480
        MascotMood.HAPPY -> 620
        MascotMood.ENCOURAGING -> 700
        MascotMood.NORMAL -> 850
        MascotMood.THINKING -> 1050
    }

    val maxBounce = when (mood) {
        MascotMood.CELEBRATING -> -14f
        MascotMood.SURPRISED -> -12f
        MascotMood.HAPPY -> -10f
        MascotMood.ENCOURAGING -> -8f
        MascotMood.NORMAL -> -6f
        MascotMood.THINKING -> -4f
    }

    val maxTilt = when (mood) {
        MascotMood.CELEBRATING -> 6.5f
        MascotMood.HAPPY -> 4.5f
        MascotMood.SURPRISED -> 3.5f
        MascotMood.ENCOURAGING -> 3.0f
        MascotMood.NORMAL -> 2.0f
        MascotMood.THINKING -> -3.5f
    }

    val (floatOffset, tiltAngle, breathScale, sparkleProgress) = if (animationsEnabled) {
        val infiniteTransition = rememberInfiniteTransition(label = "mascot_3d_anim")

        val floatAnim by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = maxBounce,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = bounceDuration),
                repeatMode = RepeatMode.Reverse
            ),
            label = "mascot_float"
        )

        val tiltAnim by infiniteTransition.animateFloat(
            initialValue = -maxTilt,
            targetValue = maxTilt,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = bounceDuration + 200),
                repeatMode = RepeatMode.Reverse
            ),
            label = "mascot_tilt"
        )

        val breathAnim by infiniteTransition.animateFloat(
            initialValue = 0.985f,
            targetValue = 1.025f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 1100),
                repeatMode = RepeatMode.Reverse
            ),
            label = "mascot_breath"
        )

        val sparkleAnim by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 750),
                repeatMode = RepeatMode.Restart
            ),
            label = "mascot_sparkle"
        )

        listOf(floatAnim, tiltAnim, breathAnim, sparkleAnim)
    } else {
        listOf(0f, 0f, 1f, 0f)
    }

    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .size(size)
            .then(
                if (onClick != null) {
                    Modifier.clickable(
                        interactionSource = interactionSource,
                        indication = null
                    ) {
                        isTapped = true
                        coroutineScope.launch {
                            delay(260)
                            isTapped = false
                        }
                        onClick()
                    }
                } else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        // 1. Sombra suave en el suelo que reacciona a la flotación 3D
        Canvas(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .size(width = size * 0.65f, height = size * 0.16f)
                .offset(y = 2.dp)
        ) {
            val progress = (floatOffset / -14f).coerceIn(0f, 1f)
            val shadowScale = 1f - (progress * 0.22f)
            val shadowAlpha = 0.22f - (progress * 0.10f)

            val shadowWidth = this.size.width * shadowScale
            val shadowHeight = this.size.height * shadowScale
            val left = (this.size.width - shadowWidth) / 2f
            val top = (this.size.height - shadowHeight) / 2f

            drawOval(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.Black.copy(alpha = shadowAlpha),
                        Color.Transparent
                    ),
                    center = Offset(this.size.width / 2f, this.size.height / 2f),
                    radius = shadowWidth / 2f
                ),
                topLeft = Offset(left, top),
                size = Size(shadowWidth, shadowHeight)
            )
        }

        // 2. Destellos mágicos / Estrellitas flotantes si está feliz o celebrando
        if (animationsEnabled && (mood == MascotMood.CELEBRATING || mood == MascotMood.HAPPY)) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer { translationY = floatOffset }
            ) {
                val w = this.size.width
                val h = this.size.height

                // Estrellitas cerca de la mano saludando
                drawMiniStar(
                    center = Offset(w * 0.20f, h * 0.32f + (sparkleProgress * -8f)),
                    size = w * 0.045f,
                    alpha = (sin(sparkleProgress * Math.PI).toFloat()).coerceIn(0f, 1f),
                    color = Color(0xFFFFD54F)
                )

                drawMiniStar(
                    center = Offset(w * 0.82f, h * 0.26f + ((1f - sparkleProgress) * -10f)),
                    size = w * 0.055f,
                    alpha = (sin((1f - sparkleProgress) * Math.PI).toFloat()).coerceIn(0f, 1f),
                    color = Color(0xFFFFB300)
                )

                if (mood == MascotMood.CELEBRATING) {
                    drawMiniStar(
                        center = Offset(w * 0.50f, h * 0.10f + (sparkleProgress * -12f)),
                        size = w * 0.06f,
                        alpha = (sin(sparkleProgress * Math.PI).toFloat()).coerceIn(0f, 1f),
                        color = Color(0xFF64B5F6)
                    )
                }
            }
        }

        // 3. Mascota Elefantito 3D renderizada con transformaciones fluidas
        Image(
            painter = painterResource(id = R.drawable.ic_mascot_elephant_3d),
            contentDescription = "Mascota Mate",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    translationY = floatOffset
                    rotationZ = tiltAngle
                    scaleX = breathScale * tapScale
                    scaleY = breathScale * tapScale
                }
        )
    }
}

/**
 * Función auxiliar para dibujar estrellitas de destello animadas.
 */
private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawMiniStar(
    center: Offset,
    size: Float,
    alpha: Float,
    color: Color
) {
    if (alpha <= 0.01f) return
    val path = androidx.compose.ui.graphics.Path()
    val points = 4
    val outerR = size
    val innerR = size * 0.40f

    for (i in 0 until points * 2) {
        val r = if (i % 2 == 0) outerR else innerR
        val angle = (i * Math.PI / points) - (Math.PI / 2.0)
        val x = (center.x + r * cos(angle)).toFloat()
        val y = (center.y + r * sin(angle)).toFloat()
        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
    }
    path.close()

    drawPath(path = path, color = color.copy(alpha = alpha))
}
