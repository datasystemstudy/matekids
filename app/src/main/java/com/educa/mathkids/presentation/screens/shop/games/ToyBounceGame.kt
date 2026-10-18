package com.educa.mathkids.presentation.screens.shop.games

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import com.educa.mathkids.presentation.theme.FredokaFont
import com.educa.mathkids.presentation.theme.GreenPrimary
import com.educa.mathkids.presentation.theme.SlateDark
import com.educa.mathkids.presentation.theme.SoftRed
import com.educa.mathkids.presentation.utils.HapticUtil
import com.educa.mathkids.presentation.utils.SoundManager
import com.educa.mathkids.presentation.utils.TtsManager
import kotlin.random.Random

private class BounceSkyItem(
    var x: Float,
    var y: Float,
    val isCoin: Boolean,
    val mathNumber: Int? = null,
    val isTargetAnswer: Boolean = false,
    var isCollected: Boolean = false,
    val speedX: Float = -115f
)

/**
 * Minijuego 2D: Rebote Mágico y Burbujas Didácticas (Pelota, Yoyo, Burbujas, Cometa).
 * Fomenta cálculo mental y control motriz fino guiando el juguete hacia las burbujas correctas.
 * Cuenta con 3 VIDAS (❤️❤️❤️). Caer al suelo o tocar espinas resta 1 vida.
 */
@Composable
fun ToyBounceGame(
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

    var currentProblem by remember(difficulty) { mutableStateOf(generateToyMathProblem(difficulty)) }
    var targetAnswer by remember { mutableIntStateOf(currentProblem.result) }

    fun generateNewMathMission() {
        currentProblem = generateToyMathProblem(difficulty)
        targetAnswer = currentProblem.result
    }

    // Física del juguete
    var ballX by remember { mutableFloatStateOf(200f) }
    var ballY by remember { mutableFloatStateOf(180f) }
    var ballVx by remember { mutableFloatStateOf(0f) }
    var ballVy by remember { mutableFloatStateOf(0f) }

    val gravity = 950f
    val bounceUpSpeed = -550f

    val skyItems = remember { mutableStateListOf<BounceSkyItem>() }
    var spawnTimer by remember { mutableFloatStateOf(0f) }

    fun restartGame() {
        lives = 3
        score = 0
        coinsCollectedThisRun = 0
        correctHits = 0
        hasWonGame = false
        skyItems.clear()
        ballX = 200f
        ballY = 180f
        ballVx = 0f
        ballVy = 0f
        isGameOver = false
        damageFlash = false
        generateNewMathMission()
    }

    LaunchedEffect(toy) {
        generateNewMathMission()
        ttsManager?.speak(
            if (isEnglish) "Magic Bounce with ${toy.name}! Pop the bubble with the correct answer! 3 lives!"
            else "¡Rebote Mágico con tu ${toy.name}! Explota la burbuja con la respuesta correcta. ¡Tienes 3 vidas!"
        )
    }

    // Auto-recuperación del destello de daño
    LaunchedEffect(damageFlash) {
        if (damageFlash) {
            kotlinx.coroutines.delay(250)
            damageFlash = false
        }
    }

    // Chris Banes compose-performance: recordar objetos Paint fuera del loop de dibujado
    val warningPaint = remember {
        android.graphics.Paint().apply {
            textSize = 30f
            color = android.graphics.Color.RED
            isFakeBoldText = true
        }
    }
    val coinEmojiPaint = remember {
        android.graphics.Paint().apply { textSize = 32f }
    }
    val hazardEmojiPaint = remember {
        android.graphics.Paint().apply { textSize = 36f }
    }
    val toyEmojiPaint = remember {
        android.graphics.Paint().apply { textSize = 72f }
    }
    val bubbleNumberPaint = remember {
        android.graphics.Paint().apply {
            textSize = 28f
            color = android.graphics.Color.WHITE
            textAlign = android.graphics.Paint.Align.CENTER
            isFakeBoldText = true
        }
    }
    val warningText = if (isEnglish) "⚠️ DON'T TOUCH THE GROUND! ⚠️" else "⚠️ ¡NO TOQUES EL SUELO! ⚠️"

    rememberGameLoop(isRunning = !isGameOver) { delta ->
        // 1. Aplicar gravedad y movimiento
        ballVy += gravity * delta
        ballX += ballVx * delta
        ballY += ballVy * delta

        // Fricción horizontal
        ballVx *= (1f - 0.8f * delta)

        // Rebotes en paredes laterales
        if (ballX < 40f) {
            ballX = 40f
            ballVx = kotlin.math.abs(ballVx) * 0.7f
        } else if (ballX > 380f) {
            ballX = 380f
            ballVx = -kotlin.math.abs(ballVx) * 0.7f
        }

        // Tocar el suelo -> PIERDE 1 VIDA
        val floorY = 460f
        if (ballY >= floorY) {
            lives = maxOf(0, lives - 1)
            soundManager?.playIncorrect()
            hapticUtil?.vibrateEncourage()
            damageFlash = true

            if (lives <= 0) {
                isGameOver = true
            } else {
                // Rebotar al centro
                ballY = 200f
                ballVy = -300f
                ballVx = 0f
            }
        }

        // 2. Mover items del cielo y detectar colisiones
        val iterator = skyItems.iterator()
        while (iterator.hasNext()) {
            val item = iterator.next()
            item.x += item.speedX * delta

            if (!item.isCollected && kotlin.math.abs(item.x - ballX) < 40f && kotlin.math.abs(item.y - ballY) < 40f) {
                item.isCollected = true
                if (item.mathNumber != null) {
                    if (item.isTargetAnswer) {
                        score += 25
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
                            if (isEnglish) "Great pop! It is $targetAnswer!"
                            else "¡Bien hecho! ¡Es $targetAnswer!"
                        )
                        generateNewMathMission()
                    } else {
                        // Explotó número incorrecto
                        lives = maxOf(0, lives - 1)
                        damageFlash = true
                        soundManager?.playIncorrect()
                        hapticUtil?.vibrateEncourage()
                        ttsManager?.speak(
                            if (isEnglish) "Look for $targetAnswer!"
                            else "¡Busca el $targetAnswer!"
                        )
                        if (lives <= 0) isGameOver = true
                    }
                } else if (item.isCoin) {
                    score += 15
                    coinsCollectedThisRun++
                    CoinShopManager.addCoin(context, 1)
                    soundManager?.playReward()
                    hapticUtil?.vibrateClick()
                    if (coinsCollectedThisRun % 5 == 0) showCelebration = true
                } else {
                    // Espina o nube peligrosa -> PIERDE 1 VIDA
                    lives = maxOf(0, lives - 1)
                    soundManager?.playIncorrect()
                    hapticUtil?.vibrateEncourage()
                    damageFlash = true
                    if (lives <= 0) isGameOver = true
                }
            }

            if (item.x < -60f) {
                iterator.remove()
            }
        }

        // 3. Generar nuevos items flotantes (Burbujas Didácticas, estrellas y espinas)
        spawnTimer += delta
        if (spawnTimer >= 1.7f) {
            spawnTimer = 0f
            val roll = random.nextFloat()
            val itemY = random.nextFloat() * 240f + 70f
            if (roll < 0.50f) {
                // Burbuja Didáctica
                val isTarget = random.nextFloat() > 0.35f
                val orbNum = if (isTarget) targetAnswer else {
                    val deltaNum = if (random.nextBoolean()) 1 else -1
                    maxOf(1, targetAnswer + deltaNum)
                }
                skyItems.add(
                    BounceSkyItem(
                        x = 420f,
                        y = itemY,
                        isCoin = true,
                        mathNumber = orbNum,
                        isTargetAnswer = (orbNum == targetAnswer)
                    )
                )
            } else if (roll < 0.78f) {
                skyItems.add(BounceSkyItem(x = 420f, y = itemY, isCoin = true))
            } else {
                skyItems.add(BounceSkyItem(x = 420f, y = itemY, isCoin = false))
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
                title = if (isEnglish) "Magic Bounce" else "Rebote Mágico",
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

            // ── Tarjeta Didáctica de Misión de Burbujas ──
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFFE8F5E9))
                    .border(1.5.dp, Color(0xFF4CAF50), RoundedCornerShape(14.dp))
                    .padding(horizontal = 14.dp, vertical = 7.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "🫧", fontSize = 17.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${currentProblem.opNameEs}: ${currentProblem.num1} ${currentProblem.opSymbol} ${currentProblem.num2} = ?",
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FredokaFont,
                        color = Color(0xFF1B5E20)
                    )
                }
                Text(
                    text = "🎯 $correctHits/30",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FredokaFont,
                    color = Color(0xFF2E7D32)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Lienzo del juego interactivo
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(22.dp))
                    .background(if (damageFlash) Color(0xFFFFEBEE) else Color(0xFFE8EAF6))
                    .border(3.dp, if (damageFlash) SoftRed else Color(0xFF7986CB), RoundedCornerShape(22.dp))
                    .pointerInput(isGameOver) {
                        detectTapGestures { tapOffset ->
                            if (!isGameOver) {
                                // Impulso al tocar: si toca cerca del juguete o abajo de él, lo lanza arriba
                                val dx = tapOffset.x - ballX
                                ballVy = bounceUpSpeed
                                ballVx = -dx * 1.8f // Impulso en dirección opuesta al toque
                                soundManager?.playButtonClick()
                                hapticUtil?.vibrateClick()
                                score += 2
                            }
                        }
                    }
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height

                    // Nubes decorativas
                    drawCircle(Color.White.copy(alpha = 0.8f), radius = 40f, center = Offset(w * 0.2f, 80f))
                    drawCircle(Color.White.copy(alpha = 0.8f), radius = 50f, center = Offset(w * 0.8f, 110f))

                    // Suelo suave con advertencia (no tocar el suelo)
                    val groundYCanvas = h - 60f
                    drawRect(
                        color = Color(0xFFFFCCBC),
                        topLeft = Offset(0f, groundYCanvas),
                        size = Size(w, 60f)
                    )
                    drawContext.canvas.nativeCanvas.drawText(
                        warningText,
                        w * 0.18f,
                        groundYCanvas + 38f,
                        warningPaint
                    )

                    // Items del cielo
                    skyItems.forEach { item ->
                        if (!item.isCollected) {
                            val ix = (item.x / 420f) * w
                            val iy = (item.y / 500f) * h
                            if (item.mathNumber != null) {
                                // Burbuja Didáctica con Número (mismo color para todas)
                                val bRadius = 24f
                                val bColor = Color(0xFF3F51B5)
                                drawCircle(
                                    color = bColor.copy(alpha = 0.35f),
                                    radius = bRadius + 6f,
                                    center = Offset(ix, iy)
                                )
                                drawCircle(
                                    color = bColor,
                                    radius = bRadius,
                                    center = Offset(ix, iy)
                                )
                                drawCircle(
                                    color = Color.White.copy(alpha = 0.5f),
                                    radius = bRadius * 0.40f,
                                    center = Offset(ix - 7f, iy - 7f)
                                )
                                drawContext.canvas.nativeCanvas.drawText(
                                    "${item.mathNumber}",
                                    ix,
                                    iy + 9f,
                                    bubbleNumberPaint
                                )
                            } else if (item.isCoin) {
                                drawCircle(Color(0xFFFFD54F), radius = 18f, center = Offset(ix, iy))
                                drawContext.canvas.nativeCanvas.drawText(
                                    "⭐",
                                    ix - 12f,
                                    iy + 10f,
                                    coinEmojiPaint
                                )
                            } else {
                                drawContext.canvas.nativeCanvas.drawText(
                                    "🌵",
                                    ix - 14f,
                                    iy + 12f,
                                    hazardEmojiPaint
                                )
                            }
                        }
                    }

                    // Juguete rebotando
                    val jx = (ballX / 420f) * w
                    val jy = (ballY / 500f) * h
                    drawContext.canvas.nativeCanvas.drawText(
                        toy.emoji,
                        jx - 28f,
                        jy + 12f,
                        toyEmojiPaint
                    )
                }

                // Indicador flotante
                Box(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 10.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.Black.copy(alpha = 0.45f))
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = if (isEnglish) "👆 TAP TO BOUNCE!" else "👆 ¡TOCA PARA REBOTAR!",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FredokaFont
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

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
                    ballX = 200f
                    ballY = 180f
                    ballVx = 0f
                    ballVy = 0f
                    damageFlash = false
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
