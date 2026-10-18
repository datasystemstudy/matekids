package com.educa.mathkids.core.streak

import android.content.Context
import android.content.SharedPreferences
import com.educa.mathkids.domain.repository.PlayerProgressRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate
import java.time.temporal.ChronoUnit

/**
 * Gestor oficial de Racha Diaria de MateKids con Protección y Recuperación de Racha.
 *
 * Reglas de negocio:
 * 1. Cada día consecutivo que el niño entra a la app aumenta la racha en 1 día (🔥).
 * 2. Si el niño entra el mismo día, su racha se mantiene intacta.
 * 3. Si el niño tenía racha (> 0) pero un día NO entra (diferencia de 2 días o más),
 *    la racha queda en riesgo de perderse.
 * 4. El sistema ofrece una oportunidad especial: ver un ANUNCIO RECOMPENSADO para salvar
 *    y recuperar la racha anterior, evitando empezar desde cero.
 */
object DailyStreakManager {

    private const val PREF_NAME = "mathkids_daily_streak"
    private const val KEY_LAST_ACTIVE_DATE = "last_active_date"
    private const val KEY_CURRENT_STREAK = "current_streak"
    private const val KEY_LOST_STREAK = "lost_streak"
    private const val KEY_RECOVERY_PENDING = "is_recovery_pending"

    private val _streakState = MutableStateFlow(0)
    val streakState: StateFlow<Int> = _streakState.asStateFlow()

    private val _lostStreakState = MutableStateFlow(0)
    val lostStreakState: StateFlow<Int> = _lostStreakState.asStateFlow()

    private val _recoveryPendingState = MutableStateFlow(false)
    val recoveryPendingState: StateFlow<Boolean> = _recoveryPendingState.asStateFlow()

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    /**
     * Inicializa los estados reactivos.
     */
    fun init(context: Context) {
        val prefs = getPrefs(context)
        _streakState.value = prefs.getInt(KEY_CURRENT_STREAK, 0)
        _lostStreakState.value = prefs.getInt(KEY_LOST_STREAK, 0)
        _recoveryPendingState.value = prefs.getBoolean(KEY_RECOVERY_PENDING, false)
    }

    /**
     * Evalúa la racha diaria al abrir la pantalla de inicio.
     * Retorna true si hay una recuperación de racha pendiente de mostrar al usuario.
     */
    suspend fun checkDailyStreak(
        context: Context,
        repository: PlayerProgressRepository? = null
    ): Boolean {
        val prefs = getPrefs(context)
        val today = LocalDate.now()
        val lastDateStr = prefs.getString(KEY_LAST_ACTIVE_DATE, "") ?: ""
        var currentStreak = prefs.getInt(KEY_CURRENT_STREAK, 0)
        val isRecoveryPending = prefs.getBoolean(KEY_RECOVERY_PENDING, false)

        if (isRecoveryPending) {
            _recoveryPendingState.value = true
            _lostStreakState.value = prefs.getInt(KEY_LOST_STREAK, currentStreak)
            return true
        }

        if (lastDateStr.isBlank()) {
            // Primer día en la aplicación
            currentStreak = 1
            prefs.edit()
                .putString(KEY_LAST_ACTIVE_DATE, today.toString())
                .putInt(KEY_CURRENT_STREAK, 1)
                .putBoolean(KEY_RECOVERY_PENDING, false)
                .apply()
            _streakState.value = 1
            _recoveryPendingState.value = false
            repository?.updateStreak(1)
            return false
        }

        val lastDate = try {
            LocalDate.parse(lastDateStr)
        } catch (e: Exception) {
            today
        }

        val daysBetween = ChronoUnit.DAYS.between(lastDate, today)

        when {
            daysBetween == 0L -> {
                // Mismo día: no cambia la racha
                _streakState.value = currentStreak
                _recoveryPendingState.value = false
                return false
            }
            daysBetween == 1L -> {
                // Día consecutivo (vino ayer y hoy vuelve): ¡Aumenta la racha!
                val newStreak = currentStreak + 1
                prefs.edit()
                    .putString(KEY_LAST_ACTIVE_DATE, today.toString())
                    .putInt(KEY_CURRENT_STREAK, newStreak)
                    .putBoolean(KEY_RECOVERY_PENDING, false)
                    .apply()
                _streakState.value = newStreak
                _recoveryPendingState.value = false
                repository?.updateStreak(newStreak)
                return false
            }
            else -> {
                // daysBetween >= 2: ¡Se saltó al menos un día!
                if (currentStreak > 0) {
                    // Tenía racha: activamos la oportunidad de recuperación con anuncio
                    val lost = currentStreak
                    prefs.edit()
                        .putInt(KEY_LOST_STREAK, lost)
                        .putBoolean(KEY_RECOVERY_PENDING, true)
                        .apply()
                    _lostStreakState.value = lost
                    _recoveryPendingState.value = true
                    return true
                } else {
                    // No tenía racha previa: comienza en 1
                    currentStreak = 1
                    prefs.edit()
                        .putString(KEY_LAST_ACTIVE_DATE, today.toString())
                        .putInt(KEY_CURRENT_STREAK, 1)
                        .putBoolean(KEY_RECOVERY_PENDING, false)
                        .apply()
                    _streakState.value = 1
                    _recoveryPendingState.value = false
                    repository?.updateStreak(1)
                    return false
                }
            }
        }
    }

    /**
     * El usuario vio con éxito el anuncio recompensado:
     * ¡Recuperamos la racha perdida y la extendemos al día actual!
     */
    suspend fun recoverStreak(
        context: Context,
        repository: PlayerProgressRepository? = null
    ) {
        val prefs = getPrefs(context)
        val today = LocalDate.now()
        val lost = prefs.getInt(KEY_LOST_STREAK, 1)
        val restoredStreak = (if (lost > 0) lost else 1) + 1

        prefs.edit()
            .putString(KEY_LAST_ACTIVE_DATE, today.toString())
            .putInt(KEY_CURRENT_STREAK, restoredStreak)
            .putInt(KEY_LOST_STREAK, 0)
            .putBoolean(KEY_RECOVERY_PENDING, false)
            .apply()

        _streakState.value = restoredStreak
        _lostStreakState.value = 0
        _recoveryPendingState.value = false

        repository?.updateStreak(restoredStreak)
    }

    /**
     * El usuario decide no ver el anuncio y empezar su racha de nuevo desde 1.
     */
    suspend fun dismissRecovery(
        context: Context,
        repository: PlayerProgressRepository? = null
    ) {
        val prefs = getPrefs(context)
        val today = LocalDate.now()

        prefs.edit()
            .putString(KEY_LAST_ACTIVE_DATE, today.toString())
            .putInt(KEY_CURRENT_STREAK, 1)
            .putInt(KEY_LOST_STREAK, 0)
            .putBoolean(KEY_RECOVERY_PENDING, false)
            .apply()

        _streakState.value = 1
        _lostStreakState.value = 0
        _recoveryPendingState.value = false

        repository?.updateStreak(1)
    }
}
