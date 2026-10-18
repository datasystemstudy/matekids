package com.educa.mathkids.core.game

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.withFrameNanos
import kotlinx.coroutines.isActive

/**
 * Hook composable para ejecutar un Game Loop a 60/120 FPS sincronizado con la pantalla.
 *
 * Utiliza [withFrameNanos] para recibir el tiempo de renderizado de cada cuadro,
 * calculando un delta seguro y suave para actualizar la física y las partículas sin tirones.
 *
 * @param isRunning Controla si el ciclo de juego está activo (se pausa automáticamente cuando es false).
 * @param onTick Callback ejecutado en cada cuadro con el deltaTime en segundos (normalizado).
 */
@Composable
fun rememberGameLoop(
    isRunning: Boolean = true,
    onTick: (deltaTimeSeconds: Float) -> Unit
) {
    val currentOnTick = rememberUpdatedState(onTick)

    LaunchedEffect(isRunning) {
        if (!isRunning) return@LaunchedEffect

        var lastFrameNanos = 0L

        while (isActive) {
            withFrameNanos { currentFrameNanos ->
                if (lastFrameNanos != 0L) {
                    val rawDelta = (currentFrameNanos - lastFrameNanos) / 1_000_000_000f
                    // Acotar el delta entre 1ms y 33ms para evitar saltos bruscos tras interrupciones
                    val clampedDelta = rawDelta.coerceIn(0.001f, 0.033f)
                    currentOnTick.value(clampedDelta)
                }
                lastFrameNanos = currentFrameNanos
            }
        }
    }
}
