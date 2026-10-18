package com.educa.mathkids.core.game

import android.content.Context
import android.content.SharedPreferences
import com.educa.mathkids.domain.model.OperationType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Gestor de persistencia y desbloqueo progresivo para los 15 niveles del minijuego de Aventura y Globos.
 *
 * Reglas:
 * - El Nivel 1 está desbloqueado por defecto.
 * - Los Niveles del 2 al 15 inician bloqueados.
 * - Cada nivel se desbloquea al superar el nivel anterior.
 */
object AdventureProgressManager {

    private const val PREF_NAME = "mathkids_adventure_progress"
    private const val KEY_PREFIX = "adventure_unlocked_level_"

    // Estado reactivo en memoria para Compose
    private val _unlockedLevelsState = MutableStateFlow<Map<OperationType, Int>>(emptyMap())
    val unlockedLevelsState: StateFlow<Map<OperationType, Int>> = _unlockedLevelsState.asStateFlow()

    // Estado reactivo para reabrir la ventana de selección de operaciones/niveles al volver atrás
    private val _pendingDialogOpState = MutableStateFlow<OperationType?>(null)
    val pendingDialogOpState: StateFlow<OperationType?> = _pendingDialogOpState.asStateFlow()

    fun requestOpenDialog(op: OperationType) {
        _pendingDialogOpState.value = op
    }

    fun clearPendingDialog() {
        _pendingDialogOpState.value = null
    }

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    /**
     * Inicializa el estado reactivo desde SharedPreferences.
     */
    fun init(context: Context) {
        val prefs = getPrefs(context)
        val map = OperationType.entries.associateWith { op ->
            prefs.getInt(KEY_PREFIX + op.name, 1).coerceIn(1, 15)
        }
        _unlockedLevelsState.value = map
    }

    /**
     * Obtiene el nivel máximo desbloqueado (1..15) para la operación dada.
     */
    fun getUnlockedLevel(context: Context, operationType: OperationType): Int {
        _unlockedLevelsState.value[operationType]?.let { return it }
        val lvl = getPrefs(context).getInt(KEY_PREFIX + operationType.name, 1).coerceIn(1, 15)
        _unlockedLevelsState.value = _unlockedLevelsState.value + (operationType to lvl)
        return lvl
    }

    /**
     * Determina si un nivel específico (1..15) está desbloqueado para la operación dada.
     */
    fun isLevelUnlocked(context: Context, operationType: OperationType, levelNumber: Int): Boolean {
        if (levelNumber <= 1) return true
        val maxUnlocked = getUnlockedLevel(context, operationType)
        return levelNumber <= maxUnlocked
    }

    /**
     * Desbloquea el siguiente nivel tras superar [completedLevel].
     * Retorna el nuevo nivel máximo desbloqueado.
     */
    fun unlockNextLevel(context: Context, operationType: OperationType, completedLevel: Int): Int {
        val currentMax = getUnlockedLevel(context, operationType)
        val nextLevel = (completedLevel + 1).coerceAtMost(15)
        if (nextLevel > currentMax) {
            getPrefs(context).edit().putInt(KEY_PREFIX + operationType.name, nextLevel).apply()
            val updated = _unlockedLevelsState.value.toMutableMap()
            updated[operationType] = nextLevel
            _unlockedLevelsState.value = updated
            return nextLevel
        }
        return currentMax
    }

    /**
     * Restablece el progreso de aventura a nivel 1 desbloqueado para todas las operaciones.
     */
    fun resetAll(context: Context) {
        getPrefs(context).edit().clear().apply()
        _unlockedLevelsState.value = OperationType.entries.associateWith { 1 }
    }
}
