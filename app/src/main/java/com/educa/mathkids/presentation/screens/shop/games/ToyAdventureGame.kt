package com.educa.mathkids.presentation.screens.shop.games

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
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
import com.educa.mathkids.presentation.utils.HapticUtil
import com.educa.mathkids.presentation.utils.SoundManager
import com.educa.mathkids.presentation.utils.TtsManager
import kotlin.random.Random

private class AdventurePlatform(
    var x: Float,
    var y: Float,
    val width: Float = 95f,
    val hasCoin: Boolean = true,
    var mathNumber: Int? = null,
    var isCollected: Boolean = false,
    val hasHazard: Boolean = false
)

/**
 * Minijuego 2D: Salto y Escalada Numérica Didáctica (Dinosaurio, Unicornio, Oso, Muñeca, Castillo).
 * Entrena conteo ascendente continuo (1, 2, 3...) y coordinación motriz en plataformas 2D fluidas.
 * Cuenta con 3 VIDAS (❤️❤️❤️). Caer al vacío o tocar una trampa cuesta 1 vida.
 */
@Composable
fun ToyAdventureGame(
    toy: ToyItem,
    isEnglish: Boolean,
    soundManager: SoundManager?,
    ttsManager: TtsManager?,
    hapticUtil: HapticUtil?,
    difficulty: ToyGameDifficulty = ToyGameDifficulty.FACIL,
    onExitGame: () -> Unit
) {
    val context = LocalContext.current
    val random = remember { Random(System.currentTimeMillis()) }

    var lives by remember { mutableIntStateOf(3) }
    var score by remember { mutableIntStateOf(0) }
    var coinsCollectedThisRun by remember { mutableIntStateOf(0) }
    var correctHits by remember { mutableIntStateOf(0) }
    var hasWonGame by remember { mutableStateOf(false) }
    var isGameOver by remember { mutableStateOf(false) }
    var showCelebration by remember { mutableStateOf(false) }
    var damageFlash by remember { mutableStateOf(false) }

    // Misión Matemática Didáctica adaptada a la dificultad
    var currentProblem by remember(difficulty) { mutableStateOf(generateToyMathProblem(difficulty)) }

    // Posición del aventurero
    var playerX by remember { mutableFloatStateOf(160f) }
    var playerY by remember { mutableFloatStateOf(360f) }
    var playerVy by remember { mutableFloatStateOf(0f) }
    var playerVx by remember { mutableFloatStateOf(0f) }
    var isGrounded by remember { mutableStateOf(true) }

    val gravity = 1250f
    val jumpVelocity = -580f
    var facingDirection by remember { mutableFloatStateOf(1f) }

    val leftInteractionSource = remember { MutableInteractionSource() }
    val rightInteractionSource = remember { MutableInteractionSource() }
    val isHoldingLeft by leftInteractionSource.collectIsPressedAsState()
    val isHoldingRight by rightInteractionSource.collectIsPressedAsState()

    val platforms = remember { mutableStateListOf<AdventurePlatform>() }

    fun initPlatforms() {
        platforms.clear()
        currentProblem = generateToyMathProblem(difficulty)
        val distractors = generateToyDistractors(currentProblem.result)
        // Plataforma base inicial
        platforms.add(AdventurePlatform(x = 90f, y = 400f, width = 180f, hasCoin = false))
        // Plataformas ascendentes con números del reto y distractores
        platforms.add(AdventurePlatform(x = 50f, y = 310f, hasCoin = true, mathNumber = currentProblem.result))
        platforms.add(AdventurePlatform(x = 200f, y = 230f, hasCoin = true, mathNumber = distractors.getOrElse(0) { currentProblem.result + 1 }))
        platforms.add(AdventurePlatform(x = 80f, y = 150f, hasCoin = true, mathNumber = distractors.getOrElse(1) { currentProblem.result - 1 }, hasHazard = false))
        platforms.add(AdventurePlatform(x = 220f, y = 70f, hasCoin = true, mathNumber = distractors.getOrElse(2) { currentProblem.result + 2 }))
        platforms.add(AdventurePlatform(x = 90f, y = -10f, hasCoin = true, mathNumber = distractors.getOrElse(3) { currentProblem.result + 3 }))
    }

    LaunchedEffect(Unit) {
        initPlatforms()
        ttsManager?.speak(
            if (isEnglish) "Jump Quest with ${toy.name}! Solve the math problem and jump on the correct platform!"
            else "¡Aventura de Salto con tu ${toy.name}! ¡Resuelve la operación y salta en la plataforma correcta!"
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
        android.graphics.Paint().apply { textSize = 30f }
    }
    val hazardEmojiPaint = remember {
        android.graphics.Paint().apply { textSize = 30f }
    }
    val heroEmojiPaint = remember {
        android.graphics.Paint().apply {
            textSize = 68f
            textAlign = android.graphics.Paint.Align.CENTER
        }
    }
    val platformNumberPaint = remember {
        android.graphics.Paint().apply {
            textSize = 22f
            color = android.graphics.Color.WHITE
            textAlign = android.graphics.Paint.Align.CENTER
            isFakeBoldText = true
        }
    }

    fun restartGame() {
        lives = 3
        score = 0
        coinsCollectedThisRun = 0
        correctHits = 0
        hasWonGame = false
        playerX = 160f
        playerY = 360f
        playerVy = 0f
        playerVx = 0f
        facingDirection = 1f
        isGrounded = true
        isGameOver = false
        damageFlash = false
        initPlatforms()
    }

    fun jump() {
        if (isGrounded && !isGameOver) {
            playerVy = jumpVelocity
            isGrounded = false
            soundManager?.playButtonClick()
            hapticUtil?.vibrateClick()
        }
    }

    fun moveLeft() {
        playerVx = -220f
        facingDirection = -1f
    }

    fun moveRight() {
        playerVx = 220f
        facingDirection = 1f
    }

    fun stopMove() {
        playerVx = 0f
    }

    rememberGameLoop(isRunning = !isGameOver && !hasWonGame) { delta ->
        // 1. Movimiento horizontal continuo, suave y ultra-fluido
        if (isHoldingLeft) {
            playerVx = (playerVx - 1200f * delta).coerceIn(-270f, 270f)
            facingDirection = -1f
        } else if (isHoldingRight) {
            playerVx = (playerVx + 1200f * delta).coerceIn(-270f, 270f)
            facingDirection = 1f
        } else {
            // Fricción exponencial suave
            playerVx *= (1f - 7.0f * delta).coerceIn(0f, 1f)
            if (kotlin.math.abs(playerVx) < 4f) playerVx = 0f
        }

        playerX += playerVx * delta
        playerX = playerX.coerceIn(20f, 340f)

        if (!isGrounded) {
            playerVy += gravity * delta
            playerY += playerVy * delta
        }

        // 2. Colisión con plataformas al caer hacia abajo
        isGrounded = false
        for (plat in platforms) {
            if (playerVy >= 0f &&
                playerX >= (plat.x - 20f) && playerX <= (plat.x + plat.width + 10f) &&
                playerY >= (plat.y - 12f) && playerY <= (plat.y + 16f)
            ) {
                playerY = plat.y - 6f
                playerVy = 0f
                isGrounded = true

                // Peligro en la plataforma
                if (plat.hasHazard) {
                    lives = maxOf(0, lives - 1)
                    damageFlash = true
                    soundManager?.playIncorrect()
                    hapticUtil?.vibrateEncourage()
                    if (lives <= 0) isGameOver = true
                }

                // Moneda / Estrella recogida con resolución didáctica
                if (plat.hasCoin && !plat.isCollected) {
                    plat.isCollected = true
                    if (plat.mathNumber == currentProblem.result) {
                        score += 30
                        coinsCollectedThisRun++
                        correctHits++
                        CoinShopManager.addCoin(context, 1)
                        soundManager?.playReward()
                        hapticUtil?.vibrateClick()
                        ttsManager?.speak(
                            if (isEnglish) "Correct! ${currentProblem.result}!"
                            else "¡Correcto! ¡${currentProblem.result}!"
                        )
                        if (correctHits >= 30) {
                            hasWonGame = true
                        } else {
                            currentProblem = generateToyMathProblem(difficulty)
                            // Actualizar plataformas activas con nuevo objetivo y distractores
                            val uncollected = platforms.filter { it.hasCoin && !it.isCollected }
                            if (uncollected.isNotEmpty()) {
                                val targetPlat = uncollected.first()
                                targetPlat.mathNumber = currentProblem.result
                                val distractors = generateToyDistractors(currentProblem.result)
                                var dIdx = 0
                                uncollected.drop(1).forEach { p ->
                                    p.mathNumber = distractors.getOrElse(dIdx % distractors.size) { currentProblem.result + 2 }
                                    dIdx++
                                }
                            }
                        }
                    } else {
                        score += 5
                        soundManager?.playButtonClick()
                        hapticUtil?.vibrateClick()
                    }
                }
                break
            }
        }

        // 3. Scroll vertical continuo
        if (playerY < 180f) {
            val scrollDiff = (180f - playerY)
            playerY = 180f
            platforms.forEach { it.y += scrollDiff }
            score += 5

            // Reciclar plataformas que salgan por abajo
            val iterator = platforms.iterator()
            while (iterator.hasNext()) {
                val plat = iterator.next()
                if (plat.y > 480f) {
                    iterator.remove()
                }
            }

            // Generar nueva plataforma arriba
            val topPlatformY = platforms.minOfOrNull { it.y } ?: 100f
            if (topPlatformY > 80f) {
                val distractors = generateToyDistractors(currentProblem.result)
                val hasTarget = platforms.any { it.hasCoin && !it.isCollected && it.mathNumber == currentProblem.result }
                val assignedNumber = if (!hasTarget) currentProblem.result else distractors.random()
                platforms.add(
                    AdventurePlatform(
                        x = random.nextFloat() * 220f + 20f,
                        y = topPlatformY - 90f,
                        hasCoin = true,
                        mathNumber = assignedNumber,
                        hasHazard = random.nextFloat() > 0.88f
                    )
                )
            }
        }

        // 4. Caída al abismo (debajo de la pantalla) -> PIERDE 1 VIDA
        if (playerY > 460f) {
            lives = maxOf(0, lives - 1)
            damageFlash = true
            soundManager?.playIncorrect()
            hapticUtil?.vibrateEncourage()

            if (lives <= 0) {
                isGameOver = true
            } else {
                // Respawn en la plataforma más baja activa
                val lowest = platforms.maxByOrNull { it.y }
                if (lowest != null) {
                    playerX = lowest.x + 30f
                    playerY = lowest.y - 20f
                } else {
                    playerX = 160f
                    playerY = 320f
                }
                playerVy = 0f
                isGrounded = true
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            ToyGameTopBar(
                title = if (isEnglish) "Jump Quest" else "Aventura de Salto",
                toyEmoji = toy.emoji,
                lives = lives,
                coins = coinsCollectedThisRun,
                correctHits = correctHits,
                targetHits = 30,
                difficulty = difficulty,
                isEnglish = isEnglish,
                onExitClick = onExitGame
            )

            Spacer(modifier = Modifier.height(8.dp))

            // ── Tarjeta Didáctica de Misión Matemática ──
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFFE1F5FE))
                    .border(1.5.dp, currentProblem.opColor, RoundedCornerShape(14.dp))
                    .padding(horizontal = 14.dp, vertical = 7.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "🔢", fontSize = 17.sp)
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
                    color = Color(0xFF0288D1)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Lienzo 2D de Aventura de Plataformas
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(22.dp))
                    .background(if (damageFlash) Color(0xFFFFEBEE) else Color(0xFFF1F8E9))
                    .border(3.dp, if (damageFlash) SoftRed else GreenPrimary.copy(alpha = 0.6f), RoundedCornerShape(22.dp))
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height

                    // Dibujar plataformas
                    platforms.forEach { plat ->
                        val px = (plat.x / 360f) * w
                        val py = (plat.y / 480f) * h
                        val pw = (plat.width / 360f) * w

                        // Bloque de plataforma de madera/hierba
                        drawRoundRect(
                            color = if (plat.hasHazard) Color(0xFFFF8A80) else Color(0xFF81C784),
                            topLeft = Offset(px, py),
                            size = Size(pw, 16f),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(6f, 6f)
                        )
                        drawRect(
                            color = Color(0xFF8D6E63),
                            topLeft = Offset(px, py + 8f),
                            size = Size(pw, 10f)
                        )

                        // Objeto en plataforma (Moneda numerada o Espina)
                        if (plat.hasCoin && !plat.isCollected) {
                            drawContext.canvas.nativeCanvas.drawText(
                                "🪙",
                                px + pw / 2f - 24f,
                                py - 6f,
                                coinEmojiPaint
                            )
                            if (plat.mathNumber != null) {
                                val badgeColor = Color(0xFF5C6BC0)
                                drawCircle(
                                    color = badgeColor,
                                    radius = 13f,
                                    center = Offset(px + pw / 2f + 14f, py - 18f)
                                )
                                drawContext.canvas.nativeCanvas.drawText(
                                    "${plat.mathNumber}",
                                    px + pw / 2f + 14f,
                                    py - 11f,
                                    platformNumberPaint
                                )
                            }
                        } else if (plat.hasHazard) {
                            drawContext.canvas.nativeCanvas.drawText(
                                "🔥",
                                px + pw / 2f - 14f,
                                py - 6f,
                                hazardEmojiPaint
                            )
                        }
                    }

                    // Dibujar Jugador / Juguete con orientación y balanceo dinámico
                    val heroX = (playerX / 360f) * w
                    val heroY = (playerY / 480f) * h
                    drawContext.canvas.nativeCanvas.save()
                    drawContext.canvas.nativeCanvas.translate(heroX, heroY)
                    // El dinosaurio (🦖) naturalmente mira hacia la izquierda.
                    // Para que siempre camine de frente hacia donde avanza:
                    // - Al avanzar a la DERECHA (facingDirection > 0f): invertimos (-1f) para mirar a la derecha.
                    // - Al avanzar a la IZQUIERDA (facingDirection < 0f): escala normal (1f) para mirar a la izquierda.
                    val leanAngle = (playerVx / 270f * 10f).coerceIn(-12f, 12f)
                    drawContext.canvas.nativeCanvas.rotate(leanAngle)
                    val scaleX = if (facingDirection > 0f) -1f else 1f
                    drawContext.canvas.nativeCanvas.scale(scaleX, 1f)
                    drawContext.canvas.nativeCanvas.drawText(
                        toy.emoji,
                        0f,
                        10f,
                        heroEmojiPaint
                    )
                    drawContext.canvas.nativeCanvas.restore()
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Controles Táctiles Adaptados para Niños (con soporte de mantener presionado)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Izquierda
                Button(
                    onClick = { moveLeft() },
                    interactionSource = leftInteractionSource,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isHoldingLeft) BluePrimary.copy(alpha = 0.85f) else BluePrimary
                    ),
                    shape = RoundedCornerShape(16.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = if (isHoldingLeft) 1.dp else 4.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp)
                ) {
                    Text(text = "⬅️", fontSize = 24.sp)
                }

                // Salto Grande Central
                Button(
                    onClick = { jump() },
                    colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                    shape = RoundedCornerShape(16.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
                    modifier = Modifier
                        .weight(1.5f)
                        .height(56.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "🦘", fontSize = 22.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isEnglish) "JUMP!" else "¡SALTAR!",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FredokaFont,
                            color = Color.White
                        )
                    }
                }

                // Derecha
                Button(
                    onClick = { moveRight() },
                    interactionSource = rightInteractionSource,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isHoldingRight) BluePrimary.copy(alpha = 0.85f) else BluePrimary
                    ),
                    shape = RoundedCornerShape(16.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = if (isHoldingRight) 1.dp else 4.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp)
                ) {
                    Text(text = "➡️", fontSize = 24.sp)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Botón Salir
            Button(
                onClick = onExitGame,
                colors = ButtonDefaults.buttonColors(containerColor = SlateDark),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(42.dp)
            ) {
                Text(
                    text = if (isEnglish) "BACK TO TOYS" else "VOLVER A MIS JUGUETES",
                    fontWeight = FontWeight.Black,
                    fontFamily = FredokaFont,
                    fontSize = 12.5.sp,
                    color = Color.White
                )
            }
        }

        if (isGameOver) {
            ToyGameOverDialog(
                toyEmoji = toy.emoji,
                toyName = toy.name,
                score = score,
                coinsEarned = coinsCollectedThisRun,
                isEnglish = isEnglish,
                onRetry = {
                    lives = 3
                    isGameOver = false
                    damageFlash = false
                    playerY = 360f
                    playerVy = 0f
                    playerVx = 0f
                    isGrounded = true
                },
                onExit = onExitGame
            )
        }

        if (hasWonGame) {
            ToyVictoryDialog(
                toyEmoji = toy.emoji,
                toyName = toy.name,
                score = score,
                coinsEarned = coinsCollectedThisRun,
                isEnglish = isEnglish,
                soundManager = soundManager,
                ttsManager = ttsManager,
                onPlayAgain = {
                    hasWonGame = false
                    correctHits = 0
                    restartGame()
                },
                onExit = onExitGame
            )
        }

        if (showCelebration) {
            ConfettiCelebration(visible = true, onFinished = { showCelebration = false })
        }
    }
}
