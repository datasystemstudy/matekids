package com.educa.mathkids.presentation.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import com.educa.mathkids.presentation.theme.AnimationConfig
import com.educa.mathkids.presentation.theme.CoralEncouragement
import com.educa.mathkids.presentation.theme.GreenPrimary
import com.educa.mathkids.presentation.theme.LocalAnimationsEnabled
import com.educa.mathkids.presentation.theme.OrangeSecondary
import com.educa.mathkids.presentation.theme.YellowStar
import kotlin.random.Random

private data class ConfettiParticle(
    val initialX: Float,
    val initialY: Float,
    val velocityX: Float,
    val velocityY: Float,
    val radius: Float,
    val color: Color,
    val isCircle: Boolean
)

/**
 * Componente ligero de celebración con partículas de confeti en Compose Canvas (Fase 9).
 * 100% nativo, sin dependencias pesadas ni fugas de memoria.
 * Se autolimita a ~900ms y respeta la configuración de reducción de movimiento.
 */
@Composable
fun ConfettiCelebration(
    visible: Boolean,
    modifier: Modifier = Modifier,
    onFinished: (() -> Unit)? = null
) {
    val animationsEnabled = LocalAnimationsEnabled.current

    if (!visible || !animationsEnabled) {
        return
    }

    val progress = remember { Animatable(0f) }

    val palette = listOf(
        YellowStar,
        OrangeSecondary,
        GreenPrimary,
        CoralEncouragement,
        Color(0xFF29B6F6),
        Color(0xFFAB47BC)
    )

    // Crear 30 partículas con trayectorias y colores aleatorios
    val particles = remember {
        val list = mutableListOf<ConfettiParticle>()
        val rnd = Random(System.currentTimeMillis())
        for (i in 0 until 32) {
            val color = palette[rnd.nextInt(palette.size)]
            val isCircle = rnd.nextBoolean()
            val radius = rnd.nextFloat() * 6f + 4f
            val startX = 0.5f + (rnd.nextFloat() - 0.5f) * 0.4f
            val startY = 0.4f + (rnd.nextFloat() - 0.5f) * 0.2f
            val vx = (rnd.nextFloat() - 0.5f) * 450f
            val vy = -rnd.nextFloat() * 320f - 80f

            list.add(
                ConfettiParticle(
                    initialX = startX,
                    initialY = startY,
                    velocityX = vx,
                    velocityY = vy,
                    radius = radius,
                    color = color,
                    isCircle = isCircle
                )
            )
        }
        list
    }

    LaunchedEffect(visible) {
        progress.snapTo(0f)
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = AnimationConfig.CELEBRATION_BURST,
                easing = LinearEasing
            )
        )
        onFinished?.invoke()
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        val p = progress.value
        val gravity = 600f
        val alpha = (1f - p).coerceIn(0f, 1f)

        particles.forEach { pt ->
            val curX = pt.initialX * w + pt.velocityX * p
            val curY = pt.initialY * h + pt.velocityY * p + 0.5f * gravity * p * p

            if (curX in 0f..w && curY in 0f..h) {
                val drawColor = pt.color.copy(alpha = alpha)
                if (pt.isCircle) {
                    drawCircle(
                        color = drawColor,
                        radius = pt.radius,
                        center = Offset(curX, curY)
                    )
                } else {
                    drawRect(
                        color = drawColor,
                        topLeft = Offset(curX - pt.radius, curY - pt.radius),
                        size = Size(pt.radius * 2f, pt.radius * 1.5f)
                    )
                }
            }
        }
    }
}
