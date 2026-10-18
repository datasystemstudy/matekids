package com.educa.mathkids.presentation.screens.operation_levels

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.educa.mathkids.R
import com.educa.mathkids.core.shop.ChestRewardManager
import com.educa.mathkids.core.shop.CoinShopManager
import com.educa.mathkids.domain.model.OperationLevel
import com.educa.mathkids.domain.model.OperationLevelConfig
import com.educa.mathkids.domain.model.OperationType
import com.educa.mathkids.domain.model.PlayerProgress
import com.educa.mathkids.domain.repository.PlayerProgressRepository
import com.educa.mathkids.presentation.localization.AppLanguage
import com.educa.mathkids.presentation.localization.LocalAppLanguage
import com.educa.mathkids.presentation.components.DynamicKidsBackground
import com.educa.mathkids.presentation.components.KidsBottomNavDock
import com.educa.mathkids.presentation.components.KidsNavTab
import com.educa.mathkids.presentation.theme.FredokaFont
import com.educa.mathkids.presentation.theme.SlateDark
import com.educa.mathkids.presentation.theme.SurfaceWhite
import kotlin.math.roundToInt

/**
 * Pantalla de Niveles de Operación de MATEKIDS:
 * 1. Barra superior con píldora de operación, racha 🔥, monedas 🪙 y energía ⚡.
 * 2. Banner de Unidad sticky con sección, unidad, título y cuaderno de notas 📋.
 * 3. Sendero ascendente de abajo hacia arriba (Nivel 1 al inicio abajo, hasta el Trofeo arriba).
 * 4. Casillas circulares 3D con número de nivel, y el último nivel coronado con un Trofeo 🏆.
 * 5. Nodos completados con mini-estrellas ⭐⭐⭐.
 * 6. Nodo activo con arcos concéntricos cian y bocadillo "EMPEZAR" con flecha.
 * 7. Nodos bloqueados 3D con número y candado 🔒.
 * 8. Cofres de recompensa: al abrirse quedan abiertos permanentemente sin dar monedas repetidas.
 * 9. Mascota Mate con insignia "2025" flotando al lado del nodo activo.
 * 10. Barra de navegación inferior oficial para niños (KidsBottomNavDock).
 */
@Composable
fun OperationLevelsScreen(
    operationType: OperationType,
    playerProgressRepository: PlayerProgressRepository,
    onLevelSelected: (Int) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    onInicioClick: () -> Unit = {},
    onJuegosClick: () -> Unit = {},
    onPremiosClick: () -> Unit = {},
    onAjustesClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val isEnglish = LocalAppLanguage.current == AppLanguage.INGLES
    val levels by playerProgressRepository.getLevels(operationType).collectAsState(initial = emptyList())
    val playerProfile by playerProgressRepository.getPlayerProfile().collectAsState(initial = PlayerProgress())
    val accumulatedCoins by CoinShopManager.coinsState.collectAsState()
    val claimedChests by ChestRewardManager.claimedChestsState.collectAsState()

    LaunchedEffect(Unit) {
        ChestRewardManager.init(context)
    }

    val safeLevels = remember(levels) {
        if (levels.isEmpty()) {
            OperationLevelConfig.getDefaultLevels(operationType)
        } else levels
    }

    val currentPlayLevel = remember(safeLevels) {
        val firstIncomplete = safeLevels.firstOrNull { it.isUnlocked && it.stars == 0 }?.levelNumber
        firstIncomplete ?: safeLevels.filter { it.isUnlocked }.maxByOrNull { it.levelNumber }?.levelNumber ?: 1
    }

    val completedLevelsCount = safeLevels.count { it.stars > 0 }
    val maxLevelNumber = remember(safeLevels) { safeLevels.maxOfOrNull { it.levelNumber } ?: 15 }

    // Colores temáticos oficiales según la operación
    val duolingoPrimaryColor = when (operationType) {
        OperationType.ADDITION -> Color(0xFF1CB0F6)       // Azul
        OperationType.SUBTRACTION -> Color(0xFF58CC02)    // Verde
        OperationType.MULTIPLICATION -> Color(0xFFCE82FF) // Púrpura
        OperationType.DIVISION -> Color(0xFFFF9600)       // Naranja
    }

    val duolingoDarkBevelColor = when (operationType) {
        OperationType.ADDITION -> Color(0xFF1899D6)
        OperationType.SUBTRACTION -> Color(0xFF46A302)
        OperationType.MULTIPLICATION -> Color(0xFFA559D9)
        OperationType.DIVISION -> Color(0xFFE07700)
    }

    var showGuidebookDialog by remember { mutableStateOf(false) }
    var chestRewardDialog by remember { mutableStateOf<Int?>(null) }

    val scrollState = rememberScrollState()
    val density = LocalDensity.current

    // Los niveles se construyen de abajo hacia arriba:
    // El nivel 1 queda abajo (comienzo del sendero) y el nivel 15 (trofeo) arriba en la cima.
    val pathItems = remember(safeLevels) {
        val ascending = mutableListOf<PathItem>()
        safeLevels.forEach { lvl ->
            ascending.add(PathItem.LevelNode(lvl))
            // Cofre de tesoro en los niveles 4 y 10
            if (lvl.levelNumber == 4 || lvl.levelNumber == 10) {
                ascending.add(PathItem.TreasureChest(chestLevel = lvl.levelNumber))
            }
        }
        // Invertimos la lista para que el nivel 15 esté en index 0 (arriba) y nivel 1 al final (abajo)
        ascending.reversed()
    }

    // Auto-scroll centrado en el nivel activo (ubicándolo en el sendero de abajo hacia arriba)
    LaunchedEffect(currentPlayLevel, pathItems) {
        val targetIndex = pathItems.indexOfFirst {
            it is PathItem.LevelNode && it.level.levelNumber == currentPlayLevel
        }
        if (targetIndex >= 0) {
            val stepHeightPx = with(density) { 120.dp.toPx() }
            val targetScrollY = ((targetIndex * stepHeightPx) - with(density) { 180.dp.toPx() }).roundToInt().coerceAtLeast(0)
            scrollState.animateScrollTo(targetScrollY)
        }
    }

    DynamicKidsBackground(modifier = modifier) {
        // 1. SENDERO CON SCROLL (debajo del header fijo)
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 142.dp, bottom = 85.dp)
                .verticalScroll(scrollState)
        ) {
            val totalWidthPx = constraints.maxWidth.toFloat()
            val stepHeightDp = 120.dp
            val stepHeightPx = with(density) { stepHeightDp.toPx() }
            val nodeSizePx = with(density) { 82.dp.toPx() }

            // Ondulación rítmica del sendero que desemboca en el centro en la cima (Trofeo) y abajo (Inicio)
            val itemPositions = remember(pathItems.size, totalWidthPx, stepHeightPx) {
                pathItems.mapIndexed { index, _ ->
                    val horizontalOffsetDp = when (index % 8) {
                        0 -> 0.dp      // Centro (Cima con el Trofeo / Nivel 1 abajo)
                        1 -> 35.dp     // Centro-derecha
                        2 -> 55.dp     // Derecha
                        3 -> 25.dp     // Centro-derecha suave
                        4 -> (-10).dp  // Centro suave
                        5 -> (-45).dp  // Izquierda
                        6 -> (-55).dp  // Centro-izquierda
                        7 -> (-25).dp  // Retorno al centro
                        else -> 0.dp
                    }
                    val xPx = (totalWidthPx / 2f) + with(density) { horizontalOffsetDp.toPx() }
                    val yPx = (index * stepHeightPx) + (stepHeightPx * 0.45f)
                    Offset(xPx, yPx)
                }
            }

            // Puntos guía conectores entre nodos con efecto 3D
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(stepHeightDp * pathItems.size + 140.dp)
            ) {
                if (itemPositions.size >= 2) {
                    for (i in 0 until itemPositions.size - 1) {
                        val p1 = itemPositions[i]
                        val p2 = itemPositions[i + 1]
                        val dotsCount = 4
                        for (d in 1..dotsCount) {
                            val frac = d.toFloat() / (dotsCount + 1)
                            val dotX = p1.x + (p2.x - p1.x) * frac
                            val dotY = p1.y + (p2.y - p1.y) * frac
                            // Sombra suave del punto
                            drawCircle(
                                color = Color(0x22000000),
                                radius = 5.dp.toPx(),
                                center = Offset(dotX, dotY + 1.dp.toPx())
                            )
                            // Punto blanco brillante
                            drawCircle(
                                color = Color.White.copy(alpha = 0.92f),
                                radius = 4.5.dp.toPx(),
                                center = Offset(dotX, dotY)
                            )
                        }
                    }
                }
            }

            // Renderizado de cada elemento del sendero
            pathItems.forEachIndexed { index, pathItem ->
                val pos = itemPositions[index]
                val itemX = pos.x - (nodeSizePx / 2f)
                val itemY = pos.y - (nodeSizePx / 2f)

                when (pathItem) {
                    is PathItem.LevelNode -> {
                        val level = pathItem.level
                        val isCurrent = level.levelNumber == currentPlayLevel
                        val isCompleted = level.stars > 0
                        val isLast = level.levelNumber == maxLevelNumber

                        Box(
                            modifier = Modifier
                                .offset { IntOffset(itemX.roundToInt(), itemY.roundToInt()) }
                                .size(88.dp, 120.dp),
                            contentAlignment = Alignment.TopCenter
                        ) {
                            DuolingoPathNode(
                                levelNumber = level.levelNumber,
                                isUnlocked = level.isUnlocked,
                                isCompleted = isCompleted,
                                isCurrent = isCurrent,
                                stars = level.stars,
                                isLastLevel = isLast,
                                baseColor = duolingoPrimaryColor,
                                darkBevelColor = duolingoDarkBevelColor,
                                isEnglish = isEnglish,
                                onClick = {
                                    if (level.isUnlocked) {
                                        onLevelSelected(level.levelNumber)
                                    }
                                }
                            )
                        }
                    }

                    is PathItem.TreasureChest -> {
                        val isChestUnlocked = safeLevels.any { it.levelNumber == pathItem.chestLevel && it.stars > 0 }
                        val isChestClaimed = claimedChests.contains("${operationType.name}_${pathItem.chestLevel}")

                        Box(
                            modifier = Modifier
                                .offset { IntOffset(itemX.roundToInt(), itemY.roundToInt()) }
                                .size(88.dp, 95.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            DuolingoWoodenChest(
                                isUnlocked = isChestUnlocked,
                                isClaimed = isChestClaimed,
                                onClick = {
                                    when {
                                        isChestClaimed -> {
                                            // Ya fue abierto: no entrega más monedas y permanece abierto
                                            chestRewardDialog = -1
                                        }
                                        isChestUnlocked -> {
                                            // Se abre por primera vez y entrega monedas
                                            val claimed = ChestRewardManager.claimChest(context, operationType, pathItem.chestLevel, 15)
                                            if (claimed) {
                                                chestRewardDialog = 15
                                            } else {
                                                chestRewardDialog = -1
                                            }
                                        }
                                        else -> {
                                            chestRewardDialog = 0
                                        }
                                    }
                                }
                            )
                        }
                    }
                }

                // DIORAMA DUOLINGO: Fogata con personaje y malvaviscos en el espacio derecho frente al cofre
                if (pathItem is PathItem.TreasureChest && (pathItem.chestLevel == 4 || pathItem.chestLevel == 10)) {
                    val campfireX = (totalWidthPx / 2f) + with(density) { 55.dp.toPx() }
                    val campfireY = pos.y - with(density) { 45.dp.toPx() }

                    Box(
                        modifier = Modifier
                            .offset { IntOffset(campfireX.roundToInt(), campfireY.roundToInt()) }
                            .size(130.dp, 140.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        DuolingoCampfireScene()
                    }
                }

                // MASCOTA MATE CON INSIGNIA "2025" (como Duo el búho en la captura a la izquierda del nivel activo)
                if (pathItem is PathItem.LevelNode && pathItem.level.levelNumber == currentPlayLevel) {
                    val mascotX = (totalWidthPx / 2f) - with(density) { 145.dp.toPx() }
                    val mascotY = pos.y + with(density) { 10.dp.toPx() }

                    Box(
                        modifier = Modifier
                            .offset { IntOffset(mascotX.roundToInt(), mascotY.roundToInt()) }
                            .size(80.dp),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_mascot_elephant_3d),
                                contentDescription = "Mate Duo",
                                modifier = Modifier.size(58.dp)
                            )
                            Box(
                                modifier = Modifier
                                    .offset(y = (-4).dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF1CB0F6))
                                    .padding(horizontal = 7.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "2025",
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    fontFamily = FredokaFont
                                )
                            }
                        }
                    }
                }
            }
        }

        // 2. HEADER SUPERIOR STICKY (TopBar + Unit Banner)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .shadow(4.dp, RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp), spotColor = Color.Black.copy(alpha = 0.08f))
                .clip(RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp))
                .background(SurfaceWhite.copy(alpha = 0.95f))
                .padding(bottom = 6.dp)
        ) {
            // Barra superior blanca con estadísticas
            DuolingoTopBar(
                operationType = operationType,
                completedLevels = completedLevelsCount,
                streak = playerProfile.currentStreak,
                coins = accumulatedCoins,
                hearts = 3,
                onBackClick = onBackClick
            )

            // Banner azul de Unidad
            DuolingoUnitBanner(
                operationType = operationType,
                currentLevel = currentPlayLevel,
                primaryColor = duolingoPrimaryColor,
                isEnglish = isEnglish,
                onGuidebookClick = { showGuidebookDialog = true }
            )
        }

        // 3. BARRA INFERIOR ANTERIOR OFICIAL PARA NIÑOS (KidsBottomNavDock)
        KidsBottomNavDock(
            currentTab = KidsNavTab.JUEGOS,
            onInicioClick = onInicioClick,
            onJuegosClick = onJuegosClick,
            onPremiosClick = onPremiosClick,
            onAjustesClick = onAjustesClick,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 16.dp, vertical = 6.dp)
        )

        // DIÁLOGO: Guía de la Unidad (Guidebook)
        if (showGuidebookDialog) {
            DuolingoGuidebookDialog(
                operationType = operationType,
                isEnglish = isEnglish,
                onDismiss = { showGuidebookDialog = false }
            )
        }

        // DIÁLOGO: Recompensa del Cofre
        chestRewardDialog?.let { rewardAmount ->
            DuolingoChestDialog(
                rewardAmount = rewardAmount,
                isEnglish = isEnglish,
                onDismiss = { chestRewardDialog = null }
            )
        }
    }
}

private sealed class PathItem {
    data class LevelNode(val level: OperationLevel) : PathItem()
    data class TreasureChest(val chestLevel: Int) : PathItem()
}

/**
 * 1. TOP BAR DUOLINGO CON STATS
 */
@Composable
private fun DuolingoTopBar(
    operationType: OperationType,
    completedLevels: Int,
    streak: Int,
    coins: Int,
    hearts: Int,
    onBackClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.size(34.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Atrás",
                    tint = Color(0xFFAFAFAF)
                )
            }

            Spacer(modifier = Modifier.width(4.dp))

            // Píldora de la Operación (como la bandera francesa de Duolingo)
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF7F7F7))
                    .border(1.5.dp, Color(0xFFE5E5E5), RoundedCornerShape(12.dp))
                    .padding(horizontal = 8.dp, vertical = 3.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = operationType.symbol,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(operationType.colorHex)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = "$completedLevels",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FredokaFont,
                        color = Color(0xFF4B4B4B)
                    )
                }
            }
        }

        // Métricas: Racha 🔥 | Monedas 💎 | Vidas/Energía ⚡
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Racha
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "🔥", fontSize = 16.sp)
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "$streak",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FredokaFont,
                    color = if (streak > 0) Color(0xFFFF9600) else Color(0xFFAFAFAF)
                )
            }

            // Monedas de oro 🪙 (reemplaza los diamantes)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "🪙", fontSize = 16.sp)
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "$coins",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FredokaFont,
                    color = Color(0xFFE65100)
                )
            }

            // Energía / Vidas
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "⚡", fontSize = 16.sp)
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                    text = "$hearts",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FredokaFont,
                    color = Color(0xFFFF4B4B)
                )
            }
        }
    }
}

/**
 * 2. BANNER DE UNIDAD AZUL DUOLINGO (Unit Header)
 */
@Composable
private fun DuolingoUnitBanner(
    operationType: OperationType,
    currentLevel: Int,
    primaryColor: Color,
    isEnglish: Boolean,
    onGuidebookClick: () -> Unit
) {
    val unitNumber = ((currentLevel - 1) / 5) + 1

    val unitTitle = when (operationType) {
        OperationType.ADDITION -> when (unitNumber) {
            1 -> if (isEnglish) "Morning: Visual addition basics" else "Básicos: Sumas con objetos y conteo"
            2 -> if (isEnglish) "Two-Digit: Tens and carrying" else "Decenas: Agrupación y sumas con llevadas"
            else -> if (isEnglish) "Mastery: Three-digit numbers" else "Maestría: Retos de tres cifras"
        }
        OperationType.SUBTRACTION -> when (unitNumber) {
            1 -> if (isEnglish) "Subtract: Count and take away" else "Restas: Quitar y contar hacia atrás"
            2 -> if (isEnglish) "Differences: Two-digit values" else "Diferencias: Decenas y retos rápidos"
            else -> if (isEnglish) "Advanced: Regrouping math" else "Avanzado: Restas con transformación"
        }
        OperationType.MULTIPLICATION -> when (unitNumber) {
            1 -> if (isEnglish) "Tables: 2, 3 and 5 groups" else "Tablas: Grupos del 2, 3 y 5"
            2 -> if (isEnglish) "Tables: 4, 6 and 7 speed" else "Tablas: Agilidad con 4, 6 y 7"
            else -> if (isEnglish) "Complete Times Tables Master" else "Maestría: Tablas del 8 al 12"
        }
        OperationType.DIVISION -> when (unitNumber) {
            1 -> if (isEnglish) "Sharing: Equal groups" else "Reparto: Partes iguales con objetos"
            2 -> if (isEnglish) "Simple Divisions and facts" else "Divisiones: Operaciones exactas"
            else -> if (isEnglish) "Grand Division Challenge" else "Gran Desafío: Cociente y residuo"
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 4.dp)
            .shadow(4.dp, RoundedCornerShape(16.dp), spotColor = primaryColor.copy(alpha = 0.25f))
            .clip(RoundedCornerShape(16.dp))
            .background(primaryColor)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (isEnglish) "SECTION 1, UNIT $unitNumber" else "SECCIÓN 1, UNIDAD $unitNumber",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FredokaFont,
                    color = Color.White.copy(alpha = 0.85f),
                    letterSpacing = 0.6.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = unitTitle,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FredokaFont,
                    color = Color.White,
                    lineHeight = 19.sp
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Cuaderno de notas / Guía interactiva
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White.copy(alpha = 0.2f))
                    .border(1.5.dp, Color.White.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                    .clickable { onGuidebookClick() },
                contentAlignment = Alignment.Center
            ) {
                Text(text = "📋", fontSize = 20.sp)
            }
        }
    }
}

/**
 * 3. NODO DE NIVEL CIRCULAR 3D DUOLINGO
 */
@Composable
private fun DuolingoPathNode(
    levelNumber: Int,
    isUnlocked: Boolean,
    isCompleted: Boolean,
    isCurrent: Boolean,
    stars: Int,
    isLastLevel: Boolean = false,
    baseColor: Color,
    darkBevelColor: Color,
    isEnglish: Boolean,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val infiniteTransition = rememberInfiniteTransition(label = "duolingo_active")
    val bobOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 650, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bob_offset"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(88.dp)
    ) {
        // Bocadillo flotante "EMPEZAR" o "GRAN FINAL" con flechita apuntando al botón
        if (isCurrent) {
            val bubbleColor = if (isLastLevel) Color(0xFFFF9800) else baseColor
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .offset(y = (-bobOffset).dp)
                    .padding(bottom = 2.dp)
            ) {
                Box(
                    modifier = Modifier
                        .shadow(4.dp, RoundedCornerShape(12.dp), spotColor = bubbleColor.copy(alpha = 0.35f))
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White)
                        .border(2.dp, bubbleColor, RoundedCornerShape(12.dp))
                        .padding(horizontal = 9.dp, vertical = 3.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isLastLevel) (if (isEnglish) "🏆 FINAL!" else "🏆 ¡GRAN FINAL!") else (if (isEnglish) "START" else "EMPEZAR"),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FredokaFont,
                        color = bubbleColor
                    )
                }
                // Flecha triangular hacia abajo
                Box(
                    modifier = Modifier
                        .offset(y = (-2).dp)
                        .size(10.dp, 6.dp)
                        .clip(GenericShape { size, _ ->
                            moveTo(0f, 0f)
                            lineTo(size.width, 0f)
                            lineTo(size.width / 2f, size.height)
                            close()
                        })
                        .background(bubbleColor)
                )
            }
        } else if (isLastLevel) {
            // Corona / Trofeo flotante superior sobre el último nivel cuando no es el actual
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                modifier = Modifier.padding(bottom = 2.dp)
            ) {
                Text(text = "✨", fontSize = 11.sp)
                Text(
                    text = if (isEnglish) "TROPHY" else "TROFEO",
                    fontSize = 9.5.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FredokaFont,
                    color = Color(0xFFFF9800)
                )
                Text(text = "✨", fontSize = 11.sp)
            }
        }

        // BOTÓN 3D CON DOBLE CAPA (Bisel inferior + Cara superior)
        Box(
            modifier = Modifier
                .size(76.dp, 84.dp)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null
                ) { onClick() },
            contentAlignment = Alignment.TopCenter
        ) {
            // El último nivel tiene tono dorado de trofeo de campeonato
            val nodeBase = if (isLastLevel && isUnlocked) Color(0xFFFFB800) else if (isUnlocked) baseColor else Color(0xFFE5E5E5)
            val nodeBevel = if (isLastLevel && isUnlocked) Color(0xFFD97706) else if (isUnlocked) darkBevelColor else Color(0xFFCECECE)

            // Arcos concéntricos de progreso activo
            if (isCurrent) {
                val ringColor = if (isLastLevel) Color(0xFFFF9800) else baseColor
                Canvas(
                    modifier = Modifier
                        .size(92.dp)
                        .offset(y = (-2).dp)
                ) {
                    // Arco superior
                    drawArc(
                        color = ringColor,
                        startAngle = 35f,
                        sweepAngle = 110f,
                        useCenter = false,
                        style = Stroke(width = 5.5.dp.toPx(), cap = StrokeCap.Round)
                    )
                    // Arco inferior
                    drawArc(
                        color = ringColor,
                        startAngle = 175f,
                        sweepAngle = 110f,
                        useCenter = false,
                        style = Stroke(width = 5.5.dp.toPx(), cap = StrokeCap.Round)
                    )
                }
            }

            // Capa Inferior: Bisel de profundidad 3D
            Box(
                modifier = Modifier
                    .offset(y = 9.dp)
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(nodeBevel)
            )

            // Capa Superior: Cara del botón
            Box(
                modifier = Modifier
                    .offset(y = if (isPressed) 7.dp else 0.dp)
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(nodeBase),
                contentAlignment = Alignment.Center
            ) {
                // Brillo superior curvo
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .height(13.dp)
                        .align(Alignment.TopCenter)
                        .offset(y = 5.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.28f))
                )

                // Contenido central: El último nivel presenta el Trofeo 🏆
                if (isLastLevel) {
                    if (isUnlocked) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(text = "🏆", fontSize = 23.sp)
                            Text(
                                text = "$levelNumber",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FredokaFont,
                                color = Color.White
                            )
                        }
                    } else {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Bloqueado",
                                tint = Color(0xFFAFAFAF),
                                modifier = Modifier.size(13.dp)
                            )
                            Text(text = "🏆", fontSize = 20.sp)
                            Text(
                                text = "$levelNumber",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FredokaFont,
                                color = Color(0xFFAFAFAF)
                            )
                        }
                    }
                } else {
                    // Casillas estándar con número visible
                    if (isUnlocked) {
                        Text(
                            text = "$levelNumber",
                            fontSize = if (levelNumber >= 100) 22.sp else if (levelNumber >= 10) 26.sp else 30.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FredokaFont,
                            color = Color.White
                        )
                    } else {
                        // Nivel bloqueado: número en tono gris con un pequeño candado
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Bloqueado",
                                tint = Color(0xFFAFAFAF),
                                modifier = Modifier.size(15.dp)
                            )
                            Text(
                                text = "$levelNumber",
                                fontSize = if (levelNumber >= 10) 18.sp else 21.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FredokaFont,
                                color = Color(0xFFAFAFAF)
                            )
                        }
                    }
                }
            }
        }

        // Nivel completado (Marca de éxito limpia sin estrellas)
        if (isCompleted) {
            Spacer(modifier = Modifier.height(2.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "✓",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF2E7D32)
                )
            }
            if (isLastLevel) {
                Text(
                    text = if (isEnglish) "CHAMPION!" else "¡CAMPEÓN!",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FredokaFont,
                    color = Color(0xFFFFB800)
                )
            }
        }
    }
}

/**
 * 4. COFRE DE MADERA DUOLINGO (Duolingo Wooden Chest)
 * Diseño detallado con vetas de madera, refuerzos claros y cerradura redonda dorada.
 */
@Composable
private fun DuolingoWoodenChest(
    isUnlocked: Boolean,
    isClaimed: Boolean = false,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(80.dp, 75.dp)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Canvas(modifier = Modifier.size(62.dp, 52.dp)) {
                val w = size.width
                val h = size.height

                // Sombra inferior del cofre
                drawRoundRect(
                    color = Color(0x33000000),
                    topLeft = Offset(4.dp.toPx(), h - 4.dp.toPx()),
                    size = Size(w - 8.dp.toPx(), 6.dp.toPx()),
                    cornerRadius = CornerRadius(3.dp.toPx(), 3.dp.toPx())
                )

                if (isClaimed) {
                    // ============================================
                    // COFRE ABIERTO (Tapa alzada hacia atrás y caja abierta)
                    // ============================================
                    // 1. Tapa abierta inclinada hacia arriba/atrás
                    drawRoundRect(
                        color = Color(0xFFC98E56),
                        topLeft = Offset(4.dp.toPx(), 0f),
                        size = Size(w - 8.dp.toPx(), 12.dp.toPx()),
                        cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx())
                    )
                    // Refuerzos de la tapa abierta
                    drawRoundRect(
                        color = Color(0xFFFFF7ED),
                        topLeft = Offset(8.dp.toPx(), 0f),
                        size = Size(7.dp.toPx(), 12.dp.toPx()),
                        cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
                    )
                    drawRoundRect(
                        color = Color(0xFFFFF7ED),
                        topLeft = Offset(w - 15.dp.toPx(), 0f),
                        size = Size(7.dp.toPx(), 12.dp.toPx()),
                        cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
                    )

                    // 2. Interior oscuro del cofre abierto
                    drawRoundRect(
                        color = Color(0xFF5D381E),
                        topLeft = Offset(2.dp.toPx(), 10.dp.toPx()),
                        size = Size(w - 4.dp.toPx(), 16.dp.toPx()),
                        cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                    )
                    // Destellos en el fondo del cofre
                    drawCircle(
                        color = Color(0xFFFFD700),
                        radius = 2.5.dp.toPx(),
                        center = Offset(w * 0.35f, 17.dp.toPx())
                    )
                    drawCircle(
                        color = Color(0xFFFFA000),
                        radius = 3.dp.toPx(),
                        center = Offset(w * 0.52f, 18.dp.toPx())
                    )
                    drawCircle(
                        color = Color(0xFFFFD700),
                        radius = 2.2.dp.toPx(),
                        center = Offset(w * 0.68f, 17.dp.toPx())
                    )

                    // 3. Bisel y cuerpo inferior de la caja de madera
                    drawRoundRect(
                        color = Color(0xFFB07644),
                        topLeft = Offset(0f, 22.dp.toPx()),
                        size = Size(w, h - 22.dp.toPx()),
                        cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx())
                    )
                    drawRoundRect(
                        color = Color(0xFFE8B887),
                        topLeft = Offset(0f, 18.dp.toPx()),
                        size = Size(w, h - 24.dp.toPx()),
                        cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx())
                    )

                    // Refuerzos metálicos del cuerpo
                    val bandWidth = 8.dp.toPx()
                    drawRoundRect(
                        color = Color(0xFFFFF7ED),
                        topLeft = Offset(5.dp.toPx(), 18.dp.toPx()),
                        size = Size(bandWidth, h - 24.dp.toPx()),
                        cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
                    )
                    drawRoundRect(
                        color = Color(0xFFFFF7ED),
                        topLeft = Offset(w - 5.dp.toPx() - bandWidth, 18.dp.toPx()),
                        size = Size(bandWidth, h - 24.dp.toPx()),
                        cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
                    )
                    // Cerradura abierta descolgada
                    val lockCenter = Offset(w / 2f, 24.dp.toPx())
                    drawCircle(
                        color = Color(0xFFFFD700),
                        radius = 6.dp.toPx(),
                        center = lockCenter
                    )
                    drawCircle(
                        color = Color(0xFF7A4E11),
                        radius = 2.dp.toPx(),
                        center = lockCenter
                    )
                } else {
                    // ============================================
                    // COFRE CERRADO (Bloqueado o listo para abrir)
                    // ============================================
                    // Bisel 3D inferior del cofre
                    drawRoundRect(
                        color = Color(0xFFB07644),
                        topLeft = Offset(0f, 6.dp.toPx()),
                        size = Size(w, h - 6.dp.toPx()),
                        cornerRadius = CornerRadius(10.dp.toPx(), 10.dp.toPx())
                    )

                    // Cuerpo de madera principal (tono miel cálido)
                    drawRoundRect(
                        color = Color(0xFFE8B887),
                        topLeft = Offset(0f, 0f),
                        size = Size(w, h - 6.dp.toPx()),
                        cornerRadius = CornerRadius(10.dp.toPx(), 10.dp.toPx())
                    )

                    // Tapa superior del cofre con línea divisoria
                    drawRoundRect(
                        color = Color(0xFFF3C79A),
                        topLeft = Offset(0f, 0f),
                        size = Size(w, (h - 6.dp.toPx()) * 0.45f),
                        cornerRadius = CornerRadius(10.dp.toPx(), 10.dp.toPx())
                    )
                    // Línea oscura de separación de la tapa
                    drawLine(
                        color = Color(0xFF9E6533),
                        start = Offset(0f, (h - 6.dp.toPx()) * 0.45f),
                        end = Offset(w, (h - 6.dp.toPx()) * 0.45f),
                        strokeWidth = 2.dp.toPx()
                    )

                    // Refuerzos metálicos claros en los bordes (crema)
                    val bandWidth = 8.dp.toPx()
                    drawRoundRect(
                        color = Color(0xFFFFF7ED),
                        topLeft = Offset(5.dp.toPx(), 0f),
                        size = Size(bandWidth, h - 6.dp.toPx()),
                        cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
                    )
                    drawRoundRect(
                        color = Color(0xFFFFF7ED),
                        topLeft = Offset(w - 5.dp.toPx() - bandWidth, 0f),
                        size = Size(bandWidth, h - 6.dp.toPx()),
                        cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
                    )

                    // Cerradura central dorada de Duolingo
                    val lockCenter = Offset(w / 2f, (h - 6.dp.toPx()) * 0.45f)
                    drawCircle(
                        color = Color(0xFFCC8F2A),
                        radius = 9.dp.toPx(),
                        center = lockCenter
                    )
                    drawCircle(
                        color = if (isUnlocked) Color(0xFFFFD700) else Color(0xFFFFFDF5),
                        radius = 7.dp.toPx(),
                        center = lockCenter
                    )
                    drawCircle(
                        color = Color(0xFF7A4E11),
                        radius = 2.5.dp.toPx(),
                        center = lockCenter
                    )
                }
            }

            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = when {
                    isClaimed -> "ABIERTO"
                    isUnlocked -> "🎁 ABRIR"
                    else -> "COFRE"
                },
                fontSize = 9.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FredokaFont,
                color = when {
                    isClaimed -> Color(0xFF4CAF50)
                    isUnlocked -> Color(0xFFE65100)
                    else -> Color(0xFFAFAFAF)
                }
            )
        }
    }
}

/**
 * 5. ESCENA DE FOGATA Y MALVAVISCOS DUOLINGO (Campfire Diorama)
 * Réplica de la escena de Duolingo: suelo de piedra, fogata brillante,
 * tronco y malvavisco asado en vara, con estrellas ⭐☆☆ abajo.
 */
@Composable
private fun DuolingoCampfireScene() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Composición gráfica Canvas de la fogata y campamento
        Box(
            modifier = Modifier.size(110.dp, 85.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height

                // Base de piedra ovalada suave
                drawOval(
                    color = Color(0xFFEFEFEF),
                    topLeft = Offset(5.dp.toPx(), h - 22.dp.toPx()),
                    size = Size(w - 10.dp.toPx(), 20.dp.toPx())
                )

                // Tronco / Asiento de madera a la izquierda
                val stumpX = 18.dp.toPx()
                val stumpY = h - 35.dp.toPx()
                drawRoundRect(
                    color = Color(0xFF7B4A28),
                    topLeft = Offset(stumpX, stumpY),
                    size = Size(26.dp.toPx(), 22.dp.toPx()),
                    cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                )
                drawOval(
                    color = Color(0xFFB37446),
                    topLeft = Offset(stumpX, stumpY - 4.dp.toPx()),
                    size = Size(26.dp.toPx(), 9.dp.toPx())
                )

                // Leños cruzados de la fogata a la derecha
                val fireCenterX = w - 35.dp.toPx()
                val fireBaseY = h - 16.dp.toPx()

                // Leño 1
                drawLine(
                    color = Color(0xFF5D381E),
                    start = Offset(fireCenterX - 16.dp.toPx(), fireBaseY + 2.dp.toPx()),
                    end = Offset(fireCenterX + 16.dp.toPx(), fireBaseY - 6.dp.toPx()),
                    strokeWidth = 6.dp.toPx(),
                    cap = StrokeCap.Round
                )
                // Leño 2
                drawLine(
                    color = Color(0xFF754522),
                    start = Offset(fireCenterX - 14.dp.toPx(), fireBaseY - 6.dp.toPx()),
                    end = Offset(fireCenterX + 14.dp.toPx(), fireBaseY + 2.dp.toPx()),
                    strokeWidth = 6.dp.toPx(),
                    cap = StrokeCap.Round
                )

                // Llamas de la fogata (capa exterior naranja)
                val firePathOuter = Path().apply {
                    moveTo(fireCenterX - 14.dp.toPx(), fireBaseY - 2.dp.toPx())
                    quadraticTo(
                        fireCenterX - 16.dp.toPx(), fireBaseY - 25.dp.toPx(),
                        fireCenterX - 4.dp.toPx(), fireBaseY - 40.dp.toPx()
                    )
                    quadraticTo(
                        fireCenterX, fireBaseY - 25.dp.toPx(),
                        fireCenterX + 4.dp.toPx(), fireBaseY - 35.dp.toPx()
                    )
                    quadraticTo(
                        fireCenterX + 16.dp.toPx(), fireBaseY - 22.dp.toPx(),
                        fireCenterX + 14.dp.toPx(), fireBaseY - 2.dp.toPx()
                    )
                    close()
                }
                drawPath(
                    path = firePathOuter,
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFFFF9600), Color(0xFFFF5722)),
                        startY = fireBaseY - 40.dp.toPx(),
                        endY = fireBaseY
                    )
                )

                // Llamas de la fogata (capa interior amarilla brillante)
                val firePathInner = Path().apply {
                    moveTo(fireCenterX - 8.dp.toPx(), fireBaseY - 2.dp.toPx())
                    quadraticTo(
                        fireCenterX - 8.dp.toPx(), fireBaseY - 18.dp.toPx(),
                        fireCenterX, fireBaseY - 28.dp.toPx()
                    )
                    quadraticTo(
                        fireCenterX + 8.dp.toPx(), fireBaseY - 18.dp.toPx(),
                        fireCenterX + 8.dp.toPx(), fireBaseY - 2.dp.toPx()
                    )
                    close()
                }
                drawPath(
                    path = firePathInner,
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFFFFEB3B), Color(0xFFFF9800)),
                        startY = fireBaseY - 28.dp.toPx(),
                        endY = fireBaseY
                    )
                )

                // Vara con malvavisco tostado
                drawLine(
                    color = Color(0xFF6B4423),
                    start = Offset(stumpX + 16.dp.toPx(), stumpY - 12.dp.toPx()),
                    end = Offset(fireCenterX - 4.dp.toPx(), fireBaseY - 28.dp.toPx()),
                    strokeWidth = 2.5.dp.toPx(),
                    cap = StrokeCap.Round
                )
                // Malvavisco en la punta
                drawRoundRect(
                    color = Color(0xFF3E2723), // Tostado oscuro
                    topLeft = Offset(fireCenterX - 7.dp.toPx(), fireBaseY - 32.dp.toPx()),
                    size = Size(8.dp.toPx(), 7.dp.toPx()),
                    cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
                )
            }

            // Personaje camper sentado en el tronco
            Box(
                modifier = Modifier
                    .offset(x = (-22).dp, y = (-26).dp)
                    .size(38.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "👧🏾", fontSize = 26.sp)
            }
        }

        Spacer(modifier = Modifier.height(2.dp))

        // Distintivo de campamento
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(top = 2.dp)
        ) {
            Text(text = "🏕️", fontSize = 14.sp)
        }
    }
}



/**
 * 7. DIÁLOGO DE LA GUÍA DE LA UNIDAD (Guidebook)
 */
@Composable
private fun DuolingoGuidebookDialog(
    operationType: OperationType,
    isEnglish: Boolean,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "📋", fontSize = 24.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isEnglish) "Unit Guidebook" else "Guía de la Unidad",
                    fontFamily = FredokaFont,
                    fontWeight = FontWeight.Black,
                    fontSize = 20.sp,
                    color = SlateDark
                )
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = if (isEnglish) {
                        "Key tips for ${operationType.getDisplayName(true)}:"
                    } else {
                        "Consejos clave para ${operationType.getDisplayName(false)}:"
                    },
                    fontSize = 14.sp,
                    color = Color(0xFF4B4B4B)
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF7F7F7))
                        .border(1.dp, Color(0xFFE5E5E5), RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        Text(
                            text = if (isEnglish) "💡 Rule of Thumb:" else "💡 Regla de Oro:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = SlateDark
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = when (operationType) {
                                OperationType.ADDITION -> if (isEnglish) "Adding puts groups together. The total always grows!" else "Sumar es juntar o agregar grupos. ¡El total siempre crece!"
                                OperationType.SUBTRACTION -> if (isEnglish) "Subtracting takes away from the whole to find the remainder." else "Restar es quitar una cantidad de un grupo para encontrar la diferencia."
                                OperationType.MULTIPLICATION -> if (isEnglish) "Multiplication is fast repeated addition of equal groups." else "Multiplicar es sumar el mismo número varias veces de forma rápida."
                                OperationType.DIVISION -> if (isEnglish) "Division shares items into equal piles." else "Dividir es repartir cosas en partes exactamente iguales."
                            },
                            fontSize = 13.sp,
                            color = Color(0xFF555555)
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = if (isEnglish) "LET'S GO!" else "¡ENTENDIDO!",
                    fontWeight = FontWeight.Black,
                    fontFamily = FredokaFont,
                    color = Color(0xFF1CB0F6)
                )
            }
        }
    )
}

/**
 * 8. DIÁLOGO DEL COFRE
 */
@Composable
private fun DuolingoChestDialog(
    rewardAmount: Int,
    isEnglish: Boolean,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = when {
                        rewardAmount > 0 -> "🎉"
                        rewardAmount == -1 -> "✨"
                        else -> "🔒"
                    },
                    fontSize = 24.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = when {
                        rewardAmount > 0 -> if (isEnglish) "Treasure Claimed!" else "¡Tesoro Reclamado!"
                        rewardAmount == -1 -> if (isEnglish) "Chest Already Open" else "Cofre Ya Abierto"
                        else -> if (isEnglish) "Locked Chest" else "Cofre Bloqueado"
                    },
                    fontFamily = FredokaFont,
                    fontWeight = FontWeight.Black,
                    fontSize = 19.sp,
                    color = SlateDark
                )
            }
        },
        text = {
            Text(
                text = when {
                    rewardAmount > 0 -> if (isEnglish) "You received +$rewardAmount gold coins! Spend them in the toy shop!" else "¡Ganaste +$rewardAmount monedas de oro! ¡Úsalas en la tienda de juguetes!"
                    rewardAmount == -1 -> if (isEnglish) "This chest is already open! You have collected its coins." else "¡Este cofre ya fue abierto! Ya recogiste tus 15 monedas de oro."
                    else -> if (isEnglish) "Complete the previous levels to open this coin chest!" else "¡Completa los niveles anteriores para abrir este cofre de monedas!"
                },
                fontSize = 14.sp,
                color = Color(0xFF4B4B4B)
            )
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "OK",
                    fontWeight = FontWeight.Black,
                    fontFamily = FredokaFont,
                    color = Color(0xFF1CB0F6)
                )
            }
        }
    )
}
