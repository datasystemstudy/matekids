package com.educa.mathkids.presentation.theme

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.runtime.compositionLocalOf

/**
 * Sistema centralizado de configuración de animaciones para MateKids (Fase 9).
 * Establece duraciones estándar, curvas de aceleración y especificaciones de resorte
 * para garantizar coherencia visual, suavidad (60 FPS) y soporte de accesibilidad (Reduced Motion).
 */
object AnimationConfig {

    // Duraciones base centralizadas (en milisegundos)
    const val FAST = 150
    const val NORMAL = 250
    const val SLOW = 400

    // Duraciones para celebraciones y conteos
    const val CELEBRATION_BURST = 1000
    const val COUNT_UP = 800
    const val FLOATING_POINTS = 750

    // Tween Specs
    fun <T> fastTween() = tween<T>(durationMillis = FAST, easing = FastOutSlowInEasing)
    fun <T> normalTween() = tween<T>(durationMillis = NORMAL, easing = FastOutSlowInEasing)
    fun <T> slowTween() = tween<T>(durationMillis = SLOW, easing = FastOutSlowInEasing)

    // Spring Specs
    fun <T> quickSpring() = spring<T>(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessMedium
    )

    fun <T> gentleSpring() = spring<T>(
        dampingRatio = Spring.DampingRatioLowBouncy,
        stiffness = Spring.StiffnessLow
    )

    fun <T> bouncySpring() = spring<T>(
        dampingRatio = 0.55f,
        stiffness = 350f
    )

    /**
     * Calcula la duración efectiva considerando si las animaciones están habilitadas.
     * Si las animaciones están desactivadas por accesibilidad o preferencia, retorna 0.
     */
    fun getEffectiveDuration(baseMillis: Int, animationsEnabled: Boolean): Int {
        return if (animationsEnabled) baseMillis else 0
    }
}

/**
 * CompositionLocal para transmitir el estado de animaciones habilitadas a toda la jerarquía de UI.
 */
val LocalAnimationsEnabled = compositionLocalOf { true }
