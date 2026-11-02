package com.educa.mathkids.presentation.screens.shop.games

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.ui.zIndex
import kotlin.math.PI
import kotlin.math.atan
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.educa.mathkids.core.game.rememberGameLoop
import kotlinx.coroutines.launch
import com.educa.mathkids.core.shop.CoinShopManager
import com.educa.mathkids.domain.model.shop.ToyItem
import com.educa.mathkids.presentation.components.ConfettiCelebration
import com.educa.mathkids.presentation.theme.BluePrimary
import com.educa.mathkids.presentation.theme.FredokaFont
import com.educa.mathkids.presentation.theme.GreenPrimary
import com.educa.mathkids.presentation.theme.OrangeSecondary
import com.educa.mathkids.presentation.theme.SlateBorder
import com.educa.mathkids.presentation.theme.SlateDark
import com.educa.mathkids.presentation.theme.SlateMuted
import com.educa.mathkids.presentation.theme.SoftRed
import com.educa.mathkids.presentation.theme.SurfaceWhite
import com.educa.mathkids.presentation.utils.HapticUtil
import com.educa.mathkids.presentation.utils.SoundManager
import com.educa.mathkids.presentation.utils.TtsManager
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.random.Random

// =========================================================================
// 1. CRAYONES: Pinta por Números Interactivo 🖍️ (Arrastra el crayón al número)
// =========================================================================

data class CrayonSector(
    val id: Int,
    val name: String,
    val nameEn: String,
    val icon: String,
    val targetNum: Int,
    val color: Color,
    var isColored: Boolean = false
)

data class CrayonTool(
    val number: Int,
    val color: Color,
    val name: String
)

@Composable
fun CrayonesColorGame(
    toy: ToyItem,
    isEnglish: Boolean,
    soundManager: SoundManager?,
    ttsManager: TtsManager?,
    hapticUtil: HapticUtil?,
    difficulty: ToyGameDifficulty = ToyGameDifficulty.FACIL,
    onExitGame: () -> Unit
) {
    val context = LocalContext.current
    var lives by remember { mutableIntStateOf(3) }
    var score by remember { mutableIntStateOf(0) }
    var coinsEarned by remember { mutableIntStateOf(0) }
    var correctHits by remember { mutableIntStateOf(0) }
    var hasWonGame by remember { mutableStateOf(false) }
    var isPaused by remember { mutableStateOf(false) }
    var isGameOver by remember { mutableStateOf(false) }
    var showConfetti by remember { mutableStateOf(false) }

    val sectorPool = remember(difficulty) {
        if (difficulty == ToyGameDifficulty.FACIL) {
            listOf(
                CrayonSector(1, "Sol Brillante", "Bright Sun", "☀️", 5, Color(0xFFFFD54F)),
                CrayonSector(2, "Cielo Azul", "Blue Sky", "☁️", 3, Color(0xFF81D4FA)),
                CrayonSector(3, "Flor Rosa", "Pink Flower", "🌸", 7, Color(0xFFF48FB1)),
                CrayonSector(4, "Pradera Verde", "Green Meadow", "🌱", 4, Color(0xFFA5D6A7))
            )
        } else {
            listOf(
                CrayonSector(1, "Sol Brillante", "Bright Sun", "☀️", 6, Color(0xFFFFD54F)),
                CrayonSector(2, "Cielo Azul", "Blue Sky", "☁️", 8, Color(0xFF81D4FA)),
                CrayonSector(3, "Flor Roja", "Red Flower", "🌺", 9, Color(0xFFEF5350)),
                CrayonSector(4, "Pradera", "Meadow", "🌱", 4, Color(0xFFA5D6A7)),
                CrayonSector(5, "Arcoíris", "Rainbow", "🌈", 7, Color(0xFFCE93D8)),
                CrayonSector(6, "Mariposa", "Butterfly", "🦋", 3, Color(0xFFFFB74D))
            )
        }
    }

    val sectors = remember(sectorPool) {
        mutableStateListOf<CrayonSector>().apply { addAll(sectorPool) }
    }

    // Bounding boxes para detección de soltado (drag and drop)
    val sectorBounds = remember { mutableStateMapOf<Int, Rect>() }

    // Estado del crayón arrastrado o seleccionado
    var rootOrigin by remember { mutableStateOf(Offset.Zero) }
    val crayonOrigins = remember { mutableStateMapOf<Int, Offset>() }
    var draggedCrayon by remember { mutableStateOf<CrayonTool?>(null) }
    var dragPosition by remember { mutableStateOf(Offset.Zero) }
    var selectedCrayonNum by remember { mutableIntStateOf(-1) }

    val crayonColors = remember {
        listOf(
            Color(0xFFFFD54F), Color(0xFF81D4FA), Color(0xFFF48FB1),
            Color(0xFFA5D6A7), Color(0xFFEF5350), Color(0xFFCE93D8),
            Color(0xFFFFB74D), Color(0xFF4DD0E1), Color(0xFFFF8A65)
        )
    }

    val crayonOptions = remember(sectors.map { it.isColored }) {
        val remaining = sectors.filter { !it.isColored }.map { it.targetNum }
        val pool = if (remaining.isNotEmpty()) remaining else listOf(5, 3, 7, 4)
        val wrong = listOf(1, 2, 6, 8, 9, 10).filter { !pool.contains(it) }.shuffled().take(2)
        val distinctNumbers = (pool + wrong).distinct().shuffled()
        distinctNumbers.mapIndexed { idx, num ->
            CrayonTool(
                number = num,
                color = crayonColors[idx % crayonColors.size],
                name = "Crayón $num"
            )
        }
    }

    fun checkWin() {
        if (sectors.all { it.isColored }) {
            showConfetti = true
            score += 40
            coinsEarned += 5
            CoinShopManager.addCoin(context, 5)
            soundManager?.playVictory()
            ttsManager?.speak(if (isEnglish) "Masterpiece finished! Awesome!" else "¡Obra de arte completada! ¡Excelente!")
            if (correctHits >= 10) {
                hasWonGame = true
            }
        }
    }

    fun handleDropOrTap(droppedCrayonNum: Int, sector: CrayonSector) {
        if (sector.isColored) return

        if (droppedCrayonNum == sector.targetNum) {
            sector.isColored = true
            score += 15
            coinsEarned += 1
            correctHits++
            CoinShopManager.addCoin(context, 1)
            soundManager?.playCorrect()
            hapticUtil?.vibrateClick()
            ttsManager?.speak(if (isEnglish) "Great! ${sector.nameEn} colored!" else "¡Genial! ¡Pintaste ${sector.name}!")
            selectedCrayonNum = -1
            checkWin()
        } else {
            lives = (lives - 1).coerceAtLeast(0)
            soundManager?.playIncorrect()
            hapticUtil?.vibrateEncourage()
            ttsManager?.speak(if (isEnglish) "Oops! Number $droppedCrayonNum doesn't match ${sector.nameEn}" else "¡Ups! El crayón $droppedCrayonNum no va en ${sector.name}")
            if (lives == 0) isGameOver = true
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFFDE7))
            .onGloballyPositioned { rootOrigin = it.positionInRoot() }
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ToyGameTopBar(
                title = if (isEnglish) "Color By Numbers" else "Pinta con Crayones",
                toyEmoji = toy.emoji,
                lives = lives,
                coins = coinsEarned,
                correctHits = correctHits,
                targetHits = 10,
                difficulty = difficulty,
                isEnglish = isEnglish,
                onExitClick = onExitGame,
                onPauseClick = { isPaused = true }
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Lienzo interactivo de pintura donde se colocan los sectores
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .shadow(6.dp, RoundedCornerShape(24.dp))
                    .clip(RoundedCornerShape(24.dp))
                    .background(SurfaceWhite)
                    .border(3.dp, Color(0xFFFFB300), RoundedCornerShape(24.dp))
                    .padding(12.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = if (isEnglish) "🖍️ Drag the crayon to the matching number!" else "🖍️ ¡Arrastra el crayón hasta el número correcto!",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FredokaFont,
                        color = SlateDark,
                        textAlign = TextAlign.Center
                    )

                    // Sectores del dibujo
                    val chunked = sectors.chunked(if (sectors.size <= 4) 2 else 3)
                    chunked.forEach { rowSectors ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            rowSectors.forEach { sector ->
                                val isOver = draggedCrayon != null && sectorBounds[sector.id]?.inflate(35f)?.contains(dragPosition) == true
                                val isSelected = selectedCrayonNum == sector.targetNum

                                Box(
                                    modifier = Modifier
                                        .onGloballyPositioned { coordinates ->
                                            val localPos = coordinates.positionInRoot() - rootOrigin
                                            sectorBounds[sector.id] = Rect(localPos, Size(coordinates.size.width.toFloat(), coordinates.size.height.toFloat()))
                                        }
                                        .scale(if (isOver || isSelected) 1.08f else 1.0f)
                                        .size(if (sectors.size <= 4) 115.dp else 95.dp)
                                        .shadow(if (isOver) 8.dp else 4.dp, RoundedCornerShape(20.dp))
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(if (sector.isColored) sector.color else if (isOver) Color(0xFFFFF9C4) else Color(0xFFF5F5F5))
                                        .border(
                                            width = if (isOver) 3.5.dp else 2.5.dp,
                                            color = if (sector.isColored) sector.color.copy(alpha = 0.9f) else if (isOver) Color(0xFFE65100) else Color(0xFFBDBDBD),
                                            shape = RoundedCornerShape(20.dp)
                                        )
                                        .clickable {
                                            if (selectedCrayonNum != -1) {
                                                handleDropOrTap(selectedCrayonNum, sector)
                                            } else {
                                                ttsManager?.speak(if (isEnglish) "Drag crayon ${sector.targetNum} here!" else "¡Arrastra el crayón con el número ${sector.targetNum} aquí!")
                                            }
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(text = sector.icon, fontSize = if (sectors.size <= 4) 34.sp else 28.sp)
                                        Spacer(modifier = Modifier.height(3.dp))
                                        Text(
                                            text = if (sector.isColored) "✓ ¡Pintado!" else "N° ${sector.targetNum}",
                                            fontSize = if (sectors.size <= 4) 14.sp else 12.sp,
                                            fontWeight = FontWeight.Black,
                                            fontFamily = FredokaFont,
                                            color = if (sector.isColored) Color(0xFF2E7D32) else Color(0xFFE65100)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Text(
                        text = if (isEnglish) "Tip: You can drag or tap the crayons" else "Tip: Puedes arrastrar o tocar los crayones",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = SlateMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Bandeja de crayones arrastrables
            Text(
                text = if (isEnglish) "🖍️ Crayons Tray (Drag or Tap):" else "🖍️ Bandeja de Crayones (Arrastra o Toca):",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FredokaFont,
                color = SlateDark
            )
            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                crayonOptions.forEach { crayon ->
                    val isBeingDragged = draggedCrayon?.number == crayon.number
                    val isSelected = selectedCrayonNum == crayon.number

                    Box(
                        modifier = Modifier
                            .onGloballyPositioned { coordinates ->
                                crayonOrigins[crayon.number] = coordinates.positionInRoot() - rootOrigin
                            }
                            .scale(if (isSelected) 1.15f else 1.0f)
                            .shadow(if (isSelected) 6.dp else 3.dp, RoundedCornerShape(16.dp))
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isSelected) Color(0xFFFFCC80) else SurfaceWhite)
                            .border(2.dp, if (isSelected) Color(0xFFE65100) else crayon.color, RoundedCornerShape(16.dp))
                            .pointerInput(crayon.number) {
                                detectDragGestures(
                                    onDragStart = { offset ->
                                        draggedCrayon = crayon
                                        val origin = crayonOrigins[crayon.number] ?: Offset.Zero
                                        dragPosition = origin + offset
                                        soundManager?.playButtonClick()
                                        hapticUtil?.vibrateClick()
                                    },
                                    onDrag = { change, dragAmount ->
                                        change.consume()
                                        dragPosition += dragAmount
                                    },
                                    onDragEnd = {
                                        val curCrayon = draggedCrayon
                                        if (curCrayon != null) {
                                            // Verificar si se soltó sobre algún sector
                                            val hitSector = sectors.firstOrNull { sec ->
                                                val b = sectorBounds[sec.id]?.inflate(35f)
                                                b != null && b.contains(dragPosition)
                                            }
                                            if (hitSector != null) {
                                                handleDropOrTap(curCrayon.number, hitSector)
                                            }
                                        }
                                        draggedCrayon = null
                                    },
                                    onDragCancel = {
                                        draggedCrayon = null
                                    }
                                )
                            }
                            .clickable {
                                selectedCrayonNum = crayon.number
                                soundManager?.playButtonClick()
                                hapticUtil?.vibrateClick()
                            }
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "🖍️", fontSize = 24.sp)
                            Text(
                                text = "${crayon.number}",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = crayon.color
                            )
                        }
                    }
                }
            }
        }

        // Crayón flotante durante el arrastre con el dedo
        draggedCrayon?.let { activeCrayon ->
            Box(
                modifier = Modifier
                    .offset { IntOffset(dragPosition.x.roundToInt() - 35, dragPosition.y.roundToInt() - 55) }
                    .zIndex(99f)
                    .scale(1.25f)
                    .shadow(10.dp, RoundedCornerShape(16.dp))
                    .clip(RoundedCornerShape(16.dp))
                    .background(SurfaceWhite)
                    .border(3.dp, activeCrayon.color, RoundedCornerShape(16.dp))
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "🖍️", fontSize = 32.sp)
                    Text(
                        text = "${activeCrayon.number}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = activeCrayon.color
                    )
                }
            }
        }

        if (showConfetti) {
            ConfettiCelebration(visible = true, onFinished = {
                showConfetti = false
                sectors.forEach { it.isColored = false }
            })
        }

        if (isGameOver) {
            ToyGameOverDialog(
                toyEmoji = toy.emoji,
                toyName = toy.name,
                score = score,
                coinsEarned = coinsEarned,
                isEnglish = isEnglish,
                onRetry = {
                    lives = 3
                    hasWonGame = false
                    isGameOver = false
                },
                onExit = onExitGame
            )
        }

        if (hasWonGame) {
            ToyVictoryDialog(
                toyEmoji = toy.emoji,
                toyName = toy.name,
                score = score,
                coinsEarned = coinsEarned,
                isEnglish = isEnglish,
                soundManager = soundManager,
                ttsManager = ttsManager,
                onPlayAgain = {
                    hasWonGame = false
                    correctHits = 0
                    lives = 3
                    score = 0
                    coinsEarned = 0
                    isGameOver = false
                    sectors.forEach { it.isColored = false }
                },
                onExit = onExitGame
            )
        }
    }
}

// =========================================================================
// 2. YOYO: Tiro al Blanco Físico y Puntería 🪀 (Péndulo oscilante con lanzamiento)
// =========================================================================
@Composable
fun YoyoTargetGame(
    toy: ToyItem,
    isEnglish: Boolean,
    soundManager: SoundManager?,
    ttsManager: TtsManager?,
    hapticUtil: HapticUtil?,
    difficulty: ToyGameDifficulty = ToyGameDifficulty.FACIL,
    onExitGame: () -> Unit
) {
    val context = LocalContext.current
    val density = LocalDensity.current
    val coroutineScope = rememberCoroutineScope()

    var lives by remember { mutableIntStateOf(3) }
    var score by remember { mutableIntStateOf(0) }
    var coinsEarned by remember { mutableIntStateOf(0) }
    var correctHits by remember { mutableIntStateOf(0) }
    var hasWonGame by remember { mutableStateOf(false) }
    var isGameOver by remember { mutableStateOf(false) }

    var mathQuestion by remember(difficulty) {
        mutableStateOf(if (difficulty == ToyGameDifficulty.FACIL) "3 + 3" else "2 × 3")
    }
    var targetNumber by remember(difficulty) {
        mutableIntStateOf(6)
    }
    var targetOptions by remember { mutableStateOf(listOf(5, 6, 8)) }
    var targetFeedback by remember { mutableStateOf(listOf<Int?>(null, null, null)) }
    var celebrationBanner by remember { mutableStateOf<String?>(null) }

    fun nextQuestion() {
        if (difficulty == ToyGameDifficulty.FACIL) {
            val isPlus = Random.nextBoolean()
            if (isPlus) {
                val a = Random.nextInt(1, 9)
                val b = Random.nextInt(1, 9)
                targetNumber = a + b
                mathQuestion = "$a + $b"
            } else {
                val a = Random.nextInt(4, 16)
                val b = Random.nextInt(1, a)
                targetNumber = a - b
                mathQuestion = "$a - $b"
            }
        } else {
            val isMult = Random.nextBoolean()
            if (isMult) {
                val a = Random.nextInt(2, 7)
                val b = Random.nextInt(2, 6)
                targetNumber = a * b
                mathQuestion = "$a × $b"
            } else {
                val b = Random.nextInt(2, 6)
                val ans = Random.nextInt(2, 7)
                val a = b * ans
                targetNumber = ans
                mathQuestion = "$a ÷ $b"
            }
        }

        // Generar 2 distractores creíbles y diferentes al resultado
        val distractors = mutableSetOf<Int>()
        var offset = 1
        while (distractors.size < 2) {
            val cand1 = targetNumber + offset
            val cand2 = (targetNumber - offset).coerceAtLeast(1)
            if (cand1 != targetNumber && cand1 > 0) distractors.add(cand1)
            if (distractors.size < 2 && cand2 != targetNumber && cand2 > 0) distractors.add(cand2)
            offset++
        }
        val pool = (distractors.toList().take(2) + targetNumber).shuffled()
        targetOptions = pool
        targetFeedback = listOf(null, null, null)
    }

    LaunchedEffect(difficulty) {
        nextQuestion()
    }

    var aimedSlotIndex by remember { mutableIntStateOf(1) }
    var isShooting by remember { mutableStateOf(false) }
    var launchStartX by remember { mutableFloatStateOf(0f) }
    val shotFlightAnim = remember { Animatable(0f) }

    val infiniteTransition = rememberInfiniteTransition(label = "yoyo_effects")
    val spinRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(600, easing = LinearEasing)),
        label = "spin"
    )
    val sightPulse by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(tween(700, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "sight_pulse"
    )

    fun executeShot(slotIndex: Int) {
        if (isShooting || isGameOver || hasWonGame) return
        isShooting = true
        aimedSlotIndex = slotIndex

        coroutineScope.launch {
            // Vuelo del yoyo hacia la diana apuntada
            shotFlightAnim.animateTo(1f, animationSpec = tween(280, easing = FastOutSlowInEasing))
            val chosenValue = targetOptions.getOrElse(slotIndex) { targetNumber }

            if (chosenValue == targetNumber) {
                targetFeedback = targetOptions.mapIndexed { idx, _ -> if (idx == slotIndex) 1 else null }
                soundManager?.playCorrect()
                hapticUtil?.vibrateClick()
                score += 20
                coinsEarned += 1
                correctHits++
                CoinShopManager.addCoin(context, 1)
                val praise = if (isEnglish) {
                    "Bullseye! $mathQuestion = $targetNumber!"
                } else {
                    "¡En el blanco! ¡$mathQuestion = $targetNumber!"
                }
                ttsManager?.speak(praise)
                celebrationBanner = if (isEnglish) "+20 POINTS! 🎯" else "¡+20 PUNTOS! 🎯"

                if (correctHits >= 15) {
                    kotlinx.coroutines.delay(450)
                    hasWonGame = true
                } else {
                    kotlinx.coroutines.delay(650)
                    nextQuestion()
                    aimedSlotIndex = 1
                }
            } else {
                targetFeedback = targetOptions.mapIndexed { idx, _ -> if (idx == slotIndex) -1 else null }
                soundManager?.playIncorrect()
                hapticUtil?.vibrateEncourage()
                lives = (lives - 1).coerceAtLeast(0)
                celebrationBanner = if (isEnglish) "Try again! ❌" else "¡Prueba otra vez! ❌"

                if (lives == 0) {
                    kotlinx.coroutines.delay(500)
                    isGameOver = true
                } else {
                    kotlinx.coroutines.delay(500)
                }
            }

            // El yoyo regresa de un tirón a la mano
            shotFlightAnim.animateTo(0f, animationSpec = tween(220, easing = FastOutSlowInEasing))
            targetFeedback = listOf(null, null, null)
            celebrationBanner = null
            isShooting = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFFE1F5FE),
                        Color(0xFFB3E5FC),
                        Color(0xFFE0F7FA)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ToyGameTopBar(
                title = if (isEnglish) "Yoyo Precision Target" else "Tiro al Blanco con Yoyo",
                toyEmoji = toy.emoji,
                lives = lives,
                coins = coinsEarned,
                correctHits = correctHits,
                targetHits = 15,
                difficulty = difficulty,
                isEnglish = isEnglish,
                onExitClick = onExitGame
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Tarjeta de la Operación Matemática
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 5.dp),
                border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFF0288D1))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp, horizontal = 14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF0288D1))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = if (isEnglish) "🎯 TARGET" else "🎯 RETO",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FredokaFont,
                                color = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "$mathQuestion = ?",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FredokaFont,
                            color = Color(0xFF01579B)
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = celebrationBanner
                            ?: if (isEnglish) "1. Tap a target to AIM 🎯  →  2. Press SHOOT! 🚀"
                            else "1. Toca una diana para APUNTAR 🎯  →  2. ¡Presiona LANZAR! 🚀",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FredokaFont,
                        color = if (celebrationBanner?.contains("+20") == true) Color(0xFF2E7D32)
                        else if (celebrationBanner?.contains("❌") == true) Color(0xFFD32F2F)
                        else Color(0xFF0277BD)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Campo de Tiro con Dianas y Yoyo
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .shadow(6.dp, RoundedCornerShape(24.dp))
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color.White)
                    .border(3.dp, Color(0xFF0288D1), RoundedCornerShape(24.dp))
            ) {
                val arenaW = constraints.maxWidth.toFloat()
                val arenaH = constraints.maxHeight.toFloat()
                val slotXPositions = listOf(
                    arenaW * 0.20f,
                    arenaW * 0.50f,
                    arenaW * 0.80f
                )
                val targetTopY = arenaH * 0.20f
                val yoyoBottomY = arenaH * 0.80f
                val anchorX = arenaW * 0.50f
                val anchorY = arenaH * 0.05f

                val currentAimedX = slotXPositions.getOrElse(aimedSlotIndex) { arenaW * 0.50f }

                // 1. Dianas táctiles superiores para APUNTAR
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = with(density) { (targetTopY - 38.dp.toPx()).toDp() }.coerceAtLeast(6.dp)),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    targetOptions.forEachIndexed { index, value ->
                        val isAimed = aimedSlotIndex == index && !isShooting
                        val feedback = targetFeedback[index]
                        val isCorrectHit = feedback == 1
                        val isWrongHit = feedback == -1

                        val scale by animateFloatAsState(
                            targetValue = when {
                                isCorrectHit -> 1.18f
                                isWrongHit -> 0.90f
                                isAimed -> sightPulse
                                else -> 1.0f
                            },
                            animationSpec = spring(),
                            label = "target_scale_$index"
                        )

                        val borderColor = when {
                            isCorrectHit -> Color(0xFF4CAF50)
                            isWrongHit -> Color(0xFFE53935)
                            isAimed -> Color(0xFFFF9800)
                            else -> Color(0xFF0288D1)
                        }
                        val bgColor = when {
                            isCorrectHit -> Color(0xFFE8F5E9)
                            isWrongHit -> Color(0xFFFFEBEE)
                            isAimed -> Color(0xFFFFF8E1)
                            else -> Color(0xFFE1F5FE)
                        }

                        Box(
                            modifier = Modifier
                                .graphicsLayer {
                                    scaleX = scale
                                    scaleY = scale
                                }
                                .size(78.dp)
                                .shadow(if (isCorrectHit || isAimed) 10.dp else 4.dp, CircleShape)
                                .clip(CircleShape)
                                .background(bgColor)
                                .border(if (isAimed) 4.dp else 3.5.dp, borderColor, CircleShape)
                                .clickable(enabled = !isShooting && !isGameOver && !hasWonGame) {
                                    // APUNTAR a la diana seleccionada
                                    aimedSlotIndex = index
                                    soundManager?.playButtonClick()
                                    hapticUtil?.vibrateClick()
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            // Círculo concéntrico interno de diana
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(CircleShape)
                                    .background(Color.White)
                                    .border(2.dp, borderColor.copy(alpha = 0.5f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = if (isCorrectHit) "✅" else if (isWrongHit) "❌" else if (isAimed) "🎯" else "⚪",
                                        fontSize = 15.sp
                                    )
                                    Text(
                                        text = "$value",
                                        fontSize = 19.sp,
                                        fontWeight = FontWeight.Black,
                                        fontFamily = FredokaFont,
                                        color = if (isCorrectHit) Color(0xFF2E7D32)
                                        else if (isWrongHit) Color(0xFFC62828)
                                        else if (isAimed) Color(0xFFE65100)
                                        else SlateDark
                                    )
                                }
                            }
                        }
                    }
                }

                // 2. Lienzo para línea de mira láser, cuerda y disparo del Yoyo
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val progress = shotFlightAnim.value
                    val currentYoyoX: Float
                    val currentYoyoY: Float

                    if (progress == 0f) {
                        // Modo apuntado estático debajo de la diana seleccionada
                        currentYoyoX = currentAimedX
                        currentYoyoY = yoyoBottomY

                        // Dibujar Línea de Mira Láser hacia la diana apuntada
                        drawLine(
                            color = Color(0xFFFF9800).copy(alpha = 0.65f),
                            start = Offset(currentYoyoX, currentYoyoY - 26.dp.toPx()),
                            end = Offset(currentYoyoX, targetTopY + 36.dp.toPx()),
                            strokeWidth = 3.dp.toPx(),
                            pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(
                                floatArrayOf(18f, 12f),
                                0f
                            ),
                            cap = StrokeCap.Round
                        )
                    } else {
                        // Modo disparo: trayectoria directa hacia la diana elegida
                        val startX = currentAimedX
                        val startY = yoyoBottomY
                        val destX = currentAimedX
                        val destY = targetTopY

                        currentYoyoX = startX + (destX - startX) * progress
                        currentYoyoY = startY + (destY - startY) * progress
                    }

                    // A. Dibujar soporte / anclaje arriba
                    drawCircle(
                        color = Color(0xFFFFB300),
                        radius = 10.dp.toPx(),
                        center = Offset(anchorX, anchorY)
                    )
                    drawCircle(
                        color = Color.White,
                        radius = 5.dp.toPx(),
                        center = Offset(anchorX, anchorY)
                    )

                    // B. Dibujar cuerda tensa del Yoyo desde el anclaje central
                    drawLine(
                        color = Color(0xFFFFD54F),
                        start = Offset(anchorX, anchorY),
                        end = Offset(currentYoyoX, currentYoyoY),
                        strokeWidth = 3.dp.toPx(),
                        cap = StrokeCap.Round
                    )

                    // C. Cuerpo del Yoyo luminoso
                    val yoyoRadius = 24.dp.toPx()
                    drawCircle(
                        color = Color.Black.copy(alpha = 0.15f),
                        radius = yoyoRadius + 2.dp.toPx(),
                        center = Offset(currentYoyoX, currentYoyoY + 3.dp.toPx())
                    )
                    drawCircle(
                        color = Color(0xFF0288D1),
                        radius = yoyoRadius,
                        center = Offset(currentYoyoX, currentYoyoY)
                    )
                    drawCircle(
                        color = Color.White,
                        radius = yoyoRadius,
                        center = Offset(currentYoyoX, currentYoyoY),
                        style = Stroke(width = 3.dp.toPx())
                    )
                    drawCircle(
                        color = Color(0xFF40C4FF),
                        radius = yoyoRadius * 0.65f,
                        center = Offset(currentYoyoX, currentYoyoY)
                    )
                    drawCircle(
                        color = Color(0xFFFFD54F),
                        radius = yoyoRadius * 0.32f,
                        center = Offset(currentYoyoX, currentYoyoY)
                    )

                    val yoyoPaint = android.graphics.Paint().apply {
                        textSize = 28.sp.toPx()
                        textAlign = android.graphics.Paint.Align.CENTER
                        isAntiAlias = true
                    }
                    drawContext.canvas.nativeCanvas.save()
                    drawContext.canvas.nativeCanvas.rotate(spinRotation, currentYoyoX, currentYoyoY)
                    val textY = currentYoyoY - ((yoyoPaint.descent() + yoyoPaint.ascent()) / 2f)
                    drawContext.canvas.nativeCanvas.drawText("🪀", currentYoyoX, textY, yoyoPaint)
                    drawContext.canvas.nativeCanvas.restore()
                }

                // 3. Indicador de puntería activa
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 6.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFFFF3E0))
                        .border(1.5.dp, Color(0xFFFF9800), RoundedCornerShape(12.dp))
                        .padding(horizontal = 14.dp, vertical = 4.dp)
                ) {
                    val aimTargetValue = targetOptions.getOrElse(aimedSlotIndex) { 0 }
                    Text(
                        text = if (isEnglish) "🎯 Aiming at: [#$aimTargetValue]" else "🎯 Apuntando a la diana: [$aimTargetValue]",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FredokaFont,
                        color = Color(0xFFE65100)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Selector Rápido de Apuntado (3 botones táctiles)
            Row(
                modifier = Modifier.fillMaxWidth(0.92f),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                targetOptions.forEachIndexed { idx, valNum ->
                    val isSelected = aimedSlotIndex == idx
                    Button(
                        onClick = {
                            if (!isShooting) {
                                aimedSlotIndex = idx
                                soundManager?.playButtonClick()
                                hapticUtil?.vibrateClick()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSelected) Color(0xFFFF9800) else Color(0xFFE1F5FE),
                            contentColor = if (isSelected) Color.White else Color(0xFF0277BD)
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.weight(1f).height(40.dp)
                    ) {
                        Text(
                            text = "🎯 #$valNum",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FredokaFont
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Botón Gigante de LANZAR
            Button(
                onClick = { executeShot(aimedSlotIndex) },
                enabled = !isShooting && !isGameOver && !hasWonGame,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF0288D1),
                    disabledContainerColor = Color(0xFF90CAF9)
                ),
                shape = RoundedCornerShape(18.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp),
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .height(54.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "🚀", fontSize = 24.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isEnglish) "SHOOT YOYO! 🪀" else "¡LANZAR YOYO! 🪀",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FredokaFont,
                        color = Color.White
                    )
                }
            }
        }

        if (isGameOver) {
            ToyGameOverDialog(
                toyEmoji = toy.emoji,
                toyName = toy.name,
                score = score,
                coinsEarned = coinsEarned,
                isEnglish = isEnglish,
                onRetry = {
                    lives = 3
                    hasWonGame = false
                    isGameOver = false
                },
                onExit = onExitGame
            )
        }

        if (hasWonGame) {
            ToyVictoryDialog(
                toyEmoji = toy.emoji,
                toyName = toy.name,
                score = score,
                coinsEarned = coinsEarned,
                isEnglish = isEnglish,
                soundManager = soundManager,
                ttsManager = ttsManager,
                onPlayAgain = {
                    hasWonGame = false
                    correctHits = 0
                    lives = 3
                    score = 0
                    coinsEarned = 0
                    isGameOver = false
                    nextQuestion()
                },
                onExit = onExitGame
            )
        }
    }
}

// =========================================================================
// 3. MUÑECA: Juego de Memoria de la Muñeca 🪆 (Emparejar accesorios)
// =========================================================================
private data class DollMemoryCard(
    val id: Int,
    val pairId: Int,
    val emoji: String,
    val nameEs: String,
    val nameEn: String
)

@Composable
fun MunecaDressUpGame(
    toy: ToyItem,
    isEnglish: Boolean,
    soundManager: SoundManager?,
    ttsManager: TtsManager?,
    hapticUtil: HapticUtil?,
    difficulty: ToyGameDifficulty = ToyGameDifficulty.FACIL,
    onExitGame: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var lives by remember { mutableIntStateOf(3) }
    var score by remember { mutableIntStateOf(0) }
    var coinsEarned by remember { mutableIntStateOf(0) }
    var correctHits by remember { mutableIntStateOf(0) }
    var hasWonGame by remember { mutableStateOf(false) }
    var isGameOver by remember { mutableStateOf(false) }

    val dollItemsPool = remember {
        listOf(
            Triple("🪆", "Muñeca", "Sweet Doll"),
            Triple("👗", "Vestido", "Pink Dress"),
            Triple("🎀", "Lazo", "Cute Bow"),
            Triple("👒", "Sombrero", "Sun Hat"),
            Triple("👑", "Corona", "Royal Crown"),
            Triple("🧸", "Osito", "Teddy Bear"),
            Triple("👠", "Zapatos", "Sparkle Shoes"),
            Triple("👜", "Cartera", "Pink Purse"),
            Triple("🌸", "Flor", "Spring Flower"),
            Triple("🦄", "Unicornio", "Pony Friend")
        )
    }

    var cards by remember { mutableStateOf<List<DollMemoryCard>>(emptyList()) }
    var revealedCardIds by remember { mutableStateOf<Set<Int>>(emptySet()) }
    var matchedCardIds by remember { mutableStateOf<Set<Int>>(emptySet()) }
    var isChecking by remember { mutableStateOf(false) }
    var dollMoodText by remember { mutableStateOf<String?>(null) }
    var dollDancing by remember { mutableStateOf(false) }

    fun setupNewBoard() {
        val pairCount = if (difficulty == ToyGameDifficulty.FACIL) 3 else 4
        val chosen = dollItemsPool.shuffled().take(pairCount)
        val newCards = mutableListOf<DollMemoryCard>()
        var cardId = 0
        chosen.forEachIndexed { pairIndex, item ->
            newCards.add(DollMemoryCard(cardId++, pairIndex, item.first, item.second, item.third))
            newCards.add(DollMemoryCard(cardId++, pairIndex, item.first, item.second, item.third))
        }
        newCards.shuffle()
        cards = newCards
        revealedCardIds = emptySet()
        matchedCardIds = emptySet()
        isChecking = false
        dollDancing = false
    }

    LaunchedEffect(difficulty) {
        setupNewBoard()
    }

    fun onCardClick(card: DollMemoryCard) {
        if (isChecking || card.id in revealedCardIds || card.id in matchedCardIds || isGameOver || hasWonGame) return

        soundManager?.playButtonClick()
        hapticUtil?.vibrateClick()
        val currentRevealed = revealedCardIds + card.id
        revealedCardIds = currentRevealed

        if (currentRevealed.size == 2) {
            isChecking = true
            val firstCard = cards.first { it.id == currentRevealed.first() }
            val secondCard = cards.first { it.id == currentRevealed.last() }

            if (firstCard.pairId == secondCard.pairId) {
                // MATCH CORRECTO!
                scope.launch {
                    matchedCardIds = matchedCardIds + firstCard.id + secondCard.id
                    revealedCardIds = emptySet()
                    isChecking = false
                    score += 25
                    coinsEarned += 1
                    correctHits++
                    CoinShopManager.addCoin(context, 1)
                    soundManager?.playVictory()
                    hapticUtil?.vibrateClick()
                    dollDancing = true
                    val itemName = if (isEnglish) firstCard.nameEn else firstCard.nameEs
                    dollMoodText = if (isEnglish) "✨ Perfect Match: $itemName! ✨" else "✨ ¡Pareja de $itemName! ✨"
                    ttsManager?.speak(if (isEnglish) "Great! You found the $itemName!" else "¡Genial! ¡Encontraste $itemName!")

                    if (correctHits >= 12) {
                        hasWonGame = true
                    } else if (matchedCardIds.size == cards.size) {
                        kotlinx.coroutines.delay(750)
                        dollMoodText = if (isEnglish) "🎉 Board cleared! Next round!" else "🎉 ¡Tablero completo! ¡Siguiente ronda!"
                        kotlinx.coroutines.delay(650)
                        setupNewBoard()
                    } else {
                        kotlinx.coroutines.delay(1200)
                        dollDancing = false
                    }
                }
            } else {
                // NO COINCIDEN
                scope.launch {
                    kotlinx.coroutines.delay(850)
                    soundManager?.playIncorrect()
                    hapticUtil?.vibrateEncourage()
                    lives = (lives - 1).coerceAtLeast(0)
                    revealedCardIds = emptySet()
                    isChecking = false
                    dollMoodText = if (isEnglish) "Not a match! Try again!" else "¡Casi! ¡Intenta de nuevo!"
                    if (lives == 0) {
                        isGameOver = true
                    }
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFFFCE4EC),
                        Color(0xFFF8BBD0),
                        Color(0xFFF48FB1).copy(alpha = 0.35f)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ToyGameTopBar(
                title = if (isEnglish) "Doll Memory Match" else "Memoria de la Muñeca",
                toyEmoji = toy.emoji,
                lives = lives,
                coins = coinsEarned,
                correctHits = correctHits,
                targetHits = 12,
                difficulty = difficulty,
                isEnglish = isEnglish,
                onExitClick = onExitGame
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Escaparate de la muñeca
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(4.dp, RoundedCornerShape(22.dp))
                    .clip(RoundedCornerShape(22.dp))
                    .background(Color.White)
                    .border(2.5.dp, Color(0xFFF06292), RoundedCornerShape(22.dp))
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Box(
                        modifier = Modifier
                            .size(68.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFCE4EC))
                            .border(2.dp, Color(0xFFE91E63), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (dollDancing) "💃" else "🪆",
                            fontSize = 38.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = dollMoodText ?: if (isEnglish) "Find 2 matching cards for the doll!" else "¡Voltea 2 cartas y encuentra las parejas!",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FredokaFont,
                            color = Color(0xFFC2185B)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "🎀 ${matchedCardIds.size / 2} / ${cards.size / 2} " +
                                        if (isEnglish) "pairs found" else "parejas halladas",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FredokaFont,
                                color = SlateDark
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Rejilla de cartas de memoria
            val cols = if (difficulty == ToyGameDifficulty.FACIL) 3 else 4
            LazyVerticalGrid(
                columns = GridCells.Fixed(cols),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(cards, key = { it.id }) { card ->
                    val isRevealed = card.id in revealedCardIds || card.id in matchedCardIds
                    val isMatched = card.id in matchedCardIds

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(if (cols == 3) 120.dp else 105.dp)
                            .shadow(
                                elevation = if (isRevealed) 5.dp else 3.dp,
                                shape = RoundedCornerShape(18.dp),
                                spotColor = Color(0xFFE91E63).copy(alpha = 0.3f)
                            )
                            .clip(RoundedCornerShape(18.dp))
                            .background(
                                if (isMatched) Color(0xFFE8F5E9)
                                else if (isRevealed) Color.White
                                else Color(0xFFFFF0F5)
                            )
                            .border(
                                width = if (isMatched) 2.5.dp else 2.dp,
                                color = if (isMatched) Color(0xFF4CAF50)
                                else if (isRevealed) Color(0xFFF06292)
                                else Color(0xFFF48FB1),
                                shape = RoundedCornerShape(18.dp)
                            )
                            .clickable(enabled = !isRevealed && !isChecking) { onCardClick(card) },
                        contentAlignment = Alignment.Center
                    ) {
                        if (isRevealed) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(text = card.emoji, fontSize = if (cols == 3) 38.sp else 32.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (isEnglish) card.nameEn else card.nameEs,
                                    fontSize = if (cols == 3) 12.sp else 10.5.sp,
                                    fontWeight = FontWeight.Black,
                                    fontFamily = FredokaFont,
                                    color = if (isMatched) Color(0xFF2E7D32) else Color(0xFFC2185B),
                                    textAlign = TextAlign.Center
                                )
                            }
                        } else {
                            // Dorso de la carta
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(text = "🪆", fontSize = if (cols == 3) 32.sp else 28.sp)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "✨",
                                    fontSize = 14.sp,
                                    color = Color(0xFFF06292)
                                )
                            }
                        }
                    }
                }
            }
        }

        if (isGameOver) {
            ToyGameOverDialog(
                toyEmoji = toy.emoji,
                toyName = toy.name,
                score = score,
                coinsEarned = coinsEarned,
                isEnglish = isEnglish,
                onRetry = {
                    lives = 3
                    hasWonGame = false
                    isGameOver = false
                },
                onExit = onExitGame
            )
        }

        if (hasWonGame) {
            ToyVictoryDialog(
                toyEmoji = toy.emoji,
                toyName = toy.name,
                score = score,
                coinsEarned = coinsEarned,
                isEnglish = isEnglish,
                soundManager = soundManager,
                ttsManager = ttsManager,
                onPlayAgain = {
                    hasWonGame = false
                    correctHits = 0
                    lives = 3
                    score = 0
                    coinsEarned = 0
                    isGameOver = false
                    setupNewBoard()
                },
                onExit = onExitGame
            )
        }
    }
}

// =========================================================================
// 4. BURBUJAS: Pompas Flotantes 2D Matemáticas 🫧 (Suma, Resta, Multiplicación, División)
// =========================================================================
private data class BubbleMathProblem(
    val num1: Int,
    val num2: Int,
    val opSymbol: String,
    val opNameEs: String,
    val opNameEn: String,
    val opColor: Color,
    val result: Int
)

private fun generateBubbleMathProblem(difficulty: ToyGameDifficulty = ToyGameDifficulty.FACIL): BubbleMathProblem {
    val ops = if (difficulty == ToyGameDifficulty.FACIL) listOf(0, 1) else listOf(2, 3)
    return when (ops.random()) {
        0 -> { // Suma
            val a = Random.nextInt(1, 10)
            val b = Random.nextInt(1, 10)
            BubbleMathProblem(a, b, "+", "➕ Suma", "➕ Addition", Color(0xFF1E88E5), a + b)
        }
        1 -> { // Resta
            val b = Random.nextInt(1, 9)
            val res = Random.nextInt(1, 10)
            val a = res + b
            BubbleMathProblem(a, b, "−", "➖ Resta", "➖ Subtraction", Color(0xFF4CAF50), res)
        }
        2 -> { // Multiplicación
            val a = Random.nextInt(2, 6)
            val b = Random.nextInt(1, 6)
            BubbleMathProblem(a, b, "×", "✖️ Multiplicar", "✖️ Multiply", Color(0xFF9C27B0), a * b)
        }
        else -> { // División
            val b = Random.nextInt(2, 6)
            val res = Random.nextInt(1, 6)
            val a = res * b
            BubbleMathProblem(a, b, "÷", "➗ Dividir", "➗ Division", Color(0xFFFF9800), res)
        }
    }
}

private fun generateBubbleDistractor(correct: Int): Int {
    val offsets = listOf(-3, -2, -1, 1, 2, 3, 4, 5).shuffled()
    for (off in offsets) {
        val cand = correct + off
        if (cand > 0 && cand != correct) return cand
    }
    return (correct + 1).coerceAtLeast(1)
}

private class BubbleEntity(
    val id: Long,
    var xFraction: Float,
    var yFraction: Float,
    var speedY: Float,
    var wobbleSpeed: Float,
    var wobbleAmplitude: Float,
    var wobblePhase: Float,
    var radiusFraction: Float,
    var value: Int,
    var tintColor: Color,
    var isPopped: Boolean = false
)

private class BubblePopParticle(
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float,
    var color: Color,
    var alpha: Float = 1f,
    var radius: Float = 6f
)

@Composable
fun BurbujasPopperGame(
    toy: ToyItem,
    isEnglish: Boolean,
    soundManager: SoundManager?,
    ttsManager: TtsManager?,
    hapticUtil: HapticUtil?,
    difficulty: ToyGameDifficulty = ToyGameDifficulty.FACIL,
    onExitGame: () -> Unit
) {
    val context = LocalContext.current
    var lives by remember { mutableIntStateOf(3) }
    var score by remember { mutableIntStateOf(0) }
    var coinsEarned by remember { mutableIntStateOf(0) }
    var correctHits by remember { mutableIntStateOf(0) }
    var hasWonGame by remember { mutableStateOf(false) }
    var isGameOver by remember { mutableStateOf(false) }

    var currentProblem by remember(difficulty) { mutableStateOf(generateBubbleMathProblem(difficulty)) }

    var canvasWidthPx by remember { mutableFloatStateOf(0f) }
    var canvasHeightPx by remember { mutableFloatStateOf(0f) }

    var frameTick by remember { mutableLongStateOf(0L) }
    var gameTime by remember { mutableFloatStateOf(0f) }

    val bubbleTints = remember {
        listOf(
            Color(0xFF00E5FF), // Aqua
            Color(0xFFFF4081), // Rosa
            Color(0xFF7C4DFF), // Morado
            Color(0xFF00E676), // Verde esmeralda
            Color(0xFFFFAB00), // Ámbar brillante
            Color(0xFF40C4FF), // Celeste cielo
            Color(0xFFFF6E40), // Coral
            Color(0xFFAB47BC), // Lavanda
            Color(0xFF26A69A), // Turquesa
            Color(0xFFFFA726)  // Naranja
        )
    }

    // Inicializar 10 burbujas distribuidas en pantalla para empezar con acción continua
    val bubbles = remember {
        val list = mutableListOf<BubbleEntity>()
        for (i in 0 until 10) {
            val initY = 0.12f + (i * 0.09f)
            list.add(
                BubbleEntity(
                    id = i.toLong(),
                    xFraction = 0.08f + (Random.nextFloat() * 0.78f),
                    yFraction = initY,
                    speedY = 0.15f + (Random.nextFloat() * 0.10f),
                    wobbleSpeed = 2.2f + (Random.nextFloat() * 2.0f),
                    wobbleAmplitude = 0.02f + (Random.nextFloat() * 0.03f),
                    wobblePhase = Random.nextFloat() * 6.28f,
                    radiusFraction = 0.095f + (Random.nextFloat() * 0.025f),
                    value = currentProblem.result,
                    tintColor = bubbleTints[i % bubbleTints.size],
                    isPopped = false
                )
            )
        }
        list
    }

    // Asegurar que al inicio y en cada cambio haya al menos 2 burbujas con la respuesta correcta
    LaunchedEffect(currentProblem) {
        val countCorrect = bubbles.count { !it.isPopped && it.value == currentProblem.result }
        if (countCorrect < 2) {
            val unpopped = bubbles.filter { !it.isPopped }.shuffled()
            unpopped.take(2).forEach { it.value = currentProblem.result }
            unpopped.drop(2).forEach {
                if (it.value == currentProblem.result) {
                    it.value = generateBubbleDistractor(currentProblem.result)
                }
            }
        }
    }

    val particles = remember { mutableListOf<BubblePopParticle>() }

    fun spawnBurstParticles(cx: Float, cy: Float, tint: Color) {
        val count = 10
        for (i in 0 until count) {
            val angle = (i * (2 * Math.PI / count) + Random.nextFloat() * 0.4f).toFloat()
            val speed = 140f + (Random.nextFloat() * 180f)
            particles.add(
                BubblePopParticle(
                    x = cx,
                    y = cy,
                    vx = cos(angle) * speed,
                    vy = sin(angle) * speed,
                    color = tint,
                    alpha = 1f,
                    radius = 4f + (Random.nextFloat() * 5f)
                )
            )
        }
    }

    fun onBubblePopped(b: BubbleEntity, cx: Float, cy: Float) {
        if (b.isPopped) return
        b.isPopped = true
        spawnBurstParticles(cx, cy, b.tintColor)

        if (b.value == currentProblem.result) {
            soundManager?.playCorrect()
            hapticUtil?.vibrateClick()
            score += 20
            coinsEarned += 1
            correctHits++
            CoinShopManager.addCoin(context, 1)

            if (correctHits >= 30) {
                hasWonGame = true
            }

            val praise = if (isEnglish) {
                "Awesome! ${currentProblem.num1} ${currentProblem.opSymbol} ${currentProblem.num2} is ${currentProblem.result}!"
            } else {
                "¡Excelente! ¡${currentProblem.num1} ${currentProblem.opSymbol} ${currentProblem.num2} es ${currentProblem.result}!"
            }
            ttsManager?.speak(praise)

            // Siguiente operación matemática variada
            val nextProb = generateBubbleMathProblem(difficulty)
            currentProblem = nextProb

            // Asegurar que burbujas que están subiendo desde abajo tengan el nuevo resultado
            val bottomBubbles = bubbles.filter { !it.isPopped && it.yFraction > 0.4f }.shuffled()
            if (bottomBubbles.isNotEmpty()) {
                bottomBubbles.take(2).forEach { it.value = nextProb.result }
            }
        } else {
            lives = (lives - 1).coerceAtLeast(0)
            soundManager?.playIncorrect()
            hapticUtil?.vibrateEncourage()
            if (lives == 0) {
                isGameOver = true
            }
        }
    }

    // Ciclo de juego a 60 FPS constante
    rememberGameLoop(isRunning = !isGameOver) { delta ->
        gameTime += delta

        // Subir burbujas hacia arriba continuamente
        for (b in bubbles) {
            if (!b.isPopped) {
                b.yFraction -= b.speedY * delta
                // Si la burbuja se va por arriba, reaparece abajo
                if (b.yFraction < -0.15f) {
                    b.yFraction = 1.08f + (Random.nextFloat() * 0.30f)
                    b.xFraction = 0.08f + (Random.nextFloat() * 0.78f)
                    b.isPopped = false
                    val hasCorrect = bubbles.any { !it.isPopped && it.value == currentProblem.result }
                    b.value = if (!hasCorrect || Random.nextFloat() < 0.30f) {
                        currentProblem.result
                    } else {
                        generateBubbleDistractor(currentProblem.result)
                    }
                }
            } else {
                // Reaparecer abajo tras ser reventada
                b.yFraction = 1.15f + (Random.nextFloat() * 0.35f)
                b.xFraction = 0.08f + (Random.nextFloat() * 0.78f)
                b.isPopped = false
                val hasCorrect = bubbles.any { !it.isPopped && it.value == currentProblem.result }
                b.value = if (!hasCorrect || Random.nextFloat() < 0.30f) {
                    currentProblem.result
                } else {
                    generateBubbleDistractor(currentProblem.result)
                }
            }
        }

        // Actualizar física de partículas
        val pIt = particles.iterator()
        while (pIt.hasNext()) {
            val p = pIt.next()
            p.x += p.vx * delta
            p.y += p.vy * delta
            p.vy += 220f * delta
            p.alpha -= 2.6f * delta
            if (p.alpha <= 0f) {
                pIt.remove()
            }
        }

        frameTick++
    }

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFFE0F7FA))) {
        Column(
            modifier = Modifier.fillMaxSize().padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ToyGameTopBar(
                title = if (isEnglish) "Bubble Popper 2D" else "Pompas Flotantes",
                toyEmoji = toy.emoji,
                lives = lives,
                coins = coinsEarned,
                correctHits = correctHits,
                targetHits = 30,
                difficulty = difficulty,
                isEnglish = isEnglish,
                onExitClick = onExitGame
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Tarjeta de la Operación Matemática
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 2.dp),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFF00BCD4))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp, horizontal = 14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(currentProblem.opColor)
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = if (isEnglish) currentProblem.opNameEn else currentProblem.opNameEs,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FredokaFont,
                                color = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "${currentProblem.num1} ${currentProblem.opSymbol} ${currentProblem.num2} = ?",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FredokaFont,
                            color = Color(0xFF006064)
                        )
                    }
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = if (isEnglish) "🫧 Pop the bubble with the correct result!" else "🫧 ¡Explota la pompa con el resultado correcto!",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FredokaFont,
                        color = Color(0xFF00838F)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Área de juego interactiva con físicas 2D y burbujas continuas
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .shadow(6.dp, RoundedCornerShape(24.dp))
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color(0xFFE0F7FA),
                                Color(0xFFB2EBF2),
                                Color(0xFF80DEEA).copy(alpha = 0.6f)
                            )
                        )
                    )
                    .border(3.dp, Color(0xFF00BCD4), RoundedCornerShape(24.dp))
                    .onSizeChanged {
                        canvasWidthPx = it.width.toFloat()
                        canvasHeightPx = it.height.toFloat()
                    }
                    .pointerInput(currentProblem, isGameOver) {
                        if (!isGameOver) {
                            detectTapGestures { tapOffset ->
                                val w = canvasWidthPx.takeIf { it > 0 } ?: return@detectTapGestures
                                val h = canvasHeightPx.takeIf { it > 0 } ?: return@detectTapGestures

                                var closestBubble: BubbleEntity? = null
                                var closestDist = Float.MAX_VALUE
                                var closestCx = 0f
                                var closestCy = 0f

                                for (b in bubbles) {
                                    if (!b.isPopped) {
                                        val cx = (b.xFraction + sin(gameTime * b.wobbleSpeed + b.wobblePhase) * b.wobbleAmplitude) * w
                                        val cy = b.yFraction * h
                                        val r = b.radiusFraction * w
                                        val dx = tapOffset.x - cx
                                        val dy = tapOffset.y - cy
                                        val dist = sqrt(dx * dx + dy * dy)
                                        if (dist <= r * 1.35f && dist < closestDist) {
                                            closestDist = dist
                                            closestBubble = b
                                            closestCx = cx
                                            closestCy = cy
                                        }
                                    }
                                }

                                closestBubble?.let { b ->
                                    onBubblePopped(b, closestCx, closestCy)
                                }
                            }
                        }
                    }
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    @Suppress("UNUSED_VARIABLE")
                    val tick = frameTick
                    val w = size.width
                    val h = size.height

                    // 1. Burbujas de fondo suaves decorativas
                    drawCircle(
                        color = Color.White.copy(alpha = 0.25f),
                        radius = 16f,
                        center = Offset(w * 0.20f, (h * 0.85f - (gameTime * 25f) % h))
                    )
                    drawCircle(
                        color = Color.White.copy(alpha = 0.20f),
                        radius = 24f,
                        center = Offset(w * 0.75f, (h * 0.60f - (gameTime * 35f) % h))
                    )
                    drawCircle(
                        color = Color.White.copy(alpha = 0.18f),
                        radius = 12f,
                        center = Offset(w * 0.50f, (h * 0.35f - (gameTime * 20f) % h))
                    )

                    // 2. Dibujar burbujas de jabón nacaradas con números
                    for (b in bubbles) {
                        if (!b.isPopped && b.yFraction in -0.15f..1.15f) {
                            val cx = (b.xFraction + sin(gameTime * b.wobbleSpeed + b.wobblePhase) * b.wobbleAmplitude) * w
                            val cy = b.yFraction * h
                            val r = b.radiusFraction * w

                            // Resplandor exterior suave
                            drawCircle(
                                color = b.tintColor.copy(alpha = 0.25f),
                                radius = r + 4f,
                                center = Offset(cx, cy)
                            )

                            // Cuerpo de burbuja con reflejo radial
                            drawCircle(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        Color.White.copy(alpha = 0.40f),
                                        b.tintColor.copy(alpha = 0.30f),
                                        b.tintColor.copy(alpha = 0.65f),
                                        Color.White.copy(alpha = 0.85f)
                                    ),
                                    center = Offset(cx - r * 0.25f, cy - r * 0.25f),
                                    radius = r
                                ),
                                radius = r,
                                center = Offset(cx, cy)
                            )

                            // Borde brillante de jabón
                            drawCircle(
                                color = Color.White.copy(alpha = 0.85f),
                                radius = r,
                                center = Offset(cx, cy),
                                style = Stroke(width = 2.5f)
                            )

                            // Brillo curvo superior
                            drawCircle(
                                color = Color.White.copy(alpha = 0.80f),
                                radius = r * 0.22f,
                                center = Offset(cx - r * 0.38f, cy - r * 0.38f)
                            )
                            // Reflejo secundario inferior
                            drawCircle(
                                color = Color.White.copy(alpha = 0.45f),
                                radius = r * 0.10f,
                                center = Offset(cx + r * 0.38f, cy + r * 0.38f)
                            )

                            // Número nítido y legible en el centro
                            val numText = "${b.value}"
                            val textPaint = android.graphics.Paint().apply {
                                textSize = r * 0.76f
                                color = android.graphics.Color.argb(255, 15, 35, 65)
                                typeface = android.graphics.Typeface.DEFAULT_BOLD
                                textAlign = android.graphics.Paint.Align.CENTER
                                isAntiAlias = true
                                setShadowLayer(6f, 0f, 2f, android.graphics.Color.WHITE)
                            }
                            val textY = cy - ((textPaint.descent() + textPaint.ascent()) / 2f)
                            drawContext.canvas.nativeCanvas.drawText(numText, cx, textY, textPaint)
                        }
                    }

                    // 3. Partículas de explosión (gotitas de agua)
                    for (p in particles) {
                        drawCircle(
                            color = p.color.copy(alpha = p.alpha.coerceIn(0f, 1f)),
                            radius = p.radius,
                            center = Offset(p.x, p.y)
                        )
                        drawCircle(
                            color = Color.White.copy(alpha = p.alpha.coerceIn(0f, 1f)),
                            radius = p.radius * 0.4f,
                            center = Offset(p.x, p.y)
                        )
                    }
                }
            }
        }

        if (isGameOver) {
            ToyGameOverDialog(
                toyEmoji = toy.emoji,
                toyName = toy.name,
                score = score,
                coinsEarned = coinsEarned,
                isEnglish = isEnglish,
                onRetry = {
                    lives = 3
                    hasWonGame = false
                    isGameOver = false
                },
                onExit = onExitGame
            )
        }

        if (hasWonGame) {
            ToyVictoryDialog(
                toyEmoji = toy.emoji,
                toyName = toy.name,
                score = score,
                coinsEarned = coinsEarned,
                isEnglish = isEnglish,
                soundManager = soundManager,
                ttsManager = ttsManager,
                onPlayAgain = {
                    lives = 3
                    score = 0
                    coinsEarned = 0
                    correctHits = 0
                    hasWonGame = false
                    currentProblem = generateBubbleMathProblem(difficulty)
                    for ((idx, b) in bubbles.withIndex()) {
                        b.yFraction = 0.12f + (idx * 0.09f)
                        b.xFraction = 0.08f + (Random.nextFloat() * 0.78f)
                        b.isPopped = false
                        b.value = if (idx < 2) currentProblem.result else generateBubbleDistractor(currentProblem.result)
                    }
                },
                onExit = onExitGame
            )
        }
    }
}

// =========================================================================
// 5. COMETA: Vuelo y Navegación en el Viento 🪁 (Altitud y corrientes de aire)
// =========================================================================
private class SkyBirdTarget(
    val id: Int,
    var xFraction: Float,
    var yFraction: Float,
    val value: Int,
    val isCorrect: Boolean,
    val birdEmoji: String,
    var isHitOrCollected: Boolean = false
)

private fun generateSkyBirdWave(probResult: Int): List<SkyBirdTarget> {
    val birdSpecies = listOf("🕊️", "🦅", "🦜", "🐦")
    val distractors = mutableListOf<Int>()
    var offset = 1
    while (distractors.size < 2) {
        val c1 = probResult + offset
        val c2 = (probResult - offset).coerceAtLeast(1)
        if (c1 != probResult && !distractors.contains(c1)) distractors.add(c1)
        if (c2 != probResult && !distractors.contains(c2) && c2 > 0) distractors.add(c2)
        offset++
    }
    distractors.shuffle()

    val altitudes = listOf(0.22f, 0.50f, 0.76f).shuffled()
    val correctIndex = Random.nextInt(3)
    val xSpacings = listOf(1.15f, 1.55f, 1.95f)

    val result = mutableListOf<SkyBirdTarget>()
    for (i in 0..2) {
        val isCorr = i == correctIndex
        val v = if (isCorr) probResult else if (i == (correctIndex + 1) % 3) distractors[0] else distractors[1]
        val birdEmoji = birdSpecies.random()
        result.add(
            SkyBirdTarget(
                id = i,
                xFraction = xSpacings[i],
                yFraction = altitudes[i],
                value = v,
                isCorrect = isCorr,
                birdEmoji = birdEmoji
            )
        )
    }
    return result
}

@Composable
fun CometaSkyGliderGame(
    toy: ToyItem,
    isEnglish: Boolean,
    soundManager: SoundManager?,
    ttsManager: TtsManager?,
    hapticUtil: HapticUtil?,
    difficulty: ToyGameDifficulty = ToyGameDifficulty.FACIL,
    onExitGame: () -> Unit
) {
    val context = LocalContext.current
    val density = LocalDensity.current

    var lives by remember { mutableIntStateOf(3) }
    var score by remember { mutableIntStateOf(0) }
    var coinsEarned by remember { mutableIntStateOf(0) }
    var correctHits by remember { mutableIntStateOf(0) }
    var hasWonGame by remember { mutableStateOf(false) }
    var isGameOver by remember { mutableStateOf(false) }

    var kiteYFraction by remember { mutableFloatStateOf(0.50f) }
    var targetKiteYFraction by remember { mutableFloatStateOf(0.50f) }
    var kiteXFraction by remember { mutableFloatStateOf(0.20f) }
    var targetKiteXFraction by remember { mutableFloatStateOf(0.20f) }

    var gameTime by remember { mutableFloatStateOf(0f) }
    var damageFlash by remember { mutableStateOf(false) }
    var celebrationMessage by remember { mutableStateOf<String?>(null) }

    var mathQuestion by remember(difficulty) {
        mutableStateOf(if (difficulty == ToyGameDifficulty.FACIL) "2 + 2" else "2 × 2")
    }
    var targetNumber by remember(difficulty) { mutableIntStateOf(4) }

    val birds = remember { mutableStateListOf<SkyBirdTarget>() }

    fun nextQuestion() {
        if (difficulty == ToyGameDifficulty.FACIL) {
            val isPlus = Random.nextBoolean()
            if (isPlus) {
                val a = Random.nextInt(1, 8)
                val b = Random.nextInt(1, 8)
                targetNumber = a + b
                mathQuestion = "$a + $b"
            } else {
                val a = Random.nextInt(4, 15)
                val b = Random.nextInt(1, a)
                targetNumber = a - b
                mathQuestion = "$a - $b"
            }
        } else {
            val isMul = Random.nextBoolean()
            if (isMul) {
                val a = Random.nextInt(2, 6)
                val b = Random.nextInt(2, 5)
                targetNumber = a * b
                mathQuestion = "$a × $b"
            } else {
                val b = Random.nextInt(2, 5)
                val ans = Random.nextInt(2, 6)
                val a = b * ans
                targetNumber = ans
                mathQuestion = "$a ÷ $b"
            }
        }
    }

    fun spawnNewWaveSafe(ans: Int) {
        val newWave = generateSkyBirdWave(ans)
        birds.clear()
        birds.addAll(newWave)
    }

    LaunchedEffect(difficulty) {
        nextQuestion()
    }

    LaunchedEffect(targetNumber) {
        spawnNewWaveSafe(targetNumber)
    }

    LaunchedEffect(damageFlash) {
        if (damageFlash) {
            kotlinx.coroutines.delay(220)
            damageFlash = false
        }
    }

    val flySpeed = 0.32f

    rememberGameLoop(isRunning = !isGameOver && !hasWonGame) { delta ->
        gameTime += delta

        // Física suave y fluida del planeador cometa
        val lerpSpeed = 9.5f * delta
        kiteYFraction += (targetKiteYFraction - kiteYFraction) * lerpSpeed.coerceAtMost(1f)
        kiteXFraction += (targetKiteXFraction - kiteXFraction) * lerpSpeed.coerceAtMost(1f)

        var needsReset = false

        for (b in birds) {
            b.xFraction -= flySpeed * delta

            // Detección de colisión entre la cometa y el ave
            if (!b.isHitOrCollected && kotlin.math.abs(b.xFraction - kiteXFraction) < 0.11f && kotlin.math.abs(b.yFraction - kiteYFraction) < 0.16f) {
                b.isHitOrCollected = true

                if (b.isCorrect) {
                    score += 25
                    coinsEarned += 1
                    correctHits++
                    CoinShopManager.addCoin(context, 1)
                    soundManager?.playVictory()
                    hapticUtil?.vibrateClick()
                    celebrationMessage = "+25 ⭐ ¡EN EL BLANCO!"

                    val praise = if (isEnglish) {
                        "Great glide! $mathQuestion is $targetNumber!"
                    } else {
                        "¡Gran vuelo! ¡$mathQuestion es $targetNumber!"
                    }
                    ttsManager?.speak(praise)

                    if (correctHits >= 15) {
                        hasWonGame = true
                    } else {
                        nextQuestion()
                    }
                    break
                } else {
                    lives = (lives - 1).coerceAtLeast(0)
                    damageFlash = true
                    soundManager?.playIncorrect()
                    hapticUtil?.vibrateEncourage()
                    celebrationMessage = if (isEnglish) "Wrong bird! ❌" else "¡Ave incorrecta! ❌"
                    if (lives == 0) isGameOver = true
                }
            }
        }

        if (needsReset || birds.all { it.xFraction < -0.25f || it.isHitOrCollected }) {
            spawnNewWaveSafe(targetNumber)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFFE3F2FD),
                        Color(0xFFBBDEFB),
                        Color(0xFFE1F5FE)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ToyGameTopBar(
                title = if (isEnglish) "Wind Kite Glider" else "Vuelo de Cometa y Aves",
                toyEmoji = toy.emoji,
                lives = lives,
                coins = coinsEarned,
                correctHits = correctHits,
                targetHits = 15,
                difficulty = difficulty,
                isEnglish = isEnglish,
                onExitClick = onExitGame
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Tarjeta de la Operación Matemática
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 5.dp),
                border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFF1976D2))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp, horizontal = 14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF1976D2))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = if (isEnglish) "🪁 FLIGHT TARGET" else "🪁 RETO EN EL AIRE",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FredokaFont,
                                color = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "$mathQuestion = ?",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FredokaFont,
                            color = Color(0xFF0D47A1)
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = celebrationMessage
                            ?: if (isEnglish) "🪁 Slide finger to glide & catch the bird with #${targetNumber}!"
                            else "🪁 ¡Desliza el dedo con fluidez y atrapa el ave con el #${targetNumber}!",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FredokaFont,
                        color = if (celebrationMessage?.contains("+25") == true) Color(0xFF2E7D32)
                        else if (celebrationMessage?.contains("❌") == true) Color(0xFFD32F2F)
                        else Color(0xFF1565C0)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Campo Aéreo Interactivo con Control Táctil Fluido
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .shadow(6.dp, RoundedCornerShape(24.dp))
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        if (damageFlash) Brush.verticalGradient(listOf(Color(0xFFFFCDD2), Color(0xFFFFEBEE)))
                        else Brush.verticalGradient(
                            listOf(
                                Color(0xFF81D4FA),
                                Color(0xFFB3E5FC),
                                Color(0xFFE1F5FE)
                            )
                        )
                    )
                    .border(3.dp, Color(0xFF1976D2), RoundedCornerShape(24.dp))
                    .pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            val h = size.height.toFloat()
                            val w = size.width.toFloat()
                            targetKiteYFraction = (targetKiteYFraction + dragAmount.y / h).coerceIn(0.12f, 0.86f)
                            targetKiteXFraction = (targetKiteXFraction + dragAmount.x / w).coerceIn(0.10f, 0.40f)
                        }
                    }
                    .pointerInput(Unit) {
                        detectTapGestures { offset ->
                            val h = size.height.toFloat()
                            targetKiteYFraction = (offset.y / h).coerceIn(0.12f, 0.86f)
                        }
                    }
            ) {
                val arenaW = constraints.maxWidth.toFloat()
                val arenaH = constraints.maxHeight.toFloat()

                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height

                    // 1. Nubes atmosféricas flotantes en el fondo
                    val cloud1X = ((gameTime * 20f) % (w + 140f)) - 70f
                    val cloud2X = (((gameTime * 14f) + w * 0.5f) % (w + 140f)) - 70f
                    drawCircle(Color.White.copy(alpha = 0.45f), radius = 34.dp.toPx(), center = Offset(cloud1X, h * 0.16f))
                    drawCircle(Color.White.copy(alpha = 0.45f), radius = 28.dp.toPx(), center = Offset(cloud1X + 28.dp.toPx(), h * 0.14f))
                    drawCircle(Color.White.copy(alpha = 0.35f), radius = 30.dp.toPx(), center = Offset(cloud2X, h * 0.32f))

                    // 2. Líneas de viento que representan corrientes de aire
                    val windOffset = (gameTime * 240f) % (w + 120f)
                    drawLine(
                        color = Color.White.copy(alpha = 0.30f),
                        start = Offset(w - windOffset, h * 0.30f),
                        end = Offset(w - windOffset + 60.dp.toPx(), h * 0.30f),
                        strokeWidth = 2.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                    drawLine(
                        color = Color.White.copy(alpha = 0.25f),
                        start = Offset(w - ((windOffset + 140f) % (w + 120f)), h * 0.65f),
                        end = Offset(w - ((windOffset + 140f) % (w + 120f)) + 80.dp.toPx(), h * 0.65f),
                        strokeWidth = 2.dp.toPx(),
                        cap = StrokeCap.Round
                    )

                    // 3. Dibujar AVES VOLANDO CON SU NÚMERO DENTRO
                    for (b in birds) {
                        if (b.xFraction in -0.3f..1.4f && !b.isHitOrCollected) {
                            val birdX = b.xFraction * w
                            val wingFlap = kotlin.math.sin(gameTime * 12f + (b.id * 2f)) * 4.dp.toPx()
                            val birdY = (b.yFraction * h) + wingFlap

                            val badgeRadius = 24.dp.toPx()

                            // Medallón / Círculo en el ave con el número (mismo color para todos)
                            drawCircle(
                                color = Color.White,
                                radius = badgeRadius,
                                center = Offset(birdX, birdY)
                            )
                            drawCircle(
                                color = Color(0xFF1976D2),
                                radius = badgeRadius,
                                center = Offset(birdX, birdY),
                                style = Stroke(width = 2.5.dp.toPx())
                            )

                            // Ave emoji volando justo arriba del círculo
                            val birdPaint = android.graphics.Paint().apply {
                                textSize = 28.sp.toPx()
                                textAlign = android.graphics.Paint.Align.CENTER
                                isAntiAlias = true
                            }
                            drawContext.canvas.nativeCanvas.drawText(b.birdEmoji, birdX, birdY - 14.dp.toPx(), birdPaint)

                            // Número visible en el centro del medallón del ave (mismo color azul marino para todos)
                            val numPaint = android.graphics.Paint().apply {
                                textSize = 20.sp.toPx()
                                color = android.graphics.Color.argb(255, 21, 101, 192)
                                typeface = android.graphics.Typeface.DEFAULT_BOLD
                                textAlign = android.graphics.Paint.Align.CENTER
                                isAntiAlias = true
                            }
                            val tY = birdY + 4.dp.toPx() - ((numPaint.descent() + numPaint.ascent()) / 2f)
                            drawContext.canvas.nativeCanvas.drawText("${b.value}", birdX, tY, numPaint)
                        }
                    }

                    // 4. Dibujar la Cometa del Jugador con Física de Viento
                    val kX = kiteXFraction * w
                    val windSway = kotlin.math.sin(gameTime * 4.5f) * 4.dp.toPx()
                    val kY = (kiteYFraction * h) + windSway
                    val kiteTilt = ((targetKiteYFraction - kiteYFraction) * 55f) + (kotlin.math.sin(gameTime * 3.5f) * 6f)

                    // Cuerda de la cometa hasta la esquina inferior izquierda
                    val stringAnchorX = w * 0.05f
                    val stringAnchorY = h * 0.98f
                    drawLine(
                        color = Color(0xFFFFD54F).copy(alpha = 0.85f),
                        start = Offset(stringAnchorX, stringAnchorY),
                        end = Offset(kX, kY + 8.dp.toPx()),
                        strokeWidth = 2.dp.toPx(),
                        cap = StrokeCap.Round
                    )

                    // Cola de la cometa con lazos de colores ondeando en el viento
                    val tailColors = listOf(Color(0xFFFF4081), Color(0xFFFFEE58), Color(0xFF00E5FF), Color(0xFFAB47BC))
                    tailColors.forEachIndexed { idx, col ->
                        val bowDelayX = kX - ((idx + 1) * 14.dp.toPx())
                        val bowDelayY = kY + 18.dp.toPx() + (kotlin.math.sin(gameTime * 5f - (idx * 0.6f)) * 8.dp.toPx())
                        drawCircle(
                            color = col,
                            radius = 4.5.dp.toPx(),
                            center = Offset(bowDelayX, bowDelayY)
                        )
                        drawLine(
                            color = Color.White.copy(alpha = 0.7f),
                            start = if (idx == 0) Offset(kX, kY + 8.dp.toPx()) else Offset(kX - (idx * 14.dp.toPx()), bowDelayY),
                            end = Offset(bowDelayX, bowDelayY),
                            strokeWidth = 1.5.dp.toPx()
                        )
                    }

                    // Cuerpo de la cometa rotando con el viento
                    val kitePaint = android.graphics.Paint().apply {
                        textSize = 46.sp.toPx()
                        textAlign = android.graphics.Paint.Align.CENTER
                        isAntiAlias = true
                    }
                    drawContext.canvas.nativeCanvas.save()
                    drawContext.canvas.nativeCanvas.rotate(kiteTilt, kX, kY)
                    val kiteTextY = kY - ((kitePaint.descent() + kitePaint.ascent()) / 2f)
                    drawContext.canvas.nativeCanvas.drawText("🪁", kX, kiteTextY, kitePaint)
                    drawContext.canvas.nativeCanvas.restore()
                }

                // Indicador flotante táctil
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(10.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(alpha = 0.85f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (isEnglish) "💨 Drag on sky to glide smoothly" else "💨 Desliza tu dedo en el cielo para planear",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FredokaFont,
                        color = Color(0xFF0277BD)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Controles de Altitud con Respuesta Fluida
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = {
                        targetKiteYFraction = (targetKiteYFraction - 0.22f).coerceAtLeast(0.12f)
                        soundManager?.playButtonClick()
                        hapticUtil?.vibrateClick()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2)),
                    shape = RoundedCornerShape(18.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 5.dp),
                    modifier = Modifier.weight(1f).height(54.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "⬆️", fontSize = 22.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isEnglish) "CLIMB UP" else "SUBIR",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FredokaFont,
                            color = Color.White
                        )
                    }
                }

                Button(
                    onClick = {
                        targetKiteYFraction = (targetKiteYFraction + 0.22f).coerceAtMost(0.86f)
                        soundManager?.playButtonClick()
                        hapticUtil?.vibrateClick()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D47A1)),
                    shape = RoundedCornerShape(18.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 5.dp),
                    modifier = Modifier.weight(1f).height(54.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "⬇️", fontSize = 22.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isEnglish) "DIVE DOWN" else "BAJAR",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FredokaFont,
                            color = Color.White
                        )
                    }
                }
            }
        }

        if (isGameOver) {
            ToyGameOverDialog(
                toyEmoji = toy.emoji,
                toyName = toy.name,
                score = score,
                coinsEarned = coinsEarned,
                isEnglish = isEnglish,
                onRetry = {
                    lives = 3
                    hasWonGame = false
                    isGameOver = false
                    kiteYFraction = 0.5f
                    targetKiteYFraction = 0.5f
                    spawnNewWaveSafe(targetNumber)
                },
                onExit = onExitGame
            )
        }

        if (hasWonGame) {
            ToyVictoryDialog(
                toyEmoji = toy.emoji,
                toyName = toy.name,
                score = score,
                coinsEarned = coinsEarned,
                isEnglish = isEnglish,
                soundManager = soundManager,
                ttsManager = ttsManager,
                onPlayAgain = {
                    hasWonGame = false
                    correctHits = 0
                    lives = 3
                    score = 0
                    coinsEarned = 0
                    isGameOver = false
                    kiteYFraction = 0.5f
                    targetKiteYFraction = 0.5f
                    nextQuestion()
                    spawnNewWaveSafe(targetNumber)
                },
                onExit = onExitGame
            )
        }
    }
}

// =========================================================================
// 6. UNICORNIO: Galope, Saltos y Esquive Matemático 🦄 (Suma, Resta, Multiplicación, División)
// =========================================================================
private data class PonyMathProblem(
    val num1: Int,
    val num2: Int,
    val opSymbol: String,
    val opNameEs: String,
    val opNameEn: String,
    val opColor: Color,
    val result: Int
)

private fun generatePonyMathProblem(difficulty: ToyGameDifficulty = ToyGameDifficulty.FACIL): PonyMathProblem {
    val ops = if (difficulty == ToyGameDifficulty.FACIL) listOf(0, 1) else listOf(2, 3)
    return when (ops.random()) {
        0 -> { // Suma
            val a = Random.nextInt(1, 10)
            val b = Random.nextInt(1, 10)
            PonyMathProblem(a, b, "+", "➕ Suma", "➕ Addition", Color(0xFF1E88E5), a + b)
        }
        1 -> { // Resta
            val b = Random.nextInt(1, 9)
            val res = Random.nextInt(1, 10)
            val a = res + b
            PonyMathProblem(a, b, "−", "➖ Resta", "➖ Subtraction", Color(0xFF4CAF50), res)
        }
        2 -> { // Multiplicación
            val a = Random.nextInt(2, 6)
            val b = Random.nextInt(1, 6)
            PonyMathProblem(a, b, "×", "✖️ Multiplicar", "✖️ Multiply", Color(0xFF9C27B0), a * b)
        }
        else -> { // División
            val b = Random.nextInt(2, 6)
            val res = Random.nextInt(1, 6)
            val a = res * b
            PonyMathProblem(a, b, "÷", "➗ Dividir", "➗ Division", Color(0xFFFF9800), res)
        }
    }
}

private enum class PonyObstacleType {
    STAR_TARGET,         // Gema / Estrella en el aire con respuesta CORRECTA (se atrapa saltando)
    GROUND_HURDLE,       // Valla en el suelo con número distractor (se debe esquivar saltando)
    AIR_DISTRACTOR       // Estrella flotante con número INCORRECTO (se debe evitar)
}

private class PonyObstacle(
    var xFraction: Float,
    val type: PonyObstacleType,
    var value: Int,
    var isHitOrCollected: Boolean = false,
    var hasScored: Boolean = false
)

private fun createPonyWave(prob: PonyMathProblem): List<PonyObstacle> {
    val distractors = mutableListOf<Int>()
    var off = 1
    while (distractors.size < 3) {
        val c1 = prob.result + off
        val c2 = (prob.result - off).coerceAtLeast(1)
        if (c1 != prob.result && !distractors.contains(c1)) distractors.add(c1)
        if (c2 != prob.result && !distractors.contains(c2) && c2 > 0) distractors.add(c2)
        off++
    }
    distractors.shuffle()

    // Seleccionar aleatoriamente cuál de los 3 puestos lleva la estrella correcta
    val correctSlot = Random.nextInt(3)
    val offsets = listOf(1.15f, 1.65f, 2.15f)
    val result = mutableListOf<PonyObstacle>()

    for (i in 0..2) {
        if (i == correctSlot) {
            result.add(PonyObstacle(xFraction = offsets[i], type = PonyObstacleType.STAR_TARGET, value = prob.result))
        } else if (i == (correctSlot + 1) % 3) {
            result.add(PonyObstacle(xFraction = offsets[i], type = PonyObstacleType.GROUND_HURDLE, value = distractors[0]))
        } else {
            result.add(PonyObstacle(xFraction = offsets[i], type = PonyObstacleType.AIR_DISTRACTOR, value = distractors[1]))
        }
    }
    return result
}

@Composable
fun UnicornioRainbowDashGame(
    toy: ToyItem,
    isEnglish: Boolean,
    soundManager: SoundManager?,
    ttsManager: TtsManager?,
    hapticUtil: HapticUtil?,
    difficulty: ToyGameDifficulty = ToyGameDifficulty.FACIL,
    onExitGame: () -> Unit
) {
    val context = LocalContext.current
    val density = LocalDensity.current

    var lives by remember { mutableIntStateOf(3) }
    var score by remember { mutableIntStateOf(0) }
    var coinsEarned by remember { mutableIntStateOf(0) }
    var correctHits by remember { mutableIntStateOf(0) }
    var hasWonGame by remember { mutableStateOf(false) }
    var isGameOver by remember { mutableStateOf(false) }

    var currentProblem by remember(difficulty) { mutableStateOf(generatePonyMathProblem(difficulty)) }

    var ponyY by remember { mutableFloatStateOf(0f) }
    var ponyVy by remember { mutableFloatStateOf(0f) }
    var jumpCount by remember { mutableIntStateOf(0) }
    var gameTime by remember { mutableFloatStateOf(0f) }
    var frameTick by remember { mutableLongStateOf(0L) }
    var damageFlash by remember { mutableStateOf(false) }
    var celebrationText by remember { mutableStateOf<String?>(null) }

    val obstacles = remember {
        mutableStateListOf<PonyObstacle>().apply {
            addAll(createPonyWave(currentProblem))
        }
    }

    fun spawnNewWaveSafe(prob: PonyMathProblem) {
        val newItems = createPonyWave(prob)
        obstacles.clear()
        obstacles.addAll(newItems)
    }

    LaunchedEffect(damageFlash) {
        if (damageFlash) {
            kotlinx.coroutines.delay(220)
            damageFlash = false
        }
    }

    val jumpVelocityPx = with(density) { 560.dp.toPx() }
    val doubleJumpVelocityPx = with(density) { 480.dp.toPx() }
    val gravityPx = with(density) { 1350.dp.toPx() }

    fun triggerJump() {
        if (isGameOver || hasWonGame) return
        if (ponyY >= -10f) {
            // Primer salto desde el suelo
            ponyVy = -jumpVelocityPx
            jumpCount = 1
            soundManager?.playButtonClick()
            hapticUtil?.vibrateClick()
        } else if (jumpCount == 1) {
            // Doble salto en el aire (aleteo mágico)
            ponyVy = -doubleJumpVelocityPx
            jumpCount = 2
            soundManager?.playButtonClick()
            hapticUtil?.vibrateClick()
        }
    }

    val runSpeed = 0.36f

    rememberGameLoop(isRunning = !isGameOver && !hasWonGame) { delta ->
        gameTime += delta

        // Física del salto del poni
        ponyVy += gravityPx * delta
        ponyY += ponyVy * delta
        if (ponyY > 0f) {
            ponyY = 0f
            ponyVy = 0f
            jumpCount = 0
        }

        val ponyX = 0.22f

        // Mover obstáculos de derecha a izquierda sin borrarlos al acertar
        for (obs in obstacles) {
            obs.xFraction -= runSpeed * delta

            // Detección de colisión cuando el obstáculo pasa por la posición del poni
            if (!obs.isHitOrCollected && kotlin.math.abs(obs.xFraction - ponyX) < 0.08f) {
                when (obs.type) {
                    PonyObstacleType.STAR_TARGET -> {
                        // Estrella correcta flotante en el aire
                        if (ponyY < -35f * density.density) {
                            obs.isHitOrCollected = true
                            score += 25
                            coinsEarned += 1
                            correctHits++
                            CoinShopManager.addCoin(context, 1)
                            soundManager?.playVictory()
                            hapticUtil?.vibrateClick()
                            celebrationText = "+25 ⭐"

                            val praise = if (isEnglish) {
                                "Great jump! ${currentProblem.num1} ${currentProblem.opSymbol} ${currentProblem.num2} is ${currentProblem.result}!"
                            } else {
                                "¡Gran salto! ¡${currentProblem.num1} ${currentProblem.opSymbol} ${currentProblem.num2} es ${currentProblem.result}!"
                            }
                            ttsManager?.speak(praise)

                            if (correctHits >= 15) {
                                hasWonGame = true
                            }
                            // No reseteamos la oleada aquí; los demás obstáculos siguen su curso para esquivarlos
                        }
                    }
                    PonyObstacleType.GROUND_HURDLE -> {
                        // Valla en el suelo con número incorrecto
                        if (ponyY > -30f * density.density) {
                            // Chocó con la valla en el suelo
                            obs.isHitOrCollected = true
                            lives = (lives - 1).coerceAtLeast(0)
                            damageFlash = true
                            soundManager?.playIncorrect()
                            hapticUtil?.vibrateEncourage()
                            if (lives == 0) isGameOver = true
                        } else {
                            // El poni saltó por encima exitosamente; no la ocultamos para que siga viéndose
                            if (!obs.hasScored) {
                                obs.hasScored = true
                                score += 5
                            }
                        }
                    }
                    PonyObstacleType.AIR_DISTRACTOR -> {
                        // Estrella incorrecta en el aire
                        if (ponyY < -35f * density.density) {
                            obs.isHitOrCollected = true
                            lives = (lives - 1).coerceAtLeast(0)
                            damageFlash = true
                            soundManager?.playIncorrect()
                            hapticUtil?.vibrateEncourage()
                            if (lives == 0) isGameOver = true
                        }
                    }
                }
            }
        }

        // Generar nueva oleada solo cuando TODOS los obstáculos hayan salido por la izquierda
        if (obstacles.all { it.xFraction < -0.25f }) {
            if (!hasWonGame && !isGameOver) {
                currentProblem = generatePonyMathProblem(difficulty)
                spawnNewWaveSafe(currentProblem)
            }
        }

        frameTick++
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFFF3E5F5),
                        Color(0xFFEDE7F6),
                        Color(0xFFFCE4EC)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ToyGameTopBar(
                title = if (isEnglish) "Rainbow Pony Dash" else "Pony Saltador 2D",
                toyEmoji = toy.emoji,
                lives = lives,
                coins = coinsEarned,
                correctHits = correctHits,
                targetHits = 15,
                difficulty = difficulty,
                isEnglish = isEnglish,
                onExitClick = onExitGame
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Tarjeta de la Operación Matemática
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 2.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 5.dp),
                border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFFBA68C8))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp, horizontal = 14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(currentProblem.opColor)
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = if (isEnglish) currentProblem.opNameEn else currentProblem.opNameEs,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FredokaFont,
                                color = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "${currentProblem.num1} ${currentProblem.opSymbol} ${currentProblem.num2} = ?",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FredokaFont,
                            color = Color(0xFF4A148C)
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (isEnglish) "🦄 Jump to catch #${currentProblem.result} & dodge hurdles!"
                        else "🦄 ¡Salta para atrapar #${currentProblem.result} y esquiva vallas!",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FredokaFont,
                        color = Color(0xFF7B1FA2)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Lienzo 2D de la Carrera y Salto del Poni
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .shadow(6.dp, RoundedCornerShape(24.dp))
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        if (damageFlash) Brush.linearGradient(listOf(Color(0xFFFFCDD2), Color(0xFFFFCDD2)))
                        else Brush.verticalGradient(
                            listOf(
                                Color(0xFFE1BEE7),
                                Color(0xFFF8BBD0),
                                Color(0xFFFFF9C4)
                            )
                        )
                    )
                    .border(3.dp, Color(0xFFBA68C8), RoundedCornerShape(24.dp))
                    .clickable { triggerJump() }
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    @Suppress("UNUSED_VARIABLE")
                    val tick = frameTick
                    val w = size.width
                    val h = size.height
                    val groundY = h * 0.74f

                    // 1. Nubes de fondo animadas
                    val cloud1X = ((gameTime * 24f) % (w + 140f)) - 70f
                    val cloud2X = (((gameTime * 16f) + w * 0.5f) % (w + 140f)) - 70f
                    drawCircle(Color.White.copy(alpha = 0.55f), radius = 32.dp.toPx(), center = Offset(cloud1X, h * 0.18f))
                    drawCircle(Color.White.copy(alpha = 0.55f), radius = 24.dp.toPx(), center = Offset(cloud1X + 26.dp.toPx(), h * 0.16f))
                    drawCircle(Color.White.copy(alpha = 0.45f), radius = 28.dp.toPx(), center = Offset(cloud2X, h * 0.28f))

                    // 2. Camino de Arcoíris animado
                    val rainbowStripeH = 7.dp.toPx()
                    val rainbowColors = listOf(
                        Color(0xFFFF5252),
                        Color(0xFFFFAB40),
                        Color(0xFFFFEE58),
                        Color(0xFF69F0AE),
                        Color(0xFF40C4FF),
                        Color(0xFF7C4DFF)
                    )
                    rainbowColors.forEachIndexed { idx, col ->
                        drawRect(
                            color = col,
                            topLeft = Offset(0f, groundY + (idx * rainbowStripeH)),
                            size = Size(w, rainbowStripeH)
                        )
                    }
                    // Pradera debajo del arcoíris
                    drawRect(
                        color = Color(0xFF81C784),
                        topLeft = Offset(0f, groundY + (rainbowColors.size * rainbowStripeH)),
                        size = Size(w, h - groundY)
                    )

                    // 3. Obstáculos y Metas en movimiento
                    for (obs in obstacles) {
                        val obsX = obs.xFraction * w
                        if (obs.xFraction in -0.3f..1.4f && !obs.isHitOrCollected) {
                            when (obs.type) {
                                PonyObstacleType.STAR_TARGET -> {
                                    // Gema / Estrella dorada flotante (meta correcta que se atrapa saltando)
                                    val gemY = groundY - 80.dp.toPx()
                                    val gemRadius = 28.dp.toPx()

                                    // Halo brillante
                                    drawCircle(
                                        color = Color(0xFFFFD54F).copy(alpha = 0.45f),
                                        radius = gemRadius + 6.dp.toPx(),
                                        center = Offset(obsX, gemY)
                                    )
                                    // Círculo gema
                                    drawCircle(
                                        color = Color(0xFFFFD54F),
                                        radius = gemRadius,
                                        center = Offset(obsX, gemY)
                                    )
                                    drawCircle(
                                        color = Color.White,
                                        radius = gemRadius,
                                        center = Offset(obsX, gemY),
                                        style = Stroke(width = 3.dp.toPx())
                                    )

                                    val starPaint = android.graphics.Paint().apply {
                                        textSize = 18.sp.toPx()
                                        textAlign = android.graphics.Paint.Align.CENTER
                                        isAntiAlias = true
                                    }
                                    drawContext.canvas.nativeCanvas.drawText("⭐", obsX, gemY - 24.dp.toPx(), starPaint)

                                    val textPaint = android.graphics.Paint().apply {
                                        textSize = 22.sp.toPx()
                                        color = android.graphics.Color.argb(255, 60, 20, 90)
                                        typeface = android.graphics.Typeface.DEFAULT_BOLD
                                        textAlign = android.graphics.Paint.Align.CENTER
                                        isAntiAlias = true
                                    }
                                    val tY = gemY - ((textPaint.descent() + textPaint.ascent()) / 2f)
                                    drawContext.canvas.nativeCanvas.drawText("${obs.value}", obsX, tY, textPaint)
                                }
                                PonyObstacleType.GROUND_HURDLE -> {
                                    // Valla en el suelo con número incorrecto
                                    val hurdleW = 46.dp.toPx()
                                    val hurdleH = 40.dp.toPx()
                                    val hurdleY = groundY - hurdleH

                                    drawRoundRect(
                                        color = Color(0xFFE53935),
                                        topLeft = Offset(obsX - (hurdleW / 2f), hurdleY),
                                        size = Size(hurdleW, hurdleH),
                                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(10.dp.toPx(), 10.dp.toPx())
                                    )
                                    drawRoundRect(
                                        color = Color.White,
                                        topLeft = Offset(obsX - (hurdleW / 2f), hurdleY),
                                        size = Size(hurdleW, hurdleH),
                                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(10.dp.toPx(), 10.dp.toPx()),
                                        style = Stroke(width = 2.5.dp.toPx())
                                    )

                                    val numPaint = android.graphics.Paint().apply {
                                        textSize = 19.sp.toPx()
                                        color = android.graphics.Color.WHITE
                                        typeface = android.graphics.Typeface.DEFAULT_BOLD
                                        textAlign = android.graphics.Paint.Align.CENTER
                                        isAntiAlias = true
                                    }
                                    val tY = (hurdleY + (hurdleH / 2f)) - ((numPaint.descent() + numPaint.ascent()) / 2f)
                                    drawContext.canvas.nativeCanvas.drawText("${obs.value}", obsX, tY, numPaint)

                                    val warnPaint = android.graphics.Paint().apply {
                                        textSize = 14.sp.toPx()
                                        textAlign = android.graphics.Paint.Align.CENTER
                                    }
                                    drawContext.canvas.nativeCanvas.drawText("⚠️", obsX, hurdleY - 4.dp.toPx(), warnPaint)
                                }
                                PonyObstacleType.AIR_DISTRACTOR -> {
                                    // Estrella flotante con distractor
                                    val gemY = groundY - 80.dp.toPx()
                                    val gemRadius = 24.dp.toPx()

                                    drawCircle(
                                        color = Color(0xFFAB47BC).copy(alpha = 0.85f),
                                        radius = gemRadius,
                                        center = Offset(obsX, gemY)
                                    )
                                    drawCircle(
                                        color = Color.White,
                                        radius = gemRadius,
                                        center = Offset(obsX, gemY),
                                        style = Stroke(width = 2.dp.toPx())
                                    )
                                    val distPaint = android.graphics.Paint().apply {
                                        textSize = 19.sp.toPx()
                                        color = android.graphics.Color.WHITE
                                        typeface = android.graphics.Typeface.DEFAULT_BOLD
                                        textAlign = android.graphics.Paint.Align.CENTER
                                        isAntiAlias = true
                                    }
                                    val tY = gemY - ((distPaint.descent() + distPaint.ascent()) / 2f)
                                    drawContext.canvas.nativeCanvas.drawText("${obs.value}", obsX, tY, distPaint)
                                }
                            }
                        }
                    }

                    // 4. Dibujar al Unicornio galopando / saltando
                    val pX = 0.22f * w
                    val runBob = if (ponyY >= 0f) kotlin.math.sin(gameTime * 14f) * 4.dp.toPx() else 0f
                    val pY = groundY + ponyY + runBob

                    // Estela de partículas de arcoíris detrás del poni
                    val bubbleColors = listOf(Color(0xFFFF4081), Color(0xFF00E5FF), Color(0xFFFFEE58), Color(0xFFE040FB))
                    bubbleColors.forEachIndexed { idx, col ->
                        val bOffset = (idx + 1) * 14.dp.toPx()
                        val bRadius = (7 - idx).coerceAtLeast(3).dp.toPx()
                        drawCircle(
                            color = col.copy(alpha = 0.65f),
                            radius = bRadius,
                            center = Offset(pX - bOffset, pY + (idx * 4.dp.toPx()))
                        )
                    }

                    val ponyPaint = android.graphics.Paint().apply {
                        textSize = 68.sp.toPx()
                        textAlign = android.graphics.Paint.Align.CENTER
                        isAntiAlias = true
                    }
                    // Invertir horizontalmente para que el poni mire hacia la derecha (hacia adelante)
                    drawContext.canvas.nativeCanvas.save()
                    drawContext.canvas.nativeCanvas.scale(-1f, 1f, pX, pY - 6.dp.toPx())
                    drawContext.canvas.nativeCanvas.drawText("🦄", pX, pY - 6.dp.toPx(), ponyPaint)
                    drawContext.canvas.nativeCanvas.restore()

                    // Destellos mágicos en salto doble
                    if (jumpCount == 2) {
                        val sparkPaint = android.graphics.Paint().apply {
                            textSize = 20.sp.toPx()
                            textAlign = android.graphics.Paint.Align.CENTER
                        }
                        drawContext.canvas.nativeCanvas.drawText("✨", pX + 22.dp.toPx(), pY - 30.dp.toPx(), sparkPaint)
                        drawContext.canvas.nativeCanvas.drawText("✨", pX - 22.dp.toPx(), pY - 30.dp.toPx(), sparkPaint)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Botón Gigante de Salto con soporte de Doble Salto
            Button(
                onClick = { triggerJump() },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFAB47BC)),
                shape = RoundedCornerShape(20.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = if (jumpCount > 0) "✨" else "🦘", fontSize = 28.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = if (jumpCount == 1) {
                            if (isEnglish) "DOUBLE FLUTTER! ✨" else "¡DOBLE SALTO MÁGICO! ✨"
                        } else {
                            if (isEnglish) "JUMP & CATCH! 🦄" else "¡SALTAR Y ATRAPAR! 🦄"
                        },
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FredokaFont,
                        color = Color.White
                    )
                }
            }
        }

        if (isGameOver) {
            ToyGameOverDialog(
                toyEmoji = toy.emoji,
                toyName = toy.name,
                score = score,
                coinsEarned = coinsEarned,
                isEnglish = isEnglish,
                onRetry = {
                    lives = 3
                    hasWonGame = false
                    isGameOver = false
                    ponyY = 0f
                    ponyVy = 0f
                    jumpCount = 0
                },
                onExit = onExitGame
            )
        }

        if (hasWonGame) {
            ToyVictoryDialog(
                toyEmoji = toy.emoji,
                toyName = toy.name,
                score = score,
                coinsEarned = coinsEarned,
                isEnglish = isEnglish,
                soundManager = soundManager,
                ttsManager = ttsManager,
                onPlayAgain = {
                    lives = 3
                    score = 0
                    coinsEarned = 0
                    correctHits = 0
                    hasWonGame = false
                    isGameOver = false
                    ponyY = 0f
                    ponyVy = 0f
                    jumpCount = 0
                    currentProblem = generatePonyMathProblem(difficulty)
                    spawnNewWaveSafe(currentProblem)
                },
                onExit = onExitGame
            )
        }
    }
}

// =========================================================================
// 7. TREN: Tren Culebrita Matemático 🚂 (Snake Game con vagones y números)
// =========================================================================

enum class SnakeTrainDirection(val dx: Int, val dy: Int) {
    UP(0, -1),
    DOWN(0, 1),
    LEFT(-1, 0),
    RIGHT(1, 0)
}

private data class FluidTrainFood(
    val pos: Offset,
    val number: Int,
    val isCorrect: Boolean
)

@Composable
fun TrenConductorGame(
    toy: ToyItem,
    isEnglish: Boolean,
    soundManager: SoundManager?,
    ttsManager: TtsManager?,
    hapticUtil: HapticUtil?,
    difficulty: ToyGameDifficulty = ToyGameDifficulty.FACIL,
    onExitGame: () -> Unit
) {
    val context = LocalContext.current
    var lives by remember { mutableIntStateOf(3) }
    var score by remember { mutableIntStateOf(0) }
    var coinsEarned by remember { mutableIntStateOf(0) }
    var correctHits by remember { mutableIntStateOf(0) }
    var currentLevel by remember { mutableIntStateOf(1) }
    val maxLevels = 20
    var hasWonGame by remember { mutableStateOf(false) }
    var isGameOver by remember { mutableStateOf(false) }
    var isPaused by remember { mutableStateOf(false) }

    var boardWidth by remember { mutableFloatStateOf(600f) }
    var boardHeight by remember { mutableFloatStateOf(700f) }

    var headPos by remember { mutableStateOf(Offset(300f, 400f)) }
    var currentDir by remember { mutableStateOf(Offset(0f, -1f)) }
    var nextDir by remember { mutableStateOf(Offset(0f, -1f)) }
    val speed = if (difficulty == ToyGameDifficulty.FACIL) 280f else 340f

    val trail = remember { mutableStateListOf<Offset>() }
    var wagonCount by remember { mutableIntStateOf(3) }
    var isInvulnerable by remember { mutableStateOf(false) }
    var invulnTimer by remember { mutableFloatStateOf(0f) }

    var mathQuestion by remember(difficulty) {
        mutableStateOf(if (difficulty == ToyGameDifficulty.FACIL) "3 + 2" else "2 × 3")
    }
    var targetNumber by remember(difficulty) {
        mutableIntStateOf(if (difficulty == ToyGameDifficulty.FACIL) 5 else 6)
    }
    var foods by remember { mutableStateOf(listOf<FluidTrainFood>()) }

    // Cantidad de números en pantalla según el nivel (3 en niveles 1-7, 4 en 8-14, 5 en 15-20)
    fun numbersForLevel(level: Int): Int = when {
        level <= 7 -> 3
        level <= 14 -> 4
        else -> 5
    }

    fun spawnFoods(target: Int, w: Float, h: Float, numWrong: Int): List<FluidTrainFood> {
        val safeW = (w - 100f).coerceAtLeast(200f)
        val safeH = (h - 100f).coerceAtLeast(200f)
        val posCorrect = Offset(50f + Random.nextFloat() * safeW, 50f + Random.nextFloat() * safeH)
        val wrongNums = mutableListOf<Int>()
        while (wrongNums.size < numWrong) {
            val candidate = if (Random.nextBoolean()) {
                target + Random.nextInt(1, 6)
            } else {
                (target - Random.nextInt(1, 6)).coerceAtLeast(1)
            }
            if (candidate != target && candidate !in wrongNums) wrongNums.add(candidate)
        }
        val result = mutableListOf(
            FluidTrainFood(posCorrect, target, isCorrect = true)
        )
        wrongNums.forEach {
            result.add(FluidTrainFood(
                Offset(50f + Random.nextFloat() * safeW, 50f + Random.nextFloat() * safeH),
                it, isCorrect = false
            ))
        }
        return result.shuffled()
    }

    fun nextQuestion() {
        if (difficulty == ToyGameDifficulty.FACIL) {
            val isPlus = Random.nextBoolean()
            if (isPlus) {
                val a = Random.nextInt(1, 6)
                val b = Random.nextInt(1, 6)
                targetNumber = a + b
                mathQuestion = "$a + $b"
            } else {
                val a = Random.nextInt(3, 10)
                val b = Random.nextInt(1, a)
                targetNumber = a - b
                mathQuestion = "$a - $b"
            }
        } else {
            val isMult = Random.nextBoolean()
            if (isMult) {
                val a = Random.nextInt(2, 5)
                val b = Random.nextInt(2, 5)
                targetNumber = a * b
                mathQuestion = "$a × $b"
            } else {
                val b = Random.nextInt(2, 5)
                val ans = Random.nextInt(2, 5)
                val a = b * ans
                targetNumber = ans
                mathQuestion = "$a ÷ $b"
            }
        }
        val numWrong = numbersForLevel(currentLevel) - 1  // total numbers - 1 correct = wrongs
        foods = spawnFoods(targetNumber, boardWidth, boardHeight, numWrong)
    }

    LaunchedEffect(boardWidth, boardHeight) {
        if (boardWidth > 100f && boardHeight > 100f && foods.isEmpty()) {
            headPos = Offset(boardWidth / 2f, boardHeight * 0.65f)
            nextQuestion()
        }
    }

    // Bucle continuo a 60 FPS ultra-fluido
    rememberGameLoop(isRunning = !isPaused && !isGameOver && !hasWonGame) { delta ->
        if (invulnTimer > 0f) {
            invulnTimer = (invulnTimer - delta).coerceAtLeast(0f)
            if (invulnTimer == 0f) isInvulnerable = false
        }

        // Girar de forma suave evitando giro de 180° inmediato
        val dot = currentDir.x * nextDir.x + currentDir.y * nextDir.y
        if (dot >= -0.2f) {
            currentDir = nextDir
        }

        val newHead = headPos + currentDir * (speed * delta)
        headPos = newHead
        trail.add(0, newHead)

        // Limitar tamaño del historial para que los vagones sigan con precisión
        val maxPoints = (wagonCount + 1) * 22
        while (trail.size > maxPoints + 6) {
            trail.removeAt(trail.lastIndex)
        }

        // 1. Colisión continua con las paredes
        val margin = 22f
        if (newHead.x < margin || newHead.x > boardWidth - margin || newHead.y < margin || newHead.y > boardHeight - margin) {
            lives = (lives - 1).coerceAtLeast(0)
            soundManager?.playIncorrect()
            hapticUtil?.vibrateEncourage()
            ttsManager?.speak(if (isEnglish) "Crash into wall! Be careful!" else "¡Choque contra la pared! ¡Cuidado!")
            if (lives == 0) {
                isGameOver = true
            } else {
                headPos = Offset(boardWidth / 2f, boardHeight * 0.6f)
                currentDir = Offset(0f, -1f)
                nextDir = Offset(0f, -1f)
                trail.clear()
                isInvulnerable = true
                invulnTimer = 1.6f
            }
            return@rememberGameLoop
        }

        // 2. Colisión continua contra sus propios vagones
        if (wagonCount >= 4 && !isInvulnerable) {
            for (wIdx in 3 until wagonCount) {
                val sampleIdx = (wIdx + 1) * 20
                val wPos = trail.getOrNull(sampleIdx)
                if (wPos != null && (newHead - wPos).getDistance() < 24f) {
                    lives = (lives - 1).coerceAtLeast(0)
                    soundManager?.playIncorrect()
                    hapticUtil?.vibrateEncourage()
                    ttsManager?.speak(if (isEnglish) "Train crashed into its wagons!" else "¡El tren chocó contra sus propios vagones!")
                    if (lives == 0) {
                        isGameOver = true
                    } else {
                        headPos = Offset(boardWidth / 2f, boardHeight * 0.6f)
                        currentDir = Offset(0f, -1f)
                        nextDir = Offset(0f, -1f)
                        trail.clear()
                        isInvulnerable = true
                        invulnTimer = 1.6f
                    }
                    return@rememberGameLoop
                }
            }
        }

        // 3. Comer número en la vía con detección continua por radio
        val hitFood = foods.firstOrNull { (newHead - it.pos).getDistance() < 34f }
        if (hitFood != null) {
            if (hitFood.isCorrect) {
                wagonCount++
                score += 25
                coinsEarned += 1
                correctHits++
                CoinShopManager.addCoin(context, 1)
                soundManager?.playCorrect()
                hapticUtil?.vibrateClick()
                ttsManager?.speak(if (isEnglish) "Nom nom! Train grew!" else "¡Correcto! ¡El tren creció!")

                if (currentLevel >= maxLevels) {
                    hasWonGame = true
                    soundManager?.playVictory()
                    ttsManager?.speak(if (isEnglish) "Super Train Completed! All 20 levels!" else "¡Super Tren Completado! ¡20 niveles completados!")
                } else {
                    currentLevel++
                    nextQuestion()
                }
            } else {
                lives = (lives - 1).coerceAtLeast(0)
                soundManager?.playIncorrect()
                hapticUtil?.vibrateEncourage()
                ttsManager?.speak(if (isEnglish) "Wrong number eaten!" else "¡Te comiste el número incorrecto!")
                if (lives == 0) {
                    isGameOver = true
                } else {
                    foods = spawnFoods(targetNumber, boardWidth, boardHeight, numbersForLevel(currentLevel) - 1)
                }
            }
        }
    }

    fun turn(dir: Offset) {
        // Evitar giro inmediato opuesto de 180°
        val dot = currentDir.x * dir.x + currentDir.y * dir.y
        if (dot < -0.2f) return

        nextDir = dir
        soundManager?.playButtonClick()
        hapticUtil?.vibrateClick()
    }

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFFE8F5E9))) {
        Column(
            modifier = Modifier.fillMaxSize().padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ToyGameTopBar(
                title = if (isEnglish) "Snake Train" else "Tren Culebrita",
                toyEmoji = toy.emoji,
                lives = lives,
                coins = coinsEarned,
                correctHits = currentLevel - 1,
                targetHits = maxLevels,
                difficulty = difficulty,
                isEnglish = isEnglish,
                onExitClick = onExitGame,
                onPauseClick = { isPaused = !isPaused }
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Cartel de Misión Matemática para el Tren
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(SurfaceWhite)
                    .border(2.dp, Color(0xFF2E7D32), RoundedCornerShape(14.dp))
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🚂 $mathQuestion = $targetNumber",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FredokaFont,
                    color = Color(0xFF1B5E20)
                )
                Text(
                    text = if (isEnglish) "Lvl $currentLevel/$maxLevels" else "Niv $currentLevel/$maxLevels",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFEF6C00)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // TABLERO ULTRA-FLUIDO EN CANVAS
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .shadow(6.dp, RoundedCornerShape(20.dp))
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFFC8E6C9))
                    .border(3.dp, Color(0xFF388E3C), RoundedCornerShape(20.dp))
                    .onSizeChanged {
                        boardWidth = it.width.toFloat()
                        boardHeight = it.height.toFloat()
                    }
                    .pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            if (kotlin.math.abs(dragAmount.x) > kotlin.math.abs(dragAmount.y)) {
                                if (dragAmount.x > 8f) turn(Offset(1f, 0f))
                                else if (dragAmount.x < -8f) turn(Offset(-1f, 0f))
                            } else {
                                if (dragAmount.y > 8f) turn(Offset(0f, 1f))
                                else if (dragAmount.y < -8f) turn(Offset(0f, -1f))
                            }
                        }
                    }
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height

                    // Cuadrícula visible de vías
                    val tileSize = 36.dp.toPx()
                    var gx = 0f
                    while (gx < w) {
                        var gy = 0f
                        while (gy < h) {
                            val isEven = ((gx / tileSize).toInt() + (gy / tileSize).toInt()) % 2 == 0
                            drawRect(
                                color = if (isEven) Color(0xFF81C784).copy(alpha = 0.55f) else Color(0xFF66BB6A).copy(alpha = 0.30f),
                                topLeft = Offset(gx, gy),
                                size = Size(tileSize, tileSize)
                            )
                            // Bordes de celda visibles
                            drawRect(
                                color = Color(0xFF388E3C).copy(alpha = 0.18f),
                                topLeft = Offset(gx, gy),
                                size = Size(tileSize, tileSize),
                                style = Stroke(width = 1f)
                            )
                            gy += tileSize
                        }
                        gx += tileSize
                    }

                    // 1. Dibujar números / estaciones de comida
                    foods.forEach { food ->
                        val fx = food.pos.x
                        val fy = food.pos.y
                        val rad = 22.dp.toPx()

                        drawCircle(
                            color = Color(0xFFFFD54F),
                            radius = rad,
                            center = Offset(fx, fy)
                        )
                        drawCircle(
                            color = Color(0xFFF57F17),
                            radius = rad,
                            center = Offset(fx, fy),
                            style = Stroke(width = 3.dp.toPx())
                        )

                        val foodPaint = android.graphics.Paint().apply {
                            textSize = 18.sp.toPx()
                            color = android.graphics.Color.parseColor("#B71C1C")
                            textAlign = android.graphics.Paint.Align.CENTER
                            isFakeBoldText = true
                            isAntiAlias = true
                        }
                        val textY = fy - ((foodPaint.descent() + foodPaint.ascent()) / 2f)
                        drawContext.canvas.nativeCanvas.drawText("${food.number}", fx, textY, foodPaint)
                    }

                    // 2. Dibujar vagones del tren a lo largo del camino recorrido
                    val wagonColors = listOf(
                        Color(0xFF1976D2), Color(0xFF388E3C), Color(0xFFF57C00),
                        Color(0xFF7B1FA2), Color(0xFF0097A7), Color(0xFFC2185B)
                    )

                    for (i in (wagonCount - 1) downTo 0) {
                        val sampleIdx = (i + 1) * 20
                        val pos = trail.getOrNull(sampleIdx) ?: (headPos - currentDir * ((i + 1) * 22f))
                        val prevPos = trail.getOrNull((sampleIdx - 4).coerceAtLeast(0)) ?: headPos
                        val angle = (atan2(pos.y - prevPos.y, pos.x - prevPos.x) * (180f / PI.toFloat()))

                        drawContext.canvas.nativeCanvas.save()
                        drawContext.canvas.nativeCanvas.translate(pos.x, pos.y)
                        drawContext.canvas.nativeCanvas.rotate(angle)

                        // Cuerpo del vagón
                        drawRoundRect(
                            color = wagonColors[i % wagonColors.size],
                            topLeft = Offset(-14.dp.toPx(), -12.dp.toPx()),
                            size = Size(28.dp.toPx(), 24.dp.toPx()),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(6.dp.toPx(), 6.dp.toPx())
                        )
                        // Borde del vagón
                        drawRoundRect(
                            color = Color.Black.copy(alpha = 0.25f),
                            topLeft = Offset(-14.dp.toPx(), -12.dp.toPx()),
                            size = Size(28.dp.toPx(), 24.dp.toPx()),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(6.dp.toPx(), 6.dp.toPx()),
                            style = Stroke(width = 2.dp.toPx())
                        )
                        // Ventana
                        drawRect(
                            color = Color(0xFFE1F5FE),
                            topLeft = Offset(-6.dp.toPx(), -6.dp.toPx()),
                            size = Size(12.dp.toPx(), 12.dp.toPx())
                        )

                        drawContext.canvas.nativeCanvas.restore()
                    }

                    // 3. Dibujar Locomotora
                    val headAngle = (atan2(currentDir.y, currentDir.x) * (180f / PI.toFloat()))
                    val blinkAlpha = if (isInvulnerable && ((invulnTimer * 10).toInt() % 2 == 0)) 0.35f else 1.0f

                    drawContext.canvas.nativeCanvas.save()
                    drawContext.canvas.nativeCanvas.translate(headPos.x, headPos.y)
                    drawContext.canvas.nativeCanvas.rotate(headAngle)

                    // Locomotora Roja y Dorada
                    drawRoundRect(
                        color = Color(0xFFD32F2F).copy(alpha = blinkAlpha),
                        topLeft = Offset(-16.dp.toPx(), -14.dp.toPx()),
                        size = Size(32.dp.toPx(), 28.dp.toPx()),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(8.dp.toPx(), 8.dp.toPx())
                    )
                    // Campana / Chimenea
                    drawCircle(
                        color = Color(0xFFFFD54F).copy(alpha = blinkAlpha),
                        radius = 6.dp.toPx(),
                        center = Offset(8.dp.toPx(), 0f)
                    )
                    // Faro delantero de luz
                    drawCircle(
                        color = Color.White.copy(alpha = blinkAlpha),
                        radius = 4.dp.toPx(),
                        center = Offset(14.dp.toPx(), 0f)
                    )

                    drawContext.canvas.nativeCanvas.restore()
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Instrucción de control por deslizamiento
            Text(
                text = if (isEnglish) "👆 Swipe to steer the train" else "👆 Desliza para guiar el tren",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FredokaFont,
                color = Color(0xFF388E3C)
            )
        }

        if (isGameOver) {
            ToyGameOverDialog(
                toyEmoji = toy.emoji,
                toyName = toy.name,
                score = score,
                coinsEarned = coinsEarned,
                isEnglish = isEnglish,
                onRetry = {
                    lives = 3
                    hasWonGame = false
                    isGameOver = false
                    isInvulnerable = true
                    invulnTimer = 2.0f
                    // Continuar desde el nivel y progreso actual
                    headPos = Offset(boardWidth / 2f, boardHeight * 0.6f)
                    currentDir = Offset(0f, -1f)
                    nextDir = Offset(0f, -1f)
                },
                onExit = onExitGame
            )
        }

        if (hasWonGame) {
            ToyVictoryDialog(
                toyEmoji = toy.emoji,
                toyName = toy.name,
                score = score,
                coinsEarned = coinsEarned,
                isEnglish = isEnglish,
                soundManager = soundManager,
                ttsManager = ttsManager,
                onPlayAgain = {
                    hasWonGame = false
                    correctHits = 0
                    currentLevel = 1
                    lives = 3
                    score = 0
                    coinsEarned = 0
                    isGameOver = false
                    wagonCount = 3
                    headPos = Offset(boardWidth / 2f, boardHeight * 0.6f)
                    currentDir = Offset(0f, -1f)
                    nextDir = Offset(0f, -1f)
                    trail.clear()
                    isInvulnerable = false
                    nextQuestion()
                },
                onExit = onExitGame
            )
        }
    }
}

// =========================================================================
// 8. CASTILLO: Construcción del Gran Castillo como Rompecabezas 🏰
// =========================================================================

data class CastleSection(
    val id: Int,
    val nameEs: String,
    val nameEn: String,
    val icon: String,
    val mathQuestion: String,
    val targetNumber: Int,
    val color: Color,
    var isBuilt: Boolean = false
)

@Composable
fun CastilloDefenseGame(
    toy: ToyItem,
    isEnglish: Boolean,
    soundManager: SoundManager?,
    ttsManager: TtsManager?,
    hapticUtil: HapticUtil?,
    difficulty: ToyGameDifficulty = ToyGameDifficulty.FACIL,
    onExitGame: () -> Unit
) {
    val context = LocalContext.current
    var lives by remember { mutableIntStateOf(3) }
    var score by remember { mutableIntStateOf(0) }
    var coinsEarned by remember { mutableIntStateOf(0) }
    var correctHits by remember { mutableIntStateOf(0) }
    var hasWonGame by remember { mutableStateOf(false) }
    var isGameOver by remember { mutableStateOf(false) }
    var showConfetti by remember { mutableStateOf(false) }

    val initialSections = remember(difficulty) {
        if (difficulty == ToyGameDifficulty.FACIL) {
            listOf(
                CastleSection(1, "Cimientos de Roca", "Stone Foundation", "🧱", "2 + 2", 4, Color(0xFF8D6E63)),
                CastleSection(2, "Portón Levadizo", "Drawbridge Gate", "🚪", "3 + 2", 5, Color(0xFF6D4C41)),
                CastleSection(3, "Muralla Central", "Central Wall", "🏰", "4 + 4", 8, Color(0xFF78909C)),
                CastleSection(4, "Torreón Izquierdo", "Left Tower", "🗼", "6 - 3", 3, Color(0xFF5C6BC0)),
                CastleSection(5, "Torreón Derecho", "Right Tower", "🗼", "9 - 2", 7, Color(0xFF5C6BC0)),
                CastleSection(6, "Cúpula y Corona", "Crown & Flags", "👑", "5 + 4", 9, Color(0xFFFFB300))
            )
        } else {
            listOf(
                CastleSection(1, "Cimientos de Roca", "Stone Foundation", "🧱", "3 × 2", 6, Color(0xFF8D6E63)),
                CastleSection(2, "Portón Levadizo", "Drawbridge Gate", "🚪", "16 ÷ 2", 8, Color(0xFF6D4C41)),
                CastleSection(3, "Muralla Central", "Central Wall", "🏰", "5 × 2", 10, Color(0xFF78909C)),
                CastleSection(4, "Torreón Izquierdo", "Left Tower", "🗼", "4 × 3", 12, Color(0xFF5C6BC0)),
                CastleSection(5, "Torreón Derecho", "Right Tower", "🗼", "21 ÷ 3", 7, Color(0xFF5C6BC0)),
                CastleSection(6, "Cúpula y Corona", "Crown & Flags", "👑", "3 × 5", 15, Color(0xFFFFB300))
            )
        }
    }

    val sections = remember(initialSections) {
        mutableStateListOf<CastleSection>().apply { addAll(initialSections) }
    }

    var rootOrigin by remember { mutableStateOf(Offset.Zero) }
    val blockOrigins = remember { mutableStateMapOf<Int, Offset>() }
    val sectionBounds = remember { mutableStateMapOf<Int, Rect>() }

    // Bloques en la cantera (los no construidos + 1 distractor)
    val quarryBlocks = remember(sections.map { it.isBuilt }) {
        val unbuilt = sections.filter { !it.isBuilt }.map { it.targetNumber }
        val dummy = ((unbuilt.maxOrNull() ?: 5) + Random.nextInt(2, 4))
        (unbuilt + dummy).distinct().shuffled()
    }

    var draggedBlockNumber by remember { mutableStateOf<Int?>(null) }
    var dragPosition by remember { mutableStateOf(Offset.Zero) }
    var selectedBlockNumber by remember { mutableIntStateOf(-1) }

    fun checkCastleComplete() {
        if (sections.all { it.isBuilt }) {
            showConfetti = true
            score += 60
            coinsEarned += 5
            correctHits += sections.size
            CoinShopManager.addCoin(context, 5)
            soundManager?.playVictory()
            ttsManager?.speak(if (isEnglish) "Grand Castle Built! Spectacular!" else "¡Gran Castillo construido con éxito! ¡Espectacular!")

            if (correctHits >= 12) {
                hasWonGame = true
            }
        }
    }

    fun handlePlaceBlock(blockNum: Int, section: CastleSection) {
        if (section.isBuilt) return

        if (blockNum == section.targetNumber) {
            section.isBuilt = true
            score += 20
            coinsEarned += 1
            CoinShopManager.addCoin(context, 1)
            soundManager?.playCorrect()
            hapticUtil?.vibrateClick()
            ttsManager?.speak(if (isEnglish) "${section.nameEn} built!" else "¡${section.nameEs} construida!")
            selectedBlockNumber = -1
            checkCastleComplete()
        } else {
            lives = (lives - 1).coerceAtLeast(0)
            soundManager?.playIncorrect()
            hapticUtil?.vibrateEncourage()
            ttsManager?.speak(if (isEnglish) "That block doesn't fit here!" else "¡Ese bloque no va en esa sección!")
            if (lives == 0) isGameOver = true
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFEDE7F6))
            .onGloballyPositioned { rootOrigin = it.positionInRoot() }
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ToyGameTopBar(
                title = if (isEnglish) "Castle Builder Puzzle" else "Construye el Gran Castillo",
                toyEmoji = toy.emoji,
                lives = lives,
                coins = coinsEarned,
                correctHits = correctHits,
                targetHits = 12,
                difficulty = difficulty,
                isEnglish = isEnglish,
                onExitClick = onExitGame
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Banner de progreso del castillo
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(SurfaceWhite)
                    .border(2.dp, Color(0xFF7E57C2), RoundedCornerShape(14.dp))
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🏰 Gran Castillo Real",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FredokaFont,
                    color = Color(0xFF512DA8)
                )
                val builtCount = sections.count { it.isBuilt }
                Text(
                    text = "🧱 Bloques: $builtCount/${sections.size}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (builtCount == sections.size) Color(0xFF2E7D32) else Color(0xFFEF6C00)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // PLANO DE CONSTRUCCIÓN DEL CASTILLO (ROMPECABEZAS ARQUITECTÓNICO)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .shadow(6.dp, RoundedCornerShape(24.dp))
                    .clip(RoundedCornerShape(24.dp))
                    .background(SurfaceWhite)
                    .border(3.dp, Color(0xFF7E57C2), RoundedCornerShape(24.dp))
                    .padding(10.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Nivel Superior: Cúpula y Corona
                    val secTop = sections.firstOrNull { it.id == 6 }
                    if (secTop != null) {
                        CastleBlueprintSlot(
                            section = secTop,
                            rootOrigin = rootOrigin,
                            bounds = sectionBounds,
                            draggedBlock = draggedBlockNumber,
                            dragPosition = dragPosition,
                            selectedBlock = selectedBlockNumber,
                            widthDp = 150,
                            heightDp = 62,
                            isEnglish = isEnglish,
                            onClick = {
                                if (selectedBlockNumber != -1) handlePlaceBlock(selectedBlockNumber, secTop)
                            }
                        )
                    }

                    // Nivel Medio: Torreón Izquierdo + Muralla Central + Torreón Derecho
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val secIzq = sections.firstOrNull { it.id == 4 }
                        val secMuralla = sections.firstOrNull { it.id == 3 }
                        val secDer = sections.firstOrNull { it.id == 5 }

                        if (secIzq != null) {
                            CastleBlueprintSlot(
                                section = secIzq,
                                rootOrigin = rootOrigin,
                                bounds = sectionBounds,
                                draggedBlock = draggedBlockNumber,
                                dragPosition = dragPosition,
                                selectedBlock = selectedBlockNumber,
                                widthDp = 95,
                                heightDp = 72,
                                isEnglish = isEnglish,
                                onClick = {
                                    if (selectedBlockNumber != -1) handlePlaceBlock(selectedBlockNumber, secIzq)
                                }
                            )
                        }

                        if (secMuralla != null) {
                            CastleBlueprintSlot(
                                section = secMuralla,
                                rootOrigin = rootOrigin,
                                bounds = sectionBounds,
                                draggedBlock = draggedBlockNumber,
                                dragPosition = dragPosition,
                                selectedBlock = selectedBlockNumber,
                                widthDp = 110,
                                heightDp = 72,
                                isEnglish = isEnglish,
                                onClick = {
                                    if (selectedBlockNumber != -1) handlePlaceBlock(selectedBlockNumber, secMuralla)
                                }
                            )
                        }

                        if (secDer != null) {
                            CastleBlueprintSlot(
                                section = secDer,
                                rootOrigin = rootOrigin,
                                bounds = sectionBounds,
                                draggedBlock = draggedBlockNumber,
                                dragPosition = dragPosition,
                                selectedBlock = selectedBlockNumber,
                                widthDp = 95,
                                heightDp = 72,
                                isEnglish = isEnglish,
                                onClick = {
                                    if (selectedBlockNumber != -1) handlePlaceBlock(selectedBlockNumber, secDer)
                                }
                            )
                        }
                    }

                    // Nivel Inferior: Gran Portón Levadizo
                    val secPorton = sections.firstOrNull { it.id == 2 }
                    if (secPorton != null) {
                        CastleBlueprintSlot(
                            section = secPorton,
                            rootOrigin = rootOrigin,
                            bounds = sectionBounds,
                            draggedBlock = draggedBlockNumber,
                            dragPosition = dragPosition,
                            selectedBlock = selectedBlockNumber,
                            widthDp = 140,
                            heightDp = 58,
                            isEnglish = isEnglish,
                            onClick = {
                                if (selectedBlockNumber != -1) handlePlaceBlock(selectedBlockNumber, secPorton)
                            }
                        )
                    }

                    // Nivel Base: Cimientos de Roca
                    val secBase = sections.firstOrNull { it.id == 1 }
                    if (secBase != null) {
                        CastleBlueprintSlot(
                            section = secBase,
                            rootOrigin = rootOrigin,
                            bounds = sectionBounds,
                            draggedBlock = draggedBlockNumber,
                            dragPosition = dragPosition,
                            selectedBlock = selectedBlockNumber,
                            widthDp = 250,
                            heightDp = 48,
                            isEnglish = isEnglish,
                            onClick = {
                                if (selectedBlockNumber != -1) handlePlaceBlock(selectedBlockNumber, secBase)
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // CANTERA DE BLOQUES DE CONSTRUCCIÓN (ARRASTRABLES)
            Text(
                text = if (isEnglish) "🧱 Stone Blocks Quarry (Drag from anywhere or Tap):" else "🧱 Cantera de Bloques (Arrastra o Toca):",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FredokaFont,
                color = SlateDark
            )
            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                quarryBlocks.forEach { bVal ->
                    val isSelected = selectedBlockNumber == bVal

                    Box(
                        modifier = Modifier
                            .onGloballyPositioned { coordinates ->
                                blockOrigins[bVal] = coordinates.positionInRoot() - rootOrigin
                            }
                            .scale(if (isSelected) 1.15f else 1.0f)
                            .shadow(if (isSelected) 6.dp else 3.dp, RoundedCornerShape(14.dp))
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (isSelected) Color(0xFFFFCC80) else Color(0xFFEDE7F6))
                            .border(2.dp, if (isSelected) Color(0xFF512DA8) else Color(0xFF7E57C2), RoundedCornerShape(14.dp))
                            .pointerInput(bVal) {
                                detectDragGestures(
                                    onDragStart = { offset ->
                                        val origin = blockOrigins[bVal] ?: Offset.Zero
                                        draggedBlockNumber = bVal
                                        dragPosition = origin + offset
                                        soundManager?.playButtonClick()
                                        hapticUtil?.vibrateClick()
                                    },
                                    onDrag = { change, dragAmount ->
                                        change.consume()
                                        dragPosition += dragAmount
                                    },
                                    onDragEnd = {
                                        val droppedVal = draggedBlockNumber
                                        if (droppedVal != null) {
                                            val hitSec = sections.firstOrNull { sec ->
                                                val b = sectionBounds[sec.id]?.inflate(35f)
                                                b != null && b.contains(dragPosition)
                                            }
                                            if (hitSec != null) {
                                                handlePlaceBlock(droppedVal, hitSec)
                                            }
                                        }
                                        draggedBlockNumber = null
                                    },
                                    onDragCancel = {
                                        draggedBlockNumber = null
                                    }
                                )
                            }
                            .clickable {
                                selectedBlockNumber = bVal
                                soundManager?.playButtonClick()
                                hapticUtil?.vibrateClick()
                            }
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "🧱", fontSize = 22.sp)
                            Text(
                                text = "N° $bVal",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FredokaFont,
                                color = Color(0xFF512DA8)
                            )
                        }
                    }
                }
            }
        }

        // Bloque flotante durante el arrastre con el dedo
        if (draggedBlockNumber != null) {
            Box(
                modifier = Modifier
                    .offset { IntOffset(dragPosition.x.roundToInt() - 40, dragPosition.y.roundToInt() - 40) }
                    .zIndex(99f)
                    .scale(1.25f)
                    .shadow(10.dp, RoundedCornerShape(16.dp))
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFEDE7F6))
                    .border(3.dp, Color(0xFF512DA8), RoundedCornerShape(16.dp))
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "🧱", fontSize = 30.sp)
                    Text(
                        text = "N° $draggedBlockNumber",
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FredokaFont,
                        color = Color(0xFF512DA8)
                    )
                }
            }
        }

        if (showConfetti) {
            ConfettiCelebration(visible = true, onFinished = {
                showConfetti = false
                sections.forEach { it.isBuilt = false }
            })
        }

        if (isGameOver) {
            ToyGameOverDialog(
                toyEmoji = toy.emoji,
                toyName = toy.name,
                score = score,
                coinsEarned = coinsEarned,
                isEnglish = isEnglish,
                onRetry = {
                    lives = 3
                    hasWonGame = false
                    isGameOver = false
                },
                onExit = onExitGame
            )
        }

        if (hasWonGame) {
            ToyVictoryDialog(
                toyEmoji = toy.emoji,
                toyName = toy.name,
                score = score,
                coinsEarned = coinsEarned,
                isEnglish = isEnglish,
                soundManager = soundManager,
                ttsManager = ttsManager,
                onPlayAgain = {
                    hasWonGame = false
                    correctHits = 0
                    lives = 3
                    score = 0
                    coinsEarned = 0
                    isGameOver = false
                    sections.forEach { it.isBuilt = false }
                },
                onExit = onExitGame
            )
        }
    }
}

@Composable
private fun CastleBlueprintSlot(
    section: CastleSection,
    rootOrigin: Offset,
    bounds: MutableMap<Int, Rect>,
    draggedBlock: Int?,
    dragPosition: Offset,
    selectedBlock: Int,
    widthDp: Int,
    heightDp: Int,
    isEnglish: Boolean,
    onClick: () -> Unit
) {
    val isOver = draggedBlock != null && bounds[section.id]?.inflate(35f)?.contains(dragPosition) == true
    val isSelectedMatch = selectedBlock == section.targetNumber

    Box(
        modifier = Modifier
            .onGloballyPositioned { coordinates ->
                val localPos = coordinates.positionInRoot() - rootOrigin
                bounds[section.id] = Rect(localPos, Size(coordinates.size.width.toFloat(), coordinates.size.height.toFloat()))
            }
            .size(width = widthDp.dp, height = heightDp.dp)
            .scale(if (isOver || isSelectedMatch) 1.06f else 1.0f)
            .shadow(if (section.isBuilt) 4.dp else 1.dp, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .background(if (section.isBuilt) section.color else if (isOver) Color(0xFFD1C4E9) else Color(0xFFF3E5F5))
            .border(
                width = if (isOver) 3.dp else 2.dp,
                color = if (section.isBuilt) Color(0xFFFFD54F) else if (isOver) Color(0xFF512DA8) else Color(0xFFB39DDB),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (section.isBuilt) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(text = section.icon, fontSize = 22.sp)
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (isEnglish) "✓ Built" else "✓ Construido",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
            }
        } else {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "${section.mathQuestion} = ?",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FredokaFont,
                    color = Color(0xFF512DA8)
                )
                Text(
                    text = if (isEnglish) section.nameEn else section.nameEs,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = SlateMuted
                )
            }
        }
    }
}

// =========================================================================
// 9. ROBOT: Laboratorio de Energía Neón 🤖 (Matriz 3x3 y baile robótico)
// =========================================================================
@Composable
fun RobotMatrixGame(
    toy: ToyItem,
    isEnglish: Boolean,
    soundManager: SoundManager?,
    ttsManager: TtsManager?,
    hapticUtil: HapticUtil?,
    difficulty: ToyGameDifficulty = ToyGameDifficulty.FACIL,
    onExitGame: () -> Unit
) {
    val context = LocalContext.current
    var lives by remember { mutableIntStateOf(3) }
    var score by remember { mutableIntStateOf(0) }
    var coinsEarned by remember { mutableIntStateOf(0) }
    var correctHits by remember { mutableIntStateOf(0) }
    var hasWonGame by remember { mutableStateOf(false) }
    var isGameOver by remember { mutableStateOf(false) }

    var energyPercent by remember { mutableIntStateOf(25) }
    var mathQuestion by remember(difficulty) {
        mutableStateOf(if (difficulty == ToyGameDifficulty.FACIL) "4 + 4" else "2 × 4")
    }
    var targetNumber by remember(difficulty) { mutableIntStateOf(8) }

    val matrixCells = remember(targetNumber) {
        val wrong1 = targetNumber + 1
        val wrong2 = (targetNumber - 1).coerceAtLeast(1)
        val wrong3 = targetNumber + 2
        val wrong4 = (targetNumber - 2).coerceAtLeast(1)
        val wrong5 = targetNumber + 3
        listOf(targetNumber, wrong1, wrong2, wrong3, wrong4, wrong5).distinct().shuffled().take(6)
    }

    fun nextQuestion() {
        if (difficulty == ToyGameDifficulty.FACIL) {
            val isPlus = Random.nextBoolean()
            if (isPlus) {
                val a = Random.nextInt(1, 9)
                val b = Random.nextInt(1, 9)
                targetNumber = a + b
                mathQuestion = "$a + $b"
            } else {
                val a = Random.nextInt(4, 16)
                val b = Random.nextInt(1, a)
                targetNumber = a - b
                mathQuestion = "$a - $b"
            }
        } else {
            val isMul = Random.nextBoolean()
            if (isMul) {
                val a = Random.nextInt(2, 6)
                val b = Random.nextInt(2, 5)
                targetNumber = a * b
                mathQuestion = "$a × $b"
            } else {
                val b = Random.nextInt(2, 5)
                val ans = Random.nextInt(2, 6)
                val a = b * ans
                targetNumber = ans
                mathQuestion = "$a ÷ $b"
            }
        }
    }

    LaunchedEffect(difficulty) {
        nextQuestion()
    }

    fun onCellTap(valCell: Int) {
        if (valCell == targetNumber) {
            energyPercent = (energyPercent + 25).coerceAtMost(100)
            score += 20
            coinsEarned += 1
            correctHits++
            CoinShopManager.addCoin(context, 1)
            soundManager?.playCorrect()
            hapticUtil?.vibrateClick()
            if (energyPercent >= 100) {
                soundManager?.playVictory()
                ttsManager?.speak(if (isEnglish) "Robot charged! Dance mode on!" else "¡Robot cargado al 100%! ¡Modo baile activado!")
                energyPercent = 25
            }
            if (correctHits >= 30) {
                hasWonGame = true
            }
            nextQuestion()
        } else {
            lives = (lives - 1).coerceAtLeast(0)
            soundManager?.playIncorrect()
            hapticUtil?.vibrateEncourage()
            if (lives == 0) isGameOver = true
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFFECEFF1))) {
        Column(
            modifier = Modifier.fillMaxSize().padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ToyGameTopBar(
                title = if (isEnglish) "Robot Laboratory" else "Laboratorio del Robot",
                toyEmoji = toy.emoji,
                lives = lives,
                coins = coinsEarned,
                correctHits = correctHits,
                targetHits = 30,
                difficulty = difficulty,
                isEnglish = isEnglish,
                onExitClick = onExitGame
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Panel cibernético del robot
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .shadow(6.dp, RoundedCornerShape(24.dp))
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color(0xFF263238))
                    .border(3.dp, Color(0xFF00E5FF), RoundedCornerShape(24.dp))
                    .padding(14.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = if (isEnglish) "⚡ Charge battery: $mathQuestion = ?" else "⚡ ¡Carga la batería resolviendo: $mathQuestion = ?",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FredokaFont,
                        color = Color(0xFF00E5FF)
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "🤖", fontSize = 56.sp)
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(text = "BATERÍA: $energyPercent%", fontSize = 14.sp, fontWeight = FontWeight.Black, color = Color(0xFF76FF03))
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(
                                modifier = Modifier
                                    .width(100.dp)
                                    .height(14.dp)
                                    .clip(RoundedCornerShape(7.dp))
                                    .background(Color(0xFF37474F))
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(energyPercent / 100f)
                                        .height(14.dp)
                                        .background(Color(0xFF76FF03))
                                )
                            }
                        }
                    }

                    // Celdas de energía de neón
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        matrixCells.take(3).forEach { valC ->
                            CyberCell(valC = valC, onClick = { onCellTap(valC) })
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        matrixCells.drop(3).forEach { valC ->
                            CyberCell(valC = valC, onClick = { onCellTap(valC) })
                        }
                    }
                }
            }
        }

        if (isGameOver) {
            ToyGameOverDialog(
                toyEmoji = toy.emoji,
                toyName = toy.name,
                score = score,
                coinsEarned = coinsEarned,
                isEnglish = isEnglish,
                onRetry = {
                    lives = 3
                    hasWonGame = false
                    isGameOver = false
                },
                onExit = onExitGame
            )
        }

        if (hasWonGame) {
            ToyVictoryDialog(
                toyEmoji = toy.emoji,
                toyName = toy.name,
                score = score,
                coinsEarned = coinsEarned,
                isEnglish = isEnglish,
                soundManager = soundManager,
                ttsManager = ttsManager,
                onPlayAgain = {
                    hasWonGame = false
                    correctHits = 0
                    lives = 3
                    score = 0
                    coinsEarned = 0
                    isGameOver = false
                    energyPercent = 25
                    nextQuestion()
                },
                onExit = onExitGame
            )
        }
    }
}

@Composable
private fun CyberCell(valC: Int, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(56.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF37474F))
            .border(2.dp, Color(0xFF00E5FF), RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "$valC", fontSize = 18.sp, fontWeight = FontWeight.Black, color = Color(0xFF00E5FF))
    }
}

// =========================================================================
// 10. PINTURA: Estudio de Arte y Lienzo Interactivo 🎨 (Pintar y colorear con el dedo)
// =========================================================================

private data class ArtistCanvasStroke(
    val points: List<Offset>,
    val color: Color,
    val strokeWidth: Float,
    val isEraser: Boolean = false
)

private data class PaintPaletteBottle(
    val value: Int,
    val color: Color,
    val labelEs: String,
    val labelEn: String,
    val isTarget: Boolean
)

@Composable
fun PinturaArtSplatterGame(
    toy: ToyItem,
    isEnglish: Boolean,
    soundManager: SoundManager?,
    ttsManager: TtsManager?,
    hapticUtil: HapticUtil?,
    difficulty: ToyGameDifficulty = ToyGameDifficulty.FACIL,
    onExitGame: () -> Unit
) {
    val context = LocalContext.current
    var lives by remember { mutableIntStateOf(3) }
    var score by remember { mutableIntStateOf(0) }
    var coinsEarned by remember { mutableIntStateOf(0) }
    var correctHits by remember { mutableIntStateOf(0) }
    var hasWonGame by remember { mutableStateOf(false) }
    var isGameOver by remember { mutableStateOf(false) }

    var mathQuestion by remember(difficulty) {
        mutableStateOf(if (difficulty == ToyGameDifficulty.FACIL) "3 + 2" else "2 × 3")
    }
    var targetNumber by remember(difficulty) {
        mutableIntStateOf(if (difficulty == ToyGameDifficulty.FACIL) 5 else 6)
    }

    // Color y grosor activo del pincel
    var activeBrushColor by remember { mutableStateOf(Color(0xFFE53935)) }
    var isEraserMode by remember { mutableStateOf(false) }
    var brushWidth by remember { mutableFloatStateOf(24f) }

    // Trazos interactivos pintados en el lienzo
    val strokes = remember { mutableStateListOf<ArtistCanvasStroke>() }
    val currentPoints = remember { mutableStateListOf<Offset>() }

    val colorPool = listOf(
        Pair(Color(0xFFE53935), "Rojo"),
        Pair(Color(0xFF1E88E5), "Azul"),
        Pair(Color(0xFF43A047), "Verde"),
        Pair(Color(0xFFFDD835), "Amarillo"),
        Pair(Color(0xFF8E24AA), "Morado"),
        Pair(Color(0xFFFB8C00), "Naranja"),
        Pair(Color(0xFFD81B60), "Rosa"),
        Pair(Color(0xFF00ACC1), "Turquesa")
    )

    var paletteBottles by remember { mutableStateOf(listOf<PaintPaletteBottle>()) }

    fun nextQuestion() {
        if (difficulty == ToyGameDifficulty.FACIL) {
            val isPlus = Random.nextBoolean()
            if (isPlus) {
                val a = Random.nextInt(1, 9)
                val b = Random.nextInt(1, 9)
                targetNumber = a + b
                mathQuestion = "$a + $b"
            } else {
                val a = Random.nextInt(4, 16)
                val b = Random.nextInt(1, a)
                targetNumber = a - b
                mathQuestion = "$a - $b"
            }
        } else {
            val isMul = Random.nextBoolean()
            if (isMul) {
                val a = Random.nextInt(2, 6)
                val b = Random.nextInt(2, 5)
                targetNumber = a * b
                mathQuestion = "$a × $b"
            } else {
                val b = Random.nextInt(2, 5)
                val ans = Random.nextInt(2, 6)
                val a = b * ans
                targetNumber = ans
                mathQuestion = "$a ÷ $b"
            }
        }

        val wrong1 = targetNumber + Random.nextInt(1, 4)
        val wrong2 = (targetNumber - Random.nextInt(1, 4)).coerceAtLeast(1)
        val wrong3 = targetNumber + Random.nextInt(4, 8)
        val candidateValues = listOf(targetNumber, wrong1, if (wrong2 != targetNumber && wrong2 != wrong1) wrong2 else targetNumber + 3, wrong3).distinct().take(3).shuffled()

        val shuffledColors = colorPool.shuffled()
        paletteBottles = candidateValues.mapIndexed { idx, v ->
            val colorInfo = shuffledColors[idx % shuffledColors.size]
            PaintPaletteBottle(
                value = v,
                color = colorInfo.first,
                labelEs = colorInfo.second,
                labelEn = colorInfo.second,
                isTarget = (v == targetNumber)
            )
        }
    }

    LaunchedEffect(difficulty) {
        nextQuestion()
    }

    fun onBottleSelected(bottle: PaintPaletteBottle) {
        if (bottle.isTarget) {
            score += 20
            coinsEarned += 1
            correctHits++
            CoinShopManager.addCoin(context, 1)
            soundManager?.playVictory()
            hapticUtil?.vibrateClick()
            activeBrushColor = bottle.color
            isEraserMode = false
            ttsManager?.speak(if (isEnglish) "Paint brush charged with ${bottle.labelEn}! Paint now!" else "¡Pincel cargado con pintura ${bottle.labelEs}! ¡A pintar!")
            if (correctHits >= 20) {
                hasWonGame = true
            }
            nextQuestion()
        } else {
            lives = (lives - 1).coerceAtLeast(0)
            soundManager?.playIncorrect()
            hapticUtil?.vibrateEncourage()
            if (lives == 0) isGameOver = true
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFFFFF8E1))) {
        Column(
            modifier = Modifier.fillMaxSize().padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ToyGameTopBar(
                title = if (isEnglish) "Paint Studio" else "Estudio de Pintura",
                toyEmoji = toy.emoji,
                lives = lives,
                coins = coinsEarned,
                correctHits = correctHits,
                targetHits = 20,
                difficulty = difficulty,
                isEnglish = isEnglish,
                onExitClick = onExitGame
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Cartel de Misión para cargar el pincel con pintura
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFFFFECB3))
                    .border(2.dp, Color(0xFFFFB300), RoundedCornerShape(14.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = if (isEnglish) "🎨 Unlock brush paint: $mathQuestion = ?" else "🎨 ¡Carga tu pincel resolviendo: $mathQuestion = ?",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FredokaFont,
                    color = Color(0xFFE65100)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Lienzo interactivo de pintura táctil
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .shadow(8.dp, RoundedCornerShape(20.dp))
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White)
                    .border(4.dp, Color(0xFFFF9800), RoundedCornerShape(20.dp))
            ) {
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(activeBrushColor, brushWidth, isEraserMode) {
                            detectDragGestures(
                                onDragStart = { offset ->
                                    currentPoints.clear()
                                    currentPoints.add(offset)
                                    hapticUtil?.vibrateClick()
                                },
                                onDrag = { change, _ ->
                                    change.consume()
                                    currentPoints.add(change.position)
                                },
                                onDragEnd = {
                                    if (currentPoints.isNotEmpty()) {
                                        strokes.add(ArtistCanvasStroke(currentPoints.toList(), activeBrushColor, brushWidth, isEraserMode))
                                        currentPoints.clear()
                                    }
                                },
                                onDragCancel = {
                                    currentPoints.clear()
                                }
                            )
                        }
                ) {
                    val w = size.width
                    val h = size.height

                    // Plantilla artística de fondo en trazo suave: Sol alegre y nubes
                    val sunCenter = Offset(w * 0.5f, h * 0.42f)
                    val sunRadius = 65.dp.toPx()

                    // Rayos del sol en línea suave
                    for (i in 0 until 12) {
                        val angle = (i * 30f) * (PI.toFloat() / 180f)
                        val r1 = sunRadius + 8.dp.toPx()
                        val r2 = sunRadius + 24.dp.toPx()
                        drawLine(
                            color = Color(0xFFFFE082).copy(alpha = 0.45f),
                            start = Offset(sunCenter.x + cos(angle) * r1, sunCenter.y + sin(angle) * r1),
                            end = Offset(sunCenter.x + cos(angle) * r2, sunCenter.y + sin(angle) * r2),
                            strokeWidth = 3.dp.toPx(),
                            cap = StrokeCap.Round
                        )
                    }

                    // Círculo del sol guía
                    drawCircle(
                        color = Color(0xFFFFF9C4).copy(alpha = 0.35f),
                        radius = sunRadius,
                        center = sunCenter
                    )
                    drawCircle(
                        color = Color(0xFFFFD54F).copy(alpha = 0.55f),
                        radius = sunRadius,
                        center = sunCenter,
                        style = Stroke(width = 2.dp.toPx())
                    )

                    // Nubes decorativas suaves
                    drawCircle(Color(0xFFE1F5FE).copy(alpha = 0.35f), radius = 38.dp.toPx(), center = Offset(w * 0.22f, h * 0.72f))
                    drawCircle(Color(0xFFE1F5FE).copy(alpha = 0.35f), radius = 48.dp.toPx(), center = Offset(w * 0.32f, h * 0.70f))
                    drawCircle(Color(0xFFE1F5FE).copy(alpha = 0.35f), radius = 38.dp.toPx(), center = Offset(w * 0.78f, h * 0.72f))
                    drawCircle(Color(0xFFE1F5FE).copy(alpha = 0.35f), radius = 48.dp.toPx(), center = Offset(w * 0.68f, h * 0.70f))

                    // Dibujar todos los trazos completados por el niño
                    strokes.forEach { stroke ->
                        val strokeColor = if (stroke.isEraser) Color.White else stroke.color
                        if (stroke.points.size > 1) {
                            for (i in 0 until stroke.points.size - 1) {
                                drawLine(
                                    color = strokeColor,
                                    start = stroke.points[i],
                                    end = stroke.points[i + 1],
                                    strokeWidth = stroke.strokeWidth,
                                    cap = StrokeCap.Round
                                )
                            }
                        } else if (stroke.points.size == 1) {
                            drawCircle(
                                color = strokeColor,
                                radius = stroke.strokeWidth / 2f,
                                center = stroke.points[0]
                            )
                        }
                    }

                    // Dibujar el trazo actualmente en arrastre
                    val activeColor = if (isEraserMode) Color.White else activeBrushColor
                    if (currentPoints.size > 1) {
                        for (i in 0 until currentPoints.size - 1) {
                            drawLine(
                                color = activeColor,
                                start = currentPoints[i],
                                end = currentPoints[i + 1],
                                strokeWidth = brushWidth,
                                cap = StrokeCap.Round
                            )
                        }
                    } else if (currentPoints.size == 1) {
                        drawCircle(
                            color = activeColor,
                            radius = brushWidth / 2f,
                            center = currentPoints[0]
                        )
                    }
                }

                // Marca de agua y guía
                if (strokes.isEmpty() && currentPoints.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isEnglish) "🖌️ Drag your finger to paint!" else "🖌️ ¡Arrastra tu dedo para pintar!",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FredokaFont,
                            color = SlateDark.copy(alpha = 0.40f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Barra de Herramientas del Pincel (Grosores, Borrador y Limpiar)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Color activo
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(if (isEraserMode) Color.White else activeBrushColor)
                            .border(2.dp, SlateDark, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = if (isEraserMode) "🧽" else "🖌️", fontSize = 16.sp)
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isEraserMode) (if (isEnglish) "Eraser" else "Borrador") else (if (isEnglish) "Painting" else "Pintando"),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = SlateDark
                    )
                }

                // Selector de grosor de pincel
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf(Pair("Fino", 14f), Pair("Medio", 26f), Pair("Grueso", 44f)).forEach { (name, width) ->
                        val isSelected = (brushWidth == width && !isEraserMode)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) Color(0xFFFFB300) else Color(0xFFFFF3E0))
                                .clickable {
                                    brushWidth = width
                                    isEraserMode = false
                                }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = name,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else SlateDark
                            )
                        }
                    }
                }

                // Borrador y Limpiar
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isEraserMode) Color(0xFFEF5350) else Color(0xFFFFEBEE))
                            .clickable { isEraserMode = !isEraserMode }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "🧽",
                            fontSize = 13.sp
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFFECEFF1))
                            .clickable {
                                strokes.clear()
                                soundManager?.playButtonClick()
                            }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "🗑️",
                            fontSize = 13.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Frascos de acuarela para responder la operación matemática
            Text(
                text = if (isEnglish) "🧪 Touch the correct bottle to load paint:" else "🧪 Toca el frasco correcto para cargar pintura:",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FredokaFont,
                color = SlateDark
            )
            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                paletteBottles.forEach { bottle ->
                    Box(
                        modifier = Modifier
                            .shadow(4.dp, RoundedCornerShape(16.dp))
                            .clip(RoundedCornerShape(16.dp))
                            .background(bottle.color.copy(alpha = 0.20f))
                            .border(2.5.dp, bottle.color, RoundedCornerShape(16.dp))
                            .clickable { onBottleSelected(bottle) }
                            .padding(horizontal = 20.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(bottle.color),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "🧪", fontSize = 16.sp)
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${bottle.value}",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = bottle.color
                            )
                        }
                    }
                }
            }
        }

        if (isGameOver) {
            ToyGameOverDialog(
                toyEmoji = toy.emoji,
                toyName = toy.name,
                score = score,
                coinsEarned = coinsEarned,
                isEnglish = isEnglish,
                onRetry = {
                    lives = 3
                    hasWonGame = false
                    isGameOver = false
                },
                onExit = onExitGame
            )
        }

        if (hasWonGame) {
            ToyVictoryDialog(
                toyEmoji = toy.emoji,
                toyName = toy.name,
                score = score,
                coinsEarned = coinsEarned,
                isEnglish = isEnglish,
                soundManager = soundManager,
                ttsManager = ttsManager,
                onPlayAgain = {
                    hasWonGame = false
                    correctHits = 0
                    lives = 3
                    score = 0
                    coinsEarned = 0
                    isGameOver = false
                    strokes.clear()
                    currentPoints.clear()
                    nextQuestion()
                },
                onExit = onExitGame
            )
        }
    }
}

// =========================================================================
// 11. COHETE: Misión Espacial de Planetas 🚀 (Culebrita Veloz y Choque de Planetas)
// =========================================================================

private data class CosmicPlanet(
    val id: Int,
    val pos: Offset,
    val radius: Float,
    val number: Int,
    val isCorrect: Boolean,
    val primaryColor: Color,
    val secondaryColor: Color,
    val hasRing: Boolean,
    val emoji: String
)

@Composable
fun CoheteSpaceMissionGame(
    toy: ToyItem,
    isEnglish: Boolean,
    soundManager: SoundManager?,
    ttsManager: TtsManager?,
    hapticUtil: HapticUtil?,
    difficulty: ToyGameDifficulty = ToyGameDifficulty.FACIL,
    onExitGame: () -> Unit
) {
    val context = LocalContext.current
    var lives by remember { mutableIntStateOf(3) }
    var score by remember { mutableIntStateOf(0) }
    var coinsEarned by remember { mutableIntStateOf(0) }
    var correctHits by remember { mutableIntStateOf(0) }
    var hasWonGame by remember { mutableStateOf(false) }
    var isGameOver by remember { mutableStateOf(false) }
    var isPaused by remember { mutableStateOf(false) }

    var boardWidth by remember { mutableFloatStateOf(600f) }
    var boardHeight by remember { mutableFloatStateOf(700f) }

    var rocketPos by remember { mutableStateOf(Offset(300f, 500f)) }
    var currentDir by remember { mutableStateOf(Offset(0f, -1f)) }
    var nextDir by remember { mutableStateOf(Offset(0f, -1f)) }

    // Velocidad notablemente más rápida que la culebrita tradicional (380f en fácil, 460f en difícil)
    val speed = if (difficulty == ToyGameDifficulty.FACIL) 380f else 460f

    var isInvulnerable by remember { mutableStateOf(false) }
    var invulnTimer by remember { mutableFloatStateOf(0f) }

    var mathQuestion by remember(difficulty) {
        mutableStateOf(if (difficulty == ToyGameDifficulty.FACIL) "4 + 3" else "2 × 4")
    }
    var targetNumber by remember(difficulty) {
        mutableIntStateOf(if (difficulty == ToyGameDifficulty.FACIL) 7 else 8)
    }
    var planets by remember { mutableStateOf(listOf<CosmicPlanet>()) }

    // Función para generar entre 5, 6 y 7 planetas distribuidos en el espacio
    fun spawnCosmicPlanets(target: Int, w: Float, h: Float, count: Int, rocketP: Offset): List<CosmicPlanet> {
        val wrongNums = mutableListOf<Int>()
        while (wrongNums.size < count - 1) {
            val candidate = if (Random.nextBoolean()) {
                target + Random.nextInt(1, 9)
            } else {
                (target - Random.nextInt(1, 9)).coerceAtLeast(1)
            }
            if (candidate != target && candidate !in wrongNums) wrongNums.add(candidate)
        }

        val allNums = (listOf(target) + wrongNums).shuffled()
        val planetStyles = listOf(
            Pair(Color(0xFF29B6F6), Color(0xFF0288D1)), // Azul Tierra
            Pair(Color(0xFFFFB74D), Color(0xFFE65100)), // Saturno Anillo
            Pair(Color(0xFFEF5350), Color(0xFFC62828)), // Marte Rojo
            Pair(Color(0xFFAB47BC), Color(0xFF6A1B9A)), // Neptuno Morado
            Pair(Color(0xFFFFEE58), Color(0xFFF57F17)), // Venus Dorado
            Pair(Color(0xFF26A69A), Color(0xFF004D40)), // Urano Esmeralda
            Pair(Color(0xFFB0BEC5), Color(0xFF37474F))  // Luna Plateada
        ).shuffled()

        val safeMarginX = 55f
        val safeMarginY = 65f
        val placedPositions = mutableListOf<Offset>()
        val result = mutableListOf<CosmicPlanet>()

        for (i in 0 until count) {
            val num = allNums[i]
            val isTarget = (num == target)
            val style = planetStyles[i % planetStyles.size]
            val rad = 30f

            var chosenPos = Offset(w / 2f, h / 2f)
            var attempts = 0
            while (attempts < 60) {
                val px = safeMarginX + Random.nextFloat() * (w - 2f * safeMarginX).coerceAtLeast(100f)
                val py = safeMarginY + Random.nextFloat() * (h - 2f * safeMarginY).coerceAtLeast(100f)
                val cand = Offset(px, py)
                val tooCloseToRocket = (cand - rocketP).getDistance() < 120f
                val tooCloseToOthers = placedPositions.any { (it - cand).getDistance() < 105f }
                if (!tooCloseToRocket && !tooCloseToOthers) {
                    chosenPos = cand
                    break
                }
                attempts++
            }
            placedPositions.add(chosenPos)
            result.add(
                CosmicPlanet(
                    id = i,
                    pos = chosenPos,
                    radius = rad,
                    number = num,
                    isCorrect = isTarget,
                    primaryColor = style.first,
                    secondaryColor = style.second,
                    hasRing = (i % 3 == 1),
                    emoji = "🪐"
                )
            )
        }
        return result
    }

    fun nextQuestion() {
        if (difficulty == ToyGameDifficulty.FACIL) {
            val isPlus = Random.nextBoolean()
            if (isPlus) {
                val a = Random.nextInt(1, 9)
                val b = Random.nextInt(1, 9)
                targetNumber = a + b
                mathQuestion = "$a + $b"
            } else {
                val a = Random.nextInt(4, 16)
                val b = Random.nextInt(1, a)
                targetNumber = a - b
                mathQuestion = "$a - $b"
            }
        } else {
            val isMul = Random.nextBoolean()
            if (isMul) {
                val a = Random.nextInt(2, 6)
                val b = Random.nextInt(2, 5)
                targetNumber = a * b
                mathQuestion = "$a × $b"
            } else {
                val b = Random.nextInt(2, 5)
                val ans = Random.nextInt(2, 6)
                val a = b * ans
                targetNumber = ans
                mathQuestion = "$a ÷ $b"
            }
        }

        // Aparecen aleatoriamente 5, 6 o 7 planetas en el espacio
        val planetCount = Random.nextInt(5, 8)
        planets = spawnCosmicPlanets(targetNumber, boardWidth, boardHeight, planetCount, rocketPos)
    }

    LaunchedEffect(boardWidth, boardHeight) {
        if (boardWidth > 100f && boardHeight > 100f && planets.isEmpty()) {
            rocketPos = Offset(boardWidth / 2f, boardHeight * 0.75f)
            nextQuestion()
        }
    }

    // Bucle continuo a 60 FPS con vuelo rápido del cohete
    rememberGameLoop(isRunning = !isPaused && !isGameOver && !hasWonGame) { delta ->
        if (invulnTimer > 0f) {
            invulnTimer = (invulnTimer - delta).coerceAtLeast(0f)
            if (invulnTimer == 0f) isInvulnerable = false
        }

        // Evitar giro inmediato opuesto de 180°
        val dot = currentDir.x * nextDir.x + currentDir.y * nextDir.y
        if (dot >= -0.2f) {
            currentDir = nextDir
        }

        // Avanzar el cohete velozmente en el espacio
        var nx = rocketPos.x + currentDir.x * (speed * delta)
        var ny = rocketPos.y + currentDir.y * (speed * delta)

        // Envoltura espacial en bordes de la pantalla (wrap-around)
        if (nx < 0f) nx = boardWidth
        else if (nx > boardWidth) nx = 0f
        if (ny < 0f) ny = boardHeight
        else if (ny > boardHeight) ny = 0f

        rocketPos = Offset(nx, ny)

        // Detección de colisión contra los planetas
        val rocketHitRadius = 26f
        for (planet in planets) {
            val dist = (rocketPos - planet.pos).getDistance()
            if (dist < (rocketHitRadius + planet.radius)) {
                if (planet.isCorrect) {
                    // ¡COLISIÓN CORRECTA! Se estrelló contra el planeta acertado
                    score += 25
                    coinsEarned += 1
                    correctHits++
                    CoinShopManager.addCoin(context, 1)
                    soundManager?.playVictory()
                    hapticUtil?.vibrateClick()
                    ttsManager?.speak(if (isEnglish) "Great! Landed on planet ${planet.number}!" else "¡Genial! ¡Chocaste con el planeta ${planet.number}!")

                    if (correctHits >= 20) {
                        hasWonGame = true
                    } else {
                        nextQuestion()
                    }
                    return@rememberGameLoop
                } else {
                    // ¡COLISIÓN INCORRECTA! Chocó con planeta erróneo -> Pierde vida
                    if (!isInvulnerable) {
                        lives = (lives - 1).coerceAtLeast(0)
                        soundManager?.playIncorrect()
                        hapticUtil?.vibrateEncourage()
                        ttsManager?.speak(if (isEnglish) "Wrong planet! Watch out!" else "¡Planeta equivocado! ¡Cuidado!")

                        isInvulnerable = true
                        invulnTimer = 1.3f

                        // Rebote inmediato hacia atrás
                        nextDir = -currentDir
                        currentDir = -currentDir

                        if (lives == 0) {
                            isGameOver = true
                        }
                        return@rememberGameLoop
                    }
                }
            }
        }
    }

    fun turn(dir: Offset) {
        val dot = currentDir.x * dir.x + currentDir.y * dir.y
        if (dot < -0.2f) return
        nextDir = dir
        soundManager?.playButtonClick()
        hapticUtil?.vibrateClick()
    }

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF0B132B))) {
        Column(
            modifier = Modifier.fillMaxSize().padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ToyGameTopBar(
                title = if (isEnglish) "Rocket Planets" else "Cohete Espacial",
                toyEmoji = toy.emoji,
                lives = lives,
                coins = coinsEarned,
                correctHits = correctHits,
                targetHits = 20,
                difficulty = difficulty,
                isEnglish = isEnglish,
                onExitClick = onExitGame,
                onPauseClick = { isPaused = !isPaused }
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Cartel de Misión Espacial
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFF1C2541))
                    .border(2.dp, Color(0xFF48CAE4), RoundedCornerShape(14.dp))
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = if (isEnglish) "🚀 Crash into planet: $mathQuestion = ?" else "🚀 Choca contra el planeta: $mathQuestion = ?",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FredokaFont,
                    color = Color(0xFFE0FBFC)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Campo espacial interactivo tipo culebrita rápida
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .shadow(8.dp, RoundedCornerShape(20.dp))
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF0D1B2A))
                    .border(3.dp, Color(0xFF1F4E79), RoundedCornerShape(20.dp))
                    .onSizeChanged {
                        boardWidth = it.width.toFloat()
                        boardHeight = it.height.toFloat()
                    }
                    .pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            if (kotlin.math.abs(dragAmount.x) > kotlin.math.abs(dragAmount.y)) {
                                if (dragAmount.x > 8f) turn(Offset(1f, 0f))
                                else if (dragAmount.x < -8f) turn(Offset(-1f, 0f))
                            } else {
                                if (dragAmount.y > 8f) turn(Offset(0f, 1f))
                                else if (dragAmount.y < -8f) turn(Offset(0f, -1f))
                            }
                        }
                    }
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height

                    // Estrellas titilantes de fondo
                    val starCount = 35
                    for (i in 0 until starCount) {
                        val sx = (i * 97f) % w
                        val sy = (i * 139f) % h
                        val sSize = if (i % 3 == 0) 2.5.dp.toPx() else 1.5.dp.toPx()
                        drawCircle(
                            color = Color.White.copy(alpha = if (i % 2 == 0) 0.85f else 0.45f),
                            radius = sSize,
                            center = Offset(sx, sy)
                        )
                    }

                    // 1. Dibujar los 5 a 7 planetas con sus números
                    planets.forEach { planet ->
                        val px = planet.pos.x
                        val py = planet.pos.y
                        val rad = planet.radius.dp.toPx()

                        // Halo atmosférico brillante
                        drawCircle(
                            color = planet.primaryColor.copy(alpha = 0.28f),
                            radius = rad + 9.dp.toPx(),
                            center = planet.pos
                        )

                        // Anillo de Saturno
                        if (planet.hasRing) {
                            drawOval(
                                color = planet.secondaryColor.copy(alpha = 0.70f),
                                topLeft = Offset(px - rad * 1.55f, py - rad * 0.45f),
                                size = Size(rad * 3.1f, rad * 0.9f),
                                style = Stroke(width = 4.dp.toPx())
                            )
                        }

                        // Cuerpo del planeta
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(planet.primaryColor, planet.secondaryColor),
                                center = Offset(px - rad * 0.25f, py - rad * 0.25f),
                                radius = rad * 1.2f
                            ),
                            radius = rad,
                            center = planet.pos
                        )

                        // Borde de planeta
                        drawCircle(
                            color = Color.White.copy(alpha = 0.60f),
                            radius = rad,
                            center = planet.pos,
                            style = Stroke(width = 2.dp.toPx())
                        )

                        // Placa circular oscura interior para máxima legibilidad del número
                        drawCircle(
                            color = Color(0xFF0B132B).copy(alpha = 0.78f),
                            radius = rad * 0.65f,
                            center = planet.pos
                        )

                        // Número grande y nítido dentro del planeta
                        val numPaint = android.graphics.Paint().apply {
                            textSize = 19.sp.toPx()
                            color = android.graphics.Color.WHITE
                            textAlign = android.graphics.Paint.Align.CENTER
                            isFakeBoldText = true
                            isAntiAlias = true
                        }
                        val textY = py - ((numPaint.descent() + numPaint.ascent()) / 2f)
                        drawContext.canvas.nativeCanvas.drawText("${planet.number}", px, textY, numPaint)
                    }

                    // 2. Dibujar el cohete rápido navegando
                    val rx = rocketPos.x
                    val ry = rocketPos.y
                    val rocketAngle = (atan2(currentDir.y, currentDir.x) * (180f / PI.toFloat())) + 90f

                    drawContext.canvas.nativeCanvas.save()
                    drawContext.canvas.nativeCanvas.translate(rx, ry)
                    drawContext.canvas.nativeCanvas.rotate(rocketAngle)

                    val alphaRocket = if (isInvulnerable && (invulnTimer * 8).toInt() % 2 == 0) 0.35f else 1.0f

                    // Fuego propulsor
                    drawOval(
                        brush = Brush.verticalGradient(
                            listOf(Color(0xFFFFEE58), Color(0xFFFF5722), Color.Transparent)
                        ),
                        topLeft = Offset(-7.dp.toPx(), 16.dp.toPx()),
                        size = Size(14.dp.toPx(), 20.dp.toPx())
                    )

                    // Aletas laterales rojas
                    drawPath(
                        path = androidx.compose.ui.graphics.Path().apply {
                            moveTo(-12.dp.toPx(), 16.dp.toPx())
                            lineTo(-20.dp.toPx(), 22.dp.toPx())
                            lineTo(-10.dp.toPx(), 6.dp.toPx())
                            close()
                        },
                        color = Color(0xFFD32F2F).copy(alpha = alphaRocket)
                    )
                    drawPath(
                        path = androidx.compose.ui.graphics.Path().apply {
                            moveTo(12.dp.toPx(), 16.dp.toPx())
                            lineTo(20.dp.toPx(), 22.dp.toPx())
                            lineTo(10.dp.toPx(), 6.dp.toPx())
                            close()
                        },
                        color = Color(0xFFD32F2F).copy(alpha = alphaRocket)
                    )

                    // Fuselaje blanco del cohete
                    drawRoundRect(
                        color = Color.White.copy(alpha = alphaRocket),
                        topLeft = Offset(-11.dp.toPx(), -15.dp.toPx()),
                        size = Size(22.dp.toPx(), 30.dp.toPx()),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(10.dp.toPx(), 10.dp.toPx())
                    )

                    // Punta cónica aerodinámica roja
                    drawPath(
                        path = androidx.compose.ui.graphics.Path().apply {
                            moveTo(0f, -28.dp.toPx())
                            lineTo(-11.dp.toPx(), -15.dp.toPx())
                            lineTo(11.dp.toPx(), -15.dp.toPx())
                            close()
                        },
                        color = Color(0xFFE53935).copy(alpha = alphaRocket)
                    )

                    // Ventana / Ojo de buey cian
                    drawCircle(
                        color = Color(0xFF00E5FF).copy(alpha = alphaRocket),
                        radius = 5.5.dp.toPx(),
                        center = Offset(0f, -4.dp.toPx())
                    )
                    drawCircle(
                        color = Color(0xFF01579B).copy(alpha = alphaRocket),
                        radius = 5.5.dp.toPx(),
                        center = Offset(0f, -4.dp.toPx()),
                        style = Stroke(width = 1.5.dp.toPx())
                    )

                    drawContext.canvas.nativeCanvas.restore()
                }
            }
        }

        if (isGameOver) {
            ToyGameOverDialog(
                toyEmoji = toy.emoji,
                toyName = toy.name,
                score = score,
                coinsEarned = coinsEarned,
                isEnglish = isEnglish,
                onRetry = {
                    lives = 3
                    hasWonGame = false
                    isGameOver = false
                    isInvulnerable = true
                    invulnTimer = 2.0f
                    rocketPos = Offset(boardWidth / 2f, boardHeight * 0.75f)
                    currentDir = Offset(0f, -1f)
                    nextDir = Offset(0f, -1f)
                },
                onExit = onExitGame
            )
        }

        if (hasWonGame) {
            ToyVictoryDialog(
                toyEmoji = toy.emoji,
                toyName = toy.name,
                score = score,
                coinsEarned = coinsEarned,
                isEnglish = isEnglish,
                soundManager = soundManager,
                ttsManager = ttsManager,
                onPlayAgain = {
                    hasWonGame = false
                    correctHits = 0
                    lives = 3
                    score = 0
                    coinsEarned = 0
                    isGameOver = false
                    nextQuestion()
                },
                onExit = onExitGame
            )
        }
    }
}

// =========================================================================
// 12. OSO: Atrapa Miel del Osito 🧸 (Canasta y miel cayendo en 2D)
// =========================================================================
@Composable
fun OsoCatcherGame(
    toy: ToyItem,
    isEnglish: Boolean,
    soundManager: SoundManager?,
    ttsManager: TtsManager?,
    hapticUtil: HapticUtil?,
    difficulty: ToyGameDifficulty = ToyGameDifficulty.FACIL,
    onExitGame: () -> Unit
) {
    val context = LocalContext.current
    var lives by remember { mutableIntStateOf(3) }
    var score by remember { mutableIntStateOf(0) }
    var coinsEarned by remember { mutableIntStateOf(0) }
    var correctHits by remember { mutableIntStateOf(0) }
    var hasWonGame by remember { mutableStateOf(false) }
    var isGameOver by remember { mutableStateOf(false) }

    var mathQuestion by remember(difficulty) {
        mutableStateOf(if (difficulty == ToyGameDifficulty.FACIL) "3 + 2" else "2 × 3")
    }
    var targetNumber by remember(difficulty) {
        mutableIntStateOf(if (difficulty == ToyGameDifficulty.FACIL) 5 else 6)
    }
    var bearXOffset by remember { mutableFloatStateOf(0f) }

    val honeyOptions = remember(targetNumber) {
        val wrong1 = targetNumber + Random.nextInt(1, 4)
        val wrong2 = (targetNumber - Random.nextInt(1, 4)).coerceAtLeast(1)
        listOf(targetNumber, wrong1, if (wrong2 != targetNumber && wrong2 != wrong1) wrong2 else targetNumber + 5).shuffled()
    }

    val dragOffsets = remember(targetNumber) { mutableStateMapOf<Int, Offset>() }
    val isDraggingMap = remember(targetNumber) { mutableStateMapOf<Int, Boolean>() }

    fun nextQuestion() {
        dragOffsets.clear()
        isDraggingMap.clear()
        if (difficulty == ToyGameDifficulty.FACIL) {
            val isPlus = Random.nextBoolean()
            if (isPlus) {
                val a = Random.nextInt(1, 9)
                val b = Random.nextInt(1, 9)
                targetNumber = a + b
                mathQuestion = "$a + $b"
            } else {
                val a = Random.nextInt(4, 16)
                val b = Random.nextInt(1, a)
                targetNumber = a - b
                mathQuestion = "$a - $b"
            }
        } else {
            val isMul = Random.nextBoolean()
            if (isMul) {
                val a = Random.nextInt(2, 6)
                val b = Random.nextInt(2, 5)
                targetNumber = a * b
                mathQuestion = "$a × $b"
            } else {
                val b = Random.nextInt(2, 5)
                val ans = Random.nextInt(2, 6)
                val a = b * ans
                targetNumber = ans
                mathQuestion = "$a ÷ $b"
            }
        }
    }

    LaunchedEffect(difficulty) {
        nextQuestion()
    }

    fun onCatchHoney(valCatch: Int) {
        if (valCatch == targetNumber) {
            score += 20
            coinsEarned += 1
            correctHits++
            CoinShopManager.addCoin(context, 1)
            soundManager?.playVictory()
            hapticUtil?.vibrateClick()
            ttsManager?.speak(if (isEnglish) "Yummy honey into the basket!" else "¡Deliciosa miel en la cesta!")
            if (correctHits >= 30) {
                hasWonGame = true
            }
            nextQuestion()
        } else {
            lives = (lives - 1).coerceAtLeast(0)
            soundManager?.playIncorrect()
            hapticUtil?.vibrateEncourage()
            dragOffsets[valCatch] = Offset.Zero
            isDraggingMap[valCatch] = false
            if (lives == 0) isGameOver = true
        }
    }

    val anyDragging = isDraggingMap.values.any { it }
    val activeDraggingPot = isDraggingMap.entries.firstOrNull { it.value }?.key
    val activeOffset = activeDraggingPot?.let { dragOffsets[it] } ?: Offset.Zero
    val isNearBasket = anyDragging && activeOffset.y < -55f

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFFFFF3E0))) {
        Column(
            modifier = Modifier.fillMaxSize().padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ToyGameTopBar(
                title = if (isEnglish) "Teddy Honey Catcher" else "Atrapa Miel del Osito",
                toyEmoji = toy.emoji,
                lives = lives,
                coins = coinsEarned,
                correctHits = correctHits,
                targetHits = 30,
                difficulty = difficulty,
                isEnglish = isEnglish,
                onExitClick = onExitGame
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Bosque del osito y su cesta
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .shadow(6.dp, RoundedCornerShape(24.dp))
                    .clip(RoundedCornerShape(24.dp))
                    .background(SurfaceWhite)
                    .border(3.dp, Color(0xFFFFB300), RoundedCornerShape(24.dp))
                    .padding(14.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = if (isEnglish) "🍯 Drag the honey pot into the basket: $mathQuestion = ?"
                        else "🍯 ¡Arrastra el tarro a la cesta resolviendo: $mathQuestion = ?",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FredokaFont,
                        color = Color(0xFFE65100),
                        textAlign = TextAlign.Center
                    )

                    // Osito con su cesta receptora de miel
                    Box(
                        modifier = Modifier
                            .offset(x = bearXOffset.dp)
                            .shadow(
                                elevation = if (isNearBasket) 8.dp else 2.dp,
                                shape = RoundedCornerShape(22.dp),
                                spotColor = Color(0xFFFF8F00).copy(alpha = 0.4f)
                            )
                            .clip(RoundedCornerShape(22.dp))
                            .background(if (isNearBasket) Color(0xFFFFF59D) else Color(0xFFFFF8E1))
                            .border(
                                width = if (isNearBasket) 3.5.dp else 2.dp,
                                color = if (isNearBasket) Color(0xFFFF6F00) else Color(0xFFFFB300),
                                shape = RoundedCornerShape(22.dp)
                            )
                            .padding(horizontal = 24.dp, vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "🧸", fontSize = 54.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "🧺", fontSize = 34.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isNearBasket) {
                                        if (isEnglish) "Drop here!" else "¡Suelta aquí!"
                                    } else {
                                        if (isEnglish) "Bear's Basket" else "Cesta del Oso"
                                    },
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Black,
                                    fontFamily = FredokaFont,
                                    color = Color(0xFFE65100)
                                )
                            }
                        }
                    }

                    Text(text = "🌲 🌳 🐝 🌳 🌲", fontSize = 22.sp)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Tarros de miel interactivos (arrastrables)
            Text(
                text = if (isEnglish) "🍯 Drag the correct honey jar up into the basket:"
                else "🍯 Arrastra con tu dedo el tarro correcto hacia la cesta:",
                fontSize = 13.5.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FredokaFont,
                color = SlateDark,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))

            val density = LocalDensity.current
            val dropThresholdPx = with(density) { 70.dp.toPx() }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                honeyOptions.forEach { potVal ->
                    val offset = dragOffsets[potVal] ?: Offset.Zero
                    val isDragging = isDraggingMap[potVal] ?: false

                    Box(
                        modifier = Modifier
                            .zIndex(if (isDragging) 20f else 1f)
                            .offset {
                                IntOffset(
                                    offset.x.roundToInt(),
                                    offset.y.roundToInt()
                                )
                            }
                            .graphicsLayer {
                                scaleX = if (isDragging) 1.18f else 1f
                                scaleY = if (isDragging) 1.18f else 1f
                                shadowElevation = if (isDragging) 16.dp.toPx() else 4.dp.toPx()
                            }
                            .shadow(if (isDragging) 8.dp else 4.dp, RoundedCornerShape(18.dp))
                            .clip(RoundedCornerShape(18.dp))
                            .background(if (isDragging) Color(0xFFFFF9C4) else Color(0xFFFFF8E1))
                            .border(
                                width = if (isDragging) 3.dp else 2.dp,
                                color = if (isDragging) Color(0xFFFF8F00) else Color(0xFFFFC107),
                                shape = RoundedCornerShape(18.dp)
                            )
                            .pointerInput(potVal, targetNumber) {
                                detectDragGestures(
                                    onDragStart = {
                                        isDraggingMap[potVal] = true
                                        hapticUtil?.vibrateClick()
                                    },
                                    onDrag = { change, dragAmount ->
                                        change.consume()
                                        val prev = dragOffsets[potVal] ?: Offset.Zero
                                        dragOffsets[potVal] = prev + dragAmount
                                    },
                                    onDragEnd = {
                                        isDraggingMap[potVal] = false
                                        val current = dragOffsets[potVal] ?: Offset.Zero
                                        // Si se arrastró hacia arriba hacia la cesta del oso:
                                        if (current.y < -dropThresholdPx) {
                                            onCatchHoney(potVal)
                                            dragOffsets[potVal] = Offset.Zero
                                        } else if (current.getDistance() < 18f) {
                                            // Toque simple: también permite interactuar
                                            onCatchHoney(potVal)
                                            dragOffsets[potVal] = Offset.Zero
                                        } else {
                                            // Suelto en otro lugar, regresa a su posición original
                                            dragOffsets[potVal] = Offset.Zero
                                        }
                                    },
                                    onDragCancel = {
                                        isDraggingMap[potVal] = false
                                        dragOffsets[potVal] = Offset.Zero
                                    }
                                )
                            }
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "🍯", fontSize = 28.sp)
                            Text(
                                text = "$potVal",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFFE65100)
                            )
                        }
                    }
                }
            }
        }

        if (isGameOver) {
            ToyGameOverDialog(
                toyEmoji = toy.emoji,
                toyName = toy.name,
                score = score,
                coinsEarned = coinsEarned,
                isEnglish = isEnglish,
                onRetry = {
                    lives = 3
                    hasWonGame = false
                    isGameOver = false
                    bearXOffset = 0f
                },
                onExit = onExitGame
            )
        }

        if (hasWonGame) {
            ToyVictoryDialog(
                toyEmoji = toy.emoji,
                toyName = toy.name,
                score = score,
                coinsEarned = coinsEarned,
                isEnglish = isEnglish,
                soundManager = soundManager,
                ttsManager = ttsManager,
                onPlayAgain = {
                    hasWonGame = false
                    correctHits = 0
                    lives = 3
                    score = 0
                    coinsEarned = 0
                    isGameOver = false
                    bearXOffset = 0f
                    nextQuestion()
                },
                onExit = onExitGame
            )
        }
    }
}

// =========================================================================
// 13. PATINETA: Skate Runner de Subidas y Bajadas 🛹 (Carrera, saltos y números)
// =========================================================================

enum class SkateItemType {
    OBSTACLE,       // 🚧 Valla u 🪨 roca en el suelo (¡Hay que saltarlo!)
    CORRECT_NUMBER, // ⭐ Número correcto de la ecuación (¡Hay que agarrarlo!)
    WRONG_NUMBER    // ❌ / 🟠 Número incorrecto distractor (¡Hay que esquivarlo!)
}

data class SkateRoadItem(
    val id: Long,
    val worldX: Float,
    val type: SkateItemType,
    var value: Int = 0,
    val icon: String = "🚧",
    val isAirborne: Boolean = false,
    var isResolved: Boolean = false
)

private fun getSkateTerrainY(worldX: Float, baseHeight: Float): Float {
    // Ondulaciones suaves en línea recta formando subidas y bajadas
    val hill1 = sin(worldX * 0.0035f) * 45f
    val hill2 = cos(worldX * 0.0075f) * 22f
    return baseHeight + hill1 + hill2
}

private fun getSkateSlopeAngle(worldX: Float, baseHeight: Float): Float {
    val delta = 8f
    val y1 = getSkateTerrainY(worldX - delta, baseHeight)
    val y2 = getSkateTerrainY(worldX + delta, baseHeight)
    val angleRad = atan2(y2 - y1, delta * 2f)
    return (angleRad * (180f / PI)).toFloat()
}

/**
 * Renderiza el muñeco skater completo con cabeza, gorra deportiva, buzo hoodie con estrella,
 * brazos en equilibrio, pantalones jeans, zapatillas deportivas y patineta turbo con luces LED.
 */
@Composable
fun SkaterMuñecoView(
    isJumping: Boolean,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.size(76.dp, 82.dp)) {
        val w = size.width
        val h = size.height
        val cx = w * 0.50f

        // 1. LUZ NEON BAJO LA TABLA ("Patineta Turbo con Luces")
        drawOval(
            brush = Brush.horizontalGradient(
                listOf(
                    Color(0xFF00E5FF).copy(alpha = 0.1f),
                    Color(0xFF00E5FF).copy(alpha = 0.85f),
                    Color(0xFFFF4081).copy(alpha = 0.85f),
                    Color(0xFF00E5FF).copy(alpha = 0.1f)
                )
            ),
            topLeft = Offset(w * 0.10f, h * 0.85f),
            size = Size(w * 0.80f, h * 0.08f)
        )

        // 2. TABLA DE PATINETA (Deck de madera con lija oscura y líneas deportivas)
        // Núcleo de madera maple
        drawRoundRect(
            color = Color(0xFFFF9800),
            topLeft = Offset(w * 0.13f, h * 0.79f),
            size = Size(w * 0.74f, h * 0.07f),
            cornerRadius = CornerRadius(6f, 6f)
        )
        // Lija superior antideslizante
        drawRoundRect(
            color = Color(0xFF263238),
            topLeft = Offset(w * 0.14f, h * 0.79f),
            size = Size(w * 0.72f, h * 0.045f),
            cornerRadius = CornerRadius(5f, 5f)
        )
        // Franja deportiva en la lija
        drawRoundRect(
            color = Color(0xFFFFD54F),
            topLeft = Offset(w * 0.28f, h * 0.805f),
            size = Size(w * 0.44f, h * 0.015f),
            cornerRadius = CornerRadius(2f, 2f)
        )

        // 3. EJES METÁLICOS (Trucks)
        drawRect(
            color = Color(0xFFB0BEC5),
            topLeft = Offset(w * 0.26f, h * 0.86f),
            size = Size(w * 0.06f, h * 0.04f)
        )
        drawRect(
            color = Color(0xFFB0BEC5),
            topLeft = Offset(w * 0.68f, h * 0.86f),
            size = Size(w * 0.06f, h * 0.04f)
        )

        // 4. RUEDAS TURBO DEPORTIVAS CON LUCES
        // Rueda delantera
        val frontWheelCenter = Offset(w * 0.29f, h * 0.915f)
        drawCircle(color = Color(0xFF37474F), radius = w * 0.075f, center = frontWheelCenter)
        drawCircle(color = Color(0xFFFF3D00), radius = w * 0.05f, center = frontWheelCenter)
        drawCircle(color = Color(0xFF00E5FF), radius = w * 0.025f, center = frontWheelCenter)
        drawCircle(color = Color.White, radius = w * 0.012f, center = frontWheelCenter)

        // Rueda trasera
        val rearWheelCenter = Offset(w * 0.71f, h * 0.915f)
        drawCircle(color = Color(0xFF37474F), radius = w * 0.075f, center = rearWheelCenter)
        drawCircle(color = Color(0xFFFF3D00), radius = w * 0.05f, center = rearWheelCenter)
        drawCircle(color = Color(0xFF00E5FF), radius = w * 0.025f, center = rearWheelCenter)
        drawCircle(color = Color.White, radius = w * 0.012f, center = rearWheelCenter)

        // 5. ZAPATILLAS DEPORTIVAS DE SKATER (Sneakers rojas y blancas)
        // Pie delantero (izquierdo)
        drawRoundRect(
            color = Color(0xFFE53935),
            topLeft = Offset(w * 0.23f, h * 0.70f),
            size = Size(w * 0.17f, h * 0.09f),
            cornerRadius = CornerRadius(5f, 5f)
        )
        drawRoundRect(
            color = Color.White,
            topLeft = Offset(w * 0.22f, h * 0.765f),
            size = Size(w * 0.18f, h * 0.026f),
            cornerRadius = CornerRadius(3f, 3f)
        )
        drawCircle(color = Color.White, radius = w * 0.025f, center = Offset(w * 0.24f, h * 0.74f))

        // Pie trasero (derecho)
        drawRoundRect(
            color = Color(0xFFD32F2F),
            topLeft = Offset(w * 0.58f, h * 0.70f),
            size = Size(w * 0.17f, h * 0.09f),
            cornerRadius = CornerRadius(5f, 5f)
        )
        drawRoundRect(
            color = Color.White,
            topLeft = Offset(w * 0.57f, h * 0.765f),
            size = Size(w * 0.18f, h * 0.026f),
            cornerRadius = CornerRadius(3f, 3f)
        )
        drawCircle(color = Color.White, radius = w * 0.025f, center = Offset(w * 0.73f, h * 0.74f))

        // 6. PIERNAS Y PANTALONES (Jeans azules en postura de patinaje)
        val kneeBend = if (isJumping) -h * 0.04f else 0f
        // Pierna delantera
        drawLine(
            color = Color(0xFF1E88E5),
            start = Offset(cx - w * 0.05f, h * 0.52f + kneeBend),
            end = Offset(w * 0.31f, h * 0.72f),
            strokeWidth = w * 0.11f,
            cap = StrokeCap.Round
        )
        // Pierna trasera
        drawLine(
            color = Color(0xFF1565C0),
            start = Offset(cx + w * 0.05f, h * 0.52f + kneeBend),
            end = Offset(w * 0.66f, h * 0.72f),
            strokeWidth = w * 0.11f,
            cap = StrokeCap.Round
        )
        // Cadera / shorts
        drawRoundRect(
            color = Color(0xFF1976D2),
            topLeft = Offset(cx - w * 0.14f, h * 0.49f + kneeBend),
            size = Size(w * 0.28f, h * 0.09f),
            cornerRadius = CornerRadius(6f, 6f)
        )

        // 7. CUERPO / TORSO (Buzo hoodie deportivo con capucha y estrella dorada)
        val torsoTop = h * 0.32f + kneeBend * 0.5f
        drawRoundRect(
            brush = Brush.verticalGradient(
                listOf(Color(0xFFFF7043), Color(0xFFE64A19))
            ),
            topLeft = Offset(cx - w * 0.18f, torsoTop),
            size = Size(w * 0.36f, h * 0.20f),
            cornerRadius = CornerRadius(10f, 10f)
        )
        // Insignia de estrella en el pecho
        drawCircle(
            color = Color(0xFFFFD54F),
            radius = w * 0.055f,
            center = Offset(cx, torsoTop + h * 0.10f)
        )
        drawCircle(
            color = Color(0xFFFFA000),
            radius = w * 0.03f,
            center = Offset(cx, torsoTop + h * 0.10f)
        )

        // 8. BRAZOS EN EQUILIBRIO DEPORTIVO
        // Brazo izquierdo (adelante)
        val leftHandY = if (isJumping) torsoTop + h * 0.04f else torsoTop + h * 0.10f
        drawLine(
            color = Color(0xFFFF7043),
            start = Offset(cx - w * 0.14f, torsoTop + h * 0.06f),
            end = Offset(w * 0.08f, leftHandY),
            strokeWidth = w * 0.09f,
            cap = StrokeCap.Round
        )
        drawCircle(color = Color(0xFFFFCC80), radius = w * 0.042f, center = Offset(w * 0.06f, leftHandY))

        // Brazo derecho (atrás, balanceando en alto)
        val rightHandY = if (isJumping) torsoTop - h * 0.08f else torsoTop + h * 0.02f
        drawLine(
            color = Color(0xFFD84315),
            start = Offset(cx + w * 0.14f, torsoTop + h * 0.06f),
            end = Offset(w * 0.90f, rightHandY),
            strokeWidth = w * 0.09f,
            cap = StrokeCap.Round
        )
        drawCircle(color = Color(0xFFFFCC80), radius = w * 0.042f, center = Offset(w * 0.92f, rightHandY))

        // Cuello del buzo
        drawOval(
            color = Color(0xFFD84315),
            topLeft = Offset(cx - w * 0.11f, torsoTop - h * 0.025f),
            size = Size(w * 0.22f, h * 0.055f)
        )

        // 9. CABEZA Y ROSTRO
        val headY = torsoTop - h * 0.12f
        // Cuello
        drawRect(
            color = Color(0xFFFFB74D),
            topLeft = Offset(cx - w * 0.045f, headY + h * 0.06f),
            size = Size(w * 0.09f, h * 0.06f)
        )
        // Cabeza
        drawCircle(color = Color(0xFFFFCC80), radius = w * 0.15f, center = Offset(cx, headY))
        // Orejitas
        drawCircle(color = Color(0xFFFFB74D), radius = w * 0.035f, center = Offset(cx - w * 0.15f, headY))
        drawCircle(color = Color(0xFFFFB74D), radius = w * 0.035f, center = Offset(cx + w * 0.15f, headY))

        // Cabello castaño que asoma
        drawOval(
            color = Color(0xFF5D4037),
            topLeft = Offset(cx - w * 0.14f, headY - h * 0.07f),
            size = Size(w * 0.28f, h * 0.09f)
        )

        // 10. GORRA DE SKATER DEPORTIVA (Gorra hacia atrás con visera)
        drawArc(
            color = Color(0xFF1E88E5),
            startAngle = 180f,
            sweepAngle = 180f,
            useCenter = true,
            topLeft = Offset(cx - w * 0.16f, headY - h * 0.14f),
            size = Size(w * 0.32f, h * 0.18f)
        )
        // Botón de la gorra
        drawCircle(color = Color(0xFFFFCA28), radius = w * 0.028f, center = Offset(cx, headY - h * 0.14f))
        // Visera trasera
        drawRoundRect(
            color = Color(0xFF1565C0),
            topLeft = Offset(cx + w * 0.08f, headY - h * 0.07f),
            size = Size(w * 0.16f, h * 0.04f),
            cornerRadius = CornerRadius(4f, 4f)
        )

        // 11. DETALLES DEL ROSTRO (Ojos brillantes, mejillas sonrosadas y gran sonrisa)
        // Ojo izquierdo
        drawOval(
            color = Color(0xFF212121),
            topLeft = Offset(cx - w * 0.09f, headY - h * 0.025f),
            size = Size(w * 0.055f, h * 0.07f)
        )
        drawCircle(color = Color.White, radius = w * 0.018f, center = Offset(cx - w * 0.075f, headY - h * 0.01f))

        // Ojo derecho
        drawOval(
            color = Color(0xFF212121),
            topLeft = Offset(cx + w * 0.035f, headY - h * 0.025f),
            size = Size(w * 0.055f, h * 0.07f)
        )
        drawCircle(color = Color.White, radius = w * 0.018f, center = Offset(cx + w * 0.05f, headY - h * 0.01f))

        // Mejillas rosadas
        drawCircle(color = Color(0xFFFF8A80).copy(alpha = 0.55f), radius = w * 0.035f, center = Offset(cx - w * 0.10f, headY + h * 0.035f))
        drawCircle(color = Color(0xFFFF8A80).copy(alpha = 0.55f), radius = w * 0.035f, center = Offset(cx + w * 0.10f, headY + h * 0.035f))

        // Sonrisa alegre
        drawArc(
            color = Color(0xFFBF360C),
            startAngle = 0f,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = Offset(cx - w * 0.055f, headY + h * 0.015f),
            size = Size(w * 0.11f, h * 0.055f),
            style = Stroke(width = 3.5f)
        )
    }
}

@Composable
fun PatinetaSkateParkGame(
    toy: ToyItem,
    isEnglish: Boolean,
    soundManager: SoundManager?,
    ttsManager: TtsManager?,
    hapticUtil: HapticUtil?,
    difficulty: ToyGameDifficulty = ToyGameDifficulty.FACIL,
    onExitGame: () -> Unit
) {
    val context = LocalContext.current
    var lives by remember { mutableIntStateOf(3) }
    var score by remember { mutableIntStateOf(0) }
    var coinsEarned by remember { mutableIntStateOf(0) }
    var correctHits by remember { mutableIntStateOf(0) }
    var hasWonGame by remember { mutableStateOf(false) }
    var isGameOver by remember { mutableStateOf(false) }
    var isPaused by remember { mutableStateOf(false) }

    var mathQuestion by remember(difficulty) {
        mutableStateOf(if (difficulty == ToyGameDifficulty.FACIL) "3 + 3" else "3 × 4")
    }
    var targetNumber by remember(difficulty) {
        mutableIntStateOf(if (difficulty == ToyGameDifficulty.FACIL) 6 else 12)
    }
    var distractors by remember { mutableStateOf(listOf(10, 14)) }
    val items = remember { mutableStateListOf<SkateRoadItem>() }

    fun nextQuestion() {
        val prevQ = mathQuestion
        if (difficulty == ToyGameDifficulty.FACIL) {
            val isPlus = Random.nextBoolean()
            if (isPlus) {
                val a = Random.nextInt(1, 10)
                val b = Random.nextInt(1, 10)
                targetNumber = a + b
                mathQuestion = "$a + $b"
            } else {
                val a = Random.nextInt(4, 18)
                val b = Random.nextInt(1, a)
                targetNumber = a - b
                mathQuestion = "$a - $b"
            }
        } else {
            // MODO MODERADO OPTIMIZADO: Tablas de multiplicar (2 a 9) y divisiones exactas dinámicas y balanceadas
            var newQ = prevQ
            var newAns = targetNumber
            var attempts = 0
            while ((newQ == prevQ || attempts == 0) && attempts < 10) {
                val isMul = Random.nextBoolean()
                if (isMul) {
                    val a = Random.nextInt(2, 10)
                    val b = Random.nextInt(2, 10)
                    newAns = a * b
                    newQ = "$a × $b"
                } else {
                    val b = Random.nextInt(2, 9)
                    val ans = Random.nextInt(2, 10)
                    val a = b * ans
                    newAns = ans
                    newQ = "$a ÷ $b"
                }
                attempts++
            }
            targetNumber = newAns
            mathQuestion = newQ
        }

        // Generar distractores inteligentes, distintos al resultado y estrictamente positivos
        val distSet = mutableSetOf<Int>()
        val offsets = listOf(-3, -2, -1, 1, 2, 3, 4, 5).shuffled()
        for (off in offsets) {
            val cand = targetNumber + off
            if (cand > 0 && cand != targetNumber) {
                distSet.add(cand)
                if (distSet.size >= 2) break
            }
        }
        var filler = 1
        while (distSet.size < 2) {
            val cand = targetNumber + filler * 2
            if (cand != targetNumber && !distSet.contains(cand)) {
                distSet.add(cand)
            }
            filler++
        }
        distractors = distSet.toList()

        // Sincronizar dinámicamente los ítems numéricos activos que ya están en la pista
        items.forEach { item ->
            if (!item.isResolved) {
                if (item.type == SkateItemType.CORRECT_NUMBER) {
                    item.value = targetNumber
                } else if (item.type == SkateItemType.WRONG_NUMBER) {
                    item.value = distractors.random()
                }
            }
        }
    }

    LaunchedEffect(difficulty) {
        nextQuestion()
    }

    // Runner physics & state
    var worldX by remember { mutableFloatStateOf(0f) }
    var jumpOffsetY by remember { mutableFloatStateOf(0f) }
    var jumpVelocity by remember { mutableFloatStateOf(0f) }
    var invulnerableTimer by remember { mutableFloatStateOf(0f) }
    var jumpOllieAngle by remember { mutableFloatStateOf(0f) }
    var nextSpawnX by remember { mutableFloatStateOf(450f) }
    var itemIdCounter by remember { mutableLongStateOf(1L) }
    var skateTrackWidth by remember { mutableFloatStateOf(360f) }
    var skateTrackHeight by remember { mutableFloatStateOf(300f) }
    var popupScoreText by remember { mutableStateOf<String?>(null) }
    var popupTimer by remember { mutableFloatStateOf(0f) }

    fun triggerJump() {
        if (jumpOffsetY <= 2f && !isGameOver && !hasWonGame) {
            jumpVelocity = -540f // impulso de salto vertical
            soundManager?.playButtonClick()
            hapticUtil?.vibrateClick()
        }
    }

    val skateSpeed = if (difficulty == ToyGameDifficulty.FACIL) 215f else 235f

    rememberGameLoop(isRunning = !isPaused && !isGameOver && !hasWonGame) { delta ->
        if (invulnerableTimer > 0f) {
            invulnerableTimer = (invulnerableTimer - delta).coerceAtLeast(0f)
        }
        if (popupTimer > 0f) {
            popupTimer = (popupTimer - delta).coerceAtLeast(0f)
            if (popupTimer <= 0f) popupScoreText = null
        }

        // Avanzar en línea recta continua
        worldX += skateSpeed * delta

        // Física de salto y gravedad
        if (jumpOffsetY > 0f || jumpVelocity != 0f) {
            jumpOffsetY = (jumpOffsetY - jumpVelocity * delta).coerceAtLeast(0f)
            jumpVelocity += 1250f * delta // gravedad
            if (jumpOffsetY <= 0f) {
                jumpOffsetY = 0f
                jumpVelocity = 0f
                jumpOllieAngle = 0f
            } else {
                jumpOllieAngle = -18f // ángulo ollie en el aire
            }
        }

        // Generar nuevos obstáculos y números por delante
        while (nextSpawnX < worldX + skateTrackWidth + 400f) {
            val pattern = (itemIdCounter % 3).toInt()
            val newItem = when (pattern) {
                0 -> {
                    // Obstáculo de suelo (🚧 o 🪨) -> se debe saltar
                    SkateRoadItem(
                        id = itemIdCounter++,
                        worldX = nextSpawnX,
                        type = SkateItemType.OBSTACLE,
                        icon = if (Random.nextBoolean()) "🚧" else "🪨",
                        isAirborne = false
                    )
                }
                1 -> {
                    // Número correcto -> se debe agarrar
                    val isAir = Random.nextBoolean()
                    SkateRoadItem(
                        id = itemIdCounter++,
                        worldX = nextSpawnX,
                        type = SkateItemType.CORRECT_NUMBER,
                        value = targetNumber,
                        icon = "⭐",
                        isAirborne = isAir
                    )
                }
                else -> {
                    // Número incorrecto distractor -> se debe esquivar
                    val wrongVal = distractors.random()
                    val isAir = Random.nextBoolean()
                    SkateRoadItem(
                        id = itemIdCounter++,
                        worldX = nextSpawnX,
                        type = SkateItemType.WRONG_NUMBER,
                        value = wrongVal,
                        icon = "❌",
                        isAirborne = isAir
                    )
                }
            }
            items.add(newItem)
            nextSpawnX += (if (difficulty == ToyGameDifficulty.FACIL) Random.nextInt(320, 440) else Random.nextInt(360, 480)).toFloat()
        }

        val skaterScreenX = skateTrackWidth * 0.22f
        val baseGroundY = skateTrackHeight * 0.70f
        val skaterGroundY = getSkateTerrainY(worldX + skaterScreenX, baseGroundY)
        val skaterCenterY = skaterGroundY - jumpOffsetY - 24f

        // Limpiar ítems que quedaron atrás
        items.removeAll { it.worldX < worldX - 250f }

        // Comprobar colisiones
        items.forEach { item ->
            if (!item.isResolved) {
                val itemScreenX = item.worldX - worldX + skaterScreenX
                val itemGroundY = getSkateTerrainY(item.worldX, baseGroundY)
                val itemCenterY = if (item.isAirborne) itemGroundY - 60f else itemGroundY - 20f

                val dx = skaterScreenX - itemScreenX
                val dy = skaterCenterY - itemCenterY
                val dist = sqrt(dx * dx + dy * dy)

                when (item.type) {
                    SkateItemType.OBSTACLE -> {
                        if (kotlin.math.abs(dx) < 32f) {
                            if (jumpOffsetY >= 36f) {
                                // ¡Saltó con éxito por encima del obstáculo!
                                item.isResolved = true
                                score += 15
                                soundManager?.playReward()
                            } else if (invulnerableTimer <= 0f) {
                                // ¡Choque contra el obstáculo!
                                item.isResolved = true
                                lives = (lives - 1).coerceAtLeast(0)
                                invulnerableTimer = 1.3f
                                soundManager?.playIncorrect()
                                hapticUtil?.vibrateEncourage()
                                ttsManager?.speak(if (isEnglish) "Jump over the obstacles!" else "¡Salta los obstáculos!")
                                if (lives == 0) isGameOver = true
                            }
                        }
                    }
                    SkateItemType.CORRECT_NUMBER -> {
                        if (dist < 46f) {
                            // ¡Agarró el número correcto!
                            item.isResolved = true
                            score += 30
                            coinsEarned += 1
                            correctHits++
                            CoinShopManager.addCoin(context, 1)
                            popupScoreText = "+30 ⭐ $mathQuestion = $targetNumber"
                            popupTimer = 1.0f
                            soundManager?.playCorrect()
                            hapticUtil?.vibrateClick()
                            ttsManager?.speak(if (isEnglish) "Great! $mathQuestion is $targetNumber!" else "¡Genial! ¡$mathQuestion es $targetNumber!")
                            if (correctHits >= 12) {
                                hasWonGame = true
                            } else {
                                nextQuestion()
                            }
                        }
                    }
                    SkateItemType.WRONG_NUMBER -> {
                        if (dist < 42f) {
                            // ¡Tocó número incorrecto!
                            item.isResolved = true
                            if (invulnerableTimer <= 0f) {
                                lives = (lives - 1).coerceAtLeast(0)
                                invulnerableTimer = 1.3f
                                soundManager?.playIncorrect()
                                hapticUtil?.vibrateEncourage()
                                ttsManager?.speak(if (isEnglish) "That's not the right number! Dodge it!" else "¡Ese no es el número! ¡Esqúivalo!")
                                if (lives == 0) isGameOver = true
                            }
                        }
                    }
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFFE8F5E9))) {
        Column(
            modifier = Modifier.fillMaxSize().padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ToyGameTopBar(
                title = if (isEnglish) "Skate Hill Runner" else "Patineta de Carreras",
                toyEmoji = toy.emoji,
                lives = lives,
                coins = coinsEarned,
                correctHits = correctHits,
                targetHits = 12,
                difficulty = difficulty,
                isEnglish = isEnglish,
                onExitClick = onExitGame
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Banner de Misión Matemática y Guía
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(SurfaceWhite)
                    .border(2.dp, Color(0xFF4CAF50), RoundedCornerShape(14.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "🎯 $mathQuestion = ?",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FredokaFont,
                        color = Color(0xFF2E7D32)
                    )
                    Text(
                        text = if (isEnglish) "⭐ Catch the answer | 🚧 Jump | ❌ Dodge" else "⭐ Atrapa la respuesta | 🚧 Salta | ❌ Esquiva",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = SlateMuted
                    )
                }
                Text(
                    text = "🏁 $correctHits/12",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FredokaFont,
                    color = Color(0xFF1B5E20)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // CIRCUITO DE PATINETA CON SUBIDAS Y BAJADAS (CANVAS + OVERLAY)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .shadow(6.dp, RoundedCornerShape(24.dp))
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color(0xFFE0F7FA))
                    .border(3.dp, Color(0xFF4CAF50), RoundedCornerShape(24.dp))
                    .onSizeChanged {
                        skateTrackWidth = it.width.toFloat().coerceAtLeast(200f)
                        skateTrackHeight = it.height.toFloat().coerceAtLeast(200f)
                    }
                    .pointerInput(Unit) {
                        detectTapGestures { triggerJump() }
                    }
            ) {
                val skaterScreenX = skateTrackWidth * 0.22f
                val baseGroundY = skateTrackHeight * 0.70f
                val skaterGroundY = getSkateTerrainY(worldX + skaterScreenX, baseGroundY)

                // Fondo de cielo y colinas onduladas
                Canvas(modifier = Modifier.fillMaxSize()) {
                    // Cielo con nubes suaves
                    drawRect(
                        brush = Brush.verticalGradient(
                            listOf(Color(0xFF80DEEA), Color(0xFFE0F7FA), Color(0xFFFFF9C4))
                        )
                    )

                    // Terreno con subidas y bajadas
                    val groundPath = androidx.compose.ui.graphics.Path().apply {
                        moveTo(0f, size.height)
                        lineTo(0f, getSkateTerrainY(worldX - skaterScreenX, baseGroundY))
                        var sx = 0f
                        while (sx <= size.width) {
                            lineTo(sx, getSkateTerrainY(worldX - skaterScreenX + sx, baseGroundY))
                            sx += 12f
                        }
                        lineTo(size.width, size.height)
                        close()
                    }

                    drawPath(
                        path = groundPath,
                        brush = Brush.verticalGradient(
                            listOf(Color(0xFF81C784), Color(0xFF4CAF50), Color(0xFF2E7D32))
                        )
                    )

                    // Borde de la pista / rampa
                    drawPath(
                        path = groundPath,
                        color = Color(0xFFFFB300),
                        style = Stroke(width = 6f)
                    )
                }

                // Ítems en el circuito (Obstáculos y Números)
                items.forEach { item ->
                    val itemScreenX = item.worldX - worldX + skaterScreenX
                    if (itemScreenX in -60f..(skateTrackWidth + 60f) && !item.isResolved) {
                        val itemGroundY = getSkateTerrainY(item.worldX, baseGroundY)
                        val itemCenterY = if (item.isAirborne) itemGroundY - 60f else itemGroundY - 20f

                        Box(
                            modifier = Modifier
                                .offset { IntOffset(itemScreenX.roundToInt() - 25, itemCenterY.roundToInt() - 25) }
                                .size(50.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            when (item.type) {
                                SkateItemType.OBSTACLE -> {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(text = item.icon, fontSize = 28.sp)
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(Color(0xFFD32F2F))
                                                .padding(horizontal = 4.dp, vertical = 1.dp)
                                        ) {
                                            Text(
                                                text = if (isEnglish) "JUMP!" else "¡SALTA!",
                                                fontSize = 8.sp,
                                                fontWeight = FontWeight.Black,
                                                color = Color.White
                                            )
                                        }
                                    }
                                }
                                SkateItemType.CORRECT_NUMBER -> {
                                    Box(
                                        modifier = Modifier
                                            .shadow(6.dp, CircleShape)
                                            .clip(CircleShape)
                                            .background(Color(0xFFFFD54F))
                                            .border(2.5.dp, Color(0xFFEF6C00), CircleShape)
                                            .padding(6.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "${item.value}",
                                            fontSize = 17.sp,
                                            fontWeight = FontWeight.Black,
                                            fontFamily = FredokaFont,
                                            color = Color(0xFFE65100)
                                        )
                                    }
                                }
                                SkateItemType.WRONG_NUMBER -> {
                                    Box(
                                        modifier = Modifier
                                            .shadow(4.dp, CircleShape)
                                            .clip(CircleShape)
                                            .background(Color(0xFFFFCCBC))
                                            .border(2.dp, Color(0xFFD84315), CircleShape)
                                            .padding(6.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "${item.value}",
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Black,
                                            fontFamily = FredokaFont,
                                            color = Color(0xFFBF360C)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // 1. Sombra dinámica sobre el terreno cuando está en el aire
                if (jumpOffsetY > 2f) {
                    val shadowAlpha = (0.35f * (1f - (jumpOffsetY / 240f))).coerceIn(0.08f, 0.35f)
                    val shadowScale = (1f - (jumpOffsetY / 350f)).coerceIn(0.45f, 1f)
                    Box(
                        modifier = Modifier
                            .offset {
                                IntOffset(
                                    (skaterScreenX - 24f * shadowScale).roundToInt(),
                                    (skaterGroundY - 3f).roundToInt()
                                )
                            }
                            .size(width = (48.dp * shadowScale), height = 8.dp)
                            .graphicsLayer { alpha = shadowAlpha }
                            .background(Color(0xFF1B5E20), CircleShape)
                    )
                }

                // 2. Efecto de polvo / velocidad sobre el suelo al patinar
                if (jumpOffsetY <= 2f && !isGameOver && !hasWonGame) {
                    Text(
                        text = "💨",
                        fontSize = 13.sp,
                        modifier = Modifier
                            .offset {
                                IntOffset(
                                    (skaterScreenX - 38f).roundToInt(),
                                    (skaterGroundY - 14f).roundToInt()
                                )
                            }
                            .graphicsLayer { alpha = 0.65f }
                    )
                }

                // 3. Patineta y Skater con Cuerpo Completo (Cabeza, buzo deportivo, brazos de balance, pantalones, zapatillas y tabla turbo)
                val skaterAngle = if (jumpOffsetY > 5f) jumpOllieAngle else getSkateSlopeAngle(worldX + skaterScreenX, baseGroundY)
                val isBlinking = (invulnerableTimer > 0f && ((invulnerableTimer * 10).toInt() % 2 == 0))

                Box(
                    modifier = Modifier
                        .offset {
                            IntOffset(
                                (skaterScreenX - 38f).roundToInt(),
                                (skaterGroundY - jumpOffsetY - 78f).roundToInt()
                            )
                        }
                        .rotate(skaterAngle)
                        .graphicsLayer { alpha = if (isBlinking) 0.35f else 1f },
                    contentAlignment = Alignment.Center
                ) {
                    SkaterMuñecoView(
                        isJumping = jumpOffsetY > 5f
                    )
                }

                // 4. Indicador animado "+30 ⭐ +1 🪙" al agarrar número correcto
                if (popupTimer > 0f && popupScoreText != null) {
                    val floatRise = (1f - popupTimer) * 35f
                    Box(
                        modifier = Modifier
                            .offset {
                                IntOffset(
                                    skaterScreenX.roundToInt() - 25,
                                    (skaterGroundY - jumpOffsetY - 100f - floatRise).roundToInt()
                                )
                            }
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFFFD54F))
                            .border(1.5.dp, Color(0xFFEF6C00), RoundedCornerShape(12.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                            .graphicsLayer { alpha = (popupTimer * 1.5f).coerceIn(0f, 1f) }
                    ) {
                        Text(
                            text = popupScoreText ?: "",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FredokaFont,
                            color = Color(0xFFBF360C)
                        )
                    }
                }

                // 5. Indicador flotante de truco Ollie en el aire
                if (jumpOffsetY > 15f) {
                    Box(
                        modifier = Modifier
                            .offset {
                                IntOffset(
                                    skaterScreenX.roundToInt() - 15,
                                    (skaterGroundY - jumpOffsetY - 98f).roundToInt()
                                )
                            }
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFFFF5722))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "🛹 ¡OLLIE! 💨",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FredokaFont,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // BOTÓN GIGANTE DE SALTO
            Button(
                onClick = { triggerJump() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp)
                    .shadow(6.dp, RoundedCornerShape(18.dp)),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
            ) {
                Text(
                    text = if (isEnglish) "🛹 JUMP! (Tap here or screen) 💨" else "🛹 ¡SALTAR! (Toca aquí o la pantalla) 💨",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FredokaFont,
                    color = Color.White
                )
            }
        }

        if (isGameOver) {
            ToyGameOverDialog(
                toyEmoji = toy.emoji,
                toyName = toy.name,
                score = score,
                coinsEarned = coinsEarned,
                isEnglish = isEnglish,
                onRetry = {
                    lives = 3
                    hasWonGame = false
                    isGameOver = false
                    jumpOffsetY = 0f
                },
                onExit = onExitGame
            )
        }

        if (hasWonGame) {
            ToyVictoryDialog(
                toyEmoji = toy.emoji,
                toyName = toy.name,
                score = score,
                coinsEarned = coinsEarned,
                isEnglish = isEnglish,
                soundManager = soundManager,
                ttsManager = ttsManager,
                onPlayAgain = {
                    hasWonGame = false
                    correctHits = 0
                    lives = 3
                    score = 0
                    coinsEarned = 0
                    isGameOver = false
                    worldX = 0f
                    jumpOffsetY = 0f
                    items.clear()
                    nextSpawnX = 450f
                    nextQuestion()
                },
                onExit = onExitGame
            )
        }
    }
}

// =========================================================================
// 14. GUITARRA: Melodía de Rock Progresiva 🎸 (Todos los sonidos forman una canción)
// =========================================================================
private data class GuitarMathProblem(
    val num1: Int,
    val num2: Int,
    val isAddition: Boolean,
    val result: Int
)

private fun generateGuitarMathProblem(): GuitarMathProblem {
    val isAdd = Random.nextBoolean()
    return if (isAdd) {
        val a = Random.nextInt(1, 9)
        val b = Random.nextInt(1, 9)
        GuitarMathProblem(a, b, true, a + b)
    } else {
        val b = Random.nextInt(1, 8)
        val res = Random.nextInt(1, 9)
        val a = res + b
        GuitarMathProblem(a, b, false, res)
    }
}

private data class SongMelodyNote(
    val noteName: String,
    val noteIndex: Int,
    val color: Color
)

private val ROCK_SONG_MELODY = listOf(
    SongMelodyNote("DO", 0, Color(0xFFE53935)),
    SongMelodyNote("MI", 2, Color(0xFF43A047)),
    SongMelodyNote("SOL", 4, Color(0xFFFDD835)),
    SongMelodyNote("DO+", 7, Color(0xFF1E88E5)),
    SongMelodyNote("SOL", 4, Color(0xFFFDD835)),
    SongMelodyNote("LA", 5, Color(0xFFAB47BC)),
    SongMelodyNote("MI", 2, Color(0xFF43A047)),
    SongMelodyNote("DO", 0, Color(0xFFE53935))
)

private data class GuitarFretString(
    val noteName: String,
    val noteIndex: Int,
    val stringColor: Color,
    val value: Int,
    val isTargetNote: Boolean
)

@Composable
fun GuitarraHeroGame(
    toy: ToyItem,
    isEnglish: Boolean,
    soundManager: SoundManager?,
    ttsManager: TtsManager?,
    hapticUtil: HapticUtil?,
    difficulty: ToyGameDifficulty = ToyGameDifficulty.FACIL,
    onExitGame: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var lives by remember { mutableIntStateOf(3) }
    var score by remember { mutableIntStateOf(0) }
    var coinsEarned by remember { mutableIntStateOf(0) }
    var correctHits by remember { mutableIntStateOf(0) }
    var hasWonGame by remember { mutableStateOf(false) }
    var isGameOver by remember { mutableStateOf(false) }

    var currentProblem by remember(difficulty) { mutableStateOf(generateToyMathProblem(difficulty)) }
    var currentMelodyStep by remember { mutableIntStateOf(0) }
    var activeStrummedIndex by remember { mutableIntStateOf(-1) }
    var isPlayingMelody by remember { mutableStateOf(false) }

    val currentTargetNote = ROCK_SONG_MELODY[currentMelodyStep % ROCK_SONG_MELODY.size]

    val guitarStrings = remember(currentProblem, currentMelodyStep) {
        val targetResult = currentProblem.result
        val distractors = listOf(
            (targetResult + 1),
            (targetResult - 1).coerceAtLeast(1),
            (targetResult + 2),
            (targetResult - 2).coerceAtLeast(1)
        ).distinct().filter { it != targetResult }.shuffled()

        // 4 cuerdas afinadas armoniosamente en la escala mayor/pentatónica
        val availableNotes = listOf(
            SongMelodyNote("DO", 0, Color(0xFFE53935)),
            SongMelodyNote("MI", 2, Color(0xFF43A047)),
            SongMelodyNote("SOL", 4, Color(0xFFFDD835)),
            SongMelodyNote("DO+", 7, Color(0xFF1E88E5)),
            SongMelodyNote("LA", 5, Color(0xFFAB47BC))
        ).filter { it.noteIndex != currentTargetNote.noteIndex }.shuffled()

        val otherNotes = availableNotes.take(3)
        val strings = mutableListOf(
            GuitarFretString(
                noteName = "${currentTargetNote.noteName} ★",
                noteIndex = currentTargetNote.noteIndex,
                stringColor = currentTargetNote.color,
                value = targetResult,
                isTargetNote = true
            ),
            GuitarFretString(
                noteName = otherNotes[0].noteName,
                noteIndex = otherNotes[0].noteIndex,
                stringColor = otherNotes[0].color,
                value = distractors[0],
                isTargetNote = false
            ),
            GuitarFretString(
                noteName = otherNotes[1].noteName,
                noteIndex = otherNotes[1].noteIndex,
                stringColor = otherNotes[1].color,
                value = distractors[1],
                isTargetNote = false
            ),
            GuitarFretString(
                noteName = otherNotes[2].noteName,
                noteIndex = otherNotes[2].noteIndex,
                stringColor = otherNotes[2].color,
                value = distractors.getOrElse(2) { targetResult + 3 },
                isTargetNote = false
            )
        ).shuffled()

        strings
    }

    fun onStrum(stringIndex: Int, strItem: GuitarFretString) {
        if (isPlayingMelody) return
        activeStrummedIndex = stringIndex

        // Siempre toca la nota musical real de la cuerda de forma consonante
        soundManager?.playMusicalNote(strItem.noteIndex)
        hapticUtil?.vibrateClick()

        if (strItem.isTargetNote) {
            // ¡Acierto! Toca la melodía acumulada de la canción hasta esta nota
            isPlayingMelody = true
            score += 20
            coinsEarned += 1
            correctHits++
            CoinShopManager.addCoin(context, 1)

            if (correctHits >= 30) {
                hasWonGame = true
            }

            coroutineScope.launch {
                kotlinx.coroutines.delay(180)

                // Reproducir las notas de la melodía acumuladas hasta este paso
                val notesToPlay = ROCK_SONG_MELODY.take(currentMelodyStep + 1)
                for (note in notesToPlay) {
                    soundManager?.playMusicalNote(note.noteIndex)
                    kotlinx.coroutines.delay(160)
                }

                soundManager?.playCorrect()

                val nextStep = currentMelodyStep + 1
                if (nextStep >= ROCK_SONG_MELODY.size) {
                    // ¡Canción completa! Al terminar se escucha completa toda la melodía tocada
                    kotlinx.coroutines.delay(400)
                    soundManager?.playApplause()
                    ttsManager?.speak(if (isEnglish) "Listen to the complete rock song!" else "¡Escucha tu canción de rock completa!")
                    kotlinx.coroutines.delay(1200)

                    for (mNote in ROCK_SONG_MELODY) {
                        soundManager?.playMusicalNote(mNote.noteIndex)
                        activeStrummedIndex = guitarStrings.indexOfFirst { it.noteIndex == mNote.noteIndex }
                        hapticUtil?.vibrateClick()
                        kotlinx.coroutines.delay(300)
                        activeStrummedIndex = -1
                        kotlinx.coroutines.delay(70)
                    }
                    soundManager?.playApplause()
                    soundManager?.playVictory()
                    ttsManager?.speak(if (isEnglish) "Bravo! Rock Star!" else "¡Bravo! ¡Estrella de Rock!")
                    currentMelodyStep = 0
                } else {
                    currentMelodyStep = nextStep
                    val opText = currentProblem.opSymbol
                    ttsManager?.speak("¡Nota ${currentTargetNote.noteName}! ¡${currentProblem.num1} $opText ${currentProblem.num2} es ${currentProblem.result}!")
                }

                isPlayingMelody = false
                activeStrummedIndex = -1
                currentProblem = generateToyMathProblem(difficulty)
            }
        } else {
            lives = (lives - 1).coerceAtLeast(0)
            soundManager?.playIncorrect()
            hapticUtil?.vibrateEncourage()
            if (lives == 0) isGameOver = true
            coroutineScope.launch {
                kotlinx.coroutines.delay(200)
                activeStrummedIndex = -1
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF1A1A2E))) {
        Column(
            modifier = Modifier.fillMaxSize().padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ToyGameTopBar(
                title = if (isEnglish) "Rock Guitar Melody" else "Guitarra Musical 2D",
                toyEmoji = toy.emoji,
                lives = lives,
                coins = coinsEarned,
                correctHits = correctHits,
                targetHits = 30,
                difficulty = difficulty,
                isEnglish = isEnglish,
                onExitClick = onExitGame
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Tarjeta de la Operación Matemática
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 2.dp),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF16213E)),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFFFF5722))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp, horizontal = 14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(currentProblem.opColor)
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = if (isEnglish) currentProblem.opNameEn else currentProblem.opNameEs,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FredokaFont,
                                color = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "${currentProblem.num1} ${currentProblem.opSymbol} ${currentProblem.num2} = ?",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FredokaFont,
                            color = Color(0xFFFF8A65)
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (isEnglish) "🎸 Play string #${currentProblem.result} to hear note ${currentTargetNote.noteName}!" else "🎸 ¡Toca el #${currentProblem.result} para avanzar la melodía!",
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FredokaFont,
                        color = Color(0xFFFFCC80)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Pentagrama y notas de la melodía en construcción
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F3460)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFAB40).copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🎼 Melodía:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FredokaFont,
                        color = Color(0xFFFFD54F)
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ROCK_SONG_MELODY.forEachIndexed { i, note ->
                            val isCompleted = (i < currentMelodyStep)
                            val isCurrent = (i == currentMelodyStep)
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        when {
                                            isCompleted -> Color(0xFF43A047)
                                            isCurrent -> Color(0xFFFF9800)
                                            else -> Color(0xFF37474F)
                                        }
                                    )
                                    .border(
                                        width = if (isCurrent) 2.dp else 1.dp,
                                        color = if (isCurrent) Color.White else Color.Transparent,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = if (isCompleted) "✓ ${note.noteName}" else note.noteName,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (isCompleted || isCurrent) Color.White else Color(0xFFB0BEC5)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Mástil interactivo con 4 Cuerdas de Guitarra
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .shadow(8.dp, RoundedCornerShape(24.dp))
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color(0xFF263238))
                    .border(3.dp, Color(0xFFFF5722), RoundedCornerShape(24.dp))
                    .padding(12.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceEvenly
                ) {
                    guitarStrings.forEachIndexed { idx, strItem ->
                        val isStrummed = (activeStrummedIndex == idx)
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .clickable { onStrum(idx, strItem) },
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isStrummed) strItem.stringColor.copy(alpha = 0.85f) else Color(0xFF37474F)
                            ),
                            border = androidx.compose.foundation.BorderStroke(
                                width = if (isStrummed) 3.dp else 1.5.dp,
                                color = if (isStrummed) Color.White else strItem.stringColor
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = if (isStrummed) 8.dp else 2.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                // Nombre de cuerda y nota musical
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = "🎸", fontSize = 20.sp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = strItem.noteName,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FredokaFont,
                                        color = if (isStrummed) Color.White else strItem.stringColor
                                    )
                                }

                                // Cuerda metálica tensa en el centro
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(if (isStrummed) 4.dp else 2.dp)
                                        .padding(horizontal = 12.dp)
                                        .background(if (isStrummed) Color.White else strItem.stringColor)
                                )

                                // Insignia con el número
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(strItem.stringColor)
                                        .border(2.dp, Color.White, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "${strItem.value}",
                                        fontSize = 19.sp,
                                        fontWeight = FontWeight.Black,
                                        fontFamily = FredokaFont,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Estado y animación de concierto
            Text(
                text = if (isPlayingMelody) "🎶 ¡TOCANDO LA MELODÍA DE ROCK! ⚡" else "🎵 Toca las cuerdas para tocar cada nota de la canción 🎸",
                fontSize = 12.5.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FredokaFont,
                color = if (isPlayingMelody) Color(0xFFFFD54F) else Color(0xFFB0BEC5)
            )
        }

        if (isGameOver) {
            ToyGameOverDialog(
                toyEmoji = toy.emoji,
                toyName = toy.name,
                score = score,
                coinsEarned = coinsEarned,
                isEnglish = isEnglish,
                onRetry = {
                    lives = 3
                    hasWonGame = false
                    isGameOver = false
                },
                onExit = onExitGame
            )
        }

        if (hasWonGame) {
            ToyVictoryDialog(
                toyEmoji = toy.emoji,
                toyName = toy.name,
                score = score,
                coinsEarned = coinsEarned,
                isEnglish = isEnglish,
                soundManager = soundManager,
                ttsManager = ttsManager,
                onPlayAgain = {
                    hasWonGame = false
                    correctHits = 0
                    lives = 3
                    score = 0
                    coinsEarned = 0
                    currentMelodyStep = 0
                    currentProblem = generateToyMathProblem(difficulty)
                },
                onExit = onExitGame
            )
        }
    }
}

// =========================================================================
// 15. HELICÓPTERO: Simulador de Vuelo 2D 🚁 (Suma, Resta, Multiplicación, División)
// =========================================================================
private data class CopterMathMission(
    val num1: Int,
    val num2: Int,
    val opSymbol: String,
    val opNameEs: String,
    val opNameEn: String,
    val opColor: Color,
    val result: Int
)

private fun generateCopterMission(difficulty: ToyGameDifficulty = ToyGameDifficulty.FACIL): CopterMathMission {
    val ops = if (difficulty == ToyGameDifficulty.FACIL) listOf(0, 1) else listOf(2, 3)
    return when (ops.random()) {
        0 -> { // Suma
            val a = Random.nextInt(2, 10)
            val b = Random.nextInt(2, 10)
            CopterMathMission(a, b, "+", "➕ Suma", "➕ Addition", Color(0xFF1E88E5), a + b)
        }
        1 -> { // Resta
            val b = Random.nextInt(1, 9)
            val res = Random.nextInt(1, 10)
            val a = res + b
            CopterMathMission(a, b, "−", "➖ Resta", "➖ Subtraction", Color(0xFF4CAF50), res)
        }
        2 -> { // Multiplicación
            val a = Random.nextInt(2, 6)
            val b = Random.nextInt(1, 6)
            CopterMathMission(a, b, "×", "✖️ Multiplicar", "✖️ Multiply", Color(0xFF9C27B0), a * b)
        }
        else -> { // División
            val b = Random.nextInt(2, 6)
            val res = Random.nextInt(1, 6)
            val a = res * b
            CopterMathMission(a, b, "÷", "➗ Dividir", "➗ Division", Color(0xFFFF9800), res)
        }
    }
}

private class FlightSkyBalloon(
    var xFraction: Float,
    val yFraction: Float,
    val isCorrectTarget: Boolean,
    var value: Int,
    val color: Color,
    var isPopped: Boolean = false,
    var popAnim: Float = 0f
)

@Composable
fun DronDeliveryGame(
    toy: ToyItem,
    isEnglish: Boolean,
    soundManager: SoundManager?,
    ttsManager: TtsManager?,
    hapticUtil: HapticUtil?,
    difficulty: ToyGameDifficulty = ToyGameDifficulty.FACIL,
    onExitGame: () -> Unit
) {
    val context = LocalContext.current
    var lives by remember { mutableIntStateOf(3) }
    var score by remember { mutableIntStateOf(0) }
    var coinsEarned by remember { mutableIntStateOf(0) }
    var correctHits by remember { mutableIntStateOf(0) }
    var hasWonGame by remember { mutableStateOf(false) }
    var isGameOver by remember { mutableStateOf(false) }

    var currentMission by remember(difficulty) { mutableStateOf(generateCopterMission(difficulty)) }

    var copterYFraction by remember { mutableFloatStateOf(0.50f) }
    var copterVy by remember { mutableFloatStateOf(0f) }
    var gameTime by remember { mutableFloatStateOf(0f) }
    var frameTick by remember { mutableLongStateOf(0L) }
    var damageFlash by remember { mutableStateOf(false) }

    // Estados de botones mantenidos presionados
    var isHoldingUp by remember { mutableStateOf(false) }
    var isHoldingDown by remember { mutableStateOf(false) }

    val balloons = remember { mutableStateListOf<FlightSkyBalloon>() }

    fun spawnBalloonsForMission(mission: CopterMathMission) {
        balloons.clear()
        // Aparecen de 3, 4 o 5 globos
        val balloonCount = Random.nextInt(3, 6)

        // Alturas bien distribuidas para que no se superpongan
        val ySlots = when (balloonCount) {
            3 -> listOf(0.24f, 0.50f, 0.76f)
            4 -> listOf(0.20f, 0.39f, 0.60f, 0.80f)
            else -> listOf(0.18f, 0.33f, 0.49f, 0.65f, 0.81f)
        }.shuffled()

        val balloonPalette = listOf(
            Color(0xFFE53935), // Rojo
            Color(0xFFFB8C00), // Naranja
            Color(0xFF43A047), // Verde
            Color(0xFF1E88E5), // Azul
            Color(0xFF8E24AA), // Morado
            Color(0xFF00ACC1), // Cyan
            Color(0xFFE91E63)  // Rosa
        ).shuffled()

        // Distractores únicos diferentes del resultado
        val distractors = mutableListOf<Int>()
        val candidatePool = mutableListOf<Int>()
        for (d in 1..9) {
            if (mission.result - d >= 0) candidatePool.add(mission.result - d)
            candidatePool.add(mission.result + d)
        }
        candidatePool.shuffle()
        for (cand in candidatePool) {
            if (cand != mission.result && !distractors.contains(cand)) {
                distractors.add(cand)
                if (distractors.size >= balloonCount - 1) break
            }
        }
        var fallback = 1
        while (distractors.size < balloonCount - 1) {
            if (fallback != mission.result && !distractors.contains(fallback)) {
                distractors.add(fallback)
            }
            fallback++
        }

        val correctIndex = Random.nextInt(balloonCount)
        var distractorIndex = 0

        for (i in 0 until balloonCount) {
            val isCorrect = (i == correctIndex)
            val value = if (isCorrect) mission.result else distractors[distractorIndex++]
            balloons.add(
                FlightSkyBalloon(
                    xFraction = 1.30f,
                    yFraction = ySlots[i],
                    isCorrectTarget = isCorrect,
                    value = value,
                    color = balloonPalette[i % balloonPalette.size]
                )
            )
        }
    }

    LaunchedEffect(currentMission) {
        spawnBalloonsForMission(currentMission)
    }

    LaunchedEffect(damageFlash) {
        if (damageFlash) {
            kotlinx.coroutines.delay(220)
            damageFlash = false
        }
    }

    // Aumento progresivo de velocidad a medida que avanza hacia los 30 aciertos
    val progressRatio = (correctHits.toFloat() / 30f).coerceIn(0f, 1f)
    val speedMultiplier = 1.0f + progressRatio * 1.15f // Acelera desde 1.0x hasta 2.15x
    val flightSpeed = 0.27f * speedMultiplier

    rememberGameLoop(isRunning = !isGameOver && !hasWonGame) { delta ->
        gameTime += delta

        // Movimiento ultra fluido con empuje continuo al dejar presionado
        val thrustAcc = 1.85f
        if (isHoldingUp) {
            copterVy = (copterVy - thrustAcc * delta).coerceAtLeast(-0.62f)
        } else if (isHoldingDown) {
            copterVy = (copterVy + thrustAcc * delta).coerceAtMost(0.62f)
        } else {
            // Amortiguación suave y natural del aire
            copterVy *= (1f - (3.8f * delta).coerceAtMost(1f))
        }

        copterYFraction += copterVy * delta
        if (copterYFraction <= 0.15f) {
            copterYFraction = 0.15f
            if (copterVy < 0f) copterVy = 0f
        } else if (copterYFraction >= 0.85f) {
            copterYFraction = 0.85f
            if (copterVy > 0f) copterVy = 0f
        }

        val copterX = 0.22f

        // Avance de los globos
        var waveAllPassed = true
        for (b in balloons) {
            b.xFraction -= flightSpeed * delta

            if (b.isPopped) {
                b.popAnim = (b.popAnim + delta * 4f).coerceAtMost(1f)
            }

            if (b.xFraction > -0.22f) {
                waveAllPassed = false
            }

            // Detección de colisión cuando el helicóptero choca el globo
            if (!b.isPopped && kotlin.math.abs(b.xFraction - copterX) < 0.08f) {
                if (kotlin.math.abs(copterYFraction - b.yFraction) < 0.10f) {
                    b.isPopped = true
                    if (b.isCorrectTarget) {
                        score += 25
                        coinsEarned += 1
                        correctHits++
                        CoinShopManager.addCoin(context, 1)
                        if (correctHits >= 30) {
                            hasWonGame = true
                        }
                        soundManager?.playVictory()
                        hapticUtil?.vibrateClick()
                        val praise = if (isEnglish) {
                            "Great shot! ${currentMission.num1} ${currentMission.opSymbol} ${currentMission.num2} is ${currentMission.result}!"
                        } else {
                            "¡Globo correcto! ¡${currentMission.num1} ${currentMission.opSymbol} ${currentMission.num2} es ${currentMission.result}!"
                        }
                        ttsManager?.speak(praise)
                        currentMission = generateCopterMission(difficulty)
                        break
                    } else {
                        lives = (lives - 1).coerceAtLeast(0)
                        damageFlash = true
                        soundManager?.playIncorrect()
                        hapticUtil?.vibrateEncourage()
                        if (lives == 0) isGameOver = true
                    }
                }
            }
        }

        // Si todos los globos de la ronda pasaron sin chocar, reubicar la oleada para otra oportunidad
        if (waveAllPassed && balloons.isNotEmpty()) {
            for (b in balloons) {
                b.xFraction = 1.30f
                b.isPopped = false
                b.popAnim = 0f
            }
        }

        frameTick++
    }

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFFE0F7FA))) {
        Column(
            modifier = Modifier.fillMaxSize().padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ToyGameTopBar(
                title = if (isEnglish) "Helicopter Flight" else "Vuelo en Helicóptero",
                toyEmoji = toy.emoji,
                lives = lives,
                coins = coinsEarned,
                correctHits = correctHits,
                targetHits = 30,
                difficulty = difficulty,
                isEnglish = isEnglish,
                onExitClick = onExitGame
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Tarjeta de la Operación Matemática con medidor de velocidad
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 2.dp),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFF00ACC1))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp, horizontal = 14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(currentMission.opColor)
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = if (isEnglish) currentMission.opNameEn else currentMission.opNameEs,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FredokaFont,
                                color = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "${currentMission.num1} ${currentMission.opSymbol} ${currentMission.num2} = ?",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FredokaFont,
                            color = Color(0xFF006064)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFFFFF3E0))
                                .border(1.dp, Color(0xFFFFB74D), RoundedCornerShape(10.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "⚡ x${String.format(java.util.Locale.US, "%.1f", speedMultiplier)}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FredokaFont,
                                color = Color(0xFFE65100)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = if (isEnglish) "🎈 Pop balloon #${currentMission.result}!" else "🎈 ¡Explota el globo con el número ${currentMission.result}!",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FredokaFont,
                        color = Color(0xFF00838F)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Lienzo 2D del Simulador de Vuelo
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .shadow(6.dp, RoundedCornerShape(24.dp))
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        if (damageFlash) Brush.linearGradient(listOf(Color(0xFFFFCDD2), Color(0xFFFFCDD2)))
                        else Brush.verticalGradient(
                            listOf(
                                Color(0xFF81D4FA), // Cielo azul claro
                                Color(0xFFB3E5FC),
                                Color(0xFFE1F5FE)
                            )
                        )
                    )
                    .border(3.dp, Color(0xFF00ACC1), RoundedCornerShape(24.dp))
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    @Suppress("UNUSED_VARIABLE")
                    val tick = frameTick
                    val w = size.width
                    val h = size.height

                    // 1. Nubes flotantes de fondo que avanzan según la velocidad
                    val cloudShift = (gameTime * 24f * speedMultiplier) % w
                    drawCircle(Color.White.copy(alpha = 0.5f), radius = 28f, center = Offset((w * 0.35f - cloudShift + w) % w, h * 0.20f))
                    drawCircle(Color.White.copy(alpha = 0.5f), radius = 38f, center = Offset((w * 0.40f - cloudShift + w) % w, h * 0.20f))
                    drawCircle(Color.White.copy(alpha = 0.4f), radius = 32f, center = Offset((w * 0.85f - cloudShift * 1.5f + w) % w, h * 0.65f))

                    // 2. Globos flotantes en el cielo (3, 4 o 5 globos con números)
                    for (b in balloons) {
                        val bx = b.xFraction * w
                        val by = b.yFraction * h

                        if (b.xFraction in -0.3f..1.4f) {
                            if (b.isPopped) {
                                // Efecto de explosión de partículas de globo
                                val burstRadius = 24f + b.popAnim * 36f
                                val burstAlpha = (1f - b.popAnim).coerceAtLeast(0f)
                                drawCircle(
                                    color = b.color.copy(alpha = burstAlpha),
                                    radius = burstRadius,
                                    center = Offset(bx, by),
                                    style = Stroke(width = 3f)
                                )
                                for (i in 0 until 6) {
                                    val ang = (i * 60f + b.popAnim * 90f) * (kotlin.math.PI.toFloat() / 180f)
                                    val px = bx + kotlin.math.cos(ang) * burstRadius
                                    val py = by + kotlin.math.sin(ang) * burstRadius
                                    drawCircle(
                                        color = b.color.copy(alpha = burstAlpha),
                                        radius = 4f * (1f - b.popAnim),
                                        center = Offset(px, py)
                                    )
                                }
                            } else {
                                // Cuerda del globo colgando hacia abajo
                                drawLine(
                                    color = Color(0xFF78909C),
                                    start = Offset(bx, by + 26f),
                                    end = Offset(bx - 3f, by + 48f),
                                    strokeWidth = 2.5f
                                )

                                // Nudo inferior del globo
                                drawCircle(
                                    color = b.color,
                                    radius = 4.5f,
                                    center = Offset(bx, by + 26f)
                                )

                                // Cuerpo ovalado del globo
                                val rx = 26f
                                val ry = 32f
                                drawOval(
                                    color = b.color,
                                    topLeft = Offset(bx - rx, by - ry),
                                    size = Size(rx * 2f, ry * 2f)
                                )

                                // Brillo 3D / reflejo en la parte superior izquierda
                                drawOval(
                                    color = Color.White.copy(alpha = 0.45f),
                                    topLeft = Offset(bx - rx * 0.65f, by - ry * 0.75f),
                                    size = Size(rx * 0.55f, ry * 0.5f)
                                )

                                // Insignia circular blanca en el centro para visibilidad óptima del número
                                drawCircle(
                                    color = Color.White.copy(alpha = 0.94f),
                                    radius = 17f,
                                    center = Offset(bx, by)
                                )

                                // Número en el centro del globo
                                val numText = "${b.value}"
                                val textPaint = android.graphics.Paint().apply {
                                    textSize = 23f
                                    color = android.graphics.Color.argb(255, 33, 33, 33)
                                    typeface = android.graphics.Typeface.DEFAULT_BOLD
                                    textAlign = android.graphics.Paint.Align.CENTER
                                    isAntiAlias = true
                                }
                                val tY = by - ((textPaint.descent() + textPaint.ascent()) / 2f)
                                drawContext.canvas.nativeCanvas.drawText(numText, bx, tY, textPaint)
                            }
                        }
                    }

                    // 3. Helicóptero VIENDO A LA DERECHA con inclinación suave
                    val hx = 0.22f * w
                    val hy = copterYFraction * h

                    val copterPaint = android.graphics.Paint().apply {
                        textSize = 64f
                        textAlign = android.graphics.Paint.Align.CENTER
                    }

                    drawContext.canvas.nativeCanvas.save()
                    // Reflejamos horizontalmente para que el helicóptero mire a la DERECHA
                    drawContext.canvas.nativeCanvas.scale(-1f, 1f, hx, hy)
                    // Inclinación dinámica de cabeceo (aerodinámica)
                    val tiltAngle = (-copterVy * 28f).coerceIn(-18f, 18f)
                    drawContext.canvas.nativeCanvas.rotate(tiltAngle, hx, hy)
                    drawContext.canvas.nativeCanvas.drawText("🚁", hx, hy + 10f, copterPaint)
                    drawContext.canvas.nativeCanvas.restore()

                    // Rastro de aire/humo del rotor hacia atrás (a la izquierda del helicóptero)
                    val pulse = (gameTime * 15f) % 1f
                    drawCircle(
                        color = Color.White.copy(alpha = 0.45f),
                        radius = 8f + pulse * 3f,
                        center = Offset(hx - 36f, hy + 12f)
                    )
                    drawCircle(
                        color = Color.White.copy(alpha = 0.30f),
                        radius = 13f + pulse * 4f,
                        center = Offset(hx - 54f, hy + 14f)
                    )
                    drawCircle(
                        color = Color.White.copy(alpha = 0.16f),
                        radius = 18f + pulse * 5f,
                        center = Offset(hx - 74f, hy + 16f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Controles de Vuelo: SUBIR y BAJAR con soporte continuo al mantener presionado
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // BOTÓN SUBIR (MANTENER PRESIONADO)
                val upBgColor = if (isHoldingUp) Color(0xFF0277BD) else Color(0xFF0288D1)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(62.dp)
                        .shadow(if (isHoldingUp) 2.dp else 5.dp, RoundedCornerShape(18.dp))
                        .clip(RoundedCornerShape(18.dp))
                        .background(upBgColor)
                        .border(2.dp, Color(0xFF81D4FA), RoundedCornerShape(18.dp))
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onPress = {
                                    isHoldingUp = true
                                    copterVy = (copterVy - 0.22f).coerceAtLeast(-0.62f)
                                    soundManager?.playButtonClick()
                                    hapticUtil?.vibrateClick()
                                    try {
                                        tryAwaitRelease()
                                    } finally {
                                        isHoldingUp = false
                                    }
                                }
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(text = "⬆️", fontSize = 26.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(horizontalAlignment = Alignment.Start) {
                            Text(
                                text = if (isEnglish) "FLY UP" else "SUBIR",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FredokaFont,
                                color = Color.White
                            )
                            Text(
                                text = if (isEnglish) "Hold pressed" else "Mantén presionado",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FredokaFont,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                        }
                    }
                }

                // BOTÓN BAJAR (MANTENER PRESIONADO)
                val downBgColor = if (isHoldingDown) Color(0xFF00838F) else Color(0xFF0097A7)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(62.dp)
                        .shadow(if (isHoldingDown) 2.dp else 5.dp, RoundedCornerShape(18.dp))
                        .clip(RoundedCornerShape(18.dp))
                        .background(downBgColor)
                        .border(2.dp, Color(0xFF80DEEA), RoundedCornerShape(18.dp))
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onPress = {
                                    isHoldingDown = true
                                    copterVy = (copterVy + 0.22f).coerceAtMost(0.62f)
                                    soundManager?.playButtonClick()
                                    hapticUtil?.vibrateClick()
                                    try {
                                        tryAwaitRelease()
                                    } finally {
                                        isHoldingDown = false
                                    }
                                }
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(text = "⬇️", fontSize = 26.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(horizontalAlignment = Alignment.Start) {
                            Text(
                                text = if (isEnglish) "FLY DOWN" else "BAJAR",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FredokaFont,
                                color = Color.White
                            )
                            Text(
                                text = if (isEnglish) "Hold pressed" else "Mantén presionado",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FredokaFont,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                        }
                    }
                }
            }
        }

        if (isGameOver) {
            ToyGameOverDialog(
                toyEmoji = toy.emoji,
                toyName = toy.name,
                score = score,
                coinsEarned = coinsEarned,
                isEnglish = isEnglish,
                onRetry = {
                    lives = 3
                    hasWonGame = false
                    isGameOver = false
                    copterYFraction = 0.50f
                    copterVy = 0f
                    isHoldingUp = false
                    isHoldingDown = false
                },
                onExit = onExitGame
            )
        }

        if (hasWonGame) {
            ToyVictoryDialog(
                toyEmoji = toy.emoji,
                toyName = toy.name,
                score = score,
                coinsEarned = coinsEarned,
                isEnglish = isEnglish,
                soundManager = soundManager,
                ttsManager = ttsManager,
                onPlayAgain = {
                    lives = 3
                    score = 0
                    coinsEarned = 0
                    correctHits = 0
                    hasWonGame = false
                    isGameOver = false
                    copterYFraction = 0.50f
                    copterVy = 0f
                    isHoldingUp = false
                    isHoldingDown = false
                    currentMission = generateCopterMission(difficulty)
                    spawnBalloonsForMission(currentMission)
                },
                onExit = onExitGame
            )
        }
    }
}

// =========================================================================
// 16. ROMPECABEZAS: Encaje de Piezas 3D 🧩 (Tablero de siluetas)
// =========================================================================
// =========================================================================
// 16. ROMPECABEZAS: Rompecabezas para Resolver 🧩 (Piezas reales de puzzle)
// =========================================================================

data class PuzzleSlot(
    val id: Int,
    val row: Int,
    val col: Int,
    val titleEs: String,
    val titleEn: String,
    val icon: String,
    val mathQuestion: String,
    val targetNumber: Int,
    val pieceColor: Color,
    var isSolved: Boolean = false
)

data class PuzzleTheme(
    val id: String,
    val nameEs: String,
    val nameEn: String,
    val bannerIcon: String,
    val backgroundColor: Color
)

@Composable
fun RompecabezasSnapGame(
    toy: ToyItem,
    isEnglish: Boolean,
    soundManager: SoundManager?,
    ttsManager: TtsManager?,
    hapticUtil: HapticUtil?,
    difficulty: ToyGameDifficulty = ToyGameDifficulty.FACIL,
    onExitGame: () -> Unit
) {
    val context = LocalContext.current
    var lives by remember { mutableIntStateOf(3) }
    var score by remember { mutableIntStateOf(0) }
    var coinsEarned by remember { mutableIntStateOf(0) }
    var correctHits by remember { mutableIntStateOf(0) }
    var hasWonGame by remember { mutableStateOf(false) }
    var isGameOver by remember { mutableStateOf(false) }
    var showConfetti by remember { mutableStateOf(false) }

    val themes = remember {
        listOf(
            PuzzleTheme("elephant", "El Elefante Sabio", "The Wise Elephant", "🐘", Color(0xFFE8F5E9)),
            PuzzleTheme("rocket", "El Cohete Espacial", "The Space Rocket", "🚀", Color(0xFFEDE7F6)),
            PuzzleTheme("lion", "El Rey León de la Selva", "The Jungle Lion King", "🦁", Color(0xFFFFF3E0)),
            PuzzleTheme("castle", "El Castillo Mágico", "The Magic Castle", "🏰", Color(0xFFE1F5FE))
        )
    }
    var currentThemeIndex by remember { mutableIntStateOf(0) }
    val currentTheme = themes[currentThemeIndex % themes.size]

    fun generateSlots(theme: PuzzleTheme, diff: ToyGameDifficulty): List<PuzzleSlot> {
        return if (diff == ToyGameDifficulty.FACIL) {
            when (theme.id) {
                "rocket" -> listOf(
                    PuzzleSlot(1, 0, 0, "Cúpula Estelar", "Star Dome", "⭐", "2 + 2", 4, Color(0xFF90CAF9)),
                    PuzzleSlot(2, 0, 1, "Punta del Cohete", "Rocket Nose", "🚀", "5 + 1", 6, Color(0xFF81D4FA)),
                    PuzzleSlot(3, 1, 0, "Propulsor de Fuego", "Fire Thruster", "🔥", "7 - 4", 3, Color(0xFFFFAB91)),
                    PuzzleSlot(4, 1, 1, "Planeta Lunar", "Moon Planet", "🪐", "9 - 1", 8, Color(0xFFB39DDB))
                )
                "lion" -> listOf(
                    PuzzleSlot(1, 0, 0, "Corona Real", "Royal Crown", "👑", "3 + 3", 6, Color(0xFFFFE082)),
                    PuzzleSlot(2, 0, 1, "Melena Feroz", "Fierce Mane", "🦁", "4 + 5", 9, Color(0xFFFFCC80)),
                    PuzzleSlot(3, 1, 0, "Huella Dorada", "Golden Paw", "🐾", "8 - 3", 5, Color(0xFFA5D6A7)),
                    PuzzleSlot(4, 1, 1, "Sol Tropical", "Tropical Sun", "☀️", "6 - 4", 2, Color(0xFFFFF59D))
                )
                "castle" -> listOf(
                    PuzzleSlot(1, 0, 0, "Torreón Mágico", "Magic Spire", "🗼", "4 + 3", 7, Color(0xFFCE93D8)),
                    PuzzleSlot(2, 0, 1, "Bandera Real", "Royal Flag", "🚩", "2 + 6", 8, Color(0xFFEF9A9A)),
                    PuzzleSlot(3, 1, 0, "Gran Muralla", "Stone Wall", "🧱", "10 - 5", 5, Color(0xFFB0BEC5)),
                    PuzzleSlot(4, 1, 1, "Puerta de Oro", "Golden Gate", "🚪", "9 - 6", 3, Color(0xFFFFE082))
                )
                else -> listOf(
                    PuzzleSlot(1, 0, 0, "Orejas y Gorrito", "Ears & Cap", "🎩", "2 + 1", 3, Color(0xFFCE93D8)),
                    PuzzleSlot(2, 0, 1, "Trompa Feliz", "Happy Trunk", "🐘", "3 + 2", 5, Color(0xFF80DEEA)),
                    PuzzleSlot(3, 1, 0, "Libro Matemático", "Math Book", "📖", "8 - 4", 4, Color(0xFFA5D6A7)),
                    PuzzleSlot(4, 1, 1, "Globos Festivos", "Party Balloons", "🎈", "7 - 1", 6, Color(0xFFFFAB91))
                )
            }
        } else {
            when (theme.id) {
                "rocket" -> listOf(
                    PuzzleSlot(1, 0, 0, "Cúpula Estelar", "Star Dome", "⭐", "2 × 2", 4, Color(0xFF90CAF9)),
                    PuzzleSlot(2, 0, 1, "Punta del Cohete", "Rocket Nose", "🚀", "3 × 2", 6, Color(0xFF81D4FA)),
                    PuzzleSlot(3, 1, 0, "Propulsor de Fuego", "Fire Thruster", "🔥", "12 ÷ 4", 3, Color(0xFFFFAB91)),
                    PuzzleSlot(4, 1, 1, "Planeta Lunar", "Moon Planet", "🪐", "16 ÷ 2", 8, Color(0xFFB39DDB))
                )
                "lion" -> listOf(
                    PuzzleSlot(1, 0, 0, "Corona Real", "Royal Crown", "👑", "2 × 3", 6, Color(0xFFFFE082)),
                    PuzzleSlot(2, 0, 1, "Melena Feroz", "Fierce Mane", "🦁", "3 × 3", 9, Color(0xFFFFCC80)),
                    PuzzleSlot(3, 1, 0, "Huella Dorada", "Golden Paw", "🐾", "15 ÷ 3", 5, Color(0xFFA5D6A7)),
                    PuzzleSlot(4, 1, 1, "Sol Tropical", "Tropical Sun", "☀️", "8 ÷ 4", 2, Color(0xFFFFF59D))
                )
                "castle" -> listOf(
                    PuzzleSlot(1, 0, 0, "Torreón Mágico", "Magic Spire", "🗼", "14 ÷ 2", 7, Color(0xFFCE93D8)),
                    PuzzleSlot(2, 0, 1, "Bandera Real", "Royal Flag", "🚩", "2 × 4", 8, Color(0xFFEF9A9A)),
                    PuzzleSlot(3, 1, 0, "Gran Muralla", "Stone Wall", "🧱", "20 ÷ 4", 5, Color(0xFFB0BEC5)),
                    PuzzleSlot(4, 1, 1, "Puerta de Oro", "Golden Gate", "🚪", "9 ÷ 3", 3, Color(0xFFFFE082))
                )
                else -> listOf(
                    PuzzleSlot(1, 0, 0, "Orejas y Gorrito", "Ears & Cap", "🎩", "3 × 1", 3, Color(0xFFCE93D8)),
                    PuzzleSlot(2, 0, 1, "Trompa Feliz", "Happy Trunk", "🐘", "10 ÷ 2", 5, Color(0xFF80DEEA)),
                    PuzzleSlot(3, 1, 0, "Libro Matemático", "Math Book", "📖", "2 × 2", 4, Color(0xFFA5D6A7)),
                    PuzzleSlot(4, 1, 1, "Globos Festivos", "Party Balloons", "🎈", "18 ÷ 3", 6, Color(0xFFFFAB91))
                )
            }
        }
    }

    val slots = remember(currentThemeIndex, difficulty) {
        mutableStateListOf<PuzzleSlot>().apply { addAll(generateSlots(currentTheme, difficulty)) }
    }

    var rootOrigin by remember { mutableStateOf(Offset.Zero) }
    val pieceOrigins = remember { mutableStateMapOf<Int, Offset>() }
    val slotBounds = remember { mutableStateMapOf<Int, Rect>() }

    // Piezas disponibles para colocar (las no resueltas + 1 señuelo)
    val trayPieces = remember(slots.map { it.isSolved }, currentThemeIndex) {
        val unsolved = slots.filter { !it.isSolved }
        val targets = unsolved.map { it.targetNumber }
        val dummy = ((targets.maxOrNull() ?: 5) + Random.nextInt(2, 5))
        (targets + dummy).distinct().shuffled()
    }

    var draggedPieceNumber by remember { mutableStateOf<Int?>(null) }
    var dragPosition by remember { mutableStateOf(Offset.Zero) }
    var selectedPieceNumber by remember { mutableIntStateOf(-1) }

    fun checkPuzzleComplete() {
        if (slots.all { it.isSolved }) {
            showConfetti = true
            score += 50
            coinsEarned += 3
            correctHits += slots.size
            CoinShopManager.addCoin(context, 3)
            soundManager?.playVictory()
            ttsManager?.speak(if (isEnglish) "Puzzle solved! Fantastic job!" else "¡Rompecabezas resuelto! ¡Excelente!")

            if (correctHits >= 12) {
                hasWonGame = true
            }
        }
    }

    fun handlePlacePiece(pieceNum: Int, slot: PuzzleSlot) {
        if (slot.isSolved) return

        if (pieceNum == slot.targetNumber) {
            slot.isSolved = true
            score += 20
            coinsEarned += 1
            CoinShopManager.addCoin(context, 1)
            soundManager?.playCorrect()
            hapticUtil?.vibrateClick()
            ttsManager?.speak(if (isEnglish) "Piece placed!" else "¡Pieza encajada!")
            selectedPieceNumber = -1
            checkPuzzleComplete()
        } else {
            lives = (lives - 1).coerceAtLeast(0)
            soundManager?.playIncorrect()
            hapticUtil?.vibrateEncourage()
            ttsManager?.speak(if (isEnglish) "Wrong slot! Try another piece" else "¡Esa pieza no encaja ahí!")
            if (lives == 0) isGameOver = true
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(currentTheme.backgroundColor)
            .onGloballyPositioned { rootOrigin = it.positionInRoot() }
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ToyGameTopBar(
                title = if (isEnglish) "Puzzle Solver" else "Gran Rompecabezas",
                toyEmoji = toy.emoji,
                lives = lives,
                coins = coinsEarned,
                correctHits = correctHits,
                targetHits = 12,
                difficulty = difficulty,
                isEnglish = isEnglish,
                onExitClick = onExitGame
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Título del puzzle actual y progreso
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(SurfaceWhite)
                    .border(2.dp, Color(0xFFFFB300), RoundedCornerShape(14.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${currentTheme.bannerIcon} ${if (isEnglish) currentTheme.nameEn else currentTheme.nameEs}",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FredokaFont,
                    color = Color(0xFFEF6C00)
                )
                val solvedCount = slots.count { it.isSolved }
                Text(
                    text = "🧩 $solvedCount/${slots.size}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2E7D32)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // TABLERO DEL ROMPECABEZAS 2x2
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .shadow(6.dp, RoundedCornerShape(24.dp))
                    .clip(RoundedCornerShape(24.dp))
                    .background(SurfaceWhite)
                    .border(3.dp, Color(0xFFFF8F00), RoundedCornerShape(24.dp))
                    .padding(10.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceEvenly
                ) {
                    // Cuadrícula 2x2 de piezas
                    Column(
                        modifier = Modifier
                            .size(240.dp)
                            .shadow(4.dp, RoundedCornerShape(18.dp))
                            .clip(RoundedCornerShape(18.dp))
                            .background(Color(0xFFFFF8E1))
                            .border(2.5.dp, Color(0xFFFFB300), RoundedCornerShape(18.dp))
                            .padding(6.dp),
                        verticalArrangement = Arrangement.SpaceEvenly,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        for (r in 0..1) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                for (c in 0..1) {
                                    val slot = slots.firstOrNull { it.row == r && it.col == c }
                                    if (slot != null) {
                                        val isOver = draggedPieceNumber != null && slotBounds[slot.id]?.inflate(35f)?.contains(dragPosition) == true
                                        val isSelectedMatch = selectedPieceNumber == slot.targetNumber

                                        Box(
                                            modifier = Modifier
                                                .onGloballyPositioned { coordinates ->
                                                    val localPos = coordinates.positionInRoot() - rootOrigin
                                                    slotBounds[slot.id] = Rect(localPos, Size(coordinates.size.width.toFloat(), coordinates.size.height.toFloat()))
                                                }
                                                .size(105.dp)
                                                .scale(if (isOver || isSelectedMatch) 1.08f else 1.0f)
                                                .shadow(if (slot.isSolved) 3.dp else 1.dp, RoundedCornerShape(14.dp))
                                                .clip(RoundedCornerShape(14.dp))
                                                .background(if (slot.isSolved) slot.pieceColor else if (isOver) Color(0xFFFFE082) else Color(0xFFF5F5F5))
                                                .border(
                                                    width = if (isOver) 3.dp else 2.dp,
                                                    color = if (slot.isSolved) Color(0xFFFF8F00) else if (isOver) Color(0xFFE65100) else Color(0xFFBDBDBD),
                                                    shape = RoundedCornerShape(14.dp)
                                                )
                                                .clickable {
                                                    if (selectedPieceNumber != -1) {
                                                        handlePlacePiece(selectedPieceNumber, slot)
                                                    } else if (!slot.isSolved) {
                                                        ttsManager?.speak(if (isEnglish) "Find piece ${slot.targetNumber} for ${slot.titleEn}!" else "¡Busca la pieza ${slot.targetNumber} para ${slot.titleEs}!")
                                                    }
                                                },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            if (slot.isSolved) {
                                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                    Text(text = slot.icon, fontSize = 42.sp)
                                                    Text(
                                                        text = if (isEnglish) slot.titleEn else slot.titleEs,
                                                        fontSize = 11.sp,
                                                        fontWeight = FontWeight.Black,
                                                        fontFamily = FredokaFont,
                                                        color = Color(0xFF1B5E20)
                                                    )
                                                }
                                            } else {
                                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                    Text(text = "🧩", fontSize = 24.sp, color = Color(0xFFFFA000))
                                                    Text(
                                                        text = "${slot.mathQuestion} =",
                                                        fontSize = 13.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        fontFamily = FredokaFont,
                                                        color = SlateDark
                                                    )
                                                    Text(
                                                        text = "?",
                                                        fontSize = 20.sp,
                                                        fontWeight = FontWeight.Black,
                                                        fontFamily = FredokaFont,
                                                        color = Color(0xFFE65100)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // BANDEJA DE PIEZAS DEL PUZZLE
            Text(
                text = if (isEnglish) "🧩 Puzzle Pieces (Drag or Tap):" else "🧩 Piezas para Encajar (Arrastra o Toca):",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FredokaFont,
                color = SlateDark
            )
            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                trayPieces.forEach { pVal ->
                    val matchingSlot = slots.firstOrNull { it.targetNumber == pVal }
                    val isSelected = selectedPieceNumber == pVal

                    Box(
                        modifier = Modifier
                            .onGloballyPositioned { coordinates ->
                                pieceOrigins[pVal] = coordinates.positionInRoot() - rootOrigin
                            }
                            .scale(if (isSelected) 1.15f else 1.0f)
                            .shadow(if (isSelected) 6.dp else 3.dp, RoundedCornerShape(16.dp))
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isSelected) Color(0xFFFFCC80) else Color(0xFFFFF8E1))
                            .border(2.dp, if (isSelected) Color(0xFFE65100) else Color(0xFFFF8F00), RoundedCornerShape(16.dp))
                            .pointerInput(pVal) {
                                detectDragGestures(
                                    onDragStart = { offset ->
                                        draggedPieceNumber = pVal
                                        val origin = pieceOrigins[pVal] ?: Offset.Zero
                                        dragPosition = origin + offset
                                        soundManager?.playButtonClick()
                                        hapticUtil?.vibrateClick()
                                    },
                                    onDrag = { change, dragAmount ->
                                        change.consume()
                                        dragPosition += dragAmount
                                    },
                                    onDragEnd = {
                                        val droppedVal = draggedPieceNumber
                                        if (droppedVal != null) {
                                            val hitSlot = slots.firstOrNull { slot ->
                                                val b = slotBounds[slot.id]
                                                b != null && b.inflate(35f).contains(dragPosition)
                                            }
                                            if (hitSlot != null) {
                                                handlePlacePiece(droppedVal, hitSlot)
                                            }
                                        }
                                        draggedPieceNumber = null
                                    },
                                    onDragCancel = {
                                        draggedPieceNumber = null
                                    }
                                )
                            }
                            .clickable {
                                selectedPieceNumber = pVal
                                soundManager?.playButtonClick()
                                hapticUtil?.vibrateClick()
                            }
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = matchingSlot?.icon ?: "🧩", fontSize = 24.sp)
                            Box(
                                modifier = Modifier
                                    .padding(top = 2.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0xFFFFE082))
                                    .padding(horizontal = 6.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    text = "N° $pVal",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFFBF360C)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Pieza flotante arrastrándose con el dedo exactamente bajo el toque
        if (draggedPieceNumber != null) {
            val matchingSlot = slots.firstOrNull { it.targetNumber == draggedPieceNumber }
            Box(
                modifier = Modifier
                    .offset {
                        IntOffset(
                            (dragPosition.x - 48.dp.toPx()).roundToInt(),
                            (dragPosition.y - 48.dp.toPx()).roundToInt()
                        )
                    }
                    .zIndex(999f)
                    .scale(1.25f)
                    .shadow(12.dp, RoundedCornerShape(16.dp))
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFFFF8E1))
                    .border(3.dp, Color(0xFFE65100), RoundedCornerShape(16.dp))
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = matchingSlot?.icon ?: "🧩", fontSize = 32.sp)
                    Text(
                        text = "Pieza N° $draggedPieceNumber",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFFE65100)
                    )
                }
            }
        }

        if (showConfetti) {
            ConfettiCelebration(visible = true, onFinished = {
                showConfetti = false
                currentThemeIndex++
            })
        }

        if (isGameOver) {
            ToyGameOverDialog(
                toyEmoji = toy.emoji,
                toyName = toy.name,
                score = score,
                coinsEarned = coinsEarned,
                isEnglish = isEnglish,
                onRetry = {
                    lives = 3
                    hasWonGame = false
                    isGameOver = false
                },
                onExit = onExitGame
            )
        }

        if (hasWonGame) {
            ToyVictoryDialog(
                toyEmoji = toy.emoji,
                toyName = toy.name,
                score = score,
                coinsEarned = coinsEarned,
                isEnglish = isEnglish,
                soundManager = soundManager,
                ttsManager = ttsManager,
                onPlayAgain = {
                    hasWonGame = false
                    correctHits = 0
                    lives = 3
                    score = 0
                    coinsEarned = 0
                    isGameOver = false
                    currentThemeIndex = 0
                    slots.forEach { it.isSolved = false }
                },
                onExit = onExitGame
            )
        }
    }
}
