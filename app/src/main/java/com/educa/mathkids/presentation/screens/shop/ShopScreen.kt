package com.educa.mathkids.presentation.screens.shop

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.educa.mathkids.presentation.ads.NativeAdCard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.educa.mathkids.core.shop.CoinShopManager
import com.educa.mathkids.domain.model.shop.ToyCategory
import com.educa.mathkids.domain.model.shop.ToyItem
import com.educa.mathkids.presentation.components.ConfettiCelebration
import com.educa.mathkids.presentation.components.DynamicKidsBackground
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
 * Pantalla oficial de la Tienda de Regalos y Juguetes de MateKids.
 * Muestra 20 juguetes infantiles (para niños y niñas) con valores de 50 a 300 monedas,
 * permitiendo canjearlos con las monedas acumuladas al resolver ejercicios.
 */
@Composable
fun ShopScreen(
    soundManager: SoundManager? = null,
    ttsManager: TtsManager? = null,
    hapticUtil: HapticUtil? = null,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val accumulatedCoins by CoinShopManager.coinsState.collectAsState()
    val purchasedToyIds by CoinShopManager.purchasedToysState.collectAsState()

    var selectedCategory by remember { mutableStateOf<ToyCategory>(ToyCategory.ALL) }
    var toyToRedeem by remember { mutableStateOf<ToyItem?>(null) }
    var showCelebration by remember { mutableStateOf(false) }

    val filteredToys = remember(selectedCategory, purchasedToyIds) {
        val all = CoinShopManager.TOYS_CATALOG
        val filtered = when (selectedCategory) {
            ToyCategory.ALL -> all
            ToyCategory.BOYS -> all.filter { it.category == ToyCategory.BOYS || it.category == ToyCategory.ALL }
            ToyCategory.GIRLS -> all.filter { it.category == ToyCategory.GIRLS || it.category == ToyCategory.ALL }
        }
        filtered.map { toy ->
            toy.copy(isPurchased = purchasedToyIds.contains(toy.id))
        }
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

                // 1. Barra Superior con Botón Atrás, Título y Saldo de Monedas
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
                            contentDescription = "Inicio",
                            tint = SlateDark
                        )
                    }

                    // Título Central
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "🏪", fontSize = 24.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "TIENDA",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FredokaFont,
                            color = SlateDark
                        )
                    }

                    // Chip de Saldo en la cabecera
                    Box(
                        modifier = Modifier
                            .shadow(3.dp, RoundedCornerShape(20.dp), spotColor = Color(0xFFFFB300).copy(alpha = 0.35f))
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0xFFFFFDE7))
                            .border(1.5.dp, Color(0xFFFFD54F), RoundedCornerShape(20.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "🪙", fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "$accumulatedCoins",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold,
                                fontFamily = FredokaFont,
                                color = Color(0xFFE65100)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 2. Tarjeta de Cofre de Monedas Acumuladas
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(6.dp, RoundedCornerShape(22.dp), spotColor = Color(0xFFFFB300).copy(alpha = 0.25f))
                        .clip(RoundedCornerShape(22.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFFFFF8E1), Color(0xFFFFECB3), Color(0xFFFFE082))
                            )
                        )
                        .border(2.dp, Color(0xFFFFCA28), RoundedCornerShape(22.dp))
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                                .border(2.dp, Color(0xFFFFB300), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "🪙", fontSize = 28.sp)
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "$accumulatedCoins Monedas Guardadas",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FredokaFont,
                                color = Color(0xFFBF360C)
                            )
                            Text(
                                text = "¡Ganas 1 moneda por cada ejercicio que aciertes!",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FredokaFont,
                                color = Color(0xFFE65100).copy(alpha = 0.85f)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // 3. Filtros de Categorías (TODOS, NIÑOS, NIÑAS)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(
                        ToyCategory.ALL to "🌟 TODOS (${CoinShopManager.TOYS_CATALOG.size})",
                        ToyCategory.BOYS to "🚀 NIÑOS",
                        ToyCategory.GIRLS to "🎀 NIÑAS"
                    ).forEach { (category, label) ->
                        val isSelected = selectedCategory == category
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .shadow(if (isSelected) 3.dp else 1.dp, RoundedCornerShape(14.dp))
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (isSelected) OrangeSecondary else SurfaceWhite)
                                .border(
                                    1.5.dp,
                                    if (isSelected) OrangeSecondary else SlateBorder,
                                    RoundedCornerShape(14.dp)
                                )
                                .clickable {
                                    soundManager?.playButtonClick()
                                    hapticUtil?.vibrateClick()
                                    selectedCategory = category
                                }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FredokaFont,
                                color = if (isSelected) Color.White else SlateDark
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // 4. Cuadrícula de 20 Juguetes con Precios de 50 a 300 Monedas
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredToys.take(4), key = { it.id }) { toy ->
                        ToyShopCard(
                            toy = toy,
                            accumulatedCoins = accumulatedCoins,
                            onRedeemClick = {
                                soundManager?.playButtonClick()
                                hapticUtil?.vibrateClick()
                                toyToRedeem = toy
                            }
                        )
                    }

                    item(span = { GridItemSpan(2) }) {
                        NativeAdCard(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp))
                    }

                    items(filteredToys.drop(4), key = { it.id }) { toy ->
                        ToyShopCard(
                            toy = toy,
                            accumulatedCoins = accumulatedCoins,
                            onRedeemClick = {
                                soundManager?.playButtonClick()
                                hapticUtil?.vibrateClick()
                                toyToRedeem = toy
                            }
                        )
                    }
                }
            }

            // Diálogo de Confirmación de Canje
            toyToRedeem?.let { toy ->
                RedeemToyDialog(
                    toy = toy,
                    currentCoins = accumulatedCoins,
                    onDismiss = { toyToRedeem = null },
                    onConfirmRedeem = {
                        val success = CoinShopManager.purchaseToy(context, toy.id, toy.price)
                        toyToRedeem = null
                        if (success) {
                            soundManager?.playVictory()
                            hapticUtil?.vibrateReward()
                            showCelebration = true
                            ttsManager?.speak("¡Felicidades! Has canjeado tu ${toy.name}")
                        } else {
                            soundManager?.playIncorrect()
                            hapticUtil?.vibrateEncourage()
                        }
                    }
                )
            }

            // Confeti de Celebración al Canjear
            if (showCelebration) {
                ConfettiCelebration(
                    visible = true,
                    onFinished = { showCelebration = false }
                )
            }
        }
    }
}

@Composable
private fun ToyShopCard(
    toy: ToyItem,
    accumulatedCoins: Int,
    onRedeemClick: () -> Unit
) {
    val canAfford = accumulatedCoins >= toy.price

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(20.dp), spotColor = Color.Black.copy(alpha = 0.10f))
            .clip(RoundedCornerShape(20.dp))
            .background(SurfaceWhite)
            .border(
                1.5.dp,
                if (toy.isPurchased) GreenPrimary else if (canAfford) Color(0xFFFFB300) else SlateBorder,
                RoundedCornerShape(20.dp)
            )
            .padding(10.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Insignia de categoría
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
                            ToyCategory.BOYS -> "🚀 Niño"
                            ToyCategory.GIRLS -> "🎀 Niña"
                            ToyCategory.ALL -> "⭐ Todos"
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

                // Insignia de precio
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFFFF8E1))
                        .border(1.dp, Color(0xFFFFD54F), RoundedCornerShape(10.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "🪙 ${toy.price}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FredokaFont,
                        color = Color(0xFFE65100)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Ilustración/Emoji del Juguete
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF8FAFC))
                    .border(1.5.dp, Color(0xFFE2E8F0), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(text = toy.emoji, fontSize = 36.sp)
            }

            Spacer(modifier = Modifier.height(6.dp))

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

            Spacer(modifier = Modifier.height(8.dp))

            // Botón de Acción según estado: Canjeado, Disponible o Monedas Insuficientes
            if (toy.isPurchased) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(34.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(GreenPrimary),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🎉 ¡OBTENIDO!",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FredokaFont,
                        color = Color.White
                    )
                }
            } else if (canAfford) {
                Button(
                    onClick = onRedeemClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(34.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800)),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        text = "🎁 CANJEAR",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FredokaFont,
                        color = Color.White
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(34.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF1F5F9))
                        .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    val missing = toy.price - accumulatedCoins
                    Text(
                        text = "🔒 Falta $missing 🪙",
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FredokaFont,
                        color = SlateMuted
                    )
                }
            }
        }
    }
}

@Composable
private fun RedeemToyDialog(
    toy: ToyItem,
    currentCoins: Int,
    onDismiss: () -> Unit,
    onConfirmRedeem: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(24.dp, RoundedCornerShape(28.dp))
                .clip(RoundedCornerShape(28.dp))
                .background(Color.White)
                .border(3.dp, Color(0xFFFFB300), RoundedCornerShape(28.dp))
                .padding(22.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "🎁", fontSize = 48.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "¿Canjear Juguete?",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FredokaFont,
                    color = SlateDark
                )

                Spacer(modifier = Modifier.height(10.dp))

                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFFF8E1))
                        .border(2.dp, Color(0xFFFFD54F), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = toy.emoji, fontSize = 48.sp)
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = toy.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FredokaFont,
                    color = SlateDark,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = toy.description,
                    fontSize = 12.sp,
                    color = SlateMuted,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(14.dp))

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFFFFFDE7))
                        .border(1.5.dp, Color(0xFFFFD54F), RoundedCornerShape(14.dp))
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "Precio: ", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = SlateDark)
                        Text(
                            text = "🪙 ${toy.price} Monedas",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFFE65100)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE2E8F0)),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                    ) {
                        Text(
                            text = "Cancelar",
                            color = SlateDark,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Button(
                        onClick = onConfirmRedeem,
                        colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .weight(1.3f)
                            .height(48.dp)
                    ) {
                        Text(
                            text = "¡SÍ, CANJEAR! 🎁",
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            fontFamily = FredokaFont,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }
}
