package com.educa.mathkids.presentation.screens.adventure.components

import android.graphics.Paint
import android.graphics.Rect
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import com.educa.mathkids.R
import com.educa.mathkids.domain.model.adventure.AdventureGameState
import com.educa.mathkids.domain.model.adventure.HeroMood
import com.educa.mathkids.domain.model.adventure.HeroState
import com.educa.mathkids.domain.model.adventure.OrbEntity
import kotlin.math.cos
import kotlin.math.sin

/**
 * Lienzo 2D de alto rendimiento para el videojuego de aventura educativa.
 * Renderiza el escenario de fantasía, la mascota animada, los orbes mágicos flotantes
 * y el sistema de partículas a 60/120 FPS.
 */
@Composable
fun AdventureGameCanvas(
    gameState: AdventureGameState,
    onCanvasTapped: (x: Float, y: Float) -> Unit,
    onSizeChanged: (width: Float, height: Float) -> Unit,
    modifier: Modifier = Modifier
) {
    // Paints de Android nativo reutilizables para texto optimizado en Canvas
    val orbTextPaint = remember {
        Paint().apply {
            color = android.graphics.Color.WHITE
            textSize = 58f
            isFakeBoldText = true
            isAntiAlias = true
            textAlign = Paint.Align.CENTER
            setShadowLayer(8f, 0f, 4f, android.graphics.Color.argb(160, 0, 0, 0))
        }
    }

    val floatingTextPaint = remember {
        Paint().apply {
            textSize = 46f
            isFakeBoldText = true
            isAntiAlias = true
            textAlign = Paint.Align.CENTER
        }
    }

    val textBounds = remember { Rect() }

    val heroBitmap = ImageBitmap.imageResource(id = R.drawable.ic_mascot_elephant_3d)

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .onSizeChanged { size ->
                onSizeChanged(size.width.toFloat(), size.height.toFloat())
            }
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    onCanvasTapped(offset.x, offset.y)
                }
            }
    ) {
        val w = size.width
        val h = size.height

        // 1. Fondo de Aventura 2D (Cielo místico + Colinas con degradados suaves)
        drawAdventureBackground(w, h)

        // 2. Mascota Héroe interactiva (Mascota 3D oficial de MateKids)
        drawHeroCharacter(gameState.hero, heroBitmap)

        // 3. Orbes mágicos flotantes
        gameState.orbs.forEach { orb ->
            drawMagicOrb(orb, orbTextPaint, textBounds)
        }

        // 4. Sistema de partículas dinámicas (Chispas, Confeti, Estrellas)
        gameState.particles.forEach { p ->
            val pColor = p.color.copy(alpha = p.alpha)
            rotate(degrees = p.rotation, pivot = Offset(p.position.x, p.position.y)) {
                // Dibujar forma de estrella o confeti rectangular
                drawRoundRect(
                    color = pColor,
                    topLeft = Offset(p.position.x - p.radius, p.position.y - p.radius * 0.7f),
                    size = Size(p.radius * 2f, p.radius * 1.4f),
                    cornerRadius = CornerRadius(4f, 4f)
                )
            }
        }

        // 5. Textos flotantes (+100 PTS, ¡COMBO x2!)
        gameState.floatingTexts.forEach { ft ->
            floatingTextPaint.color = ft.color.copy(alpha = ft.alpha).toArgb()
            floatingTextPaint.setShadowLayer(6f, 0f, 3f, android.graphics.Color.argb((180 * ft.alpha).toInt(), 0, 0, 0))
            drawContext.canvas.nativeCanvas.drawText(
                ft.text,
                ft.position.x,
                ft.position.y,
                floatingTextPaint
            )
        }
    }
}

/**
 * Renderiza el escenario: cielo de aventura, nubes etéreas y colinas verdes.
 */
private fun DrawScope.drawAdventureBackground(width: Float, height: Float) {
    // Cielo degradado de aventura
    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(
                Color(0xFF81D4FA), // Azul cielo suave arriba
                Color(0xFFE1F5FE), // Brillo de horizonte
                Color(0xFFFFF9C4)  // Luz dorada cálida en el horizonte
            ),
            startY = 0f,
            endY = height * 0.75f
        ),
        size = Size(width, height)
    )

    // Sol / Portal mágico en la esquina superior
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                Color(0xFFFFF59D).copy(alpha = 0.85f),
                Color(0xFFFFD54F).copy(alpha = 0.35f),
                Color.Transparent
            ),
            center = Offset(width * 0.82f, height * 0.16f),
            radius = width * 0.28f
        ),
        center = Offset(width * 0.82f, height * 0.16f),
        radius = width * 0.28f
    )

    // Nubes estilizadas decorativas
    drawCloud(Offset(width * 0.25f, height * 0.14f), 55f)
    drawCloud(Offset(width * 0.65f, height * 0.22f), 45f)

    // Colina lejana (verde azulado)
    val hillBackPath = Path().apply {
        moveTo(0f, height * 0.70f)
        cubicTo(
            width * 0.35f, height * 0.64f,
            width * 0.70f, height * 0.73f,
            width, height * 0.67f
        )
        lineTo(width, height)
        lineTo(0f, height)
        close()
    }
    drawPath(
        path = hillBackPath,
        brush = Brush.verticalGradient(
            colors = listOf(Color(0xFF81C784), Color(0xFF4CAF50)),
            startY = height * 0.64f,
            endY = height
        )
    )

    // Colina frontal de césped vivo
    val hillFrontPath = Path().apply {
        moveTo(0f, height * 0.78f)
        cubicTo(
            width * 0.40f, height * 0.74f,
            width * 0.68f, height * 0.82f,
            width, height * 0.76f
        )
        lineTo(width, height)
        lineTo(0f, height)
        close()
    }
    drawPath(
        path = hillFrontPath,
        brush = Brush.verticalGradient(
            colors = listOf(Color(0xFF66BB6A), Color(0xFF2E7D32)),
            startY = height * 0.74f,
            endY = height
        )
    )

    // Borde iluminado en la cima de la colina
    drawPath(
        path = hillFrontPath,
        color = Color(0xFFA5D6A7),
        style = Stroke(width = 5f)
    )
}

private fun DrawScope.drawCloud(center: Offset, baseRadius: Float) {
    val cloudColor = Color.White.copy(alpha = 0.75f)
    drawCircle(cloudColor, radius = baseRadius, center = center)
    drawCircle(cloudColor, radius = baseRadius * 0.8f, center = Offset(center.x - baseRadius * 0.8f, center.y + baseRadius * 0.1f))
    drawCircle(cloudColor, radius = baseRadius * 0.85f, center = Offset(center.x + baseRadius * 0.8f, center.y + baseRadius * 0.05f))
}

/**
 * Renderiza al héroe / mascota oficial 3D de MateKids con animaciones de emoción y salto.
 */
private fun DrawScope.drawHeroCharacter(hero: HeroState, heroBitmap: ImageBitmap) {
    val hx = hero.position.x
    val hy = hero.position.y - hero.jumpOffset

    // Tamaño aumentado para mayor presencia visual y encanto para niños
    val baseWidth = 230f
    val baseHeight = 245f

    // Escala según estado anímico
    val moodScale = when (hero.mood) {
        HeroMood.CELEBRATING -> 1.15f
        HeroMood.HURT -> 0.88f
        else -> 1.0f
    }

    val drawWidth = baseWidth * moodScale
    val drawHeight = baseHeight * moodScale

    // Sombra en el suelo reactiva a la altura del salto
    val shadowAlpha = (0.35f * (1f - (hero.jumpOffset / 160f))).coerceIn(0.10f, 0.35f)
    val shadowWidth = (drawWidth * 0.72f) * (1f - (hero.jumpOffset / 220f)).coerceIn(0.40f, 1f)
    val groundFeetY = hero.position.y + (drawHeight / 2f) - 6f
    drawOval(
        color = Color(0x33000000).copy(alpha = shadowAlpha),
        topLeft = Offset(hx - shadowWidth / 2f, groundFeetY - 8f),
        size = Size(shadowWidth, 24f)
    )

    // Renderizar la mascota oficial 3D de MateKids aumentada
    drawImage(
        image = heroBitmap,
        dstOffset = IntOffset(
            x = (hx - drawWidth / 2f).toInt(),
            y = (hy - drawHeight / 2f).toInt()
        ),
        dstSize = IntSize(
            width = drawWidth.toInt(),
            height = drawHeight.toInt()
        )
    )
}

/**
 * Renderiza un orbe mágico flotante 2D con efecto de iluminación volumétrica,
 * halo exterior y tipografía nítida en su núcleo.
 */
private fun DrawScope.drawMagicOrb(
    orb: OrbEntity,
    textPaint: Paint,
    textBounds: Rect
) {
    val center = Offset(orb.position.x, orb.position.y)
    val effectiveRadius = orb.radius * orb.popScale
    val alpha = orb.popAlpha

    if (alpha <= 0f) return

    val pulse = sin(orb.pulsePhase) * 6f

    // 1. Resplandor / Halo exterior místico
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                orb.glowColor.copy(alpha = 0.55f * alpha),
                orb.glowColor.copy(alpha = 0.18f * alpha),
                Color.Transparent
            ),
            center = center,
            radius = (effectiveRadius + 24f + pulse)
        ),
        radius = effectiveRadius + 24f + pulse,
        center = center
    )

    // 2. Anillo de energía orbital pulsante
    drawCircle(
        color = orb.glowColor.copy(alpha = 0.45f * alpha),
        radius = effectiveRadius + 8f + pulse * 0.5f,
        center = center,
        style = Stroke(width = 4f)
    )

    // 3. Esfera principal con gradiente 3D (luz en el cuadrante superior izquierdo)
    val lightSource = Offset(center.x - effectiveRadius * 0.35f, center.y - effectiveRadius * 0.35f)
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.95f * alpha),
                orb.glowColor.copy(alpha = 0.90f * alpha),
                orb.baseColor.copy(alpha = 1.0f * alpha)
            ),
            center = lightSource,
            radius = effectiveRadius * 1.25f
        ),
        radius = effectiveRadius,
        center = center
    )

    // 4. Borde brillante vítreo
    drawCircle(
        color = Color.White.copy(alpha = 0.55f * alpha),
        radius = effectiveRadius,
        center = center,
        style = Stroke(width = 3.5f)
    )

    // 5. Destello especular en la parte superior
    drawOval(
        color = Color.White.copy(alpha = 0.65f * alpha),
        topLeft = Offset(center.x - effectiveRadius * 0.45f, center.y - effectiveRadius * 0.70f),
        size = Size(effectiveRadius * 0.9f, effectiveRadius * 0.38f)
    )

    // 6. Nudo alegre de globo en la base inferior
    val knotY = center.y + effectiveRadius * 0.96f
    val knotWidth = effectiveRadius * 0.30f
    val knotHeight = effectiveRadius * 0.20f
    val knotPath = Path().apply {
        moveTo(center.x, knotY)
        lineTo(center.x - knotWidth / 2f, knotY + knotHeight)
        lineTo(center.x + knotWidth / 2f, knotY + knotHeight)
        close()
    }
    drawPath(path = knotPath, color = orb.baseColor.copy(alpha = 0.95f * alpha))

    // 7. Texto numérico centrado con tipografía escalada según el radio del globo
    textPaint.textSize = effectiveRadius * 0.92f
    textPaint.alpha = (255 * alpha).toInt()
    textPaint.getTextBounds(orb.text, 0, orb.text.length, textBounds)
    val textY = center.y + (textBounds.height() / 2f)

    drawContext.canvas.nativeCanvas.drawText(
        orb.text,
        center.x,
        textY,
        textPaint
    )
}
