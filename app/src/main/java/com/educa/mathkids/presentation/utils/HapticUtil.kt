package com.educa.mathkids.presentation.utils

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

/**
 * Utilidad centralizada de vibración háptica ligera para MateKids (Fase 9).
 * Diseñada para ofrecer microinteracciones táctiles suaves, agradables para niños,
 * respetando el interruptor de configuración centralizada.
 */
class HapticUtil(private val context: Context) {

    var isHapticsEnabled: Boolean = true

    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        vibratorManager?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    /**
     * Vibración ultracorta y sutil para botones y toques cotidianos.
     */
    fun vibrateClick() {
        if (!isHapticsEnabled || vibrator?.hasVibrator() != true) return
        try {
            vibrator.vibrate(VibrationEffect.createOneShot(25, VibrationEffect.DEFAULT_AMPLITUDE))
        } catch (_: Throwable) {}
    }

    /**
     * Vibración suave de confirmación al acertar un ejercicio.
     */
    fun vibrateSuccess() {
        if (!isHapticsEnabled || vibrator?.hasVibrator() != true) return
        try {
            vibrator.vibrate(VibrationEffect.createOneShot(55, VibrationEffect.DEFAULT_AMPLITUDE))
        } catch (_: Throwable) {}
    }

    /**
     * Vibración no punitiva de apoyo ante una respuesta incorrecta.
     */
    fun vibrateEncourage() {
        if (!isHapticsEnabled || vibrator?.hasVibrator() != true) return
        try {
            val pattern = longArrayOf(0, 35, 45, 35)
            vibrator.vibrate(VibrationEffect.createWaveform(pattern, -1))
        } catch (_: Throwable) {}
    }

    /**
     * Vibración festiva para desbloqueo de recompensas o récords.
     */
    fun vibrateReward() {
        if (!isHapticsEnabled || vibrator?.hasVibrator() != true) return
        try {
            val pattern = longArrayOf(0, 40, 40, 50, 40, 70)
            vibrator.vibrate(VibrationEffect.createWaveform(pattern, -1))
        } catch (_: Throwable) {}
    }
}
