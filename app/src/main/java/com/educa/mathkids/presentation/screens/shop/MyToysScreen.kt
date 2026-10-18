package com.educa.mathkids.presentation.screens.shop

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.window.Dialog
import com.educa.mathkids.core.shop.CoinShopManager
import com.educa.mathkids.domain.model.shop.ToyCategory
import com.educa.mathkids.domain.model.shop.ToyItem
import com.educa.mathkids.presentation.ads.NativeAdCard
import com.educa.mathkids.presentation.components.ConfettiCelebration
import com.educa.mathkids.presentation.components.DynamicKidsBackground
import com.educa.mathkids.presentation.components.MascotMood
import com.educa.mathkids.presentation.components.MetaCompanion
import com.educa.mathkids.presentation.localization.AppLanguage
import com.educa.mathkids.presentation.localization.LocalAppLanguage
import com.educa.mathkids.presentation.localization.appStrings
import com.educa.mathkids.presentation.theme.BluePrimary
import com.educa.mathkids.presentation.theme.FredokaFont
import com.educa.mathkids.presentation.theme.GreenPrimary
import com.educa.mathkids.presentation.theme.OrangeSecondary
import com.educa.mathkids.presentation.theme.SlateBorder
import com.educa.mathkids.presentation.theme.SlateDark
import com.educa.mathkids.presentation.theme.SlateMuted
import com.educa.mathkids.presentation.theme.SurfaceWhite
import com.educa.mathkids.presentation.utils.HapticUtil
import com.educa.mathkids.presentation.utils.SoundManager
import com.educa.mathkids.presentation.utils.TtsManager

/**
 * Pantalla "Mis Juguetes": Muestra exclusivamente los juguetes canjeados por el niño
 * en la Tienda de Juguetes usando sus monedas acumuladas.
 */
@Composable
fun MyToysScreen(
    soundManager: SoundManager? = null,
    ttsManager: TtsManager? = null,
    hapticUtil: HapticUtil? = null,
    onBackClick: () -> Unit,
    onNavigateToShop: () -> Unit,
    onNavigateToGames: (gameType: String, toyId: String) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val strings = appStrings()
    val isEnglish = LocalAppLanguage.current == AppLanguage.INGLES

    val accumulatedCoins by CoinShopManager.coinsState.collectAsState()
    val purchasedToyIds by CoinShopManager.purchasedToysState.collectAsState()

    val myToys = remember(purchasedToyIds) {
        CoinShopManager.TOYS_CATALOG.filter { purchasedToyIds.contains(it.id) }
    }
    val totalToys = CoinShopManager.TOYS_CATALOG.size

    var showCelebration by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        CoinShopManager.init(context)
        val welcome = if (isEnglish) {
            if (myToys.isEmpty()) "My Toys! You haven't redeemed any toys yet. Visit the shop to get your first toy!"
            else "Welcome to your toy box! You have ${myToys.size} toys. Tap any toy to play with it!"
        } else {
            if (myToys.isEmpty()) "¡Mis Juguetes! Aún no has canjeado ningún juguete. ¡Visita la tienda para conseguir el primero!"
            else "¡Bienvenidos a tu baúl de juguetes! Tienes ${myToys.size} juguetes canjeados. ¡Toca cualquiera para jugar!"
        }
        ttsManager?.speak(welcome)
    }

    DynamicKidsBackground(modifier = modifier) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .navigationBarsPadding()
            ) {
                Spacer(modifier = Modifier.height(12.dp))

                // 1. Barra Superior (Atrás, Título y Saldo)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        onClick = {
                            soundManager?.playButtonClick()
                            hapticUtil?.vibrateClick()
                            onBackClick()
                        },
                        modifier = Modifier
                            .size(46.dp)
                            .shadow(3.dp, CircleShape)
                            .clip(CircleShape)
                            .background(SurfaceWhite)
                            .border(1.5.dp, SlateBorder, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Home,
                            contentDescription = strings.navHome,
                            tint = SlateDark
                        )
                    }

                    // Título Central
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "🧸", fontSize = 24.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = strings.btnMyToys,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FredokaFont,
                            color = SlateDark
                        )
                    }

                    // Controles de Derecha: Botón de Bocina para cancelar la voz + Chip de Saldo
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        var isVoiceMuted by remember { mutableStateOf(ttsManager?.isVoiceEnabled != true) }
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .shadow(3.dp, CircleShape)
                                .clip(CircleShape)
                                .background(if (isVoiceMuted) Color(0xFFFFEBEE) else SurfaceWhite)
                                .border(1.5.dp, if (isVoiceMuted) Color(0xFFE57373) else SlateBorder, CircleShape)
                                .clickable {
                                    soundManager?.playButtonClick()
                                    ttsManager?.stop()
                                    isVoiceMuted = !isVoiceMuted
                                    ttsManager?.isVoiceEnabled = !isVoiceMuted
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (isVoiceMuted) "🔇" else "🔊",
                                fontSize = 17.sp
                            )
                        }

                        // Chip de Saldo en la cabecera
                        Box(
                            modifier = Modifier
                                .shadow(3.dp, RoundedCornerShape(20.dp), spotColor = Color(0xFFFFB300).copy(alpha = 0.35f))
                                .clip(RoundedCornerShape(20.dp))
                                .background(Color(0xFFFFF8E1))
                                .border(1.5.dp, Color(0xFFFFB300), RoundedCornerShape(20.dp))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "🪙", fontSize = 16.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "$accumulatedCoins",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Black,
                                    fontFamily = FredokaFont,
                                    color = Color(0xFFE65100)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // 2. Grilla con Mascot, Progreso y Juguetes Canjeados
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(bottom = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Mascota y bocadillo
                    item(span = { GridItemSpan(2) }) {
                        MetaCompanion(
                            message = if (myToys.isEmpty()) {
                                strings.noToysYetPrompt
                            } else {
                                if (isEnglish) "You have ${myToys.size} toys in your box! Tap on any toy to play!"
                                else "¡Tienes ${myToys.size} juguetes canjeados! ¡Toca cualquiera para jugar con él!"
                            },
                            mood = if (myToys.isEmpty()) MascotMood.THINKING else MascotMood.HAPPY,
                            mascotSize = 78.dp,
                            ttsManager = ttsManager,
                            onMascotClick = {
                                ttsManager?.speak(
                                    if (isEnglish) "Keep solving exercises to win more toys in the shop!"
                                    else "¡Sigue resolviendo ejercicios para canjear más juguetes en la tienda!"
                                )
                            }
                        )
                    }

                    // Tarjeta de Colección y Progreso
                    item(span = { GridItemSpan(2) }) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .shadow(5.dp, RoundedCornerShape(18.dp), spotColor = Color(0xFFE91E63).copy(alpha = 0.20f))
                                .clip(RoundedCornerShape(18.dp))
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(Color(0xFFFCE4EC), Color(0xFFF8BBD0), Color(0xFFF48FB1).copy(alpha = 0.4f))
                                    )
                                )
                                .border(1.5.dp, Color(0xFFE91E63).copy(alpha = 0.45f), RoundedCornerShape(18.dp))
                                .padding(14.dp)
                        ) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(text = "🎁", fontSize = 18.sp)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = if (isEnglish) "Toy Collection" else "Colección de Juguetes",
                                            fontSize = 14.5.sp,
                                            fontWeight = FontWeight.Black,
                                            fontFamily = FredokaFont,
                                            color = Color(0xFF880E4F)
                                        )
                                    }

                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(Color(0xFFE91E63))
                                            .padding(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "${myToys.size} / $totalToys",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Black,
                                            fontFamily = FredokaFont,
                                            color = Color.White
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                val progress = (myToys.size.toFloat() / totalToys.toFloat()).coerceIn(0f, 1f)
                                LinearProgressIndicator(
                                    progress = { progress },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(10.dp)
                                        .clip(RoundedCornerShape(6.dp)),
                                    color = Color(0xFFE91E63),
                                    trackColor = Color.White.copy(alpha = 0.7f),
                                    strokeCap = StrokeCap.Round
                                )
                            }
                        }
                    }



                    // Si no hay juguetes canjeados: Estado Vacío amigable
                    if (myToys.isEmpty()) {
                        item(span = { GridItemSpan(2) }) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp)
                                    .shadow(4.dp, RoundedCornerShape(22.dp), spotColor = Color.Black.copy(alpha = 0.08f))
                                    .clip(RoundedCornerShape(22.dp))
                                    .background(SurfaceWhite)
                                    .border(1.5.dp, SlateBorder, RoundedCornerShape(22.dp))
                                    .padding(24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(text = "📦", fontSize = 64.sp)
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = if (isEnglish) "Your Toy Box is Empty!" else "¡Tu Baúl de Juguetes está Vacío!",
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Black,
                                        fontFamily = FredokaFont,
                                        color = SlateDark,
                                        textAlign = TextAlign.Center
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = if (isEnglish) {
                                            "Earn coins by solving math exercises and redeem fun toys in the Toy Shop!"
                                        } else {
                                            "¡Gana monedas resolviendo ejercicios matemáticos y canjea increíbles juguetes en la Tienda!"
                                        },
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                        fontFamily = FredokaFont,
                                        color = SlateMuted,
                                        textAlign = TextAlign.Center
                                    )

                                    Spacer(modifier = Modifier.height(18.dp))

                                    Button(
                                        onClick = {
                                            soundManager?.playButtonClick()
                                            hapticUtil?.vibrateClick()
                                            onNavigateToShop()
                                        },
                                        shape = RoundedCornerShape(16.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(0xFFFF9800)
                                        ),
                                        modifier = Modifier
                                            .fillMaxWidth(0.9f)
                                            .height(48.dp)
                                    ) {
                                        Text(
                                            text = if (isEnglish) "🏪 GO TO TOY SHOP" else "🏪 IR A LA TIENDA DE JUGUETES",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Black,
                                            fontFamily = FredokaFont,
                                            color = Color.White
                                        )
                                    }
                                }
                            }
                        }
                    } else {
                        // Lista de juguetes canjeados (Únicamente los canjeados)
                        items(myToys, key = { it.id }) { toy ->
                            RedeemedToyCard(
                                toy = toy,
                                isEnglish = isEnglish,
                                onPlayClick = {
                                    val gameMode = com.educa.mathkids.presentation.screens.shop.games.getToyGameType(toy.id)
                                    soundManager?.playButtonClick()
                                    hapticUtil?.vibrateClick()
                                    ttsManager?.speak(
                                        if (isEnglish) "Let's play with ${toy.name}!"
                                        else "¡A jugar con tu ${toy.name}!"
                                    )
                                    onNavigateToGames(gameMode.name, toy.id)
                                }
                            )
                        }

                        // Anuncio Nativo integrado en el baúl de juguetes
                        item(span = { GridItemSpan(2) }) {
                            NativeAdCard(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp))
                        }

                        // Botón para ir a la tienda a canjear más juguetes
                        item(span = { GridItemSpan(2) }) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp)
                                    .shadow(4.dp, RoundedCornerShape(18.dp), spotColor = Color(0xFFFF9800).copy(alpha = 0.25f))
                                    .clip(RoundedCornerShape(18.dp))
                                    .background(Color(0xFFFFF8E1))
                                    .border(1.5.dp, Color(0xFFFFB300), RoundedCornerShape(18.dp))
                                    .clickable {
                                        soundManager?.playButtonClick()
                                        hapticUtil?.vibrateClick()
                                        onNavigateToShop()
                                    }
                                    .padding(14.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Text(text = "🏪", fontSize = 22.sp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = if (isEnglish) "VISIT SHOP TO GET MORE TOYS ▶" else "IR A LA TIENDA POR MÁS JUGUETES ▶",
                                        fontSize = 13.5.sp,
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

            // Confeti de celebración
            if (showCelebration) {
                ConfettiCelebration(
                    visible = true,
                    onFinished = { showCelebration = false }
                )
            }
        }
    }
}

/**
 * Tarjeta individual para un juguete canjeado en "Mis Juguetes"
 */
@Composable
private fun RedeemedToyCard(
    toy: ToyItem,
    isEnglish: Boolean,
    onPlayClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(20.dp), spotColor = Color.Black.copy(alpha = 0.10f))
            .clip(RoundedCornerShape(20.dp))
            .background(SurfaceWhite)
            .border(2.dp, GreenPrimary.copy(alpha = 0.7f), RoundedCornerShape(20.dp))
            .clickable { onPlayClick() }
            .padding(10.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Insignia superior
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            when (toy.category) {
                                ToyCategory.BOYS -> Color(0xFFE3F2FD)
                                ToyCategory.GIRLS -> Color(0xFFFCE4EC)
                                ToyCategory.ALL -> Color(0xFFE8F5E9)
                            }
                        )
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = when (toy.category) {
                            ToyCategory.BOYS -> if (isEnglish) "🚀 Boys" else "🚀 Niño"
                            ToyCategory.GIRLS -> if (isEnglish) "🎀 Girls" else "🎀 Niña"
                            ToyCategory.ALL -> if (isEnglish) "⭐ All" else "⭐ Todos"
                        },
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = when (toy.category) {
                            ToyCategory.BOYS -> Color(0xFF1976D2)
                            ToyCategory.GIRLS -> Color(0xFFC2185B)
                            ToyCategory.ALL -> Color(0xFF388E3C)
                        }
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFE8F5E9))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "✨ Canjeado",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black,
                        color = GreenPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Emoji grande del juguete en círculo alegre
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(Color(0xFFE8F5E9), Color(0xFFC8E6C9))
                        )
                    )
                    .border(2.dp, GreenPrimary.copy(alpha = 0.5f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(text = toy.emoji, fontSize = 40.sp)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Nombre del juguete
            Text(
                text = toy.name,
                fontSize = 12.5.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FredokaFont,
                color = SlateDark,
                textAlign = TextAlign.Center,
                maxLines = 2,
                lineHeight = 15.sp,
                modifier = Modifier.height(32.dp)
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Costo original de canje
            Text(
                text = "🪙 ${toy.price} monedas",
                fontSize = 10.5.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FredokaFont,
                color = SlateMuted
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Botón interactivo de jugar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(34.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF4CAF50)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isEnglish) "🎮 PLAY!" else "🎮 ¡JUGAR!",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FredokaFont,
                    color = Color.White
                )
            }
        }
    }
}


