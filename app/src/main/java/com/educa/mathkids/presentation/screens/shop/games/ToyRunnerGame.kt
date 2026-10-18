package com.educa.mathkids.presentation.screens.shop.games

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.educa.mathkids.core.game.rememberGameLoop
import com.educa.mathkids.core.shop.CoinShopManager
import com.educa.mathkids.domain.model.shop.ToyItem
import com.educa.mathkids.presentation.components.ConfettiCelebration
import com.educa.mathkids.presentation.theme.BluePrimary
import com.educa.mathkids.presentation.theme.FredokaFont
import com.educa.mathkids.presentation.theme.GreenPrimary
import com.educa.mathkids.presentation.theme.OrangeSecondary
import com.educa.mathkids.presentation.theme.SlateDark
import com.educa.mathkids.presentation.theme.SoftRed
import com.educa.mathkids.presentation.theme.SurfaceWhite
import com.educa.mathkids.presentation.utils.HapticUtil
import com.educa.mathkids.presentation.utils.SoundManager
import com.educa.mathkids.presentation.utils.TtsManager
import kotlin.random.Random

private class RunnerItem(
    var x: Float,
    var y: Float,
    val isCoin: Boolean, // true = moneda u orbe matemático, false = obstáculo
    val mathNumber: Int? = null,
    val isTargetAnswer: Boolean = false,
    var isCollected: Boolean = false
)

private class DustParticle(
    var x: Float,
    var y: Float,
    var radius: Float,
    var alpha: Float,
    val vx: Float,
    val vy: Float
)

/**
 * Minijuego 2D: Carrera y Pista Turbo Matemática con el Juguete favorito.
 * Combina velocidad 2D fluida, física de salto y desafíos didácticos (sumas y restas en carrera).
 * Cuenta con 3 VIDAS (❤️❤️❤️), saltos táctiles, monedas y orbes de respuesta.
 */
@Composable
fun ToyRunnerGame(
    myToys: List<ToyItem> = emptyList(),
    initialToyId: String? = null,
    toy: ToyItem? = null,
    isEnglish: Boolean,
    soundManager: SoundManager?,
    ttsManager: TtsManager?,
    hapticUtil: HapticUtil?,
    difficulty: ToyGameDifficulty = ToyGameDifficulty.FACIL,
    onExitGame: () -> Unit
) {
    val context = LocalContext.current
    val random = remember { Random(System.currentTimeMillis()) }

    val selectedToy = remember(toy, initialToyId, myToys) {
        toy
            ?: myToys.firstOrNull { it.id == initialToyId }
            ?: CoinShopManager.TOYS_CATALOG.firstOrNull { it.id == "toy_carrito" }
            ?: CoinShopManager.TOYS_CATALOG.first()
    }

    var lives by remember { mutableIntStateOf(3) }
    var score by remember { mutableIntStateOf(0) }
    var coinsCollectedThisRun by remember { mutableIntStateOf(0) }
    var correctHits by remember { mutableIntStateOf(0) }
    var hasWonGame by remember { mutableStateOf(false) }
    var showCelebration by remember { mutableStateOf(false) }
    var isGameOver by remember { mutableStateOf(false) }
    var isPaused by remember { mutableStateOf(false) }
    var damageFlash by remember { mutableStateOf(false) }

    // Estados de animación fluida 2D
    var gameTime by remember { mutableFloatStateOf(0f) }
    var heroTiltAngle by remember { mutableFloatStateOf(0f) }
    var ballRotation by remember { mutableFloatStateOf(0f) }
    var trackOffset by remember { mutableFloatStateOf(0f) }
    var hillOffset by remember { mutableFloatStateOf(0f) }
    var dustTimer by remember { mutableFloatStateOf(0f) }
    val particles = remember { mutableStateListOf<DustParticle>() }

    // Misión Matemática Didáctica
    var currentProblem by remember(difficulty) { mutableStateOf(generateToyMathProblem(difficulty)) }
    var targetAnswer by remember { mutableIntStateOf(currentProblem.result) }

    fun generateNewMathMission() {
        currentProblem = generateToyMathProblem(difficulty)
        targetAnswer = currentProblem.result
    }

    // Física del héroe (juguete)
    val groundY = 320f
    var heroY by remember { mutableFloatStateOf(groundY) }
    var heroVy by remember { mutableFloatStateOf(0f) }
    val gravity = 1500f
    val jumpVelocity = -640f
    var isGrounded by remember { mutableStateOf(true) }

    // Objetos que se desplazan (monedas, orbes matemáticos y obstáculos)
    val worldSpeed = 230f
    val items = remember { mutableStateListOf<RunnerItem>() }
    var spawnTimer by remember { mutableFloatStateOf(0f) }

    fun triggerJump() {
        if (isGrounded && !isGameOver && !isPaused) {
            heroVy = jumpVelocity
            isGrounded = false
            soundManager?.playButtonClick()
            hapticUtil?.vibrateClick()
        }
    }

    fun restartGame() {
        lives = 3
        score = 0
        coinsCollectedThisRun = 0
        correctHits = 0
        hasWonGame = false
        items.clear()
        particles.clear()
        heroY = groundY
        heroVy = 0f
        isGrounded = true
        isGameOver = false
        isPaused = false
        damageFlash = false
        heroTiltAngle = 0f
        ballRotation = 0f
        generateNewMathMission()
    }

    LaunchedEffect(selectedToy) {
        generateNewMathMission()
        ttsManager?.speak(
            if (isEnglish) "Turbo Dash with ${selectedToy.name}! Jump to catch the correct answer! 3 lives!"
            else "¡Pista Turbo con tu ${selectedToy.name}! Salta y atrapa la respuesta correcta. ¡Tienes 3 vidas!"
        )
    }

    // Auto-recuperación del destello de daño
    LaunchedEffect(damageFlash) {
        if (damageFlash) {
            kotlinx.coroutines.delay(250)
            damageFlash = false
        }
    }

    // Chris Banes compose-performance: pre-recordar Paints fuera del loop de dibujado
    val coinEmojiPaint = remember {
        android.graphics.Paint().apply { textSize = 28f }
    }
    val hazardEmojiPaint = remember {
        android.graphics.Paint().apply { textSize = 28f }
    }
    val heroEmojiPaint = remember {
        android.graphics.Paint().apply {
            textSize = 68f
            textAlign = android.graphics.Paint.Align.CENTER
        }
    }
    val mathOrbTextPaint = remember {
        android.graphics.Paint().apply {
            textSize = 26f
            color = android.graphics.Color.WHITE
            textAlign = android.graphics.Paint.Align.CENTER
            isFakeBoldText = true
        }
    }

    // Game loop fluido a 60-120 FPS (detenido si está en pausa o game over)
    rememberGameLoop(isRunning = !isGameOver && !isPaused) { delta ->
        val dt = delta.coerceIn(0.001f, 0.033f)
        gameTime += dt

        // 1. Desplazamiento de fondo y pista
        trackOffset = (trackOffset + worldSpeed * dt) % 60f
        hillOffset = (hillOffset + worldSpeed * 0.22f * dt) % 900f

        // 2. Actualizar física del salto y amortiguación
        if (!isGrounded) {
            heroVy += gravity * dt
            heroY += heroVy * dt
            if (heroY >= groundY) {
                heroY = groundY
                heroVy = 0f
                isGrounded = true
                // Efecto de polvo al aterrizar
                repeat(4) {
                    particles.add(
                        DustParticle(
                            x = 85f + (it * 4f) - 6f,
                            y = groundY + 2f,
                            radius = random.nextFloat() * 5f + 4f,
                            alpha = 0.55f,
                            vx = -(random.nextFloat() * 70f + 50f),
                            vy = -(random.nextFloat() * 25f + 10f)
                        )
                    )
                }
            }
        }

        // Inclinación dinámica según trayectoria
        val targetTilt = when {
            !isGrounded && heroVy < -100f -> -12f
            !isGrounded && heroVy > 100f -> 10f
            else -> 0f
        }
        heroTiltAngle += (targetTilt - heroTiltAngle) * (dt * 12f).coerceAtMost(1f)

        if (selectedToy.id == "toy_pelota" || selectedToy.emoji.contains("⚽") || selectedToy.emoji.contains("🪀")) {
            ballRotation += 360f * dt * 2.2f
        }

        // Estela de polvo continua en marcha
        if (isGrounded) {
            dustTimer += dt
            if (dustTimer >= 0.06f) {
                dustTimer = 0f
                particles.add(
                    DustParticle(
                        x = 70f,
                        y = groundY + 4f,
                        radius = random.nextFloat() * 4f + 3f,
                        alpha = 0.45f,
                        vx = -(random.nextFloat() * 110f + 80f),
                        vy = -(random.nextFloat() * 15f + 5f)
                    )
                )
            }
        }

        // Actualizar partículas
        val pIter = particles.iterator()
        while (pIter.hasNext()) {
            val p = pIter.next()
            p.x += p.vx * dt
            p.y += p.vy * dt
            p.radius += 8f * dt
            p.alpha -= 1.2f * dt
            if (p.alpha <= 0f || p.x < -40f) {
                pIter.remove()
            }
        }

        // 3. Mover items hacia la izquierda
        val heroX = 90f
        val heroRadius = 26f

        val iterator = items.iterator()
        while (iterator.hasNext()) {
            val item = iterator.next()
            item.x -= worldSpeed * dt

            // Detección de colisión con el juguete
            if (!item.isCollected && kotlin.math.abs(item.x - heroX) < (heroRadius + 22f)) {
                if (kotlin.math.abs(item.y - heroY) < (heroRadius + 24f)) {
                    item.isCollected = true
                    if (item.mathNumber != null) {
                        if (item.isTargetAnswer) {
                            // ¡Acertó la operación matemática!
                            score += 30
                            coinsCollectedThisRun += 2
                            correctHits++
                            CoinShopManager.addCoin(context, 2)
                            if (correctHits >= 30) {
                                hasWonGame = true
                            }
                            soundManager?.playReward()
                            hapticUtil?.vibrateClick()
                            showCelebration = true
                            ttsManager?.speak(
                                if (isEnglish) "Awesome! $targetAnswer is correct!"
                                else "¡Genial! ¡$targetAnswer es correcto!"
                            )
                            generateNewMathMission()
                        } else {
                            // Tocó un número incorrecto -> Resta vida
                            lives = maxOf(0, lives - 1)
                            damageFlash = true
                            soundManager?.playIncorrect()
                            hapticUtil?.vibrateEncourage()
                            ttsManager?.speak(
                                if (isEnglish) "Oops! Find $targetAnswer!"
                                else "¡Cuidado! Busca el $targetAnswer"
                            )
                            if (lives <= 0) isGameOver = true
                        }
                    } else if (item.isCoin) {
                        // Estrella estándar
                        score += 10
                        coinsCollectedThisRun++
                        CoinShopManager.addCoin(context, 1)
                        soundManager?.playReward()
                        hapticUtil?.vibrateClick()
                        if (coinsCollectedThisRun % 5 == 0) {
                            showCelebration = true
                        }
                    } else {
                        // Daño por obstáculo -> PIERDE 1 VIDA
                        lives = maxOf(0, lives - 1)
                        damageFlash = true
                        soundManager?.playIncorrect()
                        hapticUtil?.vibrateEncourage()

                        if (lives <= 0) {
                            isGameOver = true
                        } else {
                            heroVy = jumpVelocity * 0.45f
                            isGrounded = false
                        }
                    }
                }
            }

            // Eliminar si sale de la pantalla
            if (item.x < -60f) {
                iterator.remove()
            }
        }

        // 3. Generar nuevos items (Orbes didácticos, monedas y obstáculos)
        spawnTimer += dt
        if (spawnTimer >= 1.7f) {
            spawnTimer = 0f
            val roll = random.nextFloat()
            if (roll < 0.48f) {
                // Orbe Didáctico Matemático
                val isTarget = random.nextFloat() > 0.35f
                val orbNum = if (isTarget) targetAnswer else {
                    val deltaNum = if (random.nextBoolean()) 1 else -1
                    maxOf(1, targetAnswer + deltaNum)
                }
                val yPos = groundY - (random.nextFloat() * 105f + 30f)
                items.add(
                    RunnerItem(
                        x = 850f,
                        y = yPos,
                        isCoin = true,
                        mathNumber = orbNum,
                        isTargetAnswer = (orbNum == targetAnswer)
                    )
                )
            } else if (roll < 0.78f) {
                // Moneda / Estrella clásica
                val yPos = groundY - (random.nextFloat() * 100f + 25f)
                items.add(RunnerItem(x = 850f, y = yPos, isCoin = true))
            } else {
                // Obstáculo de tierra
                items.add(RunnerItem(x = 850f, y = groundY, isCoin = false))
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            // Cabecera Unificada con 3 Vidas, Monedas y Botón de Pausa
            ToyGameTopBar(
                title = if (isEnglish) "Turbo Dash" else "Pista Turbo",
                toyEmoji = selectedToy.emoji,
                lives = lives,
                coins = coinsCollectedThisRun,
                correctHits = correctHits,
                targetHits = 30,
                difficulty = difficulty,
                isEnglish = isEnglish,
                onExitClick = onExitGame,
                onPauseClick = {
                    isPaused = true
                    soundManager?.playButtonClick()
                    hapticUtil?.vibrateClick()
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // ── Tarjeta Didáctica de Misión Matemática ──
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFFFFF9C4))
                    .border(1.5.dp, Color(0xFFFFB300), RoundedCornerShape(14.dp))
                    .padding(horizontal = 14.dp, vertical = 7.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "🎯", fontSize = 17.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${currentProblem.opNameEs}: ${currentProblem.num1} ${currentProblem.opSymbol} ${currentProblem.num2} = ?",
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FredokaFont,
                        color = SlateDark
                    )
                }
                Text(
                    text = "🎯 $correctHits/30",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FredokaFont,
                    color = Color(0xFFE65100)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Lienzo del Juego Interactivo 2D
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(22.dp))
                    .background(if (damageFlash) Color(0xFFFFEBEE) else Color(0xFFE1F5FE))
                    .border(3.dp, if (damageFlash) SoftRed else BluePrimary.copy(alpha = 0.5f), RoundedCornerShape(22.dp))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { triggerJump() }
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val canvasWidth = size.width
                    val canvasHeight = size.height

                    // 1. Cielo y nubes animadas con desplazamiento suave
                    val cloud1X = (canvasWidth * 0.25f - (gameTime * 15f) % (canvasWidth + 100f))
                    val cloud2X = (canvasWidth * 0.78f - (gameTime * 22f) % (canvasWidth + 100f))
                    drawCircle(
                        color = Color.White.copy(alpha = 0.75f),
                        radius = 45f,
                        center = Offset(if (cloud1X < -50f) cloud1X + canvasWidth + 100f else cloud1X, 60f)
                    )
                    drawCircle(
                        color = Color.White.copy(alpha = 0.75f),
                        radius = 35f,
                        center = Offset(if (cloud1X < -50f) cloud1X + canvasWidth + 100f + 25f else cloud1X + 25f, 55f)
                    )
                    drawCircle(
                        color = Color.White.copy(alpha = 0.70f),
                        radius = 40f,
                        center = Offset(if (cloud2X < -50f) cloud2X + canvasWidth + 100f else cloud2X, 75f)
                    )

                    // 2. Colinas de fondo con paralaje
                    val hScroll = hillOffset
                    drawOval(
                        color = Color(0xFFA5D6A7).copy(alpha = 0.6f),
                        topLeft = Offset(-80f - (hScroll * 0.4f % (canvasWidth * 0.7f)), canvasHeight - 200f),
                        size = Size(canvasWidth * 0.7f, 220f)
                    )
                    drawOval(
                        color = Color(0xFFA5D6A7).copy(alpha = 0.6f),
                        topLeft = Offset(canvasWidth * 0.6f - (hScroll * 0.4f % (canvasWidth * 0.7f)), canvasHeight - 200f),
                        size = Size(canvasWidth * 0.7f, 220f)
                    )
                    drawOval(
                        color = Color(0xFF81C784).copy(alpha = 0.6f),
                        topLeft = Offset(canvasWidth * 0.45f - (hScroll * 0.7f % (canvasWidth * 0.8f)), canvasHeight - 220f),
                        size = Size(canvasWidth * 0.8f, 240f)
                    )
                    drawOval(
                        color = Color(0xFF81C784).copy(alpha = 0.6f),
                        topLeft = Offset(canvasWidth * 1.25f - (hScroll * 0.7f % (canvasWidth * 0.8f)), canvasHeight - 220f),
                        size = Size(canvasWidth * 0.8f, 240f)
                    )

                    // 3. Suelo y Pista de Carreras con marcas de velocidad
                    val groundCanvasY = canvasHeight - 85f
                    drawRect(
                        color = Color(0xFF5D4037),
                        topLeft = Offset(0f, groundCanvasY),
                        size = Size(canvasWidth, 85f)
                    )
                    drawRect(
                        color = Color(0xFF4CAF50),
                        topLeft = Offset(0f, groundCanvasY),
                        size = Size(canvasWidth, 14f)
                    )
                    // Línea discontinua de pista en movimiento fluido
                    val dashW = 34f
                    val gapW = 26f
                    val dashTotal = dashW + gapW
                    var dx = -trackOffset
                    while (dx < canvasWidth + dashTotal) {
                        drawRoundRect(
                            color = Color(0xFFFFEB3B).copy(alpha = 0.85f),
                            topLeft = Offset(dx, groundCanvasY + 42f),
                            size = Size(dashW, 6f),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(3f, 3f)
                        )
                        dx += dashTotal
                    }

                    // Partículas de polvo y estela de velocidad
                    particles.forEach { p ->
                        val pDrawX = (p.x / 850f) * canvasWidth + 30f
                        val pDrawY = groundCanvasY - (groundY - p.y)
                        drawCircle(
                            color = Color.White.copy(alpha = p.alpha.coerceIn(0f, 1f)),
                            radius = p.radius,
                            center = Offset(pDrawX, pDrawY)
                        )
                    }

                    // 4. Dibujar Items (Orbes Didácticos, Monedas y Obstáculos)
                    items.forEach { item ->
                        if (!item.isCollected) {
                            val itemDrawX = (item.x / 850f) * canvasWidth
                            val orbWave = if (item.mathNumber != null) kotlin.math.sin(gameTime * 6f + item.x * 0.03f) * 5f else 0f
                            val itemDrawY = groundCanvasY - (groundY - item.y) + orbWave

                            if (item.mathNumber != null) {
                                // Orbe Didáctico Matemático (mismo color para todos)
                                val orbRadius = 22f
                                val orbColor = Color(0xFF3F51B5)
                                drawCircle(
                                    color = orbColor.copy(alpha = 0.35f),
                                    radius = orbRadius + 7f,
                                    center = Offset(itemDrawX, itemDrawY)
                                )
                                drawCircle(
                                    color = orbColor,
                                    radius = orbRadius,
                                    center = Offset(itemDrawX, itemDrawY)
                                )
                                drawCircle(
                                    color = Color.White.copy(alpha = 0.50f),
                                    radius = orbRadius * 0.40f,
                                    center = Offset(itemDrawX - 6f, itemDrawY - 6f)
                                )
                                drawContext.canvas.nativeCanvas.drawText(
                                    "${item.mathNumber}",
                                    itemDrawX,
                                    itemDrawY + 9f,
                                    mathOrbTextPaint
                                )
                            } else if (item.isCoin) {
                                drawCircle(
                                    color = Color(0xFFFFD54F),
                                    radius = 16f,
                                    center = Offset(itemDrawX, itemDrawY)
                                )
                                drawCircle(
                                    color = Color(0xFFFFB300),
                                    radius = 12f,
                                    center = Offset(itemDrawX, itemDrawY)
                                )
                                drawContext.canvas.nativeCanvas.drawText(
                                    "⭐",
                                    itemDrawX - 10f,
                                    itemDrawY + 8f,
                                    coinEmojiPaint
                                )
                            } else {
                                // Obstáculo: Bloque o Rocas
                                drawRoundRect(
                                    color = Color(0xFFFF5722),
                                    topLeft = Offset(itemDrawX - 16f, groundCanvasY - 32f),
                                    size = Size(32f, 32f),
                                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(8f, 8f)
                                )
                                drawContext.canvas.nativeCanvas.drawText(
                                    "⚠️",
                                    itemDrawX - 12f,
                                    groundCanvasY - 8f,
                                    hazardEmojiPaint
                                )
                            }
                        }
                    }

                    // 5. Dibujar Juguete del Niño (Héroe) con orientación correcta y suspensión
                    val heroDrawX = (90f / 850f) * canvasWidth + 30f
                    val groundBob = if (isGrounded) kotlin.math.sin(gameTime * 28f) * 1.8f else 0f
                    val heroDrawY = groundCanvasY - (groundY - heroY) + groundBob

                    // Sombra dinámica bajo el juguete
                    val shadowScale = (1f - (groundY - heroY) / 220f).coerceIn(0.25f, 1f)
                    drawOval(
                        color = Color.Black.copy(alpha = 0.22f * shadowScale),
                        topLeft = Offset(heroDrawX - 24f * shadowScale, groundCanvasY - 4f),
                        size = Size(48f * shadowScale, 10f * shadowScale)
                    )

                    // Detectar si el emoji está orientado a la izquierda y voltearlo para que mire a la DERECHA
                    val shouldFlipFacingRight = selectedToy.id in listOf(
                        "toy_carrito", "toy_tren", "toy_patineta", "toy_dron", "toy_dino", "toy_cohete"
                    ) || selectedToy.emoji.contains("🏎") || selectedToy.emoji.contains("🚂") ||
                         selectedToy.emoji.contains("🛹") || selectedToy.emoji.contains("🦖") ||
                         selectedToy.emoji.contains("🚁")

                    val isBall = selectedToy.id == "toy_pelota" || selectedToy.emoji.contains("⚽") || selectedToy.emoji.contains("🪀")

                    val nativeCanvas = drawContext.canvas.nativeCanvas
                    nativeCanvas.save()
                    if (shouldFlipFacingRight) {
                        // Invertir horizontalmente para que mire a la DERECHA
                        nativeCanvas.scale(-1f, 1f, heroDrawX, heroDrawY)
                    }

                    if (isBall) {
                        nativeCanvas.rotate(ballRotation, heroDrawX, heroDrawY)
                    } else {
                        nativeCanvas.rotate(heroTiltAngle, heroDrawX, heroDrawY)
                    }

                    nativeCanvas.drawText(
                        selectedToy.emoji,
                        heroDrawX,
                        heroDrawY + 8f,
                        heroEmojiPaint
                    )
                    nativeCanvas.restore()
                }

                // Instrucción de salto flotante
                Box(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 10.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.Black.copy(alpha = 0.45f))
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = if (isEnglish) "👆 TAP TO JUMP!" else "👆 ¡TOCA LA PANTALLA PARA SALTAR!",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FredokaFont
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Botón Salir
            Button(
                onClick = onExitGame,
                colors = ButtonDefaults.buttonColors(containerColor = SlateDark),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
            ) {
                Text(
                    text = if (isEnglish) "BACK TO TOYS" else "VOLVER A MIS JUGUETES",
                    fontWeight = FontWeight.Black,
                    fontFamily = FredokaFont,
                    fontSize = 13.sp,
                    color = Color.White
                )
            }
        }

        // Diálogo de Pausa
        if (isPaused) {
            ToyGamePauseDialog(
                toyEmoji = selectedToy.emoji,
                toyName = selectedToy.name,
                score = score,
                coins = coinsCollectedThisRun,
                isEnglish = isEnglish,
                onResume = { isPaused = false },
                onRestart = {
                    restartGame()
                    isPaused = false
                },
                onExit = onExitGame
            )
        }

        // Diálogo de Game Over cuando se pierden las 3 vidas
        if (isGameOver) {
            ToyGameOverDialog(
                toyEmoji = selectedToy.emoji,
                toyName = selectedToy.name,
                score = score,
                coinsEarned = coinsCollectedThisRun,
                isEnglish = isEnglish,
                onRetry = {
                    lives = 3
                    isGameOver = false
                    damageFlash = false
                    heroY = groundY
                    heroVy = 0f
                    isGrounded = true
                },
                onExit = onExitGame
            )
        }

        if (hasWonGame) {
            ToyVictoryDialog(
                toyEmoji = selectedToy.emoji,
                toyName = selectedToy.name,
                score = score,
                coinsEarned = coinsCollectedThisRun,
                isEnglish = isEnglish,
                soundManager = soundManager,
                ttsManager = ttsManager,
                onPlayAgain = {
                    hasWonGame = false
                    restartGame()
                },
                onExit = onExitGame
            )
        }

        if (showCelebration) {
            ConfettiCelebration(
                visible = true,
                onFinished = { showCelebration = false }
            )
        }
    }
}
