package com.educa.mathkids.presentation.screens.home

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.educa.mathkids.R
import com.educa.mathkids.core.game.AdventureProgressManager
import com.educa.mathkids.core.shop.CoinShopManager
import com.educa.mathkids.domain.model.OperationType
import com.educa.mathkids.presentation.ads.AdManager
import com.educa.mathkids.presentation.ads.BannerAdView
import com.educa.mathkids.presentation.components.ConfettiCelebration
import com.educa.mathkids.presentation.components.DynamicKidsBackground
import com.educa.mathkids.presentation.components.KidsBottomNavDock
import com.educa.mathkids.presentation.components.KidsNavTab
import com.educa.mathkids.presentation.components.StreakRecoveryDialog
import com.educa.mathkids.presentation.localization.AppLanguage
import com.educa.mathkids.presentation.localization.LocalAppLanguage
import com.educa.mathkids.presentation.localization.appStrings
import com.educa.mathkids.presentation.theme.FredokaFont
import com.educa.mathkids.presentation.theme.SlateDark
import com.educa.mathkids.presentation.theme.SlateMuted
import com.educa.mathkids.presentation.theme.SoftRed
import com.educa.mathkids.presentation.theme.SurfaceWhite
import com.educa.mathkids.presentation.utils.SoundManager
import com.educa.mathkids.presentation.utils.TtsManager

/**
 * Pantalla Principal Oficial de MATEKIDS.
 * Estilo alegre, animado e infantil inspirado en el diseño Dribbble con Hero Banner,
 * mascota 3D completa y sin fondo, tarjeta de progreso con estrellas, botón de regalo diario,
 * cuadrícula 2x2 con el juego 2D de Aventura de Globos y Mis Juguetes.
 */
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    ttsManager: TtsManager,
    onNavigateToNumbers: () -> Unit,
    onNavigateToOperationSelect: () -> Unit,
    onNavigateToOperationLevels: (OperationType) -> Unit,
    onNavigateToPractice: () -> Unit,
    onNavigateToChallenge: () -> Unit,
    onNavigateToAchievements: () -> Unit,
    onNavigateToProgress: () -> Unit,
    onNavigateToSettings: () -> Unit,
    modifier: Modifier = Modifier,
    onNavigateToProblemario: () -> Unit = {},
    onNavigateToAdventure: (OperationType, Int) -> Unit = { _, _ -> },
    onNavigateToCalculator: () -> Unit = {},
    onNavigateToShop: () -> Unit = {},
    onNavigateToMyToys: () -> Unit = {},
    onNavigateToLearnCompare: () -> Unit = {},
    soundManager: SoundManager? = null,
    adManager: AdManager? = null,
    onRewardEarned: ((Int) -> Unit)? = null
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val accumulatedCoins by CoinShopManager.coinsState.collectAsState()
    val purchasedToys by CoinShopManager.purchasedToysState.collectAsState()
    val isDailyRewardClaimed by CoinShopManager.dailyRewardClaimedTodayState.collectAsState()

    val strings = appStrings()
    val isEnglish = LocalAppLanguage.current == AppLanguage.INGLES

    var showExitDialog by remember { mutableStateOf(false) }
    var showRewardCelebration by remember { mutableStateOf(false) }
    var showStreakRecoveryDialog by remember { mutableStateOf(false) }
    var recoverableStreakDays by remember { mutableIntStateOf(0) }
    var selectedAdventureOp by remember { mutableStateOf<OperationType?>(null) }

    val pendingDialogOp by AdventureProgressManager.pendingDialogOpState.collectAsState()
    LaunchedEffect(pendingDialogOp) {
        pendingDialogOp?.let { op ->
            selectedAdventureOp = op
            AdventureProgressManager.clearPendingDialog()
        }
    }

    LaunchedEffect(Unit) {
        AdventureProgressManager.init(context)
        CoinShopManager.init(context)
        viewModel.checkStreak(context) { lostDays ->
            recoverableStreakDays = lostDays
            showStreakRecoveryDialog = true
        }
    }

    fun promptExit() {
        soundManager?.playButtonClick()
        showExitDialog = true
        ttsManager.speak(if (isEnglish) "Are you sure you want to exit MateKids?" else "¿Seguro que quieres salir de MateKids?")
    }

    BackHandler(enabled = true) {
        if (selectedAdventureOp != null) {
            soundManager?.playButtonClick()
            selectedAdventureOp = null
        } else {
            promptExit()
        }
    }

    DynamicKidsBackground(modifier = modifier) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp)
                    .padding(top = 4.dp, bottom = 24.dp)
                    .navigationBarsPadding(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // ==========================================
                // 1. BARRA SUPERIOR: AVATAR, SALUDO, AJUSTES Y SALIDA
                // ==========================================
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Avatar infantil + Saludo amigable
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .shadow(4.dp, CircleShape)
                                .clip(CircleShape)
                                .background(Color(0xFFE1F5FE))
                                .border(2.5.dp, Color.White, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_app_logo),
                                contentDescription = "Logo MateKids",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Text(
                                text = if (isEnglish) "Hi, Champion!" else "¡Hola, Campeón!",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FredokaFont,
                                color = SlateDark
                            )
                            Text(
                                text = if (isEnglish) "Ready to learn and have fun?" else "¿Listo para jugar y aprender?",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                fontFamily = FredokaFont,
                                color = SlateMuted
                            )
                        }
                    }

                    // Botones de Ajustes y Salida (puerta 🚪 como estaba antes)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Botón Circular de Ajustes
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .shadow(4.dp, CircleShape, spotColor = Color(0xFF7E57C2).copy(alpha = 0.3f))
                                .clip(CircleShape)
                                .background(Color(0xFFEDE7F6))
                                .border(2.dp, Color(0xFFD1C4E9), CircleShape)
                                .clickable {
                                    soundManager?.playButtonClick()
                                    onNavigateToSettings()
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "⚙️", fontSize = 22.sp)
                        }

                        // Botón Circular de Salida
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .shadow(4.dp, CircleShape, spotColor = SoftRed.copy(alpha = 0.3f))
                                .clip(CircleShape)
                                .background(Color(0xFFFFEBEE))
                                .border(2.dp, SoftRed.copy(alpha = 0.6f), CircleShape)
                                .clickable { promptExit() },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "🚪", fontSize = 20.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // =========================================================================
                // 1.5 CÁPSULAS DE ESTADÍSTICAS: PUNTOS 🎯, MONEDAS 🪙 Y RACHA 🔥
                // =========================================================================
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 1. PUNTOS DEL JUGADOR (🎯)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .shadow(3.dp, RoundedCornerShape(16.dp), spotColor = Color(0xFFFFB300).copy(alpha = 0.3f))
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFFFFF8E1))
                            .border(1.5.dp, Color(0xFFFFD54F), RoundedCornerShape(16.dp))
                            .clickable {
                                soundManager?.playButtonClick()
                                onNavigateToAchievements()
                            }
                            .padding(horizontal = 4.dp, vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(text = "🎯", fontSize = 15.sp)
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "${uiState.totalPoints}",
                                fontSize = if (uiState.totalPoints >= 10000) 13.sp else 15.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FredokaFont,
                                maxLines = 1,
                                softWrap = false,
                                color = Color(0xFFF57F17)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = "pts",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FredokaFont,
                                maxLines = 1,
                                softWrap = false,
                                color = Color(0xFFF57F17).copy(alpha = 0.85f)
                            )
                        }
                    }

                    // 2. MONEDAS (🪙)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .shadow(3.dp, RoundedCornerShape(16.dp), spotColor = Color(0xFFFF9800).copy(alpha = 0.3f))
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFFFFF3E0))
                            .border(1.5.dp, Color(0xFFFFB74D), RoundedCornerShape(16.dp))
                            .clickable {
                                soundManager?.playButtonClick()
                                onNavigateToShop()
                            }
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
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

                    // 3. RACHA (🔥)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .shadow(3.dp, RoundedCornerShape(16.dp), spotColor = Color(0xFFFF5722).copy(alpha = 0.3f))
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFFFBE9E7))
                            .border(1.5.dp, Color(0xFFFF8A65), RoundedCornerShape(16.dp))
                            .padding(horizontal = 4.dp, vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(text = "🔥", fontSize = 15.sp)
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "${uiState.currentStreak}",
                                fontSize = if (uiState.currentStreak >= 100) 13.sp else 15.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FredokaFont,
                                maxLines = 1,
                                softWrap = false,
                                color = Color(0xFFD84315)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = if (isEnglish) "days" else "días",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FredokaFont,
                                maxLines = 1,
                                softWrap = false,
                                color = Color(0xFFD84315).copy(alpha = 0.85f)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // =========================================================================
                // 2. HERO BANNER: CIELO AZUL + MASCOTA 3D COMPLETA Y SIN FONDO + PROGRESO
                // =========================================================================
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(8.dp, RoundedCornerShape(28.dp), spotColor = Color(0xFF0288D1).copy(alpha = 0.25f))
                        .clip(RoundedCornerShape(28.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color(0xFF81D4FA),
                                    Color(0xFFB3E5FC),
                                    Color(0xFFE1F5FE)
                                )
                            )
                        )
                        .border(2.5.dp, Color.White.copy(alpha = 0.8f), RoundedCornerShape(28.dp))
                        .padding(horizontal = 14.dp, vertical = 12.dp)
                ) {
                    // Nubes decorativas suaves de fondo
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "☁️", fontSize = 28.sp, modifier = Modifier.padding(top = 4.dp))
                        Text(text = "✨", fontSize = 20.sp, modifier = Modifier.padding(top = 10.dp, end = 20.dp))
                        Text(text = "☁️", fontSize = 32.sp, modifier = Modifier.padding(top = 2.dp))
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Mascota 3D Elefante alegre: COMPLETO Y SIN FONDO
                        Box(
                            modifier = Modifier
                                .weight(0.95f)
                                .height(165.dp)
                                .clickable {
                                    soundManager?.playVictory()
                                    ttsManager.speak(if (isEnglish) "Hello! I am your elephant friend, let's learn math together!" else "¡Hola! ¡Soy tu amigo el elefante, vamos a aprender matemáticas juntos!")
                                },
                            contentAlignment = Alignment.BottomCenter
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_mascot_elephant_3d),
                                contentDescription = "Mascota Elefante 3D",
                                contentScale = ContentScale.Fit,
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .padding(vertical = 2.dp)
                            )
                        }

                        // Columna derecha: Tarjeta "Tu Progreso" + Botón "Regalo Diario"
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1.15f)
                        ) {
                            // Tarjeta de Progreso Blanca con Medalla y Estrellas
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .shadow(6.dp, RoundedCornerShape(20.dp), spotColor = Color.Black.copy(alpha = 0.10f))
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(SurfaceWhite)
                                    .clickable {
                                        soundManager?.playButtonClick()
                                        onNavigateToProgress()
                                    }
                                    .padding(horizontal = 12.dp, vertical = 10.dp)
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    // Medalla Dorada
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Text(text = "🏆", fontSize = 16.sp)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = if (isEnglish) "Your Progress" else "Tu Progreso",
                                            fontSize = 12.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = FredokaFont,
                                            color = SlateMuted
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(4.dp))

                                    // Puntos con Diana/Acierto (VISUALIZACIÓN REAL DE PUNTOS)
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Text(text = "🎯", fontSize = 22.sp)
                                        Spacer(modifier = Modifier.width(5.dp))
                                        Text(
                                            text = "${uiState.totalPoints}",
                                            fontSize = 25.sp,
                                            fontWeight = FontWeight.Black,
                                            fontFamily = FredokaFont,
                                            color = SlateDark
                                        )
                                    }

                                    Text(
                                        text = if (isEnglish) "points" else "puntos",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FredokaFont,
                                        color = Color(0xFFF57F17)
                                    )

                                    Spacer(modifier = Modifier.height(6.dp))

                                    // Barra de progreso verde redondeada
                                    val progressFraction = if (uiState.totalPoints > 0) ((uiState.totalPoints % 100) / 100f).coerceIn(0.1f, 1f) else 0.05f
                                    LinearProgressIndicator(
                                        progress = { progressFraction },
                                        color = Color(0xFF66BB6A),
                                        trackColor = Color(0xFFE8F5E9),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(8.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Botón de Regalo Diario (Solo 1 vez al día)
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .shadow(
                                        4.dp,
                                        RoundedCornerShape(18.dp),
                                        spotColor = if (isDailyRewardClaimed) Color(0xFF4CAF50).copy(alpha = 0.25f) else Color(0xFF7E57C2).copy(alpha = 0.35f)
                                    )
                                    .clip(RoundedCornerShape(18.dp))
                                    .background(
                                        if (isDailyRewardClaimed) {
                                            Brush.horizontalGradient(
                                                listOf(Color(0xFF81C784), Color(0xFF4CAF50))
                                            )
                                        } else {
                                            Brush.horizontalGradient(
                                                listOf(Color(0xFF9575CD), Color(0xFF7E57C2))
                                            )
                                        }
                                    )
                                    .clickable {
                                        if (isDailyRewardClaimed) {
                                            soundManager?.playButtonClick()
                                            ttsManager.speak(
                                                if (isEnglish) "You already claimed your reward for today! Come back tomorrow for more coins!"
                                                else "¡Ya reclamaste tu regalo de hoy! Vuelve mañana para más monedas."
                                            )
                                        } else {
                                            val claimed = CoinShopManager.claimDailyReward(context, 10)
                                            if (claimed) {
                                                soundManager?.playVictory()
                                                onRewardEarned?.invoke(10)
                                                showRewardCelebration = true
                                                ttsManager.speak(
                                                    if (isEnglish) "Congratulations! You claimed your daily reward of 10 coins!"
                                                    else "¡Felicidades! ¡Has reclamado tu regalo diario de 10 monedas!"
                                                )
                                            }
                                        }
                                    }
                                    .padding(vertical = 8.dp, horizontal = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = if (isDailyRewardClaimed) "✅" else "🎁", fontSize = 16.sp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (isDailyRewardClaimed) {
                                            if (isEnglish) "Claimed Today ✓" else "¡Reclamado Hoy! ✓"
                                        } else {
                                            if (isEnglish) "Daily Reward (+10 🪙)" else "Regalo Diario (+10 🪙)"
                                        },
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Black,
                                        fontFamily = FredokaFont,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // ===============================================
                // 3. TÍTULO DE SECCIÓN: ELIGE UNA AVENTURA
                // ===============================================
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isEnglish) "Choose a Category" else "Elige una Aventura",
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FredokaFont,
                        color = Color(0xFF1A237E)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // ====================================================================
                // 4. CUADRÍCULA 2x2: OPERACIONES, NÚMEROS, AVENTURA DE GLOBOS Y MIS JUGUETES
                // ====================================================================
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // FILA 1: NÚMEROS (Verde Menta) + OPERACIONES (Amarillo/Ámbar)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // 🔢 NÚMEROS (1 2 3)
                        VibrantCategoryCard(
                            symbols = "1️⃣2️⃣3️⃣",
                            title = if (isEnglish) "Numbers" else "Números",
                            subtitle = if (isEnglish) "Count & Learn" else "Conoce y Cuenta",
                            bgColor = Color(0xFF66BB6A),
                            borderColor = Color(0xFF43A047),
                            onClick = onNavigateToNumbers,
                            modifier = Modifier.weight(1f)
                        )

                        // 🧮 OPERACIONES (+ - × ÷)
                        VibrantCategoryCard(
                            symbols = "➕➖✖️➗",
                            title = if (isEnglish) "Operations" else "Operaciones",
                            subtitle = if (isEnglish) "Math Adventure" else "Aventura de Retos",
                            bgColor = Color(0xFFFFB300),
                            borderColor = Color(0xFFFF8F00),
                            onClick = onNavigateToOperationSelect,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // FILA 2: JUEGO 2D DE AVENTURA DE GLOBOS (Púrpura) + MIS JUGUETES (Azul Celeste)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // 🎈 JUEGO 2D DE AVENTURA DE GLOBOS (Sustituye a Aprender según solicitud)
                        VibrantCategoryCard(
                            symbols = "🎈🏹🎯",
                            title = if (isEnglish) "Balloon Quest" else "Aventura Globos",
                            subtitle = if (isEnglish) "Pop Balloons 2D" else "Revienta Globos 2D",
                            bgColor = Color(0xFFAB47BC),
                            borderColor = Color(0xFF8E24AA),
                            onClick = {
                                soundManager?.playButtonClick()
                                ttsManager.speak(if (isEnglish) "Balloon 2D Quest! Pop the correct balloons!" else "¡Aventura de Globos 2D! ¡Revienta los globos correctos!")
                                selectedAdventureOp = OperationType.ADDITION
                            },
                            modifier = Modifier.weight(1f)
                        )

                        // 🧸 MIS JUGUETES (Sala de Juegos con 3 vidas)
                        VibrantCategoryCard(
                            symbols = "🦁🏎️⚽",
                            title = if (isEnglish) "My Toys" else "Mis Juguetes",
                            subtitle = if (isEnglish) "${purchasedToys.size} Toys (3 ❤️)" else "${purchasedToys.size} Juguetes (3 ❤️)",
                            bgColor = Color(0xFF42A5F5),
                            borderColor = Color(0xFF1E88E5),
                            onClick = onNavigateToMyToys,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // =========================================================================
                // 5. MÁS ACTIVIDADES Y HERRAMIENTAS: CUADROS EN 2 FILAS PARA APROVECHAR EL ESPACIO
                // =========================================================================
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isEnglish) "More Fun & Tools" else "Más Actividades y Herramientas",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FredokaFont,
                        color = Color(0xFF1A237E)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // FILA 1: TIENDA, LOGROS, DESAFÍO
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        SecondaryKidToolPill(
                            emoji = "🏪",
                            name = strings.btnShop,
                            badge = "🪙 $accumulatedCoins",
                            bgColor = Color(0xFFFFF3E0),
                            borderColor = Color(0xFFFFB74D),
                            onClick = {
                                soundManager?.playButtonClick()
                                onNavigateToShop()
                            },
                            modifier = Modifier.weight(1f)
                        )

                        SecondaryKidToolPill(
                            emoji = "🏆",
                            name = if (isEnglish) "Awards" else "Logros",
                            badge = "${uiState.totalPoints} 🎯",
                            bgColor = Color(0xFFFFF8E1),
                            borderColor = Color(0xFFFFD54F),
                            onClick = {
                                soundManager?.playButtonClick()
                                onNavigateToAchievements()
                            },
                            modifier = Modifier.weight(1f)
                        )

                        SecondaryKidToolPill(
                            emoji = "⚡",
                            name = if (isEnglish) "Challenge" else "Desafío",
                            badge = if (isEnglish) "Speed Math" else "Contrarreloj",
                            bgColor = Color(0xFFF3E5F5),
                            borderColor = Color(0xFFCE93D8),
                            onClick = {
                                soundManager?.playButtonClick()
                                onNavigateToChallenge()
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // FILA 2: PROBLEMARIO, CALCULADORA, PROGRESO
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        SecondaryKidToolPill(
                            emoji = "📚",
                            name = strings.btnProblemario,
                            badge = if (isEnglish) "Stories" else "Cuentos",
                            bgColor = Color(0xFFE8F5E9),
                            borderColor = Color(0xFF81C784),
                            onClick = {
                                soundManager?.playButtonClick()
                                onNavigateToProblemario()
                            },
                            modifier = Modifier.weight(1f)
                        )

                        SecondaryKidToolPill(
                            emoji = "🧮",
                            name = if (isEnglish) "Calc" else "Calculadora",
                            badge = if (isEnglish) "Voice" else "Con Voz",
                            bgColor = Color(0xFFE0F7FA),
                            borderColor = Color(0xFF4DD0E1),
                            onClick = {
                                soundManager?.playButtonClick()
                                onNavigateToCalculator()
                            },
                            modifier = Modifier.weight(1f)
                        )

                        SecondaryKidToolPill(
                            emoji = "📊",
                            name = if (isEnglish) "Progress" else "Progreso",
                            badge = if (isEnglish) "Level ${uiState.level}" else "Nivel ${uiState.level}",
                            bgColor = Color(0xFFEDE7F6),
                            borderColor = Color(0xFFB39DDB),
                            onClick = {
                                soundManager?.playButtonClick()
                                onNavigateToProgress()
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Banner de Publicidad Adaptable
                BannerAdView(modifier = Modifier.fillMaxWidth())
            }

            // Confeti de celebración de Regalo Diario
            if (showRewardCelebration) {
                ConfettiCelebration(
                    visible = true,
                    onFinished = { showRewardCelebration = false }
                )
            }
        }

        // Diálogo para seleccionar nivel de Aventura de Globos 2D
        selectedAdventureOp?.let { op ->
            AdventureLevelSelectDialog(
                initialOp = op,
                isEnglish = isEnglish,
                soundManager = soundManager,
                ttsManager = ttsManager,
                onDismiss = {
                    soundManager?.playButtonClick()
                    selectedAdventureOp = null
                },
                onSelectLevel = { selectedOp, level ->
                    selectedAdventureOp = null
                    soundManager?.playButtonClick()
                    onNavigateToAdventure(selectedOp, level)
                }
            )
        }

        // Diálogo para Salir de la App
        if (showExitDialog) {
            Dialog(
                onDismissRequest = { showExitDialog = false },
                properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .shadow(16.dp, RoundedCornerShape(24.dp))
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color.White)
                        .border(2.5.dp, SoftRed.copy(alpha = 0.5f), RoundedCornerShape(24.dp))
                        .padding(22.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "👋", fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = if (isEnglish) "¿Exit MateKids?" else "¿Salir de MateKids?",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FredokaFont,
                            color = SlateDark,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (isEnglish) "Are you sure you want to close the app?" else "¿Estás seguro de que deseas salir del juego?",
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Medium,
                            fontFamily = FredokaFont,
                            color = SlateMuted,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(18.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = { showExitDialog = false },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE2E8F0))
                            ) {
                                Text(
                                    text = if (isEnglish) "STAY" else "SEGUIR",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Black,
                                    fontFamily = FredokaFont,
                                    color = SlateDark
                                )
                            }
                            Button(
                                onClick = {
                                    showExitDialog = false
                                    (context as? Activity)?.finish()
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = SoftRed)
                            ) {
                                Text(
                                    text = if (isEnglish) "EXIT" else "SALIR",
                                    fontSize = 13.sp,
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

        // Diálogo para Recuperar Racha con Anuncio Recompensado
        if (showStreakRecoveryDialog) {
            StreakRecoveryDialog(
                lostStreak = recoverableStreakDays,
                isEnglish = isEnglish,
                onWatchAd = {
                    val activity = context as? Activity
                    if (activity != null && adManager != null) {
                        adManager.showRewarded(
                            activity = activity,
                            onRewardEarned = {
                                viewModel.recoverStreak(context) {
                                    showStreakRecoveryDialog = false
                                    showRewardCelebration = true
                                    soundManager?.playVictory()
                                    ttsManager.speak(
                                        if (isEnglish) "Streak recovered! Keep the fire burning!"
                                        else "¡Racha recuperada! ¡Sigue jugando cada día!"
                                    )
                                }
                            },
                            onAdDismissed = {
                                // Cerró el diálogo de anuncio
                            }
                        )
                    } else {
                        // Fallback si no hay anuncio disponible
                        viewModel.recoverStreak(context) {
                            showStreakRecoveryDialog = false
                            showRewardCelebration = true
                        }
                    }
                },
                onDismiss = {
                    showStreakRecoveryDialog = false
                    viewModel.dismissStreakRecovery(context)
                }
            )
        }
    }
}

/**
 * Diálogo interactivo para seleccionar nivel y operación en el minijuego 2D de Aventura de Globos.
 */
@Composable
internal fun AdventureLevelSelectDialog(
    initialOp: OperationType,
    isEnglish: Boolean,
    soundManager: SoundManager? = null,
    ttsManager: TtsManager? = null,
    onDismiss: () -> Unit,
    onSelectLevel: (OperationType, Int) -> Unit
) {
    val context = LocalContext.current
    var currentOp by remember { mutableStateOf(initialOp) }

    val unlockedLevelsMap by AdventureProgressManager.unlockedLevelsState.collectAsState()
    val unlockedLevel = unlockedLevelsMap[currentOp]
        ?: AdventureProgressManager.getUnlockedLevel(context, currentOp)

    var selectedLevel by remember(currentOp, unlockedLevel) {
        mutableIntStateOf(unlockedLevel.coerceAtMost(15))
    }

    val opColor = Color(currentOp.colorHex)

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
                .border(3.dp, opColor.copy(alpha = 0.5f), RoundedCornerShape(28.dp))
                .padding(20.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Cabecera con icono, título y botón cerrar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "🎈", fontSize = 28.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = if (isEnglish) "Balloon 2D Quest" else "Aventura de Globos 2D",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FredokaFont,
                                color = SlateDark
                            )
                            Text(
                                text = if (isEnglish) "Unlocked: $unlockedLevel / 15 Levels" else "Desbloqueados: $unlockedLevel de 15 Niveles",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = opColor
                            )
                        }
                    }

                    IconButton(
                        onClick = {
                            soundManager?.playButtonClick()
                            onDismiss()
                        },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFF1F5F9))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cerrar",
                            tint = SlateMuted,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Selector de Operaciones (Suma, Resta, Multi, Divide)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val ops = listOf(
                        OperationType.ADDITION to if (isEnglish) "Suma" else "Suma",
                        OperationType.SUBTRACTION to if (isEnglish) "Resta" else "Resta",
                        OperationType.MULTIPLICATION to if (isEnglish) "Multi" else "Multi",
                        OperationType.DIVISION to if (isEnglish) "Div" else "División"
                    )
                    ops.forEach { (op, label) ->
                        val isSelected = op == currentOp
                        val color = Color(op.colorHex)
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) color else Color(0xFFF1F5F9))
                                .border(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) color else Color(0xFFCBD5E1),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable {
                                    soundManager?.playButtonClick()
                                    currentOp = op
                                }
                                .padding(vertical = 6.dp),
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

                // Regla didáctica de globos
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFF8FAFC))
                        .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(10.dp))
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = if (isEnglish) {
                            "🎈 4 balloons (Lv 1-5)  •  6 balloons (Lv 6-10)  •  8 balloons (Lv 11-15)"
                        } else {
                            "🎈 4 globos (Niv 1-5)  •  6 globos (Niv 6-10)  •  8 globos (Niv 11-15)"
                        },
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = SlateMuted,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Cuadrícula de 15 niveles (5 columnas x 3 filas)
                for (row in 0 until 3) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        for (col in 0 until 5) {
                            val levelNumber = row * 5 + col + 1
                            val isUnlocked = levelNumber <= unlockedLevel
                            val isSelected = levelNumber == selectedLevel

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                                    .shadow(if (isUnlocked) 2.dp else 0.dp, RoundedCornerShape(12.dp))
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        when {
                                            !isUnlocked -> Color(0xFFE2E8F0)
                                            isSelected -> opColor
                                            else -> Color.White
                                        }
                                    )
                                    .border(
                                        width = if (isSelected) 2.5.dp else 1.dp,
                                        color = if (isSelected) Color.White else opColor.copy(alpha = 0.5f),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .clickable(enabled = isUnlocked) {
                                        soundManager?.playButtonClick()
                                        selectedLevel = levelNumber
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (isUnlocked) "$levelNumber" else "🔒",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Black,
                                    fontFamily = FredokaFont,
                                    color = if (isSelected) Color.White else if (isUnlocked) SlateDark else SlateMuted
                                )
                            }
                        }
                    }
                    if (row < 2) Spacer(modifier = Modifier.height(6.dp))
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Botón Jugar Nivel
                Button(
                    onClick = { onSelectLevel(currentOp, selectedLevel) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = opColor)
                ) {
                    Text(
                        text = if (isEnglish) "▶ PLAY LEVEL $selectedLevel" else "▶ JUGAR NIVEL $selectedLevel",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FredokaFont,
                        color = Color.White
                    )
                }
            }
        }
    }
}

/**
 * Tarjeta de Categoría 3D Infantil con Colores Vivos y Relieve.
 */
@Composable
private fun VibrantCategoryCard(
    symbols: String,
    title: String,
    subtitle: String,
    bgColor: Color,
    borderColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    badgeContent: (@Composable () -> Unit)? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.94f else 1.0f,
        animationSpec = spring(),
        label = "category_card_scale"
    )

    Box(
        modifier = modifier
            .scale(scale)
            .shadow(10.dp, RoundedCornerShape(26.dp), spotColor = borderColor.copy(alpha = 0.45f))
            .clip(RoundedCornerShape(26.dp))
            .background(bgColor)
            .border(3.dp, Color.White.copy(alpha = 0.6f), RoundedCornerShape(26.dp)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .clickable(interactionSource = interactionSource, indication = null) { onClick() }
                .padding(vertical = 16.dp, horizontal = 10.dp)
        ) {
            Text(
                text = symbols,
                fontSize = 28.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = title,
                fontSize = 17.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FredokaFont,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(3.dp))

            Text(
                text = subtitle,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FredokaFont,
                color = Color.White.copy(alpha = 0.92f),
                textAlign = TextAlign.Center
            )
        }

        if (badgeContent != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(end = 6.dp, top = 6.dp)
            ) {
                badgeContent()
            }
        }
    }
}

/**
 * Cuadro de herramienta secundaria para niños con diseño redondeado y espacioso.
 */
@Composable
private fun SecondaryKidToolPill(
    emoji: String,
    name: String,
    badge: String,
    bgColor: Color,
    borderColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(86.dp)
            .shadow(4.dp, RoundedCornerShape(20.dp), spotColor = borderColor.copy(alpha = 0.35f))
            .clip(RoundedCornerShape(20.dp))
            .background(bgColor)
            .border(2.dp, borderColor.copy(alpha = 0.75f), RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .padding(vertical = 5.dp, horizontal = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = emoji, fontSize = 23.sp)
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = name,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FredokaFont,
                color = SlateDark,
                maxLines = 1,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(1.dp))
            Text(
                text = badge,
                fontSize = 9.5.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FredokaFont,
                color = SlateMuted,
                maxLines = 1,
                textAlign = TextAlign.Center
            )
        }
    }
}
