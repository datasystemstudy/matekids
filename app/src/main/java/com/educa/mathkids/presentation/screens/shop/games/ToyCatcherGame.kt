package com.educa.mathkids.presentation.screens.shop.games

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
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
import com.educa.mathkids.presentation.theme.OrangeSecondary
import com.educa.mathkids.presentation.theme.SlateDark
import com.educa.mathkids.presentation.theme.SurfaceWhite
import com.educa.mathkids.presentation.utils.HapticUtil
import com.educa.mathkids.presentation.utils.SoundManager
import com.educa.mathkids.presentation.utils.TtsManager
import kotlin.random.Random

data class FloatingToyBubble(
    val id: Int,
    val toy: ToyItem,
    val xFraction: Float,
    val initialYFraction: Float,
    val speed: Float,
    val sizeDp: Int = 64
)

/**
 * Minijuego: Atrapa el Juguete Flotante.
 * Desarrolla reflejos, atención visual y conteo interactivo usando los juguetes canjeados.
 */
@Composable
fun ToyCatcherGame(
    myToys: List<ToyItem>,
    isEnglish: Boolean,
    soundManager: SoundManager?,
    ttsManager: TtsManager?,
    hapticUtil: HapticUtil?,
    onExitGame: () -> Unit
) {
    val context = LocalContext.current
    val random = remember { Random(System.currentTimeMillis()) }

    val targetToy = remember(myToys) {
        myToys.randomOrNull() ?: CoinShopManager.TOYS_CATALOG.first()
    }
    val targetCountGoal = 5

    var caughtCount by remember { mutableIntStateOf(0) }
    var isMissionWon by remember { mutableStateOf(false) }
    var coinsAwarded by remember { mutableStateOf(false) }

    // Generar burbujas flotantes
    val bubbleCatalog = remember(myToys, targetToy) {
        val pool = (List(3) { targetToy } + myToys + myToys).shuffled()
        pool.mapIndexed { index, toy ->
            FloatingToyBubble(
                id = index,
                toy = toy,
                xFraction = (index % 4) * 0.22f + 0.08f + (random.nextFloat() * 0.08f),
                initialYFraction = random.nextFloat(),
                speed = 0.5f + (random.nextFloat() * 0.6f)
            )
        }
    }

    LaunchedEffect(targetToy) {
        val prompt = if (isEnglish) {
            "Mission: Catch $targetCountGoal ${targetToy.name}! Tap them as they float!"
        } else {
            "¡Misión: Atrapa $targetCountGoal ${targetToy.name}! ¡Tócalos mientras flotan!"
        }
        ttsManager?.speak(prompt)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Cabecera de Misión
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(4.dp, RoundedCornerShape(18.dp))
                    .clip(RoundedCornerShape(18.dp))
                    .background(SurfaceWhite)
                    .border(2.dp, OrangeSecondary.copy(alpha = 0.6f), RoundedCornerShape(18.dp))
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFFF3E0)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = targetToy.emoji, fontSize = 26.sp)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = if (isEnglish) "TARGET TOY:" else "OBJETIVO:",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = OrangeSecondary
                            )
                            Text(
                                text = targetToy.name,
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FredokaFont,
                                color = SlateDark,
                                maxLines = 1
                            )
                        }
                    }

                    // Contador
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(GreenPrimary)
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "🎯 $caughtCount / $targetCountGoal",
                            fontWeight = FontWeight.Black,
                            fontFamily = FredokaFont,
                            color = Color.White,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Barra de progreso
            val progressFraction = (caughtCount.toFloat() / targetCountGoal.toFloat()).coerceIn(0f, 1f)
            LinearProgressIndicator(
                progress = { progressFraction },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = GreenPrimary,
                trackColor = Color(0xFFE2E8F0),
                strokeCap = StrokeCap.Round
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Mascota Mate con diálogo
            MetaCompanion(
                message = if (isMissionWon) {
                    if (isEnglish) "Woohoo! You caught all 5 ${targetToy.name}! You won 3 coins!"
                    else "¡Súper! ¡Atrapaste los 5 ${targetToy.name}! ¡Ganaste 3 monedas!"
                } else {
                    if (isEnglish) "Find and tap the ${targetToy.emoji} ${targetToy.name}!"
                    else "¡Busca y toca los ${targetToy.emoji} ${targetToy.name}!"
                },
                mood = if (isMissionWon) MascotMood.CELEBRATING else MascotMood.HAPPY,
                mascotSize = 68.dp,
                ttsManager = ttsManager,
                onMascotClick = { ttsManager?.speak(targetToy.name) }
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Zona de burbujas flotantes animadas
            BoxWithConstraints(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(22.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFFE0F7FA), Color(0xFFE1F5FE), Color(0xFFEDE7F6))
                        )
                    )
                    .border(2.dp, Color(0xFF00B0FF).copy(alpha = 0.4f), RoundedCornerShape(22.dp))
            ) {
                val boxWidth = maxWidth
                val boxHeight = maxHeight

                val transition = rememberInfiniteTransition(label = "bubbles_anim")
                val animTime by transition.animateFloat(
                    initialValue = 0f,
                    targetValue = 1f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(durationMillis = 6500, easing = LinearEasing),
                        repeatMode = RepeatMode.Restart
                    ),
                    label = "time_float"
                )

                bubbleCatalog.forEach { bubble ->
                    // Calcular posición vertical flotante de abajo a arriba en bucle
                    val currentY = ((bubble.initialYFraction - (animTime * bubble.speed)) % 1f + 1f) % 1f
                    val offsetX = boxWidth * bubble.xFraction
                    val offsetY = boxHeight * currentY

                    var isBubblePopped by remember(bubble.id, isMissionWon) { mutableStateOf(false) }

                    if (!isBubblePopped && !isMissionWon) {
                        Box(
                            modifier = Modifier
                                .offset(x = offsetX, y = offsetY)
                                .size(64.dp)
                                .shadow(3.dp, CircleShape)
                                .clip(CircleShape)
                                .background(
                                    Brush.radialGradient(
                                        listOf(Color.White, Color(0xFFE1F5FE))
                                    )
                                )
                                .border(1.5.dp, Color(0xFF4FC3F7), CircleShape)
                                .clickable {
                                    if (bubble.toy.id == targetToy.id) {
                                        // Acierto: atrapó el juguete objetivo
                                        isBubblePopped = true
                                        caughtCount++
                                        soundManager?.playCorrect()
                                        hapticUtil?.vibrateSuccess()
                                        ttsManager?.speak(caughtCount.toString())

                                        if (caughtCount >= targetCountGoal) {
                                            isMissionWon = true
                                            soundManager?.playVictory()
                                            hapticUtil?.vibrateReward()
                                            if (!coinsAwarded) {
                                                coinsAwarded = true
                                                CoinShopManager.addCoin(context, 3)
                                            }
                                        }
                                    } else {
                                        // Juguete equivocado
                                        soundManager?.playIncorrect()
                                        hapticUtil?.vibrateEncourage()
                                        val hint = if (isEnglish) "That's ${bubble.toy.name}. Tap ${targetToy.name}!"
                                        else "Ese es ${bubble.toy.name}. ¡Busca ${targetToy.name}!"
                                        ttsManager?.speak(hint)
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = bubble.toy.emoji, fontSize = 34.sp)
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

        if (isMissionWon) {
            ConfettiCelebration(visible = true)
        }
    }
}
