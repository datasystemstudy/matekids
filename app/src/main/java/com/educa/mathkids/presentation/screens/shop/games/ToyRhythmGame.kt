package com.educa.mathkids.presentation.screens.shop.games

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.educa.mathkids.core.shop.CoinShopManager
import com.educa.mathkids.domain.model.shop.ToyItem
import com.educa.mathkids.presentation.components.ConfettiCelebration
import com.educa.mathkids.presentation.theme.FredokaFont
import com.educa.mathkids.presentation.theme.SlateDark
import com.educa.mathkids.presentation.theme.SlateMuted
import com.educa.mathkids.presentation.theme.SurfaceWhite
import com.educa.mathkids.presentation.utils.HapticUtil
import com.educa.mathkids.presentation.utils.SoundManager
import com.educa.mathkids.presentation.utils.TtsManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private data class SoundPad(
    val id: Int,
    val noteName: String,
    val nameEs: String,
    val nameEn: String,
    val emoji: String,
    val normalColor: Color,
    val activeColor: Color,
    val rimColor: Color
)

private val PADS = listOf(
    SoundPad(0, "Do", "Do (Rojo)", "Do (Red)", "🔴", Color(0xFFE53935), Color(0xFFFFCDD2), Color(0xFFB71C1C)),
    SoundPad(1, "Re", "Re (Azul)", "Re (Blue)", "🔵", Color(0xFF1E88E5), Color(0xFFBBDEFB), Color(0xFF0D47A1)),
    SoundPad(2, "Mi", "Mi (Verde)", "Mi (Green)", "🟢", Color(0xFF43A047), Color(0xFFC8E6C9), Color(0xFF1B5E20)),
    SoundPad(3, "Fa", "Fa (Amarillo)", "Fa (Yellow)", "🟡", Color(0xFFFDD835), Color(0xFFFFF9C4), Color(0xFFF57F17))
)

/**
 * Melodías infantiles clásicas adaptadas a los 4 tonos (Do, Re, Mi, Fa).
 */
private data class MelodySong(
    val id: String,
    val titleEs: String,
    val titleEn: String,
    val emoji: String,
    val notes: List<Int>, // Secuencia de notas entre 0 y 3
    val noteNames: List<String>
)

private val MELODY_SONGS = listOf(
    MelodySong(
        id = "himno_alegria",
        titleEs = "Himno a la Alegría",
        titleEn = "Ode to Joy",
        emoji = "🎶",
        notes = listOf(2, 2, 3, 2, 1, 0, 1, 2, 1, 0),
        noteNames = listOf("Mi", "Mi", "Fa", "Mi", "Re", "Do", "Re", "Mi", "Re", "Do")
    ),
    MelodySong(
        id = "campanero",
        titleEs = "Campanero",
        titleEn = "Frère Jacques",
        emoji = "🔔",
        notes = listOf(0, 1, 2, 0, 0, 1, 2, 0, 2, 3, 2, 0),
        noteNames = listOf("Do", "Re", "Mi", "Do", "Do", "Re", "Mi", "Do", "Mi", "Fa", "Mi", "Do")
    ),
    MelodySong(
        id = "escalera",
        titleEs = "Escalera Musical",
        titleEn = "Musical Scale",
        emoji = "🎹",
        notes = listOf(0, 1, 2, 3, 3, 2, 1, 0, 0, 2, 1, 0),
        noteNames = listOf("Do", "Re", "Mi", "Fa", "Fa", "Mi", "Re", "Do", "Do", "Mi", "Re", "Do")
    ),
    MelodySong(
        id = "cucu_rana",
        titleEs = "Cucú Cantaba la Rana",
        titleEn = "Cuckoo Melody",
        emoji = "🐸",
        notes = listOf(0, 2, 0, 2, 1, 3, 1, 0, 2, 3, 2, 0),
        noteNames = listOf("Do", "Mi", "Do", "Mi", "Re", "Fa", "Re", "Do", "Mi", "Fa", "Mi", "Do")
    )
)

private data class RhythmDrumQuestion(
    val equation: String,
    val targetAnswer: Int,
    val targetPadId: Int, // 0..3
    val padAnswers: List<Int> // Respuestas mostradas en cada tambor
)

private fun generateDrumQuestion(targetPadId: Int, difficulty: ToyGameDifficulty): RhythmDrumQuestion {
    val random = java.util.Random()
    val targetAnswer: Int
    val equation: String

    when (difficulty) {
        ToyGameDifficulty.FACIL -> {
            val isAddition = random.nextBoolean()
            if (isAddition) {
                val a = random.nextInt(9) + 1
                val b = random.nextInt(9) + 1
                targetAnswer = a + b
                equation = "$a + $b = ?"
            } else {
                val a = random.nextInt(9) + 3
                val b = random.nextInt(a - 1) + 1
                targetAnswer = a - b
                equation = "$a − $b = ?"
            }
        }
        ToyGameDifficulty.MODERADO -> {
            val isMultiplication = random.nextBoolean()
            if (isMultiplication) {
                val a = random.nextInt(5) + 2
                val b = random.nextInt(5) + 2
                targetAnswer = a * b
                equation = "$a × $b = ?"
            } else {
                val b = random.nextInt(4) + 2
                val ans = random.nextInt(5) + 2
                val a = b * ans
                targetAnswer = ans
                equation = "$a ÷ $b = ?"
            }
        }
    }

    // Generar distractores únicos y positivos
    val distractors = mutableSetOf<Int>()
    val offsets = listOf(-3, -2, -1, 1, 2, 3, 4, 5).shuffled(random)
    for (offset in offsets) {
        val dist = targetAnswer + offset
        if (dist > 0 && dist != targetAnswer) {
            distractors.add(dist)
            if (distractors.size == 3) break
        }
    }
    var fallback = 1
    while (distractors.size < 3) {
        if (fallback != targetAnswer && !distractors.contains(fallback)) {
            distractors.add(fallback)
        }
        fallback++
    }

    val distractorList = distractors.toList().shuffled(random)
    var dIdx = 0
    val padAnswers = mutableListOf<Int>()
    for (i in 0..3) {
        if (i == targetPadId) {
            padAnswers.add(targetAnswer)
        } else {
            padAnswers.add(distractorList[dIdx++])
        }
    }

    return RhythmDrumQuestion(
        equation = equation,
        targetAnswer = targetAnswer,
        targetPadId = targetPadId,
        padAnswers = padAnswers
    )
}

/**
 * Minijuego 2D: Tambor Musical con Operaciones Matemáticas y Melodía Completa.
 */
@Composable
fun ToyRhythmGame(
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
    var coinsCollectedThisRun by remember { mutableIntStateOf(0) }
    var hasWonGame by remember { mutableStateOf(false) }
    var isGameOver by remember { mutableStateOf(false) }
    var isPaused by remember { mutableStateOf(false) }
    var showCelebration by remember { mutableStateOf(false) }

    // Control de Canciones y Formación Progresiva de Melodía
    var currentSongIndex by remember { mutableIntStateOf(0) }
    val currentSong = MELODY_SONGS[currentSongIndex % MELODY_SONGS.size]
    var playerStep by remember { mutableIntStateOf(0) }
    var activePadId by remember { mutableStateOf<Int?>(null) }
    var isPlayingMelody by remember { mutableStateOf(false) }
    var statusText by remember { mutableStateOf("") }

    // Generar la pregunta actual correspondiente a la nota en curso
    var currentQuestion by remember(currentSongIndex, playerStep) {
        val targetNote = currentSong.notes.getOrElse(playerStep) { 0 }
        mutableStateOf(generateDrumQuestion(targetNote, difficulty))
    }

    fun restartGame() {
        lives = 3
        score = 0
        coinsCollectedThisRun = 0
        hasWonGame = false
        playerStep = 0
        isGameOver = false
        isPaused = false
        isPlayingMelody = false
        currentQuestion = generateDrumQuestion(currentSong.notes[0], difficulty)
        statusText = if (isEnglish) "Solve and tap the correct drum!" else "¡Resuelve y toca el tambor correcto!"
    }

    LaunchedEffect(currentSongIndex) {
        ttsManager?.speak(
            if (isEnglish) "Play the drum to complete ${currentSong.titleEn}!"
            else "¡Toca el tambor para completar ${currentSong.titleEs}!"
        )
        playerStep = 0
        statusText = if (isEnglish) "Solve and tap the correct drum!" else "¡Resuelve y toca el tambor correcto!"
    }

    val targetPad = PADS[currentQuestion.targetPadId]

    fun onPadPressed(padId: Int) {
        if (isPlayingMelody || isGameOver || isPaused || hasWonGame) return

        // 1. Tocar el tambor con sonido y háptica
        soundManager?.playMusicalNote(padId)
        hapticUtil?.vibrateClick()
        activePadId = padId
        coroutineScope.launch {
            delay(220)
            if (activePadId == padId) activePadId = null
        }

        // 2. Comprobar si la respuesta es correcta
        if (padId == currentQuestion.targetPadId) {
            // ¡Acierto!
            score += 20
            coinsCollectedThisRun += 1
            CoinShopManager.addCoin(context, 1)
            hapticUtil?.vibrateSuccess()

            val nextStep = playerStep + 1

            if (nextStep >= currentSong.notes.size) {
                // ¡MELODÍA COMPLETADA! Finalizar el juego con la canción entera
                playerStep = nextStep
                score += 50
                coinsCollectedThisRun += 5
                CoinShopManager.addCoin(context, 5)
                statusText = if (isEnglish)
                    "🎉 Complete melody! Listen to your song! 🎶"
                else
                    "🎉 ¡Melodía completa! ¡Escucha tu canción! 🎶"
                isPlayingMelody = true

                coroutineScope.launch {
                    delay(300)
                    ttsManager?.speak(
                        if (isEnglish) "Bravo! You completed the melody!"
                        else "¡Bravo! ¡Completaste la melodía!"
                    )
                    delay(900)

                    // Reproducir cada nota de la melodía completa secuencialmente
                    for (notePadId in currentSong.notes) {
                        activePadId = notePadId
                        soundManager?.playMusicalNote(notePadId)
                        hapticUtil?.vibrateClick()
                        delay(360)
                        activePadId = null
                        delay(80)
                    }

                    soundManager?.playApplause()
                    soundManager?.playVictory()
                    hapticUtil?.vibrateReward()
                    showCelebration = true
                    statusText = if (isEnglish)
                        "🏆 You won! Fantastic melody!"
                    else
                        "🏆 ¡Ganaste! ¡Fantástica melodía!"
                    isPlayingMelody = false

                    delay(1200)
                    hasWonGame = true
                }
            } else {
                playerStep = nextStep
                statusText = if (isEnglish) "Great! Next note 🎵" else "¡Excelente! Siguiente nota 🎵"
                currentQuestion = generateDrumQuestion(currentSong.notes[nextStep], difficulty)
            }
        } else {
            // Fallo -> pierde 1 vida
            lives = maxOf(0, lives - 1)
            soundManager?.playIncorrect()
            hapticUtil?.vibrateEncourage()

            if (lives <= 0) {
                isGameOver = true
            } else {
                statusText = if (isEnglish)
                    "Oops! Look for the color of ${targetPad.nameEn}!"
                else
                    "¡Uy! ¡Busca el color de ${targetPad.nameEs}!"
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Cabecera unificada con Vidas, Monedas y Salida
            ToyGameTopBar(
                title = if (isEnglish) "Musical Drum" else "Tambor Musical",
                toyEmoji = "🥁",
                lives = lives,
                coins = coinsCollectedThisRun,
                correctHits = playerStep,
                targetHits = currentSong.notes.size,
                difficulty = difficulty,
                isEnglish = isEnglish,
                onExitClick = onExitGame,
                onPauseClick = {
                    isPaused = true
                    soundManager?.playButtonClick()
                    hapticUtil?.vibrateClick()
                }
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Tarjeta de la Canción y Notas de la Melodía
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFFEDE7F6))
                    .border(1.5.dp, Color(0xFF7E57C2), RoundedCornerShape(14.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = currentSong.emoji, fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isEnglish) currentSong.titleEn else currentSong.titleEs,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FredokaFont,
                        color = SlateDark
                    )
                }
                Text(
                    text = "$playerStep / ${currentSong.notes.size} 🎵",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FredokaFont,
                    color = Color(0xFF6A1B9A)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // ── TARJETA DIDÁCTICA DE OPERACIÓN MATEMÁTICA Y COLOR CORRESPONDIENTE ──
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(5.dp, RoundedCornerShape(20.dp), spotColor = targetPad.normalColor.copy(alpha = 0.4f))
                    .clip(RoundedCornerShape(20.dp))
                    .background(SurfaceWhite)
                    .border(3.dp, targetPad.normalColor, RoundedCornerShape(20.dp))
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = statusText,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FredokaFont,
                        color = SlateMuted,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Ecuación Matemática Grande
                    Text(
                        text = currentQuestion.equation,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FredokaFont,
                        color = SlateDark,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Indicador del Color y Nota Correspondiente al Resultado Correcto
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = if (isEnglish) "Correct color: " else "Resultado en tambor: ",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FredokaFont,
                            color = SlateMuted
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(targetPad.normalColor)
                                .padding(horizontal = 8.dp, vertical = 3.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${targetPad.emoji} ${targetPad.noteName} (${if (isEnglish) targetPad.nameEn.substringAfter("· ") else targetPad.nameEs.substringAfter("· ")})",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FredokaFont,
                                color = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Línea de notas de la melodía en formación
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        currentSong.notes.forEachIndexed { idx, padId ->
                            val isPlayed = idx < playerStep
                            val isCurrent = idx == playerStep
                            val padInfo = PADS[padId]

                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 2.dp)
                                    .size(if (isCurrent) 18.dp else 14.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when {
                                            isPlayed -> padInfo.normalColor
                                            isCurrent -> targetPad.normalColor
                                            else -> Color(0xFFE0E0E0)
                                        }
                                    )
                                    .border(
                                        width = if (isCurrent) 2.dp else 0.dp,
                                        color = if (isCurrent) Color.White else Color.Transparent,
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isPlayed) {
                                    Text(text = "✓", fontSize = 8.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // ── CUADRÍCULA 2x2 DE TAMBORES INTERACTIVOS ──
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val padPairs = PADS.chunked(2)
                padPairs.forEach { rowPads ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        rowPads.forEach { pad ->
                            val isActive = activePadId == pad.id
                            val answerValue = currentQuestion.padAnswers.getOrElse(pad.id) { 0 }
                            val scale by animateFloatAsState(
                                targetValue = if (isActive) 1.07f else 1.0f,
                                animationSpec = spring(),
                                label = "drum_scale_${pad.id}"
                            )

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxSize()
                                    .graphicsLayer {
                                        scaleX = scale
                                        scaleY = scale
                                    }
                                    .shadow(if (isActive) 12.dp else 5.dp, RoundedCornerShape(26.dp), spotColor = pad.normalColor.copy(alpha = 0.5f))
                                    .clip(RoundedCornerShape(26.dp))
                                    .background(if (isActive) pad.activeColor else pad.normalColor)
                                    .border(
                                        width = if (isActive) 4.dp else 2.5.dp,
                                        color = if (isActive) Color.White else pad.rimColor,
                                        shape = RoundedCornerShape(26.dp)
                                    )
                                    .clickable(enabled = !isPlayingMelody && !isGameOver && !isPaused && !hasWonGame) {
                                        onPadPressed(pad.id)
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    // Encabezado del tambor (Emoji + Nota)
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Text(text = "🥁", fontSize = 24.sp)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = pad.noteName,
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Black,
                                            fontFamily = FredokaFont,
                                            color = Color.White
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(4.dp))

                                    // Resultado / Número Grande en el parche del tambor
                                    Box(
                                        modifier = Modifier
                                            .size(54.dp)
                                            .clip(CircleShape)
                                            .background(Color.White.copy(alpha = 0.92f))
                                            .border(2.dp, pad.rimColor, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "$answerValue",
                                            fontSize = 28.sp,
                                            fontWeight = FontWeight.Black,
                                            fontFamily = FredokaFont,
                                            color = pad.rimColor
                                        )
                                    }
                                }
                            }
                        }
                    }
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
                toyEmoji = toy.emoji,
                toyName = toy.name,
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

        // Diálogo de Fin de Partida (Game Over)
        if (isGameOver) {
            ToyGameOverDialog(
                toyEmoji = toy.emoji,
                toyName = toy.name,
                score = score,
                coinsEarned = coinsCollectedThisRun,
                isEnglish = isEnglish,
                onRetry = {
                    restartGame()
                },
                onExit = onExitGame
            )
        }

        // Diálogo de Victoria al completar la melodía
        if (hasWonGame) {
            ToyVictoryDialog(
                toyEmoji = "🥁",
                toyName = if (isEnglish) currentSong.titleEn else currentSong.titleEs,
                score = score,
                coinsEarned = coinsCollectedThisRun,
                isEnglish = isEnglish,
                soundManager = soundManager,
                ttsManager = ttsManager,
                onPlayAgain = {
                    currentSongIndex = (currentSongIndex + 1) % MELODY_SONGS.size
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
