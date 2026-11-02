package com.educa.mathkids.presentation.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import com.educa.mathkids.R
import com.educa.mathkids.presentation.theme.BackgroundSkyEnd
import com.educa.mathkids.presentation.theme.BackgroundSkyStart
import com.educa.mathkids.presentation.theme.LocalAnimationsEnabled
import kotlin.math.roundToInt
import kotlin.math.sin

/**
 * Fondo dinámico, animado y lúdico para MateKids.
 * Presenta un degradado fresco con los números 3D oficiales (2, 9, 3, 7, 5)
 * y fórmulas matemáticas de la ambientación ilustrada.
 */
@Composable
fun DynamicKidsBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val animationsEnabled = LocalAnimationsEnabled.current

    val infiniteTransition = rememberInfiniteTransition(label = "ambient_bg_anim")
    val animProgress by if (animationsEnabled) {
        infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 12000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "bg_float_progress"
        )
    } else {
        androidx.compose.runtime.remember { androidx.compose.runtime.mutableFloatStateOf(0f) }
    }

    val paintDeco = remember {
        android.graphics.Paint().apply {
            isAntiAlias = true
            textAlign = android.graphics.Paint.Align.CENTER
            textSize = 24f
            typeface = android.graphics.Typeface.DEFAULT_BOLD
        }
    }

    // Carga de números 3D oficiales de la ambientación
    val num2 = ImageBitmap.imageResource(id = R.drawable.ambient_num_2)
    val num9 = ImageBitmap.imageResource(id = R.drawable.ambient_num_9)
    val num3 = ImageBitmap.imageResource(id = R.drawable.ambient_num_3)
    val num7 = ImageBitmap.imageResource(id = R.drawable.ambient_num_7)
    val num5 = ImageBitmap.imageResource(id = R.drawable.ambient_num_5)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        BackgroundSkyStart,
                        Color(0xFFEFF6FF), // Azul pastel ligero
                        BackgroundSkyEnd
                    )
                )
            )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            // 1. Burbujas flotantes de colores suaves
            drawAmbientBubble(
                centerX = width * 0.12f,
                baseY = height * 0.18f,
                radius = width * 0.14f,
                color = Color(0xFF81D4FA).copy(alpha = 0.22f),
                animProgress = if (animationsEnabled) animProgress else 0f,
                phase = 0.0f
            )
            drawAmbientBubble(
                centerX = width * 0.88f,
                baseY = height * 0.26f,
                radius = width * 0.18f,
                color = Color(0xFFFFD54F).copy(alpha = 0.18f),
                animProgress = if (animationsEnabled) animProgress else 0f,
                phase = 1.8f
            )
            drawAmbientBubble(
                centerX = width * 0.18f,
                baseY = height * 0.78f,
                radius = width * 0.16f,
                color = Color(0xFF80CBC4).copy(alpha = 0.20f),
                animProgress = if (animationsEnabled) animProgress else 0f,
                phase = 3.2f
            )
            drawAmbientBubble(
                centerX = width * 0.82f,
                baseY = height * 0.84f,
                radius = width * 0.20f,
                color = Color(0xFF90CAF9).copy(alpha = 0.22f),
                animProgress = if (animationsEnabled) animProgress else 0f,
                phase = 4.5f
            )

            // 2. Colinas suaves en la base del fondo (estilo paisaje de la ilustración)
            drawCircle(
                color = Color(0xFFE8F5E9).copy(alpha = 0.40f),
                radius = width * 0.75f,
                center = Offset(width * 0.10f, height + width * 0.20f)
            )
            drawCircle(
                color = Color(0xFFE0F2F1).copy(alpha = 0.35f),
                radius = width * 0.65f,
                center = Offset(width * 0.90f, height + width * 0.15f)
            )

            // 3. Estrellas y destellos decorativos sutiles en bordes libres (sin invadir textos centrales)
            drawFloatingSymbol("★", width * 0.88f, height * 0.12f, android.graphics.Color.argb(45, 255, 179, 0), animProgress, 2.1f, paintDeco)
            drawFloatingSymbol("✦", width * 0.12f, height * 0.28f, android.graphics.Color.argb(40, 0, 188, 212), animProgress, 0.9f, paintDeco)
            drawFloatingSymbol("★", width * 0.88f, height * 0.82f, android.graphics.Color.argb(45, 255, 152, 0), animProgress, 1.4f, paintDeco)

            // 4. Números 3D oficiales de la ambientación flotando suavemente en bordes y esquinas
            val floatOffset2 = (sin((animProgress * 2 * Math.PI + 0.3f).toFloat()) * 12f)
            val floatOffset9 = (sin((animProgress * 2 * Math.PI + 1.8f).toFloat()) * 14f)
            val floatOffset3 = (sin((animProgress * 2 * Math.PI + 3.1f).toFloat()) * 12f)
            val floatOffset7 = (sin((animProgress * 2 * Math.PI + 4.5f).toFloat()) * 13f)
            val floatOffset5 = (sin((animProgress * 2 * Math.PI + 5.2f).toFloat()) * 12f)

            // Número 2 naranja 3D (abajo a la izquierda)
            val size2 = (width * 0.14f).roundToInt()
            drawImage(
                image = num2,
                dstOffset = IntOffset((width * 0.04f).roundToInt(), (height * 0.76f + floatOffset2).roundToInt()),
                dstSize = IntSize(size2, (size2 * 1.35f).roundToInt()),
                alpha = 0.40f
            )

            // Número 9 coral 3D (zona media izquierda)
            val size9 = (width * 0.13f).roundToInt()
            drawImage(
                image = num9,
                dstOffset = IntOffset((width * 0.02f).roundToInt(), (height * 0.46f + floatOffset9).roundToInt()),
                dstSize = IntSize(size9, (size9 * 1.35f).roundToInt()),
                alpha = 0.35f
            )

            // Número 3 naranja dorado 3D (arriba a la derecha)
            val size3 = (width * 0.12f).roundToInt()
            drawImage(
                image = num3,
                dstOffset = IntOffset((width * 0.84f).roundToInt(), (height * 0.18f + floatOffset3).roundToInt()),
                dstSize = IntSize(size3, (size3 * 1.30f).roundToInt()),
                alpha = 0.35f
            )

            // Número 7 rosa 3D (media derecha)
            val size7 = (width * 0.13f).roundToInt()
            drawImage(
                image = num7,
                dstOffset = IntOffset((width * 0.85f).roundToInt(), (height * 0.58f + floatOffset7).roundToInt()),
                dstSize = IntSize(size7, (size7 * 1.30f).roundToInt()),
                alpha = 0.35f
            )

            // Número 5 verde lima 3D (abajo a la derecha)
            val size5 = (width * 0.14f).roundToInt()
            drawImage(
                image = num5,
                dstOffset = IntOffset((width * 0.83f).roundToInt(), (height * 0.79f + floatOffset5).roundToInt()),
                dstSize = IntSize(size5, (size5 * 1.35f).roundToInt()),
                alpha = 0.40f
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            content()
        }
    }
}

private fun DrawScope.drawAmbientBubble(
    centerX: Float,
    baseY: Float,
    radius: Float,
    color: Color,
    animProgress: Float,
    phase: Float
) {
    val floatOffset = (sin((animProgress * 2 * Math.PI + phase).toFloat()) * 18f)
    drawCircle(
        color = color,
        radius = radius,
        center = Offset(centerX, baseY + floatOffset)
    )
}

private fun DrawScope.drawFloatingSymbol(
    text: String,
    x: Float,
    baseY: Float,
    color: Int,
    animProgress: Float,
    phase: Float,
    paint: android.graphics.Paint
) {
    val floatOffset = (sin((animProgress * 2 * Math.PI + phase).toFloat()) * 18f)
    paint.color = color
    drawContext.canvas.nativeCanvas.drawText(
        text,
        x,
        baseY + floatOffset,
        paint
    )
}
