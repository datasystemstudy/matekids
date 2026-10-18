package com.educa.mathkids.presentation.screens.shop.games

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
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
import androidx.compose.ui.graphics.Brush
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
import com.educa.mathkids.presentation.components.MascotMood
import com.educa.mathkids.presentation.components.MetaCompanion
import com.educa.mathkids.presentation.theme.BluePrimary
import com.educa.mathkids.presentation.theme.FredokaFont
import com.educa.mathkids.presentation.theme.GreenPrimary
import com.educa.mathkids.presentation.theme.SlateDark
import com.educa.mathkids.presentation.theme.SlateMuted
import com.educa.mathkids.presentation.theme.SurfaceWhite
import com.educa.mathkids.presentation.utils.HapticUtil
import com.educa.mathkids.presentation.utils.SoundManager
import com.educa.mathkids.presentation.utils.TtsManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class MemoryCard(
    val instanceId: Int,
    val toy: ToyItem,
    var isFlipped: Boolean = false,
    var isMatched: Boolean = false
)

/**
 * Minijuego: Memorama Didáctico con los Juguetes Canjeados del niño.
 */
@Composable
fun ToyMemoryGame(
    myToys: List<ToyItem>,
    isEnglish: Boolean,
    soundManager: SoundManager?,
    ttsManager: TtsManager?,
    hapticUtil: HapticUtil?,
    onExitGame: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Preparar lista de cartas basada en los juguetes canjeados
    fun generateDeck(): List<MemoryCard> {
        val baseToys = if (myToys.size >= 2) {
            myToys.shuffled().take(6)
        } else {
            // Si solo tiene 1 juguete, duplicar y agregar juguetes sorpresa del catálogo
            val filler = CoinShopManager.TOYS_CATALOG.filter { it.id != myToys.firstOrNull()?.id }.take(3)
            (myToys + filler).take(4)
        }

        val pairs = (baseToys + baseToys).shuffled()
        return pairs.mapIndexed { index, toy ->
            MemoryCard(instanceId = index, toy = toy)
        }
    }

    var cards by remember { mutableStateOf(generateDeck()) }
    var flippedIndices by remember { mutableStateOf<List<Int>>(emptyList()) }
    var matchedPairsCount by remember { mutableIntStateOf(0) }
    var movesCount by remember { mutableIntStateOf(0) }
    var isGameWon by remember { mutableStateOf(false) }
    var coinsAwarded by remember { mutableStateOf(false) }

    val totalPairs = cards.size / 2

    fun resetGame() {
        cards = generateDeck()
        flippedIndices = emptyList()
        matchedPairsCount = 0
        movesCount = 0
        isGameWon = false
        coinsAwarded = false
        soundManager?.playButtonClick()
        ttsManager?.speak(if (isEnglish) "Find the matching pairs of your toys!" else "¡Encuentra las parejas de tus juguetes!")
    }

    LaunchedEffect(Unit) {
        ttsManager?.speak(if (isEnglish) "Toy Memory! Tap the cards to find matching pairs!" else "¡Memorama de Juguetes! ¡Toca las cartas para encontrar parejas!")
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Marcador de parejas y movimientos
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFE8F5E9))
                        .border(1.dp, GreenPrimary, RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = if (isEnglish) "🃏 Pairs: $matchedPairsCount / $totalPairs" else "🃏 Parejas: $matchedPairsCount / $totalPairs",
                        fontWeight = FontWeight.Black,
                        fontFamily = FredokaFont,
                        color = GreenPrimary,
                        fontSize = 13.5.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFFFF3E0))
                        .border(1.dp, Color(0xFFFF9800), RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = if (isEnglish) "🎯 Moves: $movesCount" else "🎯 Intentos: $movesCount",
                        fontWeight = FontWeight.Black,
                        fontFamily = FredokaFont,
                        color = Color(0xFFE65100),
                        fontSize = 13.5.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Mascota interactiva
            MetaCompanion(
                message = if (isGameWon) {
                    if (isEnglish) "Awesome memory! You matched all your toys and won 3 coins!"
                    else "¡Memoria brillante! ¡Emparejaste todos tus juguetes y ganaste 3 monedas!"
                } else {
                    if (isEnglish) "Tap any card to turn it around!"
                    else "¡Toca una carta para descubrir tu juguete!"
                },
                mood = if (isGameWon) MascotMood.CELEBRATING else MascotMood.HAPPY,
                mascotSize = 72.dp,
                ttsManager = ttsManager,
                onMascotClick = { soundManager?.playButtonClick() }
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Cuadrícula de Cartas
            val columns = if (cards.size > 8) 4 else 3
            LazyVerticalGrid(
                columns = GridCells.Fixed(columns),
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(cards) { index, card ->
                    MemoryCardView(
                        card = card,
                        onClick = {
                            if (card.isMatched || card.isFlipped || flippedIndices.size >= 2) return@MemoryCardView

                            soundManager?.playButtonClick()
                            hapticUtil?.vibrateClick()
                            ttsManager?.speak(card.toy.name)

                            val updated = cards.toMutableList()
                            updated[index] = card.copy(isFlipped = true)
                            cards = updated

                            val currentFlipped = flippedIndices + index
                            flippedIndices = currentFlipped

                            if (currentFlipped.size == 2) {
                                movesCount++
                                val first = cards[currentFlipped[0]]
                                val second = cards[currentFlipped[1]]

                                if (first.toy.id == second.toy.id) {
                                    // ¡Acierto!
                                    coroutineScope.launch {
                                        delay(300)
                                        val matchedList = cards.toMutableList()
                                        matchedList[currentFlipped[0]] = first.copy(isMatched = true)
                                        matchedList[currentFlipped[1]] = second.copy(isMatched = true)
                                        cards = matchedList
                                        flippedIndices = emptyList()
                                        matchedPairsCount++
                                        soundManager?.playVictory()
                                        hapticUtil?.vibrateReward()

                                        if (matchedPairsCount >= totalPairs) {
                                            isGameWon = true
                                            if (!coinsAwarded) {
                                                coinsAwarded = true
                                                CoinShopManager.addCoin(context, 3)
                                            }
                                        }
                                    }
                                } else {
                                    // Fallo: volver a voltear tras pausa
                                    coroutineScope.launch {
                                        delay(900)
                                        val resetList = cards.toMutableList()
                                        resetList[currentFlipped[0]] = first.copy(isFlipped = false)
                                        resetList[currentFlipped[1]] = second.copy(isFlipped = false)
                                        cards = resetList
                                        flippedIndices = emptyList()
                                    }
                                }
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Botón Reiniciar / Salir
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = { resetGame() },
                    colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(46.dp)
                ) {
                    Text(
                        text = if (isEnglish) "🔄 PLAY AGAIN" else "🔄 OTRA VEZ",
                        fontWeight = FontWeight.Black,
                        fontFamily = FredokaFont,
                        fontSize = 13.sp,
                        color = Color.White
                    )
                }

                Button(
                    onClick = onExitGame,
                    colors = ButtonDefaults.buttonColors(containerColor = SlateDark),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(46.dp)
                ) {
                    Text(
                        text = if (isEnglish) "EXIT" else "SALIR",
                        fontWeight = FontWeight.Black,
                        fontFamily = FredokaFont,
                        fontSize = 13.sp,
                        color = Color.White
                    )
                }
            }
        }

        // Celebración de confeti al ganar
        if (isGameWon) {
            ConfettiCelebration(visible = true)
        }
    }
}

@Composable
private fun MemoryCardView(
    card: MemoryCard,
    onClick: () -> Unit
) {
    val rotation by animateFloatAsState(
        targetValue = if (card.isFlipped || card.isMatched) 180f else 0f,
        animationSpec = tween(durationMillis = 350),
        label = "card_flip"
    )

    Box(
        modifier = Modifier
            .aspectRatio(0.85f)
            .shadow(3.dp, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 12f * density
            }
            .clickable(enabled = !card.isMatched && !card.isFlipped) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (rotation >= 90f) {
            // Cara del frente: muestra el juguete
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer { rotationY = 180f }
                    .background(if (card.isMatched) Color(0xFFE8F5E9) else SurfaceWhite)
                    .border(
                        2.dp,
                        if (card.isMatched) GreenPrimary else Color(0xFFFFB300),
                        RoundedCornerShape(16.dp)
                    )
                    .padding(4.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = card.toy.emoji, fontSize = 34.sp)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = card.toy.name,
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FredokaFont,
                        color = SlateDark,
                        textAlign = TextAlign.Center,
                        maxLines = 1
                    )
                }
            }
        } else {
            // Reverso: diseño misterioso infantil
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.linearGradient(
                            listOf(Color(0xFF7E57C2), Color(0xFF5E35B1))
                        )
                    )
                    .border(2.dp, Color(0xFFB39DDB), RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "🎁", fontSize = 30.sp)
            }
        }
    }
}
