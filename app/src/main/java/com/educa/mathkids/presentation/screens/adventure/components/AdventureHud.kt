package com.educa.mathkids.presentation.screens.adventure.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.educa.mathkids.R
import com.educa.mathkids.domain.model.adventure.AdventureGameState
import com.educa.mathkids.domain.model.adventure.AdventurePhase
import com.educa.mathkids.presentation.theme.BluePrimary
import com.educa.mathkids.presentation.theme.OrangeSecondary
import com.educa.mathkids.presentation.theme.SoftRed
import com.educa.mathkids.presentation.theme.YellowStar

@Composable
fun AdventureTopBar(
    gameState: AdventureGameState,
    onBackClick: () -> Unit,
    onHomeClick: () -> Unit = onBackClick,
    onHintClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Botones de Navegación: Atrás (Vuelve al selector de operaciones) + Inicio (Home)
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Botón Atrás: Vuelve a la ventana de opciones de operaciones (Suma, Resta, Multi, División)
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .shadow(3.dp, RoundedCornerShape(14.dp), spotColor = OrangeSecondary.copy(alpha = 0.35f))
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color.White)
                    .border(2.dp, OrangeSecondary, RoundedCornerShape(14.dp))
                    .clickable { onBackClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Atrás - Operaciones",
                    tint = OrangeSecondary,
                    modifier = Modifier.size(26.dp)
                )
            }

            // Botón Inicio: Ir directamente a la pantalla de inicio
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .shadow(3.dp, RoundedCornerShape(14.dp), spotColor = BluePrimary.copy(alpha = 0.3f))
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color.White)
                    .border(2.dp, BluePrimary.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                    .clickable { onHomeClick() },
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_nav_inicio),
                    contentDescription = "Inicio",
                    modifier = Modifier.size(42.dp)
                )
            }
        }

        // Indicador del Nivel actual sobre 15
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White.copy(alpha = 0.92f))
                .border(2.dp, BluePrimary.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                .padding(horizontal = 10.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Nivel ${gameState.currentLevel}/15",
                fontWeight = FontWeight.Black,
                color = BluePrimary,
                fontSize = 13.sp
            )
        }

        // Vidas / Corazones
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(Color.White.copy(alpha = 0.9f))
                .border(2.dp, Color(0xFFFFCDD2), RoundedCornerShape(20.dp))
                .padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            for (i in 1..gameState.maxLives) {
                val hasLife = i <= gameState.lives
                Icon(
                    imageVector = if (hasLife) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = null,
                    tint = if (hasLife) SoftRed else Color(0xFFB0BEC5),
                    modifier = Modifier
                        .size(26.dp)
                        .padding(horizontal = 2.dp)
                )
            }
        }

        // Puntuación y Racha Combo
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (gameState.combo >= 2) {
                Box(
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFFFF9800), Color(0xFFFF5722))
                            )
                        )
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "🔥 x${gameState.combo}",
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        fontSize = 13.sp
                    )
                }
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White.copy(alpha = 0.9f))
                    .border(2.dp, Color(0xFFFFE082), RoundedCornerShape(20.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = YellowStar,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${gameState.score}",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B),
                        fontSize = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Botón de Pista didáctica
            IconButton(
                onClick = onHintClick,
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFFF9C4))
                    .border(2.dp, Color(0xFFFFD54F), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Lightbulb,
                    contentDescription = "Pista",
                    tint = Color(0xFFF57F17)
                )
            }
        }
    }
}

/**
 * Tarjeta de Desafío que flota en la parte superior del Canvas.
 */
@Composable
fun AdventureChallengeCard(
    equation: String,
    prompt: String,
    progressText: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .shadow(10.dp, RoundedCornerShape(24.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp, horizontal = 18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = prompt,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF475569)
                )
                Text(
                    text = progressText,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = BluePrimary
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Ecuación destacada
            Text(
                text = equation,
                fontSize = 38.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF0F172A),
                letterSpacing = 2.sp
            )
        }
    }
}

/**
 * Superposición modal de Victoria al completar los retos del nivel.
 */
@Composable
fun AdventureVictoryOverlay(
    gameState: AdventureGameState,
    onNextLevel: () -> Unit,
    onBackToMap: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.65f))
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(16.dp, RoundedCornerShape(32.dp)),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(32.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (gameState.currentLevel >= 15) "👑 ¡CAMPEÓN DEL MUNDO! 🏆" else "¡NIVEL ${gameState.currentLevel}/15 SUPERADO!",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF1E88E5),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Estrellas doradas animadas
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (i in 1..3) {
                        val earned = i <= gameState.stars
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = if (earned) YellowStar else Color(0xFFCBD5E1),
                            modifier = Modifier
                                .size(if (i == 2) 58.dp else 46.dp)
                                .padding(horizontal = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Resumen de Puntos
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(Color(0xFFF8FAFC))
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Puntuación Final:", color = Color(0xFF64748B), fontWeight = FontWeight.Medium)
                        Text("${gameState.score} pts", fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Racha Máxima:", color = Color(0xFF64748B), fontWeight = FontWeight.Medium)
                        Text("🔥 x${gameState.maxCombo}", fontWeight = FontWeight.Bold, color = OrangeSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onNextLevel,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = if (gameState.currentLevel >= 15) Color(0xFF00C853) else BluePrimary)
                ) {
                    Text(
                        text = if (gameState.currentLevel >= 15) "🏆 ¡Aventura Completada! (15/15)" else "¡Siguiente Nivel! (${gameState.currentLevel + 1}/15)",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = onBackToMap,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF1F5F9))
                ) {
                    Text(
                        text = "Volver al Mapa",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF475569)
                    )
                }
            }
        }
    }
}

/**
 * Superposición modal de Fin de Partida / Reintento pedagógico con opción de ver anuncio para continuar.
 */
@Composable
fun AdventureGameOverOverlay(
    gameState: AdventureGameState,
    isEnglish: Boolean = false,
    onWatchAdToContinue: (() -> Unit)? = null,
    onCloseClick: (() -> Unit)? = null,
    onRetry: () -> Unit,
    onBackToMap: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.65f))
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(16.dp, RoundedCornerShape(32.dp)),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(32.dp)
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                // Botón X en la esquina superior para cerrar y seguir en el juego
                IconButton(
                    onClick = { onCloseClick?.invoke() ?: onRetry() },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(14.dp)
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFF1F5F9))
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Cerrar",
                        tint = Color(0xFF64748B),
                        modifier = Modifier.size(20.dp)
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 26.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "💔", fontSize = 46.sp)

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = if (isEnglish) "You're out of lives!" else "¡Te quedaste sin vidas!",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFFEF5350),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (isEnglish) {
                            "You lost your 3 lives. Watch an ad to recover your 3 hearts and keep playing!"
                        } else {
                            "Has perdido tus 3 vidas. ¡Mira un anuncio para recuperar tus 3 corazones y continuar!"
                        },
                        fontSize = 14.sp,
                        color = Color(0xFF64748B),
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Píldora de recompensa destacada
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFFE8F5E9))
                            .border(1.5.dp, Color(0xFF4CAF50), RoundedCornerShape(14.dp))
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = if (isEnglish) "🎁 Reward: ❤️ ❤️ ❤️ (+3 Lives)" else "🎁 Recompensa: ❤️ ❤️ ❤️ (+3 Vidas)",
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2E7D32)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Botón 1: Ver anuncio para continuar (+3 vidas)
                    if (onWatchAdToContinue != null) {
                        Button(
                            onClick = onWatchAdToContinue,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp),
                            shape = RoundedCornerShape(20.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                        ) {
                            Text(text = "📺", fontSize = 18.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isEnglish) "Watch Ad to Continue" else "Ver anuncio para continuar",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    // Botón 2: Intentar de Nuevo (Reiniciar nivel)
                    Button(
                        onClick = onRetry,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = OrangeSecondary)
                    ) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isEnglish) "Restart Level" else "Intentar de Nuevo",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Botón 3: Salir / Seleccionar Nivel
                    Button(
                        onClick = onBackToMap,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp),
                        shape = RoundedCornerShape(18.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF1F5F9))
                    ) {
                        Text(
                            text = if (isEnglish) "Level Select" else "Seleccionar Nivel",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF475569)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Diálogo de pista didáctica emergente.
 */
@Composable
fun AdventureHintDialog(
    hint: String,
    explanation: String,
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .clickable(onClick = onDismiss)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(enabled = false) {} // Evitar dismiss al hacer clic dentro
                .shadow(16.dp, RoundedCornerShape(28.dp)),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(28.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFFF9C4)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Lightbulb,
                        contentDescription = null,
                        tint = Color(0xFFF57F17),
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "¡Pista Sabia!",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = hint,
                    fontSize = 16.sp,
                    color = Color(0xFF334155),
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = explanation,
                    fontSize = 14.sp,
                    color = Color(0xFF64748B),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(22.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BluePrimary)
                ) {
                    Text("¡Entendido!", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                }
            }
        }
    }
}
