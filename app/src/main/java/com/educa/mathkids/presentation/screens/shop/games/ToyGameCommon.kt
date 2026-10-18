package com.educa.mathkids.presentation.screens.shop.games

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.educa.mathkids.core.shop.CoinShopManager
import com.educa.mathkids.presentation.utils.SoundManager
import com.educa.mathkids.presentation.utils.TtsManager
import com.educa.mathkids.domain.model.shop.ToyItem
import com.educa.mathkids.presentation.theme.BluePrimary
import com.educa.mathkids.presentation.theme.FredokaFont
import com.educa.mathkids.presentation.theme.GreenPrimary
import com.educa.mathkids.presentation.theme.OrangeSecondary
import com.educa.mathkids.presentation.theme.SlateBorder
import com.educa.mathkids.presentation.theme.SlateDark
import com.educa.mathkids.presentation.theme.SlateMuted
import com.educa.mathkids.presentation.theme.SoftRed
import com.educa.mathkids.presentation.theme.SurfaceWhite
import android.app.Activity
import androidx.compose.ui.platform.LocalContext
import com.educa.mathkids.presentation.ads.AdManager
import kotlin.random.Random

enum class ToyGameType(
    val toyId: String,
    val emoji: String,
    val titleEs: String,
    val titleEn: String,
    val descriptionEs: String
) {
    PELOTA_JUGGLE("toy_pelota", "⚽", "Rebote y Toques 2D", "Ball Bounce & Juggle", "¡Haz malabares con la pelota en el aire y atrapa números!"),
    CRAYONES_COLOR("toy_crayones", "🖍️", "Pinta con Crayones", "Crayons Color Match", "¡Arrastra los crayones mágicos hacia el número correcto para colorear!"),
    YOYO_TARGET("toy_yoyo", "🪀", "Tiro al Blanco con Yoyo", "Yoyo Precision Target", "¡Lanza el yoyo luminoso con precisión hacia la diana correcta!"),
    MUNECA_DRESSUP("toy_muneca_trapo", "🪆", "Memoria de la Muñeca", "Doll Memory Match", "¡Encuentra las parejas de cartas que coincidan con la muñeca!"),
    CARRITO_RUNNER("toy_carrito", "🏎️", "Pista Turbo de Carreras", "Math Turbo Dash", "¡Esquiva conos y corre a toda velocidad con el bólido veloz!"),
    BURBUJAS_POP("toy_burbujas", "🫧", "Pompas Flotantes 2D", "Bubble Popper 2D", "¡Revienta las burbujas flotantes que tengan la solución correcta!"),
    COMETA_GLIDER("toy_cometa", "🪁", "Vuelo de Cometa", "Kite Wind Glider", "¡Vuela la cometa sorteando nubes de viento y atrapando estrellas!"),
    DINO_CLIMB("toy_dino", "🦖", "Escalada Jurásica 2D", "Dino Jurassic Climb", "¡Salta en orden numérico progresivo 1, 2, 3... con el gran T-Rex!"),
    UNICORNIO_RAINBOW("toy_unicornio", "🦄", "Galope en el Arcoíris", "Rainbow Unicorn Dash", "¡Galopa sobre el arcoíris brillante recogiendo gemas mágicas!"),
    ROMPECABEZAS_SNAP("toy_rompecabezas", "🧩", "Gran Rompecabezas", "Puzzle Solver", "¡Resuelve el rompecabezas encajando cada pieza en su lugar correcto!"),
    TREN_CONDUCTOR("toy_tren", "🚂", "Tren Culebrita", "Snake Train Express", "¡Guía el tren como la culebrita comiendo los números correctos para crecer sin chocar!"),
    CASTILLO_DEFENSE("toy_castillo", "🏰", "Construcción del Castillo", "Castle Puzzle Builder", "¡Arrastra los bloques matemáticos para construir un gran castillo!"),
    ROBOT_MATRIX("toy_robot", "🤖", "Laboratorio del Robot", "Robot Power Matrix", "¡Conecta las celdas de energía para que el robot espacial baile!"),
    PINTURA_SPLASH("toy_pintura", "🎨", "Salpicadura de Acuarelas", "Paint Splatter Studio", "¡Salpica acuarelas brillantes sobre el lienzo de los pequeños artistas!"),
    TAMBOR_RHYTHM("toy_tambor", "🥁", "Ritmo de Tambores", "Drum Beats Master", "¡Sigue el compás y los golpes rítmicos en el tambor alegre!"),
    COHETE_SPACE("toy_cohete", "🚀", "Misión Espacial Lunar", "Space Rocket Flight", "¡Despega esquivando asteroides y aterriza en el planeta lunar!"),
    OSO_CATCHER("toy_oso", "🧸", "Atrapa Miel del Osito", "Teddy Honey Catcher", "¡Mueve la canasta del oso abrazable para atrapar rica miel!"),
    PATINETA_SKATE("toy_patineta", "🛹", "Skate Park en Subidas y Bajadas", "Skate Hill Runner", "¡Patineta en línea recta por subidas y bajadas, salta obstáculos y atrapa los números correctos!"),
    GUITARRA_SOLO("toy_guitarra", "🎸", "Concierto Guitar Star", "Rock Guitar Strings", "¡Toca las cuerdas al compás de las notas musicales de rock!"),
    DRON_DELIVERY("toy_dron", "🚁", "Piloto de Dron y Rescate", "Drone Flight Navigator", "¡Pilota el dron con hélices turbo para entregar paquetes de ayuda!");

    companion object {
        val RUNNER get() = CARRITO_RUNNER
        val BOUNCE get() = PELOTA_JUGGLE
        val ADVENTURE get() = DINO_CLIMB
        val RHYTHM get() = TAMBOR_RHYTHM
    }
}

fun getToyGameType(toyId: String): ToyGameType {
    return when (toyId) {
        "toy_pelota" -> ToyGameType.PELOTA_JUGGLE
        "toy_crayones" -> ToyGameType.CRAYONES_COLOR
        "toy_yoyo" -> ToyGameType.YOYO_TARGET
        "toy_muneca_trapo" -> ToyGameType.MUNECA_DRESSUP
        "toy_carrito" -> ToyGameType.CARRITO_RUNNER
        "toy_burbujas" -> ToyGameType.BURBUJAS_POP
        "toy_cometa" -> ToyGameType.COMETA_GLIDER
        "toy_dino" -> ToyGameType.DINO_CLIMB
        "toy_unicornio" -> ToyGameType.UNICORNIO_RAINBOW
        "toy_rompecabezas" -> ToyGameType.ROMPECABEZAS_SNAP
        "toy_tren" -> ToyGameType.TREN_CONDUCTOR
        "toy_castillo" -> ToyGameType.CASTILLO_DEFENSE
        "toy_robot" -> ToyGameType.ROBOT_MATRIX
        "toy_pintura" -> ToyGameType.PINTURA_SPLASH
        "toy_tambor" -> ToyGameType.TAMBOR_RHYTHM
        "toy_cohete" -> ToyGameType.COHETE_SPACE
        "toy_oso" -> ToyGameType.OSO_CATCHER
        "toy_patineta" -> ToyGameType.PATINETA_SKATE
        "toy_guitarra" -> ToyGameType.GUITARRA_SOLO
        "toy_dron" -> ToyGameType.DRON_DELIVERY
        else -> ToyGameType.CARRITO_RUNNER
    }
}

/**
 * Visualizador de 3 Vidas para todos los minijuegos infantiles.
 */
@Composable
fun ToyLivesBar(
    lives: Int,
    maxLives: Int = 3,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .shadow(3.dp, RoundedCornerShape(14.dp), spotColor = SoftRed.copy(alpha = 0.25f))
            .clip(RoundedCornerShape(14.dp))
            .background(SurfaceWhite)
            .border(1.5.dp, SoftRed.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(maxLives) { index ->
                val hasLife = index < lives
                val scale by animateFloatAsState(
                    targetValue = if (hasLife) 1.0f else 0.85f,
                    animationSpec = spring(),
                    label = "life_scale_$index"
                )
                Text(
                    text = if (hasLife) "❤️" else "🖤",
                    fontSize = 18.sp,
                    modifier = Modifier.graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                    }
                )
            }
        }
    }
}

/**
 * Cabecera unificada de juego con vidas, monedas y salida.
 */
@Composable
fun ToyGameTopBar(
    title: String,
    toyEmoji: String,
    lives: Int,
    coins: Int,
    onExitClick: () -> Unit,
    onPauseClick: (() -> Unit)? = null,
    correctHits: Int? = null,
    targetHits: Int = 30,
    difficulty: ToyGameDifficulty? = null,
    isEnglish: Boolean = false,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Controles: Salir y Pausa
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Botón Salir
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .shadow(3.dp, CircleShape)
                        .clip(CircleShape)
                        .background(SurfaceWhite)
                        .border(1.5.dp, Color(0xFFE2E8F0), CircleShape)
                        .clickable { onExitClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "✖", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = SlateDark)
                }

                // Botón Pausa
                if (onPauseClick != null) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .shadow(3.dp, CircleShape)
                            .clip(CircleShape)
                            .background(SurfaceWhite)
                            .border(1.5.dp, Color(0xFFE2E8F0), CircleShape)
                            .clickable { onPauseClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "⏸️", fontSize = 16.sp)
                    }
                }
            }

            // Título + Juguete
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(text = toyEmoji, fontSize = 22.sp)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = title,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FredokaFont,
                    color = SlateDark
                )
            }

            // Vidas y Monedas
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ToyLivesBar(lives = lives, maxLives = 3)

                Box(
                    modifier = Modifier
                        .shadow(3.dp, RoundedCornerShape(14.dp))
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFFFFF9C4))
                        .border(1.5.dp, Color(0xFFFFD54F), RoundedCornerShape(14.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "🪙", fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "$coins",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FredokaFont,
                            color = Color(0xFFF57F17)
                        )
                    }
                }
            }
        }

        // Sub-barra informativa: Dificultad actual + Contador de Aciertos hacia la victoria (30)
        if (difficulty != null || correctHits != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (difficulty != null) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(difficulty.color.copy(alpha = 0.12f))
                            .border(1.2.dp, difficulty.color.copy(alpha = 0.6f), RoundedCornerShape(10.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "${difficulty.badge} ${if (isEnglish) difficulty.titleEn else difficulty.titleEs}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FredokaFont,
                            color = difficulty.color
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.width(1.dp))
                }

                if (correctHits != null) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFFE8F5E9))
                            .border(1.2.dp, Color(0xFF4CAF50), RoundedCornerShape(10.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "🎯 ${if (isEnglish) "Hits" else "Aciertos"}: $correctHits/$targetHits",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FredokaFont,
                            color = Color(0xFF2E7D32)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Diálogo de Fin de Partida (Game Over) cuando se pierden las 3 vidas.
 */
@Composable
fun ToyGameOverDialog(
    toyEmoji: String,
    toyName: String,
    score: Int,
    coinsEarned: Int,
    isEnglish: Boolean,
    onRetry: () -> Unit,
    onExit: () -> Unit
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val adManager = remember { AdManager.getInstance(context) }


    Dialog(onDismissRequest = onExit) {
        Card(
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .shadow(12.dp, RoundedCornerShape(26.dp))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Emoji con efecto suave
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFFEBEE))
                        .border(2.5.dp, SoftRed, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = toyEmoji, fontSize = 44.sp)
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = if (isEnglish) "Out of Lives!" else "¡Te quedaste sin vidas!",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FredokaFont,
                    color = SoftRed,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = if (isEnglish)
                        "You can continue from where you left off! Watch an ad to recover 3 lives."
                    else
                        "¡Puedes continuar desde donde perdiste! Mira un anuncio para recuperar 3 vidas.",
                    fontSize = 14.sp,
                    fontFamily = FredokaFont,
                    color = SlateDark,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Resumen
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFF8FAFC))
                        .padding(vertical = 10.dp, horizontal = 12.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (isEnglish) "🎯 Points" else "🎯 Puntos",
                            fontSize = 12.sp,
                            fontFamily = FredokaFont,
                            color = SlateMuted,
                            maxLines = 1,
                            softWrap = false
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "$score",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FredokaFont,
                            color = SlateDark,
                            maxLines = 1,
                            softWrap = false
                        )
                    }
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (isEnglish) "🪙 Coins" else "🪙 Monedas",
                            fontSize = 12.sp,
                            fontFamily = FredokaFont,
                            color = SlateMuted,
                            maxLines = 1,
                            softWrap = false
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "+$coinsEarned",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FredokaFont,
                            color = Color(0xFFF57F17),
                            maxLines = 1,
                            softWrap = false
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Botón Continuar con 3 Vidas desde donde perdió
                Button(
                    onClick = {
                        if (activity != null) {
                            adManager.showInterstitial(activity, onRetry)
                        } else {
                            onRetry()
                        }
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text(
                        text = if (isEnglish) "▶️ CONTINUE (+3 ❤️)" else "▶️ CONTINUAR (+3 ❤️)",
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FredokaFont,
                        color = Color.White,
                        maxLines = 1,
                        softWrap = false
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Botón Salir
                Button(
                    onClick = {
                        if (activity != null) {
                            adManager.showInterstitial(activity, onExit)
                        } else {
                            onExit()
                        }
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE2E8F0)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                ) {
                    Text(
                        text = if (isEnglish) "🚪 Back to Toys" else "🚪 Salir al Baúl",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FredokaFont,
                        color = SlateDark
                    )
                }
            }
        }
    }
}

/**
 * Diálogo de Pausa amigable para niños con opciones de Reanudar, Reiniciar o Salir.
 */
@Composable
fun ToyGamePauseDialog(
    toyEmoji: String,
    toyName: String,
    score: Int,
    coins: Int,
    isEnglish: Boolean,
    onResume: () -> Unit,
    onRestart: () -> Unit,
    onExit: () -> Unit
) {
    Dialog(onDismissRequest = onResume) {
        Card(
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .shadow(12.dp, RoundedCornerShape(26.dp))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(76.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE1F5FE))
                        .border(2.5.dp, BluePrimary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "⏸️", fontSize = 38.sp)
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = if (isEnglish) "Game Paused" else "¡Juego en Pausa!",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FredokaFont,
                    color = BluePrimary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = if (isEnglish)
                        "Take your time! $toyName is waiting for you."
                    else
                        "¡Tómate un respiro! $toyName te espera para continuar.",
                    fontSize = 13.5.sp,
                    fontFamily = FredokaFont,
                    color = SlateDark,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Marcador actual
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFF8FAFC))
                        .padding(vertical = 8.dp, horizontal = 12.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (isEnglish) "🎯 Points" else "🎯 Puntos",
                            fontSize = 12.sp,
                            fontFamily = FredokaFont,
                            color = SlateMuted,
                            maxLines = 1,
                            softWrap = false
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "$score",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FredokaFont,
                            color = SlateDark,
                            maxLines = 1,
                            softWrap = false
                        )
                    }
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (isEnglish) "🪙 Coins" else "🪙 Monedas",
                            fontSize = 12.sp,
                            fontFamily = FredokaFont,
                            color = SlateMuted,
                            maxLines = 1,
                            softWrap = false
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "+$coins",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FredokaFont,
                            color = Color(0xFFF57F17),
                            maxLines = 1,
                            softWrap = false
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Botón Reanudar
                Button(
                    onClick = onResume,
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text(
                        text = if (isEnglish) "▶️ RESUME" else "▶️ REANUDAR PARTIDA",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FredokaFont,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Botón Reiniciar
                Button(
                    onClick = onRestart,
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = OrangeSecondary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                ) {
                    Text(
                        text = if (isEnglish) "🔄 RESTART (3 ❤️)" else "🔄 REINICIAR (3 ❤️)",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FredokaFont,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Botón Salir
                Button(
                    onClick = onExit,
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE2E8F0)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(42.dp)
                ) {
                    Text(
                        text = if (isEnglish) "🚪 Back to Toys" else "🚪 Salir al Baúl",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FredokaFont,
                        color = SlateDark
                    )
                }
            }
        }
    }
}

/**
 * Niveles de dificultad para los minijuegos infantiles:
 * - FÁCIL: Solo Sumas y Restas (➕ ➖)
 * - MODERADO: Sumas, Restas, Multiplicación y División (➕ ➖ ✖️ ➗)
 */
enum class ToyGameDifficulty(
    val titleEs: String,
    val titleEn: String,
    val descEs: String,
    val descEn: String,
    val badge: String,
    val color: Color
) {
    FACIL(
        titleEs = "Fácil",
        titleEn = "Easy",
        descEs = "Sumas y Restas",
        descEn = "Addition & Subtraction",
        badge = "🟢",
        color = Color(0xFF4CAF50)
    ),
    MODERADO(
        titleEs = "Moderado",
        titleEn = "Moderate",
        descEs = "Multiplicación y División",
        descEn = "Multiplication & Division",
        badge = "⚡",
        color = Color(0xFF7E57C2)
    )
}

/**
 * Representación unificada de un reto matemático adaptado al nivel de dificultad elegido.
 */
data class ToyMathProblem(
    val num1: Int,
    val num2: Int,
    val opSymbol: String,
    val opNameEs: String,
    val opNameEn: String,
    val opColor: Color,
    val result: Int
)

/**
 * Generador pedagógico de operaciones matemáticas:
 * En FÁCIL: Sumas y Restas (➕ ➖).
 * En MODERADO: Multiplicación o División (✖️ ➗).
 */
fun generateToyMathProblem(difficulty: ToyGameDifficulty = ToyGameDifficulty.FACIL): ToyMathProblem {
    val operations = if (difficulty == ToyGameDifficulty.FACIL) {
        listOf("ADD", "SUB")
    } else {
        listOf("MUL", "DIV")
    }
    return when (operations.random()) {
        "ADD" -> {
            val a = Random.nextInt(1, 10)
            val b = Random.nextInt(1, 10)
            ToyMathProblem(a, b, "+", "➕ Suma", "➕ Addition", Color(0xFF1E88E5), a + b)
        }
        "SUB" -> {
            val b = Random.nextInt(1, 9)
            val res = Random.nextInt(1, 10)
            val a = res + b
            ToyMathProblem(a, b, "−", "➖ Resta", "➖ Subtraction", Color(0xFF4CAF50), res)
        }
        "MUL" -> {
            val a = Random.nextInt(2, 6)
            val b = Random.nextInt(1, 6)
            ToyMathProblem(a, b, "×", "✖️ Multiplicar", "✖️ Multiply", Color(0xFF8E24AA), a * b)
        }
        else -> { // DIV
            val b = Random.nextInt(2, 6)
            val res = Random.nextInt(1, 6)
            val a = res * b
            ToyMathProblem(a, b, "÷", "➗ Dividir", "➗ Division", Color(0xFFFB8C00), res)
        }
    }
}

/**
 * Genera distractores numéricos positivos no repetidos para opciones múltiples.
 */
fun generateToyDistractors(correctResult: Int, count: Int = 3): List<Int> {
    val offsets = listOf(-3, -2, -1, 1, 2, 3, 4, 5, -4).shuffled()
    val list = mutableListOf<Int>()
    for (off in offsets) {
        val cand = correctResult + off
        if (cand > 0 && cand != correctResult && !list.contains(cand)) {
            list.add(cand)
            if (list.size >= count) break
        }
    }
    var filler = 1
    while (list.size < count) {
        if (filler != correctResult && !list.contains(filler)) {
            list.add(filler)
        }
        filler++
    }
    return list
}

/**
 * Ventana obligatoria previa al juego con 2 botones:
 * 1. FÁCIL: Solo sumas y restas (➕ ➖)
 * 2. MODERADO: Sumas, restas, multiplicación y división (➕ ➖ ✖️ ➗)
 */
@Composable
fun ToyGameDifficultySelectDialog(
    toyEmoji: String,
    toyName: String,
    gameTitle: String,
    isEnglish: Boolean,
    onSelectDifficulty: (ToyGameDifficulty) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .shadow(16.dp, RoundedCornerShape(26.dp))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Cabecera con botón de cierre
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFFF3E0))
                            .border(2.dp, Color(0xFFFFB74D), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = toyEmoji, fontSize = 28.sp)
                    }

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 10.dp)
                    ) {
                        Text(
                            text = gameTitle,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FredokaFont,
                            color = SlateDark
                        )
                        Text(
                            text = if (isEnglish) "Choose Difficulty" else "Elige la Dificultad",
                            fontSize = 12.5.sp,
                            fontFamily = FredokaFont,
                            color = SlateMuted
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFF1F5F9))
                            .clickable { onDismiss() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "✖", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = SlateDark)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = if (isEnglish) "¿How do you want to play?" else "¿Cómo quieres jugar?",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FredokaFont,
                    color = SlateDark,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(14.dp))

                // BOTÓN 1: FÁCIL (Solo Sumas y Restas)
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F8E9)),
                    border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFF4CAF50)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .clickable { onSelectDifficulty(ToyGameDifficulty.FACIL) }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color(0xFF4CAF50)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "➕➖", fontSize = 20.sp)
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (isEnglish) "EASY" else "FÁCIL",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Black,
                                    fontFamily = FredokaFont,
                                    color = Color(0xFF2E7D32)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = "🟢", fontSize = 12.sp)
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (isEnglish) "Additions & Subtractions (➕ ➖)" else "Sumas y Restas (➕ ➖)",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FredokaFont,
                                color = Color(0xFF33691E)
                            )
                        }

                        Text(text = "▶️", fontSize = 18.sp)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // BOTÓN 2: MODERADO (Multiplicación o División)
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFEDE7F6)),
                    border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFF7E57C2)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .clickable { onSelectDifficulty(ToyGameDifficulty.MODERADO) }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color(0xFF7E57C2)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "✖️➗", fontSize = 20.sp)
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (isEnglish) "MODERATE" else "MODERADO",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Black,
                                    fontFamily = FredokaFont,
                                    color = Color(0xFF4527A0)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = "⚡", fontSize = 12.sp)
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (isEnglish) "Multiplication or Division (✖️ ➗)" else "Multiplicación o División (✖️ ➗)",
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FredokaFont,
                                color = Color(0xFF311B92)
                            )
                        }

                        Text(text = "▶️", fontSize = 18.sp)
                    }
                }
            }
        }
    }
}

/**
 * Diálogo triunfal tras alcanzar 30 ACIERTOS matemáticos:
 * ¡FELICITACIONES! ¡YA GANASTE!
 */
@Composable
fun ToyVictoryDialog(
    toyEmoji: String,
    toyName: String,
    score: Int,
    coinsEarned: Int,
    isEnglish: Boolean,
    soundManager: SoundManager? = null,
    ttsManager: TtsManager? = null,
    onPlayAgain: () -> Unit,
    onExit: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current

    LaunchedEffect(Unit) {
        soundManager?.playReward()
        soundManager?.playApplause()
        ttsManager?.speak(
            if (isEnglish) "Congratulations! You won! 30 correct answers!"
            else "¡Felicitaciones! ¡Ya ganaste! ¡30 aciertos logrados!"
        )
        // Bonus de 30 monedas por victoria
        CoinShopManager.addCoin(context, 30)
    }

    Dialog(onDismissRequest = onExit) {
        Card(
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .shadow(16.dp, RoundedCornerShape(28.dp))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Trofeo animado / Corona
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFFFFF9C4), Color(0xFFFFE082))
                            )
                        )
                        .border(3.dp, Color(0xFFFFB300), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "🏆", fontSize = 48.sp)
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = if (isEnglish) "¡CONGRATULATIONS!" else "¡FELICITACIONES!",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FredokaFont,
                    color = Color(0xFFF57F17),
                    textAlign = TextAlign.Center
                )

                Text(
                    text = if (isEnglish) "¡YOU WON!" else "¡YA GANASTE!",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FredokaFont,
                    color = GreenPrimary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = if (isEnglish)
                        "You achieved 30 correct hits with $toyName! Amazing job!"
                    else
                        "¡Alcanzaste 30 aciertos con $toyName! ¡Eres un súper campeón!",
                    fontSize = 14.sp,
                    fontFamily = FredokaFont,
                    color = SlateDark,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Resumen de Victoria
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFF1F8E9))
                        .border(1.5.dp, GreenPrimary.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                        .padding(vertical = 10.dp, horizontal = 14.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "🎯 Aciertos", fontSize = 11.5.sp, color = SlateMuted)
                        Text(text = "30 / 30", fontSize = 18.sp, fontWeight = FontWeight.Black, color = GreenPrimary)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "🪙 Premio Victoria", fontSize = 11.5.sp, color = SlateMuted)
                        Text(text = "+30 Monedas", fontSize = 18.sp, fontWeight = FontWeight.Black, color = Color(0xFFF57F17))
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Botón Jugar de nuevo
                Button(
                    onClick = onPlayAgain,
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text(
                        text = if (isEnglish) "🔄 PLAY AGAIN (0/30)" else "🔄 JUGAR OTRA VEZ (0/30)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FredokaFont,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Botón Salir al Baúl
                Button(
                    onClick = onExit,
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE2E8F0)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                ) {
                    Text(
                        text = if (isEnglish) "🚪 Back to Toys" else "🚪 Salir al Baúl",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FredokaFont,
                        color = SlateDark
                    )
                }
            }
        }
    }
}
