package com.educa.mathkids.core.game

import androidx.compose.ui.graphics.Color
import com.educa.mathkids.domain.model.OperationType
import com.educa.mathkids.domain.model.adventure.AdventureGameState
import com.educa.mathkids.domain.model.adventure.AdventurePhase
import com.educa.mathkids.domain.model.adventure.AdventureQuestion
import com.educa.mathkids.domain.model.adventure.FloatingTextEntity
import com.educa.mathkids.domain.model.adventure.HeroMood
import com.educa.mathkids.domain.model.adventure.HeroState
import com.educa.mathkids.domain.model.adventure.OrbEntity
import com.educa.mathkids.domain.model.adventure.ParticleEntity
import com.educa.mathkids.domain.model.adventure.Vector2D
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

/**
 * Eventos sonoros y hápticos emitidos por el motor de juego.
 */
enum class GameSoundEvent {
    CORRECT_ANSWER,
    INCORRECT_ANSWER,
    LEVEL_CLEARED,
    GAME_OVER,
    COMBO_STREAK,
    ORB_POP,
    BUTTON_CLICK
}

/**
 * Motor de lógica y física en tiempo real para el minijuego de Aventura 2D.
 */
class AdventureGameEngine(
    private val onEmitSound: (GameSoundEvent) -> Unit = {}
) {
    private val _state = MutableStateFlow(AdventureGameState())
    val state: StateFlow<AdventureGameState> = _state.asStateFlow()

    private var screenWidth: Float = 1080f
    private var screenHeight: Float = 1920f
    private var textIdCounter: Long = 0L

    fun setScreenDimensions(width: Float, height: Float) {
        if (width > 0 && height > 0) {
            screenWidth = width
            screenHeight = height
            // Posicionar al héroe en el tercio inferior izquierdo
            _state.update { current ->
                current.copy(
                    hero = current.hero.copy(
                        position = Vector2D(width * 0.15f, height * 0.78f)
                    )
                )
            }
            if (_state.value.orbs.isEmpty()) {
                spawnOrbsForCurrentQuestion()
            }
        }
    }

    fun startLevel(level: Int = 1, operation: OperationType = OperationType.ADDITION) {
        val safeLevel = level.coerceIn(1, 15)
        val question = generateQuestion(safeLevel, operation)
        _state.value = AdventureGameState(
            currentLevel = safeLevel,
            score = 0,
            stars = 0,
            combo = 0,
            maxCombo = 0,
            lives = 3,
            maxLives = 3,
            worldName = getWorldName(operation),
            phase = AdventurePhase.PLAYING,
            question = question,
            totalAnsweredCorrectly = 0,
            totalRequiredToWin = 5,
            hero = HeroState(position = Vector2D(screenWidth * 0.15f, screenHeight * 0.78f))
        )
        spawnOrbsForCurrentQuestion()
    }

    /**
     * Restaura las 3 vidas del héroe tras ver un anuncio recompensado o descartar, y reanuda la partida en el juego.
     */
    fun restoreLivesAndContinue(livesToRestore: Int = 3) {
        _state.update { current ->
            current.copy(
                lives = livesToRestore,
                maxLives = 3,
                phase = AdventurePhase.PLAYING,
                hero = current.hero.copy(mood = HeroMood.CELEBRATING, moodTimer = 1.0f)
            )
        }
        if (_state.value.orbs.isEmpty() || _state.value.orbs.all { it.isPopped }) {
            spawnOrbsForCurrentQuestion()
        }
    }

    /**
     * Paso de simulación física y actualización temporal ejecutado por el GameLoop.
     */
    fun onTick(delta: Float) {
        if (_state.value.phase != AdventurePhase.PLAYING) return

        _state.update { current ->
            // 1. Actualizar posiciones y oscilaciones de los orbes
            val updatedOrbs = current.orbs.mapNotNull { orb ->
                if (orb.isPopped) {
                    val newScale = orb.popScale + delta * 3.5f
                    val newAlpha = (orb.popAlpha - delta * 4.0f).coerceAtLeast(0f)
                    if (newAlpha <= 0f) null
                    else orb.copy(popScale = newScale, popAlpha = newAlpha)
                } else {
                    // Movimiento suave y rebote en límites jugables
                    var newX = orb.position.x + orb.velocity.x * delta
                    var newY = orb.position.y + orb.velocity.y * delta
                    var vx = orb.velocity.x
                    var vy = orb.velocity.y

                    val playAreaLeft = orb.radius + 20f
                    val playAreaRight = screenWidth - orb.radius - 20f
                    val playAreaTop = screenHeight * 0.26f // Debajo del HUD y tarjeta de pregunta
                    val playAreaBottom = screenHeight * 0.70f // Sobre el suelo del héroe

                    if (newX <= playAreaLeft) {
                        newX = playAreaLeft
                        vx = -vx
                    } else if (newX >= playAreaRight) {
                        newX = playAreaRight
                        vx = -vx
                    }

                    if (newY <= playAreaTop) {
                        newY = playAreaTop
                        vy = -vy
                    } else if (newY >= playAreaBottom) {
                        newY = playAreaBottom
                        vy = -vy
                    }

                    val newPulse = (orb.pulsePhase + delta * 2.5f) % (2f * PI.toFloat())

                    orb.copy(
                        position = Vector2D(newX, newY),
                        velocity = Vector2D(vx, vy),
                        pulsePhase = newPulse
                    )
                }
            }

            // 2. Actualizar partículas (gravedad, fricción y vida)
            val updatedParticles = current.particles.mapNotNull { p ->
                val newLife = p.life - delta
                if (newLife <= 0f) null
                else {
                    val newPos = p.position + p.velocity * delta
                    val newVel = Vector2D(p.velocity.x * 0.96f, (p.velocity.y + 250f * delta)) // Leve gravedad
                    val alpha = (newLife / p.maxLife).coerceIn(0f, 1f)
                    val newRot = p.rotation + p.rotationSpeed * delta
                    p.copy(
                        position = newPos,
                        velocity = newVel,
                        alpha = alpha,
                        rotation = newRot,
                        life = newLife
                    )
                }
            }

            // 3. Actualizar textos flotantes
            val updatedTexts = current.floatingTexts.mapNotNull { ft ->
                val newLife = ft.life - delta
                if (newLife <= 0f) null
                else {
                    val newPos = ft.position + ft.velocity * delta
                    val alpha = (newLife / 1.2f).coerceIn(0f, 1f)
                    ft.copy(position = newPos, alpha = alpha, life = newLife)
                }
            }

            // 4. Actualizar héroe (saltitos al celebrar y timer de estado)
            val heroMoodTimer = (current.hero.moodTimer - delta).coerceAtLeast(0f)
            val newMood = if (heroMoodTimer <= 0f) HeroMood.IDLE else current.hero.mood
            val jumpOffset = if (newMood == HeroMood.CELEBRATING) {
                sin((1f - heroMoodTimer / 1.0f) * PI.toFloat() * 3f) * 28f
            } else 0f

            current.copy(
                orbs = updatedOrbs,
                particles = updatedParticles,
                floatingTexts = updatedTexts,
                hero = current.hero.copy(
                    mood = newMood,
                    moodTimer = heroMoodTimer,
                    jumpOffset = jumpOffset
                )
            )
        }
    }

    /**
     * Detección de toque táctil sobre un orbe en el Canvas.
     */
    fun onCanvasTapped(tapX: Float, tapY: Float) {
        if (_state.value.phase != AdventurePhase.PLAYING) return

        val tapPos = Vector2D(tapX, tapY)
        val targetOrb = _state.value.orbs.firstOrNull { orb ->
            !orb.isPopped && tapPos.distanceTo(orb.position) <= (orb.radius * 1.25f)
        } ?: return

        handleOrbSelected(targetOrb)
    }

    private fun handleOrbSelected(orb: OrbEntity) {
        if (orb.isCorrect) {
            handleCorrectAnswer(orb)
        } else {
            handleIncorrectAnswer(orb)
        }
    }

    private fun handleCorrectAnswer(orb: OrbEntity) {
        val currentCombo = _state.value.combo + 1
        val pointsGained = 100 * currentCombo
        val newScore = _state.value.score + pointsGained
        val newCorrectCount = _state.value.totalAnsweredCorrectly + 1
        val maxCombo = maxOf(_state.value.maxCombo, currentCombo)

        onEmitSound(if (currentCombo >= 3) GameSoundEvent.COMBO_STREAK else GameSoundEvent.CORRECT_ANSWER)
        onEmitSound(GameSoundEvent.ORB_POP)

        // Generar partículas de victoria alrededor del orbe
        val burstParticles = createBurstParticles(orb.position, count = 28, isSuccess = true)

        // Texto flotante de puntuación
        val comboText = if (currentCombo > 1) "¡COMBO x$currentCombo! +$pointsGained" else "+$pointsGained PTS"
        val floatingText = FloatingTextEntity(
            id = ++textIdCounter,
            text = comboText,
            position = orb.position - Vector2D(0f, 40f),
            color = Color(0xFFFFD600)
        )

        // Marcar orbe como reventado con animación
        val updatedOrbs = _state.value.orbs.map {
            if (it.id == orb.id) it.copy(isPopped = true) else it
        }

        val levelCompleted = newCorrectCount >= _state.value.totalRequiredToWin
        val stars = if (levelCompleted) calculateStars(_state.value.lives) else _state.value.stars

        _state.update { current ->
            current.copy(
                score = newScore,
                combo = currentCombo,
                maxCombo = maxCombo,
                totalAnsweredCorrectly = newCorrectCount,
                stars = stars,
                phase = if (levelCompleted) AdventurePhase.ROUND_WON else AdventurePhase.PLAYING,
                orbs = updatedOrbs,
                particles = current.particles + burstParticles,
                floatingTexts = current.floatingTexts + floatingText,
                hero = current.hero.copy(
                    mood = HeroMood.CELEBRATING,
                    moodTimer = 1.2f
                )
            )
        }

        if (levelCompleted) {
            onEmitSound(GameSoundEvent.LEVEL_CLEARED)
            // Lluvia de confeti
            spawnCelebrationConfetti()
        } else {
            // Siguiente pregunta tras breve pausa
            advanceToNextQuestion()
        }
    }

    private fun handleIncorrectAnswer(orb: OrbEntity) {
        onEmitSound(GameSoundEvent.INCORRECT_ANSWER)
        val newLives = (_state.value.lives - 1).coerceAtLeast(0)
        val isGameOver = newLives <= 0

        val burstParticles = createBurstParticles(orb.position, count = 15, isSuccess = false)
        val floatingText = FloatingTextEntity(
            id = ++textIdCounter,
            text = "¡Casi! Inténtalo de nuevo",
            position = orb.position - Vector2D(0f, 30f),
            color = Color(0xFFEF5350)
        )

        _state.update { current ->
            current.copy(
                combo = 0,
                lives = newLives,
                phase = if (isGameOver) AdventurePhase.GAME_OVER else AdventurePhase.PLAYING,
                particles = current.particles + burstParticles,
                floatingTexts = current.floatingTexts + floatingText,
                hero = current.hero.copy(
                    mood = HeroMood.HURT,
                    moodTimer = 1.0f
                )
            )
        }

        if (isGameOver) {
            onEmitSound(GameSoundEvent.GAME_OVER)
        }
    }

    private fun advanceToNextQuestion() {
        val nextQuestion = generateQuestion(_state.value.currentLevel, _state.value.question.operationType)
        _state.update { it.copy(question = nextQuestion) }
        spawnOrbsForCurrentQuestion()
    }

    companion object {
        /**
         * Determina el número total de globos en pantalla según el nivel:
         * - Niveles 1 al 5: 4 globos
         * - Niveles 6 al 10: 6 globos (se agregan 2 globos para un total de 6)
         * - Niveles 11 al 15: 8 globos (se agregan 2 globos más para un total de 8)
         */
        fun getBalloonCountForLevel(level: Int): Int {
            return when {
                level >= 11 -> 8
                level >= 6 -> 6
                else -> 4
            }
        }
    }

    private fun spawnOrbsForCurrentQuestion() {
        val currentQuestion = _state.value.question
        val target = currentQuestion.targetValue
        val currentLevel = _state.value.currentLevel

        val balloonCount = getBalloonCountForLevel(currentLevel)
        val distractorCount = balloonCount - 1

        // Generar distractores creíbles, únicos y positivos cercanos al valor objetivo
        val distractors = mutableSetOf<Int>()
        val offsets = listOf(-1, 1, -2, 2, -3, 3, -4, 4, -5, 5, -6, 6, -7, 7, -8, 8, -10, 10).shuffled()
        for (offset in offsets) {
            val candidate = target + offset
            if (candidate > 0 && candidate != target) {
                distractors.add(candidate)
            }
            if (distractors.size >= distractorCount) break
        }
        var step = 1
        while (distractors.size < distractorCount) {
            val candidate = (target + step).coerceAtLeast(1)
            if (candidate != target) {
                distractors.add(candidate)
            }
            step = if (step > 0) -step else (-step + 1)
        }

        val allValues = (distractors.toList() + target).shuffled()

        // Límites del área jugable en pantalla
        val playTop = screenHeight * 0.27f
        val playBottom = screenHeight * 0.68f
        val playLeft = screenWidth * 0.12f
        val playRight = screenWidth * 0.88f
        val playW = playRight - playLeft
        val playH = playBottom - playTop

        // Posiciones de cuadrícula adaptadas al número de globos (4, 6 u 8)
        val gridSlots = when (balloonCount) {
            8 -> listOf(
                Vector2D(playLeft + playW * 0.28f, playTop + playH * 0.13f),
                Vector2D(playLeft + playW * 0.72f, playTop + playH * 0.13f),
                Vector2D(playLeft + playW * 0.25f, playTop + playH * 0.38f),
                Vector2D(playLeft + playW * 0.75f, playTop + playH * 0.38f),
                Vector2D(playLeft + playW * 0.29f, playTop + playH * 0.63f),
                Vector2D(playLeft + playW * 0.71f, playTop + playH * 0.63f),
                Vector2D(playLeft + playW * 0.26f, playTop + playH * 0.87f),
                Vector2D(playLeft + playW * 0.74f, playTop + playH * 0.87f)
            ).shuffled()

            6 -> listOf(
                Vector2D(playLeft + playW * 0.28f, playTop + playH * 0.18f),
                Vector2D(playLeft + playW * 0.72f, playTop + playH * 0.18f),
                Vector2D(playLeft + playW * 0.25f, playTop + playH * 0.50f),
                Vector2D(playLeft + playW * 0.75f, playTop + playH * 0.50f),
                Vector2D(playLeft + playW * 0.28f, playTop + playH * 0.82f),
                Vector2D(playLeft + playW * 0.72f, playTop + playH * 0.82f)
            ).shuffled()

            else -> listOf(
                Vector2D(playLeft + playW * 0.28f, playTop + playH * 0.28f),
                Vector2D(playLeft + playW * 0.72f, playTop + playH * 0.28f),
                Vector2D(playLeft + playW * 0.28f, playTop + playH * 0.72f),
                Vector2D(playLeft + playW * 0.72f, playTop + playH * 0.72f)
            ).shuffled()
        }

        // Paleta vibrante y alegre de 8 colores para globos
        val orbColors = listOf(
            Pair(Color(0xFF1E88E5), Color(0xFF64B5F6)), // Azul Zafiro
            Pair(Color(0xFF8E24AA), Color(0xFFBA68C8)), // Amatista Púrpura
            Pair(Color(0xFF00897B), Color(0xFF4DB6AC)), // Esmeralda Jade
            Pair(Color(0xFFFB8C00), Color(0xFFFFB74D)), // Ámbar Naranja
            Pair(Color(0xFFE91E63), Color(0xFFF06292)), // Rosa Fucsia
            Pair(Color(0xFF43A047), Color(0xFF81C784)), // Verde Alegre
            Pair(Color(0xFFFF5722), Color(0xFFFF8A65)), // Coral Sol
            Pair(Color(0xFF5E35B1), Color(0xFF9575CD))  // Violeta Real
        ).shuffled()

        val baseRadius = when (balloonCount) {
            8 -> 58f
            6 -> 70f
            else -> 82f
        }

        val maxJitter = when (balloonCount) {
            8 -> 18f
            6 -> 24f
            else -> 32f
        }

        val speedFactor = when (balloonCount) {
            8 -> 0.75f
            6 -> 0.88f
            else -> 1.0f
        }

        val newOrbs = allValues.mapIndexed { index, value ->
            val slot = gridSlots[index]
            val jitterX = Random.nextFloat() * (maxJitter * 2f) - maxJitter
            val jitterY = Random.nextFloat() * (maxJitter * 2f) - maxJitter
            val vx = (Random.nextFloat() * 32f + 20f) * speedFactor * if (Random.nextBoolean()) 1f else -1f
            val vy = (Random.nextFloat() * 28f + 16f) * speedFactor * if (Random.nextBoolean()) 1f else -1f

            val colors = orbColors[index % orbColors.size]

            OrbEntity(
                id = index + 1,
                text = value.toString(),
                value = value,
                isCorrect = value == target,
                position = Vector2D(slot.x + jitterX, slot.y + jitterY),
                velocity = Vector2D(vx, vy),
                radius = baseRadius,
                baseColor = colors.first,
                glowColor = colors.second,
                pulsePhase = Random.nextFloat() * 6.28f
            )
        }

        _state.update { it.copy(orbs = newOrbs) }
    }

    private fun createBurstParticles(origin: Vector2D, count: Int, isSuccess: Boolean): List<ParticleEntity> {
        val successColors = listOf(
            Color(0xFFFFD600), // Oro
            Color(0xFF00E676), // Verde esmeralda
            Color(0xFF00B0FF), // Celeste resplandeciente
            Color(0xFFFF4081), // Rosa magenta
            Color(0xFFFFFFFF)  // Destello blanco
        )
        val failColors = listOf(
            Color(0xFFEF5350), // Rojo
            Color(0xFFFF7043), // Coral
            Color(0xFFFFA726)  // Naranja
        )
        val palette = if (isSuccess) successColors else failColors

        return (0 until count).map {
            val angle = Random.nextFloat() * 2f * PI.toFloat()
            val speed = Random.nextFloat() * 320f + 90f
            val vx = cos(angle) * speed
            val vy = sin(angle) * speed
            val maxLife = Random.nextFloat() * 0.5f + 0.6f

            ParticleEntity(
                position = origin,
                velocity = Vector2D(vx, vy),
                color = palette.random(),
                radius = Random.nextFloat() * 8f + 5f,
                rotation = Random.nextFloat() * 360f,
                rotationSpeed = Random.nextFloat() * 400f - 200f,
                life = maxLife,
                maxLife = maxLife
            )
        }
    }

    private fun spawnCelebrationConfetti() {
        val confettiList = (0 until 60).map {
            val startX = Random.nextFloat() * screenWidth
            val startY = -Random.nextFloat() * 200f
            val vx = Random.nextFloat() * 120f - 60f
            val vy = Random.nextFloat() * 260f + 180f
            val life = Random.nextFloat() * 1.5f + 2.0f

            ParticleEntity(
                position = Vector2D(startX, startY),
                velocity = Vector2D(vx, vy),
                color = listOf(
                    Color(0xFFFFD600), Color(0xFF00E5FF), Color(0xFFFF4081),
                    Color(0xFF76FF03), Color(0xFFE040FB), Color(0xFFFFAB00)
                ).random(),
                radius = Random.nextFloat() * 10f + 6f,
                rotation = Random.nextFloat() * 360f,
                rotationSpeed = Random.nextFloat() * 300f - 150f,
                life = life,
                maxLife = life
            )
        }
        _state.update { it.copy(particles = it.particles + confettiList) }
    }

    private fun calculateStars(livesRemaining: Int): Int {
        return when (livesRemaining) {
            3 -> 3
            2 -> 2
            else -> 1
        }
    }

    fun toggleHintDialog(show: Boolean) {
        _state.update { it.copy(showHintDialog = show) }
    }

    fun togglePause() {
        _state.update { current ->
            val nextPhase = if (current.phase == AdventurePhase.PLAYING) AdventurePhase.PAUSED
            else AdventurePhase.PLAYING
            current.copy(phase = nextPhase)
        }
    }

    private fun generateQuestion(level: Int, operation: OperationType): AdventureQuestion {
        val safeLevel = level.coerceIn(1, 15)
        return when (operation) {
            OperationType.ADDITION -> {
                val (a, b) = when (safeLevel) {
                    1 -> Pair(Random.nextInt(1, 3), Random.nextInt(1, 3)) // Sumas hasta 4
                    2 -> Pair(Random.nextInt(1, 4), Random.nextInt(1, 4)) // Sumas hasta 6
                    3 -> Pair(Random.nextInt(1, 5), Random.nextInt(1, 5)) // Sumas hasta 8
                    4 -> Pair(Random.nextInt(2, 6), Random.nextInt(1, 6)) // Sumas hasta 10
                    5 -> Pair(Random.nextInt(3, 7), Random.nextInt(2, 6)) // Sumas hasta 12
                    6 -> Pair(Random.nextInt(4, 8), Random.nextInt(3, 8)) // Sumas hasta 15
                    7 -> Pair(Random.nextInt(5, 9), Random.nextInt(4, 9)) // Sumas hasta 16
                    8 -> Pair(10, Random.nextInt(1, 9))                   // Sumar con 10
                    9 -> Pair(Random.nextInt(6, 11), Random.nextInt(5, 10)) // Sumas hasta 20
                    10 -> Pair(Random.nextInt(8, 13), Random.nextInt(6, 12)) // Sumas hasta 24
                    11 -> Pair(Random.nextInt(10, 16), Random.nextInt(5, 14)) // Sumas hasta 28
                    12 -> Pair(Random.nextInt(12, 18), Random.nextInt(8, 16)) // Sumas hasta 32
                    13 -> Pair(Random.nextInt(15, 22), Random.nextInt(10, 20)) // Sumas hasta 40
                    14 -> Pair(Random.nextInt(18, 26), Random.nextInt(12, 25)) // Sumas hasta 50
                    else -> Pair(Random.nextInt(20, 31), Random.nextInt(15, 30)) // Nivel 15: Gran Desafío Maestro
                }
                val target = a + b
                AdventureQuestion(
                    equation = "$a + $b = ?",
                    targetValue = target,
                    prompt = if (safeLevel == 15) "👑 ¡Gran Desafío Maestro! Suma los orbes:" else "¡Nivel $safeLevel/15! Suma para encontrar el orbe:",
                    explanation = "$a más $b suman $target.",
                    hint = "Si tienes $a gemas y encuentras $b más, ¿cuántas tienes?",
                    operationType = operation
                )
            }
            OperationType.SUBTRACTION -> {
                val (a, b) = when (safeLevel) {
                    1 -> Pair(Random.nextInt(2, 5), Random.nextInt(1, 2)) // Restas fáciles de 1
                    2 -> Pair(Random.nextInt(3, 6), Random.nextInt(1, 3)) // Hasta 5
                    3 -> Pair(Random.nextInt(4, 8), Random.nextInt(1, 4)) // Hasta 7
                    4 -> Pair(Random.nextInt(5, 10), Random.nextInt(2, 5)) // Hasta 9
                    5 -> Pair(10, Random.nextInt(1, 9))                   // Restar de 10
                    6 -> Pair(Random.nextInt(8, 13), Random.nextInt(2, 6)) // Hasta 12
                    7 -> Pair(Random.nextInt(10, 15), Random.nextInt(3, 8)) // Hasta 14
                    8 -> Pair(Random.nextInt(12, 17), Random.nextInt(4, 9)) // Hasta 16
                    9 -> Pair(Random.nextInt(14, 19), Random.nextInt(5, 10)) // Hasta 18
                    10 -> Pair(20, Random.nextInt(2, 11))                  // Restar de 20
                    11 -> Pair(Random.nextInt(16, 23), Random.nextInt(5, 12)) // Hasta 22
                    12 -> Pair(Random.nextInt(20, 28), Random.nextInt(6, 14)) // Hasta 27
                    13 -> Pair(Random.nextInt(24, 34), Random.nextInt(8, 16)) // Hasta 33
                    14 -> Pair(Random.nextInt(30, 42), Random.nextInt(10, 20)) // Hasta 40
                    else -> Pair(Random.nextInt(35, 51), Random.nextInt(12, 25)) // Nivel 15: Gran Desafío Maestro
                }
                val target = a - b
                AdventureQuestion(
                    equation = "$a − $b = ?",
                    targetValue = target,
                    prompt = if (safeLevel == 15) "👑 ¡Gran Desafío Maestro! Resta con cuidado:" else "¡Nivel $safeLevel/15! Resta y atrapa el orbe:",
                    explanation = "$a menos $b es igual a $target.",
                    hint = "Comienza en $a y retrocede $b pasos.",
                    operationType = operation
                )
            }
            OperationType.MULTIPLICATION -> {
                val (table, mult) = when (safeLevel) {
                    1 -> Pair(1, Random.nextInt(1, 10)) // Tabla del 1
                    2 -> Pair(2, Random.nextInt(1, 10)) // Tabla del 2
                    3 -> Pair(3, Random.nextInt(1, 10)) // Tabla del 3
                    4 -> Pair(listOf(2, 3).random(), Random.nextInt(1, 10)) // Mixto 2 y 3
                    5 -> Pair(4, Random.nextInt(1, 10)) // Tabla del 4
                    6 -> Pair(5, Random.nextInt(1, 10)) // Tabla del 5
                    7 -> Pair(listOf(4, 5).random(), Random.nextInt(1, 10)) // Mixto 4 y 5
                    8 -> Pair(6, Random.nextInt(1, 10)) // Tabla del 6
                    9 -> Pair(7, Random.nextInt(1, 10)) // Tabla del 7
                    10 -> Pair(8, Random.nextInt(1, 10)) // Tabla del 8
                    11 -> Pair(9, Random.nextInt(1, 10)) // Tabla del 9
                    12 -> Pair(10, Random.nextInt(1, 10)) // Tabla del 10
                    13 -> Pair(Random.nextInt(2, 6), Random.nextInt(2, 10)) // Mixtas tablas 2-5
                    14 -> Pair(Random.nextInt(6, 10), Random.nextInt(2, 10)) // Mixtas tablas 6-9
                    else -> Pair(Random.nextInt(2, 11), Random.nextInt(2, 10)) // Nivel 15: Gran Desafío Maestro
                }
                val target = table * mult
                AdventureQuestion(
                    equation = "$table × $mult = ?",
                    targetValue = target,
                    prompt = if (safeLevel == 15) "👑 ¡Gran Desafío Maestro! Multiplica y triunfa:" else "¡Nivel $safeLevel/15! Multiplica y libera el orbe:",
                    explanation = "$table veces $mult da $target.",
                    hint = "Es equivalente a sumar $table un total de $mult veces.",
                    operationType = operation
                )
            }
            OperationType.DIVISION -> {
                val (divisor, quotient) = when (safeLevel) {
                    1 -> Pair(2, Random.nextInt(1, 6)) // Dividir entre 2 (mitades fáciles)
                    2 -> Pair(2, Random.nextInt(3, 11)) // Dividir entre 2 hasta 20
                    3 -> Pair(3, Random.nextInt(1, 6)) // Dividir entre 3 fácil
                    4 -> Pair(3, Random.nextInt(3, 11)) // Dividir entre 3 hasta 30
                    5 -> Pair(4, Random.nextInt(1, 6)) // Dividir entre 4 fácil
                    6 -> Pair(4, Random.nextInt(3, 11)) // Dividir entre 4 hasta 40
                    7 -> Pair(5, Random.nextInt(1, 11)) // Dividir entre 5
                    8 -> Pair(6, Random.nextInt(1, 10)) // Dividir entre 6
                    9 -> Pair(7, Random.nextInt(1, 10)) // Dividir entre 7
                    10 -> Pair(8, Random.nextInt(1, 10)) // Dividir entre 8
                    11 -> Pair(9, Random.nextInt(1, 10)) // Dividir entre 9
                    12 -> Pair(10, Random.nextInt(1, 10)) // Dividir entre 10
                    13 -> Pair(Random.nextInt(2, 6), Random.nextInt(2, 9)) // Mixtas divisor 2-5
                    14 -> Pair(Random.nextInt(6, 10), Random.nextInt(2, 9)) // Mixtas divisor 6-9
                    else -> Pair(Random.nextInt(2, 11), Random.nextInt(2, 10)) // Nivel 15: Gran Desafío Maestro
                }
                val dividend = divisor * quotient
                AdventureQuestion(
                    equation = "$dividend ÷ $divisor = ?",
                    targetValue = quotient,
                    prompt = if (safeLevel == 15) "👑 ¡Gran Desafío Maestro! Reparte y triunfa:" else "¡Nivel $safeLevel/15! Reparte en partes iguales:",
                    explanation = "$dividend repartido entre $divisor da $quotient.",
                    hint = "¿Qué número multiplicado por $divisor da $dividend?",
                    operationType = operation
                )
            }
        }
    }

    private fun getWorldName(operation: OperationType): String = when (operation) {
        OperationType.ADDITION -> "Valle de las Sumas"
        OperationType.SUBTRACTION -> "Cueva de las Restas"
        OperationType.MULTIPLICATION -> "Templo de la Multiplicación"
        OperationType.DIVISION -> "Bosque de las Divisiones"
    }
}
