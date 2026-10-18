package com.educa.mathkids.domain.model.adventure

import androidx.compose.ui.graphics.Color
import com.educa.mathkids.domain.model.OperationType
import kotlin.math.sqrt

/**
 * Vector 2D ligero para cálculo de posiciones, velocidades y distancias en Compose Canvas.
 */
data class Vector2D(
    val x: Float = 0f,
    val y: Float = 0f
) {
    operator fun plus(other: Vector2D): Vector2D = Vector2D(x + other.x, y + other.y)
    operator fun minus(other: Vector2D): Vector2D = Vector2D(x - other.x, y - other.y)
    operator fun times(scalar: Float): Vector2D = Vector2D(x * scalar, y * scalar)

    fun distanceTo(other: Vector2D): Float {
        val dx = x - other.x
        val dy = y - other.y
        return sqrt((dx * dx + dy * dy).toDouble()).toFloat()
    }

    fun length(): Float = sqrt((x * x + y * y).toDouble()).toFloat()

    fun normalized(): Vector2D {
        val len = length()
        return if (len > 0.0001f) Vector2D(x / len, y / len) else Vector2D(0f, 0f)
    }
}

/**
 * Orbe interactivo flotante en el espacio 2D.
 */
data class OrbEntity(
    val id: Int,
    val text: String,
    val value: Int,
    val isCorrect: Boolean,
    val position: Vector2D,
    val velocity: Vector2D,
    val radius: Float = 95f,
    val baseColor: Color = Color(0xFF1E88E5),
    val glowColor: Color = Color(0xFF64B5F6),
    val pulsePhase: Float = 0f,
    val isPopped: Boolean = false,
    val popScale: Float = 1f,
    val popAlpha: Float = 1f
)

/**
 * Partícula para efectos visuales (chispas, confeti, destellos).
 */
data class ParticleEntity(
    val position: Vector2D,
    val velocity: Vector2D,
    val color: Color,
    val radius: Float,
    val alpha: Float = 1f,
    val rotation: Float = 0f,
    val rotationSpeed: Float = 0f,
    val life: Float = 1f,
    val maxLife: Float = 1f
)

/**
 * Texto flotante que emerge al acertar (+100 PTS, ¡COMBO x3!).
 */
data class FloatingTextEntity(
    val id: Long,
    val text: String,
    val position: Vector2D,
    val velocity: Vector2D = Vector2D(0f, -120f),
    val color: Color = Color(0xFFFFD600),
    val alpha: Float = 1f,
    val life: Float = 1.2f
)

enum class HeroMood {
    IDLE,
    CELEBRATING,
    THINKING,
    HURT
}

data class HeroState(
    val position: Vector2D = Vector2D(120f, 650f),
    val mood: HeroMood = HeroMood.IDLE,
    val jumpOffset: Float = 0f,
    val moodTimer: Float = 0f
)

data class AdventureQuestion(
    val equation: String,
    val targetValue: Int,
    val prompt: String,
    val explanation: String,
    val hint: String,
    val operationType: OperationType = OperationType.ADDITION
)

enum class AdventurePhase {
    PLAYING,
    ROUND_WON,
    GAME_OVER,
    PAUSED
}

data class AdventureGameState(
    val score: Int = 0,
    val stars: Int = 0,
    val combo: Int = 0,
    val maxCombo: Int = 0,
    val lives: Int = 3,
    val maxLives: Int = 3,
    val currentLevel: Int = 1,
    val worldName: String = "Valle de los Números",
    val phase: AdventurePhase = AdventurePhase.PLAYING,
    val question: AdventureQuestion = AdventureQuestion(
        equation = "4 + 5 = ?",
        targetValue = 9,
        prompt = "¡Atrapa el orbe con el resultado correcto!",
        explanation = "4 más 5 da un total de 9.",
        hint = "Imagina que tienes 4 manzanas y juntas 5 más."
    ),
    val orbs: List<OrbEntity> = emptyList(),
    val particles: List<ParticleEntity> = emptyList(),
    val floatingTexts: List<FloatingTextEntity> = emptyList(),
    val hero: HeroState = HeroState(),
    val totalAnsweredCorrectly: Int = 0,
    val totalRequiredToWin: Int = 5,
    val showHintDialog: Boolean = false
)
