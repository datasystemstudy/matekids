package com.educa.mathkids.presentation.screens.shop.games

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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.educa.mathkids.core.shop.CoinShopManager
import com.educa.mathkids.domain.model.shop.ToyItem
import com.educa.mathkids.presentation.ads.NativeAdCard
import com.educa.mathkids.presentation.components.DynamicKidsBackground
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
 * Pantalla Principal: Sala de Juegos de Juguetes Canjeados.
 * Ofrece 20 modos de juego interactivos únicos (cada juguete tiene su mecánica),
 * todos con 3 VIDAS (❤️❤️❤️), con interfaz mejorada, selector de juegos y anuncio nativo integrado.
 */
@Composable
fun ToyGamesHubScreen(
    initialGame: String = "HUB",
    selectedToyId: String = "",
    soundManager: SoundManager? = null,
    ttsManager: TtsManager? = null,
    hapticUtil: HapticUtil? = null,
    onBackClick: () -> Unit,
    onNavigateToShop: () -> Unit,
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

    val selectedGameType = remember(selectedToyId, initialGame, myToys) {
        if (selectedToyId.isNotBlank()) {
            getToyGameType(selectedToyId)
        } else if (initialGame.isNotBlank() && initialGame.uppercase() != "HUB") {
            try {
                ToyGameType.valueOf(initialGame.uppercase())
            } catch (e: Throwable) {
                when (initialGame.uppercase()) {
                    "RUNNER" -> ToyGameType.CARRITO_RUNNER
                    "BOUNCE" -> ToyGameType.PELOTA_JUGGLE
                    "ADVENTURE" -> ToyGameType.DINO_CLIMB
                    "RHYTHM" -> ToyGameType.TAMBOR_RHYTHM
                    else -> myToys.firstOrNull()?.let { getToyGameType(it.id) } ?: ToyGameType.PELOTA_JUGGLE
                }
            }
        } else {
            myToys.firstOrNull()?.let { getToyGameType(it.id) } ?: ToyGameType.PELOTA_JUGGLE
        }
    }

    var selectedDifficulty by remember { mutableStateOf<ToyGameDifficulty?>(null) }
    var showDifficultyDialog by remember { mutableStateOf(myToys.isNotEmpty()) }

    val activeToy = remember(selectedGameType, myToys) {
        myToys.firstOrNull { getToyGameType(it.id) == selectedGameType }
            ?: CoinShopManager.TOYS_CATALOG.firstOrNull { it.id == selectedGameType.toyId }
            ?: myToys.firstOrNull()
            ?: CoinShopManager.TOYS_CATALOG.first()
    }

    LaunchedEffect(Unit) {
        CoinShopManager.init(context)
    }

    DynamicKidsBackground(modifier = modifier) {
        if (showDifficultyDialog && myToys.isNotEmpty()) {
            ToyGameDifficultySelectDialog(
                toyEmoji = activeToy.emoji,
                toyName = activeToy.name,
                gameTitle = if (isEnglish) selectedGameType.titleEn else selectedGameType.titleEs,
                isEnglish = isEnglish,
                onSelectDifficulty = { diff ->
                    soundManager?.playButtonClick()
                    selectedDifficulty = diff
                    showDifficultyDialog = false
                },
                onDismiss = {
                    if (selectedDifficulty == null) {
                        onBackClick()
                    } else {
                        showDifficultyDialog = false
                    }
                }
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp)
                .navigationBarsPadding()
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            // 1. Barra de Navegación Superior
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
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = strings.navBack,
                        tint = SlateDark
                    )
                }

                // Título y Chip de Dificultad (interactivo para cambiar dificultad)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable {
                            soundManager?.playButtonClick()
                            showDifficultyDialog = true
                        }
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "🎮", fontSize = 18.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isEnglish) selectedGameType.titleEn else selectedGameType.titleEs,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FredokaFont,
                            color = SlateDark
                        )
                    }
                    selectedDifficulty?.let { diff ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "${diff.badge} ${if (isEnglish) diff.titleEn else diff.titleEs} ⚙️",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FredokaFont,
                                color = diff.color
                            )
                        }
                    }
                }

                // Chip de Monedas
                Box(
                    modifier = Modifier
                        .shadow(3.dp, RoundedCornerShape(20.dp), spotColor = Color(0xFFFFB300).copy(alpha = 0.35f))
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFFFFF8E1))
                        .border(1.5.dp, Color(0xFFFFB300), RoundedCornerShape(20.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "🪙", fontSize = 15.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "$accumulatedCoins",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FredokaFont,
                            color = Color(0xFFE65100)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Si aún no tiene juguetes canjeados:
            if (myToys.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp)
                        .shadow(6.dp, RoundedCornerShape(24.dp))
                        .clip(RoundedCornerShape(24.dp))
                        .background(SurfaceWhite)
                        .border(2.dp, SlateBorder, RoundedCornerShape(24.dp))
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "🧸", fontSize = 64.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (isEnglish) "No toys unlocked yet!" else "¡Aún no tienes juguetes canjeados!",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FredokaFont,
                            color = SlateDark,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (isEnglish) {
                                "Redeem toys in the Toy Shop using your earned coins to unlock these awesome games!"
                            } else {
                                "¡Canjea juguetes en la Tienda con tus monedas ganadas para jugar en la sala de juegos con 3 vidas!"
                            },
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            fontFamily = FredokaFont,
                            color = SlateMuted,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = {
                                soundManager?.playButtonClick()
                                hapticUtil?.vibrateClick()
                                onNavigateToShop()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800)),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth(0.9f)
                                .height(48.dp)
                        ) {
                            Text(
                                text = if (isEnglish) "🏪 VISIT TOY SHOP" else "🏪 IR A LA TIENDA DE JUGUETES",
                                fontWeight = FontWeight.Black,
                                fontFamily = FredokaFont,
                                fontSize = 14.sp,
                                color = Color.White
                            )
                        }
                    }
                }
            } else if (selectedDifficulty != null) {
                val currentDiff = selectedDifficulty ?: ToyGameDifficulty.FACIL
                // Despacho directo al juego del juguete
                when (selectedGameType) {
                    ToyGameType.PELOTA_JUGGLE -> {
                        ToyBounceGame(
                            toy = activeToy,
                            isEnglish = isEnglish,
                            soundManager = soundManager,
                            ttsManager = ttsManager,
                            hapticUtil = hapticUtil,
                            difficulty = currentDiff,
                            onExitGame = onBackClick
                        )
                    }
                    ToyGameType.CRAYONES_COLOR -> {
                        CrayonesColorGame(
                            toy = activeToy,
                            isEnglish = isEnglish,
                            soundManager = soundManager,
                            ttsManager = ttsManager,
                            hapticUtil = hapticUtil,
                            difficulty = currentDiff,
                            onExitGame = onBackClick
                        )
                    }
                    ToyGameType.YOYO_TARGET -> {
                        YoyoTargetGame(
                            toy = activeToy,
                            isEnglish = isEnglish,
                            soundManager = soundManager,
                            ttsManager = ttsManager,
                            hapticUtil = hapticUtil,
                            difficulty = currentDiff,
                            onExitGame = onBackClick
                        )
                    }
                    ToyGameType.MUNECA_DRESSUP -> {
                        MunecaDressUpGame(
                            toy = activeToy,
                            isEnglish = isEnglish,
                            soundManager = soundManager,
                            ttsManager = ttsManager,
                            hapticUtil = hapticUtil,
                            difficulty = currentDiff,
                            onExitGame = onBackClick
                        )
                    }
                    ToyGameType.CARRITO_RUNNER -> {
                        ToyRunnerGame(
                            toy = activeToy,
                            isEnglish = isEnglish,
                            soundManager = soundManager,
                            ttsManager = ttsManager,
                            hapticUtil = hapticUtil,
                            difficulty = currentDiff,
                            onExitGame = onBackClick
                        )
                    }
                    ToyGameType.BURBUJAS_POP -> {
                        BurbujasPopperGame(
                            toy = activeToy,
                            isEnglish = isEnglish,
                            soundManager = soundManager,
                            ttsManager = ttsManager,
                            hapticUtil = hapticUtil,
                            difficulty = currentDiff,
                            onExitGame = onBackClick
                        )
                    }
                    ToyGameType.COMETA_GLIDER -> {
                        CometaSkyGliderGame(
                            toy = activeToy,
                            isEnglish = isEnglish,
                            soundManager = soundManager,
                            ttsManager = ttsManager,
                            hapticUtil = hapticUtil,
                            difficulty = currentDiff,
                            onExitGame = onBackClick
                        )
                    }
                    ToyGameType.DINO_CLIMB -> {
                        ToyAdventureGame(
                            toy = activeToy,
                            isEnglish = isEnglish,
                            soundManager = soundManager,
                            ttsManager = ttsManager,
                            hapticUtil = hapticUtil,
                            difficulty = currentDiff,
                            onExitGame = onBackClick
                        )
                    }
                    ToyGameType.UNICORNIO_RAINBOW -> {
                        UnicornioRainbowDashGame(
                            toy = activeToy,
                            isEnglish = isEnglish,
                            soundManager = soundManager,
                            ttsManager = ttsManager,
                            hapticUtil = hapticUtil,
                            difficulty = currentDiff,
                            onExitGame = onBackClick
                        )
                    }
                    ToyGameType.ROMPECABEZAS_SNAP -> {
                        RompecabezasSnapGame(
                            toy = activeToy,
                            isEnglish = isEnglish,
                            soundManager = soundManager,
                            ttsManager = ttsManager,
                            hapticUtil = hapticUtil,
                            difficulty = currentDiff,
                            onExitGame = onBackClick
                        )
                    }
                    ToyGameType.TREN_CONDUCTOR -> {
                        TrenConductorGame(
                            toy = activeToy,
                            isEnglish = isEnglish,
                            soundManager = soundManager,
                            ttsManager = ttsManager,
                            hapticUtil = hapticUtil,
                            difficulty = currentDiff,
                            onExitGame = onBackClick
                        )
                    }
                    ToyGameType.CASTILLO_DEFENSE -> {
                        CastilloDefenseGame(
                            toy = activeToy,
                            isEnglish = isEnglish,
                            soundManager = soundManager,
                            ttsManager = ttsManager,
                            hapticUtil = hapticUtil,
                            difficulty = currentDiff,
                            onExitGame = onBackClick
                        )
                    }
                    ToyGameType.ROBOT_MATRIX -> {
                        RobotMatrixGame(
                            toy = activeToy,
                            isEnglish = isEnglish,
                            soundManager = soundManager,
                            ttsManager = ttsManager,
                            hapticUtil = hapticUtil,
                            difficulty = currentDiff,
                            onExitGame = onBackClick
                        )
                    }
                    ToyGameType.PINTURA_SPLASH -> {
                        PinturaArtSplatterGame(
                            toy = activeToy,
                            isEnglish = isEnglish,
                            soundManager = soundManager,
                            ttsManager = ttsManager,
                            hapticUtil = hapticUtil,
                            difficulty = currentDiff,
                            onExitGame = onBackClick
                        )
                    }
                    ToyGameType.TAMBOR_RHYTHM -> {
                        ToyRhythmGame(
                            toy = activeToy,
                            isEnglish = isEnglish,
                            soundManager = soundManager,
                            ttsManager = ttsManager,
                            hapticUtil = hapticUtil,
                            difficulty = currentDiff,
                            onExitGame = onBackClick
                        )
                    }
                    ToyGameType.COHETE_SPACE -> {
                        CoheteSpaceMissionGame(
                            toy = activeToy,
                            isEnglish = isEnglish,
                            soundManager = soundManager,
                            ttsManager = ttsManager,
                            hapticUtil = hapticUtil,
                            difficulty = currentDiff,
                            onExitGame = onBackClick
                        )
                    }
                    ToyGameType.OSO_CATCHER -> {
                        OsoCatcherGame(
                            toy = activeToy,
                            isEnglish = isEnglish,
                            soundManager = soundManager,
                            ttsManager = ttsManager,
                            hapticUtil = hapticUtil,
                            difficulty = currentDiff,
                            onExitGame = onBackClick
                        )
                    }
                    ToyGameType.PATINETA_SKATE -> {
                        PatinetaSkateParkGame(
                            toy = activeToy,
                            isEnglish = isEnglish,
                            soundManager = soundManager,
                            ttsManager = ttsManager,
                            hapticUtil = hapticUtil,
                            difficulty = currentDiff,
                            onExitGame = onBackClick
                        )
                    }
                    ToyGameType.GUITARRA_SOLO -> {
                        GuitarraHeroGame(
                            toy = activeToy,
                            isEnglish = isEnglish,
                            soundManager = soundManager,
                            ttsManager = ttsManager,
                            hapticUtil = hapticUtil,
                            difficulty = currentDiff,
                            onExitGame = onBackClick
                        )
                    }
                    ToyGameType.DRON_DELIVERY -> {
                        DronDeliveryGame(
                            toy = activeToy,
                            isEnglish = isEnglish,
                            soundManager = soundManager,
                            ttsManager = ttsManager,
                            hapticUtil = hapticUtil,
                            difficulty = currentDiff,
                            onExitGame = onBackClick
                        )
                    }
                }
            }
        }
    }
}
